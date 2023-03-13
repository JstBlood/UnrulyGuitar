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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import commons.Board;
import commons.Quote;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import javafx.util.Pair;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private final static int MLIMIT = 1024 * 1024;
    private static String url = null;
    private static String adminPass = null;
    private static String username = null;
    private StompSession session;

    public List<Board> getBoards() {
        if(!isAdmin())
            throw new ForbiddenException();

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/list") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(adminPass, APPLICATION_JSON), new GenericType<List<Board>>() {});
    }

    public Set<Board> getPrevious() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/previous") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(username, APPLICATION_JSON), new GenericType<Set<Board>>() {});
    }

    public Board joinBoard(String key) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target("http://" + url).path("api/boards/join") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(new Pair<>(username, key), APPLICATION_JSON), Board.class);
    }

    public Board createBoard() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target("http://" + url).path("api/boards/create") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(username, APPLICATION_JSON), Board.class);
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
            session = stomp.connect("ws://" + url + "/websocket", new StompSessionHandlerAdapter() {}).get();
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
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        ServerUtils.url = url;
    }

    public void setAdminPass(String pass) {
        ServerUtils.adminPass = pass;
    }

    public void setUsername(String username) {
        ServerUtils.username = username;
    }

    public boolean isAdmin() {
        return ServerUtils.adminPass != null;
    }
}