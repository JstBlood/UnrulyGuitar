package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    private CardList parentCard;

    @FXML
    private AnchorPane root;

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
    @FXML
    private ImageView addSubtask;
    @FXML
    private Label addSubtaskLabel;
    @FXML
    private GridPane subtaskPane;
    @FXML
    private TextField subtaskTitle;
    @FXML
    private TextArea subtaskDescription;
    private List<Task> subtasks;

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
        this.subtaskPane.setVisible(false);
    }

    public void setParentBoard(Board parentBoard) {
        this.parentBoard = parentBoard;
    }


    public void setParentCard(CardList parentCard) {
        this.parentCard = parentCard;
    }

    public void submitCard(){
        if (this.title.getText().equals("")){
            this.submit.setText("Please provide a title!");
            this.submit.setStyle("-fx-text-fill: red;");
            return;
        }
        this.parentCard.addCard(new Card(this.title.getText(), description.getText(), this.parentCard));
        this.subtaskPane.setVisible(true);
        System.out.println("Grid: " + this.subtaskPane.toString());

        // communicate it to the server

    }

    public void addSubtask(){

        System.out.println("Grid: " + this.subtaskPane.toString());


        ImageView iv = new ImageView("file:/G:/My%20Drive/My_documents/MADDY_uni/Y1%20Q3/OOPP/oopp-team-22/client/assets/trashcan_icon.png");
        iv.setFitHeight(50);
        iv.setFitWidth(50);
        TextField subtaskTitle = new TextField();
        subtaskTitle.setPrefWidth(300);
        subtaskTitle.setPrefHeight(50);
        subtaskTitle.setStyle("-fx-background-color: yellow;");
        subtaskTitle.setPromptText("Add subtask title...");
        TextArea subtaskDescription = new TextArea();
        subtaskDescription.setPrefWidth(300);
        subtaskDescription.setPrefHeight(100);
        subtaskDescription.setStyle("-fx-background-color: red;");
        subtaskDescription.setPromptText("Add description...");

        this.subtaskPane.add(new Text("SHOW URSELF"), 0, 0);
        this.subtaskPane.add(subtaskTitle, 1, 0);
        this.subtaskPane.add(new Text("PLEASE"), 0, 1);
        this.subtaskPane.add(subtaskDescription, 1, 1);

        System.out.println("Grid is now: " + this.subtaskPane.getRowCount() + " by " + this.subtaskPane.getColumnCount());
        for (Node child : this.subtaskPane.getChildren()) {
            System.out.println(child.toString());
        }
    }
}
