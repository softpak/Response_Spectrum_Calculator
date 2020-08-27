/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.grum.geocalc.Coordinate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import softpak.gdms_fft.FFT_SnapShot;
import softpak.gdms_fft.Seismic;
import softpak.gdms_fft.Station;

/**
 *
 * @author you
 */
public class Relations {
    SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
    Kriging kr;
    Set<String> iss_station = Sets.newConcurrentHashSet();
    Map<Date, Seismic> seismic_map;
    Map<String, Map<String, FFT_SnapShot>> snp_selected_map;
    
    public Relations() {
        
    }
    
    public void set_Seismic_Map(Map<Date, Seismic> seismic_map) {
        this.seismic_map = seismic_map;
    }
    
    public void set_Snp_Selected_Map(Map<String, Map<String, FFT_SnapShot>> snp_selected_map) {
        this.snp_selected_map = snp_selected_map;
    }
    
    public void prep_kriging(List<Station> st_list) {
        Collections.sort(st_list, new Comparator<Station>() {
            @Override
            public int compare(Station st1, Station st2) {
                if (Double.valueOf(st1.getVs30()) > Double.valueOf(st2.getVs30()))
                    return 1;
                if (Double.valueOf(st1.getVs30()) < Double.valueOf(st2.getVs30()))
                    return -1;//min to foward
                return 0;
            }
        });
        Configs.vs30_min = Double.valueOf(st_list.get(0).getVs30());
        Configs.vs30_max = Double.valueOf(st_list.get(st_list.size()-1).getVs30());
        System.out.println("Min Vs30: "+st_list.get(0).getVs30());
        System.out.println("Max Vs30: "+st_list.get(st_list.size()-1).getVs30());
        
        //1. do kriging
        kr = new Kriging();
        //variogram model
        String model = "spherical";//spherical,exponential
        System.out.println(model);
        //gen data arrays
        //long st =System.currentTimeMillis();
        int real_st_num = st_list.size();//348 ok, 347 the problem //TAP003, TAP004
        kr.set_station_num(real_st_num);
        double[] value = new double[real_st_num];
        double[] x_lon = new double[real_st_num];
        double[] y_lat = new double[real_st_num];

        for (int c = 0 ; c < real_st_num ; c++) {
            value[c] = Double.valueOf(st_list.get(c).getVs30());
            x_lon[c] = Double.valueOf(st_list.get(c).getLongitude());
            y_lat[c] = Double.valueOf(st_list.get(c).getLatitude());
        }
        kr.set_data(value, x_lon, y_lat, model);
        kr.do_train();
    }
    
    
    
    
    int vs30;
    public void cal_relations() {
        iss_station.clear();
        
        //1.1 kr all events
        Map<Date, Double> sc_vs30 = Maps.newConcurrentMap();
        List<Double> sc_vs30_list = Lists.newArrayList();
        this.seismic_map.entrySet().parallelStream().forEach(sc -> {
            Seismic sc_temp = sc.getValue();
            double sc_lat = sc_temp.getLatitude().doubleValue();
            double sc_lon = sc_temp.getLongitude().doubleValue();
            //GlobalCoordinates sc_p = new GlobalCoordinates(sc_lat, sc_lon);//seismic
            double ep_center_vs30 = kr.predict(sc_lon, sc_lat);
            sc_vs30.put(sc_temp.getStartTime(), ep_center_vs30);
            sc_vs30_list.add(ep_center_vs30);
        });
        Collections.sort(sc_vs30_list, new Comparator<Double>() {
            @Override
            public int compare(Double sc1, Double sc2) {
                if (sc1 > sc2)
                    return 1;
                if (sc1 < sc2)
                    return -1;//min to foward
                return 0;
            }
        });
        
        //1.2 create vs30 map group
        /*
        Station Vs30 Max: 1538.03
        Station Vs30 Min: 121.45
        
        Seismic Vs30 Max: 1451.4271824085154
        Seismic Vs30 Min: 131.23962231958285
        */
        double vs30_min = sc_vs30_list.get(0);
        double vs30_max = sc_vs30_list.get(sc_vs30_list.size()-1);
        System.out.println("Seismic Vs30 Min: "+vs30_min);
        System.out.println("Seismic Vs30 Max: "+vs30_max);
        int vs30_step = 100;//meter
        int vs30_start = ((int)vs30_min/vs30_step)*vs30_step;
        int vs30_end = ((int)vs30_max/vs30_step+1)*vs30_step;
        System.out.println("Seismic Vs30 start: "+vs30_start);
        System.out.println("Seismic Vs30 end: "+vs30_end);
        Map<Integer, Map<Date, Double>> vs30_group_map = Maps.newLinkedHashMap();
        
        for (vs30 = vs30_start; vs30 < vs30_end; vs30+=vs30_step) {
            sc_vs30.entrySet().forEach(sc -> {
                double pd_vs30 = sc.getValue();
                if (pd_vs30 >= vs30 && pd_vs30 < vs30+vs30_step) {
                    if (vs30_group_map.get(vs30) == null) {
                        Map<Date, Double> vs30_temp = Maps.newConcurrentMap();
                        vs30_temp.put(sc.getKey(), pd_vs30);
                        vs30_group_map.put(vs30, vs30_temp);
                    } else {
                        vs30_group_map.get(vs30).put(sc.getKey(), pd_vs30);
                    }
                }
            });
        }
        
        
        vs30_group_map.entrySet().forEach(vs30 -> {
            System.out.println(vs30.getKey()+" <= Vs30 < "+(vs30.getKey()+vs30_step)+": "+vs30.getValue().size());
        });
        //search same vs30 stations
        //copy a snp_map
        
        Map<Integer, Map<String, FFT_SnapShot>> snp_group_by_vs30_temp = Maps.newConcurrentMap();
       
        this.snp_selected_map.entrySet().parallelStream().forEach(stn -> {
            stn.getValue().entrySet().forEach(snp -> {
                try {
                    FFT_SnapShot fft_temp = snp.getValue();
                    //find seismic match the fft_snp
                    //double st_vs30_temp = fft_temp.getVs30().doubleValue();
                    //get ep center vs30
                    double sc_lat = fft_temp.getSeismicLatitude().doubleValue();
                    double sc_lon = fft_temp.getSeismicLongitude().doubleValue();
                    double ep_center_vs30 = kr.predict(sc_lon, sc_lat);
                    int vs30_start_temp = ((int)ep_center_vs30/vs30_step)*vs30_step;
                    if (snp_group_by_vs30_temp.get(vs30_start_temp) == null) {
                        Map<String, FFT_SnapShot> vs30g_temp_map = Maps.newConcurrentMap();
                        vs30g_temp_map.put(fft_temp.getSSID(), fft_temp);
                        snp_group_by_vs30_temp.put(vs30_start_temp, vs30g_temp_map);
                    } else {
                        snp_group_by_vs30_temp.get(vs30_start_temp).put(fft_temp.getSSID(), fft_temp);
                    }
                } catch (Exception ex) {
                    Utils.logger.fatal(ex+": no match seismic. "+ format.format(snp.getValue().getStartTime())+" SSID: "+snp.getValue().getSSID());
                }
            });
        });
        Map<Integer, Map<String, FFT_SnapShot>> snp_group_by_vs30 = Maps.newLinkedHashMap();
        snp_group_by_vs30_temp.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> snp_group_by_vs30.put(x.getKey(), x.getValue()));


        
        
