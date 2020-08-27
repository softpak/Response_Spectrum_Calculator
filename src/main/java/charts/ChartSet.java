/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package charts;

import java.util.List;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 *
 * @author you
 */
public class ChartSet {
    List xData;
    List yData;
    String fn;
    XYChart chart = new XYChartBuilder().width(1920).height(1080).theme(ChartTheme.Matlab).xAxisTitle("X").yAxisTitle("Y").build();
    //1 sec 20 pixels
    
    
    public ChartSet() {
        
    }
    
    public ChartSet(List xData, List yData) {
        this.xData = xData;
        this.yData = yData;
    }
    
    public void export() {
        // Customize Chart
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setXAxisTickMarkSpacingHint(100);
        chart.setTitle("");
        
    }
    
    public void set_xData(List xData) {
        this.xData = xData;    
    }
    
    public void set_yData(List yData) {
        this.yData = yData;
    }
   
    public void set_filename(String fn) {
        this.fn = fn;
    }
        
}
