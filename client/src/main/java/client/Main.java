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
package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.*;
import client.shared.CredentialsStore;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new UnrulyModule());
    private static final UnrulyFXML FXML = new UnrulyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        var logon = FXML.load(LogonCtrl.class, "client", "scenes", "Logon.fxml");
        var boards = FXML.load(BoardsCtrl.class, "client", "scenes", "Boards.fxml");
        var boardOverview = FXML.load(BoardOverviewCtrl.class, "client",
                "scenes", "BoardOverview.fxml");
        var addCardList = FXML.load(AddCardListCtrl.class, "client", "scenes",
                "AddCardList.fxml");
        var cardDetails = FXML.load(CardDetailsCtrl.class, "client", "scenes",
                "CardDetails.fxml");
        var boardSettings = FXML.load(BoardSettingsCtrl.class, "client", "scenes",
                "BoardSettings.fxml");
        var mediaPlayer = FXML.load(MediaPlayerCtrl.class, "client", "scenes",
                "MediaPlayer.fxml");
        var locker = FXML.load(PasswordCtrl.class, "client", "scenes",
                "PasswordEntry.fxml");
        var helpScreen = FXML.load(HelpScreenCtrl.class, "client", "scenes", "HelpScreen.fxml");
        var tagsPopup = FXML.load(CardDetailsCtrl.class, "client", "scenes", "TagsPopup.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        var cStore = new CredentialsStore();

        mainCtrl.initialize(primaryStage,
                logon,
                boards,
                boardOverview,
                addCardList,
                cardDetails,
                boardSettings,
                mediaPlayer,
                locker,
                helpScreen,
                tagsPopup,
                cStore);

        primaryStage.setOnCloseRequest(e -> {
            try {
                boardOverview.getKey().stop();
            } catch (RuntimeException ignored) {
            }
        });
    }
}
