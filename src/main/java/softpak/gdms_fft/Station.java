/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author you
 */
public class Station implements Serializable {
    private final SimpleStringProperty StationName;
    private final SimpleStringProperty SubStationName;
    private final SimpleStringProperty Vs30;
    private final SimpleStringProperty Ground_Level;
    private final SimpleStringProperty Z10;
    private final SimpleStringProperty Kappa;
    private final SimpleStringProperty Longitude;
    private final SimpleStringProperty Latitude;
    private int count;
    private SimpleStringProperty nearest_active_fault_name;//km
    private SimpleStringProperty nearest_active_fault;//km
    private SimpleStringProperty nearest_active_fault_coord;
    
    
    public Station(String staion_name, String substaion_name, String vs30, String ground_level, String z10, String kappa, String longitude, String latitude, String name, String dist, String coord) {
        this.StationName = new SimpleStringProperty(staion_name);
        this.SubStationName = new SimpleStringProperty(substaion_name);
        this.Vs30 = new SimpleStringProperty(vs30);
        this.Ground_Level = new SimpleStringProperty(ground_level);
        this.Z10 = new SimpleStringProperty(z10);
        this.Kappa = new SimpleStringProperty(kappa);
        this.Longitude = new SimpleStringProperty(longitude);
        this.Latitude = new SimpleStringProperty(latitude);
        this.nearest_active_fault_name = new SimpleStringProperty(name);
        this.nearest_active_fault = new SimpleStringProperty(dist);
        this.nearest_active_fault_coord = new SimpleStringProperty(coord);
    }
    
    public SimpleStringProperty getNearestActiveFault_coord() {
        return this.nearest_active_fault_coord;
    }
    
    public SimpleStringProperty getNearestActiveFault_dist() {
        return this.nearest_active_fault;
    }
    
    public SimpleStringProperty getNearestActiveFault() {
        return this.nearest_active_fault_name;
    }
    
    public int getStationCount() {
        return count;
    }
    
    public void addStationCount() {
        count++;
    }
    
    public String getStationName() {
        return StationName.get();
    }
    
    
    public void setNearestActiveFault_coord(SimpleStringProperty nearest_active_fault_coord) {
        this.nearest_active_fault_coord = nearest_active_fault_coord;
    }
    
    public void setNearestActiveFault_dist(SimpleStringProperty dist) {
        this.nearest_active_fault = dist;
    }
    
    public void setNearestActiveFault(SimpleStringProperty fault_name) {
        this.nearest_active_fault_name = fault_name;
    }

    public void setStationName(String staion_name) {
        StationName.set(staion_name);
    }
    
    public String getSubStationName() {
        return SubStationName.get();
    }

    public void setSubStationName(String substaion_name) {
        SubStationName.set(substaion_name);
    }

    public String getVs30() {
        return Vs30.get();
    }

    public void setVs30(String vs30) {
        Vs30.set(vs30);
    }

    public String getGround_Level() {
        return Ground_Level.get();
    }

    public void setGround_Level(String ground_level) {
        Ground_Level.set(ground_level);
    }
    
    public String getZ10() {
        return Z10.get();
    }

    public void setZ10(String z10) {
        Z10.set(z10);
    }
    
    public String getKappa() {
        return Kappa.get();
    }

    public void setKappa(String kappa) {
        Kappa.set(kappa);
    }
    
    public String getLongitude() {
        return Longitude.get();
    }

    public void setLongitude(String longitude) {
        Longitude.set(longitude);
    }
    
    public String getLatitude() {
        return Latitude.get();
    }

    public void setLatitude(String latitude) {
        Latitude.set(latitude);
    }
    
}
