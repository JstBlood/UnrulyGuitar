package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Card;
import commons.Entry;
import commons.Tag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class AddCardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Board parentBoard;
    private Card parentCard;

    @FXML
    private TextField title;
    @FXML
    private ButtonBar tagsBar;
    @FXML
    private MenuButton addTag;
    @FXML
    private TextArea description;
    @FXML
    private Button submit;

    // temporary variable, because the Board class wasn't yet sufficiently implemented at the time of this commit.
    // replace with parentboard.tags, and set the value in MainCtrl when the Board is sufficiently implemented.
    private final Set<Tag> PARENTBOARD_DOT_TAGS = new HashSet<Tag>(List.of(new Tag("Tag 1", Color.BLUE), new Tag("Tag 2", Color.RED)));

    @Inject
    public AddCardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        EventHandler<ActionEvent> removeTagEvent = e -> {
            this.tagsBar.getButtons().remove((ToggleButton) e.getSource());
        };
        EventHandler<ActionEvent> addTagEvent = e -> {
            String tagName = ((MenuItem) e.getSource()).getText();
            System.out.println(this.tagsBar.getButtons().size() - 1);
            if (this.tagsBar.getButtons().stream().anyMatch(
                    b -> (b instanceof ToggleButton) && ((ToggleButton) b).getText().equals(tagName)))
                return;

            Tag tag = PARENTBOARD_DOT_TAGS.stream().filter(t -> t.name.equals(tagName)).findFirst().orElse(null);
            ToggleButton tagButton = new ToggleButton(tagName);
            tagButton.setStyle(String.format("-fx-background-color: rgb(%d, %d, %d);",
                    tag.color.getRed(), tag.color.getGreen(), tag.color.getBlue()));
            tagButton.getStyleClass().add("tag");

            tagButton.setOnAction(removeTagEvent);

            tagsBar.getButtons().add(tagButton);
        };
        for(Tag tag : PARENTBOARD_DOT_TAGS){
            MenuItem mi = new MenuItem(tag.name);
            mi.setOnAction(addTagEvent);
            this.addTag.getItems().add(mi);
        }
    }

    public void setParentBoard(Board parentBoard) {
        this.parentBoard = parentBoard;
    }

    public void setParentCard(Card parentCard) {
        this.parentCard = parentCard;
    }

    public void submitCard(){
        if (this.title.getText().equals("")){
            this.submit.setText("Please provide a title!");
            this.submit.setStyle("-fx-text-fill: red;");
            return;
        }
        this.parentCard.addEntry(new Entry(this.title.getText(), Color.BLACK, 20, "none", this.parentCard));
    }
}
