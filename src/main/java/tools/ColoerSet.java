/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author you
 */
public class ColoerSet {
    int setsize;
    List<Color> color_set = Lists.newArrayList();
    
    public ColoerSet() {
        
    }
    
    public ColoerSet(int setsize) {
        this.setsize = setsize;
    }
    
    private void gen_colorset_hsv() {
        
    }
    
    public List<Color> get_Color_Set() {
        return this.color_set;
    }
    
    public int get_ML_RGB_Color(float ml) {
        return Color.HSBtoRGB(ml/10F, 0.75F, 0.75F);
    }
    
    public int get_ML_RGB_Color(float ml, float max) {
        float cr = ml/max;
        if (cr > 1F) {
            cr = 1F;
        }
        
        return Color.HSBtoRGB(0, cr, 0.75F);
    }
    
}
