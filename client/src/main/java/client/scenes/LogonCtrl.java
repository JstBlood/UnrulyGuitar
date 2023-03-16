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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    @Inject
    public LogonCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void tryLogon() {
        server.setUrl(ip.getText());

        try {
            server.connect();
        } catch (Exception e) {
            UIUtils.showError(e.getMessage());
            return;
        }

        mainCtrl.showBoards();
    }
}
