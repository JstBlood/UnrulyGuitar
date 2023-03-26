/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import client.scenes.MainCtrl;
import commons.Board;
import commons.Card;
import commons.CardList;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    private final int mlimit = 1024 * 1024;
    private final MainCtrl store;
    private StompSession session;

    @Inject
    public ServerUtils(MainCtrl store) {
        this.store = store;
    }

    private String getServer() {
        return "http://" + store.accessStore().getUrl() + "/";
    }

    private String getSocket() {
        return "ws://" + store.accessStore().getUrl() + "/websocket";
    }

    private <T> T internalPostRequest(String path, Entity send, GenericType<T> retType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getServer()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(send, retType);
    }

    private <T> T internalGetRequest(String path, GenericType<T> retType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getServer()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(retType);
    }
    private void internalDeleteRequest(String path) {
        ClientBuilder.newClient(new ClientConfig())
                .target(getServer()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    private void internalPutRequest(String path, Entity send) {
        ClientBuilder.newClient(new ClientConfig())
                .target(getServer()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(send);
    }

    public List<Board> getBoards() {
        if(!store.accessStore().isAdmin())
            throw new ForbiddenException();

        return internalPostRequest("api/boards/restricted/" + store.accessStore().getPassword() + "/list",
                Entity.entity(null, APPLICATION_JSON),
                new GenericType<>() {});
    }

    public Set<Board> getPrevious() {
        return internalPostRequest("api/boards/secure/" + store.accessStore().getUsername() + "/previous",
                Entity.entity(null, APPLICATION_JSON),
                new GenericType<>() {});
    }

    public Board addBoard(Board board) {
        return internalPostRequest("api/boards/secure/" + store.accessStore().getUsername() + "/create",
                Entity.entity(board, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public Board getBoard(String key) {
        return internalPostRequest("api/boards/secure/" + store.accessStore().getUsername() + "/" + key + "/join",
                Entity.entity(null, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void addCardList(CardList cardList) {
        internalPostRequest("api/cardlists/add",
                Entity.entity(cardList, APPLICATION_JSON),
                new GenericType<>() {});
    }

    public void editCardList(long id, String component, String newValue) {
        internalPutRequest("api/cardlists/" + id + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON));
    }

    public void deleteCardList(long id) {
        internalDeleteRequest("api/cardlists/" + id);
    }

    public void editBoardTitle(String key, String newTitle) {
        internalPostRequest("api/boards/restricted/" + store.accessStore().getUsername()
                        + "/" + key + "/edit/title",
                Entity.entity(newTitle, APPLICATION_JSON),
                new GenericType<>(){});
    }

    // CARD RELATED FUNCTIONS

    public void addCard(Card card){
        internalPostRequest("api/cards/add",
                Entity.entity(card, APPLICATION_JSON),
                new GenericType<String>(){});
    }

    public void removeCard(long id){
        internalDeleteRequest("api/cards/" + id);
    }

    public void editCardTitle(long id, String newTitle) {
        internalPutRequest("api/cards/" + id + "/title",
                Entity.entity(newTitle, APPLICATION_JSON));
    }

    // END OF CARD RELATED FUNCTIONS

    public void forceRefresh(String key) {
        internalGetRequest("api/boards/" + key + "/forceRefresh",
                new GenericType<String>(){});
    }




    public void connect() {
        if(store.accessStore().getUrl() == null)
            throw new RuntimeException("No address provided");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(mlimit);
        container.setDefaultMaxTextMessageBufferSize(mlimit);

        var client = new StandardWebSocketClient(container);
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        stomp.setInboundMessageSizeLimit(mlimit);

        try {
            session = stomp.connect(getSocket(), new StompSessionHandlerAdapter() {}).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }
            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }
    public void send(String dest, Object o) {
        session.send(dest, o);
    }
}
