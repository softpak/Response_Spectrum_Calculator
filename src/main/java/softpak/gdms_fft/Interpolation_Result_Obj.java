/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author you
 */
public class Interpolation_Result_Obj {
    String Station_Name;
    double current_value;
    double predict_value;
    double error_value;
    double error_percent;
    List<String> ref_station = Lists.newLinkedList();
    
    public Interpolation_Result_Obj() {
        
    }
    
    public void set_StationName(String Station_Name) {
        this.Station_Name = Station_Name;
    }
    
    public void set_CurrentValue(double current_value) {
        this.current_value = current_value;
    }
    
    public void set_PredictValue(double predict_value) {
        this.predict_value = predict_value;
    }
    
    public void set_ErrorValue(double error_value) {
        this.error_value = error_value;
    }
    
    public void set_ErrorPercent(double error_percent) {
        this.error_percent = error_percent;
    }
    
    //===========================================================
    
    public String get_StationName() {
        return this.Station_Name;
    }
    
    public double get_CurrentValue() {
        return this.current_value;
    }
    
    public double get_PredictValue() {
        return this.predict_value;
    }
    
    public double get_ErrorValue() {
        return this.error_value;
    }
    
    public double get_ErrorPercent() {
        return this.error_percent;
    }
    
    public List<String> get_RefStationList() {
        return this.ref_station;
    }
}
