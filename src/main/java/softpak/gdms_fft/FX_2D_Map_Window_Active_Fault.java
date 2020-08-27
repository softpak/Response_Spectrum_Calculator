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
public class FX_2D_Map_Window_Active_Fault extends Application {
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
    
    public FX_2D_Map_Window_Active_Fault() {
        
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
        
        try {Files.walk(Paths.get(Utils.get_MainPath()+"/active_faults").toRealPath())
            .filter(files -> files.toString().endsWith(".shp"))
            .forEach(path -> {
                Utils.add_path_to_filequeue(path.toString());
            });
        } catch (IOException ex) {
            Utils.logger.warn(ex);
        }
        System.out.println(Utils.get_filequeue().size());
                
        
        Utils.get_filequeue().stream().forEach(fp -> {
            try {
                File file = new File(fp);
                int last_pos = fp.indexOf(")");
                int start_pos = 0;
                for (int p = last_pos; p > 0 ; p--) {
                    if (fp.substring(p-1, p).equals("(")) {
                        start_pos = p;
                    }
                }
                //System.out.println(fp);
                String fault_name = fp.substring(start_pos, last_pos);
                System.out.println(fault_name);
                
                //get all points
                Map<String, Object> shp_map = new HashMap<>();
                shp_map.put("url", file.toURI().toURL());
                
                DataStore dataStore = DataStoreFinder.getDataStore(shp_map);
                String typeName = dataStore.getTypeNames()[0];
                
                FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
                Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
                //PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                
                
                Active_Fault af_temp = null;
                PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                for (Active_Fault af:Utils.af_queue) {
                    if (af.getFaultName().equals(fault_name)) {
                        af_temp = af;
                        af.getPointList().forEach(p -> points.add(p.lon_WGS, p.lat_WGS));
                    }
                }
                
                System.out.println(af_temp.center_x+", "+af_temp.center_y);
                Polygon square = new Polygon(points);

                fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x88FF0000, null);//red
                        
                // renders graphics to the GeoView
                if (fp.contains("車籠埔斷層") || fp.contains("池上斷層")) {
                    ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(file.getAbsolutePath());

                    // use the shapefile feature table to create a feature layer
                    FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
                    SimpleRenderer blueRenderer = new SimpleRenderer(fillSymbol);
                    featureLayer.setRenderer(blueRenderer);
                    map.getOperationalLayers().add(featureLayer);
                } else {
                    
                    graphicsOverlay_text.getGraphics().add(new Graphic(square, fillSymbol));
                }
                
                
                TextSymbol txtSymbol = new TextSymbol(12F, fault_name, hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                //a point to define where the text is drawn
                Point pt = new Point(af_temp.center_x, af_temp.center_y, SpatialReferences.getWgs84());
                //create a graphic from the point and symbol
                Graphic gr = new Graphic(pt, txtSymbol);
                
                //add the graphic to the map
                graphicsOverlay_text.getGraphics().add(gr);
                
                /*
                featureLayer.addDoneLoadingListener(() -> {
                    if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                        // zoom to the feature layer's extent
                        //mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, featureLayer.getLoadError().getMessage());
                        alert.show();
                    }
                });*/
                
                
                
                
            }   catch (MalformedURLException ex) {
                Logger.getLogger(FX_2D_Map_Window_Active_Fault.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FX_2D_Map_Window_Active_Fault.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        });
        File shapefile = new File(Utils.get_MainPath()+"/gadm36_TWN_shp\\gadm36_TWN_0.shp");
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
        map.getOperationalLayers().add(featureLayer);
        featureLayer.setVisible(false);
        

        // create the symbols and renderer
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFFFFF00, lineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
        
        
        
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
        
        CheckBox t_over1sec = new CheckBox("T ≧ 1");
        iconbox.add(t_over1sec, 0, 0);
        t_over1sec.setSelected(true);
        t_over1sec.selectedProperty().addListener(e -> graphicsOverlay_t_over1.setVisible(t_over1sec.isSelected()));
        
        CheckBox t_less1sec = new CheckBox("T < 1");
        iconbox.add(t_less1sec, 0, 1);
        t_less1sec.setSelected(false);
        t_less1sec.selectedProperty().addListener(e -> graphicsOverlay_t_less1.setVisible(t_less1sec.isSelected()));
        graphicsOverlay_t_less1.setVisible(false);
        
        
        
                
        
        
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
        
        load_event();
        
    }
    
    private void load_event() {
        String result_file_path = Utils.get_MainPath()+"/Response_Spectrum_Result1589273305553.xlsx";
        try {
            List<Feature> sc_features = new ArrayList<>();
            List<Field> sc_pointFields = new ArrayList<>();
            List<Feature> seg_line_features = new ArrayList<>();
            List<Field> seg_line_pointFields = new ArrayList<>();
            List<Feature> path_features = new ArrayList<>();
            List<Field> path_pointFields = new ArrayList<>();
            
            sc_pointFields.add(Field.createString("Seismic Event", "Time", 100));
            FeatureCollectionTable sc_pointsTable = new FeatureCollectionTable(sc_pointFields, GeometryType.POINT, SpatialReferences.getWgs84());
            path_pointFields.add(Field.createString("Vs30 point every "+Utils.get_step_distance()+" m", "Meter", 100));
            FeatureCollectionTable path_pointsTable = new FeatureCollectionTable(path_pointFields, GeometryType.POINT, SpatialReferences.getWgs84());
            seg_line_pointFields.add(Field.createString("Segment line every "+Utils.get_step_distance()+" m", "Meter", 100));
            FeatureCollectionTable seg_line_pointsTable = new FeatureCollectionTable(seg_line_pointFields, GeometryType.POLYLINE, SpatialReferences.getWgs84());
            
            //check if is seismic file, read first 10 lines
            XSSFWorkbook SourceBook = new XSSFWorkbook(result_file_path);
            String sheet_name = SourceBook.getSheetAt(0).getSheetName();
            //System.out.println(copy_sheet_name);
            XSSFSheet sheet = SourceBook.getSheet(sheet_name);
            Set<Date> event_print_set_over1 = Sets.newConcurrentHashSet();
            Set<Date> event_print_set_less1 = Sets.newConcurrentHashSet();
            Set<String> station_print_set_over1 = Sets.newConcurrentHashSet();
            Set<String> station_print_set_less1 = Sets.newConcurrentHashSet();
            
            //System.out.println(sheet.getPhysicalNumberOfRows());
            for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
                Row row = sheet.getRow(i);
                //0 file_path
                //2~4 UNE
                if (row.getCell(3).getNumericCellValue() >= 1D || row.getCell(4).getNumericCellValue() >= 1D || row.getCell(5).getNumericCellValue() >= 1D) {//T >= 1 sec
                    String fn = row.getCell(0).toString();
                    /*
                    for (int d = 0; d < 9; d++) {
                        System.out.print(row.getCell(d).toString()+ " ");
                    }
                    System.out.println();*/
                    
                    //read file
                    BufferedReader br = null;
                    String sCurrentLine = null;
                    br = new BufferedReader(new FileReader(fn));
                    FFT fft = new FFT();
                    fft.setSeismic_StartTime_from_path(fn);
                    while((sCurrentLine = br.readLine()) != null) {
                        String temp_str = sCurrentLine;
                        if (temp_str.startsWith("#")) {
                            if (temp_str.contains("Code")) {
                                fft.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                break;
                            }
                        }
                    }
                    
                    br.close();
                                        
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
                        Seismic sc = Utils.get_seismic_map().get(format.parse(fft.getSeismic_StartTime()));
                        //epic center
                        SimpleMarkerSymbol sc_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexOrange, 10f);
                        sc_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2f));
                        //station
                        SimpleMarkerSymbol st_pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFC75AE8, 7f);
                        st_pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 2f));
                        
                        if (station_print_set_over1.add(stt_temp.getStationName())) {
                            Map<String, Object> attr = Maps.newHashMap();
                            attr.put(format.format(sc.getStartTime()), "Station");
                            Point point = new Point(Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()) , SpatialReferences.getWgs84());

                            Graphic pointGraphic = new Graphic(point, st_pointSymbol);
                            graphicsOverlay_t_over1.getGraphics().add(pointGraphic);
                            TextSymbol txtSymbol = new TextSymbol(10F, "    "+stt_temp.getStationName(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                            Graphic gr = new Graphic(point, txtSymbol);
                            graphicsOverlay_t_over1.getGraphics().add(gr);
                        }
                        //seg line
                        SimpleLineSymbol seg_line_Symbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, 0xFF219427, 1.5f);
                        seg_line_Symbol.setMarkerStyle(SimpleLineSymbol.MarkerStyle.ARROW);
                        SimpleRenderer seg_line_renderer = new SimpleRenderer(seg_line_Symbol);
                        

                        SimpleRenderer sc_point_renderer = new SimpleRenderer(sc_pointSymbol);
                        Map<String, Object> attr = Maps.newHashMap();
                        attr.put(format.format(sc.getStartTime()), "Epic Center");
                        Point point = new Point(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue() , SpatialReferences.getWgs84());

                        Graphic pointGraphic = new Graphic(point, sc_pointSymbol);
                        graphicsOverlay_t_over1.getGraphics().add(pointGraphic);
                        sc_features.add(sc_pointsTable.createFeature(attr, point));
                        if (event_print_set_over1.add(sc.getStartTime())) {
                            TextSymbol txtSymbol = new TextSymbol(10F, "    "+fft.getSeismic_StartTime()+", "+sc.getMagnitude().toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                            Graphic gr = new Graphic(point, txtSymbol);
                            graphicsOverlay_t_over1.getGraphics().add(gr);
                        }
                        
                        
                        //add path
                        Map<String, Object> path_att = Maps.newHashMap();
                        Point st_point = new Point(Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()) , SpatialReferences.getWgs84());

                        PointCollection seg_line_Points = new PointCollection(SpatialReferences.getWgs84());
                        seg_line_Points.add(sc.getLongitude().doubleValue(), sc.getLatitude().doubleValue());
                        seg_line_Points.add(st_point);
                        Polyline seg_line = new Polyline(seg_line_Points);
                        Map<String, Object> seg_line_att = Maps.newHashMap();
                        Graphic seg_line_Graphic = new Graphic(seg_line, seg_line_Symbol);
                        graphicsOverlay_t_over1.getGraphics().add(seg_line_Graphic);
                        seg_line_features.add(seg_line_pointsTable.createFeature(seg_line_att, seg_line));
                    }
                    
                    
                } else {//T < 1 sec
                    String fn = row.getCell(0).toString();
                    
                    //read file
                    BufferedReader br = null;
                    String sCurrentLine = null;
                    br = new BufferedReader(new FileReader(fn));
                    FFT fft = new FFT();
                    fft.setSeismic_StartTime_from_path(fn);
                    //System.out.println(fn);
                    while((sCurrentLine = br.readLine()) != null) {
                        String temp_str = sCurrentLine;
                        if (temp_str.startsWith("#")) {
                            if (temp_str.contains("Code")) {
                                fft.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                break;
                            }
                        }
                    }
                    
                    br.close();
                                        
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
                        sc_features.add(sc_pointsTable.createFeature(attr, point));
                        if (event_print_set_less1.add(sc.getStartTime())) {
                            TextSymbol txtSymbol = new TextSymbol(10F, "    "+fft.getSeismic_StartTime()+", "+sc.getMagnitude().toString(), hexBlue,  TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
                            Graphic gr = new Graphic(point, txtSymbol);
                            graphicsOverlay_t_less1.getGraphics().add(gr);
                        }
                        
                        
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
                        seg_line_features.add(seg_line_pointsTable.createFeature(seg_line_att, seg_line));
                    }
                    
                }
            }

        } catch (IOException | ParseException ex) {
            Logger.getLogger(FX_2D_Map_Window_Active_Fault.class.getName()).log(Level.SEVERE, null, ex);
        }
                    //System.out.println("Add " + path.toString() + ". " + " files in queue.\n");
                    
                
        //logs.appendText("Add " + path.toString() + ". " + Utils.get_filequeue().size() + " files in queue.\n");
        //System.out.println("Add " + path.toString() + ". " + Utils.get_filequeue().size() + " files in queue.\n");
                
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
