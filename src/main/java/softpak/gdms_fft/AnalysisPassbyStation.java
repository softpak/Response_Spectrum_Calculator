/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

/**
 *
 * @author you
 */
public class AnalysisPassbyStation {
    private FFT_SnapShot passby_station;//lat lon
    
    public AnalysisPassbyStation() {
        
    }
    
    public AnalysisPassbyStation(FFT_SnapShot passby_station) {
        this.passby_station = passby_station;
    }
    
    public FFT_SnapShot get_fft_data() {
        return this.passby_station;
    }
    
    public void ser_fft_data(FFT_SnapShot passby_station) {
        this.passby_station = passby_station;
    }
            
}
