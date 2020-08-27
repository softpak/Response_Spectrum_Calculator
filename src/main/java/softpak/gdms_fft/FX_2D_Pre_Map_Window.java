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
import com.esri.arcgisruntime.layers.Layer;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.json.JSONObject;
import tools.ColoerSet;
import tools.Configs;
import tools.Kriging;
import tools.Utils;

/**
 *
 * @author you
 */
public class FX_2D_Pre_Map_Window extends Application {
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
    
    private Map<Date, Seismic> analysis_map_seismic_event = Maps.newConcurrentMap();
    private List<Seismic> sort_by_ml_seismic_list = Lists.newArrayList(Utils.get_seismic_map().values());
    
    private Map<String, Station> analysis_map_station = Maps.newConcurrentMap();
    private Map<String, Map<String, FFT_SnapShot>> analysis_map_fftsnp = Maps.newConcurrentMap();
    private Map<String, Map<Integer, AnalysisStep>> analysis_map_step = Maps.newConcurrentMap();//station, step count, analysis data
    private Map<String, AnalysisPassbyStation> analysis_map_passbystation = Maps.newConcurrentMap();//step count, analysis pass by station
    private List<String> seismic_text = Lists.newArrayList();
    List<Station> station_list;
    private Queue<Seismic> sc_queue = Queues.newConcurrentLinkedQueue();
    private int pre_type;
    private GraphicsOverlay graphicsOverlay_symbol;
    private GraphicsOverlay graphicsOverlay_text;
    private ColoerSet sol_set = new ColoerSet();
    /*
    public static int map_type_2d_seismic_station = 0;
    public static int map_type_2d_seismic = 1;
    public static int map_type_2d_station = 2;
    */
    public FX_2D_Pre_Map_Window(int pre_type) {
        this.pre_type = pre_type;
    }
    
    public FX_2D_Pre_Map_Window(int pre_type, List<Station> station_list) {
        this.pre_type = pre_type;
        this.station_list = station_list;
    }
    /*
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }*/
    
