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
import java.awt.Polygon;
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
import tools.ColoerSet;
import tools.Configs;
import tools.Kriging;
import tools.Utils;

/**
 *
 * @author you
 */
public class FX_2D_Map_Window_Interpolation_Test extends Application {
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
    
    Map<String, Interpolation_Result_Obj> result_map;
    List<String> outline_list;
    private Map<Date, Seismic> analysis_map_seismic_event = Maps.newConcurrentMap();
    private Map<String, Station> analysis_map_station = Maps.newConcurrentMap();
    private Map<String, Map<String, FFT_SnapShot>> analysis_map_fftsnp = Maps.newConcurrentMap();
    private Map<String, Map<Integer, AnalysisStep>> analysis_map_step = Maps.newConcurrentMap();//station, step count, analysis data
    private Map<String, AnalysisPassbyStation> analysis_map_passbystation = Maps.newConcurrentMap();//step count, analysis pass by station
    private List<String> station_text = Lists.newArrayList();
    private GraphicsOverlay graphicsOverlay;
    private GraphicsOverlay graphicsOverlay_circle;
    private GraphicsOverlay graphicsOverlay_text;
    private ColoerSet sol_set = new ColoerSet();
    
    public FX_2D_Map_Window_Interpolation_Test(Map<String, Interpolation_Result_Obj> result_map, List<String> outline_list) {
        this.result_map = result_map;
        this.outline_list = outline_list;
    }
    
    public FX_2D_Map_Window_Interpolation_Test(Map<String, Interpolation_Result_Obj> result_map) {
        this.result_map = result_map;
    }
    
    public FX_2D_Map_Window_Interpolation_Test(List<String> outline_list) {
        this.outline_list = outline_list;
    }
    
    
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        mainController = MainApp.loader.getController();
        this.stage = stage;
        stage.setWidth(800);
        stage.setHeight(900);
        
        //create a export button
        Button export_Button = new Button("Export (xls)");
        
        export_Button.setOnAction(event -> {
            //calc kriging
            
            //set data in to collections
            
            
            //send op
        });
        
        
        

        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.show();
        // create a MapView to display the map and add it to the stack pane
        
        mapView = new MapView();
        stackPane.getChildren().addAll(mapView, export_Button);
        StackPane.setAlignment(export_Button, Pos.TOP_RIGHT);
        StackPane.setMargin(export_Button, new Insets(10, 10, 0, 0));
        //mapView.setMap(map);
        
