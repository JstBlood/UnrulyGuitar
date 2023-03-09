package client.utils;

import javafx.scene.control.Alert;
import org.jvnet.hk2.annotations.Service;

@Service
public class UIUtils {
    public static void showError(String errt) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setHeaderText("Connection failed");
        err.setContentText(errt);
        err.showAndWait();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
