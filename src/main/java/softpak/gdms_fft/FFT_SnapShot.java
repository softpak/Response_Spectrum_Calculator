/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import tools.Configs;

/**
 *
 * @author you
 */
public class FFT_SnapShot implements Serializable {
    private String SSID;
    private String StationCode;
    private String InstrumentKind;
    private Date StartTime;
    private String file_path;
    private String file_name;
    private Number PGA_U;
    private Number PGA_N;
    private Number PGA_E;
    private Number epicenter_distance;  //km
    private Number magnitude;           //ML
    private Number depth_of_focus;      //km
    private Number Vs30;
    private Number GL;
    private Number Z10;
    private Number K;
    private Number station_Longitude;
    private Number station_Latitude;
    private Number seismic_Longitude;
    private Number seismic_Latitude;
    private Number Max_U_Hz;
    private Number Max_N_Hz;
    private Number Max_E_Hz;
    private Number Max_U_Hz_20;
    private Number Max_N_Hz_20;
    private Number Max_E_Hz_20;
    
    /*
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_station;
    @FXML private TreeTableColumn<FFT_SnapShot, Date> ttc_starttime;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_magnitude;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_U20;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_N20;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_E20;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_dof;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_vs30;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_gl;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_z10;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_k;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_ed;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_long;
    @FXML private TreeTableColumn<FFT_SnapShot, Number> ttc_lat;
    */
    
    
    DecimalFormat hertz_format = new DecimalFormat("#.##");
    private Boolean selected = false;
    
    FFT_SnapShot() {
        
    }
    
    public void set_selected(Boolean bool) {
        selected = bool;
    }
    
    public Boolean is_selected() {
        return selected;
    }
    
    public FFT_SnapShot(String SSID, String StationCode, String InstrumentKind, Date StartTime, Number Max_U_Hz, Number Max_N_Hz, Number Max_E_Hz, Number Max_U_Hz_20, Number Max_N_Hz_20, Number Max_E_Hz_20, Number PGA_U, Number PGA_N, Number PGA_E) {
        this.SSID = SSID;
        this.StartTime = StartTime;
        this.StationCode = StationCode;
        this.InstrumentKind = InstrumentKind;
        
        this.Max_U_Hz = Max_U_Hz;
        this.Max_N_Hz = Max_N_Hz;
        this.Max_E_Hz = Max_E_Hz;
        this.Max_U_Hz_20 = Max_U_Hz_20;
        this.Max_N_Hz_20 = Max_N_Hz_20;
        this.Max_E_Hz_20 = Max_E_Hz_20;
        this.PGA_U = PGA_U;
        this.PGA_N = PGA_N;
        this.PGA_E = PGA_E;
    }

    
    /*
    #StationCode: ILA005
    #InstrumentKind: CV574(06106600.CVA)
    #StartTime: 2017/03/07-22:11:14.000 
    #RecordLength(sec):   51.000
    #SampleRate(Hz): 200
    #AmplitudeUnit:  gal. DCoffset(corr)
    #AmplitudeMAX. U:    4.094~   -4.353
    #AmplitudeMAX. N:    7.984~   -8.446
    #AmplitudeMAX. E:    8.373~   -9.942
    #DataSequence: Time U(+); N(+); E(+)
    #Data: 4F10.3 
    */
    public void setMax_U_Hz(Number num) {
        Max_U_Hz = num;
    }
    
    public void setMax_N_Hz(Number num) {
        Max_N_Hz = num;
    }
    
    public void setMax_E_Hz(Number num) {
        Max_E_Hz = num;
    }
    
    public void setMax_U_Hz_20(Number num) {
        Max_U_Hz_20 = num;
    }
    
    public void setMax_N_Hz_20(Number num) {
        Max_N_Hz_20 = num;
    }
    
    public void setMax_E_Hz_20(Number num) {
        Max_E_Hz_20 = num;
    }
    
    
    public void setSSID(String str) {
        SSID = str;
    }
    
