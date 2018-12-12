
package org.ma.motofx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import org.ma.motofx.tables.Clients;

/**
 * FXML Controller class
 *
 * @author maria
 */
public class FXMLServerController implements Initializable {

    @FXML
    public Button butClose;
    @FXML
    private ToggleButton butStartServer;
    @FXML
    private TableColumn<Clients, String> colIp;
    @FXML
    private TableColumn<Clients, String> colMac;
    @FXML
    private TableColumn<Clients, String> colName;
    @FXML
    private TableColumn<Clients, String> colBike;
    @FXML
    private TableView<Clients> tableClients;
    
    private final static ObservableList<Clients> TABCLIENTS = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableClients.setItems(TABCLIENTS);
        colIp.setCellValueFactory(cellData -> cellData.getValue().colIpProperty());
        colMac.setCellValueFactory(cellData -> cellData.getValue().colMacProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().colNameProperty());
        colBike.setCellValueFactory(cellData -> cellData.getValue().colBikeProperty());
    }

    public void onActionButClose(ActionEvent actionEvent) {
        StageManager.showStage(EStage.SETUP);
    }
}
