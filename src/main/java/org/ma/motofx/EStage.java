package org.ma.motofx;

import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * Ogni ENUM è uno stage. Lo stage ha un nome, una dimensione, dice se è
 * decorato o meno. Per la descrizione completa leggi i parametri
 *
 * @author maria
 */
public enum EStage {
    SETUP("TUNE!", "/fxml/FXMLSetup.fxml", "/styles/fxmlsetup.css", -1, -1, -1, -1, StageStyle.UNDECORATED, Modality.NONE),
    VIDEO("RUN!", "/fxml/FXMLVideo.fxml", "/styles/fxmlvideo.css", -1, -1, -1, -1, StageStyle.UNDECORATED, Modality.NONE),
    POSTVIDEO("RANK", "/fxml/FXMLPostVideo.fxml", "/styles/fxmlpostvideo.css", -1, -1, -1, -1, StageStyle.UNDECORATED, Modality.NONE),
    ADMIN("Administration", "/fxml/FXMLAdmin.fxml", "/styles/fxmladmin.css", 100, 100, 800, 600, StageStyle.DECORATED, Modality.APPLICATION_MODAL),
    SERVER("Server", "/fxml/FXMLServer.fxml", "/styles/fxmlserver.css", 100, 100, 800, 600, StageStyle.DECORATED, Modality.NONE);
    final String title;
    final String fxml;
    final String css;
    final int x;
    final int y;
    final int width;
    final int height;
    final StageStyle decorated;
    final Modality modality;

    EStage(String title, String fxml, String css, int x, int y, int width, int height, StageStyle decorated, Modality modality) {
        this.title = title;
        this.fxml = fxml;
        this.css = css;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.decorated = decorated;
        this.modality = modality;
    }

}
