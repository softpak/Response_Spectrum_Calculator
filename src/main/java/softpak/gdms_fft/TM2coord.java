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
public class TM2coord implements Serializable {
    public final double x_TM2, y_TM2;
        
    public TM2coord(double x_TM2, double y_TM2) {
        this.x_TM2 = x_TM2;
        this.y_TM2 = y_TM2;
    }
}
