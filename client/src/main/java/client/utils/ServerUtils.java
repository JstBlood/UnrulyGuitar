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
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import commons.Board;
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
import org.springframework.data.util.Pair;

public class ServerUtils {

    private final static int MLIMIT = 1024 * 1024;
    private static String url = null;
    private static String adminPass = null;
    private static String username = null;
    private StompSession session;

    private String getServer() {
        return "http://" + url + "/";
    }

    private String getSocket() {
        return "ws://" + url + "/websocket";
    }

    private <T> T internalPostRequest(String path, Entity send, GenericType<T> retType) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(getServer()).path(path) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(send, retType);
    }

    private <T> T internalGetRequest(String path, GenericType<T> retType) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(getServer()).path(path) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(retType);
    }

    public List<Board> getBoards() {
        if(!isAdmin())
            throw new ForbiddenException();

        return internalPostRequest("api/boards/list",
                Entity.entity(adminPass, APPLICATION_JSON),
                new GenericType<>() {});
    }

    public Set<Board> getPrevious() {
        return internalPostRequest("api/boards/previous",
                Entity.entity(username, APPLICATION_JSON),
                new GenericType<>() {});
    }

    public Board getBoard(String key) {
        return internalPostRequest("api/boards/" + key + "/join",
                Entity.entity(username, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public Board addBoard(Board board) {
        return internalPostRequest("api/boards/create",
                Entity.entity(Pair.of(username, board), APPLICATION_JSON),
                new GenericType<>(){});
    }

    public CardList addCardList(CardList cardList) {
        return internalPostRequest("api/cardlists/add",
                Entity.entity(cardList, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void forceRefresh(String key) {
        internalGetRequest("api/boards/" + key + "/forceRefresh",
                new GenericType<String>(){});
    }

    public void connect() {
        if(url == null)
            throw new RuntimeException("No address provided");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(MLIMIT);
        container.setDefaultMaxTextMessageBufferSize(MLIMIT);

        var client = new StandardWebSocketClient(container);
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        stomp.setInboundMessageSizeLimit(MLIMIT);

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
    public void setUrl(String url) {
        ServerUtils.url = url;
    }

    public void setAdminPass(String pass) {
        ServerUtils.adminPass = pass;
    }
    
    public void removeAdmin() {
        ServerUtils.adminPass = null;
    }

    public void setUsername(String username) {
        ServerUtils.username = username;
    }

    public boolean isAdmin() {
        return ServerUtils.adminPass != null;
    }
}
