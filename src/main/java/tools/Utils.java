/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javax.sql.PooledConnection;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.ss.usermodel.Row;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import softpak.gdms_fft.FFT;
import softpak.gdms_fft.FFT_SnapShot;
import softpak.gdms_fft.FX_Main_Controller;
import softpak.gdms_fft.MainApp;
import softpak.gdms_fft.Seismic;
import softpak.gdms_fft.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import softpak.gdms_fft.SearchTag;
import softpak.gdms_fft.TM2coord;
import softpak.gdms_fft.WGS84coord;
import static java.lang.Math.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import static java.util.Map.Entry.comparingByValue;
import java.util.logging.Level;
import static java.util.stream.Collectors.toMap;
import javafx.stage.FileChooser;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import softpak.gdms_fft.Active_Fault;

/**
 *
 * @author you
 */
public class Utils {
    private static Map<Date, Seismic> seismic_map = Maps.newConcurrentMap();//use string date type
    private static ObservableList<Station> station_list = FXCollections.observableArrayList();//FXCollections.observableArrayList(new Station("CHY001", "1", "CHY003", "1", "1"))
    private static Map<String, Station> station_map = Maps.newConcurrentMap();
    private static Queue<FFT> fft_queue = Queues.newConcurrentLinkedQueue();
    public static Queue<Active_Fault> af_queue = Queues.newConcurrentLinkedQueue();
    
    private static Map<String, Map<String, FFT_SnapShot>> selected_snapshot_map = Maps.newConcurrentMap();//String<Station Code>, Map<SSID, FFT_SnapShot
    
    private static Map<String, Map<String, FFT_SnapShot>> fft_snapshot_map = Maps.newConcurrentMap();//String<Station Code>, Map<SSID, FFT_SnapShot>
    private static ArrayList<Map<String, FFT_SnapShot>> search_tag_list_fft_snapshot = Lists.newArrayList();
    private static Set<String> fft_station_name_set = Sets.newConcurrentHashSet();
    private static Queue<String> filename_queue = Queues.newConcurrentLinkedQueue(); //all missions
    public static Queue<Long> unit_measure_queue = Queues.newConcurrentLinkedQueue(); //all missions
    public static Queue<String> table_rm_queue = Queues.newConcurrentLinkedQueue();
    //private static Map<String, Queue<OP_Task>> op_task_map = Maps.newConcurrentMap();
    private static Queue<OP_Task> op_task_queue = Queues.newConcurrentLinkedQueue(); //all missions
    public static Queue<String> file_with_wrong_queue = Queues.newConcurrentLinkedQueue(); //all missions
    private static Queue<String> ssid_queue = Queues.newConcurrentLinkedQueue(); //all missions
    private static String main_path;                                           //missions left
    private static String project_path;                                          //project file path
    private static String project_name;
    private String DB_name = "data.db";    
    private static SQLiteConnectionPoolDataSource ds;
    public static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    
    private static PooledConnection pool = null;
    private static Connection conn;
    private static FX_Main_Controller mainController;
    
    
    public static Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
    public static ObservableList<String> sort_list = FXCollections.observableArrayList();
    public static ObservableList<String> logic_list = FXCollections.observableArrayList();
    public static ObservableList<String> y_numerator_list = FXCollections.observableArrayList();
    public static ObservableList<String> y_denominator_list = FXCollections.observableArrayList();
    public static ObservableList<String> x_numerator_list = FXCollections.observableArrayList();
    public static ObservableList<String> x_denominator_list = FXCollections.observableArrayList();
    
    public static ObservableList<SearchTag> searchtag_list = FXCollections.observableArrayList();
    public static String ISO8601_date_format = "yyyy/MM/dd HH:mm:ss.SSS";
    public static String Seismic_date_format = "yyyy/MM/dd-HH:mm:ss.SSS";
    public static String Path_date_format = "yyyy_MM_dd_hhmmss a";
                                       //2004_11_08_115455 PM
    
