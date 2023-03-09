package client.utils;

import javafx.scene.control.Alert;

public class UIUtils {
    public static void showError(String errt) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setHeaderText("Connection failed");
        err.setContentText(errt);
        err.showAndWait();
    }
}
