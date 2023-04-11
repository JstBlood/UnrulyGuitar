package client.utils;

import javafx.scene.control.Alert;

public class UIUtils {
    public static void showError(String message) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setHeaderText(message);
        err.setContentText("");
        err.showAndWait();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