        setupMap();
    }
    
    int sub_count = 0;
    private void setupMap() {
        if (mapView != null) {
            graphicsOverlay = new GraphicsOverlay();
            graphicsOverlay_circle = new GraphicsOverlay();
            graphicsOverlay_text = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);
            mapView.getGraphicsOverlays().add(graphicsOverlay_circle);
            mapView.getGraphicsOverlays().add(graphicsOverlay_text);
            Basemap.Type basemapType = Basemap.Type.TOPOGRAPHIC;
            //int st_num = this.select_map.size();
            List<Double> lat_list = Lists.newArrayList();
            List<Double> lon_list = Lists.newArrayList();
            List<Station> st_temp_list = Lists.newLinkedList();
            //Queue<Seismic> sc_queue = Queues.newConcurrentLinkedQueue();
            Queue<Interpolation_Result_Obj> ir_queue = Queues.newConcurrentLinkedQueue();
            
            //change sub station name to main            
            
            if (this.result_map != null) {
                this.result_map.entrySet().forEach(stn -> {
                    Station st_temp = Utils.get_StationMap().get(stn.getKey());
                    lat_list.add(Double.valueOf(st_temp.getLatitude()));
                    lon_list.add(Double.valueOf(st_temp.getLongitude()));
                    //process ir queue
                    Interpolation_Result_Obj ir_temp = stn.getValue();

                    ir_queue.add(ir_temp);
                    //
                    lat_list.add(Double.valueOf(Utils.get_StationMap().get(ir_temp.get_StationName()).getLatitude()));
                    lon_list.add(Double.valueOf(Utils.get_StationMap().get(ir_temp.get_StationName()).getLongitude()));
                });
                System.out.println(this.result_map.size());

                double lat_center = lat_list.stream().mapToDouble(lat -> lat).average().getAsDouble();
                double lon_center = lon_list.stream().mapToDouble(lon -> lon).average().getAsDouble();

                mainController.get_logs_textarea().appendText("Center location is "+Configs.coord_df.format(lon_center)+","+Configs.coord_df.format(lat_center)+System.getProperty("line.separator"));
                mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
                //need a scale funtion
                int lod = 8;
                map = new ArcGISMap(basemapType, lat_center, lon_center, lod);
                mapView.setMap(map);
                FeatureCollection featureCollection = new FeatureCollection();
                FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
                map.getOperationalLayers().add(featureCollectionLayer);
                createPointTable(featureCollection, ir_queue);
            }
            
            if (this.outline_list != null) {
                this.outline_list.stream().forEach(st -> {
                    Station st_temp = Utils.get_StationMap().get(st);
                    lat_list.add(Double.valueOf(st_temp.getLatitude()));
                    lon_list.add(Double.valueOf(st_temp.getLongitude()));
                    
                    st_temp_list.add(st_temp);
                
                });
                double lat_center = lat_list.stream().mapToDouble(lat -> lat).average().getAsDouble();
                double lon_center = lon_list.stream().mapToDouble(lon -> lon).average().getAsDouble();

                mainController.get_logs_textarea().appendText("Center location is "+Configs.coord_df.format(lon_center)+","+Configs.coord_df.format(lat_center)+System.getProperty("line.separator"));
                mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
                //need a scale funtion
                int lod = 8;
                map = new ArcGISMap(basemapType, lat_center, lon_center, lod);
                mapView.setMap(map);
                FeatureCollection featureCollection = new FeatureCollection();
                FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
                map.getOperationalLayers().add(featureCollectionLayer);
                createPointTable(featureCollection, st_temp_list);
                
                
                
            }
        }
    }
    
    private void createPointTable(FeatureCollection featureCollection, Queue<Interpolation_Result_Obj> ir_queue) {
        //gen temp station and remove empy data stations
        //List<Station> st_temp = Lists.newArrayList();
        //Utils.get_StationList().stream().filter(vs30 -> !vs30.getVs30().isEmpty()).forEach(str -> st_temp.add(str));
        Set<String> ref_station = Sets.newConcurrentHashSet();
        
        ir_queue.stream().forEach(ir -> {
            Map<String, Object> attr = Maps.newHashMap();
            attr.put(ir.get_StationName(), "Station");
            ir.get_RefStationList().stream().forEach(ref -> ref_station.add(ref));
            
            double lat = Double.valueOf(Utils.get_StationMap().get(ir.get_StationName()).getLatitude());
            double lon = Double.valueOf(Utils.get_StationMap().get(ir.get_StationName()).getLongitude());
            
            
            SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, sol_set.get_ML_RGB_Color(Math.abs((float)ir.get_ErrorPercent()),200F), Math.abs((float)ir.get_ErrorPercent()*0.5F));
            //sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
            Point point = new Point(lon, lat , SpatialReferences.getWgs84());
            Graphic sc_pointGraphic = new Graphic(point, sc_pointSymbol);
            graphicsOverlay.getGraphics().add(sc_pointGraphic);
            
            //sc_features.add(sc_pointsTable.createFeature(attr, point));
            //add text symbo
            
            TextSymbol txtSymbol = new TextSymbol(10F, "                  "+ Configs.df.format(ir.get_ErrorPercent())+"% "+Configs.df.format(ir.get_PredictValue())+"/"+Configs.df.format(ir.get_CurrentValue()), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);

            //a point to define where the text is drawn
            Point pt = new Point(lon, lat, SpatialReferences.getWgs84());

                //create a graphic from the point and symbol
            Graphic gr = new Graphic(pt, txtSymbol);

            //add the graphic to the map
            graphicsOverlay_text.getGraphics().add(gr);
                
                //deep seicmic cause larger freq?
            //sc_pointsTable.setRenderer(sc_point_renderer);
            
        });
        System.out.println(ref_station.size());
        ref_station.stream().forEach(ref -> {
            double lat = Double.valueOf(Utils.get_StationMap().get(ref).getLatitude());
            double lon = Double.valueOf(Utils.get_StationMap().get(ref).getLongitude());
            SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, hexBlue, 8F);
            //sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
            Point point = new Point(lon, lat , SpatialReferences.getWgs84());
            Graphic sc_pointGraphic = new Graphic(point, sc_pointSymbol);
            graphicsOverlay.getGraphics().add(sc_pointGraphic);
            
        });
        
                
            

            
            
            
            //add path point and text
            
            
            /*
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            polylinePoints.add(new Point(fft_snp.getSeismicLongitude().doubleValue(), fft_snp.getSeismicLatitude().doubleValue()));
            polylinePoints.add(new Point(fft_snp.getStationLongitude().doubleValue(), fft_snp.getStationLatitude().doubleValue()));
            Polyline polyline = new Polyline(polylinePoints);
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, hexRed, 2f);
            polylineSymbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.ARROW);
            Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);*/
    }
    
    private void createPointTable(FeatureCollection featureCollection, List<Station> st_list) {
        //gen temp station and remove empy data stations
        //List<Station> st_temp = Lists.newArrayList();
        //Utils.get_StationList().stream().filter(vs30 -> !vs30.getVs30().isEmpty()).forEach(str -> st_temp.add(str));
        Set<String> ref_station = Sets.newConcurrentHashSet();
        List<Feature> seg_line_features = new ArrayList<>();
        List<Field> seg_line_pointFields = new ArrayList<>();
        seg_line_pointFields.add(Field.createString("outline", "Meter", 100));
        FeatureCollectionTable seg_line_pointsTable = new FeatureCollectionTable(seg_line_pointFields, GeometryType.POLYLINE, SpatialReferences.getWgs84());
        SimpleLineSymbol seg_line_Symbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, hexRed, 1.5f);
        seg_line_Symbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.NONE);
        SimpleRenderer seg_line_renderer = new SimpleRenderer(seg_line_Symbol);
        Polygon outline_p = new Polygon();
        for (int stc = 0; stc < st_list.size() ; stc++) {
            Station st_p = st_list.get(stc);
            
            Map<String, Object> attr = Maps.newHashMap();
            attr.put(st_p.getStationName(), "Station");
            double lat_p = Double.valueOf(st_p.getLatitude());
            double lon_p = Double.valueOf(st_p.getLongitude());
            outline_p.addPoint((int)(lon_p*10000D), (int)(lat_p*10000D));
            Station st_f;
            
            if (stc == st_list.size() - 1) {
                st_f = st_list.get(0);
            } else {
                st_f = st_list.get(stc+1);
            }
            double lat_f = Double.valueOf(st_f.getLatitude());
            double lon_f = Double.valueOf(st_f.getLongitude());
            
            //sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
            Point point = new Point(lon_p, lat_p , SpatialReferences.getWgs84());
            
            PointCollection seg_line_Points = new PointCollection(SpatialReferences.getWgs84());
            seg_line_Points.add(new Point(lon_p, lat_p));
            seg_line_Points.add(new Point(lon_f, lat_f));
            Polyline seg_line = new Polyline(seg_line_Points);
            Map<String, Object> seg_line_att = Maps.newHashMap();
            
            Graphic seg_line_Graphic = new Graphic(seg_line, seg_line_Symbol);
            graphicsOverlay.getGraphics().add(seg_line_Graphic);
            seg_line_features.add(seg_line_pointsTable.createFeature(seg_line_att, seg_line));
            
            //sc_features.add(sc_pointsTable.createFeature(attr, point));
            //add text symbo
            
            

            
            
            
        }
        //stations in polygon
        List<Station> stt = Lists.newArrayList(Utils.get_StationList());
        stt.removeAll(st_list);
        
        List<Station> stt_rm = Lists.newArrayList();
        
        stt.stream().forEach(ref -> {
            double lat = Double.valueOf(ref.getLatitude());
            double lon = Double.valueOf(ref.getLongitude());
            if (outline_p.contains(lon*10000D, lat*10000D)) {
                SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, hexBlue, 8F);
                //sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
                Point point = new Point(lon, lat , SpatialReferences.getWgs84());
                Graphic sc_pointGraphic = new Graphic(point, sc_pointSymbol);
                graphicsOverlay.getGraphics().add(sc_pointGraphic);
                TextSymbol txtSymbol = new TextSymbol(10F, ref.getStationName(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                //a point to define where the text is drawn
                Point pt = new Point(lon, lat, SpatialReferences.getWgs84());
                Graphic gr = new Graphic(pt, txtSymbol);
                graphicsOverlay_text.getGraphics().add(gr);
            } else {
                stt_rm.add(ref);
            }
        });
        stt.removeAll(stt_rm);
        
        //seismic in polygon
        /*
        Queue<Seismic> inner_events = Queues.newConcurrentLinkedQueue();
        Utils.get_seismic_map().entrySet().stream().forEach(sc -> {
            double lat = sc.getValue().getLatitude().doubleValue();
            double lon = sc.getValue().getLongitude().doubleValue();
            if (outline_p.contains(lon*10000D, lat*10000D)) {
                SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, hexBlue, 8F);
                //sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
                Point point = new Point(lon, lat , SpatialReferences.getWgs84());
                Graphic sc_pointGraphic = new Graphic(point, sc_pointSymbol);
                graphicsOverlay.getGraphics().add(sc_pointGraphic);
                TextSymbol txtSymbol = new TextSymbol(10F, format.format(sc.getValue().getStartTime()), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                //a point to define where the text is drawn
                Point pt = new Point(lon, lat, SpatialReferences.getWgs84());
                Graphic gr = new Graphic(pt, txtSymbol);
                graphicsOverlay_text.getGraphics().add(gr);
                inner_events.add(sc.getValue());
            }
        });       
        System.out.println(inner_events.size());
        */
            
            
            
            //add path point and text
            
            
            /*
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            polylinePoints.add(new Point(fft_snp.getSeismicLongitude().doubleValue(), fft_snp.getSeismicLatitude().doubleValue()));
            polylinePoints.add(new Point(fft_snp.getStationLongitude().doubleValue(), fft_snp.getStationLatitude().doubleValue()));
            Polyline polyline = new Polyline(polylinePoints);
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, hexRed, 2f);
            polylineSymbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.ARROW);
            Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);*/
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
