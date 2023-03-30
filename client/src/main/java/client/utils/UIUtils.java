package client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class UIUtils {
    public static void showError(String message) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setHeaderText(message);
        err.setContentText("");
        err.showAndWait();
    }

    public static void showMessage(String message) {
        Alert msg = new Alert(Alert.AlertType.INFORMATION);
        msg.setHeaderText(message);
        msg.show();

        Thread newThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            Platform.runLater(msg::close);
        });
        newThread.start();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
