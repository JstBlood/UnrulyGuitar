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
package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


/**
 * This class is the controller of the Logon scene,
 * which is the starting view as the user opens the client
 * and it's functionalities are:
 * 1. connect the user to a server
 * 2. the user can choose a username
 */

public class LogonCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField ip;

    @FXML
    private TextField username;

    @FXML
    private Button submit;

    @FXML
    private CheckBox adminChk;

    @FXML
    private TextField admin;

    @Inject
    public LogonCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void initialize() {
        this.ip.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressedIP);
        this.admin.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressedAdmin);
    }

    public void unveilAdmin() {
        if(adminChk.isSelected())
            admin.setDisable(false);
        else
            admin.setDisable(true);
    }

    /**
     * tries to log a user in
     */
    public void tryLogon() {
        if(username.getText().equals("")) {
            UIUtils.showError("You need to provide a username");
            return;
        }

        if(username.getText().endsWith("_admin")) {
            UIUtils.showError("Invalid username please pick a different one.");
            return;
        }

        mainCtrl.accessStore().setUrl(ip.getText());
        mainCtrl.accessStore().setUsername(username.getText());

        if(adminChk.isSelected()) {
            mainCtrl.accessStore().setPassword(admin.getText());
            mainCtrl.accessStore().setAdmin();
        }else {
            mainCtrl.accessStore().removePassword();
            mainCtrl.accessStore().unsetAdmin();
        }

        try {
            server.connect();

            if(adminChk.isSelected())
                server.getBoards();
        } catch (ForbiddenException e) {
            UIUtils.showError("Access denied, invalid administrative password");
            return;
        }  catch (Exception e) {
            UIUtils.showError(e.getMessage());
            return;
        }

        mainCtrl.showBoards();
    }

    public void keyPressedIP(KeyEvent e) {

        if (!adminChk.isSelected()) {
            switch (e.getCode()) {
                case ENTER:
                    tryLogon();
                    break;
                default:
                    break;
            }
        }

    }

    public void keyPressedAdmin(KeyEvent e) {

        if (adminChk.isSelected()) {
            switch (e.getCode()) {
                case ENTER:
                    tryLogon();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * shows a useful help screen
     */
    @FXML
    public void showHelp() {
        mainCtrl.showHelpScreen("logon");
    }
}
