/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package charts;

import java.util.List;

/**
 *
 * @author you
 */
public class Scatter {
    List xData;
    List yData;
    List errorBars;
    String fn;
    
    public Scatter() {
        
    }
    
    public Scatter(List xData, List yData, List errorBars) {
        this.xData = xData;
        this.yData = yData;
        this.errorBars = errorBars;
    }
    
    public Scatter(List xData, List yData) {
        this.xData = xData;
        this.yData = yData;
    }
    
    public void export_with_errors() {
        if (errorBars == null) {
            export();
        } else {
            
        }
    }
    
    public void export() {
        
        
    }
    
    public void set_xData(List xData) {
        this.xData = xData;    
    }
    
    public void set_yData(List yData) {
        this.yData = yData;
    }
    
    public void set_errorBars(List errorBars) {
        this.errorBars = errorBars;
    }
    
    public void set_filename(String fn) {
        this.fn = fn;
    }
        
}