    @Override
    public void start(Stage stage) throws Exception {
        mainController = MainApp.loader.getController();
        //sort event
        /*
        Collections.sort(this.sort_by_ml_seismic_list, new Comparator<Seismic>() {
            @Override
            public int compare(Seismic sc1, Seismic sc2) {
                if (sc1.getMagnitude().doubleValue() > sc2.getMagnitude().doubleValue())
                    return -1;
                if (sc1.getMagnitude().doubleValue() < sc2.getMagnitude().doubleValue())
                    return 1;
                return 0;
            }
        });*/
        sort_by_ml_seismic_list.sort((Seismic sc1,Seismic sc2) -> (Double.compare(sc2.getMagnitude().doubleValue(),sc1.getMagnitude().doubleValue())));
        
        this.stage = stage;
        switch (pre_type) {
            case 0://
                this.stage.setTitle("Seismic and Station Map");
                break;
            case 1://
                this.stage.setTitle("Seismic Map");
                break;
            case 2://
                this.stage.setTitle("Station Map");
                break;
        }
        stage.setWidth(800);
        stage.setHeight(600);
        
        //create a export button
        Button export_Button = new Button("Export (xls)");
        Button sub_map_Button = new Button("Generate Sub Map");
            
        
        
        export_Button.setOnAction(event -> {
            //calc kriging
            
            //set data in to collections
            
            
            //send op
        });
        
        sub_map_Button.setOnAction(event -> {
            //get near by seicmic around 10km default
            
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
        mapView.addMapScaleChangedListener(e -> {
            // holds the label that needs to be changed
            if (e.getSource().getMapScale() < 500000D) {//50000D
                graphicsOverlay_text.setVisible(true);
            } else {
                graphicsOverlay_text.setVisible(false);
            }
        });
        
    }
    
    int sub_count = 0;
    StringBuilder sb = new StringBuilder();
    private void setupMap() {
        if (mapView != null) {
            graphicsOverlay_symbol = new GraphicsOverlay();
            graphicsOverlay_text = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay_symbol);
            mapView.getGraphicsOverlays().add(graphicsOverlay_text);
            Basemap.Type basemapType = Basemap.Type.TOPOGRAPHIC;
            //int st_num = this.select_map.size();
            List<Double> lat_list = Lists.newArrayList();
            List<Double> lon_list = Lists.newArrayList();
            //List<Station> st_temp_list = Lists.newArrayList();
            //Queue<Seismic> sc_queue = Queues.newConcurrentLinkedQueue();
            //Queue<FFT_SnapShot> fft_snp_queue = Queues.newConcurrentLinkedQueue();
            //change sub station name to main            
            if (this.pre_type == 0 || this.pre_type == 1) {
                this.sort_by_ml_seismic_list.forEach(sc -> {
                    sb.delete(0, sb.length());
                    double lat = sc.getLatitude().doubleValue();
                    double lon = sc.getLongitude().doubleValue();
                    float ml = sc.getMagnitude().floatValue();
                    lat_list.add(lat);
                    lon_list.add(lon);
                    SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, sol_set.get_ML_RGB_Color(ml), ml*3L);
                    //sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
                    Point sc_point = new Point(lon, lat, SpatialReferences.getWgs84());
                    Graphic sc_pointGraphic = new Graphic(sc_point, sc_pointSymbol);
                    graphicsOverlay_symbol.getGraphics().add(sc_pointGraphic);
                    
                    seismic_text.add(sc.getLongitude().toString()+","+sc.getLatitude().toString());
                    int text_count = (int)seismic_text.parallelStream().filter(sct -> sct.equals(sc.getLongitude().toString()+","+sc.getLatitude().toString())).count()-1;
                    //a point to define where the text is drawn
                    Point pt = new Point(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue()+0.0005*text_count, SpatialReferences.getWgs84());
                    //add text
                    
                    sb.append("    ").append(format.format(sc.getStartTime())).append(", ").append(sc.getMagnitude());
                    TextSymbol txtSymbol = new TextSymbol(10F, sb.toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);

                    //a point to define where the text is drawn
                    //create a graphic from the point and symbol
                    Graphic gr = new Graphic(pt, txtSymbol);

                    //add the graphic to the map
                    graphicsOverlay_text.getGraphics().add(gr);
                });
            }
            
            if (this.pre_type == 0 || this.pre_type == 2) {
                if (this.station_list == null) {
                    Utils.get_StationMap().entrySet().forEach(st -> {
                        sb.delete(0, sb.length());
                        double lat = Double.valueOf(st.getValue().getLatitude());
                        double lon = Double.valueOf(st.getValue().getLongitude());
                        lat_list.add(lat);
                        lon_list.add(lon);
                        //show in map
                        SimpleMarkerSymbol st_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexOrange, 5L);
                        Point sc_point = new Point(lon, lat, SpatialReferences.getWgs84());
                        Graphic sc_pointGraphic = new Graphic(sc_point, st_pointSymbol);
                        graphicsOverlay_symbol.getGraphics().add(sc_pointGraphic);

                        seismic_text.add(st.getValue().getLongitude()+","+st.getValue().getLatitude());
                        //a point to define where the text is drawn
                        Point pt = new Point(Double.valueOf(st.getValue().getLongitude()), Double.valueOf(st.getValue().getLatitude()), SpatialReferences.getWgs84());
                        //add text

                        sb.append("    ").append(st.getValue().getStationName());
                        TextSymbol txtSymbol = new TextSymbol(15F, sb.toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);

                        //a point to define where the text is drawn
                        //create a graphic from the point and symbol
                        Graphic gr = new Graphic(pt, txtSymbol);

                        //add the graphic to the map
                        graphicsOverlay_text.getGraphics().add(gr);
                    });
                } else {
                    this.station_list.stream().forEach(st -> {
                        sb.delete(0, sb.length());
                        double lat = Double.valueOf(st.getLatitude());
                        double lon = Double.valueOf(st.getLongitude());
                        lat_list.add(lat);
                        lon_list.add(lon);
                        //show in map
                        SimpleMarkerSymbol st_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexOrange, 5L);
                        Point sc_point = new Point(lon, lat, SpatialReferences.getWgs84());
                        Graphic sc_pointGraphic = new Graphic(sc_point, st_pointSymbol);
                        graphicsOverlay_symbol.getGraphics().add(sc_pointGraphic);

                        seismic_text.add(st.getLongitude()+","+st.getLatitude());
                        //a point to define where the text is drawn
                        Point pt = new Point(Double.valueOf(st.getLongitude()), Double.valueOf(st.getLatitude()), SpatialReferences.getWgs84());
                        //add text

                        sb.append("    ").append(st.getStationName());
                        TextSymbol txtSymbol = new TextSymbol(15F, sb.toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);

                        //a point to define where the text is drawn
                        //create a graphic from the point and symbol
                        Graphic gr = new Graphic(pt, txtSymbol);

                        //add the graphic to the map
                        graphicsOverlay_text.getGraphics().add(gr);
                    });      
                }
            }
            //System.out.println(lat_list.size());
            
            double lat_center = lat_list.stream().mapToDouble(lat -> lat).average().getAsDouble();
            double lon_center = lon_list.stream().mapToDouble(lon -> lon).average().getAsDouble();
            
            mainController.get_logs_textarea().appendText("Center location is "+Configs.coord_df.format(lon_center)+","+Configs.coord_df.format(lat_center)+System.getProperty("line.separator"));
            mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
            //need a scale funtion
            int lod = 7;
            map = new ArcGISMap(basemapType, lat_center, lon_center, lod);
            mapView.setMap(map);
            
            
            
        }
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
