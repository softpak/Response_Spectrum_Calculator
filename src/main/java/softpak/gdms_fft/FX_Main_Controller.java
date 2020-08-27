package softpak.gdms_fft;

import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import java.security.NoSuchAlgorithmException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCurve;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.json.JSONObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import tools.CheckEquipmentErrors;
import tools.Configs;
import tools.Kriging;
import tools.OP_Engine;
import tools.OP_Task;
import tools.TimeMeasure;
import tools.Interpolation_Test;
import tools.LinearRegressionModel;
import tools.PeakFinder;
import tools.RegressionModel;
import tools.Relations;
import tools.Response_Spectrum_Calculator;
import tools.T0_Calculator;
import tools.Utils;
import static tools.Utils.logger;

public class FX_Main_Controller implements Initializable {

    long st, et;
    DecimalFormat df = new DecimalFormat("#,#0.0");
    private Project proj = new Project();//a default project
    //int mission_size = 0;
    //save snapshot every 15mins if modified
    @FXML private GridPane gp;
    @FXML private Button search_button;
    @FXML private MenuBar menu_bar;
    @FXML private MenuItem menuitem_about;
    @FXML private ComboBox sort_list;
    @FXML private ComboBox logic_list;
    @FXML private ComboBox y_numerator;
    @FXML private ComboBox y_denominator;
    @FXML private TextArea logs_textarea;
    @FXML private Label label_status;
    @FXML private Label sc_result_label;
    @FXML private TextField sc_val;
    @FXML private TextField y_numerator_power;
    @FXML private TextField y_denominator_power;
    String sort_list_pattern;
    ContextMenu main_contextMenu;
    private HashSet<Integer> treetable_selectedInd = new HashSet<>();
    private boolean _update = false;
    private int _lastSelectedIndex = -1;
    private int _previousSelectedIndex = -1;

    //String sort_list_spchar_pattern;
    //@FXML private CheckMenuItem detrend_checkitem_least_square;
    //@FXML private CheckMenuItem detrend_checkitem_del_mean;
    
    @FXML private TreeTableView fft_treetable;
    @FXML private TreeTableColumn<FFT_SnapShot, Boolean> select_box;
    //@FXML private TreeTableColumn<FFT_SnapShot, String> ttc_station;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_station;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_starttime;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_magnitude;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_U20;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_N20;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_E20;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_U;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_N;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_E;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_dof;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_vs30;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_gl;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_z10;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_k;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_ed;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_long;
    @FXML private TreeTableColumn<FFT_SnapShot, String> ttc_lat;
    private TreeItem<FFT_SnapShot> table_root;
    private List<TreeItem> select_treeitem = Lists.newArrayList();
    //private CheckBox select_CheckBox;
    //numerator
    //denominator
    
    private boolean y_cb_pga_selected = false;
    private boolean y_cb_hz_selected = false;
    private boolean y_cb_ml_selected = false;
    private boolean y_cb_vs30_selected = false;
    private boolean y_cb_dof_selected = false;
    private boolean y_cb_ed_selected = false;
    
    SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
    private boolean x_cb_pga_selected = false;
    private boolean x_cb_hz_selected = false;
    private boolean x_cb_ml_selected = false;
    private boolean x_cb_vs30_selected = false;
    private boolean x_cb_dof_selected = false;
    private boolean x_cb_ed_selected = false;
    private boolean x_cb_st_selected = false;
    private boolean x_cb_t_selected = false;
    
    @FXML
    private Button chartbystation_button;
    
    @FXML
    private void front_rear_spectrum_ButtonAction(ActionEvent event) throws Exception {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            process_station_data_into_db(dir.getCanonicalPath(), 0);//insert
        } else {
            label_status.setText("idle");
        }
        String path = dir.getAbsolutePath();
        
        XSSFWorkbook SourceBook = new XSSFWorkbook(path);
        String sheet_name = SourceBook.getSheetAt(0).getSheetName();
        //System.out.println(copy_sheet_name);
        XSSFSheet sheet = SourceBook.getSheet(sheet_name);
        List<String[]> result_list = Lists.newLinkedList();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
            Row row = sheet.getRow(i);
            //lc++;
            int cell_num = 14;
            String[] cell_string = new String[14];

