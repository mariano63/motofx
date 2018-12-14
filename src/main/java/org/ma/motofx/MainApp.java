package org.ma.motofx;

import org.ma.motofx.support.Rs232;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.Event;
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

        stageManager = new StageManager(0) {
            @Override
            void postInit() {
                /**
                 * Platform.isFxApplicationThread() is true
                 */
                ((FXMLVideoController)StageManager.getController(EStage.VIDEO)).postInitialize();
                ((FXMLSetupController)StageManager.getController(EStage.SETUP)).postInitialize();
            }
        };

        Utility.msgDebug(RS232.open()? "RS232 Opened!":"RS232 Error:Not opened!");
        StageManager.showStage(EStage.SETUP);
        
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (Event event) -> {
            //***Attento! un throw od un consume event, e si rischia di non uscire più...
            Utility.msgDebug("WindowEvent.WINDOW_CLOSE_REQUEST-->> ...exiting");
        });
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
