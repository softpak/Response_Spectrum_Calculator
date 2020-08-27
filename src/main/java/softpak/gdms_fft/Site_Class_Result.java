/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import javafx.stage.Stage;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import tools.Configs;
import tools.Kriging;
import tools.Utils;

/**
 *
 * @author you
 */
public class Site_Class_Result implements Serializable {
    /*
    class1:Vs30>=270
    class2:180<=Vs30<270
    class3:Vs30<180
    */
    private List<Site> site_list = Lists.newLinkedList();
    private List<org.locationtech.jts.geom.Coordinate> shape_list = Lists.newLinkedList();
    double step_coord_unit;
    double min_lat, min_lon, max_lat, max_lon;
    
    public Site_Class_Result() {
        
    }
    double step_dist = 0.01D;//corrds
    //0.01D = 1km
    
    private void calc_min_step() {
        try {
            //load shp file
            File file = new File("H:\\論文工具\\GDMS_FFT\\gadm36_TWN_shp\\gadm36_TWN_0.shp");
            Map<String, Object> shp_map = new HashMap<>();
            shp_map.put("url", file.toURI().toURL());
            
            DataStore dataStore = DataStoreFinder.getDataStore(shp_map);
            String typeName = dataStore.getTypeNames()[0];
            
            FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
            Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
            PointCollection points = new PointCollection(SpatialReferences.getWgs84());
            
            Polygon outline_ps = new Polygon();//shape
            
            org.geotools.feature.FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
            try (FeatureIterator<SimpleFeature> features = collection.features()) {
                while (features.hasNext()) {
                    SimpleFeature feature = features.next();
                    org.locationtech.jts.geom.Geometry g = (org.locationtech.jts.geom.Geometry)feature.getDefaultGeometry();
                    
                    org.locationtech.jts.geom.Coordinate[] coords = g.getCoordinates();
                    for (int c = 0;c < coords.length; c++) {
                        //System.out.println(coords[c].x+", "+ coords[c].y);
                        points.add(coords[c].x, coords[c].y);
                        shape_list.add(coords[c]);
                        outline_ps.addPoint((int)(coords[c].x*10000D), (int)(coords[c].y*10000D));
                    }
                    //System.out.println(coords.length);                
                    /*
                    System.out.println(feature.getAttributes().get(0));
                    
                    
                    System.out.print(feature.getID());
                    System.out.print(": ");
                    System.out.println(feature.getDefaultGeometryProperty().getValue());*/
                    //GeometryAttribute sourceGeometry = feature.getDefaultGeometryProperty();
                    
                }
            }
            //List<Station> st_list = Utils.get_StationList();
            Collections.sort(shape_list, new Comparator<org.locationtech.jts.geom.Coordinate>() {
                @Override
                public int compare(org.locationtech.jts.geom.Coordinate st1, org.locationtech.jts.geom.Coordinate st2) {
                    if (st1.x > st2.x)
                        return 1;
                    if (st1.x < st2.x)
                        return -1;//min to foward
                    return 0;
                }
            });
            min_lon = shape_list.get(0).x;
            max_lon = shape_list.get(shape_list.size()-1).x;
            
            Collections.sort(shape_list, new Comparator<org.locationtech.jts.geom.Coordinate>() {
                @Override
                public int compare(org.locationtech.jts.geom.Coordinate st1, org.locationtech.jts.geom.Coordinate st2) {
                    if (st1.y > st2.y)
                        return 1;
                    if (st1.y < st2.y)
                        return -1;//min to foward
                    return 0;
                }
            });
            min_lat = shape_list.get(0).y;
            max_lat = shape_list.get(shape_list.size()-1).y;
            /*
            int[] lon_arr = new int[shape_list.size()-1];
            int[] lat_arr = new int[shape_list.size()-1];
            for (int gc = 0; gc < shape_list.size()-1; gc++) {
                lon_arr[gc] = Math.abs((int)(shape_list.get(gc+1).x*10000D)-(int)(shape_list.get(gc).x*10000D));
                lat_arr[gc] = Math.abs((int)(shape_list.get(gc+1).y*10000D)-(int)(shape_list.get(gc).y*10000D));
            }
            
            System.out.println(findGCD(lon_arr));
            System.out.println(findGCD(lat_arr));*/
            
            
            //
            BigDecimal temp_bd = new BigDecimal(min_lat);
            temp_bd = temp_bd.setScale(4, RoundingMode.HALF_UP);// 小數後面四位, 四捨五入   
            min_lat = temp_bd.doubleValue();
            //
            temp_bd = new BigDecimal(max_lat);
            temp_bd = temp_bd.setScale(4, RoundingMode.HALF_UP);// 小數後面四位, 四捨五入   
            max_lat = temp_bd.doubleValue();
            //
            temp_bd = new BigDecimal(min_lon);
            temp_bd = temp_bd.setScale(4, RoundingMode.HALF_UP);// 小數後面四位, 四捨五入   
            min_lon = temp_bd.doubleValue();
            //
            temp_bd = new BigDecimal(max_lon);
            temp_bd = temp_bd.setScale(4, RoundingMode.HALF_UP);// 小數後面四位, 四捨五入   
            max_lon = temp_bd.doubleValue();
            //21.89652824, 25.30041695, 120.03180695, 122.00624847
            //21.8965, 25.3004, 120.0318, 122.0062
            System.out.println(min_lat+", "+max_lat+", "+min_lon+", "+max_lon);
            int lon_step = (int)((max_lon - min_lon)/step_dist);
            int lat_step = (int)((max_lat - min_lat)/step_dist);
            
            //2468 4255
            //19744, 34040
            System.out.println(lon_step+", "+lat_step);
            double start_lon = min_lon;
            
            Queue<org.locationtech.jts.geom.Coordinate> in_area_coords = Queues.newConcurrentLinkedQueue();
            
            int count = 0;
            for (int lonc = 0; lonc < lon_step; lonc++) {
                start_lon += step_dist;
                double start_lat = min_lat;
                for (int latc = 0; latc < lat_step; latc++) {
                    start_lat += step_dist;
                    if (outline_ps.contains(start_lon*10000D, start_lat*10000D)) {
                        in_area_coords.add(new org.locationtech.jts.geom.Coordinate(start_lon, start_lat));
                        //count++;
                    }
                }
            }
            System.out.println(in_area_coords.size());
            //System.out.println(count);
            
            
            
            
            
            FX_2D_Map_Window_Site_Class site = new FX_2D_Map_Window_Site_Class(in_area_coords, step_dist);
            try {
                site.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(Site_Class_Result.class.getName()).log(Level.SEVERE, null, ex);
            }
            site.setTitle("2D Map - Site Class");
            
        }   catch (MalformedURLException ex) {
            Logger.getLogger(Site_Class_Result.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Site_Class_Result.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public int gcd(int a, int b) { 
        if (a == 0) 
            return b; 
        return gcd(b % a, a); 
    } 
  
    // Function to find gcd of array of 
    // numbers 
    public int findGCD(int arr[]) { 
        int n = arr.length;
        int result = arr[0]; 
        for (int i = 1; i < n; i++){ 
            result = gcd(arr[i], result); 
  
            if(result == 1) 
            { 
               return 1; 
            } 
        } 
  
        return result; 
    }
    
    public void calc_vs30_for_every_site() {
        calc_min_step();
        
    }
    
    
    public void add_site(Site site) {
        site_list.add(site);
    }
    
    public List<Site> get_siteList() {
        return this.site_list;
    }
    
    
}
