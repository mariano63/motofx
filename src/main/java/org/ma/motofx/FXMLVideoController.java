package org.ma.motofx;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        FXMLSetupController fxml = (FXMLSetupController)
                StageManager.getController(EStage.SETUP);
        labelBike.textProperty().bind(fxml.getTextfieldBike().textProperty());
        labelPilota.textProperty().bind(fxml.getTextfieldPilota().textProperty());
        progressBarFrontBreak.progressProperty().bind(ArduinoData.frenoAnteriorePercent.divide(100d));
        progressBarRearBreak.progressProperty().bind(ArduinoData.frenoPosteriorePercent.divide(100d));

        Scene scenaSetup = StageManager.getScene(EStage.SETUP);
//                SCENA.get(SceneManager.LeScene.SETUP).getScene();
        ScaleThumbs scaleThumbs =
                new ScaleThumbs(scenaSetup, groupThumbs);

    }

    public void playTheVideo() {
        if (mediaPlayer != null) {
            if (isPreCount) {
                thd1 = new DoPreCount(this);
                thd1.execute();
                isPreCount=false;
            }
            //mediaPlayer.play();
            mediaPlayer.setCycleCount(1);
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
                sleep(2000);
                progressCallback("3");
                sleep(2000);
                progressCallback("2");
                sleep(2000);
                progressCallback("1");
                sleep(2000);
                progressCallback(-1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Object params) {
            panePreCount.setVisible(false);
        }

        @Override
        protected void progressCallback(Object... params) {
            textPreCount.setText(params[0]+"");
            if (params[0] instanceof Integer) {
                if ((Integer)params[0] == -1) {
                    if(StageManager.getEStageAttuale().equals(EStage.VIDEO))
                        mediaPlayer.play();
                }
            }
        }
    }

    private void setMediaViewFullSize(MediaView mv) {
        final DoubleProperty width = mv.fitWidthProperty();
        final DoubleProperty height = mv.fitHeightProperty();
        width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
        mv.setPreserveRatio(false);
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
        mediaPlayer = new MediaPlayer(ilVideo);
        mediaView.setMediaPlayer(mediaPlayer);
        setMediaViewFullSize(mediaView);
        PaneVideo.getChildren().setAll(mediaView);
        VideoProcessing videoProcessing = new VideoProcessing(mediaPlayer
                , mediaView
                , (FXMLVideoController) StageManager.getController(EStage.VIDEO)
        );
    }

    @FXML
    private void ButBackFromVideoOnAction(ActionEvent event) {
        thd1.getBackGroundThread().interrupt();
        StageManager.showStage(EStage.SETUP);
    }

//    private void playaa(ActionEvent event) {
//        Prop.setMediaUrl(Paths.get(Prop.CIRCUITI.toString(), "aa.mp4"));
//        changeVideo();
//    }
}
