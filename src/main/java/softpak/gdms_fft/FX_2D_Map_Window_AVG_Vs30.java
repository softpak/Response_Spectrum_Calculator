/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.toolkit.Scalebar;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import java.io.BufferedReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import tools.Configs;
import tools.Utils;

/**
 *
 * @author you
 */
public class FX_2D_Map_Window_AVG_Vs30 extends Application {
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
    private GraphicsOverlay graphicsOverlay_text;
    private GraphicsOverlay graphicsOverlay;
    private GraphicsOverlay graphicsOverlay_t_over1;
    private GraphicsOverlay graphicsOverlay_t_less1;
    private SimpleFillSymbol fillSymbol;
    
    public FX_2D_Map_Window_AVG_Vs30() {
        
    }
    
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
    
        
    @Override
    public void start(Stage stage) throws Exception {
        mainController = MainApp.loader.getController();
        graphicsOverlay_text = new GraphicsOverlay();
        graphicsOverlay = new GraphicsOverlay();
        graphicsOverlay_t_over1 = new GraphicsOverlay();
        graphicsOverlay_t_less1 = new GraphicsOverlay();
        this.stage = stage;
        stage.setWidth(800);
        stage.setHeight(600);
        
        //create a export button
        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.show();
        // create a MapView to display the map and add it to the stack pane
        
        mapView = new MapView();
        final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
        // set initial map view point
        /*
        Point initialPoint = new Point((this.min_lon+this.max_lon)/2D, (this.min_lat+this.max_lat)/2D, SpatialReferences.getWgs84());
        Viewpoint viewpoint = new Viewpoint(initialPoint, 10000000); // point, scale
        map.setInitialViewpoint(viewpoint);*/

        // create a view for this ArcGISMap and set ArcGISMap to it
        mapView = new MapView();
        mapView.setMap(map);
        mapView.getGraphicsOverlays().add(graphicsOverlay_text);
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        mapView.getGraphicsOverlays().add(graphicsOverlay_t_over1);
        mapView.getGraphicsOverlays().add(graphicsOverlay_t_less1);
        double sc_point_lon = 120.249D;
        double sc_point_lat = 23.7047D;
        
        double st_point_lon = 121.6189D;
        double st_point_lat = 24.6604D;        
        //step 0.01 = 1 km
        //sc radius max 10km or nearest vs30 rock layer
        //1. find upper left and lower right
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        GlobalCoordinates sc_p = new GlobalCoordinates(sc_point_lat, sc_point_lon);
        GlobalCoordinates st_p = new GlobalCoordinates(st_point_lat, st_point_lon);
        
        GlobalCoordinates left_dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, sc_p, 270D, 1000D*11D, new double[1]);
        GlobalCoordinates left_up_dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, left_dest, 0D, 1000D*11D, new double[1]);
        GlobalCoordinates right_dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, sc_p, 90D, 1000D*11D, new double[1]);
        GlobalCoordinates right_low_dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, right_dest, 180D, 1000D*11D, new double[1]);
        double sc_lon_left = sc_point_lon;
        for (int lon_step = 1; sc_lon_left < left_up_dest.getLongitude(); lon_step++) {
            
        }
        
        
        
        GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, sc_p, st_p);
        double temp_distance = geoCurve.getEllipsoidalDistance();
        
        
        
        
        double center_lon = (sc_point_lon+st_point_lon)/2D;
        double center_lat = (sc_point_lat+st_point_lat)/2D;
        
        
        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        java.awt.Polygon outline_shape = new java.awt.Polygon();
        
        
                
        System.out.println(center_lon+", "+center_lat);
        Polygon square = new Polygon(points);

        fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x88FF0000, null);//red

        // create the symbols and renderer
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFFFFF00, lineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
        
        
        
        Scalebar scaleBar = new Scalebar(mapView);
        // specify skin style for the scale bar
        scaleBar.setSkinStyle(Scalebar.SkinStyle.GRADUATED_LINE);

        // set the unit system (default is METRIC)
        scaleBar.setUnitSystem(UnitSystem.METRIC);
       
        // show current map scale in a label within the control panel
        
        
        stackPane.getChildren().addAll(mapView, scaleBar);
        
        load_event();
        
    }
    
    private void load_event() {
        
            List<Feature> sc_features = new ArrayList<>();
            List<Field> sc_pointFields = new ArrayList<>();
            List<Feature> seg_line_features = new ArrayList<>();
            List<Field> seg_line_pointFields = new ArrayList<>();
            List<Feature> path_features = new ArrayList<>();
            List<Field> path_pointFields = new ArrayList<>();
           
            //System.out.println(sheet.getPhysicalNumberOfRows());
                       
            /*
            PointCollection seg_line_Points = new PointCollection(SpatialReferences.getWgs84());
            seg_line_Points.add(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue());
            seg_line_Points.add(st_point);
            Polyline seg_line = new Polyline(seg_line_Points);
            Map<String, Object> seg_line_att = Maps.newHashMap();
            Graphic seg_line_Graphic = new Graphic(seg_line, seg_line_Symbol);
            graphicsOverlay_t_over1.getGraphics().add(seg_line_Graphic);
            seg_line_features.add(seg_line_pointsTable.createFeature(seg_line_att, seg_line));
                    
                    
                    
                
                    
                    
                                        
                    //get station
                    Station stt_temp = Utils.get_StationList().stream().filter(st -> st.getStationName().equals(fft.getStationCode())).findAny().orElse(null);
                    //check sub st name
                    String st_vs30 = "0";
                    if (stt_temp == null) {
                        stt_temp = Utils.get_StationList().stream().filter(stt -> stt.getSubStationName().equals(fft.getStationCode())).findAny().orElse(null);
                        if (stt_temp != null) {
                            st_vs30 = stt_temp.getVs30();
                        }
                    } else {
                        st_vs30 = stt_temp.getVs30();
                    }
                    
                    if (stt_temp != null) {
                        //draw ep_center
                        //System.out.println(fft.getSeismic_StartTime());
                        Seismic sc = Utils.get_seismic_map().get(format.parse(fft.getSeismic_StartTime()));
                        //epic center
                        SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF9670, 5f);
                        
                        //seg line
                        SimpleLineSymbol seg_line_Symbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, 0xFF63AFFF, 2f);
                        seg_line_Symbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.NONE);
                        SimpleRenderer seg_line_renderer = new SimpleRenderer(seg_line_Symbol);
                        //station
                        SimpleMarkerSymbol st_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFC75AE8, 7f);
                        st_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF666666, 2f));

                        if (station_print_set_less1.add(stt_temp.getStationName())) {
                            Map<String, Object> attr = Maps.newHashMap();
                            attr.put(format.format(sc.getStartTime()), "Station");
                            Point point = new Point(Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()) , SpatialReferences.getWgs84());
                            TextSymbol txtSymbol = new TextSymbol(10F, "    "+stt_temp.getStationName(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                            Graphic gr = new Graphic(point, txtSymbol);
                            Graphic pointGraphic = new Graphic(point, st_pointSymbol);
                            graphicsOverlay_t_less1.getGraphics().add(pointGraphic);
                            graphicsOverlay_t_less1.getGraphics().add(gr);
                        }
                        
                        SimpleRenderer sc_point_renderer = new SimpleRenderer(sc_pointSymbol);
                        Map<String, Object> attr = Maps.newHashMap();
                        attr.put(format.format(sc.getStartTime()), "Epic Center");
                        Point point = new Point(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue() , SpatialReferences.getWgs84());

                        Graphic pointGraphic = new Graphic(point, sc_pointSymbol);
                        graphicsOverlay_t_less1.getGraphics().add(pointGraphic);
                        
                        
                        
                        //add path
                        Map<String, Object> path_att = Maps.newHashMap();
                        Point st_point = new Point(Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()) , SpatialReferences.getWgs84());

                        PointCollection seg_line_Points = new PointCollection(SpatialReferences.getWgs84());
                        seg_line_Points.add(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue());
                        seg_line_Points.add(st_point);
                        Polyline seg_line = new Polyline(seg_line_Points);
                        Map<String, Object> seg_line_att = Maps.newHashMap();
                        Graphic seg_line_Graphic = new Graphic(seg_line, seg_line_Symbol);
                        graphicsOverlay_t_less1.getGraphics().add(seg_line_Graphic);
                                        
                }
            }
        //logs.appendText("Add " + path.toString() + ". " + Utils.get_filequeue().size() + " files in queue.\n");
        //System.out.println("Add " + path.toString() + ". " + Utils.get_filequeue().size() + " files in queue.\n");
        */ 
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
