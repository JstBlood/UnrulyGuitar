package client.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class BoardSettingsCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    @FXML
    private HBox tagsBar;
    public List<Tag> tags = new ArrayList<>();

    @Inject
    public BoardSettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareTags();
    }

    public void refreshTags(List<Tag> updatedTags) {
        tags = updatedTags;
        prepareTags();
    }

    private void prepareTags() {
        for(Tag tag : tags) {
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
    }

    @FXML
    public void addTag() throws IOException {
        server.addTag(generateTag());
    }

    private Tag generateTag() {
        return new Tag("New tag", null, mainCtrl.getCurrentBoard());
    }

    public void save() {
        mainCtrl.showBoardOverview();
    }
}
