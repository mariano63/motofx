package org.ma.motofx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import static org.ma.motofx.SceneManager.SCENA;

public class FXMLSceneController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button button;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me! I change Scene");
        SCENA.get(SceneManager.LeScene.VIDEO).esponiLaScena();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        button.setText("paraben");
    }    
}
