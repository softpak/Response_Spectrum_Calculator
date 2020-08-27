/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author you
 */
public class Active_Fault implements Serializable {
    //
    String fault_name;
    List<WGS84coord> point_list = Lists.newLinkedList();
    double center_x, center_y;
    String file_path;
    
    public Active_Fault(String fault_name, List<WGS84coord> point_list, double center_x, double center_y, String file_path) {
        this.fault_name = fault_name;
        this.point_list = point_list;
        this.center_x = center_x;
        this.center_y = center_y;
        this.file_path = file_path;
    }
    
    public String getFaultName() {
        return this.fault_name;
    }
    
    public List<WGS84coord> getPointList() {
        return this.point_list;
    }
    
    public double getCenterX() {
        return this.center_x;
    }
    
    public double getCenterY() {
        return this.center_y;
    }
    
    public String getFilePath() {
        return this.file_path;
    }
    
}
