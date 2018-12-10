package org.ma.motofx;

import com.sun.glass.ui.Screen;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ma.motofx.support.Utility;

/**
 *
 * @author maria
 */
interface entraEdEsce {

    default void postEntrata(Object controller) {
    }

    default void preUscita(Object controller) {
    }

}

public abstract class SceneManager implements entraEdEsce {

    private static Screen screen = Screen.getScreens().get(0);
    private final static int INTERNAL_MONITOR = 0;

    //...che è anche la scena attuale...
    private static SceneManager scenaAttuale = null;

    static final EnumMap<SceneManager.LeScene, SceneManager> SCENA
            = new EnumMap<>(SceneManager.LeScene.class);

    static SceneManager getScenaAttuale() {
        return scenaAttuale;
    }

    public enum LeScene {
        SETUP("TUNE!", "/fxml/FXMLSetup.fxml", "/styles/fxmlsetup.css", -1, -1),
        VIDEO("RUN!", "/fxml/FXMLVideo.fxml", "/styles/fxmlvideo.css", -1, -1),
        ADMIN("Administration", "/fxml/FXMLAdmin.fxml", "/styles/fxmladmin.css", 0, 0);
//        TEST("TEST", "/fxml/Scene.fxml", "/styles/Styles.css", 0, 0, -1, -1);
final String title;
        final String fxml;
        final String css;
        final int x;
        final int y;
        final int width;
        final int height;

        LeScene(String title, String fxml, String css, int x, int y) {
            this.title = title;
            this.fxml = fxml;
            this.css = css;
            this.x = x;
            this.y = y;
            this.width = -1;
            this.height = -1;
        }
    }

    final LeScene leScene;
    private Parent root = null;
    private Scene scene = null;
    private Stage stage = null;
    private final Object controller;

    SceneManager(LeScene ls, Stage st) {
        this.stage = st;
        this.leScene = ls;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ls.fxml));
        try {
            root = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SceneManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Dopo il .load() viene caricato il controller
        controller = loader.getController();
        scene = new Scene(root);
        scene.getStylesheets().add(ls.css);
        stage.setTitle(ls.title);
    }

    /**
     * Fulcro del manager. Il metodo si incarica di cambiare scena e di
     * richiamare pre-post metodi di interfaccia.
     */
    public void esponiLaScena() {
        if (scenaAttuale != null) {
            scenaAttuale.preUscita(scenaAttuale.getController());
        }
        stage.setScene(scene);

        //dimensioni scena
        stage.setX(leScene.x < 0 ? getScreen().getX() : leScene.x + getScreen().getX());
        stage.setY(leScene.y < 0 ? getScreen().getY() : leScene.y + getScreen().getY());
        stage.setWidth(leScene.width < 0 ? getScreen().getWidth() : leScene.width);
        stage.setHeight(leScene.height < 0 ? getScreen().getHeight() : leScene.height);

        postEntrata(controller);
        scenaAttuale = this;
    }

    /**
     * aggiungi questa scena come leaf alla root
     *
     */
    void openAsDialog() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(leScene.fxml));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(SceneManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stagist = new Stage();
        stagist.initOwner(stage);
        stagist.initModality(Modality.APPLICATION_MODAL);
//            stagist.initStyle(StageStyle.UNDECORATED);
        stagist.setTitle("ADMINISTRATOR");
        if (root1 != null) {
            stagist.setScene(new Scene(root1));
            stagist.showAndWait();
        }
    }

    /**
     * prende un controllo
     *
     * @return una classe controllo. ex: FXMLVideoController fx =
     * (FXMLVideoController)VIDEO.getController();
     */
    public Object getController() {
        return controller;
    }

    public Scene getScene() {
        return scene;
    }

    public Parent getRoot() {
        return root;
    }

    /**
     *
     */
    void setStyleSheetFromJar() {
        String css = SceneManager.class.getResource("/styles/fxmldata.css").toExternalForm();
        scene.getStylesheets().remove(0);

//        scene.getStylesheets().removeAll();
        scene.getStylesheets().add(css);
    }

    public void setStyleSheetFromFile(String cssSceneFile) {
        File f = new File("filecss.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
    }

    private static Screen getScreen() {
        return ((screen != null) ? screen : Screen.getScreens().get(INTERNAL_MONITOR));
    }

    /**
     * @param stage
     *
     */
    static void setScreen(Stage stage) {
        //Sbatto lo stage nel monitor X, se non c'è lo rimetto nel primary
        try {
            screen = Screen.getScreens().get(1);
        } catch (IndexOutOfBoundsException ex) {
            Utility.msgDebug("Non trovo il monitor " + 1 + ", lascio il primario");
            screen = Screen.getScreens().get(INTERNAL_MONITOR);
        }
//        stage.setX(screen.getX());
//        stage.setY(screen.getY());
//        stage.setWidth(screen.getWidth());
//        stage.setHeight(screen.getHeight());
            stage.initStyle(StageStyle.UNDECORATED);
    }
}
