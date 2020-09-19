/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.Utils;

/**
 *
 * @author you
 */
public class Seismic implements Serializable {
    private Date StartTime;
    private Number Longitude;
    private Number Latitude;
    private Number depth_of_focus;      //km
    private Number magnitude;           //ML
    private Number data_num;
    private Number predict_Vs30;
    private String nearest_active_fault_name;//km
    private Number nearest_active_fault;//km
    private WGS84coord nearest_active_fault_coord;
    private String SSID;
    
    
    //日期 震央經度 震央緯度 深度 芮氏規模 自由場資料筆數
    public Seismic() {
        
    }
    
    public Seismic(Date StartTime, Number Longitude, Number Latitude,Number depth_of_focus, Number magnitude, Number data_num) {
        this.StartTime = StartTime;
        this.Longitude = Longitude;
        this.Latitude = Latitude;
        this.depth_of_focus = depth_of_focus;       //km
        this.magnitude = magnitude;                 //ML
        this.data_num = data_num;
    }
    
    public void setNearestActiveFault_coord(WGS84coord nearest_active_fault_coord) {
        this.nearest_active_fault_coord = nearest_active_fault_coord;
    }
    
    public void setNearestActiveFault_dist(Number dist) {
        this.nearest_active_fault = dist;
    }
    
    public void setNearestActiveFault(String fault_name) {
        this.nearest_active_fault_name = fault_name;
    }
    
    public void setPredictVs30(Number Vs30) {
        this.predict_Vs30 = Vs30;
    }
    
    public void setStartTime(Date StartTime) {
        this.StartTime = StartTime;
    }
    
    public void setLongitude(Number Longitude) {
        this.Longitude = Longitude;
    }
    
    public void setLatitude(Number Latitude) {
        this.Latitude = Latitude;
    }
    
    public void setDepth_Of_Focus(Number depth_of_focus) {
        this.depth_of_focus = depth_of_focus;
    }
    
    public void setMagnitude(Number magnitude) {
        this.magnitude = magnitude;
    }
    
    public void setData_Num(Number data_num) {
        this.data_num = data_num;
    }
    
    //------------------------------------------------------------
    
    public WGS84coord getNearestActiveFault_coord() {
        return this.nearest_active_fault_coord;
    }
    
    public Number getNearestActiveFault_dist() {
        return this.nearest_active_fault;
    }
    
    public String getNearestActiveFault() {
        return this.nearest_active_fault_name;
    }
    
    public Number getPredictVs30() {
        return this.predict_Vs30;
    }
    
    public Date getStartTime() {
        return this.StartTime;
    }
    
    public Number getLongitude() {
        return this.Longitude;
    }
    
    public Number getLatitude() {
        return this.Latitude;
    }
    
    public Number getDepth_Of_Focus() {
        return this.depth_of_focus;
    }
    
    public Number getMagnitude() {
        return this.magnitude;
    }
    
    public Number getData_Num() {
        return this.data_num;
    }
    
    public void setSSID(String str) {
        SSID = str;
    }
    
    public String getSSID() {
        return this.SSID;
    }
   
    public void calc_SSID() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashbytes = digest.digest((StartTime.toString()+Longitude.toString()+Latitude.toString()+depth_of_focus.toString()+magnitude.toString()).getBytes(StandardCharsets.UTF_8));
            //SSID = bytesToHex(hashbytes);
            setSSID(bytesToHex(hashbytes));
            Utils.add_ssid_to_ssidqueue(this.SSID);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Seismic.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(SSID);
    }
    
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
