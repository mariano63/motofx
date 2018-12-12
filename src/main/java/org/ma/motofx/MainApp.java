package org.ma.motofx;

import org.ma.motofx.support.Rs232;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.ma.motofx.data.Prop;
import org.ma.motofx.support.Utility;

public class MainApp extends Application {

    /**
     * Caching...
     */
    private static final Rs232 RS232 = new Rs232();
    static StageManager stageManager;

    @Override
    public void start(Stage stage) {

        stageManager = new StageManager(0);
//         SCENA.put(LeScene.VIDEO,new SceneManager(LeScene.VIDEO, stage) {
//            @Override
//            public void postEntrata(Object controller) {
//                //Il controller qui è relativo alla scena dove si è
//                //deciso di andare.Quindi di sicuro non VIDEO, che rileggeremo 
//                //allora in modo diretto.
//                FXMLVideoController fxml = (FXMLVideoController)SCENA.get(SceneManager.LeScene.VIDEO).getController();
//                if(fxml.getMediaPlayer()!=null){
//                    fxml.playTheVideo();                    
//                }
//            }
//            
//            @Override
//            public void preUscita(Object controller) {
//                //dovunque si finisca dalla scena video, si mette in pausa il filmato
//                //Il controller qui è relativo alla scena VIDEO
//                FXMLVideoController fxml = (FXMLVideoController)controller;
//                if(fxml.getMediaPlayer()!=null){
//                    fxml.getMediaPlayer().pause();
//                }
//            }
//        });
        Utility.msgDebug(RS232.open()? "RS232 Opened!":"RS232 Error:Not opened!");
        stageManager.showStage(EStage.SETUP);
        
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
