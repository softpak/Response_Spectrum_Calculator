/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jtransforms.fft.DoubleFFT_1D;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import tools.Configs;
import tools.Utils;
import static tools.Utils.logger;

/**
 *
 * @author you
 */
public class FFT implements Serializable {
    private String SSID;
    private BigDecimal RecordLength;//sec
    private BigDecimal SampleRate;//Hz
    private String StationCode;
    private String InstrumentKind;
    private String Accelerometer_StartTime;
    private String Seismic_StartTime;
    private String file_path;
    private String file_name;
    private transient double[][] array_o;
    private double[][] array_t;
    //private String[] hertz;
    private double[] hertz;
    private double[] hertz_20;
    private double[] fft_U_in;
    private double[] fft_N_in;
    private double[] fft_E_in;
    private double[] fft_array_U;
    private double[] fft_array_N;
    private double[] fft_array_E;
    private double[] fft_array_U_abs;
    private double[] fft_array_N_abs;
    private double[] fft_array_E_abs;
    
    private double[] fft_array_U_real;
    private double[] fft_array_N_real;
    private double[] fft_array_E_real;
    private double[] fft_array_U_imag;
    private double[] fft_array_N_imag;
    private double[] fft_array_E_imag;
    
    private double[] fft_array_U_abs_20;
    private double[] fft_array_N_abs_20;
    private double[] fft_array_E_abs_20;
    private double[] fft_array_U_nor;
    private double[] fft_array_U_nor_20;
    private double[] fft_array_N_nor;
    private double[] fft_array_N_nor_20;
    private double[] fft_array_E_nor;
    private double[] fft_array_E_nor_20;
    private double epicenter_distance;  //km
    private double magnitude;           //ML
    private double depth_of_focus;      //km
    private double pga_u;
    private double pga_n;
    private double pga_e;
    private transient int rows_o;
    private transient int columns_o;
    private int rows_t;
    private int columns_t;
    private int arr_count;
    private int U_max_pos = 0;
    private int N_max_pos = 0;
    private int E_max_pos = 0;
    private int U_max_20_pos = 0;
    private int N_max_20_pos = 0;
    private int E_max_20_pos = 0;
    DecimalFormat hertz_format = new DecimalFormat("#.##");
    
    /*
    private DefaultCategoryDataset line_chart_dataset_U = new DefaultCategoryDataset();
    private DefaultCategoryDataset line_chart_dataset_N = new DefaultCategoryDataset();
    private DefaultCategoryDataset line_chart_dataset_E = new DefaultCategoryDataset();*/
    
    /*
    #StationCode: ILA005
    #InstrumentKind: CV574(06106600.CVA)
    #StartTime: 2017/03/07-22:11:14.000 
    #RecordLength(sec):   51.000
    #SampleRate(Hz): 200
    #AmplitudeUnit:  gal. DCoffset(corr)
    #AmplitudeMAX. U:    4.094~   -4.353
    #AmplitudeMAX. N:    7.984~   -8.446
    #AmplitudeMAX. E:    8.373~   -9.942
    #DataSequence: Time U(+); N(+); E(+)
    #Data: 4F10.3 
    */
    public void setSSID(String str) {
        SSID = str;
    }
   
