package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller of the AddCardList scene,
 * where the user can create a new CardList for the current board.
 */

public class PasswordCtrl implements Initializable{
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField password;
    @FXML
    private Button unlock;
    @FXML
    private Button changePass;
    @FXML
    private Button removePass;

    @Inject
    public PasswordCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        password.textProperty().addListener((o, oldV, newV) -> {
            password.setStyle("-fx-text-fill: red;");
        });
    }

    public void stepTwo() {
        updateFields(mainCtrl.getCurrentBoard());
    }

    public void prepare() {
        server.connect();

        server.registerForMessages("/topic/board/" + mainCtrl.getCurrentBoard().key, Board.class, q -> {
            Platform.runLater(() -> {
                updateFields(q);
            });
        });

        updateFields(mainCtrl.getCurrentBoard());
    }

    private void updateFields(Board newState) {
        if(newState.isPasswordProtected) {
            if (mainCtrl.accessStore().isAdmin()) {
                password.setPromptText("New Password");
                unlock.setVisible(false);
                changePass.setDisable(false);
                removePass.setDisable(false);
            } else {
                if (mainCtrl.accessStore().getPassword() == null) {
                    password.setPromptText("Password");
                    unlock.setText("Unlock");
                    unlock.setVisible(true);
                    changePass.setDisable(true);
                    removePass.setDisable(true);
                } else {
                    password.setPromptText("Password");
                    unlock.setText("Forget Password");
                    unlock.setVisible(true);
                    changePass.setDisable(false);
                    removePass.setDisable(false);
                }
            }
        } else {
            password.setPromptText("New Password");
            unlock.setText("Set Password");
            unlock.setVisible(true);
            changePass.setDisable(true);
            removePass.setDisable(true);
        }

        if(!mainCtrl.accessStore().isAdmin()) {
            password.setText(mainCtrl.accessStore().getPassword());
            password.setStyle("");
        } else {
            password.setText("");
            password.setStyle("");
        }
    }

    @FXML
    public void unlock() {
        if(mainCtrl.accessStore().isAdmin()) {
            server.changePass(mainCtrl.getCurrentBoard().key, password.getText());
            return;
        }

        if(!mainCtrl.getCurrentBoard().isPasswordProtected) {
            mainCtrl.accessStore().setPassword(password.getText());
            server.changePass(mainCtrl.getCurrentBoard().key, password.getText());
            return;
        }

        if(mainCtrl.accessStore().getPassword() == null) {
            try {
                server.validate(mainCtrl.getCurrentBoard().key, password.getText());
                mainCtrl.accessStore().setPassword(password.getText());
            } catch (Exception e) {
                mainCtrl.accessStore().removePassword();
                UIUtils.showError("Invalid password");
            }
        } else {
            mainCtrl.accessStore().removePassword();
        }

        server.forceRefresh(mainCtrl.getCurrentBoard().key);
    }

    @FXML
    public void changePass() {
        String newPass = password.getText();
        try {
            server.changePass(mainCtrl.getCurrentBoard().key, newPass);
        } catch (Exception e) {
            mainCtrl.accessStore().removePassword();
            UIUtils.showError("Invalid password");
        }

        if(!mainCtrl.accessStore().isAdmin())
            mainCtrl.accessStore().setPassword(newPass);
    }

    @FXML
    public void removePass() {
        try {
            server.removePass(mainCtrl.getCurrentBoard().key);
        } catch (Exception e) {
            mainCtrl.accessStore().removePassword();
            UIUtils.showError("Invalid password");
        }

        if(!mainCtrl.accessStore().isAdmin())
            mainCtrl.accessStore().removePassword();
    }

    @FXML
    public void cancel() {
        mainCtrl.showBoardOverview();
    }
}
