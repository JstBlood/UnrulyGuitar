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

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import client.shared.CredentialsStore;
import commons.Board;
import commons.Card;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

public class MainCtrl {
    private final String passwordFile = "passwords.bin";
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
    private Scene lockscreen;
    private PasswordCtrl lockscreenCtrl;

    private HelpScreenCtrl helpScreenCtrl;
    private Scene helpScreen;
    private MediaPlayerCtrl mediaPlayerCtrl;
    private Scene mediaPlayer;

    private CardDetailsCtrl tagsPopupCtrl;
    private Scene tagsPopup;

    private CredentialsStore cStore;

    private HashMap<String, String> passwordStore = new HashMap<>();

    private HashSet<Long> usedPresets = new HashSet<>();

    public CredentialsStore accessStore() {
        return cStore;
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public void initialize(Stage primaryStage,
                           Pair<LogonCtrl, Parent> logon,
                           Pair<BoardsCtrl, Parent> boards,
                           Pair<BoardOverviewCtrl, Parent> boardOverview,
                           Pair<AddCardListCtrl, Parent> addCardList,
                           Pair<CardDetailsCtrl, Parent> cardDetails,
                           Pair<BoardSettingsCtrl, Parent> boardSettings,
                           Pair<MediaPlayerCtrl, Parent> mediaPlayer,
                           Pair<PasswordCtrl, Parent> locker,
                           Pair<HelpScreenCtrl, Parent> helpScreen,
                           Pair<CardDetailsCtrl, Parent> tagsPopup,
                           CredentialsStore cStore) {

        this.primaryStage = primaryStage;

        this.logon = new Scene(logon.getValue());

        this.boardsCtrl = boards.getKey();
        this.boards = new Scene(boards.getValue());

        this.boardOverviewCtrl = boardOverview.getKey();
        this.boardOverview = new Scene(boardOverview.getValue());

        this.addCardList = new Scene(addCardList.getValue());

        this.cardDetailsCtrl = cardDetails.getKey();
        this.cardDetails = new Scene(cardDetails.getValue());

        this.boardSettingsCtrl = boardSettings.getKey();
        this.boardSettings = new Scene(boardSettings.getValue());

        this.lockscreen = new Scene(locker.getValue());
        this.lockscreenCtrl = locker.getKey();

        this.helpScreenCtrl = helpScreen.getKey();
        this.helpScreen = new Scene(helpScreen.getValue());

        this.tagsPopupCtrl = tagsPopup.getKey();
        this.tagsPopup = new Scene(tagsPopup.getValue());

        this.mediaPlayerCtrl = mediaPlayer.getKey();
        this.mediaPlayer = new Scene(mediaPlayer.getValue());

        this.cStore = cStore;

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/client/images/unruly_guitar_icon.png"))));

        try {
            readPasswords();
        } catch (Exception e) {
            System.out.println("No password file found, continuing..." + e.getMessage());
        }

        prepareHelp();
        prepareBoardOverview();
        prepareCardDetails();

        showLogon();
        primaryStage.show();
    }

    public void flushPasswords() throws IOException {
        FileOutputStream f = null;
        ObjectOutputStream o = null;

        try {
            f = new FileOutputStream(passwordFile);
            o = new ObjectOutputStream(f);

            o.writeObject(this.passwordStore);
        } catch (Exception e) {
            System.out.println("Failed to write passwords file: " + e.getMessage());
        } finally {
            if(o != null)
                o.close();
            if(f != null)
                f.close();
        }
    }

    @SuppressWarnings("unchecked")
    private void readPasswords() throws IOException {
        FileInputStream f = null;
        ObjectInputStream o = null;

        try {
            f = new FileInputStream(passwordFile);
            o = new ObjectInputStream(f);

            this.passwordStore = (HashMap<String, String>) o.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if(o != null)
                o.close();
            if(f != null)
                f.close();
        }
    }

    public void setPassword(String password) {
        if(accessStore().isAdmin())
            return;

        accessStore().setPassword(password);
        this.passwordStore.put(getCurrentBoard().key, password);
        silenceFilesystemErrors();
    }

    public void silenceFilesystemErrors() {
        try {
            flushPasswords();
        } catch (Exception e) {
            System.out.println("Writing password file failed...");
        }
    }

    public void removePassword() {
        if(accessStore().isAdmin())
            return;

        accessStore().removePassword();
        this.passwordStore.remove(getCurrentBoard().key);
        silenceFilesystemErrors();
    }

    public HashSet<Long> accessUsedPresets() {
        return usedPresets;
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

    public void showCardDetails(Card card) {
        cardDetailsCtrl.prepare(card, false);
        primaryStage.setTitle("Card Details");
        primaryStage.setScene(cardDetails);
    }

    public void showBoardOverview() {
        if(!accessStore().isAdmin() && passwordStore.containsKey(getCurrentBoard().key)) {
            accessStore().setPassword(
                    passwordStore.get(getCurrentBoard().key));
        } else if(!accessStore().isAdmin()) {
            accessStore().removePassword();
        }

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

    public void showUnlock() {
        primaryStage.setTitle("Passwords");
        lockscreenCtrl.stepTwo();
        primaryStage.setScene(lockscreen);
    }

    public void updateBoardSettings(Board newState) {
        boardSettingsCtrl.update(newState);
    }

    public void showHelpScreen(String prevScene) {
        helpScreenCtrl.setPrevScene(prevScene);
        primaryStage.setTitle("Help");
        primaryStage.setScene(helpScreen);
    }

    public void showTagsPopup(Card card) {
        tagsPopupCtrl.prepare(card, true);
        primaryStage.setTitle("Tags popup");
        primaryStage.setScene(tagsPopup);
    }
    public void showMediaPlayer(Card card) throws IOException {
        mediaPlayerCtrl.initialize(card);
        primaryStage.setTitle("Media Player");
        primaryStage.setScene(mediaPlayer);
    }
    public void prepareHelp() {
        logon.setOnKeyPressed(e -> {
            if (e.isShiftDown() && e.getCode().equals(KeyCode.SLASH)) {
                showHelpScreen("logon");
            }
        });
        boards.setOnKeyPressed(e -> {
            if (e.isShiftDown() && e.getCode().equals(KeyCode.SLASH)) {
                showHelpScreen("boards");
            }
        });
        boardOverview.setOnKeyPressed(e -> {
            if (e.isShiftDown() && e.getCode().equals(KeyCode.SLASH)) {
                showHelpScreen("boardOverview");
            }
        });
        boardSettings.setOnKeyPressed(e -> {
            if (e.isShiftDown() && e.getCode().equals(KeyCode.SLASH)) {
                showHelpScreen("boardSettings");
            }
        });
    }

    public void prepareBoardOverview() {
        //TODO : set traversal engine
    }

    public void prepareCardDetails() {
        cardDetails.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ESCAPE)) {
                showBoardOverview();
            }
        });
    }

    public Board getCurrentBoard() {
        return boardOverviewCtrl.getBoard();
    }

    public void setupBoardOverview(Board board) {
        boardOverviewCtrl.prepare(board);
        lockscreenCtrl.prepare();
    }


}