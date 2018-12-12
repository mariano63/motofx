package org.ma.motofx.tables;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author maria
 */
public class Clients {
    
    private final StringProperty colIp;
    public StringProperty colIpProperty() {
        return this.colIp;
    }
    public String getColIp() {
        return colIp.get();
    }
    public void setcolIp(String value) {
        colIp.set(value);
    }
    
    private final StringProperty colMac;
    public StringProperty colMacProperty() {
        return this.colMac;
    }
    public String getcolMac() {
        return colMac.get();
    }
    public void setcolMac(String value) {
        colMac.set(value);
    }
    
    private final StringProperty colName;
    public StringProperty colNameProperty() {
        return this.colName;
    }
    public String getcolName() {
        return colName.get();
    }
    public void setcolName(String value) {
        colName.set(value);
    }
    
    private final StringProperty colBike;
    public StringProperty colBikeProperty() {
        return this.colBike;
    }
    public String getcolBike() {
        return colBike.get();
    }
    public void setcolBike(String value) {
        colBike.set(value);
    }
    private final StringProperty colActivity;
    public StringProperty colActivityProperty() {
        return this.colActivity;
    }
    public String getcolActivity() {
        return colActivity.get();
    }
    public void setcolActivity(String value) {
        colActivity.set(value);
    }
    
    
    public Clients(String colIp,String colMac, String colName, String colBike, String colActivity) {
        this.colIp = new SimpleStringProperty(colIp);
        this.colMac = new SimpleStringProperty(colMac);
        this.colName = new SimpleStringProperty(colName);
        this.colBike = new SimpleStringProperty(colBike);
        this.colActivity = new SimpleStringProperty(colActivity);
    }
    
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s",colIp.get(),colMac.get(),colName.get(),colBike.get(),colActivity.get());
    }
}