            for (int j = 0; j < cell_num; j++) {
                cell_string[j] = row.getCell(j).toString();
            }
            result_list.add(cell_string);
            //System.out.println();
            
        }
        //gen station nem set
        Set<String> st_name_set = Sets.newLinkedHashSet();
        result_list.forEach(rs -> st_name_set.add(rs[3]));
        Map<String, List<String[]>> data_map = Maps.newConcurrentMap();//station, time
        
        
        //String[] cell_head = {"Path", "magnitude", "Date", "Station", "RS_U", "RS_N", "RS_E", "EpicenterDistance", "Fault Dist(Station)", "Fault Dist(Seismic)", "Fault Name(Seismic)", "Seismic Vs30", "Station Vs30"};
        //                       0        1           2       3          4       5       6        7                          8                      9                10                      11             12                        
        //source sort by avg pga
        for (int rc = 0; rc < result_list.size(); rc++) {
            String[] data_temp = result_list.get(rc);
            if (data_map.get(data_temp[3]) == null) {
                List<String[]> data_list = Lists.newLinkedList();
                data_list.add(data_temp);
                data_map.put(data_temp[3], data_list);
            } else {
                data_map.get(data_temp[3]).add(data_temp);
            }
        }
        data_map.entrySet().parallelStream().forEach(dm -> {
            int data_size = dm.getValue().size();
            if (data_size > 19) {//20
                //sort list
                dm.getValue().sort((String[] z1, String[] z2) -> {
                    if (Double.valueOf(z1[13]) > Double.valueOf(z2[13]))
                        return 1;
                    if (Double.valueOf(z1[13]) < Double.valueOf(z2[13]))
                        return -1;
                    return 0;
                });
                //dm.getValue().forEach(data -> System.out.println(Arrays.toString(data)));
                
                
                //Map to linkedlist
                int rear10 = (int) Math.round(data_size*0.1D);//least 2
                int front10 = data_size - rear10;
                T0_Calculator weak_t0c = new T0_Calculator(dm.getValue(), 0, rear10, false);
                weak_t0c.calculate();
                T0_Calculator strong_t0c = new T0_Calculator(dm.getValue(), front10, dm.getValue().size(), true);
                strong_t0c.calculate();
            }
        });
        System.out.println("Done");
        
    }
    
    @FXML
    private void export_pga_ButtonAction(ActionEvent event) throws Exception {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            process_station_data_into_db(dir.getCanonicalPath(), 0);//insert
        } else {
            label_status.setText("idle");
        }
        String path = dir.getAbsolutePath();
        
        XSSFWorkbook SourceBook = new XSSFWorkbook(path);
        String sheet_name = SourceBook.getSheetAt(0).getSheetName();
        //System.out.println(copy_sheet_name);
        XSSFSheet sheet = SourceBook.getSheet(sheet_name);
        List<String[]> result_list = Lists.newLinkedList();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
            Row row = sheet.getRow(i);
            //lc++;
            int cell_num = 13;
            String[] cell_string = new String[13];

            for (int j = 0; j < cell_num; j++) {
                cell_string[j] = row.getCell(j).toString();
            }
            result_list.add(cell_string);
            //System.out.println();
            
        }
        //gen station nem set
        Set<String> st_name_set = Sets.newLinkedHashSet();
        result_list.forEach(rs -> st_name_set.add(rs[3]));
        Map<String, Map<String, String[]>> data_map = Maps.newConcurrentMap();//station, time
        
        
        //String[] cell_head = {"Path", "magnitude", "Date", "Station", "RS_U", "RS_N", "RS_E", "EpicenterDistance", "Fault Dist(Station)", "Fault Dist(Seismic)", "Fault Name(Seismic)", "Seismic Vs30", "Station Vs30"};
        //                       0        1           2       3          4       5       6        7                          8                      9                10                      11             12
        for (int rc = 0; rc < result_list.size(); rc++) {
            String[] data_temp = result_list.get(rc);
            if (data_map.get(data_temp[3]) == null) {
                Map<String, String[]> dtat_temp = Maps.newConcurrentMap();
                dtat_temp.put(data_temp[2], data_temp);
                data_map.put(data_temp[3], dtat_temp);
            } else {
                data_map.get(data_temp[3]).put(data_temp[2], data_temp);
            }
        }
        //String[] cell_head = {"Path", "Station", "PGA_U", "PGA_N", "PGA_E", "AVG_NE_PGA"};
        List<String[]> export_result_list = Lists.newLinkedList();
        data_map.entrySet().parallelStream().forEach(dm -> {
            if (dm.getValue().size() > 0) {//4
            //if (dm.getKey().equals("TAP002")) {
                dm.getValue().entrySet().stream().forEach(data -> {
                try {
                    String[] s = data.getValue();
                    String[] export = new String[6];
                    //calc spectrum
                    BufferedReader f_br = null;
                    String f_sCurrentLine = null;
                    int eff_count = 0;
                    String f_path = s[0];
                    export[0] = f_path;
                    f_br = new BufferedReader(new FileReader(f_path));
                    FFT fft = new FFT();
                    fft.setSeismic_StartTime_from_path(f_path);
                    fft.setFilePath(Paths.get(f_path).getParent().toString());
                    fft.setFileName(Paths.get(f_path).getFileName().toString());
                    while((f_sCurrentLine = f_br.readLine()) != null) {
                        String temp_str = f_sCurrentLine;
                        if (temp_str.startsWith("#")) {
                            if (temp_str.contains("Code")) {
                                eff_count++;
                                fft.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            }
                            if (temp_str.contains("Kind")) {
                                eff_count++;
                                fft.setInstrumentKind(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            }
                            if (temp_str.contains("StartTime")) {
                                eff_count++;
                                String mod = temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim();
                                mod = mod.replace("-", " ");
                                fft.setAccelerometer_StartTime(mod);
                            }
                            if (temp_str.contains("sec")) {
                                eff_count++;
                                fft.setRecordLength(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            }
                            if (temp_str.contains("Hz")) {
                                eff_count++;
                                fft.setSampleRate(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                fft.initDataArray(fft.getRecordLength().multiply(fft.getSampleRate()).intValue(), 4);
                            }
                        } else {//stage 2
                            fft.setArrayO(temp_str);
                        }
                    }
                    f_br.close();
                    if (eff_count >= 5) {
                        try {
                            fft.transpose();
                            fft.calc_SSID();
                            fft.transferDataToFFT();
                        } catch (NoSuchAlgorithmException ex) {
                            Utils.logger.fatal(ex);
                        }
                    }
                    double u_PGA = fft.getPGA_U().doubleValue();
                    double n_PGA = fft.getPGA_N().doubleValue();
                    double e_PGA = fft.getPGA_E().doubleValue();
                    double ne_avg_PGA = (Math.abs(n_PGA) + Math.abs(e_PGA))/2D;
                    export[1] = fft.getStationCode();
                    export[2] = Configs.df.format(u_PGA);
                    export[3] = Configs.df.format(n_PGA);
                    export[4] = Configs.df.format(e_PGA);
                    export[5] = Configs.df.format(ne_avg_PGA);
                    export_result_list.add(export);
                    
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        });
        
        
        
        //export to excel
        XSSFWorkbook export_rs_workbook = new XSSFWorkbook();
        String export_sheet_name = "PGA result";
        String file_name = "PGA_Result"+System.currentTimeMillis();
        int rowCount = 0;
        XSSFSheet export_sheet = export_rs_workbook.createSheet(export_sheet_name);
        //add head
        Row row_head = export_sheet.createRow(rowCount);
        String[] cell_head = {"Path", "Station", "PGA_U", "PGA_N", "PGA_E", "AVG_NE_PGA"};
        for (int rc = 0; rc < cell_head.length; rc++) {
            Cell cell = row_head.createCell(rc);
            cell.setCellValue(cell_head[rc]);
        }
        for (int rc = 0; rc < export_result_list.size(); rc++) {
            try { 
                Row row = export_sheet.createRow(++rowCount);
                String[] data_temp = export_result_list.get(rc);
                String[] cell_string = {data_temp[0], data_temp[1], data_temp[2], data_temp[3], data_temp[4], data_temp[5]};
                if (data_map.get(data_temp[2]) == null) {
                    Map<String, String[]> dtat_temp = Maps.newConcurrentMap();
                    dtat_temp.put(data_temp[1], data_temp);
                    data_map.put(data_temp[2], dtat_temp);
                } else {
                    data_map.get(data_temp[2]).put(data_temp[1], data_temp);
                }
                
                for (int rcc = 0; rcc < cell_string.length; rcc++) {
                    Cell cell = row.createCell(rcc);
                    if (rcc > 1) {//numbers
                        cell_string[rcc] = cell_string[rcc].replace(",", "");
                        cell.setCellValue(Double.valueOf(cell_string[rcc]));
                    } else {//string
                        cell.setCellValue(cell_string[rcc]);
                    }
                }
                try (FileOutputStream outputStream = new FileOutputStream(file_name+".xlsx")) {
                    export_rs_workbook.write(outputStream);
                } catch (FileNotFoundException ex) {
                    Utils.logger.fatal(ex);
                    //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Utils.logger.fatal(ex);
                    //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                }
                //rowCount++;
            } catch (Exception ex) {
                Utils.logger.fatal(ex);
            }
        }
        
        System.out.println("Export PGA Done");
    }
    
    @FXML
    private void check_error_ButtonAction(ActionEvent event) throws Exception {
        CheckEquipmentErrors cee = new CheckEquipmentErrors();
        cee.check();
    }
    
    
    int st_count, row_count;
    
    private double[] calculateSD(List<Double> data) {
        double sum = 0.0, standardDeviation = 0.0;
        
        int length = data.size();
        
        double mean = data.stream().mapToDouble(num -> num).average().getAsDouble();

        for(double num: data) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        double[] result = new double[2];
        result[0] = mean;
        result[1] = Math.sqrt(standardDeviation/length);
        return result;
    }
    
    @FXML
    private void calc_linear_regression_ButtonAction(ActionEvent event) throws Exception {
        //load exist excel file
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            process_station_data_into_db(dir.getCanonicalPath(), 0);//insert
        } else {
            label_status.setText("idle");
        }
        String path = dir.getAbsolutePath();
        XSSFWorkbook SourceBook = new XSSFWorkbook(path);
        String sheet_name = SourceBook.getSheetAt(0).getSheetName();
        //System.out.println(copy_sheet_name);
        XSSFSheet sheet = SourceBook.getSheet(sheet_name);
        List<String[]> result_list = Lists.newLinkedList();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
            Row row = sheet.getRow(i);
            //lc++;
            int cell_num = 13;
            String[] cell_string = new String[13];

            for (int j = 0; j < cell_num; j++) {
                cell_string[j] = row.getCell(j).toString();
            }
            result_list.add(cell_string);
            //System.out.println();
            
        }
        //gen station nem set
        Set<String> st_name_set = Sets.newLinkedHashSet();
        result_list.forEach(rs -> st_name_set.add(rs[3]));
        Map<String, Map<String, String[]>> data_map = Maps.newConcurrentMap();//station, time
        
        
        //String[] cell_head = {"Path", "magnitude", "Date", "Station", "RS_U", "RS_N", "RS_E", "EpicenterDistance", "Fault Dist(Station)", "Fault Dist(Seismic)", "Fault Name(Seismic)", "Seismic Vs30", "Station Vs30"};
        //                       0        1           2       3          4       5       6        7                          8                      9                10                      11             12
        
        for (int rc = 0; rc < result_list.size(); rc++) {
            String[] data_temp = result_list.get(rc);
            if (data_map.get(data_temp[3]) == null) {
                Map<String, String[]> dtat_temp = Maps.newConcurrentMap();
                dtat_temp.put(data_temp[2], data_temp);
                data_map.put(data_temp[3], dtat_temp);
            } else {
                data_map.get(data_temp[3]).put(data_temp[2], data_temp);
            }
        }
        //Set<String> basin_set = Sets.newHashSet("TAP095","TAP005","TAP006","TAP007","TAP010","TAP013","TAP015","TAP017","TAP109","TAP088","TAP022","TAP021","TAP020","TAP100","TAP097","TAP032","TAP115","TAP043","TAP053");
        //Set<String> basin_set = Sets.newHashSet("TCU136","TCU102","TCU101","TCU060","TCU053","TCU100","TCU050","TCU049","TCU057","TCU056","TCU051","TCU054","TCU082","TCU055","TCU133","TCU099","TCU165","TCU134","TCU063","TCU062","TCU075","TCU125");
        //Set<String> basin_set = Sets.newHashSet("TCU073","TCU074");
       
        data_map.entrySet().stream().forEach(dm -> {
            //if (basin_set.contains(dm.getKey())) {
                if (dm.getValue().size() > 4) {//4
                    String st_name = dm.getKey();
                    List<Double> x_list = Lists.newLinkedList();
                    List<Double> y_U_list = Lists.newLinkedList();
                    List<Double> y_N_list = Lists.newLinkedList();
                    List<Double> y_E_list = Lists.newLinkedList();
                    //get magnitude from date
                    dm.getValue().entrySet().stream().forEach(data -> {
                        String[] s = data.getValue();
                        x_list.add(Double.valueOf(s[11]));//
                        //System.out.println(s[7]);
                        y_U_list.add(1D/Double.valueOf(s[4]));
                        y_N_list.add(1D/Double.valueOf(s[5]));
                        y_E_list.add(1D/Double.valueOf(s[6]));
                        //near fault
                        /*
                        if (Double.valueOf(s[1]) >= 7D) {//mg >= 7
                            if (Double.valueOf(s[9]) > 20D) {//<=20
                                x_list.add(Double.valueOf(s[11]));//
                                //System.out.println(s[7]);
                                y_U_list.add(1D/Double.valueOf(s[4]));
                                y_N_list.add(1D/Double.valueOf(s[5]));
                                y_E_list.add(1D/Double.valueOf(s[6]));
                            }
                        }

                        if (Double.valueOf(s[1]) < 7D) {//mg < 7
                            if (Double.valueOf(s[9]) > 10D) {//<=10
                                x_list.add(Double.valueOf(s[11]));//
                                //System.out.println(s[7]);
                                y_U_list.add(1D/Double.valueOf(s[4]));
                                y_N_list.add(1D/Double.valueOf(s[5]));
                                y_E_list.add(1D/Double.valueOf(s[6]));
                            }
                        }*/
                    });
                    int data_size = x_list.size();
                    if (data_size > 4) {//4
                        double[] x = new double[data_size];
                        double[] y_U = new double[data_size];
                        double[] y_N = new double[data_size];
                        double[] y_E = new double[data_size];
                        for (int c = 0; c < data_size; c++) {
                            x[c] = x_list.get(c);
                            y_U[c] = y_U_list.get(c);
                            y_N[c] = y_N_list.get(c);
                            y_E[c] = y_E_list.get(c);
                        }
                        /*
                        double resultU = Utils.getR2(x, y_U);
                        double resultN = Utils.getR2(x, y_N);
                        double resultE = Utils.getR2(x, y_E);*/

                        double[] resultU = calculateSD(y_U_list);
                        double[] resultN = calculateSD(y_N_list);
                        double[] resultE = calculateSD(y_E_list);
                        //System.out.println(st_name+": "+ Configs.df.format(resultU)+", "+Configs.df.format(resultN)+", "+Configs.df.format(resultE));

                        System.out.println(st_name+": "+ Configs.df.format(resultU[0])+", "+Configs.df.format(resultU[1])+": "
                                                        +Configs.df.format(resultN[0])+", "+Configs.df.format(resultN[1])+": "
                                                        +Configs.df.format(resultE[0])+", "+Configs.df.format(resultE[1])+" Vs30: "+ x_list.get(0));
                    }
                }
            //}
        });
        System.out.println("Done");
        List<Station> input_list = Lists.newArrayList();
        st_name_set.stream().forEach(st_name -> {
            Station st_temp = Utils.get_StationMap().get(st_name);
            if (st_temp != null) {
                input_list.add(st_temp);
            }
        });
        FX_2D_Pre_Map_Window map_2d = new FX_2D_Pre_Map_Window(2, input_list);
        map_2d.start(new Stage());
    }
    
    @FXML
    private void calc_dist_between_seismic_and_fault_ButtonAction(ActionEvent event) throws Exception {
        Utils.get_seismic_map().entrySet().stream().forEach(sc -> {
            try {
                Seismic sc_temp = sc.getValue();
                String[] result = Utils.getNearestFault(sc_temp.getLatitude().doubleValue(), sc_temp.getLongitude().doubleValue());
                WGS84coord coord = new WGS84coord(Double.valueOf(result[1]), Double.valueOf(result[0]));
                sc_temp.setNearestActiveFault_coord(coord);
                sc_temp.setNearestActiveFault(result[2]);
                sc_temp.setNearestActiveFault_dist(Double.valueOf(result[3]));
                //update SQL
                String sql_update = "UPDATE Seismic SET Data = ? WHERE StartTime =?";
                PreparedStatement pst_u = Utils.getConnection().prepareStatement(sql_update);
                
                byte[] data = SerializationUtils.serialize(sc_temp);
                pst_u.setBytes(1, data);
                pst_u.setString(2, format.format(sc_temp.getStartTime()));
                System.out.println(format.format(sc_temp.getStartTime()));
                pst_u.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        System.out.println("Done");
        
    }
    
    @FXML
    private void calc_dist_between_station_and_fault_ButtonAction(ActionEvent event) throws Exception {
        //add nearest fault
        Utils.get_StationList().stream().forEach(st -> {
            try {
                String[] result = Utils.getNearestFault(Double.valueOf(st.getLatitude()), Double.valueOf(st.getLongitude()));
                st.setNearestActiveFault_coord(new SimpleStringProperty(result[0]+","+result[1]));
                st.setNearestActiveFault(new SimpleStringProperty(result[2]));
                st.setNearestActiveFault_dist(new SimpleStringProperty(result[3]));
                //update SQL
                String sql_update = "UPDATE Station SET FaultCoord = ? , FaultName = ?, FaultDist = ? WHERE StationName =?";
                PreparedStatement pst_u = Utils.getConnection().prepareStatement(sql_update);
                
                String f_coord = result[0]+","+result[1];
                String f_name = result[2];
                String f_dist = result[3];
                
                pst_u.setString(1, f_coord);
                pst_u.setString(2, f_name);
                pst_u.setString(3, f_dist);
                pst_u.setString(4, st.getStationName());
                
                pst_u.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
    }
    
    @FXML
    private void import_active_fault_data_ButtonAction(ActionEvent event) throws Exception {
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
                
                
                //get all points
                Map<String, Object> shp_map = new HashMap<>();
                shp_map.put("url", file.toURI().toURL());
                
                DataStore dataStore = DataStoreFinder.getDataStore(shp_map);
                String typeName = dataStore.getTypeNames()[0];
                
                FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
                Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
                //PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                
                List<Double> lat_list = Lists.newArrayList();
                List<Double> lon_list = Lists.newArrayList();
                
                List<WGS84coord> points = Lists.newLinkedList();
                
                org.geotools.feature.FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
                try (FeatureIterator<SimpleFeature> features = collection.features()) {
                    while (features.hasNext()) {
                        SimpleFeature feature = features.next();
                        org.locationtech.jts.geom.Geometry g = (org.locationtech.jts.geom.Geometry)feature.getDefaultGeometry();
                        
                        org.locationtech.jts.geom.Coordinate[] coords = g.getCoordinates();
                        for (int c = 0;c < coords.length; c++) {
                            //System.out.println(coords[c].x+", "+ coords[c].y);
                            //points.add(coords[c].x, coords[c].y);
                            WGS84coord wg84c = Utils.Cal_TWD97_To_lonlat(coords[c].x, coords[c].y);
                            lat_list.add(wg84c.lat_WGS);
                            lon_list.add(wg84c.lon_WGS);
                            points.add(wg84c);
                        }
                        //System.out.println(coords.length);
                    }
                }
                double lat_center = lat_list.stream().mapToDouble(lat -> lat).average().getAsDouble();
                double lon_center = lon_list.stream().mapToDouble(lon -> lon).average().getAsDouble();
                Active_Fault af_temp = new Active_Fault(fault_name, points, lon_center, lat_center, fp);
                Utils.af_queue.add(af_temp);
       
            } catch (Exception ex) {
                
            }
        });
        Utils.get_filequeue().clear();
        try {
            this.get_label_status().setText("Saving Active Fault Temp file...");
            FileOutputStream fileOut = new FileOutputStream(Utils.get_MainPath()+"/AF_temp.tmp");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            //dump data into project object
            out.writeObject(Utils.af_queue);
            out.close();
            fileOut.close();
            this.get_label_status().setText("AF Temp file saved.");
        } catch (FileNotFoundException ex) {
            Utils.logger.fatal(ex);
            this.get_label_status().setText("Error occurs while saving AF Temp file.");
        } catch (IOException ex) {
            Utils.logger.fatal(ex);
            this.get_label_status().setText("Error occurs while saving AF　Temp file.");
        }
    }
    
    @FXML
    private void open_2D_active_fault_map_ButtonAction(ActionEvent event) throws Exception {
        FX_2D_Map_Window_Active_Fault activefault = new FX_2D_Map_Window_Active_Fault();
        try {
            activefault.start(new Stage());
        } catch (Exception ex) {
            Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        activefault.setTitle("2D Map - Active Faults");
    }
    
    @FXML
    private void design_spectrum_ButtonAction(ActionEvent event) throws Exception {
        //load exist excel file
        //String path = Utils.get_MainPath()+"/Response_Spectrum_Result1592856743673.xlsx";
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            process_station_data_into_db(dir.getCanonicalPath(), 0);//insert
        } else {
            label_status.setText("idle");
        }
        String path = dir.getAbsolutePath();
        
        XSSFWorkbook SourceBook = new XSSFWorkbook(path);
        String sheet_name = SourceBook.getSheetAt(0).getSheetName();
        //System.out.println(copy_sheet_name);
        XSSFSheet sheet = SourceBook.getSheet(sheet_name);
        List<String[]> result_list = Lists.newLinkedList();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
            Row row = sheet.getRow(i);
            //lc++;
            int cell_num = 13;
            String[] cell_string = new String[13];

            for (int j = 0; j < cell_num; j++) {
                cell_string[j] = row.getCell(j).toString();
            }
            result_list.add(cell_string);
            //System.out.println();
            
        }
        //gen station nem set
        Set<String> st_name_set = Sets.newLinkedHashSet();
        result_list.forEach(rs -> st_name_set.add(rs[3]));
        Map<String, Map<String, String[]>> data_map = Maps.newConcurrentMap();//station, time
        
        
        //String[] cell_head = {"Path", "magnitude", "Date", "Station", "RS_U", "RS_N", "RS_E", "EpicenterDistance", "Fault Dist(Station)", "Fault Dist(Seismic)", "Fault Name(Seismic)", "Seismic Vs30", "Station Vs30"};
        //                       0        1           2       3          4       5       6        7                          8                      9                10                      11             12
        double g = 9806.665D;
        for (int rc = 0; rc < result_list.size(); rc++) {
            String[] data_temp = result_list.get(rc);
            if (data_map.get(data_temp[3]) == null) {
                Map<String, String[]> dtat_temp = Maps.newConcurrentMap();
                dtat_temp.put(data_temp[2], data_temp);
                data_map.put(data_temp[3], dtat_temp);
            } else {
                data_map.get(data_temp[3]).put(data_temp[2], data_temp);
            }
        }
       
        data_map.entrySet().parallelStream().forEach(dm -> {
            if (dm.getValue().size() > 0) {//4
            //if (dm.getKey().equals("TAP002")) {
                try {
                
                    String st_name = dm.getKey();
                    //calc Response Spectrum
                    List<double[]> x_axis = Lists.newLinkedList();
                    List<double[]> y_U_Sa_list = Lists.newLinkedList();
                    List<double[]> y_N_Sa_list = Lists.newLinkedList();
                    List<double[]> y_E_Sa_list = Lists.newLinkedList();
                    List<double[]> y_U_Sv_list = Lists.newLinkedList();
                    List<double[]> y_N_Sv_list = Lists.newLinkedList();
                    List<double[]> y_E_Sv_list = Lists.newLinkedList();
                    
                    dm.getValue().entrySet().stream().forEach(data -> {
                        try {
                            String[] s = data.getValue();
                            //calc spectrum
                            BufferedReader f_br = null;
                            String f_sCurrentLine = null;
                            int eff_count = 0;
                            String f_path = s[0];
                            f_br = new BufferedReader(new FileReader(f_path));
                            FFT fft = new FFT();
                            fft.setSeismic_StartTime_from_path(f_path);
                            fft.setFilePath(Paths.get(f_path).getParent().toString());
                            fft.setFileName(Paths.get(f_path).getFileName().toString());
                            while((f_sCurrentLine = f_br.readLine()) != null) {
                                String temp_str = f_sCurrentLine;
                                if (temp_str.startsWith("#")) {
                                    if (temp_str.contains("Code")) {
                                        eff_count++;
                                        fft.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                    }
                                    if (temp_str.contains("Kind")) {
                                        eff_count++;
                                        fft.setInstrumentKind(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                    }
                                    if (temp_str.contains("StartTime")) {
                                        eff_count++;
                                        String mod = temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim();
                                        mod = mod.replace("-", " ");
                                        fft.setAccelerometer_StartTime(mod);
                                    }
                                    if (temp_str.contains("sec")) {
                                        eff_count++;
                                        fft.setRecordLength(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                    }
                                    if (temp_str.contains("Hz")) {
                                        eff_count++;
                                        fft.setSampleRate(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                        fft.initDataArray(fft.getRecordLength().multiply(fft.getSampleRate()).intValue(), 4);
                                    }
                                } else {//stage 2
                                    fft.setArrayO(temp_str);
                                }
                            }
                            f_br.close();
                            if (eff_count >= 5) {
                                try {
                                    fft.transpose();
                                    fft.calc_SSID();
                                    fft.transferDataToFFT();
                                } catch (NoSuchAlgorithmException ex) {
                                    Utils.logger.fatal(ex);
                                }
                            }
                            double[] rs_u_in = fft.getOriginData_U();
                            double[] rs_n_in = fft.getOriginData_N();
                            double[] rs_e_in = fft.getOriginData_E();

                            for (int i = 0; i < rs_u_in.length;i++) {
                                rs_u_in[i] = rs_u_in[i]*g;
                                rs_n_in[i] = rs_n_in[i]*g;
                                rs_e_in[i] = rs_e_in[i]*g;
                            }
                            double damping_ratio = 5D;//5%
                            double sample_rate = 1D/fft.getSampleRate().doubleValue();// in secs
                            double period = 5;//10 secs
                            Response_Spectrum_Calculator rsc = new Response_Spectrum_Calculator();
                            double[] result_u_Sa = rsc.get_Acc_Response_Spectrum(rs_u_in, sample_rate, damping_ratio, g, period, fft.getRecordLength().doubleValue());
                            double[] result_u_Sv = rsc.get_Velocity_Response_Spectrum();
                            double u_PGV = rsc.getPGV(rs_u_in);
                            //System.out.println(fft.getPGA_U()+"/"+u_PGV);
                            double[] result_n_Sa = rsc.get_Acc_Response_Spectrum(rs_n_in, sample_rate, damping_ratio, g, period, fft.getRecordLength().doubleValue());
                            double[] result_n_Sv = rsc.get_Velocity_Response_Spectrum();
                            double n_PGV = rsc.getPGV(rs_n_in);
                            //System.out.println(fft.getPGA_N()+"/"+n_PGV);
                            double[] result_e_Sa = rsc.get_Acc_Response_Spectrum(rs_e_in, sample_rate, damping_ratio, g, period, fft.getRecordLength().doubleValue());
                            double[] result_e_Sv = rsc.get_Velocity_Response_Spectrum();
                            double e_PGV = rsc.getPGV(rs_e_in);
                            //System.out.println(fft.getPGA_E()+"/"+e_PGV);
                            //regular Ca
                            for (int i = 0; i < result_u_Sa.length;i++) {
                                result_u_Sa[i] = result_u_Sa[i]/Math.abs(fft.getPGA_U().doubleValue());
                                result_n_Sa[i] = result_n_Sa[i]/Math.abs(fft.getPGA_N().doubleValue());
                                result_e_Sa[i] = result_e_Sa[i]/Math.abs(fft.getPGA_E().doubleValue());
                                
                                result_u_Sv[i] = result_u_Sv[i]/u_PGV;
                                result_n_Sv[i] = result_n_Sv[i]/n_PGV;
                                result_e_Sv[i] = result_e_Sv[i]/e_PGV;
                            }
                            //System.out.println(period/sample_rate);
                            //if Ca >=2.5 add in the list
                            
                            y_U_Sa_list.add(result_u_Sa);
                            y_N_Sa_list.add(result_n_Sa);
                            y_E_Sa_list.add(result_e_Sa);
                            
                            y_U_Sv_list.add(result_u_Sv);
                            y_N_Sv_list.add(result_n_Sv);
                            y_E_Sv_list.add(result_e_Sv);
                            
                            x_axis.add(rsc.getT());

                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    //find min period length
                    double T_min_value = 2500;
                    int T_min_pos = 0;// 5/0.004
                    for (int i = 0 ; i < x_axis.size() ; i++) {
                        if (x_axis.get(i).length < T_min_value) {
                            T_min_value = x_axis.get(i).length;
                            T_min_pos = i;
                        }
                    }
                    
                    int data_lenght = x_axis.get(T_min_pos).length;

                    //double[] U_avg = new double[data_lenght];
                    double[] NE_avg_Sa = new double[data_lenght];
                    double[] NE_avg_Sv = new double[data_lenght];
                    
                    double[] y_U_std_list = new double[data_lenght];
                    double[] y_NE_std_list = new double[data_lenght];
                    //double[] y_E_std_list = new double[data_lenght];
                    
                    for (int c = 0; c < data_lenght; c++) {
                        //List<Double> U_data = Lists.newArrayList();
                        List<Double> NE_data_Sa = Lists.newArrayList();
                        List<Double> NE_data_Sv = Lists.newArrayList();
                        for (int ac = 0; ac < y_U_Sa_list.size(); ac++) {
                            //U_data.add(y_U_list.get(ac)[c]);
                            NE_data_Sa.add(y_N_Sa_list.get(ac)[c]);
                            NE_data_Sa.add(y_E_Sa_list.get(ac)[c]);
                            
                            NE_data_Sv.add(y_N_Sv_list.get(ac)[c]);
                            NE_data_Sv.add(y_E_Sv_list.get(ac)[c]);
                        }
                        //U_avg[c] = U_data.parallelStream().mapToDouble(u -> Math.abs(u)).average().getAsDouble();
                        NE_avg_Sa[c] = NE_data_Sa.parallelStream().mapToDouble(ne -> Math.abs(ne)).average().getAsDouble();
                        
                        double[] stddev = calculateSD(NE_data_Sv);
                        NE_avg_Sv[c] = stddev[0]+stddev[1];
                        /*
                        System.out.println(NE_avg_Sv[c]);
                        System.out.println(Arrays.toString(stddev));
                        System.out.println(sv_temp);*/
                        
                    }
                                        
                    //get avg spectrum
                    
                    //calc range
                    List<Double> Sa_range = Lists.newLinkedList();
                    List<Double> Sv_range = Lists.newLinkedList();
                    
                    
                    //find max
                    double ne_acc_max_value = 0;
                    int NE_max_pos = 0;
                    for (int i = 0 ; i < NE_avg_Sa.length ; i++) {
                        if (NE_avg_Sa[i] >= 2.5D) {
                            Sa_range.add(i*x_axis.get(0)[1]);
                        }
                        
                        if (NE_avg_Sv[i] >= 2.5D) {
                            Sv_range.add(i*x_axis.get(0)[1]);
                        }
                        
                        if (NE_avg_Sa[i] > ne_acc_max_value) {
                            ne_acc_max_value = NE_avg_Sa[i];
                            NE_max_pos = i;
                        }
                    }
                    double RS_ne = NE_max_pos*x_axis.get(0)[1];
                            
                    //XYChart acc_chart_u = QuickChart.getChart(st_name+" Design Spectrum: sec", "Period(sec)", "Cv", "U avg Axis", x_axis.get(0), U_avg);
                    
                    //if (Sa_range.size() > 0) {
                        //XYChart acc_chart_ne = QuickChart.getChart(st_name+" Ca: "+Configs.df.format(Sa_range.get(0))+" ~ "+ Configs.df.format(Sa_range.get(Sa_range.size()-1)) +" sec", "Period(sec)", "Sa", "NE avg Axis", x_axis.get(T_min_pos), NE_avg_Sa);
                        XYChart acc_chart_ne = QuickChart.getChart(st_name+" Ca: "+Configs.df.format(ne_acc_max_value)+" times", "Period(sec)", "Sa", "NE avg Axis", x_axis.get(T_min_pos), NE_avg_Sa);
                        acc_chart_ne.getStyler().setXAxisMax(5D);
                        BitmapEncoder.saveBitmap(acc_chart_ne, Utils.get_MainPath()+"/Sa_"+st_name+"_NE.png", BitmapEncoder.BitmapFormat.PNG);
                    //}
                    
                    //log T
                    double[] time = x_axis.get(T_min_pos);
                    int low_limit_pos = 0;
                    double low_limit_val = 0;
                    int pgv_pos = 0;
                    double pgv_val = 0;
                    
                                                
                    
                    double step = x_axis.get(T_min_pos)[1];
                    //System.out.println(step);
                    for (int i = 0 ; i < NE_avg_Sv.length ; i++) {
                        /*
                        if (x_axis.get(T_min_pos)[i] - 0.01D <= step) {
                            leg_sa_start = i;
                        }
                        
                        if (x_axis.get(T_min_pos)[i] - 0.3D <= step) {
                            leg_03 = i;
                        }
                        if (x_axis.get(T_min_pos)[i] - 1D <= step) {
                            leg_10 = i;
                        }*/
                        
                        
                        if (NE_avg_Sv[i] > 0) {
                            double log_temp = Math.log10(NE_avg_Sv[i]);
                            /*
                            if (log_temp < 0) {
                                log_temp = 0;
                            }*/
                            NE_avg_Sv[i] = log_temp;
                        }
                        if (time[i] > 0) {
                            double log_temp = Math.log10(time[i]);
                            /*
                            if (log_temp < 0) {
                                log_temp = 0;
                            }*/
                            time[i] = log_temp;
                        }
                        //find min
                        if (NE_avg_Sv[i] < low_limit_val) {
                            low_limit_val = NE_avg_Sv[i];
                            low_limit_pos = i;
                        }
                        if (NE_avg_Sv[i] > pgv_val) {
                            pgv_val = NE_avg_Sv[i];
                            pgv_pos = i;
                        }
                    }
                    //
                    //find peak
                    PeakFinder pf = new PeakFinder(x_axis.get(T_min_pos), NE_avg_Sv);
                    pf.calc_peak_pos();
                    int leg_sa_start = 0;
                    int leg_sa_end = 0;
                    if (pf.get_peak_pos().size() > 1) {
                        leg_sa_start = pf.get_peak_pos().get(0);//first peak
                        leg_sa_end = pf.get_peak_pos().get(Math.min(1, Math.max(1, pf.get_peak_pos().size()/5)));
                        if (leg_sa_start >= leg_sa_end) {
                            leg_sa_start = 2;//0.01 sec
                        }
                    } else {
                        leg_sa_start = 2;
                        leg_sa_end = pf.get_peak_pos().get(0);
                    }
                    
                    int leg_sv_start = 0;
                    int leg_sv_end = 0;
                    Map<Integer, Double> peak_map = Maps.newLinkedHashMap();
                    for (int c = 0; c < pf.get_peak_pos().size(); c++) {
                        int pos = pf.get_peak_pos().get(c);
                        peak_map.put(pos, NE_avg_Sv[pos]);
                    }
                    //sor map by value
                    Map<Integer, Double> sv_peak_map = peak_map.entrySet().stream().sorted(comparingByValue(Comparator.reverseOrder())).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                    List<Integer> peak_list = Lists.newLinkedList();
                    int count = 0;
                    int leg = Math.max((int)(sv_peak_map.size()*0.4D),2);
                    //System.out.println(leg);
                    for (Map.Entry<Integer, Double> entry : sv_peak_map.entrySet()) {
                        if (count == leg) {
                            break;
                        } else {//2~750
                            if (entry.getKey() > 2 && entry.getKey() < 750) {
                                peak_list.add(entry.getKey());
                                //System.out.println(entry.getKey());
                                count ++;
                            }
                        }
                    }
                    int sv_peak_max_pos = peak_list.get(0);
                    Collections.sort(peak_list);
                    //System.out.println(peak_list.size());
                    int mid_pos = peak_list.indexOf(sv_peak_max_pos);
                    if (peak_list.size() >= 3) {
                        leg_sv_start = peak_list.get(0);
                        leg_sv_end = peak_list.get(2);
                        /*
                        if (mid_pos > 0 && mid_pos != peak_list.size()-1) {
                            leg_sv_start = peak_list.get(mid_pos-1);
                            leg_sv_end = peak_list.get(mid_pos+1);
                        }
                        if (mid_pos == 0) {
                            leg_sv_start = peak_list.get(mid_pos);
                            leg_sv_end = peak_list.get(mid_pos+2);
                        }
                        if (mid_pos == peak_list.size()-1) {
                            leg_sv_start = peak_list.get(mid_pos-2);
                            leg_sv_end = peak_list.get(mid_pos);
                        }*/
                    } else if (peak_list.size() > 1 && peak_list.size() < 3){
                        leg_sv_start = peak_list.get(0);
                        leg_sv_end = peak_list.get(1);
                    } else if (peak_list.size() < 2){
                        leg_sv_start = peak_list.get(mid_pos)-20;//0.1 secs
                        leg_sv_end = peak_list.get(mid_pos)+20;//0.1 secs
                    }
                    double slope = Math.abs((NE_avg_Sv[peak_list.get(mid_pos)] - NE_avg_Sv[leg_sv_end])/
                            (Math.log10(peak_list.get(mid_pos)*step)-Math.log10(leg_sv_end*step)));
                    
                    //System.out.println(slope);
                    if (slope >= 0.1D) {
                        leg_sv_start = peak_list.get(mid_pos);
                        leg_sv_end = leg_sv_start+30;
                    }
                    if (slope >= 0.2D) {
                        leg_sv_start = peak_list.get(mid_pos);
                        leg_sv_end = leg_sv_start+20;
                    }
                    if (slope >= 0.3D) {
                        leg_sv_start = peak_list.get(mid_pos);
                        leg_sv_end = leg_sv_start+10;
                    }
                    if (slope >= 0.6D) {
                        leg_sv_start = peak_list.get(mid_pos);
                        leg_sv_end = leg_sv_start+3;
                    }
                    
                    if (leg_sv_start > leg_sv_end) {
                        int temp_s = leg_sv_start;
                        int temp_e = leg_sv_end;
                        leg_sv_start = temp_e;
                        leg_sv_end = temp_s;
                    }
                    
                    /*
                    leg_sv_start = peak_list.get(0);
                    leg_sv_end = peak_list.get(2);*/
                    int peak_leg_sa_start = 2;
                    int peak_leg_sa_end = peak_list.get(mid_pos);
                                        
                    //leg_sa_end = Math.min(leg_sa_end, leg_sv_start);
                    
                    //find min and del forward
                    //from 0.01 sec
                                        
                    double[] sa_time_new_peak = Arrays.copyOfRange(time, peak_leg_sa_start, peak_leg_sa_end);
                    double[] sa_NE_avg_Sv_new_peak = Arrays.copyOfRange(NE_avg_Sv, peak_leg_sa_start, peak_leg_sa_end);
                    
                    double[] sa_time_new_nonepeak = Arrays.copyOfRange(time, leg_sa_start, leg_sa_end);
                    double[] sa_NE_avg_Sv_new_nonepeak = Arrays.copyOfRange(NE_avg_Sv, leg_sa_start, leg_sa_end);
                    
                    //find Cv range
                    double[] sv_time_new = Arrays.copyOfRange(time, leg_sv_start, leg_sv_end);
                    double[] sv_NE_avg_Sv_new = Arrays.copyOfRange(NE_avg_Sv, leg_sv_start, leg_sv_end);
                    
                    //display
                    double[] time_new = Arrays.copyOfRange(time, low_limit_pos, time.length);
                    double[] NE_avg_Sv_new = Arrays.copyOfRange(NE_avg_Sv, low_limit_pos, NE_avg_Sv.length);
                    
                    
                    //calc T0
                    double T0 = 0;
                    double point_y = 0;
                    double nonepeak_constant = 0;
                    double peak_constant = 0;
                    double constant = 0;
                    
                    if (sa_time_new_peak.length > 1) {
                        RegressionModel model_sa = new LinearRegressionModel(sa_time_new_peak, sa_NE_avg_Sv_new_peak);
                        model_sa.fix_slope(1D);
                        model_sa.compute();  
                        double[] coefficients_sa = model_sa.getCoefficients();
                        peak_constant = coefficients_sa[0];
                        //System.out.println(coefficients[0] + "," + coefficients[1]);
                    }
                    if (sa_time_new_nonepeak.length > 1) {
                        RegressionModel model_sa = new LinearRegressionModel(sa_time_new_nonepeak, sa_NE_avg_Sv_new_nonepeak);
                        model_sa.fix_slope(1D);
                        model_sa.compute();  
                        double[] coefficients_sa = model_sa.getCoefficients();
                        nonepeak_constant = coefficients_sa[0];
                        //System.out.println(coefficients[0] + "," + coefficients[1]);
                    }
                    constant = Math.max(peak_constant, nonepeak_constant);
                    
                    if (sv_time_new.length > 1) {
                        RegressionModel model_sv = new LinearRegressionModel(sv_time_new, sv_NE_avg_Sv_new);
                        model_sv.fix_slope(0D);
                        model_sv.compute();
                        double[] coefficients_sv = model_sv.getCoefficients();
                        point_y = coefficients_sv[0];
                        //System.out.println(coefficients_sv[0] + "," + coefficients_sv[1]);
                        T0 = Math.pow(10D,point_y-constant);
                        System.out.println(st_name+": "+Configs.coord_df.format(T0)+" Ca: "+Configs.coord_df.format(ne_acc_max_value)+ " : "+leg_sv_start+", "+leg_sv_end+": "+slope);
                    }
                    
                    //if (Sv_range.size() > 0) {
                        //XYChart vel_chart_ne = QuickChart.getChart(st_name+" Design Spectrum: "+Configs.df.format(Sv_range.get(0))+" ~ "+ Configs.df.format(Sv_range.get(Sv_range.size()-1)) +" sec", "Period(sec)", "Sv", "NE avg Axis", x_axis.get(T_min_pos), NE_avg_Sv);
                        XYChart vel_chart_ne = new XYChart(800, 600);
                        vel_chart_ne.setTitle(st_name+" T0: "+ Configs.df.format(T0)+" sec  y: " + Configs.df.format(point_y)+" const: "+Configs.df.format(constant));
                        vel_chart_ne.setXAxisTitle("Log T(sec)");
                        vel_chart_ne.setYAxisTitle("Log(Sv)");
                        double[] peak_t = new double[sv_peak_map.size()];
                        double[] peak_val = new double[sv_peak_map.size()];
                        int p_count = 0;
                        for (Map.Entry<Integer, Double> entry : sv_peak_map.entrySet()) {
                            if (entry.getKey() < time_new.length) {
                                peak_t[p_count] = time_new[entry.getKey()];
                                peak_val[p_count] = 2;
                                p_count++;
                            }
                        }
                        vel_chart_ne.addSeries("peak", peak_t, peak_val).setMarker(SeriesMarkers.TRIANGLE_UP).setLineStyle(SeriesLines.NONE);
                        
                        vel_chart_ne.addSeries("NE avg Axis", time_new, NE_avg_Sv_new).setMarker(SeriesMarkers.NONE);
                        double[] sv_t = {-2,2};
                        double[] sv_val = {point_y, point_y};
                        vel_chart_ne.addSeries("Log(Sv) y = "+Configs.df.format(point_y), sv_t, sv_val).setMarker(SeriesMarkers.NONE);
                        double max_x = 1.5D;
                        /*
                        if (max_x + constant < point_y) {
                            max_x = point_y+1D;
                        }*/
                        double[] sa_t = {-2,max_x};
                        double[] sa_peak_val = {-2+peak_constant, max_x+peak_constant};
                        double[] sa_nonepeak_val = {-2+nonepeak_constant, max_x+nonepeak_constant};
                        vel_chart_ne.addSeries("Log(Sv) slope = 1 peak", sa_t, sa_peak_val).setMarker(SeriesMarkers.NONE);
                        vel_chart_ne.addSeries("Log(Sv) slope = 1 nonepeak", sa_t, sa_nonepeak_val).setMarker(SeriesMarkers.NONE);
                        
                        vel_chart_ne.getStyler().setXAxisMax(2D);
                        vel_chart_ne.getStyler().setXAxisMin(-2D);
                        BitmapEncoder.saveBitmap(vel_chart_ne, Utils.get_MainPath()+"/Vel_"+st_name+"_NE.png", BitmapEncoder.BitmapFormat.PNG);
                    //}
                    // Show it
                    //acc_chart_u.getStyler().setXAxisMax(5D);
                    
                    

                    //BitmapEncoder.saveBitmap(acc_chart_u, Utils.get_MainPath()+"/"+st_name+"_U.png", BitmapEncoder.BitmapFormat.PNG);
                    
                    
                    

                    //new SwingWrapper(acc_chart_u).displayChart();
                    //new SwingWrapper(acc_chart_ne).displayChart();
                } catch (IOException ex) {
                    Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        System.out.println("Done");
        
        
    }
    
    int stt_count = 0;
    @FXML
    private void response_spectrum_ButtonAction(ActionEvent event) throws Exception {
        Queue<String> path = Queues.newConcurrentLinkedQueue();
        Queue<String> path_rm = Queues.newConcurrentLinkedQueue();
        Queue<String> path_long_period = Queues.newConcurrentLinkedQueue();
        BufferedReader br = null;
        BufferedReader br_rm = null;
        String sCurrentLine = null;
        String sCurrentLine_rm = null;
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/gal_more_or_equal_to_80.txt"));
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/gal_more_or_equal_to_10_65905.txt"));
        br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/recorrect_20200624.txt"));
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/taipei_basin_gal_over_80.txt"));
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/gal_more_or_equal_to_25_23636.txt"));
        
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/recurrect_20200605.txt"));
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/newmark_test.txt"));
        while((sCurrentLine = br.readLine()) != null) {
            path.add(sCurrentLine);
        }
        
        br_rm = new BufferedReader(new FileReader(Utils.get_MainPath()+"/remove_list_80.txt"));
        while((sCurrentLine_rm = br_rm.readLine()) != null) {
            path_rm.add(sCurrentLine_rm);
        }
        br.close();
        
        br_rm.close();
        /*
        System.out.println(path.size()+" records.");
        path.removeAll(path_rm);*/
        System.out.println(path.size()+" records.");
        System.out.println(Utils.get_seismic_map().size());
        
        double g = 9806.665D;
        /*
        double[] elc  = {0.0063,0.00364,0.00099,0.00428,0.00758,0.01087,0.00682,0.00277,-0.00128,0.00368,0.00864,0.0136,0.00727,0.00094,0.0042,0.00221,0.00021,0.00444,0.00867,0.0129,0.01713,-0.00343,-0.024,-0.00992,0.00416,0.00528,0.01653,0.02779,0.03904,0.02449,0.00995,0.00961,0.00926,0.00892,-0.00486,-0.01864,-0.03242,-0.03365,-0.05723,-0.04534,-0.03346,-0.03201,-0.03056,-0.02911,-0.02766,-0.04116,-0.05466,-0.06816,-0.08166,-0.06846,-0.05527,-0.04208,-0.04259,-0.04311,-0.02428,-0.00545,0.01338,0.03221,0.05104,0.06987,0.0887,0.04524,0.00179,-0.04167,-0.08513,-0.12858,-0.17204,-0.12908,-0.08613,-0.08902,-0.09192,-0.09482,-0.09324,-0.09166,-0.09478,-0.09789,-0.12902,-0.07652,-0.02401,0.02849,0.08099,0.1335,0.186,0.2385,0.21993,0.20135,0.18277,0.1642,0.14562,0.16143,0.17725,0.13215,0.08705,0.04196,-0.00314,-0.04824,-0.09334,-0.13843,-0.18353,-0.22863,-0.27372,-0.31882,-0.25024,-0.18166,-0.11309,-0.04451,0.02407,0.09265,0.16123,0.22981,0.29839,0.23197,0.16554,0.09912,0.0327,-0.03372,-0.10014,-0.16656,-0.23299,-0.29941,-0.00421,0.29099,0.2238,0.15662,0.08943,0.02224,-0.04495,0.01834,0.08163,0.14491,0.2082,0.18973,0.17125,0.13759,0.10393,0.07027,0.03661,0.00295,-0.03071,-0.00561,0.01948,0.04458,0.06468,0.08478,0.10487,0.05895,0.01303,-0.03289,-0.07882,-0.03556,0.00771,0.05097,0.01013,-0.03071,-0.07156,-0.1124,-0.15324,-0.11314,-0.07304,-0.03294,0.00715,-0.0635,-0.13415,-0.2048,-0.12482,-0.04485,0.03513,0.1151,0.19508,0.12301,0.05094,-0.02113,-0.0932,-0.02663,0.03995,0.10653,0.17311,0.11283,0.05255,-0.00772,0.01064,0.029,0.04737,0.06573,0.02021,-0.0253,-0.07081,-0.04107,-0.01133,0.00288,0.01709,0.03131,-0.02278,-0.07686,-0.13095,-0.18504,-0.14347,-0.1019,-0.06034,-0.01877,0.0228,-0.00996,-0.04272,-0.02147,-0.00021,0.02104,-0.01459,-0.05022,-0.08585,-0.12148,-0.15711,-0.19274,-0.22837,-0.18145,-0.13453,-0.08761,-0.04069,0.00623,0.05316,0.10008,0.147,0.09754,0.04808,-0.00138,0.05141,0.1042,0.15699,0.20979,0.26258,0.16996,0.07734,-0.01527,-0.10789,-0.20051,-0.06786,0.06479,0.01671,-0.03137,-0.07945,-0.12753,-0.17561,-0.22369,-0.27177,-0.15851,-0.04525,0.06802,0.18128,0.14464,0.108,0.07137,0.03473,0.09666,0.1586,0.22053,0.18296,0.14538,0.1078,0.07023,0.03265,0.06649,0.10033,0.13417,0.10337,0.07257,0.04177,0.01097,-0.01983,0.04438,0.1086,0.17281,0.10416,0.03551,-0.03315,-0.1018,-0.07262,-0.04344,-0.01426,0.01492,-0.02025,-0.05543,-0.0906,-0.12578,-0.16095,-0.19613,-0.14784,-0.09955,-0.05127,-0.00298,-0.01952,-0.03605,-0.05259,-0.04182,-0.03106,-0.02903,-0.02699,0.02515,0.0177,0.02213,0.02656,0.00419,-0.01819,-0.04057,-0.06294,-0.02417,0.0146,0.05337,0.02428,-0.0048,-0.03389,-0.00557,0.02274,0.00679,-0.00915,-0.02509,-0.04103,-0.05698,-0.01826,0.02046,0.00454,-0.01138,-0.00215,0.00708,0.00496,0.00285,0.00074,-0.00534,-0.01141,0.00361,0.01863,0.03365,0.04867,0.0304,0.01213,-0.00614,-0.02441,0.01375,0.01099,0.00823,0.00547,0.00812,0.01077,-0.00692,-0.02461,-0.0423,-0.05999,-0.07768,-0.09538,-0.06209,-0.0288,0.00448,0.03777,0.01773,-0.00231,-0.02235,0.01791,0.05816,0.03738,0.0166,-0.00418,-0.02496,-0.04574,-0.02071,0.00432,0.02935,0.01526,0.01806,0.02086,0.00793,-0.00501,-0.01795,-0.03089,-0.01841,-0.00593,0.00655,-0.02519,-0.05693,-0.04045,-0.02398,-0.0075,0.00897,0.00384,-0.00129,-0.00642,-0.01156,-0.02619,-0.04082,-0.05545,-0.04366,-0.03188,-0.06964,-0.05634,-0.04303,-0.02972,-0.01642,-0.00311,0.0102,0.0235,0.03681,0.05011,0.02436,-0.00139,-0.02714,-0.00309,0.02096,0.04501,0.06906,0.05773,0.0464,0.03507,0.03357,0.03207,0.03057,0.0325,0.03444,0.03637,0.01348,-0.00942,-0.03231,-0.02997,-0.03095,-0.03192,-0.02588,-0.01984,-0.01379,-0.00775,-0.01449,-0.02123,0.01523,0.0517,0.08816,0.12463,0.16109,0.12987,0.09864,0.06741,0.03618,0.00495,0.0042,0.00345,0.00269,-0.05922,-0.12112,-0.18303,-0.12043,-0.05782,0.00479,0.0674,0.13001,0.08373,0.03745,0.06979,0.10213,-0.03517,-0.17247,-0.13763,-0.10278,-0.06794,-0.0331,-0.03647,-0.03984,-0.00517,0.0295,0.06417,0.09883,0.1335,0.05924,-0.01503,-0.08929,-0.16355,-0.06096,0.04164,0.01551,-0.01061,-0.03674,-0.06287,-0.08899,-0.0543,-0.01961,0.01508,0.04977,0.08446,0.05023,0.016,-0.01823,-0.05246,-0.08669,-0.06769,-0.0487,-0.0297,-0.01071,0.00829,-0.00314,0.02966,0.06246,-0.00234,-0.06714,-0.04051,-0.01388,0.01274,0.00805,0.03024,0.05243,0.02351,-0.00541,-0.03432,-0.06324,-0.09215,-0.12107,-0.0845,-0.04794,-0.01137,0.0252,0.06177,0.04028,0.0188,0.04456,0.07032,0.09608,0.12184,0.0635,0.00517,-0.05317,-0.03124,-0.0093,0.01263,0.03457,0.03283,0.03109,0.02935,0.04511,0.06087,0.07663,0.09239,0.05742,0.02245,-0.01252,0.0068,0.02611,0.04543,0.01571,-0.01402,-0.04374,-0.07347,-0.0399,-0.00633,0.02724,0.0608,0.03669,0.01258,-0.01153,-0.03564,-0.00677,0.0221,0.05098,0.07985,0.06915,0.05845,0.04775,0.03706,0.02636,0.05822,0.09009,0.12196,0.10069,0.07943,0.05816,0.03689,0.01563,-0.00564,-0.0269,-0.04817,-0.06944,-0.0907,-0.11197,-0.11521,-0.11846,-0.1217,-0.12494,-0.165,-0.20505,-0.15713,-0.10921,-0.06129,-0.01337,0.03455,0.08247,0.07576,0.06906,0.06236,0.08735,0.11235,0.13734,0.12175,0.10616,0.09057,0.07498,0.08011,0.08524,0.09037,0.06208,0.03378,0.00549,-0.02281,-0.05444,-0.0403,-0.02615,-0.01201,-0.02028,-0.02855,-0.06243,-0.03524,-0.00805,-0.04948,-0.03643,-0.02337,-0.03368,-0.01879,-0.00389,0.011,0.02589,0.01446,0.00303,-0.0084,0.00463,0.01766,0.03069,0.04372,0.02165,-0.00042,-0.02249,-0.04456,-0.03638,-0.02819,-0.02001,-0.01182,-0.02445,-0.03707,-0.04969,-0.05882,-0.06795,-0.07707,-0.0862,-0.09533,-0.06276,-0.03018,0.00239,0.03496,0.04399,0.05301,0.03176,0.01051,-0.01073,-0.03198,-0.05323,0.00186,0.05696,0.01985,-0.01726,-0.05438,-0.01204,0.03031,0.07265,0.11499,0.07237,0.02975,-0.01288,0.01212,0.03711,0.03517,0.03323,0.01853,0.00383,0.00342,-0.02181,-0.04704,-0.07227,-0.0975,-0.12273,-0.08317,-0.04362,-0.00407,0.03549,0.07504,0.1146,0.07769,0.04078,0.00387,0.00284,0.00182,-0.05513,0.04732,0.05223,0.05715,0.06206,0.06698,0.07189,0.02705,-0.01779,-0.06263,-0.10747,-0.15232,-0.12591,-0.0995,-0.07309,-0.04668,-0.02027,0.00614,0.03255,0.00859,-0.01537,-0.03932,-0.06328,-0.03322,-0.00315,0.02691,0.01196,-0.003,0.00335,0.0097,0.01605,0.02239,0.04215,0.06191,0.08167,0.03477,-0.01212,-0.01309,-0.01407,-0.05274,-0.02544,0.00186,0.02916,0.05646,0.08376,0.01754,-0.04869,-0.02074,0.00722,0.03517,-0.00528,-0.04572,-0.08617,-0.0696,-0.05303,-0.03646,-0.01989,-0.00332,0.01325,0.02982,0.01101,-0.00781,-0.02662,-0.00563,0.01536,0.03635,0.05734,0.03159,0.00584,-0.01992,-0.00201,0.01589,-0.01024,-0.03636,-0.06249,-0.0478,-0.03311,-0.04941,-0.0657,-0.082,-0.0498,-0.0176,0.0146,0.0468,0.079,0.0475,0.016,-0.0155,-0.00102,0.01347,0.02795,0.04244,0.05692,0.03781,0.0187,-0.00041,-0.01952,-0.00427,0.01098,0.02623,0.04148,0.01821,-0.00506,-0.00874,-0.03726,-0.06579,-0.026,0.0138,0.05359,0.09338,0.05883,0.02429,-0.01026,-0.0448,-0.01083,-0.01869,-0.02655,-0.03441,-0.02503,-0.01564,-0.00626,-0.01009,-0.01392,0.0149,0.04372,0.03463,0.02098,0.00733,-0.00632,-0.01997,0.00767,0.03532,0.03409,0.03287,0.03164,0.02403,0.01642,0.00982,0.00322,-0.00339,0.02202,-0.01941,-0.06085,-0.10228,-0.07847,-0.05466,-0.03084,-0.00703,0.01678,0.01946,0.02214,0.02483,0.01809,-0.00202,-0.02213,-0.00278,0.01656,0.0359,0.05525,0.07459,0.06203,0.04948,0.03692,-0.00145,0.04599,0.04079,0.03558,0.03037,0.03626,0.04215,0.04803,0.05392,0.04947,0.04502,0.04056,0.03611,0.03166,0.00614,-0.01937,-0.04489,-0.0704,-0.09592,-0.07745,-0.05899,-0.04052,-0.02206,-0.00359,0.01487,0.01005,0.00523,0.00041,-0.00441,-0.00923,-0.01189,-0.01523,-0.01856,-0.0219,-0.00983,0.00224,0.01431,0.00335,-0.0076,-0.01856,-0.00737,0.00383,0.01502,0.02622,0.01016,-0.0059,-0.02196,-0.00121,0.01953,0.04027,0.02826,0.01625,0.00424,0.00196,-0.00031,-0.00258,-0.00486,-0.00713,-0.00941,-0.01168,-0.01396,-0.0175,-0.02104,-0.02458,-0.02813,-0.03167,-0.03521,-0.04205,-0.04889,-0.03559,-0.02229,-0.00899,0.00431,0.01762,0.00714,-0.00334,-0.01383,0.01314,0.04011,0.06708,0.0482,0.02932,0.01043,-0.00845,-0.02733,-0.04621,-0.03155,-0.01688,-0.00222,0.01244,0.02683,0.04121,0.05559,0.03253,0.00946,-0.0136,-0.01432,-0.01504,-0.01576,-0.04209,-0.02685,-0.01161,0.00363,0.01887,0.03411,0.03115,0.02819,0.02917,0.03015,0.03113,0.00388,-0.02337,-0.05062,-0.0382,-0.02579,-0.01337,-0.00095,0.01146,0.02388,0.03629,0.01047,-0.01535,-0.04117,-0.06699,-0.05207,-0.03715,-0.02222,-0.0073,0.00762,0.02254,0.03747,0.04001,0.04256,0.04507,0.04759,0.0501,0.04545,0.0408,0.02876,0.01671,0.00467,-0.00738,-0.00116,0.00506,0.01128,0.0175,-0.00211,-0.02173,-0.04135,-0.06096,-0.08058,-0.06995,-0.05931,-0.04868,-0.03805,-0.02557,-0.0131,-0.00063,0.01185,0.02432,0.0368,0.04927,0.02974,0.01021,-0.00932,-0.02884,-0.04837,-0.0679,-0.04862,-0.02934,-0.01006,0.00922,0.02851,0.04779,0.02456,0.00133,-0.0219,-0.04513,-0.06836,-0.04978,-0.0312,-0.01262,0.00596,0.02453,0.04311,0.06169,0.08027,0.09885,0.06452,0.03019,-0.00414,-0.03848,-0.07281,-0.05999,-0.04717,-0.03435,-0.03231,-0.03028,-0.02824,-0.00396,0.02032,0.00313,-0.01406,-0.03124,-0.04843,-0.06562,-0.05132,-0.03702,-0.02272,-0.00843,0.00587,0.02017,0.02698,0.03379,0.04061,0.04742,0.05423,0.03535,0.01647,0.01622,0.01598,0.01574,0.00747,-0.0008,-0.00907,0.00072,0.01051,0.0203,0.03009,0.03989,0.03478,0.02967,0.02457,0.03075,0.03694,0.04313,0.04931,0.0555,0.06168,-0.00526,-0.0722,-0.06336,-0.05451,-0.04566,-0.03681,-0.03678,-0.03675,-0.03672,-0.01765,0.00143,0.02051,0.03958,0.05866,0.03556,0.01245,-0.01066,-0.03376,-0.05687,-0.04502,-0.03317,-0.02131,-0.00946,0.00239,-0.00208,-0.00654,-0.01101,-0.01548,-0.012,-0.00851,-0.00503,-0.00154,0.00195,0.00051,-0.00092,0.01135,0.02363,0.0359,0.04818,0.06045,0.07273,0.02847,-0.01579,-0.06004,-0.05069,-0.04134,-0.03199,-0.03135,-0.03071,-0.03007,-0.01863,-0.00719,0.00425,0.0157,0.02714,0.03858,0.02975,0.02092,0.02334,0.02576,0.02819,0.03061,0.03304,0.01371,-0.00561,-0.02494,-0.02208,-0.01923,-0.01638,-0.01353,-0.01261,-0.0117,-0.00169,0.00833,0.01834,0.02835,0.03836,0.04838,0.03749,0.0266,0.01571,0.00482,-0.00607,-0.01696,-0.0078,0.00136,0.01052,0.01968,0.02884,-0.00504,-0.03893,-0.02342,-0.00791,0.00759,0.0231,0.00707,-0.00895,-0.02498,-0.041,-0.05703,-0.0292,-0.00137,0.02645,0.05428,0.03587,0.01746,-0.00096,-0.01937,-0.03778,-0.02281,-0.00784,0.00713,0.0221,0.03707,0.05204,0.06701,0.08198,0.03085,-0.02027,-0.0714,-0.12253,-0.08644,-0.05035,-0.01426,0.02183,0.05792,0.094,0.13009,0.03611,-0.05787,-0.04802,-0.03817,-0.02832,-0.01846,-0.00861,-0.03652,-0.06444,-0.06169,-0.05894,-0.05618,-0.06073,-0.06528,-0.04628,-0.02728,-0.00829,0.01071,0.0297,0.03138,0.03306,0.03474,0.03642,0.04574,0.05506,0.06439,0.07371,0.08303,0.03605,-0.01092,-0.0579,-0.04696,-0.03602,-0.02508,-0.01414,-0.03561,-0.05708,-0.07855,-0.06304,-0.04753,-0.03203,-0.01652,-0.00102,0.00922,0.01946,0.0297,0.03993,0.05017,0.06041,0.07065,0.08089,-0.00192,-0.08473,-0.07032,-0.0559,-0.04148,-0.05296,-0.06443,-0.0759,-0.08738,-0.09885,-0.06798,-0.0371,-0.00623,0.02465,0.05553,0.0864,0.11728,0.14815,0.08715,0.02615,-0.03485,-0.09584,-0.071,-0.04616,-0.02132,0.00353,0.02837,0.05321,-0.00469,-0.06258,-0.12048,-0.0996,-0.07872,-0.05784,-0.03696,-0.01608,0.0048,0.02568,0.04656,0.06744,0.08832,0.1092,0.13008,0.10995,0.08982,0.06969,0.04955,0.04006,0.03056,0.02107,0.01158,0.0078,0.00402,0.00024,-0.00354,-0.00732,-0.0111,-0.0078,-0.0045,-0.0012,0.0021,0.0054,-0.00831,-0.02203,-0.03575,-0.04947,-0.06319,-0.05046,-0.03773,-0.025,-0.01227,0.00046,0.00482,0.00919,0.01355,0.01791,0.02228,0.00883,-0.00462,-0.01807,-0.03152,-0.02276,-0.01401,-0.00526,0.0035,0.01225,0.02101,0.01437,0.00773,0.0011,0.00823,0.01537,0.02251,0.01713,0.01175,0.00637,0.01376,0.02114,0.02852,0.03591,0.04329,0.03458,0.02587,0.01715,0.00844,-0.00027,-0.00898,-0.00126,0.00645,0.01417,0.02039,0.02661,0.03283,0.03905,0.04527,0.03639,0.0275,0.01862,0.00974,0.00086,-0.01333,-0.02752,-0.04171,-0.02812,-0.01453,-0.00094,0.01264,0.02623,0.0169,0.00756,-0.00177,-0.01111,-0.02044,-0.02977,-0.03911,-0.02442,-0.00973,0.00496,0.01965,0.03434,0.02054,0.00674,-0.00706,-0.02086,-0.03466,-0.02663,-0.0186,-0.01057,-0.00254,-0.00063,0.00128,0.00319,0.0051,0.00999,0.01488,0.00791,0.00093,-0.00605,0.00342,0.01288,0.02235,0.03181,0.04128,0.02707,0.01287,-0.00134,-0.01554,-0.02975,-0.04395,-0.03612,-0.02828,-0.02044,-0.0126,-0.00476,0.00307,0.01091,0.00984,0.00876,0.00768,0.00661,0.01234,0.01807,0.0238,0.02953,0.03526,0.02784,0.02042,0.013,-0.03415,-0.00628,-0.00621,-0.00615,-0.00609,-0.00602,-0.00596,-0.0059,-0.00583,-0.00577,-0.00571,-0.00564,-0.00558,-0.00552,-0.00545,-0.00539,-0.00532,-0.00526,-0.0052,-0.00513,-0.00507,-0.00501,-0.00494,-0.00488,-0.00482,-0.00475,-0.00469,-0.00463,-0.00456,-0.0045,-0.00444,-0.00437,-0.00431,-0.00425,-0.00418,-0.00412,-0.00406,-0.00399,-0.00393,-0.00387,-0.0038,-0.00374,-0.00368,-0.00361,-0.00355,-0.00349,-0.00342,-0.00336,-0.0033,-0.00323,-0.00317,-0.00311,-0.00304,-0.00298,-0.00292,-0.00285,-0.00279,-0.00273,-0.00266,-0.0026,-0.00254,-0.00247,-0.00241,-0.00235,-0.00228,-0.00222,-0.00216,-0.00209,-0.00203,-0.00197,-0.0019,-0.00184,-0.00178,-0.00171,-0.00165,-0.00158,-0.00152,-0.00146,-0.00139,-0.00133,-0.00127,-0.0012,-0.00114,-0.00108,-0.00101,-0.00095,-0.00089,-0.00082,-0.00076,-0.0007,-0.00063,-0.00057,-0.00051,-0.00044,-0.00038,-0.00032,-0.00025,-0.00019,-0.00013,-6.00E-05,0,0};
        for (int i = 0; i < elc.length;i++) {
            elc[i] = elc[i]*9810D;
        }*/
        List<String[]> result_list = Lists.newLinkedList();
        //Set<String> basin_set = Sets.newHashSet("TAP095","TAP005","TAP006","TAP007","TAP010","TAP013","TAP015","TAP017","TAP109","TAP088","TAP022","TAP021","TAP020","TAP100","TAP097","TAP032","TAP115","TAP043","TAP053");
        //Set<String> basin_set = Sets.newHashSet("MTN118","TAP004","TAP005","TAP087","TAP120","MTN120","TAP003","TAP010","TAP011","TAP016","TAP017","TAP120","TAP037","TAP013","TAP014");
        //Set<String> basin_set = Sets.newHashSet("TAP006","TAP007","TAP008","TAP092","TAP012","TAP102","TAP022","TAP021","TAP020","TAP018","TAP110","TAP024","TAP023","TAP038","TAP043","TAP026","TAP027","TAP028","TAP101","TAP067");
        
        //Configs.Work_Pool.submit(() -> {
            path.parallelStream().forEach(f_path -> {
                try {
                    double vs30_predict = 0;
                    BufferedReader f_br = null;
                    String f_sCurrentLine = null;
                    int eff_count = 0;
                    f_br = new BufferedReader(new FileReader(f_path));
                    FFT fft = new FFT();
                    fft.setSeismic_StartTime_from_path(f_path);
                    fft.setFilePath(Paths.get(f_path).getParent().toString());
                    fft.setFileName(Paths.get(f_path).getFileName().toString());
                    while((f_sCurrentLine = f_br.readLine()) != null) {
                        String temp_str = f_sCurrentLine;
                        if (temp_str.startsWith("#")) {
                            if (temp_str.contains("Code")) {
                                eff_count++;
                                fft.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            }
                            if (temp_str.contains("Kind")) {
                                eff_count++;
                                fft.setInstrumentKind(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            }
                            if (temp_str.contains("StartTime")) {
                                eff_count++;
                                String mod = temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim();
                                mod = mod.replace("-", " ");
                                fft.setAccelerometer_StartTime(mod);
                            }
                            if (temp_str.contains("sec")) {
                                eff_count++;
                                fft.setRecordLength(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            }
                            if (temp_str.contains("Hz")) {
                                eff_count++;
                                fft.setSampleRate(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                                fft.initDataArray(fft.getRecordLength().multiply(fft.getSampleRate()).intValue(), 4);
                            }
                        } else {//stage 2
                            fft.setArrayO(temp_str);
                        }
                    }
                    f_br.close();
                    //raf.close();
                    //if (basin_set.contains(fft.getStationCode())) {//filter basin data
                        if (eff_count >= 5) {
                            try {
                                fft.transpose();
                                fft.calc_SSID();
                                fft.transferDataToFFT();
                            } catch (NoSuchAlgorithmException ex) {
                                Utils.logger.fatal(ex);
                            }
                        }
                        double[] rs_u_in = fft.getOriginData_U();
                        double[] rs_n_in = fft.getOriginData_N();
                        double[] rs_e_in = fft.getOriginData_E();


                        for (int i = 0; i < rs_u_in.length;i++) {
                            rs_u_in[i] = rs_u_in[i]*g;
                            rs_n_in[i] = rs_n_in[i]*g;
                            rs_e_in[i] = rs_e_in[i]*g;
                        }
                        double damping_ratio = 5D;//5%
                        double sample_rate = 1D/fft.getSampleRate().doubleValue();// in secs
                        double period = Math.min(fft.getRecordLength().doubleValue()/2D, 20);//20
                        Response_Spectrum_Calculator rsc = new Response_Spectrum_Calculator();
                        double[] result_u = rsc.get_Acc_Response_Spectrum(rs_u_in, sample_rate, damping_ratio, g, period, fft.getRecordLength().doubleValue());
                        double[] result_n = rsc.get_Acc_Response_Spectrum(rs_n_in, sample_rate, damping_ratio, g, period, fft.getRecordLength().doubleValue());
                        double[] result_e = rsc.get_Acc_Response_Spectrum(rs_e_in, sample_rate, damping_ratio, g, period, fft.getRecordLength().doubleValue());
                        //find max
                        double u_acc_max_value = 0;
                        double n_acc_max_value = 0;
                        double e_acc_max_value = 0;
                        int U_max_pos = 0;
                        int N_max_pos = 0;
                        int E_max_pos = 0;
                        for (int i = 0 ; i < result_u.length ; i++) {
                            if (result_u[i] > u_acc_max_value) {
                                u_acc_max_value = result_u[i];
                                U_max_pos = i;
                            }
                            if (result_n[i] > n_acc_max_value) {
                                n_acc_max_value = result_n[i];
                                N_max_pos = i;
                            }
                            if (result_e[i] > e_acc_max_value) {
                                e_acc_max_value = result_e[i];
                                E_max_pos = i;
                            }
                        }
                        double RS_u = U_max_pos*sample_rate;
                        double RS_n = N_max_pos*sample_rate;
                        double RS_e = E_max_pos*sample_rate;
                        //draw acc and spectrum chart
                        stt_count++;
                        /*
                        XYChart acc_chart_u = QuickChart.getChart(fft.getStationCode()+"_"+fft.getSeismic_StartTime()+" U Max: "+RS_u+" sec", "Period(sec)", "ACC(gal)", "U Axis", rsc.getT(), result_u);
                        XYChart acc_chart_n = QuickChart.getChart(fft.getStationCode()+"_"+fft.getSeismic_StartTime()+" N Max: "+RS_n+" sec", "Period(sec)", "ACC(gal)", "N Axis", rsc.getT(), result_n);
                        XYChart acc_chart_e = QuickChart.getChart(fft.getStationCode()+"_"+fft.getSeismic_StartTime()+" E Max: "+RS_e+" sec", "Period(sec)", "ACC(gal)", "E Axis", rsc.getT(), result_e);
                        // Show it
                        acc_chart_u.getStyler().setXAxisMax(10D);
                        acc_chart_n.getStyler().setXAxisMax(10D);
                        acc_chart_e.getStyler().setXAxisMax(10D);

                        BitmapEncoder.saveBitmap(acc_chart_u, Utils.get_MainPath()+"/"+fft.getFileName()+"_U.png", BitmapEncoder.BitmapFormat.PNG);
                        BitmapEncoder.saveBitmap(acc_chart_n, Utils.get_MainPath()+"/"+fft.getFileName()+"_N.png", BitmapEncoder.BitmapFormat.PNG);
                        BitmapEncoder.saveBitmap(acc_chart_e, Utils.get_MainPath()+"/"+fft.getFileName()+"_E.png", BitmapEncoder.BitmapFormat.PNG);
                        */
                        /*
                        new SwingWrapper(acc_chart_u).displayChart();
                        new SwingWrapper(acc_chart_n).displayChart();
                        new SwingWrapper(acc_chart_e).displayChart();
                        */


                        if (RS_u >= 1D || RS_n >= 1D || RS_e >= 1D) {
                            path_long_period.add(f_path);
                        }


                        //calc distance
                        Date fft_date = format.parse(fft.getSeismic_StartTime());
                        Station stt_temp = Utils.get_StationList().stream().filter(stt -> stt.getStationName().equals(fft.getStationCode())).findAny().orElse(null);
                        //check sub st name
                        String st_vs30 = "0";
                        double st_faultdist = 0;
                        if (stt_temp == null) {
                            stt_temp = Utils.get_StationList().stream().filter(stt -> stt.getSubStationName().equals(fft.getStationCode())).findAny().orElse(null);
                            if (stt_temp != null) {
                                st_vs30 = stt_temp.getVs30();
                                st_faultdist = Double.valueOf(stt_temp.getNearestActiveFault_dist().getValue());
                            }
                        } else {
                            st_vs30 = stt_temp.getVs30();
                            st_faultdist = Double.valueOf(stt_temp.getNearestActiveFault_dist().getValue());
                        }

                        double sc_fault_dist  = 0;
                        String sc_fault_name  = "";
                        String sc_magnitude  = "";
                        if (Utils.get_seismic_map().containsKey(fft_date)) {
                            Seismic sc_temp = Utils.get_seismic_map().get(fft_date);

                            if (stt_temp != null) {
                                Coordinate sc_lon_temp = Coordinate.fromDegrees(sc_temp.getLongitude().doubleValue());
                                Coordinate sc_lat_temp = Coordinate.fromDegrees(sc_temp.getLatitude().doubleValue());
                                com.grum.geocalc.Point sc_p = com.grum.geocalc.Point.at(sc_lat_temp, sc_lon_temp);//seismic

                                Coordinate st_lon_temp = Coordinate.fromDegrees(Double.valueOf(stt_temp.getLongitude()));
                                Coordinate st_lat_temp = Coordinate.fromDegrees(Double.valueOf(stt_temp.getLatitude()));
                                com.grum.geocalc.Point st_p = com.grum.geocalc.Point.at(st_lat_temp, st_lon_temp);//seismic

                                //Number ed_temp = fft_temp.vincentyDistance(sc_p, st_p);
                                Number ed_temp = EarthCalc.vincentyDistance(sc_p, st_p)/1000D;//km
                                fft.setEpicenterDistance(ed_temp.doubleValue());
                            }
                            fft.setMagnitude(sc_temp.getMagnitude().doubleValue());

                            //predict Vs30
                            Kriging kr = new Kriging();
                            //variogram model
                            String model = "spherical";//spherical,exponential,gaussian
                            Map<String, Double> dist_map = Maps.newLinkedHashMap();
                            Utils.get_StationMap().entrySet().stream().forEach(st_sample -> {
                                GeodeticCalculator geoCalc = new GeodeticCalculator();
                                GlobalCoordinates st_p = new GlobalCoordinates(sc_temp.getLatitude().doubleValue(), sc_temp.getLongitude().doubleValue());
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
                                    }
                                } else {//distance in 10km
                                    if (sp_temp.getValue() <= distance) {
                                        Station st_temp = Utils.get_StationMap().get(sp_temp.getKey());
                                        train_list.add(st_temp);
                                    } else {
                                        break;
                                    }
                                }
                                sample_count++;
                            }

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
                            vs30_predict = kr.predict(sc_temp.getLongitude().doubleValue(), sc_temp.getLatitude().doubleValue());
                            sc_fault_dist = sc_temp.getNearestActiveFault_dist().doubleValue();
                            sc_fault_name = sc_temp.getNearestActiveFault();
                            sc_magnitude = sc_temp.getMagnitude().toString();
                        }
                        String[] result = {f_path , sc_magnitude, fft.getSeismic_StartTime(), fft.getStationCode(), Configs.data_df.format(RS_u), Configs.data_df.format(RS_n), Configs.data_df.format(RS_e)
                                , Configs.df.format(fft.getEpicenterDistance()), Configs.df.format(st_faultdist), Configs.df.format(sc_fault_dist), sc_fault_name, Configs.df.format(vs30_predict), st_vs30};
                        result_list.add(result);
                        /*
                        System.out.println(f_path+": " + sc_magnitude+"  "+fft.getSeismic_StartTime()+"  "+ fft.getStationCode()+" U: "+Configs.data_df.format(RS_u)+ "  N: "+Configs.data_df.format(RS_n)+ "  E: "+Configs.data_df.format(RS_e)+"  Period "
                                        + Configs.df.format(fft.getEpicenterDistance()) + " km(epc to station) "+ Configs.df.format(st_faultdist) + " km(fault to station) "+Configs.df.format(sc_fault_dist) + " km(fault to seismic) fault: "+sc_fault_name +"  "
                                        + Configs.df.format(vs30_predict) +" m/s(seicmic) "+ st_vs30 +" m/s(station)");*/
                    //}
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | ParseException ex) {
                    Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        //}).get();
        System.out.println(st_count);
        //sort by station
        Collections.sort(result_list, new Comparator<String[]>() {

            @Override
            public int compare(String[] lhs, String[] rhs) {
                // TODO Auto-generated method stub
                int returning = lhs[2].compareTo(rhs[2]);
                return returning;
            }

        });
        //gen station nem set
        Set<String> st_name_set = Sets.newLinkedHashSet();
        result_list.forEach(rs -> st_name_set.add(rs[2]));
        Map<String, Map<String, String[]>> data_map = Maps.newConcurrentMap();//station, time
        
        //export to excel
        XSSFWorkbook rs_workbook = new XSSFWorkbook();
        String sheet_name = "RS result";
        String file_name = "Response_Spectrum_Result"+System.currentTimeMillis();
        int rowCount = 0;
        XSSFSheet sheet = rs_workbook.createSheet(sheet_name);
        //add head
        Row row_head = sheet.createRow(rowCount);
        String[] cell_head = {"Path", "Magnitude", "Date", "Station", "RS_U", "RS_N", "RS_E", "EpicenterDistance", "Fault Dist(Station)", "Fault Dist(Seismic)", "Fault Name(Seismic)", "Seismic Vs30", "Station Vs30"};
        for (int rc = 0; rc < cell_head.length; rc++) {
            Cell cell = row_head.createCell(rc);
            cell.setCellValue(cell_head[rc]);
        }
        for (int rc = 0; rc < result_list.size(); rc++) {
            try { 
                Row row = sheet.createRow(++rowCount);
                String[] data_temp = result_list.get(rc);
                String[] cell_string = {data_temp[0], data_temp[1], data_temp[2], data_temp[3], data_temp[4], data_temp[5], data_temp[6], data_temp[7], data_temp[8], data_temp[9], data_temp[10], data_temp[11], data_temp[12]};
                if (data_map.get(data_temp[2]) == null) {
                    Map<String, String[]> dtat_temp = Maps.newConcurrentMap();
                    dtat_temp.put(data_temp[1], data_temp);
                    data_map.put(data_temp[2], dtat_temp);
                } else {
                    data_map.get(data_temp[2]).put(data_temp[1], data_temp);
                }
                
                for (int rcc = 0; rcc < cell_string.length; rcc++) {
                    Cell cell = row.createCell(rcc);
                    if (rcc > 3 && rcc != 10) {//numbers
                        cell_string[rcc] = cell_string[rcc].replace(",", "");
                        cell.setCellValue(Double.valueOf(cell_string[rcc]));
                    } else {//string
                        cell.setCellValue(cell_string[rcc]);
                    }
                }
                try (FileOutputStream outputStream = new FileOutputStream(file_name+".xlsx")) {
                    rs_workbook.write(outputStream);
                } catch (FileNotFoundException ex) {
                    Utils.logger.fatal(ex);
                    //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Utils.logger.fatal(ex);
                    //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                }
                //rowCount++;
            } catch (Exception ex) {
                Utils.logger.fatal(ex);
            }
        }
        System.out.println((double)path_long_period.size()/(double)path.size()*100D+"%");
        
        System.out.println("Done");
    }
    @FXML
    private void relations_test_ButtonAction(ActionEvent event) throws Exception {
        Relations rl = new Relations();
        rl.prep_kriging(Utils.get_StationList());
        rl.set_Seismic_Map(Utils.get_seismic_map());
        rl.set_Snp_Selected_Map(Utils.get_fft_snp_map());
        rl.cal_relations();
        
        //rl.relations_with_nearby_seismic();
    }
    
    @FXML
    private void open_2D_site_class_map_ButtonAction(ActionEvent event) throws Exception {
        Site_Class_Result scr = new Site_Class_Result();
        scr.calc_vs30_for_every_site();
        
        
    }
    
    @FXML
    private void kriging_rbf_test_ButtonAction(ActionEvent event) throws Exception {
        Interpolation_Test it = new Interpolation_Test();
        List<Station> st_temp_list = Utils.get_StationList().stream().collect(Collectors.toList());
        /*
        List<Station> st_temp_list = Lists.newArrayList();
        try {
            //Connection con = Utils.getConnection();
            String path = "H:/論文工具/GDMS_FFT/stations.xlsx";
            int lc = 0;
            File station_file = new File(path);
            FileInputStream fis = new FileInputStream(station_file);
            // Finds the workbook instance for XLSX file

            //get ext
            String ext = path.substring(path.lastIndexOf("."));
            Workbook station_WorkBook;
            XSSFWorkbook station_WorkBook_x;
            Sheet sheet = null;
            if (ext.equals(".xls")) {
                station_WorkBook = WorkbookFactory.create(fis);
                sheet = station_WorkBook.getSheetAt(2);
            } else if (ext.equals(".xlsx")) {
                station_WorkBook_x = XSSFWorkbookFactory.createWorkbook(fis);
                sheet = station_WorkBook_x.getSheetAt(2);
            }
            // Return first sheet from the XLSX workbook

            //getPhysicalNumberOfRows
            for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
                Row row = sheet.getRow(i);
                lc = sheet.getPhysicalNumberOfRows()-i-1;
                //lc++;
                StringBuffer sb = new StringBuffer();
                Cell cell;
                if(row != null) {
                    //System.out.println();
                    String stationname = row.getCell(0).toString().substring(0, 6);//also chars in ()
                    String longitude = String.valueOf(row.getCell(1).getNumericCellValue());
                    String latitude = String.valueOf(row.getCell(2).getNumericCellValue());
                    //String ground_level = row.getCell(3).toString();
                    String vs30 = String.valueOf(row.getCell(4).getNumericCellValue());
                    //String z10 = row.getCell(5).toString();
                    //String kappa = row.getCell(6).toString();
                    System.out.println(longitude+", "+latitude+": "+vs30);
                    Station st_temp = new Station(stationname, "", vs30, "", "", "", longitude, latitude);
                    st_temp_list.add(st_temp);
                }
            }
            fis.close();
        } catch (FileNotFoundException ex) {
            Utils.logger.fatal(ex);
        } catch (IOException ex) {
            Utils.logger.fatal(ex);
        } catch (EncryptedDocumentException ex) {
            Utils.logger.fatal(ex);
        } catch (InvalidFormatException ex) {
            Utils.logger.fatal(ex);
        }*/
        
        
        /*
        it.run_test(4, st_temp_list);
        it.run_test(3, st_temp_list);
        it.run_test(2, st_temp_list);*/
        it.run_test(1, st_temp_list, 40D, 200D);
    }
    
    @FXML
    private void rmse_ButtonAction(ActionEvent event) throws Exception {
        st_count = 1;
        row_count = 1;
        
        double min_dist = 1000D*1000D;////over then 200km
        // instantiate the calculator
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        
        //open a new window and run dist calc test
        
        
        
        //select check box
        
        
        //write into excel
        /*
        XSSFWorkbook accuracy_check_WorkBook_x = new XSSFWorkbook();
        Sheet vin_sheet = accuracy_check_WorkBook_x.createSheet("vin_check");
        Sheet har_sheet = accuracy_check_WorkBook_x.createSheet("har_check");
        Sheet gcd_sheet = accuracy_check_WorkBook_x.createSheet("gcd_check");
        String[] columns = {"lat_delta", "lon_delta"};
        Row headerRow_vin = vin_sheet.createRow(0);
        Row headerRow_har = har_sheet.createRow(0);
        Row headerRow_gcd = gcd_sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
          Cell cell = headerRow_vin.createCell(i);
          cell.setCellValue(columns[i]);
        }
        for (int i = 0; i < columns.length; i++) {
          Cell cell = headerRow_har.createCell(i);
          cell.setCellValue(columns[i]);
        }
        for (int i = 0; i < columns.length; i++) {
          Cell cell = headerRow_gcd.createCell(i);
          cell.setCellValue(columns[i]);
        }*/
        
        
        
        //gen chart
        // Create Chart
        XYChart vin_chart = new XYChartBuilder().width(800).height(600).build();
        // Customize Chart
        vin_chart.setXAxisTitle("Latitude");
        vin_chart.setYAxisTitle("Longitude");
        vin_chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        vin_chart.getStyler().setChartTitleVisible(false);
        vin_chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
        vin_chart.getStyler().setMarkerSize(16);
        
        // Series
        List<Double> vin_xData = new LinkedList<>();
        List<Double> vin_yData = new LinkedList<>();
        
        XYChart har_chart = new XYChartBuilder().width(800).height(600).build();
        // Customize Chart
        har_chart.setXAxisTitle("Latitude");
        har_chart.setYAxisTitle("Longitude");
        har_chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        har_chart.getStyler().setChartTitleVisible(false);
        har_chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
        har_chart.getStyler().setMarkerSize(16);
        
        // Series
        List<Double> har_xData = new LinkedList<>();
        List<Double> har_yData = new LinkedList<>();
        
        XYChart gcd_chart = new XYChartBuilder().width(800).height(600).build();
        // Customize Chart
        gcd_chart.setXAxisTitle("Latitude");
        gcd_chart.setYAxisTitle("Longitude");
        gcd_chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        gcd_chart.getStyler().setChartTitleVisible(false);
        gcd_chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
        gcd_chart.getStyler().setMarkerSize(16);
        
        // Series
        List<Double> gcd_xData = new LinkedList<>();
        List<Double> gcd_yData = new LinkedList<>();
        
        
        
        
        
        //cal azimuth and dist between stations and seismic using 3 methods
        List<double[]> rmse_vin_coord = Lists.newArrayList();
        List<double[]> rmse_har_coord = Lists.newArrayList();
        List<double[]> rmse_gcd_coord = Lists.newArrayList();
        
        List<Double> rmse_vin_dist = Lists.newArrayList();
        List<Double> rmse_har_dist = Lists.newArrayList();
        List<Double> rmse_gcd_dist = Lists.newArrayList();
        //List<Double> rmse_hyb_dist = Lists.newArrayList();
        
        Utils.get_StationList().stream().forEach(st -> {
            Coordinate st_lon_temp = Coordinate.fromDegrees(Double.valueOf(st.getLongitude()));
            Coordinate st_lat_temp = Coordinate.fromDegrees(Double.valueOf(st.getLatitude()));
            com.grum.geocalc.Point n_st_p = com.grum.geocalc.Point.at(st_lat_temp, st_lon_temp);//station
            GlobalCoordinates gc_st_p = new GlobalCoordinates(Double.valueOf(st.getLatitude()), Double.valueOf(st.getLongitude()));//for vin station
            
            Utils.get_StationList().stream().forEach(sc -> {
                
                Coordinate sc_lon_temp = Coordinate.fromDegrees(Double.valueOf(sc.getLongitude()));
                Coordinate sc_lat_temp = Coordinate.fromDegrees(Double.valueOf(sc.getLatitude()));
                
                /*
                Coordinate sc_lon_temp = Coordinate.fromDegrees(sc.getValue().getLongitude().doubleValue());
                Coordinate sc_lat_temp = Coordinate.fromDegrees(sc.getValue().getLatitude().doubleValue());*/
                com.grum.geocalc.Point n_sc_p = com.grum.geocalc.Point.at(sc_lat_temp, sc_lon_temp);//seismic
                //vincenty
                //GlobalCoordinates gc_sc_p = new GlobalCoordinates(sc.getValue().getLatitude().doubleValue(), sc.getValue().getLongitude().doubleValue());//for vin station
                GlobalCoordinates gc_sc_p = new GlobalCoordinates(Double.valueOf(sc.getLatitude()), Double.valueOf(sc.getLongitude()));//for vin station
                //double vin_dist = Utils.vincentyDistance(n_st_p, n_sc_p);
                GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, gc_st_p, gc_sc_p);
		double vin_dist = geoCurve.getEllipsoidalDistance();
                double _deg = EarthCalc.bearing(n_st_p, n_sc_p);
                double vin_deg = geoCurve.getAzimuth();
                //System.out.println(vin_dist+","+vin_deg);
                
                if (vin_dist > 0 && vin_dist < min_dist) {
                    // find the destination
                    double[] endBearing = new double[1];
                    GlobalCoordinates dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, gc_st_p, vin_deg, vin_dist, endBearing);
                    //Coordinate dest_lon_temp = Coordinate.fromDegrees(dest.getLongitude());
                    //Coordinate dest_lat_temp = Coordinate.fromDegrees(dest.getLatitude());
                    //com.grum.geocalc.Point dest_sc_p = com.grum.geocalc.Point.at(dest_lat_temp, dest_lon_temp);//seismic
                    GeodeticCurve dest_geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, dest, gc_sc_p);
                    
                    double vin_dist_re = dest_geoCurve.getEllipsoidalDistance();
                    //com.grum.geocalc.Point vin_dist_Point = EarthCalc.pointAt(n_st_p, vin_deg, vin_dist);
                    //double[] vin_Point = {vin_dist_Point.latitude-n_sc_p.latitude, vin_dist_Point.longitude-n_sc_p.longitude};
                    double[] vin_Point = {dest.getLatitude()-n_sc_p.latitude, dest.getLongitude()-n_sc_p.longitude};
                    rmse_vin_coord.add(vin_Point);
                    rmse_vin_dist.add(vin_dist_re);
                    /*
                    //add into row
                    Row row = vin_sheet.createRow(row_count);
                    row.createCell(0).setCellValue(vin_Point[0]);
                    row.createCell(1).setCellValue(vin_Point[1]);*/
                }
                //System.out.println("vin_dist: "+vin_dist_re);
                //harvesine
                double har_dist = EarthCalc.harvesineDistance(n_st_p, n_sc_p);
                if (har_dist > 0 && har_dist < min_dist) {
                    com.grum.geocalc.Point har_dist_Point = EarthCalc.pointAt(n_st_p, _deg, har_dist);
                    double har_dist_re = EarthCalc.harvesineDistance(har_dist_Point, n_sc_p);//should be zero
                    double[] har_Point = {har_dist_Point.latitude-n_sc_p.latitude, har_dist_Point.longitude-n_sc_p.longitude};
                    rmse_har_coord.add(har_Point);
                    rmse_har_dist.add(har_dist_re);
                    
                    /*
                    GlobalCoordinates hyb_sc_p = new GlobalCoordinates(har_dist_Point.latitude, har_dist_Point.longitude);//for vin station
                    //double vin_dist = Utils.vincentyDistance(n_st_p, n_sc_p);
                    GeodeticCurve hyb_geoCurve = geoCalc.calculateGeodeticCurve(reference, hyb_sc_p, gc_sc_p);
                    double hyb_dist_re = hyb_geoCurve.getEllipsoidalDistance();
                    rmse_hyb_dist.add(hyb_dist_re);*/
                    /*
                    //add into row
                    Row row = har_sheet.createRow(row_count);
                    row.createCell(0).setCellValue(har_Point[0]);
                    row.createCell(1).setCellValue(har_Point[1]);*/
                }
                //System.out.println("har_dist: "+har_dist_re);
                //gcd
                double gcd_dist = EarthCalc.gcdDistance(n_st_p, n_sc_p);
                if (gcd_dist > 0 && gcd_dist < min_dist) {
                    com.grum.geocalc.Point gcd_dist_Point = EarthCalc.pointAt(n_st_p, _deg, gcd_dist);
                    double gcd_dist_re = EarthCalc.gcdDistance(gcd_dist_Point, n_sc_p);//should be zero
                    double[] gcd_Point = {gcd_dist_Point.latitude-n_sc_p.latitude, gcd_dist_Point.longitude-n_sc_p.longitude};
                    rmse_gcd_coord.add(gcd_Point);
                    rmse_gcd_dist.add(gcd_dist_re);
                    /*
                    //add into row
                    Row row = gcd_sheet.createRow(row_count);
                    row.createCell(0).setCellValue(gcd_Point[0]);
                    row.createCell(1).setCellValue(gcd_Point[1]);*/
                }
                //System.out.println("gcd_dist: "+gcd_dist_re);
            row_count++;
            });
            System.out.println(st_count+"/"+Utils.get_StationList().size());
            st_count++;
            
        });
        
        
        logger.fatal("RMSE distance(Vincenty formula): "+Math.sqrt(rmse_vin_dist.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vin_dist.size())+" m.");
        logger.fatal("Average distance(Vincenty formula): "+rmse_vin_dist.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble()+" m.");
        logger.fatal("Max distance(Vincenty formula): "+rmse_vin_dist.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble()+" m.");
        logger.fatal("RMSE distance(Haversine formula): "+Math.sqrt(rmse_har_dist.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_har_dist.size())+" m.");
        logger.fatal("Average distance(Haversine formula): "+rmse_har_dist.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble()+" m.");
        logger.fatal("Max distance(Haversine formula): "+rmse_har_dist.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble()+" m.");
        logger.fatal("RMSE distance(GCD formula): "+Math.sqrt(rmse_gcd_dist.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_gcd_dist.size())+" m.");
        logger.fatal("Average distance(GCD formula): "+rmse_gcd_dist.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble()+" m.");
        logger.fatal("Max distance(GCD formula): "+rmse_gcd_dist.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble()+" m.");
        //logger.fatal("RMSE distance(Hybrid Vincenty and Harvesine formula): "+Math.sqrt(rmse_hyb_dist.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_hyb_dist.size())+" m.");
        logger.fatal("Total: "+ rmse_vin_dist.size());
        
        logger.fatal("RMSE coordinate(Vincenty formula): "+Math.sqrt(rmse_vin_coord.parallelStream().mapToDouble(d -> d[0]*d[0]).sum()/(double)rmse_vin_coord.size())+", "+Math.sqrt(rmse_vin_coord.parallelStream().mapToDouble(d -> d[1]*d[1]).sum()/(double)rmse_vin_coord.size()));
        logger.fatal("Average coordinate(Vincenty formula): "+rmse_vin_coord.parallelStream().mapToDouble(d -> Math.abs(d[0])).average().getAsDouble()+", "+rmse_vin_coord.parallelStream().mapToDouble(d -> Math.abs(d[1])).average().getAsDouble());
        logger.fatal("Max coordinate(Vincenty formula): "+rmse_vin_coord.parallelStream().mapToDouble(d -> Math.abs(d[0])).max().getAsDouble()+", "+rmse_vin_coord.parallelStream().mapToDouble(d -> Math.abs(d[1])).max().getAsDouble());
        logger.fatal("RMSE coordinate(Haversine formula): "+Math.sqrt(rmse_har_coord.parallelStream().mapToDouble(d -> d[0]*d[0]).sum()/(double)rmse_har_coord.size())+", "+Math.sqrt(rmse_har_coord.parallelStream().mapToDouble(d -> d[1]*d[1]).sum()/(double)rmse_har_coord.size()));
        logger.fatal("Average coordinate(Haversine formula): "+rmse_har_coord.parallelStream().mapToDouble(d -> Math.abs(d[0])).average().getAsDouble()+", "+rmse_har_coord.parallelStream().mapToDouble(d -> Math.abs(d[1])).average().getAsDouble());
        logger.fatal("Max coordinate(Haversine formula): "+rmse_har_coord.parallelStream().mapToDouble(d -> Math.abs(d[0])).max().getAsDouble()+", "+rmse_har_coord.parallelStream().mapToDouble(d -> Math.abs(d[1])).max().getAsDouble());
        logger.fatal("RMSE coordinate(GCD formula): "+Math.sqrt(rmse_gcd_coord.parallelStream().mapToDouble(d -> d[0]*d[0]).sum()/(double)rmse_gcd_coord.size())+", "+Math.sqrt(rmse_gcd_coord.parallelStream().mapToDouble(d -> d[1]*d[1]).sum()/(double)rmse_gcd_coord.size()));
        logger.fatal("Average coordinate(GCD formula): "+rmse_gcd_coord.parallelStream().mapToDouble(d -> Math.abs(d[0])).average().getAsDouble()+", "+rmse_gcd_coord.parallelStream().mapToDouble(d -> Math.abs(d[1])).average().getAsDouble());
        logger.fatal("Max coordinate(GCD formula): "+rmse_gcd_coord.parallelStream().mapToDouble(d -> Math.abs(d[0])).max().getAsDouble()+", "+rmse_gcd_coord.parallelStream().mapToDouble(d -> Math.abs(d[1])).max().getAsDouble());
        logger.fatal("Total: "+ rmse_vin_coord.size());
        
        Collections.shuffle(rmse_vin_coord);
        Collections.shuffle(rmse_har_coord);
        Collections.shuffle(rmse_gcd_coord);
        int data_point_max = 10000;
        
        for (int dp = 0; dp < data_point_max; dp ++) {
            vin_xData.add(rmse_vin_coord.get(dp)[0]);
            vin_yData.add(rmse_vin_coord.get(dp)[1]);
            
            har_xData.add(rmse_har_coord.get(dp)[0]);
            har_yData.add(rmse_har_coord.get(dp)[1]);
            
            gcd_xData.add(rmse_gcd_coord.get(dp)[0]);
            gcd_yData.add(rmse_gcd_coord.get(dp)[1]);
        }
        
        //find max and min axis
        List<Double> x_max = Lists.newArrayList();
        List<Double> y_max = Lists.newArrayList();
        List<Double> x_min = Lists.newArrayList();
        List<Double> y_min = Lists.newArrayList();
        
        x_max.add(vin_xData.stream().mapToDouble(d -> d).max().getAsDouble());
        x_max.add(har_xData.stream().mapToDouble(d -> d).max().getAsDouble());
        x_max.add(gcd_xData.stream().mapToDouble(d -> d).max().getAsDouble());
        
        y_max.add(vin_yData.stream().mapToDouble(d -> d).max().getAsDouble());
        y_max.add(har_yData.stream().mapToDouble(d -> d).max().getAsDouble());
        y_max.add(gcd_yData.stream().mapToDouble(d -> d).max().getAsDouble());
                
        x_min.add(vin_xData.stream().mapToDouble(d -> d).min().getAsDouble());
        x_min.add(har_xData.stream().mapToDouble(d -> d).min().getAsDouble());
        x_min.add(gcd_xData.stream().mapToDouble(d -> d).min().getAsDouble());
        
        y_min.add(vin_yData.stream().mapToDouble(d -> d).min().getAsDouble());
        y_min.add(har_yData.stream().mapToDouble(d -> d).min().getAsDouble());
        y_min.add(gcd_yData.stream().mapToDouble(d -> d).min().getAsDouble());
        
        double axis_x_max = x_max.stream().mapToDouble(d -> d).max().getAsDouble();
        double axis_x_min = x_min.stream().mapToDouble(d -> d).min().getAsDouble();
        double axis_y_max = y_max.stream().mapToDouble(d -> d).max().getAsDouble();
        double axis_y_min = y_min.stream().mapToDouble(d -> d).min().getAsDouble();
        
        vin_chart.getStyler().setYAxisMin(axis_y_min);
        vin_chart.getStyler().setYAxisMax(axis_y_max);
        vin_chart.getStyler().setXAxisMin(axis_x_min);
        vin_chart.getStyler().setXAxisMax(axis_x_max);
        
        har_chart.getStyler().setYAxisMin(axis_y_min);
        har_chart.getStyler().setYAxisMax(axis_y_max);
        har_chart.getStyler().setXAxisMin(axis_x_min);
        har_chart.getStyler().setXAxisMax(axis_x_max);
        
        gcd_chart.getStyler().setYAxisMin(axis_y_min);
        gcd_chart.getStyler().setYAxisMax(axis_y_max);
        gcd_chart.getStyler().setXAxisMin(axis_x_min);
        gcd_chart.getStyler().setXAxisMax(axis_x_max);
        
        vin_chart.addSeries("Vincenty Accuracy", vin_xData, vin_yData);
        har_chart.addSeries("Haversine Accuracy", har_xData, har_yData);
        gcd_chart.addSeries("GCD Accuracy", gcd_xData, gcd_yData);
        
        BitmapEncoder.saveBitmap(vin_chart, "Vincenty_Accuracy_"+System.currentTimeMillis()+".png", BitmapEncoder.BitmapFormat.PNG);
        BitmapEncoder.saveBitmap(har_chart, "Haversine_Accuracy_"+System.currentTimeMillis()+".png", BitmapEncoder.BitmapFormat.PNG);
        BitmapEncoder.saveBitmap(gcd_chart, "GCD_Accuracy_"+System.currentTimeMillis()+".png", BitmapEncoder.BitmapFormat.PNG);
        
        /*
        new SwingWrapper<XYChart>(vin_chart).displayChart().setTitle("Vincenty Accuracy Scatter Chart");
        new SwingWrapper<XYChart>(har_chart).displayChart().setTitle("Harvesine Accuracy Scatter Chart");
        new SwingWrapper<XYChart>(gcd_chart).displayChart().setTitle("GCD Accuracy Scatter Chart");*/
        
       
        //logger.fatal("Overall: "+ Utils.get_StationList().size()*Utils.get_ssidqueue().size());
        // Write the output to a file
        /*
        FileOutputStream fileOut = new FileOutputStream("accuracy_check_"+System.currentTimeMillis()+".xlsx");
        accuracy_check_WorkBook_x.write(fileOut);
        fileOut.close();*/
    }
    
    @FXML
    private void remove_nonmatching_data_ButtonAction(ActionEvent event) throws Exception {
        Utils.get_fft_snp_map().entrySet().forEach(stn -> {
            stn.getValue().entrySet().forEach(fft_snp -> {
                if (!Utils.get_seismic_map().containsKey(fft_snp.getValue().getStartTime())) {
                    //add del list
                    String[] del_data = {stn.getKey(), fft_snp.getValue().getSSID()};
                    Configs.db_del_queue.add(del_data);
                }
            });
        });
        //add mission
        while (!Configs.db_del_queue.isEmpty()) {
            String[] del_data = Configs.db_del_queue.poll();
            String del_sql = "DELETE FROM " + del_data[0] + " WHERE SSID = ?";
            PreparedStatement pst_s = Utils.getConnection().prepareStatement(del_sql);
            pst_s.setString(1, del_data[1]);
            Utils.add_to_op_task_queue(new OP_Task(OP_Engine.delete_from_db, pst_s, "DELETE "+ del_data[0]+ ", SSID: "+del_data[1]+"."+System.getProperty("line.separator")));
        }
    }
    
    
    @FXML
    private void set_smooth_ButtonAction(ActionEvent event) throws Exception {
        
    }
    
    @FXML
    private void set_hanningsmooth_ButtonAction(ActionEvent event) throws Exception {
        ((CheckMenuItem) event.getSource()).getParentMenu().getItems().forEach(cmi -> ((CheckMenuItem)cmi).setSelected(false));
        ((CheckMenuItem) event.getSource()).setSelected(true);
        String smooth_times = ((CheckMenuItem) event.getSource()).getText();
        //lods = lods.substring(6);//"Level_"
        Utils.set_smoothing_mode(((CheckMenuItem) event.getSource()).getParentMenu().getText() ,Integer.valueOf(smooth_times));
        Utils.check_logs_textarea();
        logs_textarea.appendText("Smoothing: "+((CheckMenuItem) event.getSource()).getParentMenu().getText()+" "+smooth_times+" times."+System.getProperty("line.separator"));
        logs_textarea.selectPositionCaret(logs_textarea.getLength());
    }
    
    @FXML
    private void open_2D_station_map_ButtonAction(ActionEvent event) throws Exception {
        FX_2D_Pre_Map_Window ff = new FX_2D_Pre_Map_Window(2);
        ff.start(new Stage());
    }
    
    @FXML
    private void open_2D_seismic_station_map_ButtonAction(ActionEvent event) throws Exception {
        FX_2D_Pre_Map_Window ff = new FX_2D_Pre_Map_Window(0);
        ff.start(new Stage());
    }
    
    @FXML
    private void set_staiotn_distance_to_epiccenter_vector_ButtonAction(ActionEvent event) {
        ((CheckMenuItem) event.getSource()).getParentMenu().getItems().forEach(cmi -> ((CheckMenuItem)cmi).setSelected(false));
        ((CheckMenuItem) event.getSource()).setSelected(true);
        String dist = ((CheckMenuItem) event.getSource()).getText();
        //lods = lods.substring(6);//"Level_"
        Utils.set_station_distance_to_epiccenter_vector(Integer.valueOf(dist)*1000);
        Utils.check_logs_textarea();
        logs_textarea.appendText("Distance to Epic Center Vector: "+dist+" KM"+System.getProperty("line.separator"));
        logs_textarea.selectPositionCaret(logs_textarea.getLength());
    }
    
    
    @FXML
    private void kriging_vs30_ButtonAction(ActionEvent event) {
        try {
            Kriging kr = new Kriging();
            //List<Station> st_temp = Lists.newArrayList();
            //variogram model
            String model = ((MenuItem)event.getSource()).getText();
            Utils.check_logs_textarea();
            this.get_logs_textarea().appendText("Kriging Vs30 using Variogram Model: "+model+"." +System.getProperty("line.separator"));
            this.get_logs_textarea().selectPositionCaret(this.get_logs_textarea().getLength());
            model = model.toLowerCase();
            System.out.println(model);
            //Utils.get_StationList().stream().filter(vs30 -> !vs30.getVs30().isEmpty()).forEach(str -> st_temp.add(str));
            //gen data arrays
            double sigma2 = 0D;
            double alpha = 100D;
            //long st =System.currentTimeMillis();
            int real_st_num = Utils.get_StationList().size();
            //int real_st_num = Utils.get_StationList().size();//348 ok, 347 the problem //TAP003, TAP004
            kr.set_station_num(real_st_num);
            double[] value = new double[real_st_num];
            double[] x_lon = new double[real_st_num];
            double[] y_lat = new double[real_st_num];
            
            for (int c = 0 ; c < real_st_num ; c++) {
                //System.out.println(Utils.get_StationList().get(c).getVs30()+":"+Utils.get_StationList().get(c).getLongitude()+","+Utils.get_StationList().get(c).getLatitude());
                value[c] = Double.valueOf(Utils.get_StationList().get(c).getVs30());
                //trans to tm2
                /*
                TM2coord tm2_tmp = Utils.Cal_lonlat_To_twd97(Double.valueOf(st_temp.get(c).getLongitude()), Double.valueOf(st_temp.get(c).getLatitude()));
                x_lon[c] = tm2_tmp.x_TM2;
                y_lat[c] = tm2_tmp.y_TM2;*/
                x_lon[c] = Double.valueOf(Utils.get_StationList().get(c).getLongitude());
                y_lat[c] = Double.valueOf(Utils.get_StationList().get(c).getLatitude());
            }
            /*
            double[] value = {229.88,229.62};
            double[] x_lon = {120.249,120.4205};
            double[] y_lat = {23.7047,23.7175};
            */
            /*
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new FileReader("libs/kriging.js"));
            
            Invocable invocable = (Invocable) engine;
            Object kriging = engine.get("kriging");//use Great-circle distance instead of Geographical distance
            Object variogram = invocable.invokeMethod(kriging, "train", value, x_lon, y_lat, model, sigma2, alpha);*/
            kr.set_data(value, x_lon, y_lat, model);
            Utils.add_to_op_task_queue(new OP_Task(OP_Engine.kriging, kr));
            /*
            Object variogram = kr.train(value, x_lon, y_lat, model);
            JSONObject jb_kr = new JSONObject(variogram.toString());
            //System.out.println(jb_kr);
            this.get_logs_textarea().appendText("Kriging Vs30 Done. ("+real_st_num+" Stations)"+System.getProperty("line.separator"));
            this.get_logs_textarea().selectPositionCaret(this.get_logs_textarea().getLength());
            long et =System.currentTimeMillis();
            System.out.println((et-st)/1000L+"s");
            Files.write(Paths.get(Utils.get_MainPath()+"/krigin_vs30_"+System.currentTimeMillis()+".json"), jb_kr.toString().getBytes());
            this.get_logs_textarea().appendText("Save Kriging result." +System.getProperty("line.separator"));
            this.get_logs_textarea().selectPositionCaret(this.get_logs_textarea().getLength());
            */
            //serialize to Json in javascript
            /*
            try (FileOutputStream fileOut = new FileOutputStream(Utils.get_MainPath()+"/krigin_vs30_temp.tmp")) {
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                //dump data into project object
                out.writeObject(kr);
                out.close();
            }*/
            //Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            logger.debug(ex);
        }
    }
    
    @FXML
    private void merge_by_main_sub_station_name_ButtonAction(ActionEvent event) {
        
    }
    
    
    @FXML
    private void kriging_z10_ButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void kriging_kappa_ButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void save_tempfile_ButtonAction(ActionEvent event) {
        Utils.add_to_op_task_queue(new OP_Task(OP_Engine.auto_save_project));
    }
    
    @FXML
    private void match_data_ButtonAction(ActionEvent event) {
        //1.load from db
        //SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
        try {
            DatabaseMetaData md = Utils.getConnection().getMetaData();
            ResultSet rs_t = md.getTables(null, null, "%", null);
            while (rs_t.next()) {
                String t_t = rs_t.getString(3);
                if (t_t.equals("Station")) {
                    
                } else if (t_t.equals("Seismic")) {
                    String sql = "SELECT * FROM Seismic";
                    PreparedStatement pst = Utils.getConnection().prepareStatement(sql);
                    ResultSet rs_s = pst.executeQuery();
                    while (rs_s.next()) {
                        String st_temp = rs_s.getString("StartTime");
                        Seismic sc_temp = SerializationUtils.deserialize(rs_s.getBytes("Data"));
                        Utils.add_to_seismic_map(format.parse(st_temp), sc_temp);
                    }
                    pst.close();
                    rs_s.close();
                } else {   
                    //select FFT from DB
                    String sql = "SELECT * FROM "+ t_t;
                    PreparedStatement pst = Utils.getConnection().prepareStatement(sql);
                    try (ResultSet rs_fft = pst.executeQuery()) {
                        while (rs_fft.next()) {
                            String st_temp = rs_fft.getString("SSID");
                            //FFT fft_temp = SerializationUtils.deserialize(rs_fft.getBytes("Data"));
                            FFT_SnapShot fft_temp = SerializationUtils.deserialize(rs_fft.getBytes("Data"));
                            //get station
                            Station stt_temp = Utils.get_StationList().stream().filter(st -> st.getStationName().equals(fft_temp.getStationCode())).findAny().orElse(null);
                            //check sub st name
                            if (stt_temp == null) {
                                stt_temp = Utils.get_StationList().stream().filter(st -> st.getSubStationName().equals(fft_temp.getStationCode())).findAny().orElse(null);
                            }
                            
                            //if (Utils.get_seismic_map().containsKey(format.parse(fft_temp.getSeismic_StartTime()))) {
                            if (Utils.get_seismic_map().containsKey(fft_temp.getStartTime())) {
                                //System.out.println(fft_temp.getSeismic_StartTime());
                                //Seismic sc_temp = Utils.get_seismic_map().get(format.parse(fft_temp.getSeismic_StartTime()));
                                Seismic sc_temp = Utils.get_seismic_map().get(fft_temp.getStartTime());
                                fft_temp.setDepthOfFocus(sc_temp.getDepth_Of_Focus().doubleValue());
                                //System.out.println(sc_temp.getDepth_Of_Focus().doubleValue());
                                //fft_temp.setEpicenterDistance(fft_temp.calc_distance(sc_temp.getLongitude().doubleValue(), sc_temp.getLatitude().doubleValue(), Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()), 0D, 0D));
                                fft_temp.setMagnitude(sc_temp.getMagnitude().doubleValue());
                                //System.out.println("Match ML."+sc_temp.getMagnitude().doubleValue());
                            } else {
                                //System.out.println("No Match...("+fft_temp.getSeismic_StartTime()+":");
                                logger.fatal("No Match...("+format.format(fft_temp.getStartTime())+")");
                                //System.out.println("No Match...("+format.format(fft_temp.getStartTime())+")");
                            }
                            
                            
                            Utils.add_to_fft_station_name_set(fft_temp.getStationCode());
                            if (!Utils.get_ssidqueue().contains(st_temp)) {
                                Map<String, FFT_SnapShot> temp_fftdatamap = Utils.get_fft_snp_map().get(fft_temp.getStationCode());
                                FFT_SnapShot fft_ss = new FFT_SnapShot(fft_temp.getSSID(), fft_temp.getStationCode(), fft_temp.getInstrumentKind(), fft_temp.getStartTime(),
                                        fft_temp.getMax_U_Hz(), fft_temp.getMax_N_Hz(), fft_temp.getMax_E_Hz(),
                                        fft_temp.getMax_U_Hz_20(), fft_temp.getMax_N_Hz_20(), fft_temp.getMax_E_Hz_20(),
                                        fft_temp.getPGA_U(), fft_temp.getPGA_N(), fft_temp.getPGA_E());
                                if (stt_temp != null) {
                                    fft_ss.setVs30(Double.valueOf(stt_temp.getVs30()));
                                    fft_ss.setGL(Double.valueOf(stt_temp.getGround_Level()));
                                    fft_ss.setZ10(Double.valueOf(stt_temp.getZ10()));
                                    fft_ss.setK(Double.valueOf(stt_temp.getKappa()));
                                    fft_ss.setStationLongitude(Double.valueOf(stt_temp.getLongitude()));
                                    fft_ss.setStationLatitude(Double.valueOf(stt_temp.getLatitude()));
                                }
                                
                                if (temp_fftdatamap != null) {
                                    temp_fftdatamap.put(fft_temp.getSSID(), fft_ss);
                                } else {//null
                                    Map<String, FFT_SnapShot> temp_emptyfftmap = Maps.newConcurrentMap();
                                    temp_emptyfftmap.put(fft_temp.getSSID(), fft_ss);
                                    Utils.get_fft_snp_map().put(fft_temp.getStationCode(), temp_emptyfftmap);
                                }
                            }
                            String sql_match = "UPDATE " +fft_temp.getStationCode()+" SET DATA=? WHERE SSID =?";
                            PreparedStatement pst_m = Utils.getConnection().prepareStatement(sql_match);
                            byte[] data = SerializationUtils.serialize(fft_temp);
                            pst_m.setBytes(1, data);
                            pst_m.setString(2, fft_temp.getSSID());
                            Utils.add_to_op_task_queue(new OP_Task(OP_Engine.insert_or_update_into_db, pst_m, "UPDATE "+ fft_temp.getStationCode()+ ", SSID: "+fft_temp.getSSID()+"."+System.getProperty("line.separator")));
                        }
                    }
                    
                    
                }
            }
            
            
            rs_t.close();
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        } catch (ParseException ex) {
            Utils.logger.fatal(ex);
        }
        //2.match data
        
        
        
        //3.update db
    }
    
    @FXML
    private void process_data_ls_ButtonAction(ActionEvent event) {
        Configs.detrend_least_square_selected = true;
        Configs.detrend_del_mean_selected = false;
        process_data_ButtonAction();
    }
        
    @FXML
    private void process_data_rdc_ButtonAction(ActionEvent event) {
        Configs.detrend_least_square_selected = false;
        Configs.detrend_del_mean_selected = true;
        process_data_ButtonAction();
    }
    
    private void process_data_ButtonAction() {
        Configs.Work_Pool.execute(() -> {
            try {Files.walk(Paths.get(Utils.get_MainPath()).toRealPath())
                //.filter(Files::isRegularFile)
                .filter(files -> files.toString().endsWith(".txt"))
                .forEach(path -> {
                    //check if is seismic file, read first 10 lines
                    Utils.add_path_to_filequeue(path.toString());
                    int n = Utils.get_filequeue().size();
                    Platform.runLater(() -> {
                        Utils.check_logs_textarea();
                        logs_textarea.appendText("Add " + path.toString() + ". " + n + " files in queue."+System.getProperty("line.separator"));
                        label_status.setText(Utils.get_filequeue().size()+" files in queue.");
                        logs_textarea.selectPositionCaret(logs_textarea.getLength());
                        
                    });
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        Utils.logger.fatal(ex);
                    }
                    //logs.appendText("Add " + path.toString() + ". " + Utils.get_filequeue().size() + " files in queue.\n");
                    //System.out.println("Add " + path.toString() + ". " + Utils.get_filequeue().size() + " files in queue.\n");
                });
            } catch (IOException ex) {
                //Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                //logs_textarea.appendText(ex + "\n");
                Utils.logger.warn(ex);
            } catch (Exception e) {
                //logs_textarea.appendText(e + "\n");
                Utils.logger.warn(e);
            }
            
            Utils.get_filequeue().stream().forEach(fn -> {
                Read_file_task rft = new Read_file_task(fn);
                rft.compute();
                //rft.fork();
            });
            //Utils.clear_filequeue();

            //et = System.currentTimeMillis();
            //System.out.println("1.8:" + (et - st) + "ms.");
        });
    }
    
    
    @FXML
    private void save_project_ButtonAction(ActionEvent event) throws IOException {
        if (Utils.get_ProjectPath() == null) {
            FileChooser sp_fileChooser = new FileChooser();
            sp_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Seismic Project File", "*.spf"));
            File dir = sp_fileChooser.showSaveDialog(MainApp.stage);
            if (dir != null) {
                try {
                    label_status.setText(dir.getCanonicalPath());
                    Utils.set_ProjectPath(dir.getCanonicalPath());
                    proj.set_ProjectPath(dir.getCanonicalPath());
                    Utils.set_ProjectName(dir.getName());
                    proj.set_ProjectName(dir.getName());
                } catch (IOException ex) {
                    Utils.logger.fatal(ex);
                }
            } else {
                label_status.setText("idle");
            }
        }
        FileOutputStream fileOut = new FileOutputStream(Utils.get_ProjectPath());
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        //dump data into project object
        
        
        
        out.writeObject(proj);
        out.close();
        fileOut.close();
        label_status.setText("Save project in "+Utils.get_ProjectPath()+".");
        MainApp.stage.setTitle("FFT tool for seismic by Department of Architecture NCKU - "+Utils.get_ProjectName());
        //System.out.printf("Serialized data is saved in /tmp/employee.ser");
        
    }
    
    @FXML
    private void save_as_ButtonAction(ActionEvent event) throws IOException {
        
    }
    
    @FXML
    private void flush_db_ButtonAction(ActionEvent event) {
        try {
            Utils.DB_vacuum();
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        }
    }
    
    @FXML
    private void clear_fft_data_DB_ButtonAction(ActionEvent event) {
        try {
            DatabaseMetaData md = Utils.getConnection().getMetaData();
            ResultSet rs_t = md.getTables(null, null, "%", null);
            while (rs_t.next()) {
                String t_t = rs_t.getString(3);
                if (t_t.equals("Station")) {

                } else if (t_t.equals("Seismic")) {

                } else {   
                    Utils.table_rm_queue.add(t_t);
                }
            }
            if (Utils.table_rm_queue.size() == 0) {
                Platform.runLater(() -> {
                    Utils.check_logs_textarea();
                    logs_textarea.appendText("No data to clear."+System.getProperty("line.separator"));
                    //label_status.setText(Utils.get_filequeue().size()+" files in queue.");
                    logs_textarea.selectPositionCaret(logs_textarea.getLength());
                });
                return;
            }
            
            Utils.table_rm_queue.stream().forEach(trq -> {
                try {
                    String t_str = trq;
                    String sql = "DROP TABLE IF EXISTS "+ t_str;
                    PreparedStatement pst;
                    pst = Utils.getConnection().prepareStatement(sql);
                    Utils.add_to_op_task_queue(new OP_Task(OP_Engine.clear_fft_data_db, pst, "DROP TABLE IF EXISTS "+ t_str+"."+System.getProperty("line.separator")));
                } catch (SQLException ex) {
                    Utils.logger.fatal(ex);
                }
            });
            //clear station name set
            Utils.clear_fft_station_name_set();
            
            
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        }
    }
    
    @FXML
    private void open_ffstaion_window_ButtonAction(ActionEvent event) throws IOException {
        MainApp.freefield_window.show();
    }
    
    @FXML
    private void exit_ButtonAction(ActionEvent event) throws IOException {
        System.exit(0);
    }
    
    @FXML
    private void import_ffstaion_data_ButtonAction(ActionEvent event) throws IOException {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            process_station_data_into_db(dir.getCanonicalPath(), 0);//insert
        } else {
            label_status.setText("idle");
        }
    }
    
    @FXML
    private void update_ffstaion_data_ButtonAction(ActionEvent event) throws IOException {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            process_station_data_into_db(dir.getCanonicalPath(), 1);//update
        } else {
            label_status.setText("idle");
        }
    }
    
    @FXML
    private void new_project_ButtonAction(ActionEvent event) throws IOException {
        //open a new screen to set up the project
        MainApp.new_project_window.show();
    }
    
    @FXML
    private void open_project_ButtonAction(ActionEvent event) throws IOException {
        FileChooser project_fileChooser = new FileChooser();
        project_fileChooser.setTitle("Project File");
        project_fileChooser.setInitialDirectory(new File("."));                 
        project_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Seismic Project File", "*.spf"));
        File dir = project_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            try {
                label_status.setText(dir.getCanonicalPath());
                Utils.set_ProjectPath(dir.getCanonicalPath());
                Utils.set_ProjectName(dir.getName());
                MainApp.stage.setTitle("FFT tool for seismic by Department of Architecture NCKU - "+Utils.get_ProjectName());
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
            }
        } else {
            label_status.setText("idle");
        }
    }
    
    @FXML
    private void search_data_ButtonAction(ActionEvent event) throws IOException {
        String tag_name = sort_list.getValue().toString();
        String search_condition = sc_val.getText();
        String logic_symbol = logic_list.getValue().toString();
        SearchTag temp_st;
        int tag_size = Utils.searchtag_list.size();
        if (tag_name.equals("Time(yyyy/mm/dd)")) {
            temp_st = new SearchTag(tag_name, logic_symbol, search_condition, tag_size);
            //draw searchtag_list
            
        } else if (tag_name.equals("StationName")){
            
        } else if (tag_name.equals("Magnitude(Ml)")){
            
        } else if (tag_name.equals("DepthOfFocus(Km)")){
            
        } else if (tag_name.equals("Vs30(M/s)")){
            
        } else if (tag_name.equals("Ground Level(M)")){
            
        } else if (tag_name.equals("Z10(M/s)")){
            
        } else if (tag_name.equals("Kappa(K)")){
            
        } else if (tag_name.equals("EpicenterDistance(Km)")){
            
        } else if (tag_name.equals("Longitude")){
            
        } else if (tag_name.equals("Latitude")){
            
        } else if (tag_name.equals("Axial U Max Hz(0~20)")){
            
        } else if (tag_name.equals("Axial U Max Hz(0~50)")){
            
        } else if (tag_name.equals("Axial U Max Hz(All)")){
            
        } else if (tag_name.equals("Axial N Max Hz(0~20)")){
            
        } else if (tag_name.equals("Axial N Max Hz(0~50)")){
            
        } else if (tag_name.equals("Axial N Max Hz(All)")){
            
        } else if (tag_name.equals("Axial E Max Hz(0~20)")){
            
        } else if (tag_name.equals("Axial E Max Hz(0~50)")){
            
        } else if (tag_name.equals("Axial E Max Hz(All)")){
            
        } else {
            
        }

        /*
        temp_st = new SearchTag(tag_name, logic_symbol, , tag_size);
        //String tag_name, String logic_symbol, String condition_str, int tag_order
        
        
        Utils.searchtag_list.add(tag_size, temp_st);*/
        
        
        
    }
    
    XYChart chartbyselected_U = null;
    XYChart chartbyselected_N = null;
    XYChart chartbyselected_E = null;
    
    @FXML
    private void chartbyselect_ButtonAction(ActionEvent event) throws IOException {
        //System.out.println("gen chart by station.");
        // Create Chart
        // Show it
        //x axial select
        /*
            x_cb_acc_selected
            x_cb_hz_selected
            *x_cb_ml_selected
            *x_cb_vs30_selected
            x_cb_dof_selected
            x_cb_ed_selected
            x_cb_st_selected
            x_cb_t_selected 
        */
        String tilte_temp = null;
        Set hash_temp = Sets.newConcurrentHashSet();
        if (x_cb_ml_selected) {
            Utils.get_fft_snp_map().entrySet().forEach(st -> {
                st.getValue().entrySet().forEach(snp -> {
                    hash_temp.add(snp.getValue().getMagnitude());
                });
            });
            chartbyselected_U = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_N = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_E = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            tilte_temp = "Ml";
            chartbyselected_U.setXAxisTitle("Ml");
            chartbyselected_N.setXAxisTitle("Ml");
            chartbyselected_E.setXAxisTitle("Ml");
        }
        
        if (x_cb_vs30_selected) {
            Utils.get_fft_snp_map().entrySet().forEach(st -> {
                st.getValue().entrySet().forEach(snp -> {
                    try {
                        hash_temp.add(snp.getValue().getVs30());
                    } catch (Exception ex) {
                        Utils.logger.error(snp.getValue().getStationCode()+","+snp.getValue().getVs30()+","+snp.getValue().getStartTime()+","+snp.getValue().getInstrumentKind());
                        //System.out.println(snp.getValue().getStationCode()+","+snp.getValue().getVs30()+","+snp.getValue().getStartTime()+",");
                    }
                    //System.out.println(snp.getValue().getStationCode()+":"+snp.getValue().getVs30());
                });
            });
            chartbyselected_U = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_N = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_E = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            tilte_temp = "Vs30";
            chartbyselected_U.setXAxisTitle("Vs30(M/s)");
            chartbyselected_N.setXAxisTitle("Vs30(M/s)");
            chartbyselected_E.setXAxisTitle("Vs30(M/s)");
        }
        
        if (x_cb_ed_selected) {
            Utils.get_fft_snp_map().entrySet().forEach(st -> {
                st.getValue().entrySet().forEach(snp -> {
                    try {
                        hash_temp.add(snp.getValue().getEpicenterDistance());
                    } catch (Exception ex) {
                        Utils.logger.error(snp.getValue().getStationCode()+","+snp.getValue().getEpicenterDistance()+","+snp.getValue().getStartTime()+","+snp.getValue().getInstrumentKind());
                        //System.out.println(snp.getValue().getStationCode()+","+snp.getValue().getVs30()+","+snp.getValue().getStartTime()+",");
                    }
                    //System.out.println(snp.getValue().getStationCode()+":"+snp.getValue().getVs30());
                });
            });
            chartbyselected_U = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_N = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_E = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            tilte_temp = "EpicenterDistance(Km)";
            chartbyselected_U.setXAxisTitle("EpicenterDistance(Km)");
            chartbyselected_N.setXAxisTitle("EpicenterDistance(Km)");
            chartbyselected_E.setXAxisTitle("EpicenterDistance(Km)");
        }
        
        if (x_cb_dof_selected) {
            Utils.get_fft_snp_map().entrySet().forEach(st -> {
                st.getValue().entrySet().forEach(snp -> {
                    try {
                        hash_temp.add(snp.getValue().getDepthOfFocus());
                    } catch (Exception ex) {
                        Utils.logger.error(snp.getValue().getStationCode()+","+snp.getValue().getDepthOfFocus()+","+snp.getValue().getStartTime()+","+snp.getValue().getInstrumentKind());
                        //System.out.println(snp.getValue().getStationCode()+","+snp.getValue().getVs30()+","+snp.getValue().getStartTime()+",");
                    }
                    //System.out.println(snp.getValue().getStationCode()+":"+snp.getValue().getVs30());
                });
            });
            chartbyselected_U = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_N = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            chartbyselected_E = new XYChart(Math.max(hash_temp.size()*15,1600), 900);
            tilte_temp = "DepthOfFocus";
            chartbyselected_U.setXAxisTitle("DepthOfFocus(Km)");
            chartbyselected_N.setXAxisTitle("DepthOfFocus(Km)");
            chartbyselected_E.setXAxisTitle("DepthOfFocus(Km)");
        }
        
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------
        
        //y axial select
        if (y_cb_hz_selected) {
            tilte_temp += "- Hz(20)";
            chartbyselected_U.setYAxisTitle("Hz");
            chartbyselected_N.setYAxisTitle("Hz");
            chartbyselected_E.setYAxisTitle("Hz");

            hash_temp.forEach(num -> {
                List<Number> fft_queue = Lists.newArrayList();
                List<Number> fft_hz_U_queue = Lists.newArrayList();
                List<Number> fft_hz_N_queue = Lists.newArrayList();
                List<Number> fft_hz_E_queue = Lists.newArrayList();
                Utils.get_fft_snp_map().entrySet().forEach(ent -> {
                    if (x_cb_ml_selected) {
                        ent.getValue().entrySet().stream().filter(ent_sub_fil -> num.equals(ent_sub_fil.getValue().getMagnitude())).forEach(ent_sub -> {
                            fft_queue.add((Number)num);
                            fft_hz_U_queue.add(ent_sub.getValue().getMax_U_Hz_20());
                            fft_hz_N_queue.add(ent_sub.getValue().getMax_N_Hz_20());
                            fft_hz_E_queue.add(ent_sub.getValue().getMax_E_Hz_20());
                        });
                    }
                    if (x_cb_vs30_selected) {
                        ent.getValue().entrySet().stream().filter(ent_sub_fil -> num.equals(ent_sub_fil.getValue().getVs30())).forEach(ent_sub -> {
                            fft_queue.add((Number)num);
                            fft_hz_U_queue.add(ent_sub.getValue().getMax_U_Hz_20());
                            fft_hz_N_queue.add(ent_sub.getValue().getMax_N_Hz_20());
                            fft_hz_E_queue.add(ent_sub.getValue().getMax_E_Hz_20());
                        });
                    }
                    if (x_cb_ed_selected) {
                        ent.getValue().entrySet().stream().filter(ent_sub_fil -> num.equals(ent_sub_fil.getValue().getEpicenterDistance())).forEach(ent_sub -> {
                            fft_queue.add((Number)num);
                            fft_hz_U_queue.add(ent_sub.getValue().getMax_U_Hz_20());
                            fft_hz_N_queue.add(ent_sub.getValue().getMax_N_Hz_20());
                            fft_hz_E_queue.add(ent_sub.getValue().getMax_E_Hz_20());
                        });
                    }
                    if (x_cb_dof_selected) {
                        ent.getValue().entrySet().stream().filter(ent_sub_fil -> num.equals(ent_sub_fil.getValue().getDepthOfFocus())).forEach(ent_sub -> {
                            fft_queue.add((Number)num);
                            fft_hz_U_queue.add(ent_sub.getValue().getMax_U_Hz_20());
                            fft_hz_N_queue.add(ent_sub.getValue().getMax_N_Hz_20());
                            fft_hz_E_queue.add(ent_sub.getValue().getMax_E_Hz_20());
                        });
                    }
                    
                    
                });
                if (fft_queue.size() > 0) {
                    //System.out.println("ML("+ml_num+"):"+fft_ml_queue.size());
                    chartbyselected_U.addSeries("Data(U)"+num, fft_queue, fft_hz_U_queue);
                    chartbyselected_N.addSeries("Data(N)"+num, fft_queue, fft_hz_N_queue);
                    chartbyselected_E.addSeries("Data(E)"+num, fft_queue, fft_hz_E_queue);
                }
            });
        }
        chartbyselected_U.setTitle(tilte_temp);
        chartbyselected_U.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chartbyselected_U.getStyler().setMarkerSize(8);
        chartbyselected_N.setTitle(tilte_temp);
        chartbyselected_N.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chartbyselected_N.getStyler().setMarkerSize(8);
        chartbyselected_E.setTitle(tilte_temp);
        chartbyselected_E.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chartbyselected_E.getStyler().setMarkerSize(8);
        long tst = System.currentTimeMillis();
        BitmapEncoder.saveBitmap(chartbyselected_U, Utils.get_MainPath()+"/"+Utils.get_ProjectName()+tilte_temp+"_U_"+tst+".png", BitmapEncoder.BitmapFormat.PNG);
        BitmapEncoder.saveBitmap(chartbyselected_N, Utils.get_MainPath()+"/"+Utils.get_ProjectName()+tilte_temp+"_N_"+tst+".png", BitmapEncoder.BitmapFormat.PNG);
        BitmapEncoder.saveBitmap(chartbyselected_E, Utils.get_MainPath()+"/"+Utils.get_ProjectName()+tilte_temp+"_E_"+tst+".png", BitmapEncoder.BitmapFormat.PNG);
        new SwingWrapper<XYChart>(chartbyselected_U).displayChart().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        new SwingWrapper<XYChart>(chartbyselected_N).displayChart().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        new SwingWrapper<XYChart>(chartbyselected_E).displayChart().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        
        
        
        
        
        
        /*
        XYChart nor_all_chart = new XYChart(Utils.get_fftmap().size()*30, 1080);
        nor_all_chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        nor_all_chart.getStyler().setMarkerSize(8);
        nor_all_chart.setTitle("FFT nor(N)");
        nor_all_chart.setXAxisTitle("Station");
        nor_all_chart.setYAxisTitle("Hz");
        
        Utils.get_fftmap().entrySet().forEach(maps -> {
            List<String> fft_Staion_queue = Lists.newArrayList();
            List<Number> fft_N_queue = Lists.newArrayList();
            maps.getValue().entrySet().forEach(fft -> {
                fft_Staion_queue.add(fft.getValue().getStationCode());
                fft_N_queue.add(fft.getValue().getMax_N_Hz_20());
            });
            
            nor_all_chart.addSeries("Station", fft_Staion_queue, fft_N_queue);
            //if (fft.getMax_N_Hz() != null) {
                //fft_N_queue.add(fft.getMax_N_Hz());
            //} else {
                //fft_N_queue.add(0);
            //}
        });
        
        
       
        //System.out.println(file_path+"/"+file_name+"_A.png");
        BitmapEncoder.saveBitmap(nor_all_chart, Utils.get_MainPath()+"/"+Utils.get_ProjectName()+"_nor_N.png", BitmapEncoder.BitmapFormat.PNG);
        */
    }
    
    @FXML
    private void import_seismic_data_ButtonAction(ActionEvent event) throws IOException {
        /*
        System.out.println("You clicked me!");
        label.setText("Hello World!");
        
        st = System.currentTimeMillis();*/

        //if (Utils.get_MainPath() != null) {
        choose_dic();
        
        //read by lines
        //st = System.currentTimeMillis();
    }
    
    @FXML
    private void load_data_fromtempfile_ButtonAction(ActionEvent event) throws IOException {
        
    }
    
    @FXML
    private void set_lod_ButtonAction(ActionEvent event) throws IOException {
        ((CheckMenuItem) event.getSource()).getParentMenu().getItems().forEach(cmi -> ((CheckMenuItem)cmi).setSelected(false));
        ((CheckMenuItem) event.getSource()).setSelected(true);
        String lods = ((CheckMenuItem) event.getSource()).getText();
        lods = lods.substring(6);//"Level_"
        Utils.set_level_of_detail(Integer.valueOf(lods));
        Utils.check_logs_textarea();
        logs_textarea.appendText("Level of Detail: "+lods+System.getProperty("line.separator"));
        logs_textarea.selectPositionCaret(logs_textarea.getLength());
    }
    
    
    
    @FXML
    private void load_data_fromDB_ButtonAction(ActionEvent event) throws IOException {
        //1.list all table
        init_nodes();
        try {
            //SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
            DatabaseMetaData md = Utils.getConnection().getMetaData();
            ResultSet rs_t = md.getTables(null, null, "%", null);
            fft_treetable.setShowRoot(false);
            FFT_SnapShot ffts_root = new FFT_SnapShot();
            ffts_root.setStationCode("Root");
            table_root = new TreeItem<>(ffts_root);
            table_root.setExpanded(true);
            while (rs_t.next()) {
                String t_t = rs_t.getString(3);
                if (t_t.equals("Station")) {
                    
                } else if (t_t.equals("Seismic")) {
                    String sql = "SELECT * FROM Seismic";
                    PreparedStatement pst = Utils.getConnection().prepareStatement(sql);
                    ResultSet rs_s = pst.executeQuery();
                    while (rs_s.next()) {
                        String st_temp = rs_s.getString("StartTime");
                        Seismic sc_temp = SerializationUtils.deserialize(rs_s.getBytes("Data"));
                        Utils.add_to_seismic_map(format.parse(st_temp), sc_temp);
                    }
                    pst.close();
                    rs_s.close();
                } else {   
                    //if (Utils.add_to_fft_station_name_set(t_t)) {//add success
                    add_treenodes(t_t);
                    //}
                }
            }
            
            rs_t.close();
            Platform.runLater(() -> label_status.setText(Utils.get_fft_station_name_set().size() + " tables in DB."));
            
            //2.loop tables and gen snapshot
            Utils.get_fft_station_name_set().stream().forEach(st -> {
                try {
                    String sql = "SELECT * FROM "+ st;
                    //System.out.println(sql);
                    PreparedStatement pst = Utils.getConnection().prepareStatement(sql);
                    Utils.add_to_op_task_queue(new OP_Task(OP_Engine.gen_snapshot_from_db, pst));
                } catch (SQLException ex) {
                    Utils.logger.fatal(ex);
                }
            });
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        } catch (ParseException ex) {
            Utils.logger.fatal(ex);
        }
    }

    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //label_status.setText("idle");

        //cancel
        // Create ContextMenu
        /*
        Image openIcon = null;
        BufferedImage bfimg = ImageIO.read(new File("Diskette.png"));
        openIcon = SwingFXUtils.toFXImage(bfimg, null);
        //Image openIcon = new Image(getClass().getResourceAsStream("Diskette.png"));
        ImageView openView = new ImageView(openIcon);
        openView.setFitWidth(15);
        openView.setFitHeight(15);*/
        
        
        main_contextMenu = new ContextMenu();
        Menu mme0 = new Menu("Check This Event");
        //mme0.setGraphic(openView);
        MenuItem mme0_item_2D = new MenuItem("2D Map");
        MenuItem mme0_item_3D = new MenuItem("3D Map");
        //mme0_item_2D.setGraphic(openView);
        //mme0_item_2D.setGraphic(openView);
        mme0.getItems().addAll(mme0_item_2D, mme0_item_3D);
        Menu mme1 = new Menu("Check Selected Events");
        MenuItem mme1_item_2D = new MenuItem("2D Map");
        MenuItem mme1_item_3D = new MenuItem("3D Map");
        mme1.getItems().addAll(mme1_item_2D, mme1_item_3D);
        Menu mme2 = new Menu("Check All Event");
        MenuItem mme2_item_2D = new MenuItem("2D Map");
        MenuItem mme2_item_3D = new MenuItem("3D Map");
        mme2.getItems().addAll(mme2_item_2D, mme2_item_3D);
        mme0_item_2D.setOnAction(new EventHandler<ActionEvent>() {
            StringBuilder stn_listed = new StringBuilder();
            @Override
            public void handle(ActionEvent event) {
                try {
                    //calc the nearest stations from the vector from epic center to free-field station and draw it
                    /*
                    final Map<String, FFT_SnapShot> temp_select_map = Maps.newConcurrentMap();
                    Utils.get_fftmap().entrySet().forEach(st -> {
                    st.getValue().entrySet().stream().filter(tss -> tss.getValue().is_selected())
                    .forEach(snp -> {
                    temp_select_map.put(snp.getValue().getSSID(), snp.getValue());
                    //System.out.print(snp.getValue().getStationCode()+"_"+snp.getValue().getStartTime()+"  ");
                    });
                    });*/
                    stn_listed.delete(0, stn_listed.length());
                    System.out.println("Check This Event in 2D Map");
                    FX_2D_Map_Window ff = new FX_2D_Map_Window(Utils.get_selectedmap());
                    ff.start(new Stage());
                    stn_listed.append("Stations: ");
                    Utils.get_selectedmap().entrySet().forEach(stn -> stn_listed.append(stn.getKey()).append(", "));
                    ff.setTitle(stn_listed.toString()+" 2D Map");
                } catch (Exception ex) {
                    Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);    
                }
            }
        });
        mme0_item_3D.setOnAction(new EventHandler<ActionEvent>() {
            StringBuilder stn_listed = new StringBuilder();
            @Override
            public void handle(ActionEvent event) {
                try {
                    /*
                    final Map<String, FFT_SnapShot> temp_select_map = Maps.newConcurrentMap();
                    Utils.get_fftmap().entrySet().forEach(st -> {
                    st.getValue().entrySet().stream().filter(tss -> tss.getValue().is_selected())
                    .forEach(snp -> {
                    temp_select_map.put(snp.getValue().getSSID(), snp.getValue());
                    //System.out.print(snp.getValue().getStationCode()+"_"+snp.getValue().getStartTime()+"  ");
                    });
                    });*/
                    //label.setText("Select Menu Item 2");
                    System.out.println("Check This Event in 3D Map");
                    FX_3D_Map_Window ff = new FX_3D_Map_Window(Utils.get_selectedmap());
                    ff.start(new Stage());
                    stn_listed.append("Stations: ");
                    Utils.get_selectedmap().entrySet().forEach(stn -> stn_listed.append(stn.getKey()).append(", "));
                    
                    ff.setTitle(stn_listed.toString()+" 3D Map");
                    
                } catch (Exception ex) {                    
                    Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // Add MenuItem to ContextMenu
        MenuItem mme3 = new MenuItem("Select All");
        MenuItem mme4 = new MenuItem("Deselect All");
        MenuItem mme5 = new MenuItem("Invert Selection");
        MenuItem mme6 = new MenuItem("Remove");
        MenuItem mme7 = new MenuItem("Remove(DB)");
        main_contextMenu.getItems().addAll(mme0, mme1, mme2);
        main_contextMenu.getItems().add(new SeparatorMenuItem());
        main_contextMenu.getItems().addAll(mme3, mme4, mme5);
        main_contextMenu.getItems().add(new SeparatorMenuItem());
        main_contextMenu.getItems().addAll(mme6, mme7);
        mme7.setOnAction((ActionEvent event) -> {
            try {
                /*
                final Map<String, FFT_SnapShot> temp_select_map = Maps.newConcurrentMap();
                Utils.get_fftmap().entrySet().forEach(st -> {
                st.getValue().entrySet().stream().filter(tss -> tss.getValue().is_selected())
                .forEach(snp -> {
                temp_select_map.put(snp.getValue().getSSID(), snp.getValue());
                //System.out.print(snp.getValue().getStationCode()+"_"+snp.getValue().getStartTime()+"  ");
                });
                });*/
                Utils.get_selectedmap().entrySet().forEach(stn -> {
                    stn.getValue().entrySet().forEach(ss -> {
                        try {
                            //remove form db, add missions
                            String del_sql = "DELETE FROM " + stn.getKey() + " WHERE SSID = ?";
                            PreparedStatement pst_s = Utils.getConnection().prepareStatement(del_sql);
                            pst_s.setString(1, ss.getKey());
                            Utils.add_to_op_task_queue(new OP_Task(OP_Engine.delete_from_db, pst_s, "DELETE "+ stn.getKey()+ ", SSID: "+ss.getKey()+"."+System.getProperty("line.separator")));
                            //remove from tree table
                            //table_root.getChildren().remove(fft_treetable.getSelectionModel().getSelectedIndex());
                            //fft_treetable.getColumns().remove(fft_treetable.getSelectionModel().getSelectedIndex());
                            //fft_treetable.refresh();
                            //select_treeitem.remove(fft_treetable.getSelectionModel().getSelectedIndex());
                            for (TreeItem<FFT_SnapShot> child : table_root.getChildren()){
                                if (child.getValue().getStationCode().equals(stn.getKey())) {
                                    //child.getChildren().add(new TreeItem<>(fft_ss));
                                    child.getChildren().remove(fft_treetable.getSelectionModel().getSelectedItem());
                                    
                                }
                            }
                            Utils.get_fft_snp_map().get(stn.getKey()).remove(ss.getKey());
                        } catch (SQLException ex) {
                            Utils.logger.fatal(ex);
                        }
                    });
                });
                fft_treetable.refresh();
                Utils.get_selectedmap().clear();
            } catch (Exception ex) {
                logger.fatal(ex);
            } 
        });
        
        
        //init root
        fft_treetable.setShowRoot(false);
        FFT_SnapShot ffts_root = new FFT_SnapShot();
        ffts_root.setStationCode("Root");
        table_root = new TreeItem<>(ffts_root);
        table_root.setExpanded(true);
        /*
        Image imageDecline = new Image(getClass().getResourceAsStream("/cancel_16.png"));
        search_button.setGraphic(new ImageView(imageDecline));*/
        search_button.setDisable(true);
        logs_textarea.setEditable(false);
        /*
        gp.setGridLinesVisible(true);
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setPadding(new Insets(0, 0, 0, 0));
        */
        //fft_treetable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //treetable_selectedInd
        
        
        fft_treetable.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t) -> {
            main_contextMenu.hide();//clear first
            //update check box
            if (Utils.get_selectedmap().size() > 0) {
                main_contextMenu.getItems().get(0).setDisable(false);
                main_contextMenu.getItems().get(1).setDisable(false);
            } else {
                main_contextMenu.getItems().get(0).setDisable(true);
                main_contextMenu.getItems().get(1).setDisable(true);
            }
            
            if(t.getButton() == MouseButton.PRIMARY) {
                System.out.println(fft_treetable.getSelectionModel().getSelectedItem());
                
                
                
            }
            if(t.getButton() == MouseButton.SECONDARY) {
                if (fft_treetable.getSelectionModel().getSelectedIndex() > 0) {
                    main_contextMenu.show(fft_treetable, t.getScreenX(), t.getScreenY());
                    // When user right-click on Circle
                }
            }
            
            
        });
        sc_val.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches(sort_list_pattern)) {
                    search_button.setDisable(false);
                } else {
                    search_button.setDisable(true);
                }
            }
        });
        sort_list.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //label_status.setText(newValue);
                //to applay rex
                if (newValue.equals("Time(yyyy/mm/dd)")) {
                    sort_list_pattern = "^[0-9]{4}/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])$";
                } else if (newValue.equals("StationName")){
                    
                } else if (newValue.equals("Magnitude(Ml)")){
                    
                } else if (newValue.equals("DepthOfFocus(Km)")){
                    
                } else if (newValue.equals("Vs30(M/s)")){
                    
                } else if (newValue.equals("Ground Level(M)")){
                    
                } else if (newValue.equals("Z10(M/s)")){
                    
                } else if (newValue.equals("Kappa(K)")){
                    
                } else if (newValue.equals("EpicenterDistance(Km)")){
                    
                } else if (newValue.equals("Longitude")){
                    
                } else if (newValue.equals("Latitude")){
                    
                } else if (newValue.equals("Axial U Max Hz(0~20)")){
                    
                } else if (newValue.equals("Axial U Max Hz(0~50)")){
                    
                } else if (newValue.equals("Axial U Max Hz(All)")){
                    
                } else if (newValue.equals("Axial N Max Hz(0~20)")){
                    
                } else if (newValue.equals("Axial N Max Hz(0~50)")){
                    
                } else if (newValue.equals("Axial N Max Hz(All)")){
                    
                } else if (newValue.equals("Axial E Max Hz(0~20)")){
                    
                } else if (newValue.equals("Axial E Max Hz(0~50)")){
                    
                } else if (newValue.equals("Axial E Max Hz(All)")){
                    
                } else {
                    
                }
            }
        });
        sort_list.setItems(Utils.sort_list);
        logic_list.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                label_status.setText(newValue);
            }
        });
        logic_list.setItems(Utils.logic_list);
        y_numerator.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
            }
        });
        y_numerator.setItems(Utils.y_numerator_list);
        y_denominator.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
            }
        });
        y_denominator.setItems(Utils.y_denominator_list);
        
        /*
        gp.heightProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        fft_treetable.setLayoutY(newValue.doubleValue()-400D-5D-200D-16D-10D);
        logs_textarea.setLayoutY(newValue.doubleValue()-200D-16D-10D);
        label_status.setLayoutY(newValue.doubleValue()-16D-5D);
        }
        });*/
        /*
        y_cb_pga.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        y_cb_hz.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        y_cb_hz_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " selected");
        } else {
        y_cb_hz_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " deselected");
        }
        });
        
        y_cb_ml.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        y_cb_vs30.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        y_cb_dof.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        y_cb_ed.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        //----------------------------------------------------
        x_cb_pga.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        x_cb_hz.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        x_cb_ml.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        x_cb_ml_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " selected");
        } else {
        x_cb_ml_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " deselected");
        }
        });
        
        x_cb_vs30.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        x_cb_vs30_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " selected");
        } else {
        x_cb_vs30_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " deselected");
        }
        });
        
        x_cb_dof.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        x_cb_dof_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " selected");
        } else {
        x_cb_dof_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " deselected");
        }
        });
        
        x_cb_ed.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        x_cb_ed_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " selected");
        } else {
        x_cb_ed_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckBox)event.getSource()).getText() + " deselected");
        }
        });
        
        x_cb_st.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });
        
        x_cb_t.setOnAction((ActionEvent event) -> {
        if (((CheckBox)event.getSource()).isSelected()) {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " selected");
        } else {
        System.out.println
        ("\t\t\t\t" + ((CheckBox)event.getSource())
        .getText() + " deselected");
        }
        });*/
        /*
        detrend_checkitem_del_mean.setOnAction((ActionEvent event) -> {
        if (((CheckMenuItem)event.getSource()).isSelected()) {
        Configs.detrend_del_mean_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckMenuItem)event.getSource()).getText() + " selected");
        } else {
        Configs.detrend_del_mean_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckMenuItem)event.getSource()).getText() + " deselected");
        }
        });
        
        detrend_checkitem_least_square.setOnAction((ActionEvent event) -> {
        if (((CheckMenuItem)event.getSource()).isSelected()) {
        Configs.detrend_least_square_selected = true;
        //System.out.println("\t\t\t\t" + ((CheckMenuItem)event.getSource()).getText() + " selected");
        } else {
        Configs.detrend_least_square_selected = false;
        //System.out.println("\t\t\t\t" + ((CheckMenuItem)event.getSource()).getText() + " deselected");
        }
        });*/
        
        /*
        gp.widthProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        fft_treetable.setMinWidth(newValue.doubleValue()-10D);
        menu_bar.setMinWidth(newValue.doubleValue());
        logs_textarea.setMinWidth(newValue.doubleValue()-10D);
        }
        });*/
        
        /*
        logs.textProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
        logs.selectPositionCaret(logs.getLength());
        logs.deselect();
        });*/
        menuitem_about.setOnAction((ActionEvent event) -> {
            Alert alert = new Alert(AlertType.INFORMATION); // 實體化Alert對話框物件，並直接在建構子設定對話框的訊息類型
            alert.setTitle("About"); //設定對話框視窗的標題列文字
            alert.setHeaderText(null);
            alert.setContentText("This program is written by \nHe ZongYou.");
            alert.show();
        });
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public class Read_db_task extends RecursiveAction {

        @Override
        protected void compute() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    

    public class Read_file_task extends RecursiveAction {

        String fn;
        String file_path;//no file name
        int eff_count = 0;
        
        public Read_file_task(String fn) {
            this.fn = fn;

        }

        @Override
        protected void compute() {
            TimeMeasure tm = new TimeMeasure();
            tm.begin_unit_measure();
            try {
                //RandomAccessFile raf = new RandomAccessFile(fn, "r");
                BufferedReader br = null;
                String sCurrentLine = null;
                br = new BufferedReader(new FileReader(fn));
                FFT fft = new FFT();
                fft.setSeismic_StartTime_from_path(fn);
                fft.setFilePath(Paths.get(fn).getParent().toString());
                fft.setFileName(Paths.get(fn).getFileName().toString());
                while((sCurrentLine = br.readLine()) != null) {
                    String temp_str = sCurrentLine;
                    if (temp_str.startsWith("#")) {
                        if (temp_str.contains("Code")) {
                            eff_count++;
                            fft.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                        }
                        if (temp_str.contains("Kind")) {
                            eff_count++;
                            fft.setInstrumentKind(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                        }
                        if (temp_str.contains("StartTime")) {
                            eff_count++;
                            String mod = temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim();
                            mod = mod.replace("-", " ");
                            fft.setAccelerometer_StartTime(mod);
                        }
                        if (temp_str.contains("sec")) {
                            eff_count++;
                            fft.setRecordLength(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                        }
                        if (temp_str.contains("Hz")) {
                            eff_count++;
                            fft.setSampleRate(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                            fft.initDataArray(fft.getRecordLength().multiply(fft.getSampleRate()).intValue(), 4);
                        }
                    } else {//stage 2
                        fft.setArrayO(temp_str);
                    }
                }
                br.close();
                //raf.close();
                
                if (eff_count >= 5) {
                    try {
                        fft.transpose();
                        fft.calc_SSID();
                        fft.transferDataToFFT();
                    } catch (NoSuchAlgorithmException ex) {
                        Utils.logger.fatal(ex);
                    }
                    
                    Utils.add_to_fft_station_name_set(fft.getStationCode());
                    //if (!Utils.get_ssidqueue().contains(fft.getSSID())) {
                    //no data in map
                    Map<String, FFT_SnapShot> temp_fft_snp_datamap = Utils.get_fft_snp_map().get(fft.getStationCode());
                    
                    //SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
                    //System.out.println(fft.getStationCode());

                    FFT_SnapShot fft_ss = new FFT_SnapShot(fft.getSSID(), fft.getStationCode(), fft.getInstrumentKind(), format.parse(fft.getSeismic_StartTime()),
                                                            fft.getMax_U_Hz(), fft.getMax_N_Hz(), fft.getMax_E_Hz(),
                                                            fft.getMax_U_Hz_20(), fft.getMax_N_Hz_20(), fft.getMax_E_Hz_20(),
                                                            fft.getPGA_U(), fft.getPGA_N(), fft.getPGA_E());
                    fft_ss.setFileName(fft.getFileName());
                    fft_ss.setFilePath(fft.getFilePath());
                    //get station
                    Station stt_temp = Utils.get_StationList().stream().filter(st -> st.getStationName().equals(fft_ss.getStationCode())).findAny().orElse(null);
                    //check sub st name
                    if (stt_temp == null) {
                        stt_temp = Utils.get_StationList().stream().filter(st -> st.getSubStationName().equals(fft_ss.getStationCode())).findAny().orElse(null);
                    }
                    if (Utils.get_seismic_map().containsKey(fft_ss.getStartTime())) {
                        //System.out.println(fft_temp.getSeismic_StartTime());
                        Seismic sc_temp = Utils.get_seismic_map().get(fft_ss.getStartTime());
                        fft_ss.setDepthOfFocus(sc_temp.getDepth_Of_Focus().doubleValue());
                        //System.out.println(sc_temp.getDepth_Of_Focus().doubleValue());
                        //fft_temp.setEpicenterDistance(fft_temp.calc_distance(sc_temp.getLongitude().doubleValue(), sc_temp.getLatitude().doubleValue(), Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()), 0D, 0D));
                        fft_ss.setMagnitude(sc_temp.getMagnitude().doubleValue());
                        //System.out.println("Match ML."+sc_temp.getMagnitude().doubleValue());
                    } else {
                        //System.out.println("No Match...("+format.format(fft_ss.getStartTime())+")");
                        logger.fatal("No Match...("+format.format(fft_ss.getStartTime())+")");

                    }
                    if (stt_temp != null) {
                        fft_ss.setVs30(Double.valueOf(stt_temp.getVs30()));
                        fft_ss.setGL(Double.valueOf(stt_temp.getGround_Level()));
                        fft_ss.setZ10(Double.valueOf(stt_temp.getZ10()));
                        fft_ss.setK(Double.valueOf(stt_temp.getKappa()));
                        fft_ss.setStationLongitude(Double.valueOf(stt_temp.getLongitude()));
                        fft_ss.setStationLatitude(Double.valueOf(stt_temp.getLatitude()));
                    }

                    if (temp_fft_snp_datamap != null) {
                        temp_fft_snp_datamap.put(fft.getSSID(), fft_ss);
                    } else {//null
                        Map<String, FFT_SnapShot> temp_emptyff_snp_tmap = Maps.newConcurrentMap();
                        temp_emptyff_snp_tmap.put(fft.getSSID(), fft_ss);
                        Utils.get_fft_snp_map().put(fft.getStationCode(), temp_emptyff_snp_tmap);
                    }
                    //fft.draw_fft_chart_onebyone_20();
                    //}
                    try {
                        Utils.DB_create_fft_by_station_table(fft.getStationCode());
                        String sql_s = "SELECT SSID FROM "+ fft.getStationCode() +" WHERE SSID = '" + fft.getSSID() + "'";
                        PreparedStatement pst_s = Utils.getConnection().prepareStatement(sql_s);
                        ResultSet rs_s = pst_s.executeQuery();
                        if (!rs_s.next()) {
                            //byte[] data = SerializationUtils.serialize(fft);//original
                            //byte[] data = SerializationUtils.serialize(Utils.get_fft_snp_map().get(fft.getStationCode()).get(fft.getSSID()));//snapshot
                            byte[] data = SerializationUtils.serialize(fft_ss);
                            if (data.length < 10) {
                                System.out.println(fft.getStationCode()+":"+fft.getInstrumentKind()+":"+fft.getSeismic_StartTime()+":"+fft.getSSID());
                            }
                            
                            String sql = "insert into "+ fft.getStationCode() + " (SSID, Data) values (?, ?)";
                            PreparedStatement pst = Utils.getConnection().prepareStatement(sql);
                            pst.setString(1, fft.getSSID()); 
                            pst.setBytes(2, data);
                            Utils.add_to_op_task_queue(new OP_Task(OP_Engine.insert_or_update_into_db, pst, "Insert Into "+ fft.getStationCode() + ", SSID: "+fft.getSSID()+"."+System.getProperty("line.separator")));
                        } else {
                            Platform.runLater(() -> {
                                Utils.check_logs_textarea();
                                logs_textarea.appendText("SSID("+Utils.get_filequeue().size()+"): "+fft.getSSID()+" already exist."+System.getProperty("line.separator"));
                                logs_textarea.selectPositionCaret(logs_textarea.getLength());
                            });
                        }
                        pst_s.close();
                        rs_s.close();
                    } catch (SQLException ex) {
                        Utils.logger.fatal(ex);
                    }
                    
                }
                //copy file to Done folder and del the original one
                
                //1.create done folder
                /*
                String export_dir = file_path.replace("undone", "done");
                File new_path = new File(export_dir);
                //System.out.println(new_path.getAbsolutePath());
                if (!new_path.exists()) {
                    if (new_path.mkdirs()) {
                        Utils.check_logs_textarea();
                        logs_textarea.appendText("Done folder does not exist. Create one.");
                        logs_textarea.selectPositionCaret(logs_textarea.getLength());
                    } else {
                        Utils.check_logs_textarea();
                        logs_textarea.appendText("Done folder exist.");
                        logs_textarea.selectPositionCaret(logs_textarea.getLength());
                    }
                }
                Files.copy(new File(fn).toPath(), new_path.toPath(), StandardCopyOption.REPLACE_EXISTING);
                */
            } catch (FileNotFoundException ex) {
                Utils.logger.fatal(ex);
            } catch (IOException | ParseException ex) {
                Utils.logger.fatal(ex);
            }
            
            tm.stop_unit_measure();
            
            
            
            Platform.runLater(() -> {
                Utils.get_filequeue().remove(fn);
                if (Utils.get_filequeue().size() > 0) {
                    label_status.setText(Utils.get_filequeue().size() + " files in queue. Remaining time: "+ tm.get_escapetime(Utils.get_filequeue().size()));
                } else {
                    et = System.currentTimeMillis();
                    //System.out.println("result:"+(et-st)+"ms.");
                    label_status.setText("done(" + df.format((et - st) / 1000D) + "s)");
                    logs_textarea.appendText("<==========================================>"+System.getProperty("line.separator"));
                    while (Utils.file_with_wrong_queue.size() > 0) {
                        Utils.check_logs_textarea();
                        logs_textarea.appendText(Utils.file_with_wrong_queue.poll());
                        logs_textarea.selectPositionCaret(logs_textarea.getLength());
                    }
                //show filters after calculate


                //sort by station, ML, time, distance from center
                }
                
            });
        }
    }

    private void choose_dic() {
        DirectoryChooser DicChooser = new DirectoryChooser();
        DicChooser.setTitle("Open Resource File");
        File dir = DicChooser.showDialog(MainApp.stage);
        if (dir != null) {
            try {
                label_status.setText(dir.getCanonicalPath());
                Utils.set_MainPath(dir.getCanonicalPath());
                Utils.check_logs_textarea();
                logs_textarea.appendText("Select Folder "+dir.getCanonicalPath()+"."+System.getProperty("line.separator"));
                logs_textarea.selectPositionCaret(logs_textarea.getLength());
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
            }
        } else {
            label_status.setText("idle");
        }
    }
    
    int lc = 0;
    //load from excel
    private void process_station_data_into_db(String path, int type) throws FileNotFoundException, IOException {//0 insert, 1 update
        Configs.Work_Pool.submit(() -> {
            try {
                //Connection con = Utils.getConnection();
                File station_file = new File(path);
                FileInputStream fis = new FileInputStream(station_file);
                // Finds the workbook instance for XLSX file
                
                //get ext
                String ext = path.substring(path.lastIndexOf("."));
                Workbook station_WorkBook;
                XSSFWorkbook station_WorkBook_x;
                Sheet sheet = null;
                if (ext.equals(".xls")) {
                    station_WorkBook = WorkbookFactory.create(fis);
                    sheet = station_WorkBook.getSheetAt(0);
                } else if (ext.equals(".xlsx")) {
                    station_WorkBook_x = XSSFWorkbookFactory.createWorkbook(fis);
                    sheet = station_WorkBook_x.getSheetAt(0);
                }
                // Return first sheet from the XLSX workbook
                
                //getPhysicalNumberOfRows
                for (int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++) {
                    Row row = sheet.getRow(i);
                    lc = sheet.getPhysicalNumberOfRows()-i-1;
                    //lc++;
                    StringBuffer sb = new StringBuffer();
                    Cell cell;
                    if(row != null) {
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            cell = row.getCell(j);
                            sb.append(cell.toString()+ "  ");
                            //System.out.print(cell.toString()+"  ");//取出j列j行的值
                        }
                        //System.out.println();
                        try {
                            switch (type) {
                                case 0:
                                    Utils.insert_StationData(row);
                                    break;
                                case 1:
                                    Utils.update_StationData(row);
                                    break;
                                default:
                                    break;
                            } 
                        } catch (SQLException ex) {
                            Utils.logger.fatal(ex);
                        }
                    }
                    
                    Platform.runLater(() -> {
                        logs_textarea.appendText(sb+"."+System.getProperty("line.separator"));
                        Utils.check_logs_textarea();
                        logs_textarea.selectPositionCaret(logs_textarea.getLength());
                        if (lc > 0) {
                            label_status.setText(lc+" Stations to write.");
                        } else {
                            label_status.setText("Done");
                        }   
                    });
                    
                    
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        Utils.logger.fatal(ex);
                    }
                }
                fis.close();
            } catch (FileNotFoundException ex) {
                Utils.logger.fatal(ex);
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
            } catch (EncryptedDocumentException ex) {
                Utils.logger.fatal(ex);
            } catch (InvalidFormatException ex) {
                Utils.logger.fatal(ex);
            }
        });
    }
    
    @FXML
    private void import_seismic_event_data_ButtonAction(ActionEvent event) throws IOException {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Seismic Event File");
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            label_status.setText(dir.getCanonicalPath());
            Utils.set_ProjectPath(dir.getCanonicalPath());
            import_seismic_data_into_db(dir.getCanonicalPath());
        } else {
            label_status.setText("idle");
        }
    }
    
    //import from excel
    private void import_seismic_data_into_db(String path) {
        Configs.Work_Pool.execute(() -> {
            try {
                File station_file = new File(path);
                FileInputStream fis = new FileInputStream(station_file);
                // Finds the workbook instance for XLSX file
                //get ext
                String ext = path.substring(path.lastIndexOf("."));
                Workbook station_WorkBook;
                XSSFWorkbook station_WorkBook_x;
                Sheet sheet = null;
                int sheet_num = 0;
                if (ext.equals(".xls")) {
                    station_WorkBook = WorkbookFactory.create(fis);
                    sheet_num = station_WorkBook.getNumberOfSheets();
                    for (int si = 0; si < sheet_num; si++) {
                        sheet = station_WorkBook.getSheetAt(si);
                        process_seismic_event_data_from_excel(sheet);
                    }
                    Platform.runLater(() -> label_status.setText(Utils.get_seismic_map().size()+" seismic data.(done)"));
                    
                } else if (ext.equals(".xlsx")) {
                    station_WorkBook_x = XSSFWorkbookFactory.createWorkbook(fis);
                    sheet_num = station_WorkBook_x.getNumberOfSheets();
                    for (int si = 0; si < sheet_num; si++) {
                        sheet = station_WorkBook_x.getSheetAt(si);
                        process_seismic_event_data_from_excel(sheet);
                    }
                    Platform.runLater(() -> label_status.setText(Utils.get_seismic_map().size()+" seismic data.(done)"));
                }
                
                /*
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FX_Main_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                
                
                fis.close();
            } catch (FileNotFoundException ex) {
                Utils.logger.fatal(ex);
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
            } catch (EncryptedDocumentException | InvalidFormatException ex) {
                Utils.logger.fatal(ex);
            }
        });
    }
    
    private void process_seismic_event_data_from_excel(Sheet sheet) {
        //SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
        for (int i = 0; i <= sheet.getPhysicalNumberOfRows() ; i++) {
            Row row = sheet.getRow(i);
            try {
                double data_num = row.getCell(8).getNumericCellValue();
                //ignore data number import all event
                //if (data_num > 0D) {
                    //日期 震央經度 震央緯度 深度 芮氏規模 自由場資料筆數
                    Seismic sc_temp = new Seismic();
                    for (int j = 2; j <= 7; j++) {
                        Cell cell = row.getCell(j);
                        switch (j) {
                            case 2://date
                                sc_temp.setStartTime(cell.getDateCellValue());
                                break;
                            case 3://long
                                sc_temp.setLongitude(cell.getNumericCellValue());
                                break;
                            case 4://lat
                                sc_temp.setLatitude(cell.getNumericCellValue());
                                break;
                            case 5://depth
                                sc_temp.setDepth_Of_Focus(cell.getNumericCellValue());
                                break;
                            case 6://ml
                                sc_temp.setMagnitude(cell.getNumericCellValue());
                                break;
                            case 7://numbers of data
                                /*
                                String num_str = cell.getStringCellValue();
                                int post = num_str.indexOf("-");
                                num_str = num_str.substring(0, post);
                                sc_temp.setData_Num(Integer.valueOf(num_str.trim()));*/
                                try {
                                    sc_temp.setData_Num((int)data_num);
                                } catch (Exception ex) {
                                    sc_temp.setData_Num(0);
                                }
                                break;
                        }
                        //logs.appendText("\n");
                    }
                    //add nearest fault and vs30
                    sc_temp.setPredictVs30(get_kriging_result(sc_temp.getLongitude().doubleValue(), sc_temp.getLatitude().doubleValue()));
                    Map<String, Double> nearest_af = Maps.newConcurrentMap();
                    
                    Utils.af_queue.stream().forEach(af -> {
                        List<Double> af_dist = Lists.newArrayList();
                        af.getPointList().stream().forEach(pp -> {
                            GeodeticCalculator geoCalc = new GeodeticCalculator();
                            GlobalCoordinates af_p = new GlobalCoordinates(pp.lat_WGS, pp.lon_WGS);
                            GlobalCoordinates sc_p = new GlobalCoordinates(sc_temp.getLatitude().doubleValue(), sc_temp.getLongitude().doubleValue());
                            
                            GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, sc_p, af_p);
                            af_dist.add(geoCurve.getEllipsoidalDistance()/1000D);
                        });
                        Collections.sort(af_dist);
                        
                        nearest_af.put(af.getFaultName(), af_dist.get(0));
                    });
                    Map<String, Double> sorted_dist_map = nearest_af.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                    
                    Map.Entry<String, Double> entry = sorted_dist_map.entrySet().iterator().next();
                    sc_temp.setNearestActiveFault_dist(entry.getValue());
                    sc_temp.setNearestActiveFault(entry.getKey());
                    //System.out.println(format.format(sc_temp.getStartTime())+": "+sc_temp.getNearestActiveFault()+", "+sc_temp.getNearestActiveFault_dist()+" km");
                    
                    
                    Platform.runLater(() -> {
                        Utils.check_logs_textarea();
                        logs_textarea.appendText("date:"+format.format(sc_temp.getStartTime())+", long:" + sc_temp.getLongitude()+", lat:" + sc_temp.getLatitude() +
                                        ", depth(km):" + sc_temp.getDepth_Of_Focus()+", ml:" + sc_temp.getMagnitude()+", data number:" + sc_temp.getData_Num()+"."+System.getProperty("line.separator"));
                        logs_textarea.selectPositionCaret(logs_textarea.getLength());
                    });
                    //check event
                    String sql_s = "SELECT StartTime FROM Seismic WHERE StartTime = '" + format.format(sc_temp.getStartTime()) + "'";
                    PreparedStatement pst_s = Utils.getConnection().prepareStatement(sql_s);
                    ResultSet rs_s = pst_s.executeQuery();
                    if (!rs_s.next()) {
                        //insert into DB
                        String sql = "insert into Seismic (StartTime, Data) values (?, ?)";
                        PreparedStatement pst;
                        try {
                            byte[] data = SerializationUtils.serialize(sc_temp);
                            pst = Utils.getConnection().prepareStatement(sql);
                            pst.setString(1, format.format(sc_temp.getStartTime())); 
                            pst.setBytes(2, data);
                            Utils.add_to_op_task_queue(new OP_Task(OP_Engine.insert_or_update_into_db, pst));
                            Utils.add_to_seismic_map(sc_temp.getStartTime(), sc_temp);
                        } catch (SQLException ex) {
                            Utils.logger.fatal(ex);
                        }

                        Platform.runLater(() -> label_status.setText(Utils.get_seismic_map().size()+" Insert seismic data."));
                    } else {
                        
                        Platform.runLater(() -> {
                            Utils.check_logs_textarea();
                            logs_textarea.appendText("Event already exist."+System.getProperty("line.separator"));
                            logs_textarea.selectPositionCaret(logs_textarea.getLength());
                        });
                    }
                    
                    
                    
                //}
            } catch (Exception exp) {
                //System.out.println("no numbers");
                Platform.runLater(() -> label_status.setText(Utils.get_seismic_map().size()+" seismic data."));
                //do nothing
            }
            try {
                Thread.sleep(1L);
            } catch (InterruptedException ex) {
                Utils.logger.fatal(ex);
            }
        }
    }

    public Label get_label_status() {
        return label_status;
    }
    
    public TextArea get_logs_textarea() {
        return logs_textarea;
    }
    
    public TreeTableView get_fft_treetable() {
        return fft_treetable;
    }
    
    public TreeTableColumn<FFT_SnapShot, String> get_ttc_station() {
        return ttc_station;
    }
    
    public TreeTableColumn<FFT_SnapShot, String> get_ttc_starttime() {
        return ttc_starttime;
    }
    
    public TreeTableColumn get_ttc_magnitude() {
        return ttc_magnitude;
    }
    
    public TreeTableColumn get_ttc_U20() {
        return ttc_U20;
    }
    
    public TreeTableColumn get_ttc_N20() {
        return ttc_N20;
    }
    
    public TreeTableColumn get_ttc_E20() {
        return ttc_E20;
    }
    
    public TreeTableColumn get_ttc_dof() {
        return ttc_dof;
    }
    
    public TreeTableColumn get_ttc_vs30() {
        return ttc_vs30;
    }
    
    public TreeTableColumn get_ttc_gl() {
        return ttc_gl;
    }
    
    public TreeTableColumn get_ttc_z10() {
        return ttc_z10;
    }
    
    public TreeTableColumn get_ttc_k() {
        return ttc_k;
    }
    
    public TreeTableColumn get_ttc_ed() {
        return ttc_ed;
    }
    
    public TreeTableColumn get_ttc_long() {
        return ttc_long;
    }
    
    public TreeTableColumn get_ttc_lat() {
        return ttc_lat;
    }
    
    public TreeItem<FFT_SnapShot> get_table_root() {
        return table_root;
    }
    
    public TreeTableColumn<FFT_SnapShot, Boolean> get_select_box() {
        return select_box;
    }
    
    public void add_treenodes(String stationname) {
        //add stations
        Utils.add_to_fft_station_name_set(stationname);
        FFT_SnapShot ffts_temp = new FFT_SnapShot();
        ffts_temp.setStationCode(stationname);
        TreeItem<FFT_SnapShot> st_temp = new TreeItem<>(ffts_temp);
        st_temp.setExpanded(true);
        /*
        TreeItem<String> temp = new TreeItem<>(t_t);
        st_temp.getChildren().add(temp);*/
        //first layer
        table_root.getChildren().add(st_temp);
    }
    
    private void init_nodes() {
        //SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
        fft_treetable.setEditable(true);
        
        ttc_station.setCellValueFactory(st -> new ReadOnlyStringWrapper(st.getValue().getValue().getStationCode()));
        
        
        select_box.setCellFactory(new Callback<TreeTableColumn<FFT_SnapShot,Boolean>,TreeTableCell<FFT_SnapShot,Boolean>>() {
            //final BooleanProperty selected = new SimpleBooleanProperty();
            //CheckBox checkBox = new CheckBox();
            @Override
            public TreeTableCell<FFT_SnapShot,Boolean> call(TreeTableColumn<FFT_SnapShot,Boolean> p) {
                CheckBoxTreeTableCell<FFT_SnapShot,Boolean> cell = new CheckBoxTreeTableCell<FFT_SnapShot,Boolean>();
                cell.setAlignment(Pos.CENTER);
                
                
                //checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> 
                //((FFT_SnapShot)cell.getTreeTableRow().getItem()).set_selected(isSelected));
               
                return cell;
            }
        });
        //select_box.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(select_box));
        //select_box.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(select_box));
        select_box.setCellValueFactory(cellData -> {
            // binding the cell property with the model
            Boolean is_selected = cellData.getValue().getValue().is_selected();
            BooleanProperty selected = new SimpleBooleanProperty(is_selected);
            
            selected.addListener((obs, oldVal, newVal) -> {
                
                String st_code = cellData.getValue().getValue().getStationCode();
                String SSID = null;
                try {
                    SSID = cellData.getValue().getValue().getSSID();
                    //System.out.println(SSID);
                } catch (Exception e) {//select the tree
                    //System.out.println("node is selected.");
                    cellData.getValue().getValue().set_selected(false);
                }
                if (SSID != null) {
                    if (newVal) {//select
                        if (Utils.get_selectedmap().containsKey(st_code)) {//check station code exist
                            Utils.get_selectedmap().get(st_code).put(SSID, cellData.getValue().getValue());
                        } else {//create station code in layer 1
                            Map<String, FFT_SnapShot> temp_emptyfftmap = Maps.newConcurrentMap();
                            temp_emptyfftmap.put(SSID, cellData.getValue().getValue());
                            Utils.get_selectedmap().put(st_code, temp_emptyfftmap);
                            //
                            fft_treetable.refresh();
                        }
                        System.out.println(fft_treetable.getSelectionModel().getSelectedItem());
                        select_treeitem.add((TreeItem)fft_treetable.getSelectionModel().getSelectedItem());
                    } else {//deselect
                        if (Utils.get_selectedmap().containsKey(st_code)) {
                            if (Utils.get_selectedmap().get(st_code).containsKey(SSID)) {
                                Utils.get_selectedmap().get(st_code).remove(SSID);
                                select_treeitem.remove((TreeItem)fft_treetable.getSelectionModel().getSelectedItem());
                                fft_treetable.refresh();
                            }
                        }
                    }
                    cellData.getValue().getValue().set_selected(newVal);
                    //System.out.println(cellData.getValue().getValue().getStationCode()+"is selected: "+newVal);
                    Utils.get_selectedmap().entrySet().forEach(st -> {
                        st.getValue().entrySet().stream().filter(tss -> tss.getValue().is_selected())
                            .forEach(snp -> {
                                System.out.print(snp.getValue().getStationCode()+" - "+snp.getValue().getStartTime()+"  ");
                            });

                    });
                    System.out.println();
                } else {
                    cellData.getValue().getChildren().forEach(child -> child.getValue().set_selected(newVal));
                    //cellData.getValue().getValue().set_selected(newVal);
                    selected.set(newVal);
                    if (newVal) {//select whole node
                        Utils.get_fft_snp_map().get(st_code).entrySet().forEach(st -> {
                            st.getValue().set_selected(true);
                            if (st.getValue().getSSID() != null) {
                                if (Utils.get_selectedmap().containsKey(st_code)) {//check station code exist
                                    Utils.get_selectedmap().get(st_code).put(st.getValue().getSSID(), st.getValue());
                                } else {//create station code in layer 1
                                    Map<String, FFT_SnapShot> temp_emptyfftmap = Maps.newConcurrentMap();
                                    temp_emptyfftmap.put(st.getValue().getSSID(), st.getValue());
                                    Utils.get_selectedmap().put(st_code, temp_emptyfftmap);
                                    fft_treetable.refresh();
                                }
                            }
                        });
                    } else {
                        Utils.get_selectedmap().remove(st_code);
                        fft_treetable.refresh();
                    }
                    Utils.get_selectedmap().entrySet().forEach(st -> {
                        st.getValue().entrySet().stream().filter(tss -> tss.getValue().is_selected())
                            .forEach(snp -> {
                                System.out.print(snp.getValue().getStationCode()+" - "+snp.getValue().getStartTime()+"  ");
                            });

                    });
                    System.out.println();
                }
            });
            return selected;
        });
        
        
        ttc_starttime.setCellValueFactory(stt -> {
            try {
                return new ReadOnlyStringWrapper(format.format(stt.getValue().getValue().getStartTime()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_magnitude.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMagnitude()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_U20.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMax_U_Hz_20()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_N20.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMax_N_Hz_20()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_E20.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMax_E_Hz_20()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_U.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMax_U_Hz()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_N.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMax_N_Hz()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        ttc_E.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getMax_E_Hz()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_dof.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getDepthOfFocus()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_vs30.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getVs30()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_gl.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getGL()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_z10.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getZ10()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_k.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getK()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_ed.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.df.format(ma.getValue().getValue().getEpicenterDistance()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_long.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.coord_df.format(ma.getValue().getValue().getSeismicLongitude()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });

        ttc_lat.setCellValueFactory(ma -> {
            try {
                return new ReadOnlyStringWrapper(Configs.coord_df.format(ma.getValue().getValue().getSeismicLatitude()));
            } catch (Exception exp) {
                return null;//null, do nothing.
            }
        });
        //System.out.println(t_t);
        fft_treetable.setRoot(table_root);
    }
    
    boolean do_rename = false;
    public void load_tp() {
        //check if exist
        String tp_path = Utils.get_MainPath()+"/temp.tmp";
        String af_path = Utils.get_MainPath()+"/AF_temp.tmp";
        //System.out.println(tp_path);
        File temp_p_file = new File(tp_path);
        File temp_af_file = new File(af_path);
        init_nodes();
        
        if (temp_p_file.exists()) {
            Platform.runLater(() -> label_status.setText("Temp file exists. Loading..."));
            
                FileInputStream fis = null;
                ObjectInputStream ois = null;
                try {
                    fis = new FileInputStream(tp_path);
                    ois = new ObjectInputStream(fis);
                    //dump
                    Map<String, Map<String, FFT_SnapShot>> temp_map = (Map<String, Map<String, FFT_SnapShot>>) ois.readObject();
                    temp_map.forEach(Utils.get_fft_snp_map()::putIfAbsent);
                    Utils.get_fft_snp_map().forEach((k1,v1) -> {
                        add_treenodes(k1);
                        ((Map<String, FFT_SnapShot>)v1).forEach((k2,v2) -> {
                            TreeItem<FFT_SnapShot> ss_temp = new TreeItem<>(v2);
                            ss_temp.setExpanded(false);
                            Platform.runLater(() -> {
                                for (TreeItem<FFT_SnapShot> child : table_root.getChildren()){
                                    if (child.getValue().getStationCode().equals(v2.getStationCode())) {
                                        child.getChildren().add(new TreeItem<>(v2));
                                    }
                                }
                            });
                            fft_treetable.setRoot(table_root);
                        });
                        //System.out.println(k.toString());
                    });
                    System.out.println(Utils.get_fft_snp_map().entrySet().parallelStream().mapToInt(m -> m.getValue().size()).sum());
                    
                    Platform.runLater(() -> label_status.setText("Done. "+"data."));
                    fis.close();
                    ois.close();
                } catch (FileNotFoundException ex) {
                    Utils.logger.fatal(ex);
                    try {
                        fis.close();
                        ois.close();
                    } catch (IOException ex1) {
                        Utils.logger.fatal(ex1);
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    Utils.logger.fatal(ex);
                    try {
                        fis.close();
                        ois.close();
                    } catch (IOException ex1) {
                        Utils.logger.fatal(ex1);
                    }
                    do_rename = true;
                }
            
            
        } else {
            Platform.runLater(() -> label_status.setText("Temp file does not exist."));
        }
        
        if (temp_af_file.exists()) {
            Platform.runLater(() -> label_status.setText("AF Temp file exists. Loading..."));
            
                FileInputStream fis = null;
                ObjectInputStream ois = null;
                try {
                    fis = new FileInputStream(af_path);
                    ois = new ObjectInputStream(fis);
                    //dump
                    Queue<Active_Fault> temp_af = (Queue<Active_Fault>) ois.readObject();
                    temp_af.forEach(af -> Utils.af_queue.add(af));
                    
                    System.out.println(Utils.af_queue.size());
                    Platform.runLater(() -> label_status.setText("Done. "+"AF data."));
                    fis.close();
                    ois.close();
                } catch (FileNotFoundException ex) {
                    Utils.logger.fatal(ex);
                    try {
                        fis.close();
                        ois.close();
                    } catch (IOException ex1) {
                        Utils.logger.fatal(ex1);
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    Utils.logger.fatal(ex);
                    try {
                        fis.close();
                        ois.close();
                    } catch (IOException ex1) {
                        Utils.logger.fatal(ex1);
                    }
                }
        } else {
            Platform.runLater(() -> label_status.setText("AF Temp file does not exist."));
        }
        
        
        if (do_rename) {
            Path rnFile = Paths.get(tp_path);
            try {
                Files.move(rnFile, rnFile.resolveSibling(System.currentTimeMillis()+"_temp.tmp"), ATOMIC_MOVE);
            } catch (IOException ex1) {
                Utils.logger.fatal(ex1);
            }
            Platform.runLater(() -> label_status.setText("Rename the temp file because of the wrong version."));
            Utils.logger.error("Rename the temp file.");
        }
        /*
        Queue<FFT_SnapShot> level4 = Queues.newConcurrentLinkedQueue();
        double level = 10D;//25
        //gal >= 80
        Utils.get_fft_snp_map().entrySet().stream().forEach(s -> {
            s.getValue().entrySet().stream().forEach(ss -> {
                //if (ss.getValue().getPGA_U().doubleValue() >= level || ss.getValue().getPGA_N().doubleValue() >= level || ss.getValue().getPGA_E().doubleValue() >= level) {
                if (ss.getValue().getPGA_N().doubleValue() >= level || ss.getValue().getPGA_E().doubleValue() >= level) {//NS EW
                    System.out.println(ss.getValue().getFilePath()+"\\"+ss.getValue().getFileName());
                    level4.add(ss.getValue());
                }
            });
        });
        System.out.println(level4.size());*/
    }
    
    /*
    @FXML
    public void select_variogram_from_binaryfile_ButtonAction(ActionEvent event) {//Serializable
        //show file chooser
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Json File");
        station_fileChooser.setInitialDirectory(new File("."));     
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json")
        );
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            try {
                Utils.check_logs_textarea();
                this.get_logs_textarea().appendText("Load Json File: "+dir.getCanonicalPath()+System.getProperty("line.separator"));
                this.get_logs_textarea().selectPositionCaret(this.get_logs_textarea().getLength());
                Stream<String> stream = Files.lines(Paths.get(dir.getCanonicalPath()));
                StringBuilder sb = new StringBuilder();
                stream.forEach(lin -> sb.append(lin));
                //Configs.temp_kriging.put("variogram", ));
                
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
            }
        } else {
            label_status.setText("idle");
        }
        
    }*/
    
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

        result.put(ir_temp.get_StationName(), ir_temp);
         
        return vs30_predict;
    }
}
