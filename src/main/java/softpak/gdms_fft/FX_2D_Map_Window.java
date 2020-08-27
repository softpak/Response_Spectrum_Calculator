/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.json.JSONObject;
import tools.Configs;
import tools.Kriging;
import tools.Utils;

/**
 *
 * @author you
 */
public class FX_2D_Map_Window extends Application {
    private static FX_Main_Controller mainController;
    ArcGISMap map;
    Stage stage;
    private MapView mapView;
    
    private int hexRed = 0xFFFF0000;
    private int hexGreen = 0xFF00FF00;
    private int hexBlue = 0xFF0000FF;
    private int hexOrange = 0xFFFF800A;
    private int hexF70082 = 0xFFF70082;
    private int hex88E909 = 0xFF88E909;
    private int hex8A0BAA = 0xFF8A0BAA;
    SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
    
    Map<String, Map<String, FFT_SnapShot>> select_map;
    private Map<Date, Seismic> analysis_map_seismic_event = Maps.newConcurrentMap();
    private Map<String, Station> analysis_map_station = Maps.newConcurrentMap();
    private Map<String, Map<String, FFT_SnapShot>> analysis_map_fftsnp = Maps.newConcurrentMap();
    private Map<String, Map<Integer, AnalysisStep>> analysis_map_step = Maps.newConcurrentMap();//station, step count, analysis data
    private Map<String, AnalysisPassbyStation> analysis_map_passbystation = Maps.newConcurrentMap();//step count, analysis pass by station
    private List<String> station_text = Lists.newArrayList();
    private Queue<Seismic> sc_queue = Queues.newConcurrentLinkedQueue();
    private Kriging kr;
    private GraphicsOverlay graphicsOverlay;
    private GraphicsOverlay graphicsOverlay_line;
    private GraphicsOverlay graphicsOverlay_text;
    
    public FX_2D_Map_Window(Map<String, Map<String, FFT_SnapShot>> select_map) {
        this.select_map = select_map;
    }
    
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        mainController = MainApp.loader.getController();
        this.stage = stage;
        stage.setWidth(800);
        stage.setHeight(600);
        if (Configs.temp_kriging.get("kriging") != null) {
            kr = Configs.temp_kriging.get("kriging");
        } else {
            //select kriging file or generate intime
            //1.show a vbox
            
            
        }
        //create a export button
        Button export_Button = new Button("Export (xls)");
        Button sub_map_Button = new Button("Generate Sub Map");
            
        
        
        export_Button.setOnAction(event -> {
            //calc kriging
            
            //set data in to collections
            
            
            //send op
        });
        
