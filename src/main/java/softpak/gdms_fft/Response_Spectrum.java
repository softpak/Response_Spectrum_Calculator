/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * @author you
 */
public class Response_Spectrum implements Serializable{
    //time, damper ratio, sample ratio, T
    private String SSID;
    private BigDecimal RecordLength;//sec
    private BigDecimal SampleRate;//Hz
    private String StationCode;
    private String InstrumentKind;
    private String Accelerometer_StartTime;
    private String Seismic_StartTime;
    private String file_path;
    private String file_name;
    
    //private String[] hertz;
    private double epicenter_distance;  //km
    private double magnitude;           //ML
    private double depth_of_focus;      //km
    private double pga_u;
    private double pga_n;
    private double pga_e;
    
    private int U_max_pos = 0;
    private int N_max_pos = 0;
    private int E_max_pos = 0;
    private int U_max_20_pos = 0;
    private int N_max_20_pos = 0;
    private int E_max_20_pos = 0;
    DecimalFormat hertz_format = new DecimalFormat("#.##");
    
    public Response_Spectrum() {
        
    }
    
    
}
