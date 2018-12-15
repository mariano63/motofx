package org.ma.motofx;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import org.ma.motofx.data.ArduinoData;
import org.ma.motofx.data.Prop;
import org.ma.motofx.graph.ScaleThumbs;
import org.ma.motofx.support.AsyncTask;
import org.ma.motofx.support.Utility;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import static java.lang.Thread.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.MediaErrorEvent;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author ma
 */
@SuppressWarnings("unused")
public class FXMLVideoController implements Initializable {

    @FXML
    MediaView mediaView;
    private MediaPlayer mediaPlayer = null;
    @FXML
    private Pane PaneVideo;
    @FXML
    private Label labelPilota;
    @FXML
    private Label labelBike;
    @FXML
    private Button buttonExit;
    @FXML
    private Label labelLap;
    @FXML
    private Label labelTime;

    @FXML
    private ProgressBar progressBarFrontBreak;
    @FXML
    private ProgressBar progressBarRearBreak;
    @FXML
    private Group groupThumbs;
    @FXML
    private Text textPreCount;
    @FXML
    AnchorPane panePreCount;

    private boolean isPreCount = true;
    DoPreCount thd1;

    public Label getLabelLap() {
        return labelLap;
    }

    public Label getLabelTime() {
        return labelTime;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utility.msgDebug("Initialize video controller!!!");

        Scene scenaSetup = StageManager.getScene(EStage.SETUP);
//                SCENA.get(SceneManager.LeScene.SETUP).getScene();
        ScaleThumbs scaleThumbs
                = new ScaleThumbs(scenaSetup, groupThumbs);

    }

    public void postInitialize() {
        //Binds
        FXMLSetupController fxml = (FXMLSetupController) StageManager.getController(EStage.SETUP);
        labelBike.textProperty().bind(fxml.getTextfieldBike().textProperty());
        labelPilota.textProperty().bind(fxml.getTextfieldPilota().textProperty());
        progressBarFrontBreak.progressProperty().bind(ArduinoData.frenoAnteriorePercent.divide(100d));
        progressBarRearBreak.progressProperty().bind(ArduinoData.frenoPosteriorePercent.divide(100d));

        //Properties
//        StageManager.getStage(EStage.VIDEO).focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown) -> {
//            if (getMediaPlayer() != null) {
//                if (onShown && getMediaPlayer().getStatus() != MediaPlayer.Status.PLAYING) {
//                    playTheVideo();
//                } else {
//                    if (getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
//                        getMediaPlayer().pause();
//                    }
//                }
//            }
//        });
        StageManager.getStage(EStage.VIDEO).setOnHidden((WindowEvent event) -> {
            getMediaPlayer().pause();
        });
        StageManager.getStage(EStage.VIDEO).setOnShown((WindowEvent event) -> {
//            getMediaPlayer().play();
        });
        
        mediaView.setOnError((MediaErrorEvent arg0) -> {
            System.out.println("view error");
            arg0.getMediaError().printStackTrace(System.out);
        });
        setMediaViewFullSize(mediaView);
        PaneVideo.getChildren().setAll(mediaView);
        
    }

    private void setMediaViewFullSize(MediaView mv) {
        final DoubleProperty width = mv.fitWidthProperty();
        final DoubleProperty height = mv.fitHeightProperty();
        width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
        mv.setPreserveRatio(false);
    }
    
    public void playTheVideo() {
        if (mediaPlayer != null) {
            if (isPreCount) {
                thd1 = new DoPreCount(this);
                thd1.execute();
                isPreCount = false;
            }
            //mediaPlayer.play();
        }
    }

    class DoPreCount extends AsyncTask {

        private final FXMLVideoController controller;

        DoPreCount(FXMLVideoController controller) {
            this.controller = controller;
        }

        @Override
        public void onPreExecute() {
            panePreCount.setVisible(true);
        }

        //Datosi che il timer mi arriva da sistema
        @Override
        public Object doInBackground(Object... params) {
//            Utility.msgDebug("isFxApplicationThread dovrebbe essere falso:" + isFxApplicationThread());
            try {
                progressCallback("");
                sleep(1000);
                progressCallback("3");
                sleep(1000);
                progressCallback("2");
                sleep(1000);
                progressCallback("1");
                sleep(1000);
                progressCallback(-1);
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLVideoController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        public void onPostExecute(Object params) {
            panePreCount.setVisible(false);
        }

        @Override
        protected void progressCallback(Object... params) {
            textPreCount.setText(params[0] + "");
            if (params[0] instanceof Integer) {
                if ((Integer) params[0] == -1) {
                    if (StageManager.getEStageAttuale().equals(EStage.VIDEO)) {
                        mediaPlayer.play();
                    }
                }
            }
        }
    }


    /**
     * Il filmato precedente partirà daccapo, dato il dispose.
     */
    @SuppressWarnings("unused")
    public void changeVideo() {
        isPreCount = true;
        Path videoPath = Prop.getMediaUrl();
        if (!videoPath.toFile().exists()) {
            videoPath = Prop.getMediaUrlDefault();
            Prop.setMediaUrl(Prop.getMediaUrlDefault());
        }
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
        Media ilVideo = new Media(videoPath.toUri().toString());
        ilVideo.setOnError(() -> {
            System.out.println("Media error");
            ilVideo.getError().printStackTrace(System.out);
        });
        mediaPlayer = new MediaPlayer(ilVideo);
        mediaPlayer.setOnError(() -> {
            System.out.println("player error");
            mediaPlayer.getError().printStackTrace(System.out);
        });
        
        mediaView.setMediaPlayer(mediaPlayer);
        VideoProcessing videoProcessing = new VideoProcessing(mediaPlayer,
                mediaView,
                (FXMLVideoController) StageManager.getController(EStage.VIDEO)
        );
    }

    @FXML
    private void ButBackFromVideoOnAction(ActionEvent event) {
        thd1.getBackGroundThread().interrupt();
        mediaPlayer.pause();
        StageManager.showStage(EStage.SETUP);
    }

//    private void playaa(ActionEvent event) {
//        Prop.setMediaUrl(Paths.get(Prop.CIRCUITI.toString(), "aa.mp4"));
//        changeVideo();
//    }
}
