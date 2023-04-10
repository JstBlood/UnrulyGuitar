package client.scenes;

import java.io.IOException;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.ColorPreset;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BoardSettingsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    @FXML
    private VBox tagsBar;
    @FXML
    private HBox presetList;

    private Board board;

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

    /**
     * The Board settings view.
     * @param server The server connection.
     * @param mainCtrl The main (root) controller.
     */
    @Inject
    public BoardSettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Update the board to a new state.
     * @param newState The new state to merge with our current one.
     */
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

    /**
     * Add all tags on the merge to our current ones.
     */
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
                TagCtrl tagCtrl = tagLoader.getController();
                tagCtrl.delete.setOnAction(event -> {
                    try {
                        server.deleteTag(tag.id);
                    } catch (Exception e) {
                        UIUtils.showError("You cannot delete this tag since it is utilized somewhere else");
                    }
                    server.forceRefresh(mainCtrl.getCurrentBoard().key);
                });
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

    /**
     * Add a new tag.
     */
    @FXML
    public void addTag() {
        server.addTag(generateTag());
    }

    /**
     * Change this board's foreground.
     */
    @FXML
    public void foregroundChange() {
        server.updateBoard(board.key, "foreground",
                "#" + foregroundColor.getValue().toString().substring(2));
    }

    /**
     * Change this board's background.
     */
    @FXML
    public void backgroundChange() {
        server.updateBoard(board.key, "background",
                "#" + backgroundColor.getValue().toString().substring(2));
    }

    /**
     * Reset this board's foreground.
     */
    @FXML
    public void resetBoardForeground() {
        server.updateBoard(board.key, "foreground",
                Board.getDefaultForeground());
    }

    /**
     * Reset this board's background.
     */
    @FXML
    public void resetBoardBackground() {
        server.updateBoard(board.key, "background",
                Board.getDefaultBackground());
    }

    /**
     * Reset this board's list foreground.
     */
    @FXML
    public void resetListForeground() {
        server.updateBoard(board.key, "list/foreground",
                Board.getDefaultForeground());
    }

    /**
     * Reset this board's list background.
     */
    @FXML
    public void resetListBackground() {
        server.updateBoard(board.key, "list/background",
                Board.getDefaultBackground());
    }

    /**
     * Change this board's list foreground.
     */
    @FXML
    public void foregroundChangeList() {
        server.updateBoard(board.key, "list/foreground",
                "#" + foregroundColorList.getValue().toString().substring(2));
    }

    /**
     * Change this board's list background.
     */
    @FXML
    public void backgroundChangeList() {
        server.updateBoard(board.key, "list/background",
                "#" + backgroundColorList.getValue().toString().substring(2));
    }

    /**
     * Add a new color preset with default values.
     */
    public void addPreset() {
        server.addBoardPreset(board.key, new ColorPreset());
    }

    /**
     * Generate a new tag with default values.
     * @return The new tag in question.
     */
    private Tag generateTag() {
        return new Tag("New tag", mainCtrl.getCurrentBoard());
    }

    /**
     * Save all the preset changes.
     */
    public void save() {
        updatePreset();
        updatePreset();
        mainCtrl.showBoardOverview();
    }

    /**
     * Update the boards' default preset based on the value in the ChoiceBox.
     */
    private void updatePreset() {
        server.updateBoardDefaultPreset(board.key, Long.parseLong(defPreset.getSelectionModel().getSelectedItem()
                .substring(4)));
    }

    @FXML
    public void showHelp() {
        mainCtrl.showHelpScreen("boardSettings");
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
