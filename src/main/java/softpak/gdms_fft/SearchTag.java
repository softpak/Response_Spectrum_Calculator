/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 *
 * @author you
 */
public class SearchTag {
    String tag_name;
    String condition_str;
    String logic_symbol;
    Number condition_num;
    int tag_order;//start from 0
    Button cancel;
    Button tag;
    
    public SearchTag(String tag_name, String logic_symbol, String condition_str, int tag_order) {
        
        init();
    }
    
    public SearchTag(String tag_name, String logic_symbol, Number condition_num, int tag_order) {
        
        init();
    }
    
    private void init() {
        tag.setOnAction((ActionEvent event) -> {
            
        });
        
        cancel.setOnAction((ActionEvent event) -> {
            
        });
    
    }
    
}
