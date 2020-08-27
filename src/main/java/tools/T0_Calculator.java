/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import softpak.gdms_fft.FFT;

/**
 *
 * @author you
 */
public class T0_Calculator {
    List<String[]> data;
    int frontleg;
    int rearleg;
    boolean is_strong;
    
    
    public T0_Calculator() {
        
    }
    
    public T0_Calculator(List<String[]> data, int frontleg, int rearleg) {
        this.data = data;
        this.frontleg = frontleg;
        this.rearleg = rearleg;
    }
    
    public T0_Calculator(List<String[]> data, int frontleg, int rearleg, boolean is_strong) {
        this.data = data;
        this.frontleg = frontleg;
        this.rearleg = rearleg;
        this.is_strong = is_strong;
    }
    
    public void calculate() {
        int data_size = this.data.size();
        double g = 9806.665D;
        try {

            String st_name = this.data.get(0)[3];
            //calc Response Spectrum
            List<double[]> x_axis = Lists.newLinkedList();
            List<double[]> y_U_Sa_list = Lists.newLinkedList();
            List<double[]> y_N_Sa_list = Lists.newLinkedList();
            List<double[]> y_E_Sa_list = Lists.newLinkedList();
            List<double[]> y_U_Sv_list = Lists.newLinkedList();
            List<double[]> y_N_Sv_list = Lists.newLinkedList();
            List<double[]> y_E_Sv_list = Lists.newLinkedList();
            
            List<Double> avg_pga_list = Lists.newArrayList();
            
            
            for (int p = this.frontleg; p < this.rearleg; p ++) {
                try {
                    String[] s = this.data.get(p);
                    //System.out.println(Arrays.toString(s));
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
                    avg_pga_list.add(fft.getPGA_E().doubleValue());
                    avg_pga_list.add(fft.getPGA_N().doubleValue());
                    
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
                    Logger.getLogger(T0_Calculator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(T0_Calculator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            double avg_pga = avg_pga_list.stream().mapToDouble(pga -> Math.abs(pga)).average().getAsDouble();
            //System.out.println(st_name+", "+avg);
            
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
                //System.out.println(sv_temp);

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
            
            XYChart acc_chart_ne = QuickChart.getChart(st_name+" Ca: "+Configs.df.format(ne_acc_max_value)+" times", "Period(sec)", "Sa", "NE avg Axis", x_axis.get(T_min_pos), NE_avg_Sa);
            acc_chart_ne.getStyler().setXAxisMax(5D);
            BitmapEncoder.saveBitmap(acc_chart_ne, Utils.get_MainPath()+"/Sa_"+this.is_strong+"_"+st_name+"_NE.png", BitmapEncoder.BitmapFormat.PNG);
 
            //log T
            double[] time = x_axis.get(T_min_pos);
            int low_limit_pos = 0;
            double low_limit_val = 0;
            int pgv_pos = 0;
            double pgv_val = 0;


            double step = x_axis.get(T_min_pos)[1];
            //System.out.println(step);
            for (int i = 0 ; i < NE_avg_Sv.length ; i++) {
                if (NE_avg_Sa[i] > 0) {
                    double log_temp = Math.log10(NE_avg_Sa[i]);
                    NE_avg_Sa[i] = log_temp;
                }
                
                if (NE_avg_Sv[i] > 0) {
                    double log_temp = Math.log10(NE_avg_Sv[i]);
                    
                    NE_avg_Sv[i] = log_temp;
                }
                if (time[i] > 0) {
                    double log_temp = Math.log10(time[i]);
              
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
            
            //find max acc
            double acc_max_value = 0;
            int acc_max_pos = 0;// 5/0.004
            for (int i = 0 ; i < NE_avg_Sa.length ; i++) {
                if (NE_avg_Sa[i] > acc_max_value) {
                    acc_max_value = NE_avg_Sa[i];
                    acc_max_pos = i;
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
            double[] NE_avg_Sa_new = Arrays.copyOfRange(NE_avg_Sa, low_limit_pos, NE_avg_Sa.length);

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
                System.out.println(st_name+": "+Configs.coord_df.format(T0)+" "+this.is_strong+" Ca: "+Configs.coord_df.format(ne_acc_max_value)+ " AVG_PGA: "+Configs.coord_df.format(avg_pga));
            }
            //if (Sa_range.size() > 0) {
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
            BitmapEncoder.saveBitmap(vel_chart_ne, Utils.get_MainPath()+"/Vel_"+this.is_strong+"_"+st_name+"_NE.png", BitmapEncoder.BitmapFormat.PNG);
            //}
            
            // Show it
            //acc_chart_u.getStyler().setXAxisMax(5D);



            //BitmapEncoder.saveBitmap(acc_chart_u, Utils.get_MainPath()+"/"+st_name+"_U.png", BitmapEncoder.BitmapFormat.PNG);




            //new SwingWrapper(acc_chart_u).displayChart();
            //new SwingWrapper(acc_chart_ne).displayChart();
        } catch (IOException ex) {
            Logger.getLogger(T0_Calculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
    
}