    public static void init() {
        DB_init(); //Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        sort_list.addAll(
                "Time(yyyy/mm/dd)",
                "StationName",
                "Magnitude(Ml)",
                "DepthOfFocus(Km)",
                "Vs30(M/s)",
                "Ground Level(M)",
                "Z10(M/s)",
                "Kappa(K)",
                "EpicenterDistance(Km)",
                "Longitude",
                "Latitude",
                "Axial U Max Hz(0~20)",
                "Axial U Max Hz(0~50)",
                "Axial U Max Hz(All)",
                "Axial N Max Hz(0~20)",
                "Axial N Max Hz(0~50)",
                "Axial N Max Hz(All)",
                "Axial E Max Hz(0~20)",
                "Axial E Max Hz(0~50)",
                "Axial E Max Hz(All)");
        logic_list.addAll(
                " ","＋","－","×","÷","±","＜","＞","＝","≦","≧","≠","≒");
        y_numerator_list.addAll(
                "PGA(Gal)",
                "Hertz(Hz)",
                "Hertz U(Hz)",
                "Hertz N(Hz)",
                "Hertz E(Hz)",
                "Magnitude(Ml)",
                "Vs30(M/s)",
                "Depth Of Focus(Km)",
                "Epicenter Distance(Km)",
                "Station",
                "Time(Date)",
                "1"
        );
        y_denominator_list.addAll(y_numerator_list);
        x_numerator_list.addAll(y_numerator_list);
        x_denominator_list.addAll(y_numerator_list);
        mainController = MainApp.loader.getController();
        //set_MainPath(System.getProperty("user.dir"));
    }
    
    
    
    public static void DB_init() {
        try {
            SQLiteConfig config = new SQLiteConfig();
            // config.setReadOnly(true);
            config.setSharedCache(true);
            config.enableRecursiveTriggers(true);

            ds = new SQLiteConnectionPoolDataSource(config);
            ds.setUrl("jdbc:sqlite:data.db");
            pool = ds.getPooledConnection();
            conn = pool.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Station ("
                    + "StationName TEXT,"
                    + "SubStationName TEXT,"
                    + "Vs30 TEXT,"
                    + "Ground_Level TEXT,"
                    + "Z10 TEXT,"
                    + "Kappa TEXT,"
                    + "Longitude TEXT,"
                    + "Latitude TEXT,"
                    + "FaultName TEXT,"
                    + "FaultDist TEXT,"
                    + "FaultCoord TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Seismic ("
                    + "StartTime TEXT,"
                    + "Data BLOB)");
            stmt.close();
            load_StationData_fromDB();
        
            load_SeimicData_fromDB();
            stmt.close();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void DB_vacuum() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("vacuum");
        stmt.close();
        Platform.runLater(() -> {
            Utils.check_logs_textarea();
            mainController.get_logs_textarea().appendText("Flush Complete."+System.getProperty("line.separator"));
            mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
        });
    }
    
    public static void DB_create_fft_by_station_table(String table) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS "+table+ " ("
                + "SSID TEXT,"
                + "Data BLOB)");
        /*
        stmt.execute("CREATE TABLE IF NOT EXISTS "+table+ " ("
                + "SSID TEXT,"
                + "StationName TEXT,"
                + "StartTime TEXT,"
                + "EpicenterDistance TEXT,"
                + "Magnitude TEXT,"
                + "DepthOfFocus TEXT,"
                + "K TEXT,"
                + "Origin_Array_U TEXT,"
                + "Origin_Array_N TEXT,"
                + "Origin_Array_E TEXT,"
                + "FFT_Array_U TEXT,"
                + "FFT_Array_N TEXT,"
                + "FFT_Array_E TEXT,"
                + "Max_Hz_U TEXT,"
                + "Max_Hz_N TEXT,"
                + "Max_Hz_E TEXT,"
                + "Other TEXT,"
                + "Longitude TEXT,"
                + "Latitude TEXT)");*/
        stmt.close();
    }
    
    //drop table
    public static void dropTable(Connection con)throws SQLException{
        String sql = "drop table test ";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
    }
    
    public static void insert_FFTData(String staion, FFT fft)throws SQLException, IOException{
        DB_create_fft_by_station_table(staion);
        byte[] data = SerializationUtils.serialize(fft);
        String sql = "insert into "+ staion + " (SSID, Data values(?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, fft.getSSID()); 
        pst.setBytes(2, data);
        pst.executeUpdate();
        
        //FFt fft_temp = SerializationUtils.deserialize(data)
    }
    
    public static void insert_StationData(Row row)throws SQLException{
        String sql = "insert into Station (StationName, SubStationName, Vs30, Ground_Level, Z10, Kappa, Longitude, Latitude) values(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        String stationname = row.getCell(0).toString().substring(0, 6);//also chars in ()
        String sub_stationname = "";
        if (row.getCell(0).toString().length() > 6) {
            int fq_pos = row.getCell(0).toString().indexOf("(");
            int lq_pos = row.getCell(0).toString().indexOf(")");
            sub_stationname = row.getCell(0).toString().substring(fq_pos+1, lq_pos);
        }
        
        
        String longitude = row.getCell(1).toString();
        String latitude = row.getCell(2).toString();
        String ground_level = row.getCell(3).toString();
        String vs30 = row.getCell(4).toString();
        String z10 = row.getCell(5).toString();
        String kappa = row.getCell(6).toString();
        
        pst.setString(1, stationname);
        pst.setString(2, sub_stationname);
        pst.setString(3, vs30);
        pst.setString(4, ground_level);
        pst.setString(5, z10);
        pst.setString(6, kappa);
        pst.setString(7, longitude);
        pst.setString(8, latitude);
        pst.executeUpdate();
        Station temp_st = new Station(stationname, sub_stationname, vs30, ground_level, z10, kappa, longitude, latitude, "", "", "");
        if (station_map.get(stationname) == null) {//not exist
            station_list.add(temp_st);
            station_map.put(stationname, temp_st);
        } else {//update
            update_StationData(row);
        }
    }
    
    public static void update_StationData(Row row)throws SQLException{
        String sql_update = "UPDATE Station SET SubStationName = ?, Vs30 = ?, Ground_Level = ?, Z10 = ?, Kappa = ? , Longitude = ?, Latitude = ? WHERE StationName =?";
        PreparedStatement pst_u = Utils.getConnection().prepareStatement(sql_update);
        
        //get row data
        String stationname = row.getCell(0).toString().substring(0, 6);//also chars in ()
        String sub_stationname = "";
        if (row.getCell(0).toString().length() > 6) {
            int fq_pos = row.getCell(0).toString().indexOf("(");
            int lq_pos = row.getCell(0).toString().indexOf(")");
            sub_stationname = row.getCell(0).toString().substring(fq_pos+1, lq_pos);
        }
        
        
        String longitude = row.getCell(1).toString();
        String latitude = row.getCell(2).toString();
        String ground_level = row.getCell(3).toString();
        String vs30 = row.getCell(4).toString();
        String z10 = row.getCell(5).toString();
        String kappa = row.getCell(6).toString();
                
        pst_u.setString(1, sub_stationname);
        pst_u.setString(2, vs30);
        pst_u.setString(3, ground_level);
        pst_u.setString(4, z10);
        pst_u.setString(5, kappa);
        pst_u.setString(6, longitude);
        pst_u.setString(7, latitude);
        pst_u.setString(8, stationname);
        
        pst_u.executeUpdate();
        //Utils.add_to_op_task_queue(new OP_Task(OP_Engine.insert_or_update_into_db, pst_u, "UPDATE Station: "+ stationname+ "."+System.getProperty("line.separator")));
        int rm_ind = 0;
        for (int c = 0; c< station_list.size(); c++) {
            if (station_list.get(c).getStationName().equals(stationname)) {
                rm_ind = c;
                break;
            }
        }
        //remove old one
        station_list.remove(rm_ind);
        station_map.remove(stationname);
        //add new one
        Station temp_st = new Station(stationname, sub_stationname, vs30, ground_level, z10, kappa, longitude, latitude, "", "", "");
        station_list.add(temp_st);
        station_map.put(stationname, temp_st);
    }
    
    public static void load_StationData_fromDB()throws SQLException{
        String sql = "SELECT * FROM Station";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            //System.out.println("."+rs.getString("StationName")+".");
            if (!rs.getString("Vs30").equals("")) {//filter empty stations
                Station temp = new Station(rs.getString("StationName"),
                                    rs.getString("SubStationName"),
                                    rs.getString("Vs30"),
                                    rs.getString("Ground_Level"),
                                    rs.getString("Z10"),
                                    rs.getString("Kappa"),
                                    rs.getString("Longitude"),
                                    rs.getString("Latitude"),
                                    rs.getString("FaultName"),
                                    rs.getString("FaultDist"),
                                    rs.getString("FaultCoord"));
                station_list.add(temp);
                station_map.put(rs.getString("StationName"), temp);
            }
        }
        pst.close();
        rs.close();
    }
    
    public static void load_SeimicData_fromDB() throws SQLException{
        String sql = "SELECT * FROM Seismic";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            //System.out.println("."+rs.getString("StationName")+".");
            Seismic sc_temp = SerializationUtils.deserialize(rs.getBytes("Data"));
            seismic_map.put(sc_temp.getStartTime(), sc_temp);
        }
        pst.close();
        rs.close();
    }
    
    public static void update(Connection con,int id,String name)throws SQLException{
        String sql = "update test set name = ? where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        int idx = 1 ; 
        pst.setString(idx++, name);
        pst.setInt(idx++, id);
        pst.executeUpdate();
    }
    
    public static void delete(Connection con,int id)throws SQLException{
        String sql = "delete from test where id = ?";
        PreparedStatement pst = null;
        pst = con.prepareStatement(sql);
        int idx = 1 ; 
        pst.setInt(idx++, id);
        pst.executeUpdate();
    }
    
    public static Connection getConnection() throws SQLException {
        return conn;
        //ds.setServerName("sample.db");
    }
    
    
    public static void listFilesForFolder(final File folder, String file_ext) throws IOException {
     for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, file_ext);
            } else {
                //System.out.println(fileEntry.getName());
                if (fileEntry.getName().contains(file_ext)) {
                    //System.out.println(fileEntry.getCanonicalPath());
                    filename_queue.add(fileEntry.getCanonicalPath());
                }
            }
        }   
    }
    
    public static void add_to_seismic_map(Date iso8601_date, Seismic sc) {
        seismic_map.put(iso8601_date, sc);
    }
    
    public static Map<Date, Seismic> get_seismic_map() {
        return seismic_map;
    }
    
    
    
    public static void add_ssid_to_ssidqueue(String SSID) {
        ssid_queue.add(SSID);
    }
    
    public static Queue get_ssidqueue() {
        return ssid_queue;
    }
    
    public static void add_path_to_filequeue(String file_path) {
        //System.out.println(file_path);
        filename_queue.add(file_path);
    }
    
    public static void clear_filequeue() {
        filename_queue.clear();
    }
    
    public static Queue<String> get_filequeue() {
        return filename_queue;
    }
    
    public static void add_to_op_task_queue(OP_Task dbt) {
        op_task_queue.add(dbt);
        /*
        if (op_task_map.size() > 1) {//2 queue
            if (op_task_map.get("T1").size() >= op_task_map.get("T2").size()) {
                op_task_map.get("T2").add(dbt);
            } else {
                op_task_map.get("T1").add(dbt);
            }
            
            
        } else {//create element
            if (op_task_map.get("T1") == null) {
                Queue<OP_Task> temp_que = Queues.newConcurrentLinkedQueue();
                temp_que.add(dbt);
                op_task_map.put("T1", temp_que);
            } else {
                Queue<OP_Task> temp_que = Queues.newConcurrentLinkedQueue();
                temp_que.add(dbt);
                op_task_map.put("T2", temp_que);
            }
        }*/
    }
    
    
    public static boolean add_to_fft_station_name_set(String st_name) {
        return fft_station_name_set.add(st_name);
    }
    
    
    public static void clear_fft_station_name_set() {
        fft_station_name_set.clear();
    }
    
    /*
    public static Map<String, Queue<OP_Task>> get_op_task_map() {
        return op_task_map;
    }*/
    
    
    public static Queue<OP_Task> get_op_task_queue() {
        return op_task_queue;
    }
    
    public static Set<String> get_fft_station_name_set() {
        return fft_station_name_set;
    }
    
    public static void removefrom_fft_station_name_set(String str) {
        fft_station_name_set.remove(str);
    }
    
    public static Map<String, Map<String, FFT_SnapShot>> get_selectedmap() {
        return selected_snapshot_map;
    }
    
    public static Map<String, Map<String, FFT_SnapShot>> get_fft_snp_map() {
        return fft_snapshot_map;
    }
    
    public static ArrayList<Map<String, FFT_SnapShot>> get_search_tag_list() {
        return search_tag_list_fft_snapshot;
    }
    
    public static void set_MainPath(String str) {
        main_path = str;
    }
    
    public static String get_MainPath() {
        return main_path;
    }
    
    public static void set_ProjectPath(String str) {
        project_path = str;
    }
    
    public static void set_ProjectName(String str) {
        project_name = str;
    }
    
    public static String get_ProjectPath() {
        return project_path;
    }
    
    public static String get_ProjectName() {
        return project_name;
    }
    
    public static ObservableList<Station> get_StationList() {
        return station_list;
    }
    
    public static Map<String, Station> get_StationMap() {
        return station_map;
    }
    
    public static void check_logs_textarea() {
        //System.out.println(mainController.get_logs_textarea().getText().split("\\n").length);
        if (mainController.get_logs_textarea().getText().length() > Configs.log_textarea_max_lines) {
            int del_pos = mainController.get_logs_textarea().getText().split("\\n").length;
            //int del_pos = 200;
            mainController.get_logs_textarea().deleteText(0, del_pos);
        }
    }
    
    public static void text_input_check(String pt, TextField tf) {
        if (!tf.getText().matches(pt)) {
            tf.setText(tf.getText().replaceAll(pt, ""));
        }
    }
    
    public static void set_global_lod(int lod) {
        Configs.global_lod = lod;
    }
    
    public static int get_global_lod() {
        return Configs.global_lod;
    }
    
    public static void set_step_distance(int dist) {
        Configs.step_distance = dist;
    }
    
    public static int get_step_distance() {
        return Configs.step_distance;
    }
    
    public static void set_level_of_detail(int lod) {
        Configs.level_of_detail = lod;
    }
    
    public static int get_level_of_detail() {
        return Configs.level_of_detail;
    }
    
    public static void set_station_distance_to_epiccenter_vector(int dist) {
        Configs.station_distance_to_epiccenter_vector = dist;
    }
    
    public static int get_station_distance_to_epiccenter_vector() {
        return Configs.station_distance_to_epiccenter_vector;
    }
    
     public static void set_smoothing_mode(String mode, int times) {
        if (mode.contains("Boxcar")) {
            Configs.smoothing_mode = 0;
        }
        if (mode.contains("Hanning")) {
            Configs.smoothing_mode = 1;
        }
        if (mode.contains("Gaussian")) {
            Configs.smoothing_mode = 2;
        }
        Configs.smoothing_times = times;
    }
    
    public static String[] getNearestFault(double lat, double lon) {
        Map<String, Double> nearest_af = Maps.newConcurrentMap();
        Map<String[], Double> nearest_af_coord = Maps.newConcurrentMap();
        Utils.af_queue.stream().forEach(af -> {
            List<Double> af_dist = Lists.newArrayList();
            af.getPointList().stream().forEach(pp -> {
                GeodeticCalculator geoCalc = new GeodeticCalculator();
                GlobalCoordinates af_p = new GlobalCoordinates(pp.lat_WGS, pp.lon_WGS);
                GlobalCoordinates sc_p = new GlobalCoordinates(lat, lon);

                GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, sc_p, af_p);
                double dist = geoCurve.getEllipsoidalDistance()/1000D;
                af_dist.add(dist);
                String[] coord = {String.valueOf(pp.lat_WGS), String.valueOf(pp.lon_WGS)};
                nearest_af_coord.put(coord, dist);
                
            });
            Collections.sort(af_dist);

            nearest_af.put(af.getFaultName(), af_dist.get(0));
        });
        Map<String, Double> sorted_dist_map = nearest_af.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        Map<String[], Double> sorted_coord_map = nearest_af_coord.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        Map.Entry<String, Double> entry = sorted_dist_map.entrySet().iterator().next();
        Map.Entry<String[], Double> coord_entry = sorted_coord_map.entrySet().iterator().next();
        //sc_temp.setNearestActiveFault_dist(entry.getValue().toString());
        //sc_temp.setNearestActiveFault(entry.getKey());
        String[] ce = coord_entry.getKey();
        String f_lat = ce[0];
        String f_lon = ce[1];
        String f_name = entry.getKey();
        String f_dist = entry.getValue().toString();
        String[] result = {f_lat, f_lon, f_name, f_dist};
        
        return result;
    }
    
    /**
    * 
    * @param vertexPointX - vector center x
    * @param vertexPointY - vector center y
    * @param point0X
    * @param point0Y
    * @param point1X
    * @param point1Y
    * @return
    */
    public static double getDegrees_from_inner_product(double vertexPointX, double vertexPointY, double point0X, double point0Y, double point1X, double point1Y) {
        //dot product
        double vector = (point0X - vertexPointX) * (point1X - vertexPointX) + (point0Y - vertexPointY) * (point1Y - vertexPointY);
        //cross product
        double sqrt = Math.sqrt(
                    (Math.abs((point0X - vertexPointX) * (point0X - vertexPointX)) + Math.abs((point0Y - vertexPointY) * (point0Y - vertexPointY)))
                  * (Math.abs((point1X - vertexPointX) * (point1X - vertexPointX)) + Math.abs((point1Y - vertexPointY) * (point1Y - vertexPointY)))
        );
        //radian
        double radian = Math.acos(vector / sqrt);
        //degree
        return (180D * radian / Math.PI);
    }
    
    private static double a = 6378137.0D;
    private static double b = 6356752.314245D;
    private static double lon0 = 121D * Math.PI / 180D;
    private static double k0 = 0.9999D;
    private static double dx = 250000D;
    private static double dy = 0D;
    private static double e = 1D - Math.pow(b, 2D) / Math.pow(a, 2D);
    private static double e2 = (1D - Math.pow(b, 2D) / Math.pow(a, 2D)) / (Math.pow(b, 2D) / Math.pow(a, 2D));

    public static TM2coord Cal_lonlat_To_twd97(double lon ,double lat) { 

        lon = (lon/180D) * Math.PI;
        lat = (lat/180D) * Math.PI;
        
        //---------------------------------------------------------
        double e = Math.pow((1D - Math.pow(b,2D) / Math.pow(a,2D)), 0.5D);
        double e2 = Math.pow(e,2D)/(1-Math.pow(e,2D)); 
        double n = ( a - b ) / ( a + b );
        double nu = a / Math.pow((1-(Math.pow(e,2D)) * (Math.pow(Math.sin(lat), 2D) ) ) , 0.5D);
        double p = lon - lon0;
        double A = a * (1 - n + (5D/4D) * (Math.pow(n,2D) - Math.pow(n,3D)) + (81D/64D) * (Math.pow(n, 4D)  - Math.pow(n, 5D)));
        double B = (3D * a * n/2.0) * (1 - n + (7D/8.0)*(Math.pow(n,2) - Math.pow(n,3)) + (55D/64.0)*(Math.pow(n,4) - Math.pow(n,5)));
        double C = (15D * a * (Math.pow(n,2D))/16.0)*(1 - n + (3D/4.0)*(Math.pow(n,2) - Math.pow(n,3)));
        double D = (35D * a * (Math.pow(n,3D))/48.0)*(1 - n + (11D/16.0)*(Math.pow(n,2) - Math.pow(n,3)));
        double E = (315D * a * (Math.pow(n,4D))/51.0)*(1 - n);

        double S = A * lat - B * Math.sin(2D * lat) +C * Math.sin(4D * lat) - D * Math.sin(6D * lat) + E * Math.sin(8D * lat);
 
        //計算Y值
        double K1 = S*k0;
        double K2 = k0*nu*Math.sin(2*lat)/4.0;
        double K3 = (k0*nu*Math.sin(lat)*(Math.pow(Math.cos(lat),3D))/24.0) * (5D - Math.pow(Math.tan(lat),2) + 9D*e2*Math.pow((Math.cos(lat)),2D) + 4D*(Math.pow(e2,2D))*(Math.pow(Math.cos(lat),4D)));
        double y = K1 + K2*(Math.pow(p,2D)) + K3*(Math.pow(p,4D));
 
        //計算X值
        double K4 = k0*nu*Math.cos(lat);
        double K5 = (k0*nu*(Math.pow(Math.cos(lat),3D))/6.0) * (1 - Math.pow(Math.tan(lat),2D) + e2*(Math.pow(Math.cos(lat),2D)));
        double x = K4 * p + K5 * (Math.pow(p, 3D)) + dx;
        TM2coord tm2c = new TM2coord(x,y);
        return tm2c;
    }
    
    public static WGS84coord Cal_TWD97_To_lonlat(double x, double y) {
        x -= dx;
        y -= dy;

        // Calculate the Meridional Arc
        double M = y / k0;

        // Calculate Footprint Latitude
        double mu = M / (a * (1.0 - e / 4.0 - 3D * Math.pow(e, 2D) / 64.0D - 5D * Math.pow(e, 3D) / 256.0D));
        double e1 = (1.0D - Math.sqrt(1.0D - e)) / (1.0D + Math.sqrt(1.0D - e));

        double J1 = (3D * e1 / 2D - 27D * Math.pow(e1, 3D) / 32.0D);
        double J2 = (21D * Math.pow(e1, 2D) / 16D - 55D * Math.pow(e1, 4D) / 32.0D);
        double J3 = (151D * Math.pow(e1, 3D) / 96.0D);
        double J4 = (1097D * Math.pow(e1, 4D) / 512.0D);

        double fp = mu + J1 * Math.sin(2D * mu) + J2 * Math.sin(4D * mu) + J3 * Math.sin(6D * mu) + J4 * Math.sin(8D * mu);

        // Calculate Latitude and Longitude
        double C1 = e2 * Math.pow(Math.cos(fp), 2D);
        double T1 = Math.pow(Math.tan(fp), 2D);
        double R1 = a * (1D - e) / Math.pow((1 - e * Math.pow(Math.sin(fp), 2D)), (3.0D / 2.0D));
        double N1 = a / Math.pow((1 - e * Math.pow(Math.sin(fp), 2D)), 0.5D);

        double D = x / (N1 * k0);

        // 計算緯度
        double Q1 = N1 * Math.tan(fp) / R1;
        double Q2 = (Math.pow(D, 2D) / 2.0D);
        double Q3 = (5D + 3D * T1 + 10D * C1 - 4D * Math.pow(C1, 2D) - 9D * e2) * Math.pow(D, 4D) / 24.0D;
        double Q4 = (61D + 90D * T1 + 298D * C1 + 45D * Math.pow(T1, 2D) - 3D * Math.pow(C1, 2D) - 252D * e2) * Math.pow(D, 6D) / 720.0D;
        double lat = fp - Q1 * (Q2 - Q3 + Q4);

        // 計算經度
        double Q5 = D;
        double Q6 = (1D + 2D * T1 + C1) * Math.pow(D, 3) / 6D;
        double Q7 = (5D - 2D * C1 + 28 * T1 - 3D * Math.pow(C1, 2D) + 8D * e2 + 24D * Math.pow(T1, 2D)) * Math.pow(D, 5D) / 120.0D;
        double lon = lon0 + (Q5 - Q6 + Q7) / Math.cos(fp);

        lat = (lat * 180) / Math.PI; //緯度
        lon = (lon * 180) / Math.PI; //經度

        WGS84coord wgs84c = new WGS84coord(lon,lat);
        return wgs84c;
    }
    
    /** Calculates the root mean squared error. */
    public static double getRMSE_1d(double[] truth, double[] prediction) {
        if (truth.length != prediction.length) {
            throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", truth.length, prediction.length));
        }

        int n = truth.length;
        double rss = 0.0;
        for (int i = 0; i < n; i++) {
            rss += Math.pow(truth[i] - prediction[i], 2);
        }

        return Math.sqrt(rss/n);
    }
    
    //lat lon
    public static double[] getRMSE_2d(double[] truth_x, double[] truth_y, double[] prediction_x, double[] prediction_y) {
        if (truth_x.length != prediction_x.length) {
            throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", truth_x.length, prediction_x.length));
        }
        if (truth_y.length != prediction_y.length) {
            throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", truth_y.length, prediction_y.length));
        }

        double mrse[] = new double[2];
        mrse[0] = getRMSE_1d(truth_x, prediction_x);
        mrse[1] = getRMSE_1d(truth_y, prediction_y);
        return mrse;
    }
    
    public static double vincentyDistance(com.grum.geocalc.Point standPoint, com.grum.geocalc.Point forePoint) {
        return vincenty(standPoint, forePoint).distance;
    }
    
    private static Vincenty vincenty(com.grum.geocalc.Point standPoint, com.grum.geocalc.Point forePoint) {
        double λ1 = toRadians(standPoint.longitude);
        double λ2 = toRadians(forePoint.longitude);

        double φ1 = toRadians(standPoint.latitude);
        double φ2 = toRadians(forePoint.latitude);

        double a = 6_378_137D;
        double f = 1D / 298.257223563D;
        double b = (1D-f)*a;

        double L = λ2 - λ1;
        double tanU1 = (1D - f) * tan(φ1), cosU1 = 1D / sqrt((1D + tanU1 * tanU1)), sinU1 = tanU1 * cosU1;
        double tanU2 = (1D - f) * tan(φ2), cosU2 = 1D / sqrt((1D + tanU2 * tanU2)), sinU2 = tanU2 * cosU2;

        double λ = L, λʹ, iterationLimit = 100, cosSqα, σ, cos2σM, cosσ, sinσ, sinλ, cosλ;
        do {
            sinλ = sin(λ);
            cosλ = cos(λ);
            double sinSqσ = (cosU2 * sinλ) * (cosU2 * sinλ) + (cosU1 * sinU2 - sinU1 * cosU2 * cosλ) * (cosU1 * sinU2 - sinU1 * cosU2 * cosλ);
            sinσ = sqrt(sinSqσ);
            if (sinσ == 0) return new Vincenty(0, 0, 0);  // co-incident points
            cosσ = sinU1 * sinU2 + cosU1 * cosU2 * cosλ;
            σ = atan2(sinσ, cosσ);
            double sinα = cosU1 * cosU2 * sinλ / sinσ;
            cosSqα = 1D - sinα * sinα;
            cos2σM = cosσ - 2D * sinU1 * sinU2 / cosSqα;

            if (Double.isNaN(cos2σM)) cos2σM = 0;  // equatorial line: cosSqα=0 (§6)
            double C = f / 16D * cosSqα * (4D + f * (4D - 3D * cosSqα));
            λʹ = λ;
            λ = L + (1D - C) * f * sinα * (σ + C * sinσ * (cos2σM + C * cosσ * (-1D + 2D * cos2σM * cos2σM)));
        } while (abs(λ - λʹ) > 1e-12 && --iterationLimit > 0);

        if (iterationLimit == 0) throw new IllegalStateException("Formula failed to converge");

        double uSq = cosSqα * (a * a - b * b) / (b * b);
        double A = 1D + uSq / 16384D * (4096D + uSq * (-768D + uSq * (320D - 175D * uSq)));
        double B = uSq / 1024D * (256D + uSq * (-128D + uSq * (74D - 47D * uSq)));
        double Δσ = B * sinσ * (cos2σM + B / 4D * (cosσ * (-1D + 2D * cos2σM * cos2σM) -
                B / 6D * cos2σM * (-3D + 4D * sinσ * sinσ) * (-3D + 4D * cos2σM * cos2σM)));

        double distance = b * A * (σ - Δσ);

        double initialBearing = atan2(cosU2 * sinλ, cosU1 * sinU2 - sinU1 * cosU2 * cosλ);
        initialBearing = (initialBearing + 2D * PI) % (2D * PI); //turning value to trigonometric direction

        double finalBearing = atan2(cosU1 * sinλ, -sinU1 * cosU2 + cosU1 * sinU2 * cosλ);
        finalBearing = (finalBearing + 2D * PI) % (2D * PI);  //turning value to trigonometric direction

        return new Vincenty(distance, toDegrees(initialBearing), toDegrees(finalBearing));
    }
    
    private static class Vincenty {
        /**
         * distance is the distance in meter
         * initialBearing is the initial bearing, or forward azimuth (in reference to North point), in degrees
         * finalBearing is the final bearing (in direction p1→p2), in degrees
         */
        final double distance, initialBearing, finalBearing;

        Vincenty(double distance, double initialBearing, double finalBearing) {
            this.distance = distance;
            this.initialBearing = initialBearing;
            this.finalBearing = finalBearing;
        }
    }
    
    public static double getR2(double[] x, double[] y) {
        int n = 0;
        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        while(n < x.length) {
            sumx  += x[n];
            sumx2 += x[n] * x[n];
            sumy  += y[n];
            n++;
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        //StdOut.println("y   = " + beta1 + " * x + " + beta0);

        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        double R2    = ssr / yybar;
        /*
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/n + xbar*xbar*svar1;
        System.out.println("R^2                 = " + R2);
        svar0 = svar * sumx2 / (n * xxbar);*/
        return R2;
    }
    
    public static File excel_filechooser(String title) {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle(title);
        station_fileChooser.setInitialDirectory(new File("."));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 97-2003", "*.xls"));
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        
        return dir;
    }
}