    public void calc_SSID() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rows_t; i++) {
            for (int j = 0; j < columns_t; j++) {
                sb.append(String.valueOf(array_t[i][j]));
            }
        }
        final byte[] hashbytes = digest.digest(new String(StationCode+InstrumentKind+Seismic_StartTime+sb).getBytes(StandardCharsets.UTF_8));
        //SSID = bytesToHex(hashbytes);
        setSSID(bytesToHex(hashbytes));
        Utils.add_ssid_to_ssidqueue(SSID);
        //System.out.println(SSID);
    }
    
    private String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public void setEpicenterDistance(double dist) {
        epicenter_distance = dist;
    }
    
    public void setMagnitude(double mag) {
        magnitude = mag;
    }
    
    public void setDepthOfFocus(double dep) {
        depth_of_focus = dep;
    }
    
    public void setStationCode(String str) {
        str = str.trim();
        //System.out.println(str);
        StationCode = str;
    }
    
    public void setInstrumentKind(String str) {
        str = str.trim();
        //System.out.println(str);
        InstrumentKind = str;
    }
    
    public void setAccelerometer_StartTime(String str) {
        str = str.trim();
        Accelerometer_StartTime = str;
        //System.out.println(str);
        /*
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
        StartTime = format.parse(str);
        System.out.println(StartTime);*/
    }
    
    public void setSeismic_StartTime(String str) {
        str = str.trim();
        Seismic_StartTime = str;
    }
    
    public void setSeismic_StartTime_from_path(String str) {
        //set from file_path
        //System.out.println(str);
        SimpleDateFormat ph_format = new SimpleDateFormat(Utils.Path_date_format, Locale.ENGLISH);
        SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
        String[] date_temp = str.split(Pattern.quote(File.separator));
        for (int a = 0; a < date_temp.length; a++) {
            try {
                Seismic_StartTime = format.format(ph_format.parse(date_temp[a]));
                //System.out.println(Seismic_StartTime);
            } catch (ParseException ex) {
                //Seismic_StartTime = " ";
                logger.debug(ex);
                //Logger.getLogger(FFT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    public void setRecordLength(String str) {
        str = str.trim();
        //System.out.println(str+"sec");
        RecordLength = new BigDecimal(str);
    }
    
    public void setSampleRate(String str) {
        str = str.trim();
        //System.out.println(str+"Hz");
        SampleRate = new BigDecimal(str);
    }
    
    //==========================================================================
    public String getSSID() {
        return SSID;
    }
    
    public double getEpicenterDistance() {
        return epicenter_distance;
    }
    
    public double getMagnitude() {
        return magnitude;
    }
    
    public double getDepthOfFocus() {
        return depth_of_focus;
    }
    
    public BigDecimal getRecordLength() {
        return RecordLength;
    }
    
    public BigDecimal getSampleRate() {
        return SampleRate;
    }
    
    public String getStationCode() {
        return StationCode;
    }
    
    public String getInstrumentKind() {
        return InstrumentKind;
    }
    
    public String getAccelerometer_StartTime() {
        return Accelerometer_StartTime;
    }
    
    public String getSeismic_StartTime() {
        return Seismic_StartTime;
    }
    
    
    public Number getMax_U_Hz() {
        return hertz[U_max_pos];
    }
    
    public Number getMax_N_Hz() {
        return hertz[N_max_pos];
    }
    
    public Number getMax_E_Hz() {
        return hertz[E_max_pos];
    }
    
    public Number getMax_U_Hz_20() {
        return hertz_20[U_max_20_pos];
    }
    
    public Number getMax_N_Hz_20() {
        return hertz_20[N_max_20_pos];
    }
    
    public Number getMax_E_Hz_20() {
        return hertz_20[E_max_20_pos];
    }
    
    public Number getPGA_U() {
        return pga_u;
    }
    
    public Number getPGA_N() {
        return pga_n;
    }
    
    public Number getPGA_E() {
        return pga_e;
    }
    
    public void initDataArray(int row, int column) {
        array_o = new double[row][column];
        array_t = new double[column][row];
        fft_array_U = new double[row*2];
        fft_array_N = new double[row*2];
        fft_array_E = new double[row*2];
        fft_array_U_real = new double[row*2];
        fft_array_N_real = new double[row*2];
        fft_array_E_real = new double[row*2];
        fft_array_U_imag = new double[row*2];
        fft_array_N_imag = new double[row*2];
        fft_array_E_imag = new double[row*2];
        fft_array_U_abs = new double[row*2];
        fft_array_N_abs = new double[row*2];
        fft_array_E_abs = new double[row*2];
        fft_array_U_nor = new double[row*2];
        fft_array_N_nor = new double[row*2];
        fft_array_E_nor = new double[row*2];
        rows_o = row;
        columns_o = column;
        rows_t = column;
        columns_t = row;
        arr_count = 0;
    }
    
    public void setArrayO(String data) {
        try {
            String[] da = data.trim().split("\\s+");
            if (da.length == 4) {//prevent incomplete data
                for (int c = 0; c < da.length; c++) { 
                    array_o[arr_count][c] = Double.valueOf(da[c]);
                }
                arr_count++;
            }
        } catch (Exception ex) {
            Utils.file_with_wrong_queue.add(file_path+"\\"+file_name+" has issues."+System.getProperty("line.separator"));
            logger.fatal(file_path+"\\"+file_name+" has issues.");
            //System.out.println(data+":"+ex+","+StationCode+":"+InstrumentKind+":"+StartTime+"."+arr_count);
        }
    }
    
    
    //transpose
    public void transpose() {
        for (int i = 0; i < rows_o; i++) {
            for (int j = 0; j < columns_o; j++) {
                array_t[j][i] = array_o[i][j];
            }
        }
    }
    
    
    public void setArrayO(int row, String data) {
        String[] da = data.trim().split("\\s+");
        for (int c = 0; c < da.length; c++) { 
            array_o[row][c] = Double.valueOf(da[c]);
        }
    }
    
    public void printArrayO() {
        for (int r = 0; r < rows_o; r++) {
            System.out.println();
            for (int c = 0; c < columns_o; c++) {
                System.out.print(array_o[r][c]+";");
            }
        }
        System.out.println();
    }
    
    public void printArrayT() {
        for (int r = 0; r < rows_t; r++) {
            System.out.println();
            for (int c = 0; c < columns_t; c++) {
                System.out.print(array_t[r][c]+";");
            }
        }
        System.out.println();
    }
    
    public double[] detrend_least_square(double[] x, double[] y) {
        if (x.length != y.length) {
            System.out.println(x.length +","+ y.length);
            throw new IllegalArgumentException("The x and y data elements needs to be of the same length");
        }
        SimpleRegression regression = new SimpleRegression();
        
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }
        
        double slope = regression.getSlope();
        double intercept = regression.getIntercept();
        
        for (int i = 0; i < x.length; i++) {
            //y -= intercept + slope * x 
            y[i] -= intercept + (x[i] * slope);
        }
        return y;
    }
    
    public double[] detrend_del_mean(double[] x) {
        double avg = Arrays.stream(x).average().orElse(Double.NaN);
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i] - avg;
        }
        return x;
    }
    
    
    public void transferDataToFFT() {
        //System.out.println("transferDataToFFT");
        int loopcount = columns_t/4;
        boolean loop = true;
        hertz = new double[columns_t];
        hertz_format.applyPattern("0.00");
        double U_max = 0;
        double N_max = 0;
        double E_max = 0;
        double U_max_20 = 0;
        double N_max_20 = 0;
        double E_max_20 = 0;
        
        /*
        DoubleFFT_1D fft_U = new DoubleFFT_1D(rows_o);
        DoubleFFT_1D fft_N = new DoubleFFT_1D(rows_o);
        DoubleFFT_1D fft_E = new DoubleFFT_1D(rows_o);*/
        
        fft_U_in = new double[rows_o];
        fft_N_in = new double[rows_o];
        fft_E_in = new double[rows_o];
        double[] pga_U_in = new double[rows_o];
        double[] pga_N_in = new double[rows_o];
        double[] pga_E_in = new double[rows_o];
        
        for (int c = 0;  c < columns_t; c++) {
            fft_U_in[c] = array_t[1][c];
            fft_N_in[c] = array_t[2][c];
            fft_E_in[c] = array_t[3][c];
            pga_U_in[c] = Math.abs(array_t[1][c]);
            pga_N_in[c] = Math.abs(array_t[2][c]);
            pga_E_in[c] = Math.abs(array_t[3][c]);
            hertz[c] = (double)c * (1D/RecordLength.doubleValue());
            //System.out.println(hertz[c]);
        }
        //get PGA
        double pga_u_value = 0;
        double pga_n_value = 0;
        double pga_e_value = 0;
        int U_pga_pos = 0;
        int N_pga_pos = 0;
        int E_pga_pos = 0;
        for (int i = 0 ; i < pga_U_in.length ; i++) {
            if (pga_U_in[i] > pga_u_value) {
                pga_u_value = pga_U_in[i];
                U_pga_pos = i;
            }
            if (pga_N_in[i] > pga_n_value) {
                pga_n_value = pga_N_in[i];
                N_pga_pos = i;
            }
            if (pga_E_in[i] > pga_e_value) {
                pga_e_value = pga_E_in[i];
                E_pga_pos = i;
            }
        }
        pga_u = fft_U_in[U_pga_pos];
        pga_n = fft_N_in[N_pga_pos];
        pga_e = fft_E_in[E_pga_pos];
        
        
        
        
        /*
        if (Configs.detrend_least_square_selected) {
            fft_U_in = detrend_least_square(hertz, fft_U_in);
            fft_N_in = detrend_least_square(hertz, fft_N_in);
            fft_E_in = detrend_least_square(hertz, fft_E_in);
        }
        
        if (Configs.detrend_del_mean_selected) {
            fft_U_in = detrend_del_mean(fft_U_in);
            fft_N_in = detrend_del_mean(fft_N_in);
            fft_E_in = detrend_del_mean(fft_E_in);
        }
        
        
        System.arraycopy(fft_U_in, 0, fft_array_U, 0, fft_U_in.length);
        System.arraycopy(fft_N_in, 0, fft_array_N, 0, fft_N_in.length);
        System.arraycopy(fft_E_in, 0, fft_array_E, 0, fft_E_in.length);

        fft_U.realForwardFull(fft_array_U);
        fft_N.realForwardFull(fft_array_N);
        fft_E.realForwardFull(fft_array_E);

        for (int c = 0;  c < columns_t*2; c++) {
            if (c % 2 == 0) {//real
                fft_array_U_real[c/2] = fft_array_U[c]/(columns_t*2);
                fft_array_N_real[c/2] = fft_array_N[c]/(columns_t*2);
                fft_array_E_real[c/2] = fft_array_E[c]/(columns_t*2);
            } else {//imag
                fft_array_U_imag[c/2] = fft_array_U[c]/(columns_t*2);
                fft_array_N_imag[c/2] = fft_array_N[c]/(columns_t*2);
                fft_array_E_imag[c/2] = fft_array_E[c]/(columns_t*2);
            }
        }
        //mix
        for (int c = 0; c < columns_t; c++) {
            fft_array_U_abs[c] = Math.sqrt(Math.pow(fft_array_U_real[c],2)+Math.pow(fft_array_U_imag[c],2));
            fft_array_N_abs[c] = Math.sqrt(Math.pow(fft_array_N_real[c],2)+Math.pow(fft_array_N_imag[c],2));
            fft_array_E_abs[c] = Math.sqrt(Math.pow(fft_array_E_real[c],2)+Math.pow(fft_array_E_imag[c],2));
        }*/
        
        /*
        for (int c = 0;  c < columns_t; c++) {
            System.out.println((c+1)+":"+fft_array_U_abs[c]+", "+fft_array_N_abs[c]+", "+fft_array_E_abs[c]);
        }*/
        
        
        //smoothing here
        //smoothing();
        
        boolean first_cal = true;
        /*
        while (loop) {
            if (first_cal) {
                for (int c = 0;  c < columns_t; c++) {
                    fft_array_U_nor[c] = fft_array_U_abs[c]/U_max;
                    fft_array_N_nor[c] = fft_array_N_abs[c]/N_max;
                    fft_array_E_nor[c] = fft_array_E_abs[c]/E_max;
                    //hertz[c] = (double)c * (1D/RecordLength.doubleValue());
                }

                for (int c = 0;  c < columns_t; c++) {
                    if (hertz[c] >= 20D) {
                        hertz_20 = new double[c];
                        fft_array_U_nor_20 = new double[c];
                        fft_array_N_nor_20 = new double[c];
                        fft_array_E_nor_20 = new double[c];
                        fft_array_U_abs_20 = new double[c];
                        fft_array_N_abs_20 = new double[c];
                        fft_array_E_abs_20 = new double[c];
                        hertz_20 = Arrays.copyOfRange(hertz, 0, c);
                        fft_array_U_nor_20 = Arrays.copyOfRange(fft_array_U_nor, 0, c);
                        fft_array_N_nor_20 = Arrays.copyOfRange(fft_array_N_nor, 0, c);
                        fft_array_E_nor_20 = Arrays.copyOfRange(fft_array_E_nor, 0, c);
                        fft_array_U_abs_20 = Arrays.copyOfRange(fft_array_U_abs, 0, c);
                        fft_array_N_abs_20 = Arrays.copyOfRange(fft_array_N_abs, 0, c);
                        fft_array_E_abs_20 = Arrays.copyOfRange(fft_array_E_abs, 0, c);
                        break;
                    }
                }
                first_cal = false;
            }
            //----------------------------------------------------------------------------
            double max_u_value = 0;
            double max_n_value = 0;
            double max_e_value = 0;
            U_max_pos = 0;
            N_max_pos = 0;
            E_max_pos = 0;
            for (int i = 0 ; i < fft_array_U_abs.length ; i++) {
                if (fft_array_U_abs[i] > max_u_value) {
                  max_u_value = fft_array_U_abs[i];
                  U_max_pos = i;
                }
                if (fft_array_N_abs[i] > max_n_value) {
                  max_n_value = fft_array_N_abs[i];
                  N_max_pos = i;
                }
                if (fft_array_E_abs[i] > max_e_value) {
                  max_e_value = fft_array_E_abs[i];
                  E_max_pos = i;
                }
            }
            
            
            
            //----------------------------------------------------------------------------
            double max_20_u_value = 0;
            double max_20_n_value = 0;
            double max_20_e_value = 0;
            U_max_20_pos = 0;
            N_max_20_pos = 0;
            E_max_20_pos = 0;
            for (int i = 0 ; i < fft_array_U_abs_20.length ; i++) {
                if (fft_array_U_abs_20[i] > max_20_u_value) {
                  max_20_u_value = fft_array_U_abs_20[i];
                  U_max_20_pos = i;
                }
                if (fft_array_N_abs_20[i] > max_20_n_value) {
                  max_20_n_value = fft_array_N_abs_20[i];
                  N_max_20_pos = i;
                }
                if (fft_array_E_abs_20[i] > max_20_e_value) {
                  max_20_e_value = fft_array_E_abs_20[i];
                  E_max_20_pos = i;
                }
            }
            
                        
            if (getMax_U_Hz().doubleValue() >= 0.01D && getMax_N_Hz().doubleValue() >= 0.01D && getMax_E_Hz().doubleValue() >= 0.01D &&
                getMax_U_Hz_20().doubleValue() >= 0.01D && getMax_N_Hz_20().doubleValue() >= 0.01D && getMax_E_Hz_20().doubleValue() >= 0.01D) {
                loop = false;
            } else {
                if (getMax_N_Hz().doubleValue() < 0.01D) {
                    //System.out.println("max N: "+getMax_N_Hz());
                    fft_array_N_abs[N_max_pos] = 0;
                }
                if (getMax_U_Hz().doubleValue() < 0.01D) {
                    //System.out.println("max U: "+getMax_U_Hz()+"("+file_name+")"+". Pos: "+U_max_pos);
                    
                    fft_array_U_abs[U_max_pos] = 0;
                }
                if (getMax_E_Hz().doubleValue() < 0.01D) {
                    //System.out.println("max E: "+getMax_E_Hz());
                    fft_array_E_abs[E_max_pos] = 0;
                }
                if (getMax_N_Hz_20().doubleValue() < 0.01D) {
                    fft_array_N_abs_20[N_max_20_pos] = 0;
                }
                if (getMax_U_Hz_20().doubleValue() < 0.01D) {
                    fft_array_U_abs_20[U_max_20_pos] = 0;
                }
                if (getMax_E_Hz_20().doubleValue() < 0.01D) {
                    fft_array_E_abs_20[E_max_20_pos] = 0;
                }
                if (loopcount <= 0) {
                    loop = false;
                }
            }
            loopcount--;
        }*/
    }
    
    public double[] getOriginData_U() {
        return fft_U_in;
    }
    
    public double[] getOriginData_N() {
        return fft_N_in;
    }
    
    public double[] getOriginData_E() {
        return fft_E_in;
    }
    
    public double[] getFFTData_U() {
        return fft_array_U;
    }
    
    public double[] getFFTData_N() {
        return fft_array_N;
    }
    
    public double[] getFFTData_E() {
        return fft_array_E;
    }
    
    public double[] getFFTData_U_abs() {
        return fft_array_U_abs;
    }
    
    public double[] getFFTData_N_abs() {
        return fft_array_N_abs;
    }
    
    public double[] getFFTData_E_abs() {
        return fft_array_E_abs;
    }
    
    public double[] getFFTData_U_nor() {
        return fft_array_U_nor;
    }
    
    public double[] getFFTData_N_nor() {
        return fft_array_N_nor;
    }
    
    public double[] getFFTData_E_nor() {
        return fft_array_E_nor;
    }
    
    public String getFilePath() {
        return this.file_path;
        //System.out.println(str);
    }
    
    public String getFileName() {
        return this.file_name;
        //System.out.println(str);
    }
    
    public void setFilePath(String str) {
        file_path = str;
        //System.out.println(str);
    }
    
    public void setFileName(String str) {
        file_name = str;
        //System.out.println(str);
    }
    
    public void draw_fft_chart_onebyone_20() throws IOException {
        //System.out.println("gen chart.");
        // Create Chart


        // Show it
        XYChart nor_all_chart = new XYChart(1920, 1080);
        nor_all_chart.setTitle("FFT nor");
        nor_all_chart.setXAxisTitle("Hz");
        nor_all_chart.setYAxisTitle("Val");
        XYSeries nor_all_series = nor_all_chart.addSeries("U",hertz_20, fft_array_U_nor_20);
        nor_all_series.setMarker(SeriesMarkers.NONE);
        nor_all_chart.addSeries("N",hertz_20, fft_array_N_nor_20);
        nor_all_series.setMarker(SeriesMarkers.NONE);
        nor_all_chart.addSeries("E",hertz_20, fft_array_E_nor_20);
        nor_all_series.setMarker(SeriesMarkers.NONE);
        //System.out.println(file_path+"/"+file_name+"_A.png");
        BitmapEncoder.saveBitmap(nor_all_chart, file_path+"/"+file_name+"_nor_A.png", BitmapFormat.PNG);

        XYChart abs_all_chart = new XYChart(1920, 1080);
        abs_all_chart.setTitle("FFT abs");
        abs_all_chart.setXAxisTitle("Hz");
        abs_all_chart.setYAxisTitle("Val");
        XYSeries abs_all_series = abs_all_chart.addSeries("U",hertz_20, fft_array_U_abs_20);
        abs_all_series.setMarker(SeriesMarkers.NONE);
        abs_all_chart.addSeries("N",hertz_20, fft_array_N_abs_20);
        abs_all_series.setMarker(SeriesMarkers.NONE);
        abs_all_chart.addSeries("E",hertz_20, fft_array_E_abs_20);
        abs_all_series.setMarker(SeriesMarkers.NONE);
        //System.out.println(file_path+"/"+file_name+"_A.png");
        BitmapEncoder.saveBitmap(abs_all_chart, file_path+"/"+file_name+"_abs_A.png", BitmapFormat.PNG);
        /*
        N = 18000;
        T = 1/200;
        k = 0:N-1;
        hertz = k * (1/(N*T));
        Ux = T027002_263(:, 2);
        UX = fft(Ux)/(N*2);
        UP = unwrap(angle(UX));
        plot (hertz(1:N),abs(UX)), xlabel ('Hz'),ylabel ('|AMP(k)|'), grid on*/
        //System.out.println(file_path+"/"+file_name+"_U.png");
        /*
        JFreeChart lineChartObject_U = ChartFactory.createLineChart(
           "FFT","Hz","Val",
           line_chart_dataset_U,PlotOrientation.VERTICAL,true,true,false);
        // Create an NumberAxis
        NumberAxis valueAxis = (NumberAxis) lineChartObject_U.getXYPlot().getRangeAxis().;
        valueAxis.setTickSelector(new NumberTickSelector(true));
        
        int width = line_chart_dataset_U.getColumnCount()*100;
        int height = 1440;
        File lineChart_U = new File(file_path+"/"+file_name+"_U.png" ); 
        ChartUtils.saveChartAsPNG(lineChart_U, lineChartObject_U, width ,height);
   
        JFreeChart lineChartObject_N = ChartFactory.createLineChart(
           "FFT","Hz",
           "Val",
           line_chart_dataset_N,PlotOrientation.VERTICAL,
           true,true,false);

        
        //lineChartObject_N.getXYPlot().getRangeAxis().setAutoRangeMinimumSize(100D);
        
        Number maximum_N = DatasetUtils.findMaximumRangeValue(line_chart_dataset_N);
        Number minimum_N = DatasetUtils.findMinimumRangeValue(line_chart_dataset_N);
        System.out.println("N_max:"+maximum_N);
        System.out.println("N_min:"+minimum_N);
        
        File lineChart_N = new File(file_path+"/"+file_name+"_N.png" ); 
        ChartUtils.saveChartAsPNG(lineChart_N ,lineChartObject_N, width ,height);
       
        JFreeChart lineChartObject_E = ChartFactory.createLineChart(
           "FFT","Hz",
           "Val",
           line_chart_dataset_E,PlotOrientation.VERTICAL,
           true,true,false);
        
        Number maximum_E = DatasetUtils.findMaximumRangeValue(line_chart_dataset_E);
        Number minimum_E = DatasetUtils.findMinimumRangeValue(line_chart_dataset_E);
        
        File lineChart_E = new File(file_path+"/"+file_name+"_E.png" ); 
        ChartUtils.saveChartAsPNG(lineChart_E ,lineChartObject_E, width ,height);
        */
        /*
        System.out.println("E_max:"+maximum_E);
        System.out.println("E_max index:"+max_e_pos);
        System.out.println("E_max check:"+fft_array_E_nor[max_e_pos]);
        System.out.println("E_min:"+minimum_E);*/
        
        //File lineChart_E = new File(file_path+"/"+file_name+"_E.png" );
        
    }
    
    /*
    //unit KM,M Harvesine formula
    public double calc_distance(double lon1, double lat1, double lon2, double lat2, double gl1, double gl2, String unit) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = Configs.R * c * 1000; // convert to meters
        double height = gl1 - gl2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        unit = unit.toUpperCase();
        if (unit.equals("KM")) {
            distance = Math.sqrt(distance)/1000D;
        } else {
            distance = Math.sqrt(distance);
        }
        return distance;
    }*/
    /*
    Boxcar
    Hanning
    Gaussian
    */
    private void Boxcar_Smoothing() {
        
    }
    
    public double[] Hanning_Smoothing(double[] input) {
        double[] output = new double[input.length];
        //5 times
        for (int st = 0;  st < 5; st++) {
            for (int c = 1;  c < input.length; c++) {
                output[c] = 0.25D*input[c-1] + 0.5D*input[c] + 0.25D*input[c+1];
            }
        }
        return output;
    }
    
    
    private void Hanning_Smoothing() {
        //5 times
        for (int st = 0;  st < 5; st++) {
            for (int c = 1;  c < columns_t; c++) {
                fft_array_U_abs[c] = 0.25D*fft_array_U_abs[c-1] + 0.5D*fft_array_U_abs[c] + 0.25D*fft_array_U_abs[c+1];
                fft_array_N_abs[c] = 0.25D*fft_array_N_abs[c-1] + 0.5D*fft_array_N_abs[c] + 0.25D*fft_array_N_abs[c+1];
                fft_array_E_abs[c] = 0.25D*fft_array_E_abs[c-1] + 0.5D*fft_array_E_abs[c] + 0.25D*fft_array_E_abs[c+1];
            }
        }
    }
    
    private void Gaussian_Smoothing() {
        
    }
    
    private void smoothing() {
        switch (Configs.smoothing_mode) {
            case 0:
                Boxcar_Smoothing();
                break;
            case 1:
                Hanning_Smoothing();
                break;
            case 2:
                Gaussian_Smoothing();
                break;
            default:
                Hanning_Smoothing();
                break;
        }
        
    }
}
