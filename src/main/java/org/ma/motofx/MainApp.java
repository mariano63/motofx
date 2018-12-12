package org.ma.motofx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.MediaPlayer;
import org.ma.motofx.support.Rs232;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.ma.motofx.data.Prop;
import org.ma.motofx.support.Utility;

import javax.rmi.CORBA.Util;

public class MainApp extends Application {

    /**
     * Caching...
     */
    private static final Rs232 RS232 = new Rs232();
    static StageManager stageManager;

    @Override
    public void start(Stage stage) {

        stageManager = new StageManager(0) {
            @Override
            void postInit() {
                /**
                 * Platform.isFxApplicationThread() is true
                 */
                StageManager.getStage(EStage.VIDEO).focusedProperty().addListener(new ChangeListener<Boolean>()
                {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown)
                    {
//                Utility.msgDebug(" VIDEO focus onHidden:"+onHidden+", onShown"+onShown);
                        FXMLVideoController fxml = (FXMLVideoController)StageManager.getController(EStage.VIDEO);

                        if (fxml.getMediaPlayer() != null) {
                            if (onShown && fxml.getMediaPlayer().getStatus()!= MediaPlayer.Status.PLAYING) {
                                fxml.playTheVideo();
                            }
                            else {
                                if(fxml.getMediaPlayer().getStatus()== MediaPlayer.Status.PLAYING)
                                    fxml.getMediaPlayer().pause();
                            }
                        }
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (fxml.getMediaPlayer() != null) {
//                                    if (onShown) fxml.playTheVideo();
//                                    else fxml.getMediaPlayer().pause();
//                                }
//                            }
//                        });
                    }
                });
            }
        };

        Utility.msgDebug(RS232.open()? "RS232 Opened!":"RS232 Error:Not opened!");
        StageManager.showStage(EStage.SETUP);
        
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
                new EventHandler() {
            @Override
            public void handle(Event event) {
                //***Attento! un throw od un consume event, e si rischia di non uscire più...
                Utility.msgDebug("WindowEvent.WINDOW_CLOSE_REQUEST-->> ...exiting");
            }
        }
        );
    }


    @Override
    public void stop() throws Exception {
        Prop.getInstance().saveProperties();
        super.stop(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
