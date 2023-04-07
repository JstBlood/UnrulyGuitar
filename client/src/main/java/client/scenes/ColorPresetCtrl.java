package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.ColorPreset;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorPresetCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private ColorPicker background;
    @FXML
    private ColorPicker foreground;
    @FXML
    private Label title;

    private ColorPreset colorPreset;
    private String parentKey;

    @Inject
    public ColorPresetCtrl(ServerUtils server, MainCtrl mainCtrl, ColorPreset colorPreset, String parentKey) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.colorPreset = colorPreset;
        this.parentKey = parentKey;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propagate();
    }


    public void propagate() {
        title.setText("No #" + colorPreset.id);
        background.setValue(Color.valueOf(colorPreset.background));
        foreground.setValue(Color.valueOf(colorPreset.foreground));
    }

    @FXML
    public void updateBackground() {
        server.updateBoard(parentKey, "preset/" + colorPreset.id + "/background",
                "#" + background.getValue().toString().substring(2));
    }

    @FXML
    public void updateForeground() {
        server.updateBoard(parentKey, "preset/" + colorPreset.id + "/foreground",
                "#" + foreground.getValue().toString().substring(2));
    }

    @FXML
    public void delete() {
        if(mainCtrl.accessUsedPresets().contains(colorPreset.id)) {
            UIUtils.showError("You cannot remove this preset it is used by cards or as a default");
            return;
        }

        server.deleteBoardPreset(parentKey, colorPreset.id);
    }
}
