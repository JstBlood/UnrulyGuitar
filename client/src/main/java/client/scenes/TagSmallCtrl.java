package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * This is the controller for the Tag scene which represents a tag.
 */
public class TagSmallCtrl implements Initializable {
    @FXML
    private TextField name;

    public Tag tag;

    /**
     * Create a small (uneditable) tag object.
     * @param tag The initial tag object.
     */
    @Inject
    public TagSmallCtrl(Tag tag) {
        this.tag = tag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setStyle("-fx-text-fill: " + tag.colors.foreground + ";" +
                "-fx-background-color: " + tag.colors.background + ";");
        name.setText(tag.name);

        Text text = new Text(tag.name);
        text.setFont(name.getFont());
        double size = text.getLayoutBounds().getWidth()
                + 18d;
        name.setPrefWidth(size);
    }
}
