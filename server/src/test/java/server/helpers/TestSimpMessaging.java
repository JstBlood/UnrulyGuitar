package server.helpers;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

public class TestSimpMessaging extends SimpMessagingTemplate {
    private List<Object> sent = new ArrayList<>();
    private List<String> paths = new ArrayList<>();
    public TestSimpMessaging() {
        super((message, timeout) -> true);
    }

    public List<Object> getSent() {
        return sent;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void clear() {
        sent.clear();
        paths.clear();
    }

    @Override
    public void convertAndSend(String path, Object obj) {
        sent.add(obj);
        paths.add(path);
    }
}
