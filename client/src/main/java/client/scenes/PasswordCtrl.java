package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.CardList;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import jdk.internal.event.SecurityPropertyModificationEvent;

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
    }

    public void prepare() {
        server.registerForMessages("/topic/board/" + board.key, Board.class, q -> {
            Platform.runLater(() -> {
                updateFields();
            });
        });
    }

    private void updateFields() {
        if(mainCtrl.getCurrentBoard().isPasswordProtected) {
            if (mainCtrl.accessStore().isAdmin()) {
                unlock.setVisible(false);
                changePass.setDisable(false);
                removePass.setDisable(false);
            } else {
                if (mainCtrl.accessStore().getPassword() == null) {
                    unlock.setText("Unlock");
                    changePass.setDisable(true);
                    removePass.setDisable(true);
                } else {
                    unlock.setText("Forget Password");
                    changePass.setDisable(false);
                    removePass.setDisable(false);
                }
            }
        } else {
            unlock.setText("Set Password");
            changePass.setDisable(true);
            removePass.setDisable(true);
        }
    }

    @FXML
    public void unlock() {
        if(!mainCtrl.getCurrentBoard().isPasswordProtected) {
            mainCtrl.accessStore().setPassword(password.getText());
            server.changePass(mainCtrl.getCurrentBoard().key, password.getText());
            return;
        }

        if(mainCtrl.accessStore().getPassword() == null) {
            mainCtrl.accessStore().setPassword(password.getText());

            try {
                server.validate(mainCtrl.getCurrentBoard().key);
            } catch (Exception e) {
                mainCtrl.accessStore().removePassword();
                UIUtils.showError("Invalid password");
            }
        } else {
            mainCtrl.accessStore().removePassword();
        }

        updateFields();
    }

    @FXML
    public void changePass() {
        mainCtrl.accessStore().setPassword(password.getText());
        try {
            server.changePass(mainCtrl.getCurrentBoard().key, password.getText());
        } catch (Exception e) {
            mainCtrl.accessStore().removePassword();
            UIUtils.showError("Invalid password");
        }

        updateFields();
    }

    @FXML
    public void removePass() {
        mainCtrl.accessStore().removePassword();
        try {
            server.changePass(mainCtrl.getCurrentBoard().key, password.getText());
        } catch (Exception e) {
            mainCtrl.accessStore().removePassword();
            UIUtils.showError("Invalid password");
        }

        updateFields();
    }

    @FXML
    public void cancel() {
        clearFields();
        mainCtrl.showBoardOverview();
    }

    private void clearFields() {
        password.clear();
    }
}
