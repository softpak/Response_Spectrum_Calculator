/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.Serializable;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author you
 */
public class Project implements Serializable {
    private Queue<String> file_list_queue = Queues.newConcurrentLinkedQueue();   //with path
    private Map<String, FFT> data_group_map = Maps.newConcurrentMap();         //for check box
    private Map<String, Station> station_map = Maps.newConcurrentMap();         //for check box
    private String projetc_file_name;                                        //with path
    private String projetc_name = "Default";
    
    
    Project() {

    }
    
    public void set_ProjectPath(String str) {
        projetc_file_name = str;
    }
    
    
    public String get_ProjectPath() {
        return projetc_file_name;
    }
    
    public void set_ProjectName(String str) {
        projetc_name = str;
    }
    
    
    public String get_ProjectName() {
        return projetc_name;
    }
    
}
