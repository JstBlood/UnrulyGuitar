package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.List;

public class AddCardCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private CardList parentCardList;

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

    @Inject
    public AddCardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void prepare(){
        EventHandler<ActionEvent> removeTagEvent = e -> {
            this.tagsBar.getButtons().remove((ToggleButton) e.getSource());
        };
        EventHandler<ActionEvent> addTagEvent = e -> {
            String tagName = ((MenuItem) e.getSource()).getText();
            System.out.println(this.tagsBar.getButtons().size() - 1);
            if (this.tagsBar.getButtons().stream().anyMatch(
                    b -> (b instanceof ToggleButton) && ((ToggleButton) b).getText().equals(tagName)))
                return;

            Tag tag = mainCtrl.getCurrentBoard().tags.stream().filter(t ->
                    t.name.equals(tagName)).findFirst().orElse(null);
            ToggleButton tagButton = new ToggleButton(tagName);
            tagButton.setStyle(String.format("-fx-background-color: rgb(%d, %d, %d);",
                    tag.color.getRed(), tag.color.getGreen(), tag.color.getBlue()));
            tagButton.getStyleClass().add("tag");

            tagButton.setOnAction(removeTagEvent);

            tagsBar.getButtons().add(tagButton);
        };
        for(Tag tag : mainCtrl.getCurrentBoard().tags){
            MenuItem mi = new MenuItem(tag.name);
            mi.setOnAction(addTagEvent);
            this.addTag.getItems().add(mi);
        }
    }

    public void setParentCardList(CardList parentCardList) {
        this.parentCardList = parentCardList;
    }

    public void submitCard(){
        // communicate it to the parent List
        if (this.title.getText().equals("")) {
            this.submit.setText("Please provide a title!");
            this.submit.setStyle("-fx-text-fill: red;");
            return;
        }

        // communicate it to the server
        try {
            Card newCard = generateCard();
            server.addCard(newCard);
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }

        // go back to the overview
        clearFields();
        mainCtrl.showBoardOverview();
    }

    public void addSubtask(){

        System.out.println("Grid: " + this.subtaskPane.toString());
        ImageView iv = new ImageView("file:/client/images/trashcan_icon.png");
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

        System.out.println("Grid is now: "
                + this.subtaskPane.getRowCount()
                + " by "
                + this.subtaskPane.getColumnCount());
        for (Node child : this.subtaskPane.getChildren()) {
            System.out.println(child.toString());
        }
    }

    public Card generateCard(){
        return new Card(this.title.getText(), this.description.getText(), this.parentCardList);
    }

    public void cancelCard() {
        mainCtrl.showBoard();
    }

    public void clearFields(){
        this.title.clear();
        this.description.clear();
    }
}

