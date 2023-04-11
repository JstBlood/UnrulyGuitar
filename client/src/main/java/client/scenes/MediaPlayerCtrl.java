package client.scenes;

import java.io.*;
import java.net.MalformedURLException;
import javax.inject.Inject;

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
        setColors();
        // Disable buttons until a media file is loaded

        playButton.setDisable(true);
        playButton.setVisible(false);
        timeSlider.setDisable(true);
        timeSlider.setVisible(false);

        if(card.file!=null)
        {
            if(!card.file.equals("nope"))
            {
                this.file=server.getFile(card.file);
                openFile(file);
            }
        } else card.setFile("nope");
    }
    @FXML
    private void handleOpenFile() throws MalformedURLException {
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
    private void openFile(File file) throws MalformedURLException {
        if (file != null) {
            this.file=file;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            // Check if the selected file is a media file or an image
            String filename = file.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            if (extension.equals("jpg") || extension.equals("png")) {
                // Display the selected image in the image view
                disableVideo();

                imageView.setImage(new Image(file.toURI().toString()));

                imageSetup();
            } else {
                // Create a media player and set it to the media view
                disableImage();

                Media media = new Media(file.toURI().toURL().toString());
                mediaPlayer = new MediaPlayer(media);

                mediaView.setMediaPlayer(mediaPlayer);

                videoSetup();
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
    private void setColors() {
        if(card.colors == null) {
            anchorPane.setStyle("-fx-background-color: " +
                    card.parentCardList.parentBoard.defaultPreset.background + ";");
        }
        else {
            anchorPane.setStyle("-fx-background-color: " + card.colors.background + ";");
        }
    }
    private void videoSetup() {
        AnchorPane.setTopAnchor(mediaView, 40.0);
        AnchorPane.setBottomAnchor(mediaView, 0.0);

        mediaView.setFitHeight(anchorPane.getHeight() - 135.0);
        mediaView.setFitWidth(anchorPane.getWidth());
        anchorPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            // Update the imageView's fitWidth and position when the width changes
            mediaView.setFitWidth(newValue.doubleValue());
        });

        anchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            // Update the imageView's fitHeight and position when the height changes
            mediaView.setFitHeight(newValue.doubleValue() - 135.0);
        });
    }

    private void imageSetup() {
        AnchorPane.setTopAnchor(imageView, 40.0);
        AnchorPane.setBottomAnchor(imageView, 0.0);

        // Add a listener to the AnchorPane's width and height properties
        anchorPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            // Update the imageView's fitWidth and position when the width changes
            imageView.setFitWidth(newValue.doubleValue());
        });

        anchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            // Update the imageView's fitHeight and position when the height changes
            imageView.setFitHeight(newValue.doubleValue() - 100.0);
        });
    }

    @FXML
    private void save() throws IOException {
        if(this.file!=null)
            if (!card.file.equals(file.getName())) {
                if (!card.file.equals("nope")) {
                    server.deleteFile(card.file);
                }
                String fileName=card.id+file.getName().substring(file.getName().lastIndexOf("."));
                card.setFile(fileName);
                server.updateCard(card.id, "file", card.file);
                server.uploadFile(copyFile(fileName));
            }
    }
    private File copyFile(String name) throws IOException {
        File newFile = new File(name);

        // Copy the contents of the original file to the new file
        InputStream in = new FileInputStream(file);
        OutputStream out = new FileOutputStream(newFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
        return newFile;
    }
    @FXML
    private void delete(){
        if(this.file!=null)
        {
            disableVideo();
            imageView.setVisible(false);
            if(!card.file.equals("nope"))
            {
                card.setFile("nope");
                server.updateCard(card.id, "file", card.file);
                server.deleteFile(file.getName());
            }
        }
    }
    private void disableImage(){
        mediaView.setVisible(true);
        imageView.setVisible(false);
        playButton.setVisible(true);playButton.setDisable(false);
        timeSlider.setVisible(true);timeSlider.setDisable(false);
        timeSlider.setValue(timeSlider.getMin());
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