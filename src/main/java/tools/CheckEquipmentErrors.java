/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import softpak.gdms_fft.FFT;

/**
 *
 * @author you
 */
public class CheckEquipmentErrors {
    Set<Double> data_vary_N = Sets.newLinkedHashSet();
    Set<Double> data_vary_E = Sets.newLinkedHashSet();
    double g = 9806.665D;
    
    public CheckEquipmentErrors() {
        
    }
    
    
    public void check() throws FileNotFoundException, IOException {
        Queue<String> path = Queues.newConcurrentLinkedQueue();
        BufferedReader br = null;
        String sCurrentLine = null;
        //br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/gal_more_or_equal_to_80.txt"));
        br = new BufferedReader(new FileReader(Utils.get_MainPath()+"/recorrect_20200624.txt"));
        
        while((sCurrentLine = br.readLine()) != null) {
            path.add(sCurrentLine);
        }
        br.close();
        
        path.parallelStream().forEach(f_path -> {
            try {
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
                
                if (eff_count >= 5) {
                    fft.transpose();
                    fft.calc_SSID();
                    fft.transferDataToFFT();
                }
                double[] rs_u_in = fft.getOriginData_U();
                double[] rs_n_in = fft.getOriginData_N();
                double[] rs_e_in = fft.getOriginData_E();
                /*
                for (int i = 0; i < rs_n_in.length-1;i++) {
                    data_vary_N.add(Math.abs((rs_n_in[i] - rs_n_in[i+1])/rs_n_in[i]));
                    data_vary_E.add(Math.abs((rs_e_in[i] - rs_e_in[i+1])/rs_e_in[i]));
                }*/
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
                //regular Cv
                for (int i = 0; i < result_u.length;i++) {
                    result_u[i] = result_u[i]/Math.abs(fft.getPGA_U().doubleValue());
                    result_n[i] = result_n[i]/Math.abs(fft.getPGA_N().doubleValue());
                    result_e[i] = result_e[i]/Math.abs(fft.getPGA_E().doubleValue());
                }
                
                
                
                //draw acc and spectrum chart
                XYChart acc_chart_u = QuickChart.getChart(fft.getStationCode()+"_"+fft.getSeismic_StartTime()+" U Max: "+RS_u+" sec", "Period(sec)", "Cv", "U Axis", rsc.getT(), result_u);
                XYChart acc_chart_n = QuickChart.getChart(fft.getStationCode()+"_"+fft.getSeismic_StartTime()+" N Max: "+RS_n+" sec", "Period(sec)", "Cv", "N Axis", rsc.getT(), result_n);
                XYChart acc_chart_e = QuickChart.getChart(fft.getStationCode()+"_"+fft.getSeismic_StartTime()+" E Max: "+RS_e+" sec", "Period(sec)", "Cv", "E Axis", rsc.getT(), result_e);
                // Show it
                acc_chart_u.getStyler().setXAxisMax(5D);
                acc_chart_n.getStyler().setXAxisMax(5D);
                acc_chart_e.getStyler().setXAxisMax(5D);
                /*
                BitmapEncoder.saveBitmap(acc_chart_u, Utils.get_MainPath()+"/"+fft.getFileName()+"_U.png", BitmapEncoder.BitmapFormat.PNG);
                BitmapEncoder.saveBitmap(acc_chart_n, Utils.get_MainPath()+"/"+fft.getFileName()+"_N.png", BitmapEncoder.BitmapFormat.PNG);
                BitmapEncoder.saveBitmap(acc_chart_e, Utils.get_MainPath()+"/"+fft.getFileName()+"_E.png", BitmapEncoder.BitmapFormat.PNG);*/


                new SwingWrapper(acc_chart_u).displayChart();
                new SwingWrapper(acc_chart_n).displayChart();
                new SwingWrapper(acc_chart_e).displayChart();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CheckEquipmentErrors.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CheckEquipmentErrors.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(CheckEquipmentErrors.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        List<Double> result_list_N = Lists.newLinkedList(data_vary_N);
        List<Double> result_list_E = Lists.newLinkedList(data_vary_E);
        
        Collections.sort(result_list_N, new Comparator<Double>() {
            @Override
            public int compare(Double lhs, Double rhs) {
                // TODO Auto-generated method stub
                int returning = lhs.compareTo(rhs);
                return returning;
            }
        });
        Collections.sort(result_list_E, new Comparator<Double>() {
            @Override
            public int compare(Double lhs, Double rhs) {
                // TODO Auto-generated method stub
                int returning = lhs.compareTo(rhs);
                return returning;
            }
        });
        /*
        System.out.println(result_list_N.get(result_list_N.size()-1)+", "+ result_list_N.get(0));
        System.out.println(result_list_E.get(result_list_E.size()-1)+", "+ result_list_E.get(0));*/
        result_list_N.stream().forEach(System.out::println);
        System.out.println("/////////////////////////////////////////////////////");
        result_list_E.stream().forEach(System.out::println);
    }
}
