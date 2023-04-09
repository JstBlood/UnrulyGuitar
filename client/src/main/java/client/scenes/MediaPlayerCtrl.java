package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class MediaPlayerCtrl {

    @FXML
    private Button playButton;

    @FXML
    private Slider timeSlider;

    @FXML
    private MediaView mediaView;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView imageView;

    private MediaPlayer mediaPlayer;
    private File file;

    private boolean isPlaying = false;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;

    @Inject
    public MediaPlayerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
    public void initialize(Card c) throws IOException {
        this.card=c;
        // Disable buttons until a media file is loaded
        playButton.setDisable(true);
        playButton.setVisible(false);
        timeSlider.setDisable(true);
        timeSlider.setVisible(false);
        mediaView.fitWidthProperty().bind(anchorPane.widthProperty());
        mediaView.fitHeightProperty().bind(anchorPane.heightProperty());
        imageView.fitWidthProperty().bind(anchorPane.widthProperty());
        imageView.fitHeightProperty().bind(anchorPane.heightProperty());
        if(card.getFile()!=null)
        {
            if(!card.getFile().equals("nope"))
            {
                this.file=server.getFile(card.getFile());
                openFile(file);
            }
        } else card.setFile("nope");
    }
    @FXML
    private void handleOpenFile() {
        // Create a file chooser dialog and set the initial directory
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        // Filter for media files
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Media Files (*.mp4, *.mp3, *.wav, *.jpg, *.png)", "*.mp4", "*.mp3", "*.wav", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        // Show the file chooser dialog and get the selected file
        Stage stage = (Stage) mediaView.getScene().getWindow();
        openFile(fileChooser.showOpenDialog(stage));
    }
    private void openFile(File file){
        if (file != null) {
            this.file=file;
            // Check if the selected file is a media file or an image
            String filename = file.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            if (extension.equals("jpg") || extension.equals("png")) {
                // Display the selected image in the image view
                disableVideo();
                imageView.setImage(new Image(file.toURI().toString()));
            } else {
                // Create a media player and set it to the media view
                disableImage();
                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                // Bind the time slider to the media player's current time
                timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (timeSlider.isValueChanging()) {mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));}
                });
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    if (!timeSlider.isValueChanging()) {timeSlider.setValue(newValue.toSeconds());}
                });
                mediaPlayer.setOnReady(() -> timeSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds()));
            }
        }
    }
    @FXML
    private void save() throws IOException {
        if(this.file!=null)
            if(!card.getFile().equals(file.getName()))
            {
                String newFileName = card.getId() + file.getName().substring(file.getName().lastIndexOf("."));
                // Rename the file
                file.renameTo(new File(file.getParent(), newFileName));
                card.setFile(file.getName());
                server.uploadFile(file);
            }
    }
    @FXML
    private void delete(){
        disableVideo();
        imageView.setVisible(false);
        card.setFile("nope");
        server.deleteFile(file.getName());
    }
    private void disableImage(){
        mediaView.setVisible(true);
        imageView.setVisible(false);
        playButton.setVisible(true);playButton.setDisable(false);
        timeSlider.setVisible(true);timeSlider.setDisable(false);
    }
    private void disableVideo(){
        pause();
        mediaView.setVisible(false);
        imageView.setVisible(true);
        playButton.setVisible(false);playButton.setDisable(true);
        timeSlider.setVisible(false);timeSlider.setDisable(true);
    }
    private void pause(){
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            playButton.setText("Play");
        }
    }
    @FXML
    private void handlePlay() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.play();
            isPlaying = true;
            playButton.setText("Pause");
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                pause();
            });
        }else pause();
    }

    @FXML
    private void handleTimeSliderChanged() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(mediaPlayer.getMedia().getDuration()
                    .multiply(timeSlider.getValue() / timeSlider.getMax()));
        }
    }

    @FXML
    private void backButton(){
        playButton.setVisible(false);playButton.setDisable(true);
        timeSlider.setVisible(false);timeSlider.setDisable(true);
        mediaView.setVisible(false);
        imageView.setVisible(false);
        pause();
        mainCtrl.showCardDetails(card);
    }
}