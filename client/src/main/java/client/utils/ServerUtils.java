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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import client.scenes.MainCtrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Task;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
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
    private  MainCtrl store;
    private StompSession session;

    @Inject
    public ServerUtils(MainCtrl store) {
        this.store = store;
    }

    private  String getServer() {
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

    private <T> T internalPutRequest(String path, Entity send, GenericType<T> retType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getServer()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(send, retType);
    }

    // BOARD RELATED FUNCTIONS

    public List<Board> getBoards() {
        if(!store.accessStore().isAdmin())
            throw new ForbiddenException();

        return internalGetRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/list",
                new GenericType<>() {});
    }

    public Set<Board> getPrevious() {
        return internalGetRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/previous",
                new GenericType<>() {});
    }

    public Board addBoard(Board board) {
        return internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/add",
                Entity.entity(board, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public Board joinBoard(String key) {
        return internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/join/" + key,
                Entity.entity(null, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void leaveBoard(String key) {
        internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/leave/" + key,
                Entity.entity(null, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteBoard(String key) {
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key);
    }

    public Board updateBoard(String key, String component, Object newValue) {
        return internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    // END OF BOARD RELATED FUNCTIONS

    // START OF TASK RELATED FUNCTIONS

    public void addBoard(Task task) {
        internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/tasks/add",
                Entity.entity(task, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void updateTask(long key, String component, Object newValue) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/tasks/" + key + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteTask(long id){
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/tasks/" + id);
    }

    // END OF TASK RELATED FUNCTIONS


    // CARD LIST RELATED METHODS

    public void addCardList(CardList cardList) {
        internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/lists/add",
                Entity.entity(cardList, APPLICATION_JSON),
                new GenericType<>() {});
    }

    public void deleteCardList(long id) {
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/lists/" + id);
    }

    public CardList updateCardList(long id, String component, String newValue) {
        return internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/lists/" + id + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    // END OF CARD LIST RELATED METHODS


    // CARD RELATED FUNCTIONS

    public Card addCard(Card card){
        return internalPostRequest("secure/" + store.accessStore().getUsername() + "/cards/add",
                Entity.entity(card, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteCard(long id){
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/cards/" + id);
    }

    public Card updateCard(long id, String component, Object newValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = null;
        try {
            jsonValue = objectMapper.writeValueAsString(newValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return internalPutRequest("secure/" + store.accessStore().getUsername() +
                        "/cards/" + id + "/" + component,
                Entity.json(jsonValue),
                new GenericType<>(){});
    }

    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

    public void registerForUpdates(Consumer<Card> consumer) {
        EXEC.submit(() -> {
            while(!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig())
                        .target(getServer()).path("secure/" + store.accessStore().getUsername() +
                                "/cards/updates")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);
                if(res.getStatus() == 204) {
                    continue;
                }
                var c = res.readEntity(Card.class);
                consumer.accept(c);
            }
        });
    }

    public void stop() {
        EXEC.shutdownNow();
    }

    // END OF CARD RELATED FUNCTIONS

    public void forceRefresh(String key) {
        internalGetRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/boards/force_refresh/" + key,
                new GenericType<>(){});
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

    public <T> void deregister() {
        if(this.session != null)
            session.disconnect();
    }

    public void send(String dest, Object o) {
        session.send(dest, o);
    }
}
