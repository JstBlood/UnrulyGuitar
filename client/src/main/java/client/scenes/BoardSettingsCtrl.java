package client.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.ColorPreset;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class BoardSettingsCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    @FXML
    private HBox tagsBar;
    @FXML
    private HBox presetList;

    public Board board;

    @FXML
    private ColorPicker foregroundColor;
    @FXML
    private ColorPicker backgroundColor;
    @FXML
    private ColorPicker foregroundColorList;
    @FXML
    private ColorPicker backgroundColorList;

    @FXML
    private ChoiceBox<String> defPreset;

    @Inject
    public BoardSettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void update(Board newState) {
        board = newState;
        prepareTags();
        foregroundColor.setValue(Color.valueOf(board.colors.foreground));
        backgroundColor.setValue(Color.valueOf(board.colors.background));
        foregroundColorList.setValue(Color.valueOf(board.cardListColors.foreground));
        backgroundColorList.setValue(Color.valueOf(board.cardListColors.background));

        defPreset.getItems().clear();
        for(ColorPreset c : board.cardPresets) {
            defPreset.getItems().add("No #" + c.id);

            if (c.id == board.defaultPreset.id) {
                defPreset.getSelectionModel().select(defPreset.getItems().size() - 1);
            }
        }
    }

    private void prepareTags() {
        tagsBar.getChildren().clear();
        for(Tag tag : board.tags) {
            FXMLLoader tagLoader = new FXMLLoader(getClass().getResource("/client/scenes/Tag.fxml"));

            tagLoader.setControllerFactory(c ->
                    new TagCtrl(this.server, this.mainCtrl, tag)
            );

            Node newTagNode = null;
            try {
                newTagNode = tagLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            tagsBar.getChildren().add(newTagNode);
        }

        presetList.getChildren().clear();
        for(ColorPreset preset : board.cardPresets) {
            FXMLLoader presetLoader = new FXMLLoader(getClass().getResource("/client/scenes/ColorPreset.fxml"));

            presetLoader.setControllerFactory(c ->
                    new ColorPresetCtrl(this.server, this.mainCtrl, preset, board.key)
            );

            Node newPreset = null;
            try {
                newPreset = presetLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            presetList.getChildren().add(newPreset);
        }
    }

    @FXML
    public void addTag() throws IOException {
        server.addTag(generateTag());
    }

    @FXML
    public void foregroundChange() {
        server.updateBoard(board.key, "foreground",
                "#" + foregroundColor.getValue().toString().substring(2));
    }

    @FXML
    public void backgroundChange() {
        server.updateBoard(board.key, "background",
                "#" + backgroundColor.getValue().toString().substring(2));
    }

    @FXML
    public void resetBoardForeground() {
        server.updateBoard(board.key, "foreground",
                Board.getDefaultForeground());
    }

    @FXML
    public void resetBoardBackground() {
        server.updateBoard(board.key, "background",
                Board.getDefaultBackground());
    }

    @FXML
    public void resetListForeground() {
        server.updateBoard(board.key, "list/foreground",
                Board.getDefaultForeground());
    }

    @FXML
    public void resetListBackground() {
        server.updateBoard(board.key, "list/background",
                Board.getDefaultBackground());
    }

    @FXML
    public void foregroundChangeList() {
        server.updateBoard(board.key, "list/foreground",
                "#" + foregroundColorList.getValue().toString().substring(2));
    }

    @FXML
    public void backgroundChangeList() {
        server.updateBoard(board.key, "list/background",
                "#" + backgroundColorList.getValue().toString().substring(2));
    }

    public void addPreset() {
        server.addBoardPreset(board.key, new ColorPreset());
    }

    private Tag generateTag() {
        return new Tag("New tag", null, mainCtrl.getCurrentBoard());
    }

    public void save() {
        updatePreset();
        updatePreset();
        mainCtrl.showBoardOverview();
    }

    private void updatePreset() {
        server.updateBoardDefaultPreset(board.key, Long.parseLong(defPreset.getSelectionModel().getSelectedItem()
                .substring(4)));
    }
}