        sub_map_Button.setOnAction(event -> {
            //get near by seicmic around 5km default
            this.select_map.entrySet().forEach(stn -> {
                StringBuilder sub_stn_listed = new StringBuilder();
                sc_queue.forEach(sc_ref -> {
                    GeodeticCalculator geoCalc = new GeodeticCalculator();
                    //Coordinate ref_lon_temp = Coordinate.fromDegrees(sc_ref.getLongitude().doubleValue());
                    //Coordinate ref_lat_temp = Coordinate.fromDegrees(sc_ref.getLatitude().doubleValue());
                    //com.grum.geocalc.Point o_sc_p = com.grum.geocalc.Point.at(ref_lat_temp, ref_lon_temp);//seismic
                    GlobalCoordinates o_sc_p = new GlobalCoordinates(sc_ref.getLatitude().doubleValue(), sc_ref.getLongitude().doubleValue());
                    Utils.get_seismic_map().entrySet().forEach(sc -> {
                        sub_count = 0;
                        //Coordinate sc_lon_temp = Coordinate.fromDegrees(sc.getValue().getLongitude().doubleValue());
                        //Coordinate sc_lat_temp = Coordinate.fromDegrees(sc.getValue().getLatitude().doubleValue());
                        Map<String, Map<String, FFT_SnapShot>> temp_selected_snapshot_map = Maps.newConcurrentMap();
                        GlobalCoordinates n_sc_p = new GlobalCoordinates(sc.getValue().getLatitude().doubleValue(), sc.getValue().getLongitude().doubleValue());
                        //com.grum.geocalc.Point n_sc_p = com.grum.geocalc.Point.at(sc_lat_temp, sc_lon_temp);//station
                        GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, n_sc_p, o_sc_p);
                        if (geoCurve.getEllipsoidalDistance() <= Configs.nearby_staiotn_distance) {
                                //Map<String, Map<String, FFT_SnapShot>> temp_selected_snapshot_map = Maps.newConcurrentMap();
                                sub_stn_listed.delete(0, sub_stn_listed.length());
                                //meter
                                //sc_queue.add(sc.getValue());

                                //find the station
                                Utils.get_fft_snp_map().get(stn.getKey()).entrySet().forEach(fft -> {
                                    if (fft.getValue().getStartTime().equals(sc.getValue().getStartTime())) {
                                        stn.getValue().entrySet().forEach(stn_temp -> {
                                            if (!stn_temp.getValue().getStartTime().equals(sc.getValue().getStartTime())) {
                                                Map<String, FFT_SnapShot> temp_fft = Maps.newConcurrentMap();
                                                temp_fft.put(fft.getValue().getSSID(), fft.getValue());
                                                temp_selected_snapshot_map.put(stn.getKey(), temp_fft);
                                                sub_count++;
                                            }
                                        });
                                    }
                                });



                                
                            

                        }
                        //open new 2D map
                        if (sub_count > 0) {
                            try {
                                FX_2D_Map_Window sub_ff = new FX_2D_Map_Window(temp_selected_snapshot_map);
                                sub_ff.start(new Stage());
                                sub_stn_listed.append("Near by Seicmics: ")
                                        .append(format.format(sc.getValue().getStartTime())).append(" ")
                                        .append(Configs.df.format(sc.getValue().getMagnitude().doubleValue())).append(" ")
                                        .append(sc.getValue().getDepth_Of_Focus()).append("KM");
                                sub_ff.setTitle(sub_stn_listed.toString()+" 2D Map");
                            } catch (Exception ex) {
                                Logger.getLogger(FX_2D_Map_Window.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                });
            });
        });
        

        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.show();
        // create a MapView to display the map and add it to the stack pane
        
        mapView = new MapView();
        stackPane.getChildren().addAll(mapView, export_Button, sub_map_Button);
        StackPane.setAlignment(export_Button, Pos.TOP_RIGHT);
        StackPane.setMargin(export_Button, new Insets(10, 10, 0, 0));
        StackPane.setAlignment(sub_map_Button, Pos.TOP_RIGHT);
        StackPane.setMargin(sub_map_Button, new Insets(40, 10, 0, 0));
        //mapView.setMap(map);
        
        setupMap();
    }
    
    int sub_count = 0;
    private void setupMap() {
        if (mapView != null) {
            graphicsOverlay = new GraphicsOverlay();
            graphicsOverlay_line = new GraphicsOverlay();
            graphicsOverlay_text = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);
            mapView.getGraphicsOverlays().add(graphicsOverlay_line);
            mapView.getGraphicsOverlays().add(graphicsOverlay_text);
            Basemap.Type basemapType = Basemap.Type.TOPOGRAPHIC;
            //int st_num = this.select_map.size();
            List<Double> lat_list = Lists.newArrayList();
            List<Double> lon_list = Lists.newArrayList();
            //List<Station> st_temp_list = Lists.newArrayList();
            //Queue<Seismic> sc_queue = Queues.newConcurrentLinkedQueue();
            Queue<FFT_SnapShot> fft_snp_queue = Queues.newConcurrentLinkedQueue();
            //change sub station name to main            
            
            
            this.select_map.entrySet().forEach(stn -> {
                //System.out.println(stn.getKey());
                Station st_temp = null;
                st_temp = Utils.get_StationMap().get(stn.getKey());
                if (st_temp == null) {
                    for (Station sl : Utils.get_StationList()) {
                        if (sl.getSubStationName().equals(stn.getKey())) {
                            st_temp = sl;
                        }
                    }
                }
                //st_temp_list.add(st_temp);
                lat_list.add(Double.valueOf(st_temp.getLatitude()));
                lon_list.add(Double.valueOf(st_temp.getLongitude()));
                //process FFT queue
                for (FFT_SnapShot fft_snp : stn.getValue().values()) {
                    try {
                        fft_snp_queue.add(fft_snp);
                        sc_queue.add(Utils.get_seismic_map().get(fft_snp.getStartTime()));
                        //
                        lat_list.add(fft_snp.getSeismicLatitude().doubleValue());
                        lon_list.add(fft_snp.getSeismicLongitude().doubleValue());
                    } catch (Exception ex) {
                        Utils.logger.fatal(format.format(fft_snp.getStartTime()));
                    }
                }
                
                
                
            });
            //System.out.println(lat_list.size());
            
            double lat_center = lat_list.stream().mapToDouble(lat -> lat).average().getAsDouble();
            double lon_center = lon_list.stream().mapToDouble(lon -> lon).average().getAsDouble();
            
            mainController.get_logs_textarea().appendText("Center location is "+Configs.coord_df.format(lon_center)+","+Configs.coord_df.format(lat_center)+System.getProperty("line.separator"));
            mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
            //need a scale funtion
            int lod = 7;
            map = new ArcGISMap(basemapType, lat_center, lon_center, lod);
            mapView.setMap(map);
            FeatureCollection featureCollection = new FeatureCollection();
            FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
            map.getOperationalLayers().add(featureCollectionLayer);
            createPointTable(featureCollection, fft_snp_queue, sc_queue);
        }
    }
    
    private void createPointTable(FeatureCollection featureCollection, Queue<FFT_SnapShot> fft_snp_queue, Queue<Seismic> sc_queue) {
        //gen temp station and remove empy data stations
        //List<Station> st_temp = Lists.newArrayList();
        //Utils.get_StationList().stream().filter(vs30 -> !vs30.getVs30().isEmpty()).forEach(str -> st_temp.add(str));
        
        List<Feature> sc_features = new ArrayList<>();
        List<Field> sc_pointFields = new ArrayList<>();
        
        List<Feature> station_features = new ArrayList<>();
        List<Field> station_pointFields = new ArrayList<>();
        
        //stations on route
        List<Feature> passby_station_features = new ArrayList<>();
        List<Field> passby_station_pointFields = new ArrayList<>();
        
        List<Feature> path_features = new ArrayList<>();
        List<Field> path_pointFields = new ArrayList<>();
        
        List<Feature> seg_line_features = new ArrayList<>();
        List<Field> seg_line_pointFields = new ArrayList<>();
        //if (graphicsOverlay != null) {
            
            /*
            Point point = new Point(-118.29507, 34.13501, SpatialReferences.getWgs84());
            Graphic pointGraphic = new Graphic(point, sc_pointSymbol);
            graphicsOverlay.getGraphics().add(pointGraphic);*/
        //}
        seg_line_pointFields.add(Field.createString("Segment line every "+Utils.get_step_distance()+" m", "Meter", 100));
        FeatureCollectionTable seg_line_pointsTable = new FeatureCollectionTable(seg_line_pointFields, GeometryType.POLYLINE, SpatialReferences.getWgs84());
        
        path_pointFields.add(Field.createString("Vs30 point every "+Utils.get_step_distance()+" m", "Meter", 100));
        FeatureCollectionTable path_pointsTable = new FeatureCollectionTable(path_pointFields, GeometryType.POINT, SpatialReferences.getWgs84());
        
        sc_pointFields.add(Field.createString("Seismic Event", "Time", 100));
        FeatureCollectionTable sc_pointsTable = new FeatureCollectionTable(sc_pointFields, GeometryType.POINT, SpatialReferences.getWgs84());
        
        station_pointFields.add(Field.createString("Station Event", "Time", 100));
        FeatureCollectionTable station_pointsTable = new FeatureCollectionTable(station_pointFields, GeometryType.POINT, SpatialReferences.getWgs84());
        
        passby_station_pointFields.add(Field.createString("Pass by Station Event", "Time", 100));
        FeatureCollectionTable passby_station_pointsTable = new FeatureCollectionTable(passby_station_pointFields, GeometryType.POINT, SpatialReferences.getWgs84());
        
        //epic center
        SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexOrange, 15f);
        sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 3.0f));
        SimpleRenderer sc_point_renderer = new SimpleRenderer(sc_pointSymbol);
        
        
        //passby station
        SimpleMarkerSymbol passby_station_Symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, hex88E909, 6f);
        passby_station_Symbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hex8A0BAA, 2.0f));
        SimpleRenderer passby_station_renderer = new SimpleRenderer(passby_station_Symbol);
        
        //station
        SimpleMarkerSymbol station_Symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, hexGreen, 10f);
        station_Symbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
        SimpleRenderer station_renderer = new SimpleRenderer(station_Symbol);
        
        //vs30 point
        SimpleMarkerSymbol path_Symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.X, hexF70082, 1f);
        //station_Symbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
        SimpleRenderer path_renderer = new SimpleRenderer(path_Symbol);
        
        //seg line
        SimpleLineSymbol seg_line_Symbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, hexRed, 1.5f);
        seg_line_Symbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.ARROW);
        SimpleRenderer seg_line_renderer = new SimpleRenderer(seg_line_Symbol);
        
        //add seismic points using cicle
        sc_queue.forEach(sc -> {
            Map<String, Object> attr = Maps.newHashMap();
            attr.put(format.format(sc.getStartTime()), "Epic Center");
            Point point = new Point(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue() , SpatialReferences.getWgs84());
            
            Graphic pointGraphic = new Graphic(point, sc_pointSymbol);
            graphicsOverlay.getGraphics().add(pointGraphic);
            sc_features.add(sc_pointsTable.createFeature(attr, point));
            //add text symbo
            if (kr != null) {
                StringBuilder sb = new StringBuilder();
                double k_val = (double) kr.predict(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue());
                sb.append(k_val).append(": ").append(sc.getLongitude().doubleValue()).append(", ").append(sc.getLatitude().doubleValue());
                TextSymbol txtSymbol = new TextSymbol(10F, sb.toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);

                //a point to define where the text is drawn
                Point pt = new Point(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue(), SpatialReferences.getWgs84());

                //create a graphic from the point and symbol
                Graphic gr = new Graphic(pt, txtSymbol);

                //add the graphic to the map
                graphicsOverlay.getGraphics().add(gr);
                
                //deep seicmic cause larger freq?
                
            }
            
        });
        
        //add station points using triangle
        fft_snp_queue.forEach(fft_snp -> {
            //point data
            System.out.println(fft_snp.getStartTime());
            Map<String, Object> attr = Maps.newHashMap();
            attr.put(format.format(fft_snp.getStartTime()), "Station Location");
            Point point = new Point(fft_snp.getStationLongitude().doubleValue(), fft_snp.getStationLatitude().doubleValue() , SpatialReferences.getWgs84());
            Graphic pointGraphic = new Graphic(point, station_Symbol);
            graphicsOverlay.getGraphics().add(pointGraphic);
            station_features.add(station_pointsTable.createFeature(attr, point));
            
            //add segment lines from epic center to station with kriging data
            
            //split segments by step
            double seismic_lon = fft_snp.getSeismicLongitude().doubleValue();
            double seismic_lat = fft_snp.getSeismicLatitude().doubleValue();
            double station_lon = fft_snp.getStationLongitude().doubleValue();
            double station_lat = fft_snp.getStationLatitude().doubleValue();
            Coordinate seismic_lon_temp = Coordinate.fromDegrees(seismic_lon);
            Coordinate seismic_lat_temp = Coordinate.fromDegrees(seismic_lat);
            Coordinate station_lon_temp = Coordinate.fromDegrees(station_lon);
            Coordinate station_lat_temp = Coordinate.fromDegrees(station_lat);
            //add seicmic text
            TextSymbol ep_txtSymbol = new TextSymbol(10F, format.format(fft_snp.getStartTime())+" "+fft_snp.getMagnitude(), hexRed,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
            //a point to define where the text is drawn
            Point ep_pt = new Point(seismic_lon, seismic_lat+0.005D, SpatialReferences.getWgs84());
            //create a graphic from the point and symbol
            Graphic ep_gr = new Graphic(ep_pt, ep_txtSymbol);

            //add the graphic to the map
            graphicsOverlay.getGraphics().add(ep_gr);
            
            
            com.grum.geocalc.Point st_p = com.grum.geocalc.Point.at(seismic_lat_temp, seismic_lon_temp);//seismic
            com.grum.geocalc.Point end_p = com.grum.geocalc.Point.at(station_lat_temp, station_lon_temp);//station
            
            //add stations next to route around the distance of the epic center and freefield station in radius 
            double dist_as_r = EarthCalc.vincentyDistance(st_p, end_p);
            //double dist_to_vec = dist_as_r/10D;//0.1 of radius
            double dist_to_vec = Utils.get_station_distance_to_epiccenter_vector();
            Map<String, Station> nearby_station = Maps.newConcurrentMap();
            List<Station> nearby_epic_center = Lists.newArrayList();
            Map<String, AnalysisPassbyStation> ansys_passby_station = Maps.newConcurrentMap();
            //Set<Integer> already_add_stations = Sets.newConcurrentHashSet();
           
            for (int sc = 0; sc < Utils.get_StationList().size() ; sc++) {
                Station st = Utils.get_StationList().get(sc);
                        
                double st_temp_lon = Double.valueOf(st.getLongitude());
                double st_temp_lat = Double.valueOf(st.getLatitude());
                com.grum.geocalc.Point st_temp_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(st_temp_lat), Coordinate.fromDegrees(st_temp_lon));
                
                //1.check dist
                //2.check vector innder degrees, trans to TM2 location
                //3.check 
                double st_point_dist = EarthCalc.vincentyDistance(end_p, st_temp_p);
                if (st_point_dist <= dist_as_r) {//check dist in radius
                    //check inner degrees
                    TM2coord tm2_c = Utils.Cal_lonlat_To_twd97(end_p.longitude, end_p.latitude);
                    TM2coord tm2_0 = Utils.Cal_lonlat_To_twd97(st_p.longitude, st_p.latitude);
                    TM2coord tm2_1 = Utils.Cal_lonlat_To_twd97(Double.valueOf(st.getLongitude()),  Double.valueOf(st.getLatitude()));
                    double innder_d = Utils.getDegrees_from_inner_product(tm2_c.x_TM2, tm2_c.y_TM2,
                                                                   tm2_0.x_TM2, tm2_0.y_TM2, 
                                                                   tm2_1.x_TM2, tm2_1.y_TM2);
                    
                    if (innder_d <= 90D) {
                        //check dist to the vector
                        com.grum.geocalc.Point first_p = com.grum.geocalc.Point.at(seismic_lat_temp, seismic_lon_temp);//seismic
                        com.grum.geocalc.Point second_p = com.grum.geocalc.Point.at(station_lat_temp, station_lon_temp);//station
                        
                        com.grum.geocalc.Point mid_p = EarthCalc.midPoint(first_p, second_p);
                        
                        double first_p_to_station_dist = EarthCalc.vincentyDistance(first_p, st_temp_p);
                        double second_p_to_station_dist = EarthCalc.vincentyDistance(second_p, st_temp_p);
                        //double mid_p_to_station_dist = EarthCalc.vincentyDistance(mid_p, st_temp_p);
                        
                        
                        //
                        //com.grum.geocalc.Point mid_p_close_to_first_p = EarthCalc.midPoint(first_p, mid_p);
                        //com.grum.geocalc.Point mid_p_close_to_second_p = EarthCalc.midPoint(second_p, mid_p);
                        double mid_p_to_station_dist = EarthCalc.vincentyDistance(st_temp_p, mid_p);
                        //st_temp_p
                        if (mid_p_to_station_dist > dist_to_vec) {
                            //check witch side is closer
                            if (first_p_to_station_dist < second_p_to_station_dist) {//close to 1st potint
                                com.grum.geocalc.Point mid_p_temp_now = EarthCalc.midPoint(first_p, mid_p);
                                com.grum.geocalc.Point mid_p_temp_next;
                                double mid_p_dist_now;
                                double mid_p_dist_next;
                                //int count = 0;
                                while(true) {
                                    //count++;
                                    mid_p_temp_next = EarthCalc.midPoint(first_p, mid_p_temp_now);
                                    mid_p_dist_now = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_now);
                                    mid_p_dist_next = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_next);
                                    //System.out.println(count+": "+mid_p_dist_now+", "+ mid_p_dist_next);
                                    if (mid_p_dist_next < mid_p_dist_now) {//kepp moving
                                        mid_p_temp_now = mid_p_temp_next;
                                    } else {//get pre point
                                        if (mid_p_dist_now <= dist_to_vec) {
                                            if (!nearby_station.containsKey(st.getStationName())) {
                                                nearby_station.put(st.getStationName(), st);
                                            }
                                            break;
                                        } else {//still to far
                                            break;
                                        }
                                    }
                                }
                            } else if (mid_p_to_station_dist < first_p_to_station_dist) {//close to 2nd potint
                                com.grum.geocalc.Point mid_p_temp_now = EarthCalc.midPoint(second_p, mid_p);
                                com.grum.geocalc.Point mid_p_temp_next;
                                double mid_p_dist_now;
                                double mid_p_dist_next;
                                while(true) {
                                    mid_p_temp_next = EarthCalc.midPoint(second_p, mid_p_temp_now);
                                    mid_p_dist_now = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_now);
                                    mid_p_dist_next = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_next);
                                    if (mid_p_dist_next < mid_p_dist_now) {//kepp moving
                                        mid_p_temp_now = mid_p_temp_next;
                                    } else {//get pre point
                                        if (mid_p_dist_now <= dist_to_vec) {
                                            if (!nearby_station.containsKey(st.getStationName())) {
                                                nearby_station.put(st.getStationName(), st);
                                            }
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            } else {//mid point is at center break the loop
                                break;
                            }

                        } else {//very close to the center of vector
                            if (!nearby_station.containsKey(st.getStationName())) {
                                nearby_station.put(st.getStationName(), st);
                            }
                        }
                        //dist_to_vec
                    }
                }
                
                double epc_point_dist = EarthCalc.vincentyDistance(st_p, com.grum.geocalc.Point.at(Coordinate.fromDegrees(st_temp_lat), Coordinate.fromDegrees(st_temp_lon)));
                if (epc_point_dist <= dist_as_r) {
                    TM2coord tm2_c = Utils.Cal_lonlat_To_twd97(st_p.longitude, st_p.latitude);
                    TM2coord tm2_0 = Utils.Cal_lonlat_To_twd97(end_p.longitude, end_p.latitude);
                    TM2coord tm2_1 = Utils.Cal_lonlat_To_twd97(Double.valueOf(st.getLongitude()),  Double.valueOf(st.getLatitude()));
                    double innder_d = Utils.getDegrees_from_inner_product(tm2_c.x_TM2, tm2_c.y_TM2,
                                                                   tm2_0.x_TM2, tm2_0.y_TM2, 
                                                                   tm2_1.x_TM2, tm2_1.y_TM2);
                    if (innder_d <= 90D) {
                        //check dist to the vector
                        com.grum.geocalc.Point first_p = com.grum.geocalc.Point.at(seismic_lat_temp, seismic_lon_temp);//seismic
                        com.grum.geocalc.Point second_p = com.grum.geocalc.Point.at(station_lat_temp, station_lon_temp);//station
                        
                        com.grum.geocalc.Point mid_p = EarthCalc.midPoint(first_p, second_p);
                        
                        double first_p_to_station_dist = EarthCalc.vincentyDistance(first_p, st_temp_p);
                        double second_p_to_station_dist = EarthCalc.vincentyDistance(second_p, st_temp_p);
                        //double mid_p_to_station_dist = EarthCalc.vincentyDistance(mid_p, st_temp_p);
                        
                        
                        //
                        //com.grum.geocalc.Point mid_p_close_to_first_p = EarthCalc.midPoint(first_p, mid_p);
                        //com.grum.geocalc.Point mid_p_close_to_second_p = EarthCalc.midPoint(second_p, mid_p);
                        double mid_p_to_station_dist = EarthCalc.vincentyDistance(st_temp_p, mid_p);
                        //st_temp_p
                        if (mid_p_to_station_dist > dist_to_vec) {
                            //check witch side is closer
                            if (first_p_to_station_dist < second_p_to_station_dist) {//close to 1st potint
                                com.grum.geocalc.Point mid_p_temp_now = EarthCalc.midPoint(first_p, mid_p);
                                com.grum.geocalc.Point mid_p_temp_next;
                                double mid_p_dist_now;
                                double mid_p_dist_next;
                                //int count = 0;
                                while(true) {
                                    //count++;
                                    mid_p_temp_next = EarthCalc.midPoint(first_p, mid_p_temp_now);
                                    mid_p_dist_now = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_now);
                                    mid_p_dist_next = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_next);
                                    //System.out.println(count+": "+mid_p_dist_now+", "+ mid_p_dist_next);
                                    if (mid_p_dist_next < mid_p_dist_now) {//kepp moving
                                        mid_p_temp_now = mid_p_temp_next;
                                    } else {//get pre point
                                        if (mid_p_dist_now <= dist_to_vec) {
                                            if (!nearby_station.containsKey(st.getStationName())) {
                                                nearby_station.put(st.getStationName(), st);
                                            }
                                            break;
                                        } else {//still to far
                                            break;
                                        }
                                    }
                                }
                            } else if (mid_p_to_station_dist < first_p_to_station_dist) {//close to 2nd potint
                                com.grum.geocalc.Point mid_p_temp_now = EarthCalc.midPoint(second_p, mid_p);
                                com.grum.geocalc.Point mid_p_temp_next;
                                double mid_p_dist_now;
                                double mid_p_dist_next;
                                while(true) {
                                    mid_p_temp_next = EarthCalc.midPoint(second_p, mid_p_temp_now);
                                    mid_p_dist_now = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_now);
                                    mid_p_dist_next = EarthCalc.vincentyDistance(st_temp_p, mid_p_temp_next);
                                    if (mid_p_dist_next < mid_p_dist_now) {//kepp moving
                                        mid_p_temp_now = mid_p_temp_next;
                                    } else {//get pre point
                                        if (mid_p_dist_now <= dist_to_vec) {
                                            if (!nearby_station.containsKey(st.getStationName())) {
                                                nearby_station.put(st.getStationName(), st);
                                            }
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            } else {//mid point is at center break the loop
                                break;
                            }

                        } else {//very close to the center of vector
                            if (!nearby_station.containsKey(st.getStationName())) {
                                nearby_station.put(st.getStationName(), st);
                            }
                        }
                        //dist_to_vec
                    }
                }
                //add paasby stations, and check the event exist in the station
                nearby_epic_center.forEach(nec -> {
                    Map<String, FFT_SnapShot> tt = Utils.get_fft_snp_map().get(nec.getStationName());
                    if (tt != null) {
                        tt.entrySet().forEach(snp -> {
                            if (snp.getValue().getStartTime().equals(fft_snp.getStartTime())) {
                                Map<String, Object> passby_attr = Maps.newHashMap();
                                attr.put(nec.getStationName(), "Passby Station Location");
                                Point passby_point = new Point(Double.valueOf(nec.getLongitude()), Double.valueOf(nec.getLatitude()) , SpatialReferences.getWgs84());

                                Graphic passby_pointGraphic = new Graphic(passby_point, passby_station_Symbol);
                                graphicsOverlay.getGraphics().add(passby_pointGraphic);
                                passby_station_features.add(passby_station_pointsTable.createFeature(passby_attr, passby_point));
                            }
                        });
                    }
                    
                });
                nearby_station.entrySet().forEach(nec -> {
                    Map<String, FFT_SnapShot> tt = Utils.get_fft_snp_map().get(nec.getValue().getStationName());
                    if (tt != null) {
                        tt.entrySet().forEach(snp -> {
                            if (snp.getValue().getStartTime().equals(fft_snp.getStartTime())) {
                                if (!ansys_passby_station.containsKey(snp.getValue().getStationCode())) {
                                    ansys_passby_station.put(snp.getValue().getStationCode(), new AnalysisPassbyStation(snp.getValue()));
                                }
                                Map<String, Object> passby_attr = Maps.newHashMap();
                                attr.put(nec.getValue().getStationName(), "Passby Station Location");
                                Point passby_point = new Point(Double.valueOf(nec.getValue().getLongitude()), Double.valueOf(nec.getValue().getLatitude()) , SpatialReferences.getWgs84());

                                Graphic passby_pointGraphic = new Graphic(passby_point, passby_station_Symbol);
                                graphicsOverlay.getGraphics().add(passby_pointGraphic);
                                passby_station_features.add(passby_station_pointsTable.createFeature(passby_attr, passby_point));
                            }
                        });
                    }
                });
                
                
            }

            List<com.grum.geocalc.Point> step_points = Lists.newArrayList();
            step_points.add(st_p);
            GeodeticCalculator geoCalc = new GeodeticCalculator();
            while (true) {
                //fast vincenty
                GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, new GlobalCoordinates(st_p.latitude, st_p.longitude), new GlobalCoordinates(end_p.latitude, end_p.longitude));
                //double temp_distance = EarthCalc.vincentyDistance(st_p, end_p);
                //double azimuth_temp = EarthCalc.vincentyBearing(st_p, end_p);
                double temp_distance = geoCurve.getEllipsoidalDistance();
                double azimuth_temp = geoCurve.getAzimuth();
                if (temp_distance < Utils.get_step_distance()) {//add last
                    step_points.add(end_p);
                    break;
                } else {//dist is bigger then a step
                    GlobalCoordinates dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, new GlobalCoordinates(st_p.latitude, st_p.longitude), azimuth_temp, Utils.get_step_distance(), new double[1]);
                    //Coordinate dest_lon_temp = Coordinate.fromDegrees(dest.getLongitude());
                    //Coordinate dest_lat_temp = Coordinate.fromDegrees(dest.getLatitude());
                    //com.grum.geocalc.Point dest_sc_p = com.grum.geocalc.Point.at(dest_lat_temp, dest_lon_temp);//seismic
                    //com.grum.geocalc.Point this_point = EarthCalc.pointAt(st_p, azimuth_temp, Utils.get_step_distance());
                    Coordinate this_point_lon_temp = Coordinate.fromDegrees(dest.getLongitude());
                    Coordinate this_point_lat_temp = Coordinate.fromDegrees(dest.getLatitude());
                    com.grum.geocalc.Point this_point = com.grum.geocalc.Point.at(this_point_lat_temp, this_point_lon_temp);
                    step_points.add(this_point);
                    st_p = this_point;
                }
            }
            
            
            //add path point and text
            int c = 0;
            double land_decay = 1.0D;
            double freq_decay = 1.0D;
            ansys_passby_station.entrySet().forEach(aps-> {
                StringBuilder sb = new StringBuilder();
                FFT_SnapShot aps_temp = aps.getValue().get_fft_data();
                station_text.add(aps_temp.getStationCode());
                //sb.append(Configs.df.format(aps_temp.getVs30())).append(": ").append(Configs.df.format(aps_temp.getStationLongitude().doubleValue())).append(", ").append(Configs.df.format(aps_temp.getStationLatitude().doubleValue()));
                sb.append(aps_temp.getStationCode())   
                        .append(" - ")
                    .append(Configs.df.format(aps_temp.getVs30()))
                        .append(": U ")
                    .append(Configs.df.format(aps_temp.getMax_U_Hz_20().doubleValue()))
                        .append(", N ")
                    .append(Configs.df.format(aps_temp.getMax_N_Hz_20().doubleValue()))
                        .append(", E ")
                    .append(Configs.df.format(aps_temp.getMax_E_Hz_20().doubleValue()));
                
                //have text overlapping problem
                TextSymbol txtSymbol = new TextSymbol(10F, sb.toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                int text_count = (int)station_text.stream().filter(stt -> stt.equals(aps_temp.getStationCode())).count()-1;
                //a point to define where the text is drawn
                Point pt = new Point(aps_temp.getStationLongitude().doubleValue()+0.001*text_count, aps_temp.getStationLatitude().doubleValue()+0.001*text_count, SpatialReferences.getWgs84());

                //create a graphic from the point and symbol
                Graphic gr = new Graphic(pt, txtSymbol);

                //add the graphic to the map
                graphicsOverlay.getGraphics().add(gr);
            
            });
            
            
            for (com.grum.geocalc.Point st : step_points) {
                Map<String, Object> path_att = Maps.newHashMap();
                Point vs30_point = new Point(st.longitude, st.latitude , SpatialReferences.getWgs84());
                path_att.put(Configs.coord_df.format(st.longitude)+","+Configs.coord_df.format(st.latitude), vs30_point);
                Graphic pathGraphic = new Graphic(vs30_point, path_Symbol);
                graphicsOverlay.getGraphics().add(pathGraphic);
                path_features.add(path_pointsTable.createFeature(path_att, vs30_point));

                //add seg lines every 200 points(20km)
                if (c + 1 < step_points.size() && c % 200 == 0 && c > 0) {
                    PointCollection seg_line_Points = new PointCollection(SpatialReferences.getWgs84());
                    seg_line_Points.add(new Point(st.longitude, st.latitude));
                    seg_line_Points.add(new Point(step_points.get(c+1).longitude,step_points.get(c+1).latitude));
                    Polyline seg_line = new Polyline(seg_line_Points);
                    Map<String, Object> seg_line_att = Maps.newHashMap();
                    seg_line_att.put(Configs.coord_df.format(st.longitude)+","+Configs.coord_df.format(st.latitude), seg_line);
                    Graphic seg_line_Graphic = new Graphic(seg_line, seg_line_Symbol);
                    graphicsOverlay.getGraphics().add(seg_line_Graphic);
                    seg_line_features.add(seg_line_pointsTable.createFeature(seg_line_att, seg_line));
                }
                /*
                SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, hexRed, 2f);
                polylineSymbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.ARROW);
                Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
                graphicsOverlay.getGraphics().add(polylineGraphic);*/
                c++;
                //double k_val = kr.Kriging_predict_json(st.longitude, st.latitude, "gaussian");
                //double k_val = kr.Kriging_predict(st.longitude, st.latitude);
                //TM2coord tm2_tmp = Utils.Cal_lonlat_To_twd97(st.longitude, st.latitude);
                if (kr != null) {
                    double k_val = (double) kr.predict(st.longitude, st.latitude);
                    if (c < step_points.size()) {
                        double next_k_val = (double) kr.predict(step_points.get(c).longitude, step_points.get(c).latitude);
                        land_decay = land_decay - ((1D/k_val)*0.1D);
                        if (k_val > next_k_val) {
                            //calc stiffness
                            freq_decay = freq_decay / (1+(0.1D/Math.abs(k_val-next_k_val))*0.1D);
                        } else {
                            freq_decay = freq_decay * (1+(0.1D/Math.abs(k_val-next_k_val))*0.1D);
                        }
                    }
                    
                    
                    StringBuilder sb = new StringBuilder();
                    //sb.append(Configs.df.format(k_val)).append(": ").append(Configs.df.format(st.longitude)).append(", ").append(Configs.df.format(st.latitude));
                    
                    sb.append(Configs.df.format(k_val));
                    /*
                    .append(": U ")
                    .append(Configs.df.format(aps_temp.getMax_U_Hz_20().doubleValue()))
                        .append(", N ")
                    .append(Configs.df.format(aps_temp.getMax_N_Hz_20().doubleValue()))
                        .append(", E ")
                    .append(Configs.df.format(aps_temp.getMax_E_Hz_20().doubleValue()));
                    */
                    TextSymbol txtSymbol = new TextSymbol(10F, sb.toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);

                    //a point to define where the text is drawn
                    Point pt = new Point(st.longitude, st.latitude, SpatialReferences.getWgs84());

                    //create a graphic from the point and symbol
                    Graphic gr = new Graphic(pt, txtSymbol);

                    //add the graphic to the map
                    graphicsOverlay.getGraphics().add(gr);
                    //System.out.println(c+", "+ st+", "+ k_val);
                    if (analysis_map_step.get(fft_snp.getStationCode()) == null) {
                        Map<Integer, AnalysisStep> temp_step = Maps.newConcurrentMap();
                        temp_step.put(c, new AnalysisStep(c, st, k_val));
                        analysis_map_step.put(fft_snp.getStationCode(), temp_step);
                    } else {
                        analysis_map_step.get(fft_snp.getStationCode()).put(c, new AnalysisStep(c, st, k_val));
                    }
                }
                /*
                System.out.println("Land Decay Coefficient: "+land_decay);
                System.out.println("Frequency Coefficient: "+freq_decay);
                System.out.println("Result Freqency: "+land_decay/freq_decay);*/
            }
            System.out.println(ansys_passby_station.size());
            System.out.println(nearby_station.size());
            System.out.println(nearby_epic_center.size());
            
            
            /*
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            polylinePoints.add(new Point(fft_snp.getSeismicLongitude().doubleValue(), fft_snp.getSeismicLatitude().doubleValue()));
            polylinePoints.add(new Point(fft_snp.getStationLongitude().doubleValue(), fft_snp.getStationLatitude().doubleValue()));
            Polyline polyline = new Polyline(polylinePoints);
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, hexRed, 2f);
            polylineSymbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.ARROW);
            Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);*/
            
        });
        
        passby_station_pointsTable.setRenderer(passby_station_renderer);
        featureCollection.getTables().add(passby_station_pointsTable);
        passby_station_pointsTable.addFeaturesAsync(passby_station_features);
        
        seg_line_pointsTable.setRenderer(seg_line_renderer);
        featureCollection.getTables().add(seg_line_pointsTable);
        seg_line_pointsTable.addFeaturesAsync(seg_line_features);
        
        path_pointsTable.setRenderer(path_renderer);
        featureCollection.getTables().add(path_pointsTable);
        path_pointsTable.addFeaturesAsync(path_features);
        
        
        sc_pointsTable.setRenderer(sc_point_renderer);
        featureCollection.getTables().add(sc_pointsTable);
        sc_pointsTable.addFeaturesAsync(sc_features);
        
        station_pointsTable.setRenderer(station_renderer);
        featureCollection.getTables().add(station_pointsTable);
        station_pointsTable.addFeaturesAsync(station_features);
    }
    
    
    
    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