    public void setEpicenterDistance(double dist) {
        epicenter_distance = dist;
    }
    
    public void setMagnitude(double mag) {
        magnitude = mag;
    }
    
    public void setDepthOfFocus(double dep) {
        depth_of_focus = dep;
    }
    
    public void setStationCode(String str) {
        StationCode = str;
    }
    
    public void setInstrumentKind(String str) {
        InstrumentKind = str;
    }
    
    public void setStartTime(Date date) {
        StartTime = date;
    }
    
    public void setVs30(double vs30) {
        Vs30 = vs30;
    }
    
    public void setGL(double gl) {
        GL = gl;
    }
    
    public void setZ10(double z10) {
        Z10 = z10;
    }
    
    public void setK(double k) {
        K = k;
    }
    
    public void setPGA_U(double u) {
        PGA_U = u;
    }
    
    public void setPGA_N(double n) {
        PGA_N = n;
    }
    
    public void setPGA_E(double e) {
        PGA_E = e;
    }
    
    public void setStationLongitude(double longitude) {
        station_Longitude = longitude;
    }
    
    public void setStationLatitude(double latitude) {
        station_Latitude = latitude;
    }
    
    public void setSeismicLongitude(double longitude) {
        seismic_Longitude = longitude;
    }
    
    public void setSeismicLatitude(double latitude) {
        seismic_Latitude = latitude;
    }
    
    
    
    
    //==========================================================================
    public String getSSID() {
        return SSID;
    }
    
    public Number getEpicenterDistance() {
        return epicenter_distance;
    }
    
    public Number getMagnitude() {
        return magnitude;
    }
    
    public Number getDepthOfFocus() {
        return depth_of_focus;
    }
   
    public String getStationCode() {
        return StationCode;
    }
    
    public String getInstrumentKind() {
        return InstrumentKind;
    }
    
    public Date getStartTime() {
        return StartTime;
    }
    
    public Number getMax_U_Hz() {
        return Max_U_Hz;
    }
    
    public Number getMax_N_Hz() {
        return Max_N_Hz;
    }
    
    public Number getMax_E_Hz() {
        return Max_E_Hz;
    }
    
    public Number getMax_U_Hz_20() {
        return Max_U_Hz_20;
    }
    
    public Number getMax_N_Hz_20() {
        return Max_N_Hz_20;
    }
    
    public Number getMax_E_Hz_20() {
        return Max_E_Hz_20;
    }
    
    public Number getVs30() {
        return Vs30;
    }
    
    public Number getGL() {
        return GL;
    }
    
    public Number getZ10() {
        return Z10;
    }
    
    public Number getK() {
        return K;
    }
    
    public Number getPGA_U() {
        return PGA_U;
    }
    
    public Number getPGA_N() {
        return PGA_N;
    }
    
    public Number getPGA_E() {
        return PGA_E;
    }
    
    
    public Number getStationLongitude() {
        return station_Longitude;
    }
    
    public Number getStationLatitude() {
        return station_Latitude;
    }
    
    public Number getSeismicLongitude() {
        return seismic_Longitude;
    }
    
    public Number getSeismicLatitude() {
        return seismic_Latitude;
    }
    
    public String getFilePath() {
        return this.file_path;
    }
    
    public String getFileName() {
        return this.file_name;
    }
        
    public void setFilePath(String str) {
        file_path = str;
    }
    
    public void setFileName(String str) {
        file_name = str;
    }
    /*
    public double calc_distance(double lon1, double lat1, double lon2, double lat2, double gl1, double gl2, String unit) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = Configs.R * c * 1000; // convert to meters
        double height = gl1 - gl2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        unit = unit.toUpperCase();
        if (unit.equals("KM")) {
            distance = Math.sqrt(distance)/1000D;
        } else {//Meter
            distance = Math.sqrt(distance);
        }
        return distance;
    }*/
}
