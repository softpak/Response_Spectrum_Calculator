/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.Serializable;

/**
 *
 * @author you
 */
public class WGS84coord implements Serializable {
    public final double lon_WGS, lat_WGS;
        
    public WGS84coord(double lon_WGS, double lat_WGS) {
        this.lon_WGS = lon_WGS;
        this.lat_WGS = lat_WGS;
    }
}
