/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONObject;
import org.sqlite.jdbc4.JDBC4PreparedStatement;
import softpak.gdms_fft.FFT;
import softpak.gdms_fft.FFT_SnapShot;
import softpak.gdms_fft.FX_Main_Controller;
import softpak.gdms_fft.Seismic;
import softpak.gdms_fft.Station;

/**
 *
 * @author you
 */
public class OP_Engine implements Runnable{
    TimeMeasure tm = new TimeMeasure();
    private boolean running = true;
    private Queue<PreparedStatement> obj_to_write_pst = Queues.newConcurrentLinkedQueue();
    public static final short loop_tables = 1;
    public static final short insert_or_update_into_db = 2;
    public static final short gen_snapshot_from_db = 3;
    public static final short insert_seismic_data_into_db = 4;
    public static final short clear_fft_data_db = 5;
    public static final short auto_save_project = 6;
    public static final short load_from_temp_file = 7;
    public static final short kriging = 8;
    public static final short export_to_excel = 9;
    public static final short update_treeitem = 10;
    public static final short delete_from_db = 11;
    public static final short init_op_engine = 30000;
    private static FXMLLoader loader;
    FX_Main_Controller mainController;
    
    public OP_Engine(FXMLLoader loader) {
        this.loader = loader;
        this.mainController = loader.getController();
    }
    
    
    @Override
    public void run() {
        while (running) {
            try {
                //Utils.get_op_task_map().entrySet().forEach(opt_queue -> {
                while (!Utils.get_op_task_queue().isEmpty()) {
                    OP_Task opt_temp = Utils.get_op_task_queue().poll();
                    switch (opt_temp.get_mission_type()) {
                        case loop_tables:handle_Loop_Tables((PreparedStatement)opt_temp.get_Mission_Object());break;
                        case insert_or_update_into_db:handle_Insert_Or_Update_Into_DB((PreparedStatement)opt_temp.get_Mission_Object(), opt_temp.get_String());break;
                        case gen_snapshot_from_db: handle_Gen_SnapShot_From_DB((PreparedStatement)opt_temp.get_Mission_Object()); break;
                        case insert_seismic_data_into_db: handle_Insert_Seismic_Data_Into_DB(opt_temp.get_Mission_Object()); break;
                        case clear_fft_data_db: handle_Clear_FFT_Data_DB((PreparedStatement)opt_temp.get_Mission_Object(), opt_temp.get_String()); break;
                        case auto_save_project: handle_Auto_Save_Project(); break;
                        case load_from_temp_file: handle_Load_From_Temp_File(); break;
                        case kriging: handle_Kriging(opt_temp.get_Mission_Object()); break;
                        case export_to_excel: handle_Export_To_Excel(opt_temp.get_Mission_Object()); break;
                        case update_treeitem: handle_Update_Treeitem(opt_temp.get_Mission_Object()); break;
                        case delete_from_db: handle_Delete_From_DB((PreparedStatement)opt_temp.get_Mission_Object(), opt_temp.get_String());break;
                        //case CHANGE_SET: handleCHANGE_SET(in, out); break;
                        //case BUILD_SET: handleBUILD_SET(in, out); break;
                        //case PRE_INIT: handlePRE_INIT(in, out); break;
                        //case PLAYER_CHAT: handlePLAYER_CHAT(in, out); break;
                        //case TRAIN_UNIT: handleTRAIN_UNIT(in, out); break;
                        //case MANUFACTURE_UNIT: handleMANUFACTURE_UNIT(in, out); break;
                        //case UNIT_SET_PATH: handleUNIT_SET_PATH(in, out); break;
                        //case UPDATE_UNIT_DATA: handleUPDATE_UNIT_DATA(in, out); break;
                        //case UPDATE_RELATION: handleUPDATE_RELATION(in, out); break;
                        //case ADD_MASS: handleADD_MASS(in, out); break;
                        //case SET_PLAYER_POS: handleSET_PLAYER_POS(in, out); break;
                        //case HEART_BEAT: handleHEART_BEAT(in, out); break;
                        //case SWITCH_CONNECTION: handleSWITCH_CONNECTION(in, out); break;
                        //case CREATE_ACCOUNT: handleCREATE_ACCOUNT(in, out); break;
                        //case LOGIN: handleLOGIN(in, out); break;
                        //case RESET_PASSWORD: handleRESET_PASSWORD(in, out); break;
                        //case GEN_VCODE: handleGEN_VCODE(in, out); break;
                        default: break;
                    }
                } 
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
    
    
    private void handle_Clear_FFT_Data_DB(PreparedStatement obj, String str) {
        try {
            TimeMeasure tm = new TimeMeasure();
            tm.begin_unit_measure();
            Utils.table_rm_queue.poll();
            obj.executeUpdate();
            obj.close();
            tm.stop_unit_measure();
            Platform.runLater(() -> {
                Utils.check_logs_textarea();
                mainController.get_logs_textarea().appendText(str);
                mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
                mainController.get_label_status().setText(Utils.table_rm_queue.size() + " files in queue. Remaining time: "+ tm.get_escapetime(Utils.table_rm_queue.size()));
            });
            if (Utils.table_rm_queue.size() == 0) {
                Platform.runLater(() -> {
                    Utils.check_logs_textarea();
                    mainController.get_logs_textarea().appendText("No data to clear."+System.getProperty("line.separator"));
                    //label_status.setText(Utils.get_filequeue().size()+" files in queue.");
                    mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
                    mainController.get_label_status().setText("Done.");
                });
            }
            //Utils.DB_vacuum();
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        }
        
    }
    
    private void handle_Auto_Save_Project() {
        Platform.runLater(() -> {
            try {
                mainController.get_label_status().setText("Saving Temp file...");
                FileOutputStream fileOut = new FileOutputStream(Utils.get_MainPath()+"/temp.tmp");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                //dump data into project object
                out.writeObject(Utils.get_fft_snp_map());
                out.close();
                fileOut.close();
                mainController.get_label_status().setText("Temp file saved.");
            } catch (FileNotFoundException ex) {
                Utils.logger.fatal(ex);
                mainController.get_label_status().setText("Error occurs while saving Temp file.");
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
                mainController.get_label_status().setText("Error occurs while saving Temp file.");
            }
        });
    }
    
    private void handle_Insert_Or_Update_Into_DB(PreparedStatement obj, String str) {
        try {
            obj.executeUpdate();
            obj.close();
            /*
            Platform.runLater(() -> {
                Utils.check_logs_textarea();
                mainController.get_logs_textarea().appendText("("+Utils.get_filequeue().size()+"): "+str);
                mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
            });*/
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        }
    }
    
    private void handle_Loop_Tables(PreparedStatement obj) {
        
    }
    
    private void handle_Export_To_Excel(Object obj) {
        //create a folder name Export if not exist
        File export_dir = new File("Export");
        if (!export_dir.exists()) {
            export_dir.mkdir();
            mainController.get_logs_textarea().appendText("Export folder does not exist. Create one.");
            mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
        } else {
            mainController.get_logs_textarea().appendText("Export folder exist.");
            mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
        }
        
    }
    
    private void handle_Update_Treeitem(Object obj) {
        
    }
    
    private void handle_Delete_From_DB(PreparedStatement obj, String str) {
        try {
            obj.executeUpdate();
            obj.close();
            Platform.runLater(() -> {
                Utils.check_logs_textarea();
                mainController.get_logs_textarea().appendText("Delete from DB: "+str);
                mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
            });
        } catch (SQLException ex) {
            Utils.logger.fatal(ex);
        }
    }
    
    
    private void handle_Kriging(Object obj) {
        long st =System.currentTimeMillis();
        ((Kriging) obj).do_train();
        //System.out.println(jb_kr);
        long et =System.currentTimeMillis();
        long dur = et-st;
        int sec = (int)(dur / 1000L) % 60;
        int min = (int)(dur / (60L*1000L)) % 60;
        int hr = (int)(dur / (60L*60L*1000L) % 24);
        int day = (int)(dur / (24L*60L*60L*1000L));
        String tt = "";
        if (day > 0) {
            tt += day+"  ";
        }
        if (hr >= 0) {
            tt += Configs.time_df.format(hr)+":";
        }
        if (min >= 0) {
            tt += Configs.time_df.format(min)+":";
        }
        if (sec >= 0) {
            tt += Configs.time_df.format(sec)+".";
        }
        mainController.get_logs_textarea().appendText("Kriging Vs30 Done. ("+((Kriging) obj).get_station_num()+" Stations: "+tt+")"+System.getProperty("line.separator"));
        mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
        Configs.temp_kriging.put("kriging", (Kriging) obj);
        /*
        JSONObject json_temp = new JSONObject(((Kriging) obj).get_variogram().toString());
        json_temp.put("x", ((Kriging) obj).get_x_arr());
        json_temp.put("y", ((Kriging) obj).get_y_arr());*/
        //Files.write(Paths.get(Utils.get_MainPath()+"/krigin_vs30_"+System.currentTimeMillis()+".json"), json_temp.toString().getBytes());
        
        mainController.get_logs_textarea().appendText("Save Kriging result as json." +System.getProperty("line.separator"));
        mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());
    }
    
    
    
    private void handle_Gen_SnapShot_From_DB(PreparedStatement obj) {
        try {
            String st = null;
            StringBuilder sdata = new StringBuilder();
            ResultSet rs = obj.executeQuery();
            tm.begin_unit_measure();
            while (rs.next()) {
                //Map<String, FFT> temp_fftdatamap = Maps.newConcurrentMap();
                Map<String, FFT_SnapShot> temp_fftdatamap = Maps.newConcurrentMap();
                //FFT fft_temp = SerializationUtils.deserialize(rs.getBytes("Data"));
                FFT_SnapShot fft_temp = SerializationUtils.deserialize(rs.getBytes("Data"));
                st = fft_temp.getStationCode();
                temp_fftdatamap.put(rs.getString("SSID"), fft_temp);
                Map<String, FFT_SnapShot> temp_fftsnap_datamap = Utils.get_fft_snp_map().get(fft_temp.getStationCode());
                //fft_temp.setStartTime(fft_temp.getStartTime().replace("-", " "));
                //SimpleDateFormat format = new SimpleDateFormat(Utils.ISO8601_date_format);
                /*
                FFT_SnapShot fft_ss = new FFT_SnapShot(fft_temp.getSSID(), fft_temp.getStationCode(), fft_temp.getInstrumentKind(), format.parse(fft_temp.getSeismic_StartTime()),
                                                fft_temp.getMax_U_Hz(), fft_temp.getMax_N_Hz(), fft_temp.getMax_E_Hz(),
                                                fft_temp.getMax_U_Hz_20(), fft_temp.getMax_N_Hz_20(), fft_temp.getMax_E_Hz_20(),
                                                fft_temp.getPGA_U(), fft_temp.getPGA_N(), fft_temp.getPGA_E());
                */
                Station stt_temp = Utils.get_StationList().stream().filter(stt -> stt.getStationName().equals(fft_temp.getStationCode())).findAny().orElse(null);
                //check sub st name
                if (stt_temp == null) {
                    stt_temp = Utils.get_StationList().stream().filter(stt -> stt.getSubStationName().equals(fft_temp.getStationCode())).findAny().orElse(null);
                }        
                
                if (Utils.get_seismic_map().containsKey(fft_temp.getStartTime())) {
                    Seismic sc_temp = Utils.get_seismic_map().get(fft_temp.getStartTime());
                    fft_temp.setDepthOfFocus(sc_temp.getDepth_Of_Focus().doubleValue());
                    //fft_temp.setDepthOfFocus(sc_temp.getDepth_Of_Focus().doubleValue());
                    if (stt_temp != null) {
                        //change method
                        //Number ed_temp = fft_temp.calc_distance(sc_temp.getLongitude().doubleValue(), sc_temp.getLatitude().doubleValue(), Double.valueOf(stt_temp.getLongitude()), Double.valueOf(stt_temp.getLatitude()), 0D, 0D, "km");
                        Coordinate sc_lon_temp = Coordinate.fromDegrees(sc_temp.getLongitude().doubleValue());
                        Coordinate sc_lat_temp = Coordinate.fromDegrees(sc_temp.getLatitude().doubleValue());
                        com.grum.geocalc.Point sc_p = com.grum.geocalc.Point.at(sc_lat_temp, sc_lon_temp);//seismic
                        
                        Coordinate st_lon_temp = Coordinate.fromDegrees(Double.valueOf(stt_temp.getLongitude()));
                        Coordinate st_lat_temp = Coordinate.fromDegrees(Double.valueOf(stt_temp.getLatitude()));
                        com.grum.geocalc.Point st_p = com.grum.geocalc.Point.at(st_lat_temp, st_lon_temp);//seismic
                        
                        //Number ed_temp = fft_temp.vincentyDistance(sc_p, st_p);
                        Number ed_temp = EarthCalc.vincentyDistance(sc_p, st_p)/1000D;//km
                        fft_temp.setEpicenterDistance(ed_temp.doubleValue());
                        //fft_temp.setEpicenterDistance(ed_temp.doubleValue());
                    } else {
                        fft_temp.setEpicenterDistance(0D);
                        //fft_temp.setEpicenterDistance(0D);
                    }
                    fft_temp.setMagnitude(sc_temp.getMagnitude().doubleValue());
                    //fft_ss.setMagnitude(sc_temp.getMagnitude().doubleValue());
                    //System.out.println(sc_temp.getStartTime()+":"+sc_temp.getLongitude()+","+sc_temp.getLatitude());
                    fft_temp.setSeismicLongitude(sc_temp.getLongitude().doubleValue());
                    fft_temp.setSeismicLatitude(sc_temp.getLatitude().doubleValue());
                }
                /*
                if (stt_temp != null) {
                    fft_ss.setVs30(Double.valueOf(stt_temp.getVs30()));
                    fft_ss.setGL(Double.valueOf(stt_temp.getGround_Level()));
                    fft_ss.setZ10(Double.valueOf(stt_temp.getZ10()));
                    fft_ss.setK(Double.valueOf(stt_temp.getKappa()));
                    fft_ss.setStationLongitude(Double.valueOf(stt_temp.getLongitude()));
                    fft_ss.setStationLatitude(Double.valueOf(stt_temp.getLatitude()));
                }*/
                
                if (temp_fftsnap_datamap != null) {
                    //temp_fftsnap_datamap.put(fft_temp.getSSID(), fft_ss);
                    temp_fftsnap_datamap.put(fft_temp.getSSID(), fft_temp);
                } else {//null
                    Map<String, FFT_SnapShot> temp_emptyfftmap = Maps.newConcurrentMap();
                    //temp_emptyfftmap.put(fft_temp.getSSID(), fft_ss);
                    temp_emptyfftmap.put(fft_temp.getSSID(), fft_temp);
                    Utils.get_fft_snp_map().put(fft_temp.getStationCode(), temp_emptyfftmap);
                }
                //sdata.append(fft_temp.getStationCode()+","+ fft_temp.getInstrumentKind()+","+ fft_temp.getSeismic_StartTime()+"`s snapshot is done."+System.getProperty("line.separator"));
                sdata.append(fft_temp.getStationCode()+","+ fft_temp.getInstrumentKind()+","+ fft_temp.getStartTime()+"`s snapshot is done."+System.getProperty("line.separator"));
                
                //TreeItem<FFT_SnapShot> ss_temp = new TreeItem<>(fft_ss);
                TreeItem<FFT_SnapShot> ss_temp = new TreeItem<>(fft_temp);
                ss_temp.setExpanded(false);
                Platform.runLater(() -> {
                    for (TreeItem<FFT_SnapShot> child : mainController.get_table_root().getChildren()){
                        if (child.getValue().getStationCode().equals(fft_temp.getStationCode())) {
                            //child.getChildren().add(new TreeItem<>(fft_ss));
                            child.getChildren().add(new TreeItem<>(fft_temp));
                        }
                    }
                });
                //push to a mission
                
                
                
                //mainController.get_fft_treetable().setRoot(mainController.get_table_root());
                
                //mainController.get_ttc_station().setCellValueFactory(stt -> new ReadOnlyStringWrapper(stt.getValue().getValue().getStationCode()));
                
                //mainController.get_ttc_starttime().setCellValueFactory(ss -> new ReadOnlyStringWrapper(format.format(ss.getValue().getValue().getStartTime())));
                
            }
            obj.close();
            rs.close();
            
            tm.stop_unit_measure();
            Utils.removefrom_fft_station_name_set(st);
            Platform.runLater(() -> {
                Utils.check_logs_textarea();
                mainController.get_fft_treetable().setRoot(mainController.get_table_root());
                mainController.get_logs_textarea().appendText(sdata.toString());
                mainController.get_logs_textarea().selectPositionCaret(mainController.get_logs_textarea().getLength());

                mainController.get_label_status().setText(Utils.get_fft_station_name_set().size() + " tables to proceed. Remaining time: "+ tm.get_escapetime(Utils.get_fft_station_name_set().size()));
                if (Utils.get_fft_station_name_set().size() == 0) {
                    mainController.get_label_status().setText("Generate SnapShots from DB is done.");
                }
            });
            
        } catch (SQLException ex) {
            Utils.logger.fatal(ex+":"+obj.toString());
        }
    }
    
    private void handle_Insert_Seismic_Data_Into_DB(Object obj) {
        
        
    }
    
    private void handle_Load_From_Temp_File() {
        Platform.runLater(() -> mainController.load_tp());
    }
    
}
