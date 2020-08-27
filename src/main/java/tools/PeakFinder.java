/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author you
 */
public class PeakFinder {
    double[] x_axis, y_axis;
    int font_pos, mid_pos, rear_pos;
    List<Integer> peak_pos = Lists.newLinkedList();
    
    public PeakFinder() {
        
    }
    
    public PeakFinder(double[] x_axis, double[] y_axis) {
        this.x_axis = x_axis;
        this.y_axis = y_axis;
    }
    
    public void set_Data(double[] x_axis, double[] y_axis) {
        this.x_axis = x_axis;
        this.y_axis = y_axis;
    }
    
    /*
    0.1/0.004 = 25 pos
    0.1/0.005 = 20 pos
    3/0.004 = 750
    3/0.005 = 600
    
    25~750
    20~600
    
    */
    public void calc_peak_pos() {
        if (peak_pos.size() > 0) {
            peak_pos.clear();
        }
        for (int cx = 1; cx < this.x_axis.length-1; cx++) {
            if (this.y_axis[cx] > this.y_axis[cx-1] && this.y_axis[cx] > this.y_axis[cx+1]) {
                peak_pos.add(cx);
            }
        }
    }
    
    public List<Integer> get_peak_pos() {
        return peak_pos;
    }
    
    
}
