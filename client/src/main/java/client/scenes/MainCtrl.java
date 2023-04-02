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

import java.util.Objects;

import client.shared.CredentialsStore;
import commons.Board;
import commons.Card;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private Scene logon;

    private int silence = 0;

    private BoardsCtrl boardsCtrl;
    private Scene boards;

    private BoardOverviewCtrl boardOverviewCtrl;
    private Scene boardOverview;

    private Scene addCardList;

    private CardDetailsCtrl cardDetailsCtrl;
    private Scene cardDetails;

    private BoardSettingsCtrl boardSettingsCtrl;
    private Scene boardSettings;

    private CredentialsStore cStore;

    public CredentialsStore accessStore() {
        return cStore;
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public void initialize(Stage primaryStage,
                           Pair<LogonCtrl, Parent> logon,
                           Pair<BoardsCtrl, Parent> boards,
                           Pair<BoardOverviewCtrl, Parent> boardOverview,
                           Pair<AddCardListCtrl, Parent> addCardList,
                           Pair<CardDetailsCtrl, Parent> addCard,
                           Pair<BoardSettingsCtrl, Parent> boardSettings,
                           CredentialsStore cStore) {

        this.primaryStage = primaryStage;

        this.logon = new Scene(logon.getValue());

        this.boardsCtrl = boards.getKey();
        this.boards = new Scene(boards.getValue());

        this.boardOverviewCtrl = boardOverview.getKey();
        this.boardOverview = new Scene(boardOverview.getValue());

        this.addCardList = new Scene(addCardList.getValue());

        this.cardDetailsCtrl = addCard.getKey();
        this.cardDetails = new Scene(addCard.getValue());

        this.boardSettingsCtrl = boardSettings.getKey();
        this.boardSettings = new Scene(boardSettings.getValue());

        this.cStore = cStore;

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/client/images/unruly_guitar_icon.png"))));

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
        primaryStage.show();
    }

    public void showCardDetails(Card about) {
        cardDetailsCtrl.prepare(about);
        primaryStage.setTitle("Adding Card");
        primaryStage.setScene(cardDetails);
    }

    public void showBoardOverview() {
        // TODO: FIX ME
        //boardOverviewCtrl.prepareLongPolling();

        primaryStage.setTitle("Current board");
        primaryStage.setScene(boardOverview);
    }

    public void showAddCardList() {
        primaryStage.setTitle("Add a new List");
        primaryStage.setScene(addCardList);
    }

    public void showBoardSettings() {
        primaryStage.setTitle("Settings");
        primaryStage.setScene(boardSettings);
    }

    public void updateBoardSettings(Board newState) {
        boardSettingsCtrl.update(newState);
    }

    public Board getCurrentBoard() {
        return boardOverviewCtrl.getBoard();
    }

    public void setupBoardOverview(Board board) {
        boardOverviewCtrl.prepare(board);
    }
}