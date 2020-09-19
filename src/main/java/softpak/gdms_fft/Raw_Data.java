/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jtransforms.fft.DoubleFFT_1D;
import org.knowm.xchart.BitmapEncoder;
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
public class Raw_Data implements Serializable {
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
    private double Vs30;
    private double GL;
    private double Z10;
    private double K;
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
    private double station_Longitude;
    private double station_Latitude;
    private double seismic_Longitude;
    private double seismic_Latitude;
    
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
    public Raw_Data() {
        
    }
    
    
    public void setSSID(String str) {
        SSID = str;
    }
   
    public void calc_SSID() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hashbytes = digest.digest(new String(StationCode+InstrumentKind+Seismic_StartTime).getBytes(StandardCharsets.UTF_8));
        //SSID = bytesToHex(hashbytes);
        setSSID(bytesToHex(hashbytes));
        Utils.add_ssid_to_ssidqueue(SSID);
        //System.out.println(SSID);
    }
    
    private String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1) {
                hexString.append('0');
            }
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
    }
    
    public void setSeismic_StartTime(String str) {
        str = str.trim();
        Seismic_StartTime = str;
    }
    
    public void setSeismic_StartTime_from_path(String str) {
        //set from file_path
        SimpleDateFormat ph_format = new SimpleDateFormat(Utils.Path_date_format, Locale.ENGLISH);
        SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
        String[] date_temp = str.split(Pattern.quote(File.separator));
        for (int a = 0; a < date_temp.length; a++) {
            try {
                Seismic_StartTime = format.format(ph_format.parse(date_temp[a]));
            } catch (ParseException ex) {
                logger.debug(ex);
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
    
    public void setVs30(double vs30) {
        Vs30 = vs30;
    }
    
    public void setGL(double gl) {
        GL = gl;
    }
    
    public void setZ10(double z10) {
        Z10 = z10;
    }
    
    public void setK(double k) {
        K = k;
    }
    
    public void setStationLongitude(double longitude) {
        station_Longitude = longitude;
    }
    
    public void setStationLatitude(double latitude) {
        station_Latitude = latitude;
    }
    
    public void setSeismicLongitude(double longitude) {
        seismic_Longitude = longitude;
    }
    
    public void setSeismicLatitude(double latitude) {
        seismic_Latitude = latitude;
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
    
    public double getPGA_U() {
        return pga_u;
    }
    
    public double getPGA_N() {
        return pga_n;
    }
    
    public double getPGA_E() {
        return pga_e;
    }
    
    public double getVs30() {
        return Vs30;
    }
    
    public double getGL() {
        return GL;
    }
    
    public double getZ10() {
        return Z10;
    }
    
    public double getK() {
        return K;
    }
    
    public double getStationLongitude() {
        return station_Longitude;
    }
    
    public double getStationLatitude() {
        return station_Latitude;
    }
    
    public double getSeismicLongitude() {
        return seismic_Longitude;
    }
    
    public double getSeismicLatitude() {
        return seismic_Latitude;
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
    
    
    public void preprocessingData() {
        hertz = new double[columns_t];
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
    
    public void load_data_from_source(String filepath) {
        try {
            BufferedReader f_br = null;
            String f_sCurrentLine = null;
            int eff_count = 0;
            f_br = new BufferedReader(new FileReader(filepath));
            this.setSeismic_StartTime_from_path(filepath);
            this.setFilePath(Paths.get(filepath).getParent().toString());
            this.setFileName(Paths.get(filepath).getFileName().toString());
            while((f_sCurrentLine = f_br.readLine()) != null) {
                String temp_str = f_sCurrentLine;
                if (temp_str.startsWith("#")) {
                    if (temp_str.contains("Code")) {
                        eff_count++;
                        this.setStationCode(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                    }
                    if (temp_str.contains("Kind")) {
                        eff_count++;
                        this.setInstrumentKind(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                    }
                    if (temp_str.contains("StartTime")) {
                        eff_count++;
                        String mod = temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim();
                        mod = mod.replace("-", " ");
                        this.setAccelerometer_StartTime(mod);
                    }
                    if (temp_str.contains("sec")) {
                        eff_count++;
                        this.setRecordLength(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                    }
                    if (temp_str.contains("Hz")) {
                        eff_count++;
                        this.setSampleRate(temp_str.substring(temp_str.indexOf(":") + 1, temp_str.length()).trim());
                        this.initDataArray(this.getRecordLength().multiply(this.getSampleRate()).intValue(), 4);
                    }
                } else {//stage 2
                    this.setArrayO(temp_str);
                }
            }
            f_br.close();
            if (eff_count >= 5) {
                try {
                    this.transpose();
                    this.calc_SSID();
                    this.preprocessingData();
                    //this.transferDataToFFT();
                } catch (NoSuchAlgorithmException ex) {
                    Utils.logger.fatal(ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FFT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FFT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
