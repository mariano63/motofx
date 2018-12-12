package org.ma.motofx;

import com.sun.glass.ui.Screen;
import java.io.IOException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author maria
 */

abstract class StageManager {

    private static Screen screen;
    private static final EnumMap<EStage, DataStage> STAGES = new EnumMap<>(EStage.class);
    private static EStage stageAttuale;

    public StageManager(int numeroSchermo) {
        setScreen(numeroSchermo);
        for (EStage estage : EStage.values()) {
            stageAttuale = estage;
            DataStage ds = new DataStage();
            ds.stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(estage.fxml));
            try {
                ds.root = loader.load();
            } catch (IOException ex) {
                Logger.getLogger(StageManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Dopo il .load() viene caricato il controller
            ds.controller = loader.getController();
            ds.scene = new Scene(ds.root);
            ds.scene.getStylesheets().add(estage.css);
            ds.stage.setTitle(estage.title);
            ds.stage.initStyle(estage.decorated);
            ds.stage.setScene(ds.scene);
            ds.stage.setX(estage.x < 0 ? getScreen().getX() : estage.x + getScreen().getX());
            ds.stage.setY(estage.y < 0 ? getScreen().getY() : estage.y + getScreen().getY());
            ds.stage.setWidth(estage.width < 0 ? getScreen().getWidth() : estage.width);
            ds.stage.setHeight(estage.height < 0 ? getScreen().getHeight() : estage.height);
            ds.stage.show();
            STAGES.put(estage, ds);
        }
        postInit();
    }

    //Qui andranno tutti i BIND e i vari cazzi relativi agli STAGE focus-hide, etc...
    abstract void postInit();

    public static Stage getStageAttuale() {
        return STAGES.get(stageAttuale).stage;
    }
    public static EStage getEStageAttuale() {
        return stageAttuale;
    }

    static Object getController(EStage es) {
        return STAGES.get(es).controller;
    }
    static Stage getStage(EStage es) {
        return STAGES.get(es).stage;
    }
    static Scene getScene(EStage es){
        return STAGES.get(es).scene;
    }
    static void showStage(EStage es) {
//        STAGES.get(stageAttuale).stage.hide();        
        STAGES.get(es).stage.toFront();
        stageAttuale = es;
    }

    private static Screen getScreen() {
        return ((screen != null) ? screen : Screen.getScreens().get(0));
    }

    /**
     * @param nMonitor monitor. (0=internal)
     */
    private static void setScreen(int nMonitor) {
        //Sbatto lo stage nel monitor X, se non c'è lo rimetto nel primary
        try {
            screen = Screen.getScreens().get(nMonitor);
        } catch (IndexOutOfBoundsException ex) {
            screen = Screen.getScreens().get(0);
        }
    }
}
class DataStage {

    Parent root;
    Scene scene;
    Stage stage;
    Object controller;

    public DataStage() {
    }
}
