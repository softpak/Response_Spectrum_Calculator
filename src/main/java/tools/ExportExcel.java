/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import softpak.gdms_fft.FFT_SnapShot;

/**
 *
 * @author you
 */
public class ExportExcel {
    public ExportExcel() {
        
    }
    
    
    
    //
    /*
    public ExportExcel(String type, Map<Integer, Map<String, FFT_SnapShot>> snp_group_by_type) {
        snp_group_by_type.entrySet().parallelStream().forEach(vs30_group -> {
            XSSFWorkbook vs30_group_workbook = new XSSFWorkbook();
            int vs30_g = vs30_group.getKey();
            String sheet_name = (int)vs30_g+" <= Vs30 < "+  (int)(vs30_g+10);
            String file_name = (int)vs30_g+"_Vs30_"+  (int)(vs30_g+10);
            System.out.println("========== Vs30 Group: "+ sheet_name +" ==========");
            int rowCount = 0;
            XSSFSheet sheet = vs30_group_workbook.createSheet(sheet_name);
            //add head
            Row row_head = sheet.createRow(++rowCount);
            String[] cell_head = {"Date", "Seismic Vs30", "Station Vs30", "EpicenterDistance", "Depth", "Max U Hz 20", "Max N Hz 20", "Max E Hz 20", "Max Vs30", "Min Vs30"};
            for (int rc = 0; rc < cell_head.length; rc++) {
                Cell cell = row_head.createCell(rc);
                cell.setCellValue(cell_head[rc]);
            }
            for (Map.Entry<String, FFT_SnapShot> snp_temp : vs30_group.getValue().entrySet()) {
            //vs30_group.getValue().entrySet().stream().forEach(snp_temp -> {
                try {
                    FFT_SnapShot snp = snp_temp.getValue();
                    Date date_temp = snp.getStartTime();
                    Map<Date, Double> sc_vs30_g_map = vs30_group_map.get(vs30_g);
                    double sc_vs30_temp = sc_vs30_g_map.get(date_temp);
                    
                        //1.set step points as 100 points and calc these points and get vs30
                        //2.write a excel file with all data
                    
                    //calc passby step points max and min predict vs30
                    GeodeticCalculator geoCalc = new GeodeticCalculator();
                    double max_vs30 = -1;
                    double min_vs30 = -1;

                    max_vs30 = Math.max(sc_vs30_temp, snp.getVs30().doubleValue());
                    min_vs30 = Math.min(sc_vs30_temp, snp.getVs30().doubleValue());
                    GlobalCoordinates st_p = new GlobalCoordinates(snp.getSeismicLatitude().doubleValue(), snp.getSeismicLongitude().doubleValue());

                    while (true) {
                        //fast vincenty
                        GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, st_p, new GlobalCoordinates(snp.getStationLatitude().doubleValue(), snp.getStationLongitude().doubleValue()));
                        //double temp_distance = EarthCalc.vincentyDistance(st_p, end_p);
                        //double azimuth_temp = EarthCalc.vincentyBearing(st_p, end_p);
                        double temp_distance = geoCurve.getEllipsoidalDistance();
                        double azimuth_temp = geoCurve.getAzimuth();
                        if (temp_distance < Utils.get_step_distance()) {//add last
                            max_vs30 = Math.max(max_vs30, snp.getVs30().doubleValue());
                            min_vs30 = Math.min(min_vs30, snp.getVs30().doubleValue());
                            break;
                        } else {//dist is bigger then a step
                            GlobalCoordinates dest = geoCalc.calculateEndingGlobalCoordinates(Configs.reference, st_p, azimuth_temp, Utils.get_step_distance(), new double[1]);
                            double _vs30 = kr.predict(dest.getLongitude(), dest.getLatitude());
                            max_vs30 = Math.max(max_vs30, _vs30);
                            min_vs30 = Math.min(min_vs30, _vs30);
                            st_p = dest;
                        }
                    }


                    Row row = sheet.createRow(++rowCount);
                    String[] cell_string = {format.format(date_temp), Configs.df.format(sc_vs30_temp), Configs.df.format(snp.getVs30().doubleValue()),
                                            Configs.df.format(snp.getEpicenterDistance().doubleValue()), Configs.df.format(snp.getDepthOfFocus().doubleValue()), Configs.df.format(snp.getMax_U_Hz_20()),
                                            Configs.df.format(snp.getMax_N_Hz_20()), Configs.df.format(snp.getMax_E_Hz_20()), Configs.df.format(max_vs30), Configs.df.format(min_vs30)};
                    for (int rc = 0; rc < cell_string.length; rc++) {
                        Cell cell = row.createCell(rc);
                        cell.setCellValue(cell_string[rc]);
                    }
                    try (FileOutputStream outputStream = new FileOutputStream(file_name+".xlsx")) {
                        vs30_group_workbook.write(outputStream);
                    } catch (FileNotFoundException ex) {
                        Utils.logger.fatal(ex);
                    } catch (IOException ex) {
                        Utils.logger.fatal(ex);
                    }
                    //rowCount++;
                } catch (Exception ex) {
                    --rowCount;
                    Utils.logger.fatal(ex+": Error while calculating Geodetic Curve. "+ format.format(snp_temp.getValue().getStartTime())+" SSID: "+snp_temp.getValue().getSSID());
                }
            }
        });
        
        
    }
    */
}
