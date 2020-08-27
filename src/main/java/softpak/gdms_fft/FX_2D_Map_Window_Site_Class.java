/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.json.JSONObject;
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
public class FX_2D_Map_Window_Site_Class extends Application {
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
    Queue<org.locationtech.jts.geom.Coordinate> inner_coords = Queues.newConcurrentLinkedQueue();
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
    double min_lat, max_lat, min_lon , max_lon, step_dist;
    
    public FX_2D_Map_Window_Site_Class(Queue<org.locationtech.jts.geom.Coordinate> inner_coords, double step_dist) {
        this.inner_coords = inner_coords;
        this.step_dist = step_dist;
    }
    
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
    
    private SimpleFillSymbol fillSymbol;
    private List<SimpleLineSymbol> lineSymbols;
    
    @Override
    public void start(Stage stage) throws Exception {
        mainController = MainApp.loader.getController();
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
        
        // creates a square from four points
        /*
        File file = new File("H:\\論文工具\\GDMS_FFT\\gadm36_TWN_shp\\gadm36_TWN_0.shp");
        Map<String, Object> shp_map = new HashMap<>();
        shp_map.put("url", file.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(shp_map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        
        
        org.geotools.feature.FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                org.locationtech.jts.geom.Geometry g = (org.locationtech.jts.geom.Geometry)feature.getDefaultGeometry();
                
                org.locationtech.jts.geom.Coordinate[] coords = g.getCoordinates();
                for (int c = 0;c < coords.length; c++) {
                    //System.out.println(coords[c].x+", "+ coords[c].y);
                    points.add(coords[c].x, coords[c].y);
                }
                System.out.println(coords.length);
            }
        }*/
        double edge_dist = this.step_dist / 2D;
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        this.inner_coords.forEach(ic -> {
            PointCollection points = new PointCollection(SpatialReferences.getWgs84());
            double Vs30_pre = get_kriging_result(ic.x, ic.y);
            //System.out.println(ic.x+", "+ic.y);
            points.add(ic.x-edge_dist, ic.y-edge_dist);
            points.add(ic.x-edge_dist, ic.y+edge_dist);
            points.add(ic.x+edge_dist, ic.y+edge_dist);
            points.add(ic.x+edge_dist, ic.y-edge_dist);
            
            Polygon square = new Polygon(points);

            Site s_temp = new Site(ic.y, ic.x, Vs30_pre);
            // transparent red (0x88FF0000) color symbol
            //private int hexRed = 0xFFFF0000;
            //private int hexGreen = 0xFF00FF00;
            //private int hexBlue = 0xFF0000FF;
            switch (s_temp.get_site_class()) {
                case 1:
                    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x8800FF00, null);//green
                    break;
                case 2:
                    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x880000FF, null);//blue
                    break;
                case 3:
                    fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x88FF0000, null);//red
                    break;
                default:
                    break;
            }
            // renders graphics to the GeoView
            graphicsOverlay.getGraphics().add(new Graphic(square, fillSymbol));
        
        });
        
        
        

        //createLineSymbols();
        
        
        // create a shapefile feature table from the local data
        File shapefile = new File("H:\\論文工具\\GDMS_FFT\\gadm36_TWN_shp\\gadm36_TWN_0.shp");
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shapefile.getAbsolutePath());
        
        // use the shapefile feature table to create a feature layer
        FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);

        featureLayer.addDoneLoadingListener(() -> {
            if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                // zoom to the feature layer's extent
                mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, featureLayer.getLoadError().getMessage());
                alert.show();
            }
        });

        // add the feature layer to the map
        map.getOperationalLayers().add(featureLayer);

        // create the symbols and renderer
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFFFFF00, lineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
        
        // create a toggle button to switch between renderers
        ToggleButton symbolizeButton = new ToggleButton("Toggle Symbology");
        symbolizeButton.setOnAction(e -> {
            if (symbolizeButton.isSelected()) {
              featureLayer.setRenderer(renderer);
            } else {
              // switch back to the default renderer
              featureLayer.resetRenderer();
            }
        });
        
        Scalebar scaleBar = new Scalebar(mapView);
        // specify skin style for the scale bar
        scaleBar.setSkinStyle(Scalebar.SkinStyle.GRADUATED_LINE);

        // set the unit system (default is METRIC)
        scaleBar.setUnitSystem(UnitSystem.METRIC);

        // to enhance visibility of the scale bar, by making background transparent
        Color transparentWhite = new Color(1, 1, 1, 0.7);
        scaleBar.setBackground(new Background(new BackgroundFill(transparentWhite, new CornerRadii(5), Insets.EMPTY)));
        // add the map view and scale bar to stack pane
        // set position of scale bar
        StackPane.setAlignment(scaleBar, Pos.BOTTOM_LEFT);
        // give padding to scale bar
        StackPane.setMargin(scaleBar, new Insets(0, 0, 50, 50));
        
        GridPane iconbox = new GridPane();
        //controlsVBox.getStyleClass().add("panel-region");
        iconbox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
        iconbox.setHgap(2);
        iconbox.setVgap(5);
        iconbox.setPadding(new Insets(10.0));
        iconbox.setMaxSize(180, 85);
        
        Rectangle rect_g = new Rectangle(0, 0, 20, 20);
        Color tg = new Color(0, 1, 0, 0.53);
        rect_g.setFill(tg);
        iconbox.add(rect_g, 0, 0);
        
        Rectangle rect_b = new Rectangle(0, 0, 20, 20);
        Color tb = new Color(0, 0, 1, 0.53);
        rect_b.setFill(tb);
        iconbox.add(rect_b, 0, 1);
        
        Rectangle rect_r = new Rectangle(0, 0, 20, 20);
        Color tr = new Color(1, 0, 0, 0.53);
        rect_r.setFill(tr);
        iconbox.add(rect_r, 0, 2);
        
        // create checkboxes for toggling the sublayer visibility manually
        Label label_site_class_1 = new Label(" Site Class I");
        label_site_class_1.setTextFill(Color.WHITE);
        iconbox.add(label_site_class_1, 1, 0);
        
        Label label_site_class_2 = new Label(" Site Class II");
        label_site_class_2.setTextFill(Color.WHITE);
        iconbox.add(label_site_class_2, 1, 1);
        
        Label label_site_class_3 = new Label(" Site Class III");
        label_site_class_3.setTextFill(Color.WHITE);
        iconbox.add(label_site_class_3, 1, 2);
                
        
        
        // show current map scale in a label within the control panel
        Label currentMapScaleLabel = new Label();
        currentMapScaleLabel.setTextFill(Color.WHITE);
        mapView.addMapScaleChangedListener(mapScaleChangedEvent ->
                currentMapScaleLabel.setText("Scale: 1:" + Math.round(mapView.getMapScale()))
        );
        iconbox.add(currentMapScaleLabel, 0, 3 , 5, 5);
        StackPane.setAlignment(iconbox, Pos.TOP_LEFT);
        StackPane.setMargin(iconbox, new Insets(60, 0, 0, 20));
        
        
        stackPane.getChildren().addAll(mapView, scaleBar, iconbox);
        
        
    }
    
    private void createLineSymbols() {

        lineSymbols = new ArrayList<>();

        // solid blue (0xFF0000FF) simple line symbol
        SimpleLineSymbol blueLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 3);
        lineSymbols.add(blueLineSymbol);

        // solid green (0xFF00FF00) simple line symbol
        SimpleLineSymbol greenLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF00FF00, 3);
        lineSymbols.add(greenLineSymbol);

        // solid red (0xFFFF0000) simple line symbol
        SimpleLineSymbol redLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3);
        lineSymbols.add(redLineSymbol);
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
    
    double distance = 1000*10;//10 km
    double max_distance = 1000*10;//10 km
    Map<String, Interpolation_Result_Obj> result = Maps.newLinkedHashMap();
    private double get_kriging_result(double lon, double lat) {
        //0. draw a 2D predict map
        Kriging kr = new Kriging();
        //variogram model
        String model = "spherical";//spherical,exponential,gaussian
        //System.out.println(model);
        
        Interpolation_Result_Obj ir_temp = new Interpolation_Result_Obj();
        ir_temp.set_StationName("");
        //1. calc distance    


        Map<String, Double> dist_map = Maps.newLinkedHashMap();
        Utils.get_StationMap().entrySet().stream().forEach(st_sample -> {
            GeodeticCalculator geoCalc = new GeodeticCalculator();
            GlobalCoordinates st_p = new GlobalCoordinates(lat, lon);
            GlobalCoordinates st_s = new GlobalCoordinates(Double.valueOf(st_sample.getValue().getLatitude()), Double.valueOf(st_sample.getValue().getLongitude()));

            GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, st_s);
            double temp_distance = geoCurve.getEllipsoidalDistance();
            dist_map.put(st_sample.getValue().getStationName(), temp_distance);
        });
        Map<String, Double> sorted_dist_map = dist_map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        //System.out.println(sorted_dist_map.size()+" sort by dist: " + sorted_dist_map);
        //2. get nearest 30 points or all in 10km range
        int sample_count = 1;//at least 30

        List<Station> train_list = Lists.newLinkedList();

        for (Map.Entry<String, Double> sp_temp : sorted_dist_map.entrySet()) {
            if (sample_count < 31) {//30 samples basic but in 50 km
                if (sp_temp.getValue() <= max_distance || train_list.size() < 10) {
                    Station st_temp = Utils.get_StationMap().get(sp_temp.getKey());
                    train_list.add(st_temp);
                    ir_temp.get_RefStationList().add(st_temp.getStationName());
                }
            } else {//distance in 10km
                if (sp_temp.getValue() <= distance) {
                    Station st_temp = Utils.get_StationMap().get(sp_temp.getKey());
                    train_list.add(st_temp);
                    ir_temp.get_RefStationList().add(st_temp.getStationName());
                } else {
                    break;
                }
            }
            sample_count++;
        }
        /*
        Utils.get_StationList().stream().forEach(st-> {
            if (!st.getStationName().equals(st_predict.getStationName())) {
                train_list.add(st);
            }
        });*/

        //System.out.println(train_list.size()+" stations in train list");
        int real_st_num = train_list.size();
        //int real_st_num = Utils.get_StationList().size();//348 ok, 347 the problem //TAP003, TAP004
        kr.set_station_num(real_st_num);
        double[] value = new double[real_st_num];
        double[] x_lon = new double[real_st_num];
        double[] y_lat = new double[real_st_num];

        for (int c = 0 ; c < real_st_num ; c++) {
            value[c] = Double.valueOf(train_list.get(c).getVs30());
            x_lon[c] = Double.valueOf(train_list.get(c).getLongitude());
            y_lat[c] = Double.valueOf(train_list.get(c).getLatitude());
        }
        kr.set_data(value, x_lon, y_lat, model);
        kr.do_train();
        double vs30_predict = kr.predict(lon, lat);
        ir_temp.set_PredictValue(vs30_predict);
        //double vs30_cur = Double.valueOf(st_predict.getVs30());
        //ir_temp.set_CurrentValue(vs30_cur);
        //double error = vs30_predict-vs30_cur;
        //ir_temp.set_ErrorValue(error);
        //rmse_vs30_val.add(error);//pridict-current
        //double error_percent = 100D*(vs30_predict-vs30_cur)/vs30_cur;
        //ir_temp.set_ErrorPercent(error_percent);
        //rmse_vs30_percent.add(error_percent);
        result.put(ir_temp.get_StationName(), ir_temp);
        
        
        
        //gen data arrays
        //long st =System.currentTimeMillis();
        
        //System.out.println("train complete");
        
       
        /*
        System.out.println("RMSE Vs30(kriging): "+Configs.data_df.format(Math.sqrt(rmse_vs30_val.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_val.size()))+" m/s. "+
                                                 Configs.data_df.format(Math.sqrt(rmse_vs30_percent.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_percent.size()))+" %");
        System.out.println("Average Vs30(kriging): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" m/s. "+
                                                    Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" %");
        System.out.println("Max Vs30(kriging): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" m/s. "+
                                                Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" %");*/
        
        return vs30_predict;
    }
}
