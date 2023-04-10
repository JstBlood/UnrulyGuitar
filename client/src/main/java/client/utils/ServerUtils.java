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

import client.scenes.MainCtrl;
import commons.*;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

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
        try {
            return ClientBuilder.newClient(new ClientConfig())
                    .target(getServer()).path(path)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(send, retType);
        } catch (ForbiddenException e) {
            UIUtils.showError("You cannot edit this board since its password protected and you have not" +
                    " entered a correct password.");
            return null;
        }
    }

    private <T> T uncheckedInternalGetRequest(String path, GenericType<T> retType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getServer()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(retType);
    }

    private <T> T internalGetRequest(String path, GenericType<T> retType) {
        try {
            return uncheckedInternalGetRequest(path, retType);
        } catch (ForbiddenException e) {
            UIUtils.showError("You cannot edit this board since its password protected and you have not" +
                    " entered a correct password.");
            return null;
        }
    }

    private void internalDeleteRequest(String path) {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(getServer()).path(path)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .delete();
        } catch (ForbiddenException e) {
            UIUtils.showError("You cannot edit this board since its password protected and you have not" +
                    " entered a correct password.");
        }
    }

    private <T> T internalPutRequest(String path, Entity send, GenericType<T> retType) {
        try {
            return ClientBuilder.newClient(new ClientConfig())
                    .target(getServer()).path(path)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .put(send, retType);
        } catch (ForbiddenException e) {
            UIUtils.showError("You cannot edit this board since its password protected and you have not" +
                    " entered a correct password.");
            return null;
        }
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

    public void validate(String key, String toTry) {
        uncheckedInternalGetRequest("secure/" + store.accessStore().getUsername() + "/" +
                        toTry + "/boards/" + key + "/validate",
                new GenericType<>(){});
    }

    public void changePass(String key, String newPass) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key + "/password",
                Entity.entity(newPass, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void removePass(String key) {
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key + "/password");
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

    public void updateBoard(String key, String component, Object newValue) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void updateBoardDefaultPreset(String key, Long newId) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key + "/defaultPreset",
                Entity.entity(newId, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void addBoardPreset(String key, ColorPreset newPreset) {
        internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/boards/" + key + "/addColorPreset",
                Entity.entity(newPreset, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteBoardPreset(String key, Long id) {
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/boards/" + key + "/removeColorPreset/" + id);
    }


    // END OF BOARD RELATED FUNCTIONS

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

    public void updateCardList(long id, String component, Object newValue) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/lists/" + id + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>() {
                });
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void registerForUpdates(Consumer<CardList> consumer) {
        executorService.submit(() -> {
            while(!Thread.interrupted()) {
                try {
                    var res = ClientBuilder.newClient(new ClientConfig())
                            .target(getServer()).path("secure/" + store.accessStore().getUsername() +
                                    "/lists/updates")
                            .request(APPLICATION_JSON)
                            .accept(APPLICATION_JSON)
                            .get(Response.class);
                    if (res.getStatus() == 204) {
                        continue;
                    }
                    var cl = res.readEntity(CardList.class);
                    consumer.accept(cl);
                } catch (Exception ignored) {

                }
            }
        });
    }

    public void stop() {
        executorService.shutdownNow();
    }

    // END OF CARD LIST RELATED METHODS

    // CARD RELATED FUNCTIONS

    public Card addCard(Card card){
        return internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/cards/add",
                Entity.entity(card, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteCard(long id){
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/cards/" + id);
    }

    public void updateCard(long id, String component, Object newValue) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/cards/" + id + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void updateCardPreset(long id, Long newId) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/cards/" + id + "/preset",
                Entity.entity(newId, APPLICATION_JSON),
                new GenericType<>(){});
    }

    // END OF CARD RELATED FUNCTIONS

    // START OF TASK RELATED FUNCTIONS

    public void addTask(Task task) {
        internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/tasks/add",
                Entity.entity(task, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void updateTask(long id, String component, Object newValue) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/tasks/" + id + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteTask(long id){
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/tasks/" + id);
    }

    // END OF TASK RELATED FUNCTIONS

    // START OF TAG RELATED FUNCTIONS

    public void addTag(Tag tag) {
        internalPostRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/tags/add",
                Entity.entity(tag, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void updateTag(long id, String component, Object newValue) {
        internalPutRequest("secure/" + store.accessStore().getUsername() + "/" +
                        store.accessStore().getPassword() + "/tags/" + id + "/" + component,
                Entity.entity(newValue, APPLICATION_JSON),
                new GenericType<>(){});
    }

    public void deleteTag(long id){
        internalDeleteRequest("secure/" + store.accessStore().getUsername() + "/" +
                store.accessStore().getPassword() + "/tags/" + id);
    }

    // END OF TAG RELATED FUNCTIONS

    // START OF FILE RELATED FUNCTIONS

    public void uploadFile(File file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Convert the file to a multipart file
        FileSystemResource resource = new FileSystemResource(file);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        // Create the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create the request entity with the headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send the request to the server
        String url = getServer()+ "secure/" + store.accessStore().getUsername() + "/file/add";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Check the response status code and print the response body
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println(response.getBody());
        } else {
            System.out.println("Error uploading file: " + response.getStatusCode());
        }
    }
    public File getFile(String fileName) throws IOException {
        String url = getServer()+ "secure/" + store.accessStore().getUsername() + "/file/" + fileName;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        InputStream inputStream = new ByteArrayInputStream(response.getBody());

        // Get the extension of the original file
        String extension = fileName.substring(fileName.lastIndexOf("."));
        File file = File.createTempFile("temp", extension);

        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();

        // Rename the temporary file to match the original file name
        File renamedFile = new File(file.getParentFile(), fileName);
        file.renameTo(renamedFile);

        return renamedFile;
    }
    public void deleteFile(String fileName) {
        RestTemplate restTemplate = new RestTemplate();

        // Create the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Send the request to the server
        String url = getServer() + "secure/" + store.accessStore().getUsername() + "/file/" + fileName;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

        // Check the response status code and print the response body
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("File deleted: " + response.getBody());
        } else {
            System.out.println("Error deleting file: " + response.getStatusCode());
        }
    }

    // END OF FILE RELATED FUNCTIONS
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
}