        snp_group_by_vs30.entrySet().parallelStream().forEach(vs30_group -> {
            XSSFWorkbook vs30_group_workbook = new XSSFWorkbook();
            int vs30_g = vs30_group.getKey();
            String sheet_name = (int)vs30_g+" <= Vs30 < "+  (int)(vs30_g+vs30_step);
            String file_name = (int)vs30_g+"_Vs30_"+  (int)(vs30_g+vs30_step);
            System.out.println("========== Vs30 Group: "+ sheet_name +" ==========");
            int rowCount = 0;
            XSSFSheet sheet = vs30_group_workbook.createSheet(sheet_name);
            //add head
            Row row_head = sheet.createRow(rowCount);
            String[] cell_head = {"Date", "Seismic Vs30", "Station Vs30", "ML", "EpicenterDistance", "Depth", "Max U Hz 20", "Max N Hz 20", "Max E Hz 20", "Max Vs30", "Min Vs30"};
            for (int rc = 0; rc < cell_head.length; rc++) {
                Cell cell = row_head.createCell(rc);
                cell.setCellValue(cell_head[rc]);
            }
            
            for (Map.Entry<String, FFT_SnapShot> snp_temp : vs30_group.getValue().entrySet()) {
                //vs30_group.getValue().entrySet().stream().forEach(snp_temp -> {
                String ex_st_name_temp = "";
                Date ex_date_temp = null;
                try { 

                    FFT_SnapShot snp = snp_temp.getValue();
                    Date date_temp = snp.getStartTime();
                    ex_date_temp = date_temp;
                    
                    Map<Date, Double> sc_vs30_g_map = vs30_group_map.get(vs30_g);
                    double sc_vs30_temp = sc_vs30_g_map.get(date_temp);
                    ex_st_name_temp = snp.getStationCode();
                    
                    /*
                        1.set step points as 100 points and calc these points and get vs30
                        2.write a excel file with all data
                    */
                    //calc passby step points max and min predict vs30
                    GeodeticCalculator geoCalc = new GeodeticCalculator();
                    double max_vs30 = -1;
                    double min_vs30 = -1;

                    max_vs30 = Math.max(sc_vs30_temp, snp.getVs30().doubleValue());
                    min_vs30 = Math.min(sc_vs30_temp, snp.getVs30().doubleValue());
                    GlobalCoordinates st_p = new GlobalCoordinates(snp.getSeismicLatitude().doubleValue(), snp.getSeismicLongitude().doubleValue());

                    //calc avg vs30
                    List<Double> vs30_step_list = Lists.newArrayList();
                    
                    while (true) {
                        GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, new GlobalCoordinates(snp.getStationLatitude().doubleValue(), snp.getStationLongitude().doubleValue()));
                        
                        
                        
                        
                        double temp_distance = geoCurve.getEllipsoidalDistance();
                        double azimuth_temp = geoCurve.getAzimuth();
                        if (temp_distance < Utils.get_step_distance()) {//add last
                            max_vs30 = Math.max(max_vs30, snp.getVs30().doubleValue());
                            min_vs30 = Math.min(min_vs30, snp.getVs30().doubleValue());
                            vs30_step_list.add(snp.getVs30().doubleValue());
                            break;
                        } else {//dist is bigger then a step
                            GlobalCoordinates dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, st_p, azimuth_temp, Utils.get_step_distance(), new double[1]);
                            double _vs30 = kr.predict(dest.getLongitude(), dest.getLatitude());
                            max_vs30 = Math.max(max_vs30, _vs30);
                            min_vs30 = Math.min(min_vs30, _vs30);
                            vs30_step_list.add(_vs30);
                            st_p = dest;
                        }
                    }
                    /*
                    System.out.println(format.format(date_temp)+": Seismic Vs30: "+ Configs.df.format(sc_vs30_temp) +": Station Vs30: "+Configs.df.format(snp.getVs30().doubleValue())+ 
                                    " Distance: "+Configs.df.format(snp.getEpicenterDistance().doubleValue())+ " Freq U N E: "+ Configs.df.format(snp.getMax_U_Hz_20())+
                                    " "+Configs.df.format(snp.getMax_N_Hz_20())+" "+Configs.df.format(snp.getMax_E_Hz_20()));*/

                    Row row = sheet.createRow(++rowCount);
                    String[] cell_string = {format.format(date_temp), Configs.df.format(sc_vs30_temp), Configs.df.format(snp.getVs30().doubleValue()),Configs.df.format(snp.getMagnitude()),
                                            Configs.df.format(snp.getEpicenterDistance().doubleValue()), Configs.df.format(snp.getDepthOfFocus().doubleValue()), Configs.df.format(snp.getMax_U_Hz_20()),
                                            Configs.df.format(snp.getMax_N_Hz_20()), Configs.df.format(snp.getMax_E_Hz_20()), Configs.df.format(max_vs30), Configs.df.format(min_vs30)};
                    for (int rc = 0; rc < cell_string.length; rc++) {
                        Cell cell = row.createCell(rc);
                        if (rc > 0) {//numbers
                            cell_string[rc] = cell_string[rc].replace(",", "");
                            cell.setCellValue(Double.valueOf(cell_string[rc]));
                        } else {//date
                            cell.setCellValue(cell_string[rc]);
                        }
                    }
                    try (FileOutputStream outputStream = new FileOutputStream(file_name+".xlsx")) {
                        vs30_group_workbook.write(outputStream);
                    } catch (FileNotFoundException ex) {
                        Utils.logger.fatal(ex);
                        //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Utils.logger.fatal(ex);
                        //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //rowCount++;
                } catch (Exception ex) {
                    if (rowCount > 1) {
                        --rowCount;
                    }
                    //Utils.logger.fatal(ex+": Error while calculating Geodetic Curve. "+ format.format(snp_temp.getValue().getStartTime())+" SSID: "+snp_temp.getValue().getSSID());
                    //Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                    Utils.logger.fatal("Something wrong with station(No further info): "+ex_st_name_temp+". "+format.format(ex_date_temp));
                    iss_station.add(ex_st_name_temp);
                }
            }
        });
        Utils.logger.fatal("Relation Sheets Done.");  
        iss_station.stream().forEach(System.out::println);
        
        
        
        
        //2. integral speed and get distance
        /*
        Utils.get_fft_snp_map().entrySet().stream().forEach(stn -> {
            try {
                stn.getValue().entrySet().parallelStream().forEach(snp -> {
                    GeodeticCalculator geoCalc = new GeodeticCalculator();
                    FFT_SnapShot fft_temp = snp.getValue();
                    GlobalCoordinates st_p = new GlobalCoordinates(fft_temp.getSeismicLatitude().doubleValue(), fft_temp.getSeismicLongitude().doubleValue());//strat from seismic
                    GlobalCoordinates end_p = new GlobalCoordinates(fft_temp.getStationLatitude().doubleValue(), fft_temp.getStationLongitude().doubleValue());//end to station

                    GeodeticCurve first_geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, end_p);
                    double total_dist = first_geoCurve.getEllipsoidalDistance();//meter
                    double ep_center_vs30 = kr.predict(st_p.getLongitude(), st_p.getLatitude());
                    double avg_line_slope = (ep_center_vs30-fft_temp.getVs30().doubleValue())/total_dist;
                    //System.out.println("Slope: "+avg_line_slope);
                    //calc steps and vs30
                    List<GlobalCoordinates> step_points = Lists.newArrayList();
                    List<Double> Vs30_viscocity = Lists.newArrayList();
                    List<Double> density_list = Lists.newArrayList();
                    step_points.add(st_p);
                    Vs30_viscocity.add(fft_temp.getVs30().doubleValue()*100D);
                    density_list.add(Math.sqrt(fft_temp.getVs30().doubleValue()-Configs.vs30_min));
                    
                    //2 end point is zero
                    //int step_count = 1;
                    while (true) {
                        GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, end_p);
                        double temp_distance = geoCurve.getEllipsoidalDistance();
                        double azimuth_temp = geoCurve.getAzimuth();
                        if (temp_distance < Utils.get_step_distance()) {//add last
                            step_points.add(end_p);
                            Vs30_viscocity.add(ep_center_vs30*100D);
                            density_list.add(Math.sqrt(ep_center_vs30-Configs.vs30_min));
                            //2 end point is zero
                            break;
                        } else {//dist is bigger then a step
                            GlobalCoordinates this_point = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, st_p, azimuth_temp, Utils.get_step_distance(), new double[1]);
                            step_points.add(this_point);
                            //predict vs30
                            double vs30_pre_temp = kr.predict(this_point.getLongitude(), this_point.getLatitude());
                            //calc slope
                            //GeodeticCurve geoCurve_temp = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, this_point);
                            //double avg_vs30 = geoCurve_temp.getEllipsoidalDistance()*avg_line_slope+ep_center_vs30;
                            //Vs30_integral.add((vs30_pre_temp-avg_vs30));
                            density_list.add(Math.sqrt(vs30_pre_temp-Configs.vs30_min));
                            Vs30_viscocity.add(vs30_pre_temp*100D);//viscocity => k
                            st_p = this_point;
                        }
                        //System.out.println("step: "+step_count+" dist: "+temp_distance);
                        //step_count++;
                    }

                    double avg_viscocity = Vs30_viscocity.stream().mapToDouble(d -> d).average().getAsDouble();//k
                    double avg_density = density_list.stream().mapToDouble(d -> d).average().getAsDouble();//density
                    double mass = total_dist*100D*30D*avg_density;//m
                    System.out.println(fft_temp.getStationCode()+"; "+fft_temp.getStartTime()+"; ML: "+ fft_temp.getMagnitude().toString()+"; "+
                            "U: "+fft_temp.getMax_U_Hz_20()+" N: "+fft_temp.getMax_N_Hz_20()+" E: "+fft_temp.getMax_E_Hz_20()+
                            " AVG_Viscocity: "+avg_viscocity+" Freq Constant: "+Math.sqrt(avg_viscocity/mass)+" Slope: "+avg_line_slope+
                            " EP Depth: "+fft_temp.getDepthOfFocus().doubleValue()+" Dist(KM): "+fft_temp.getEpicenterDistance()
                    );
                    
                    

                });
            } catch (Exception ex) {
                //System.out.println("Data Error: "+snp.getValue().getStationCode()+"; "+snp.getValue().getStartTime()+"; ML: "+ snp.getValue().getMagnitude().toString()+".");                            
                System.out.println("Data Error: "+stn.getKey());
            }
        });
        */
        
        
        //3. 
        
    }
    
    
    public void relations_with_nearby_seismic() {
        Configs.Work_Pool.execute(() -> {
            Utils.get_fft_snp_map().entrySet().parallelStream().forEach(stn -> {
                String station_name = stn.getKey();
                Map<String, FFT_SnapShot> stn_group = stn.getValue();
                stn_group.entrySet().stream().forEach(snp -> {
                    Map<String, FFT_SnapShot> temp_selected_snapshot_map = Maps.newConcurrentMap();
                    try {
                        GeodeticCalculator geoCalc = new GeodeticCalculator();
                        FFT_SnapShot fft_temp = snp.getValue();
                        GlobalCoordinates o_sc_p = new GlobalCoordinates(fft_temp.getSeismicLatitude().doubleValue(), fft_temp.getSeismicLongitude().doubleValue());
                        //Coordinate ref_lon_temp = Coordinate.fromDegrees(sc_ref.getLongitude().doubleValue());
                        //Coordinate ref_lat_temp = Coordinate.fromDegrees(sc_ref.getLatitude().doubleValue());
                        //com.grum.geocalc.Point o_sc_p = com.grum.geocalc.Point.at(ref_lat_temp, ref_lon_temp);//seismic
                        //1. put self first
                        temp_selected_snapshot_map.put(fft_temp.getSSID(), fft_temp);

                        stn_group.entrySet().stream().forEach(sc -> {
                            //Coordinate sc_lon_temp = Coordinate.fromDegrees(sc.getValue().getLongitude().doubleValue());
                            //Coordinate sc_lat_temp = Coordinate.fromDegrees(sc.getValue().getLatitude().doubleValue());
                            try {
                                GlobalCoordinates n_sc_p = new GlobalCoordinates(sc.getValue().getSeismicLatitude().doubleValue(), sc.getValue().getSeismicLongitude().doubleValue());
                                //com.grum.geocalc.Point n_sc_p = com.grum.geocalc.Point.at(sc_lat_temp, sc_lon_temp);//station
                                GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, n_sc_p, o_sc_p);
                                //need to check same epc events
                                //gps cordinates to 4 decimal places around taiwan is 10 meters like 120.3000 25.3001 to 120.3000 25.3000
                                if (geoCurve.getEllipsoidalDistance() <= Configs.nearby_staiotn_distance && geoCurve.getEllipsoidalDistance() > 1) {//not the same point
                                    //find the station
                                    if (!fft_temp.getStartTime().equals(sc.getValue().getStartTime())) {
                                        temp_selected_snapshot_map.put(sc.getValue().getSSID(), sc.getValue());
                                    }
                                }
                            } catch (Exception ex) {
                                //Utils.logger.fatal(ex+": Error while calculating Geodetic Curve. "+ format.format(sc.getValue().getStartTime())+" SSID: "+sc.getValue().getSSID());
                                Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        });

                    } catch (Exception ex) {
                        //Utils.logger.fatal(ex+": Error while calculating Geodetic Curve. "+ format.format(snp.getValue().getStartTime())+" SSID: "+snp.getValue().getSSID());
                        Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Utils.logger.fatal(temp_selected_snapshot_map.size());
                    if (temp_selected_snapshot_map.size() >= Configs.num_of_samples) {
                        export_excel(station_name, temp_selected_snapshot_map);
                    }
                });

            });
            Utils.logger.fatal("Done.");
        });
    }
    
    private void export_excel(String station_name, Map<String, FFT_SnapShot> temp_selected_snapshot_map) {
        //export excel
        
        //create excel files with station name
        XSSFWorkbook snp_group_workbook = new XSSFWorkbook();
        String sheet_name = station_name+"_"+System.currentTimeMillis();
        String file_name = sheet_name;
        XSSFSheet sheet = snp_group_workbook.createSheet(sheet_name);
        System.out.println("========== Snp Group: "+ sheet_name +" ==========");
        int rowCount = 0;
        Row row_head = sheet.createRow(rowCount);
        String[] cell_head = {"Date", "Seismic Vs30", "Station Vs30", "ML", "EpicenterDistance", "Depth", "Max U Hz 20", "Max N Hz 20", "Max E Hz 20", "Max Vs30", "Min Vs30", "Higher then EPC(%)", "Lower then EPC(%)"};
        for (int rc = 0; rc < cell_head.length; rc++) {
            Cell cell = row_head.createCell(rc);
            cell.setCellValue(cell_head[rc]);
        }
        for (Map.Entry<String, FFT_SnapShot> snp_ : temp_selected_snapshot_map.entrySet()) {
            GeodeticCalculator geoCalc = new GeodeticCalculator();
            //add head
            try {
                FFT_SnapShot snp_tt = snp_.getValue();
                Date date_temp = snp_tt.getStartTime();
                double sc_lon = snp_tt.getSeismicLongitude().doubleValue();
                double sc_lat = snp_tt.getSeismicLatitude().doubleValue();
                double station_vs30 = snp_tt.getVs30().doubleValue();

                double ep_center_vs30 = kr.predict(sc_lon, sc_lat);
                /*
                    1.set step points as 100 points and calc these points and get vs30
                    2.write a excel file with all data
                */
                //calc passby step points max and min predict vs30
                GeodeticCalculator geoCalc_t = new GeodeticCalculator();
                double max_vs30 = -1;
                double min_vs30 = -1;
                max_vs30 = Math.max(ep_center_vs30, station_vs30);
                min_vs30 = Math.min(ep_center_vs30, station_vs30);
                GlobalCoordinates st_p = new GlobalCoordinates(sc_lat, sc_lon);
                List<Double> vs30_higher_then_epc_nor_by_epc = Lists.newArrayList();
                List<Double> vs30_lower_then_epc_nor_by_epc = Lists.newArrayList();
                int step_count = 0;
                while (true) {
                    //fast vincenty
                    GeodeticCurve geoCurve_t = geoCalc_t.calculateGeodeticCurve(Configs.reference, st_p, new GlobalCoordinates(snp_tt.getStationLatitude().doubleValue(), snp_tt.getStationLongitude().doubleValue()));
                    //double temp_distance = EarthCalc.vincentyDistance(st_p, end_p);
                    //double azimuth_temp = EarthCalc.vincentyBearing(st_p, end_p);
                    double temp_distance = geoCurve_t.getEllipsoidalDistance();
                    double azimuth_temp = geoCurve_t.getAzimuth();
                    step_count++;
                    if (temp_distance < Utils.get_step_distance()) {//add last
                        max_vs30 = Math.max(max_vs30, station_vs30);
                        min_vs30 = Math.min(min_vs30, station_vs30);
                        if (station_vs30 > ep_center_vs30) {//bigger or equal can transport more energy
                            double exceed_by_percent = (station_vs30-ep_center_vs30)/ep_center_vs30;
                            vs30_higher_then_epc_nor_by_epc.add(exceed_by_percent);
                        } else {
                            double lack_by_percent = (station_vs30-ep_center_vs30)/ep_center_vs30;
                            vs30_lower_then_epc_nor_by_epc.add(lack_by_percent);
                        }
                        break;
                    } else {//dist is bigger then a step
                        GlobalCoordinates dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, st_p, azimuth_temp, Utils.get_step_distance(), new double[1]);
                        double _vs30 = kr.predict(dest.getLongitude(), dest.getLatitude());
                        max_vs30 = Math.max(max_vs30, _vs30);
                        min_vs30 = Math.min(min_vs30, _vs30);
                        st_p = dest;
                        if (_vs30 > ep_center_vs30) {//bigger or equal can transport more energy
                            double exceed_by_percent = (_vs30-ep_center_vs30)/ep_center_vs30;
                            vs30_higher_then_epc_nor_by_epc.add(exceed_by_percent);
                        } else {
                            double lack_by_percent = (_vs30-ep_center_vs30)/ep_center_vs30;
                            vs30_lower_then_epc_nor_by_epc.add(lack_by_percent);
                        }
                    }
                }
                double vs30_higher_tenbe_avg = 0;
                double vs30_lower_tenbe_avg = 0;
                if (vs30_higher_then_epc_nor_by_epc.size() > 0) {
                    vs30_higher_tenbe_avg = vs30_higher_then_epc_nor_by_epc.stream().mapToDouble(vs30 -> vs30).average().getAsDouble();
                }
                if (vs30_lower_then_epc_nor_by_epc.size() > 0) {
                    vs30_lower_tenbe_avg = vs30_lower_then_epc_nor_by_epc.stream().mapToDouble(vs30 -> vs30).average().getAsDouble()*-1;
                }
                
                Row row = sheet.createRow(++rowCount);
                String[] cell_string = {format.format(date_temp), Configs.df.format(ep_center_vs30), Configs.df.format(station_vs30), Configs.df.format(snp_tt.getMagnitude()),
                                        Configs.df.format(snp_tt.getEpicenterDistance().doubleValue()), Configs.df.format(snp_tt.getDepthOfFocus().doubleValue()), Configs.df.format(snp_tt.getMax_U_Hz_20()),
                                        Configs.df.format(snp_tt.getMax_N_Hz_20()), Configs.df.format(snp_tt.getMax_E_Hz_20()), Configs.df.format(max_vs30), Configs.df.format(min_vs30),
                                        Configs.df.format(vs30_higher_tenbe_avg*100D), Configs.df.format(vs30_lower_tenbe_avg*100D)};
                for (int rc = 0; rc < cell_string.length; rc++) {
                    Cell cell = row.createCell(rc);
                    if (rc > 0) {//numbers
                        cell.setCellValue(Double.valueOf(cell_string[rc]));
                    } else {//date
                        cell.setCellValue(cell_string[rc]);
                    }
                    //Cell cell = row.createCell(rc);
                    //cell.setCellValue(cell_string[rc]);
                }
                try (FileOutputStream outputStream = new FileOutputStream(file_name+".xlsx")) {
                    snp_group_workbook.write(outputStream);
                } catch (FileNotFoundException ex) {
                    Utils.logger.fatal(ex);
                } catch (IOException ex) {
                    Utils.logger.fatal(ex);
                }
            } catch (Exception ex) {
                --rowCount;
                //Utils.logger.fatal(ex+": Error while calculating Geodetic Curve. "+ format.format(snp_.getValue().getStartTime())+" SSID: "+snp_.getValue().getSSID());
                Logger.getLogger(Relations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    
    
}


