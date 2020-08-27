/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.grum.geocalc.Coordinate;

/**
 *
 * @author you
 */
public class AnalysisStep {
    private com.grum.geocalc.Point geocalc_point;//lat lon
    private com.esri.arcgisruntime.geometry.Point arcgis_point;//lon lat
    private int stepcount;
    private double krigin_val;
    
    
    public AnalysisStep(int stepcount, com.esri.arcgisruntime.geometry.Point arcgis_point, double krigin_val) {
        this.stepcount = stepcount;
        this.arcgis_point = arcgis_point;
        Coordinate lon = Coordinate.fromDegrees(arcgis_point.getX());
        Coordinate lat = Coordinate.fromDegrees(arcgis_point.getY());
        this.geocalc_point = com.grum.geocalc.Point.at(lat, lon);
        this.krigin_val = krigin_val;
    }
    
    public AnalysisStep(int stepcount, com.grum.geocalc.Point geocalc_point, double krigin_val) {
        this.stepcount = stepcount;
        this.geocalc_point = geocalc_point;
        this.arcgis_point = new com.esri.arcgisruntime.geometry.Point(geocalc_point.longitude, geocalc_point.latitude, SpatialReferences.getWgs84());
        this.krigin_val = krigin_val;
    }
    
    public int get_stepcount() {
        return this.stepcount;
    }
    
    public double get_krigin_val() {
        return this.krigin_val;
    }
    
    public com.esri.arcgisruntime.geometry.Point get_arcgis_point() {
        return this.arcgis_point;
    }
    
    public com.grum.geocalc.Point get_geocalc_point() {
        return this.geocalc_point;
    }
}
