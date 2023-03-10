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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private LogonCtrl logonCtrl;
    private Scene logon;

    private BoardsCtrl boardsCtrl;
    private Scene boards;

    private CardListCtrl listCtrl;
    private Scene list;


    public void initialize(Stage primaryStage, Pair<LogonCtrl, Parent> logon, Pair<BoardsCtrl, Parent> boards,
                           Pair<CardListCtrl, Parent> list) {
        this.primaryStage = primaryStage;

        this.logonCtrl = logon.getKey();
        this.logon = new Scene(logon.getValue());

        this.boardsCtrl = boards.getKey();
        this.boards = new Scene(boards.getValue());

        this.listCtrl= list.getKey();
        this.list=new Scene(list.getValue());
        showLogon();
        primaryStage.show();
    }

    public void showLogon() {
        primaryStage.setTitle("Server Connection");
        primaryStage.setScene(logon);
    }

    public void showBoards() {
        boardsCtrl.prepare();
        primaryStage.setTitle("Pick a board");
        primaryStage.setScene(boards);
    }

    public void showList(){
        primaryStage.setTitle("Lists");
        primaryStage.setScene(list);
    }
}