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
import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Map.Entry.comparingByValue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import javafx.stage.Stage;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.pujun.interp.*;
import softpak.gdms_fft.FFT_SnapShot;
import softpak.gdms_fft.FX_2D_Map_Window_Interpolation_Test;
import softpak.gdms_fft.Interpolation_Result_Obj;
import softpak.gdms_fft.Seismic;
import softpak.gdms_fft.Station;

/**
 *
 * @author you
 */
public class Interpolation_Test {
    
    public Interpolation_Test() {
        
    }
    //1. select predict targets using Latin hypercube sampling
    //2. calculate nearest stations in range or 30 stations from samples
    //3. train each set and predict it
    
    
    public void run_test(int sample_div, List<Station> st_list, double error_percent_setting, double error_value_setting) {
        Map<String, Station> all_st_map = Maps.newConcurrentMap();
        st_list.stream().forEach(st -> {
            all_st_map.put(st.getStationName(), st);
        });
        
        //ConvexHullAlgorithms cha = new ConvexHullAlgorithms();
        //List outline_p = cha.calcPoints(st_list);
        
        
        ConcaveHullAlgorithms cha = new ConcaveHullAlgorithms();
        List<Point> pl = Lists.newArrayList();
        st_list.stream().forEach(st -> {
            Point pt = new Point(st.getStationName(), Double.valueOf(st.getLongitude()), Double.valueOf(st.getLatitude()));
            pl.add(pt);
        });
        
        List<String> outline_p = Lists.newArrayList();
        cha.calculateConcaveHull(pl, 3).stream().forEach(ss -> outline_p.add(ss.getStation()));
        
        
        List<Station> st_rm_list = Lists.newArrayList();
        outline_p.stream().forEach(op -> {
            Station st_temp = Utils.get_StationMap().get(op);
            st_rm_list.add(st_temp);
        });
        
        
        
        st_list.removeAll(st_rm_list);
        
        
        FX_2D_Map_Window_Interpolation_Test kr_ff = new FX_2D_Map_Window_Interpolation_Test(outline_p);
        try {
            kr_ff.start(new Stage());
        } catch (Exception ex) {
            Logger.getLogger(Interpolation_Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        kr_ff.setTitle("2D Map. Kriging Test: ");
        
        
        
        Set<Double> lon_cube = Sets.newConcurrentHashSet();
        Set<Double> lat_cube = Sets.newConcurrentHashSet();
        List<Station> station_sample_target = Lists.newArrayList();
        List<Station> station_predic_target = Lists.newArrayList();
        List<Station> station_temp = st_list;
        Random rnd = new Random();
        rnd.setSeed(3345678L);
        int tests = 1;
        //int sample_size = station_temp.size()/2/sample_div;
        int sample_size = station_temp.size();
        int target_size = station_temp.size()/2;
        //int target_size = 115;
        System.out.println("===================================================");
        System.out.println("Divide parts: "+sample_div+". Max Sample size: "+sample_size);
        while (tests < 2) {
            try {
                System.out.println("========= test"+tests+" =========");
                lon_cube.clear();
                lat_cube.clear();
                station_sample_target.clear();
                station_predic_target.clear();
                Collections.shuffle(station_temp, rnd);
                int count = 0;
                while(count < station_temp.size()) {
                    Station st_temp = station_temp.get(count);
                    //1.check stations` corrds are not the biggest or smallest
                    //Concav hull algorithms
                    station_sample_target.add(st_temp);
                    //latin hypercube sampling
                    /*
                    if (station_sample_target.size() < sample_size) {
                        if (lat_cube.add(Double.valueOf(st_temp.getLatitude())) && lon_cube.add(Double.valueOf(st_temp.getLongitude()))) {
                            station_sample_target.add(st_temp);
                        } else {
                            station_predic_target.add(st_temp);
                        }
                    } else {
                        if (station_predic_target.size() < target_size) {
                            station_predic_target.add(st_temp);
                        }
                    }*/
                    count++; 
                }
                System.out.println("Sample size: "+station_sample_target.size());
                System.out.println("Predict size: "+station_predic_target.size());
                //do_kriging_test(station_sample_target, station_predic_target);
                //do_rbf_test(station_sample_target, station_predic_target);
                
                
                //send a map
                Map<String, Interpolation_Result_Obj> kriging_result_map = do_kriging_test(station_sample_target, all_st_map);
                Map<String, Interpolation_Result_Obj> rbf_result_map = do_rbf_test(station_sample_target, all_st_map);
                
                /*
                FX_2D_Map_Window_Interpolation_Test kr_ff = new FX_2D_Map_Window_Interpolation_Test(kriging_result_map, outline_p);
                FX_2D_Map_Window_Interpolation_Test rbf_ff = new FX_2D_Map_Window_Interpolation_Test(rbf_result_map, outline_p);
                kr_ff.start(new Stage());
                kr_ff.setTitle("2D Map. Kriging Test: "+ tests);
                rbf_ff.start(new Stage());
                rbf_ff.setTitle("2D Map. RBF Test: "+ tests);
                //check seismic events in polygon and remove events if stations` predict error are over 40%
                
                Polygon outline_ps = new Polygon();//shape
                for (int stc = 0; stc < st_list.size() ; stc++) {
                    Station st_p = st_list.get(stc);
                    double lat_p = Double.valueOf(st_p.getLatitude());
                    double lon_p = Double.valueOf(st_p.getLongitude());
                    outline_ps.addPoint((int)(lon_p*10000D), (int)(lat_p*10000D));
                }
                */
                /*
                Queue<Seismic> inner_events = Queues.newConcurrentLinkedQueue();
                Utils.get_seismic_map().entrySet().stream().forEach(sc -> {
                    double lat = sc.getValue().getLatitude().doubleValue();
                    double lon = sc.getValue().getLongitude().doubleValue();
                    Map<String, Double> dist_map = Maps.newLinkedHashMap();
                    //if (outline_ps.contains(lon*10000D, lat*10000D)) {//1. in polygon
                        //2.nearby station predict error under 40%
                        //calc dist and sort
                        station_sample_target.stream().forEach(stt -> {
                            GeodeticCalculator geoCalc = new GeodeticCalculator();
                            GlobalCoordinates st_p = new GlobalCoordinates(lat, lon);//seismic
                            GlobalCoordinates st_s = new GlobalCoordinates(Double.valueOf(stt.getLatitude()), Double.valueOf(stt.getLongitude()));//station
                            GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, st_s);
                            double temp_distance = geoCurve.getEllipsoidalDistance();
                            dist_map.put(stt.getStationName(), temp_distance);
                        });
                        Map<String, Double> sorted_dist_map = dist_map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                        
                        int c = 0;//at least 30
                        int error_count = 0; //1
                        for (Map.Entry<String, Double> sp_temp : sorted_dist_map.entrySet()) {
                            //System.out.println(sp_temp.getValue().doubleValue());
                            
                            if (c < 4) {//2. 3 nearest points to check
                                //System.out.println(c);
                                String StationName = sp_temp.getKey();
                                double error_p = Math.abs(kriging_result_map.get(StationName).get_ErrorPercent());
                                //System.out.println(error_p);
                                double error_val = Math.abs(kriging_result_map.get(StationName).get_ErrorValue());
                                if (error_p > error_percent_setting || error_val > error_value_setting) {
                                    error_count++;
                                }
                            } else {
                                //System.out.println(error_count);
                                if (error_count == 0) {
                                    inner_events.add(sc.getValue());
                                }
                                break;
                            }
                            c++;
                        }
                    //}
                });       
                System.out.println("Inner Seicmic Events: "+inner_events.size());
                //free field events
                Map<String, Map<String, FFT_SnapShot>> snp_map = Maps.newConcurrentMap();
                
                Utils.get_fft_snp_map().entrySet().parallelStream().forEach(map -> {
                    map.getValue().entrySet().stream().forEach(snp -> {
                        inner_events.stream().forEach(sce -> {
                            Map<String, FFT_SnapShot> temp_fft_snp_datamap = snp_map.get(snp.getValue().getStationCode());
                            if (snp.getValue().getStartTime().equals(sce.getStartTime())) {
                                if (temp_fft_snp_datamap != null) {
                                    temp_fft_snp_datamap.put(snp.getValue().getSSID(), snp.getValue());
                                } else {//null
                                    Map<String, FFT_SnapShot> temp_emptyff_snp_tmap = Maps.newConcurrentMap();
                                    temp_emptyff_snp_tmap.put(snp.getValue().getSSID(), snp.getValue());
                                    snp_map.put(snp.getValue().getStationCode(), temp_emptyff_snp_tmap);
                                }
                            }
                        });
                    });
                });
                int sum = 0;
                for (Map.Entry<String, Map<String, FFT_SnapShot>> sp_temp : snp_map.entrySet()) {
                    sum +=sp_temp.getValue().size();
                }
                System.out.println("Inner FreeField Events: "+sum);
                */
                tests++;
                /*
                Map<Date, Seismic> seismic_map = Maps.newConcurrentMap();
                inner_events.parallelStream().forEach(ie -> seismic_map.put(ie.getStartTime(), ie));
                
                
                Relations rl = new Relations();
                rl.prep_kriging(station_sample_target);
                rl.set_Seismic_Map(seismic_map);
                rl.set_Snp_Selected_Map(snp_map);
                rl.cal_relations();
                System.out.println("Done");
                */
            } catch (Exception ex) {
                Logger.getLogger(Interpolation_Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    double distance = 1000*10;//10 km
    double max_distance = 1000*10;//10 km
    
    private Map<String,Interpolation_Result_Obj> do_kriging_test(List<Station> sample, Map<String, Station> all_st_map) {
        Map<String,Interpolation_Result_Obj> result = Maps.newLinkedHashMap();
        //0. draw a 2D predict map
        
        Kriging kr = new Kriging();
        //variogram model
        String model = "spherical";//spherical,exponential,gaussian
        System.out.println(model);
        List<Double> rmse_vs30_val = Lists.newArrayList();
        List<Double> rmse_vs30_percent = Lists.newArrayList();
        sample.stream().forEach(st_predict -> {
            Interpolation_Result_Obj ir_temp = new Interpolation_Result_Obj();
            ir_temp.set_StationName(st_predict.getStationName());
            //1. calc distance    
            
            
            Map<String, Double> dist_map = Maps.newLinkedHashMap();
            all_st_map.entrySet().stream().forEach(st_sample -> {
                if (!st_sample.getValue().getStationName().equals(st_predict.getStationName())) {
                    GeodeticCalculator geoCalc = new GeodeticCalculator();
                    GlobalCoordinates st_p = new GlobalCoordinates(Double.valueOf(st_predict.getLatitude()), Double.valueOf(st_predict.getLongitude()));
                    GlobalCoordinates st_s = new GlobalCoordinates(Double.valueOf(st_sample.getValue().getLatitude()), Double.valueOf(st_sample.getValue().getLongitude()));

                    GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, st_s);
                    double temp_distance = geoCurve.getEllipsoidalDistance();
                    dist_map.put(st_sample.getValue().getStationName(), temp_distance);
                }
            });
            Map<String, Double> sorted_dist_map = dist_map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
            
            //System.out.println(sorted_dist_map.size()+" sort by dist: " + sorted_dist_map);
            //2. get nearest 30 points or all in 10km range
            int sample_count = 1;//at least 30
            
            List<Station> train_list = Lists.newLinkedList();
            
            for (Map.Entry<String, Double> sp_temp : sorted_dist_map.entrySet()) {
                if (sample_count < 31) {//30 samples basic but in 50 km
                    if (sp_temp.getValue() <= max_distance || train_list.size() < 10) {
                        Station st_temp = all_st_map.get(sp_temp.getKey());
                        train_list.add(st_temp);
                        ir_temp.get_RefStationList().add(st_temp.getStationName());
                    }
                } else {//distance in 10km
                    if (sp_temp.getValue() <= distance) {
                        Station st_temp = all_st_map.get(sp_temp.getKey());
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
            double vs30_predict = kr.predict(Double.valueOf(st_predict.getLongitude()), Double.valueOf(st_predict.getLatitude()));
            ir_temp.set_PredictValue(vs30_predict);
            double vs30_cur = Double.valueOf(st_predict.getVs30());
            ir_temp.set_CurrentValue(vs30_cur);
            double error = vs30_predict-vs30_cur;
            ir_temp.set_ErrorValue(error);
            rmse_vs30_val.add(error);//pridict-current
            double error_percent = 100D*(vs30_predict-vs30_cur)/vs30_cur;
            ir_temp.set_ErrorPercent(error_percent);
            rmse_vs30_percent.add(error_percent);
            result.put(ir_temp.get_StationName(), ir_temp);
        });
        
        
        //gen data arrays
        //long st =System.currentTimeMillis();
        
        //System.out.println("train complete");
        
       
        
        System.out.println("RMSE Vs30(kriging): "+Configs.data_df.format(Math.sqrt(rmse_vs30_val.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_val.size()))+" m/s. "+
                                                 Configs.data_df.format(Math.sqrt(rmse_vs30_percent.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_percent.size()))+" %");
        System.out.println("Average Vs30(kriging): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" m/s. "+
                                                    Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" %");
        System.out.println("Max Vs30(kriging): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" m/s. "+
                                                Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" %");
        
        return result;
    }
    
    private Map<String,Interpolation_Result_Obj> do_rbf_test(List<Station> sample, Map<String, Station> all_st_map) {
        Map<String,Interpolation_Result_Obj> result = Maps.newLinkedHashMap();
        List<Double> rmse_vs30_val = Lists.newArrayList();
        List<Double> rmse_vs30_percent = Lists.newArrayList();
        sample.stream().forEach(st_predict -> {
            Interpolation_Result_Obj ir_temp = new Interpolation_Result_Obj();
            ir_temp.set_StationName(st_predict.getStationName());
            //1. calc distance    
            Map<String, Double> dist_map = Maps.newLinkedHashMap();
            all_st_map.entrySet().stream().forEach(st_sample -> {
                if (!st_sample.getValue().getStationName().equals(st_predict.getStationName())) {
                    GeodeticCalculator geoCalc = new GeodeticCalculator();
                    GlobalCoordinates st_p = new GlobalCoordinates(Double.valueOf(st_predict.getLatitude()), Double.valueOf(st_predict.getLongitude()));
                    GlobalCoordinates st_s = new GlobalCoordinates(Double.valueOf(st_sample.getValue().getLatitude()), Double.valueOf(st_sample.getValue().getLongitude()));

                    GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, st_s);
                    double temp_distance = geoCurve.getEllipsoidalDistance();
                    dist_map.put(st_sample.getValue().getStationName(), temp_distance);
                }
            });
            Map<String, Double> sorted_dist_map = dist_map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
            //System.out.println(sorted_dist_map.size()+" sort by dist: " + sorted_dist_map);
            //2. get nearest 30 points or all in 10km range
            int sample_count = 1;//at least 30
            
            List<Station> train_list = Lists.newLinkedList();
            for (Map.Entry<String, Double> sp_temp : sorted_dist_map.entrySet()) {
                if (sample_count < 31) {//30 samples basic but in 50 km
                    if (sp_temp.getValue() <= max_distance || train_list.size() < 10) {
                        Station st_temp = all_st_map.get(sp_temp.getKey());
                        train_list.add(st_temp);
                        ir_temp.get_RefStationList().add(st_temp.getStationName());
                    }
                } else {//distance in 10km
                    if (sp_temp.getValue() <= distance) {
                        Station st_temp = all_st_map.get(sp_temp.getKey());
                        train_list.add(st_temp);
                        ir_temp.get_RefStationList().add(st_temp.getStationName());
                    } else {
                        break;
                    }
                }
                sample_count++;
            }
            
            int real_st_num = train_list.size();
            double[][] xlon_ylat = new double[real_st_num][2];
            double[] value = new double[real_st_num];
            for (int c = 0 ; c < real_st_num ; c++) {
                value[c] = Double.valueOf(train_list.get(c).getVs30());
                xlon_ylat[c] = new double[2];
                xlon_ylat[c][0] = Double.valueOf(train_list.get(c).getLongitude());
                xlon_ylat[c][1] = Double.valueOf(train_list.get(c).getLatitude());
            }
            RBF_interp rbf = new RBF_interp(xlon_ylat,value, new RBF_linear());//RBF_thinplate, RBF_linear

            
            
            double[] pt = {Double.valueOf(st_predict.getLongitude()), Double.valueOf(st_predict.getLatitude())};
            double vs30_predict = rbf.interp(pt);
            ir_temp.set_PredictValue(vs30_predict);
            double vs30_cur = Double.valueOf(st_predict.getVs30());
            ir_temp.set_CurrentValue(vs30_cur);
            //System.out.println("predict val: "+vs30_predict+"; current val: "+ st.getVs30());
            double error = vs30_predict-vs30_cur;
            ir_temp.set_ErrorValue(error);
            rmse_vs30_val.add(error);//pridict-current
            double error_percent = 100D*(vs30_predict-vs30_cur)/vs30_cur;
            ir_temp.set_ErrorPercent(error_percent);
            rmse_vs30_percent.add(error_percent);
            result.put(ir_temp.get_StationName(), ir_temp);
        });
        
        
        
        
        
        System.out.println("RMSE Vs30(RBF): "+Configs.data_df.format(Math.sqrt(rmse_vs30_val.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_val.size()))+" m/s. "+
                                             Configs.data_df.format(Math.sqrt(rmse_vs30_percent.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_percent.size()))+" %");
        System.out.println("Average Vs30(RBF): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" m/s. "+
                                                Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" %");
        System.out.println("Max Vs30(RBF): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" m/s. "+
                                            Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" %");
        
        return result;
    }
    
    
    
    private void do_kriging_test(List<Station> sample, List<Station> predict) {
        Kriging kr = new Kriging();
        //variogram model
        String model = "spherical";//spherical,exponential,gaussian
        System.out.println(model);
        //gen data arrays
        //long st =System.currentTimeMillis();
        int real_st_num = sample.size();
        //int real_st_num = Utils.get_StationList().size();//348 ok, 347 the problem //TAP003, TAP004
        kr.set_station_num(real_st_num);
        double[] value = new double[real_st_num];
        double[] x_lon = new double[real_st_num];
        double[] y_lat = new double[real_st_num];

        for (int c = 0 ; c < real_st_num ; c++) {
            value[c] = Double.valueOf(sample.get(c).getVs30());
            x_lon[c] = Double.valueOf(sample.get(c).getLongitude());
            y_lat[c] = Double.valueOf(sample.get(c).getLatitude());
        }
        kr.set_data(value, x_lon, y_lat, model);
        kr.do_train();
        System.out.println("train complete");
        List<Double> rmse_vs30_val = Lists.newArrayList();
        List<Double> rmse_vs30_percent = Lists.newArrayList();
       
        predict.forEach(st -> {
            double vs30_predict = kr.predict(Double.valueOf(st.getLongitude()), Double.valueOf(st.getLatitude()));
            //System.out.println("predict val: "+vs30_predict+"; current val: "+ st.getVs30());
            double vs30_cur = Double.valueOf(st.getVs30());
            rmse_vs30_val.add(vs30_predict-vs30_cur);//pridict-current
            rmse_vs30_percent.add(100D*(vs30_predict-vs30_cur)/vs30_cur);//%
            
        });
        System.out.println("RMSE Vs30(kriging): "+Configs.data_df.format(Math.sqrt(rmse_vs30_val.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_val.size()))+" m/s. "+
                                                 Configs.data_df.format(Math.sqrt(rmse_vs30_percent.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_percent.size()))+" %");
        System.out.println("Average Vs30(kriging): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" m/s. "+
                                                    Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" %");
        System.out.println("Max Vs30(kriging): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" m/s. "+
                                                Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" %");
    }
    
    private void do_rbf_test(List<Station> sample, List<Station> predict) {
        int real_st_num = sample.size();
        double[][] xlon_ylat = new double[real_st_num][2];
        double[] value = new double[real_st_num];
        for (int c = 0 ; c < real_st_num ; c++) {
            value[c] = Double.valueOf(sample.get(c).getVs30());
            xlon_ylat[c] = new double[2];
            xlon_ylat[c][0] = Double.valueOf(sample.get(c).getLongitude());
            xlon_ylat[c][1] = Double.valueOf(sample.get(c).getLatitude());
        }
        RBF_interp rbf = new RBF_interp(xlon_ylat,value, new RBF_linear());//RBF_thinplate, RBF_linear
        
        List<Double> rmse_vs30_val = Lists.newArrayList();
        List<Double> rmse_vs30_percent = Lists.newArrayList();
        predict.forEach(st -> {
            double[] pt = {Double.valueOf(st.getLongitude()), Double.valueOf(st.getLatitude())};
            double vs30_predict = rbf.interp(pt);
            double vs30_cur = Double.valueOf(st.getVs30());
            //System.out.println("predict val: "+vs30_predict+"; current val: "+ st.getVs30());
            rmse_vs30_val.add(vs30_predict-vs30_cur);//pridict-current
            rmse_vs30_percent.add(100D*(vs30_predict-vs30_cur)/vs30_cur);//%
        });
        System.out.println("RMSE Vs30(RBF): "+Configs.data_df.format(Math.sqrt(rmse_vs30_val.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_val.size()))+" m/s. "+
                                             Configs.data_df.format(Math.sqrt(rmse_vs30_percent.parallelStream().mapToDouble(d -> d*d).sum()/(double)rmse_vs30_percent.size()))+" %");
        System.out.println("Average Vs30(RBF): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" m/s. "+
                                                Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).average().getAsDouble())+" %");
        System.out.println("Max Vs30(RBF): "+Configs.data_df.format(rmse_vs30_val.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" m/s. "+
                                            Configs.data_df.format(rmse_vs30_percent.parallelStream().mapToDouble(d -> Math.abs(d)).max().getAsDouble())+" %");
    }
    
    
    
}
