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
public class Site {
    private double lat, lon, vs30;
    private int site_class;
        
    public Site(double lat, double lon, double vs30) {
        this.lat = lat;
        this.lon = lon;
        this.vs30 = vs30;
        set_site_calss(vs30);
    }
    
    /*
    class1:Vs30>=270
    class2:180<=Vs30<270
    class3:Vs30<180
    */
    
    private void set_site_calss(double vs30) {
        if (vs30 < 180D) {
            site_class = 3;
        } else if (vs30 >= 180D && vs30 < 270D) {
            site_class = 2;
        } else if ( vs30 >= 270D) {
            site_class = 1;
        }
        //System.out.println(site_class);
    }
    
    public double get_lat() {
        return this.lat;
    }
    
    public double get_lon() {
        return this.lon;
    }
    
    public double get_vs30() {
        return this.vs30;
    }
    
    public int get_site_class() {
        return this.site_class;
    }
    
}
