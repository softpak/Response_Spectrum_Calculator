/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.time.Duration;
import java.time.Instant;

/**
 *
 * @author you
 */
public class TimeMeasure {
    private Instant unit_start;
    private Instant unit_end;
    public static long dur;
    public static int sec;
    public static int min;
    public static int hr;
    public static int day;
    
    
    public TimeMeasure() {
        
    }
    
    public void reset() {
        Utils.unit_measure_queue.clear();
    }
    
    public void begin_unit_measure() {
        unit_start = Instant.now();
    }
    
    public void stop_unit_measure() {
        unit_end = Instant.now();
    }
    
    public String get_escapetime(int units) {
        if (Utils.unit_measure_queue.size() > 100) {
            Utils.unit_measure_queue.poll();
            Utils.unit_measure_queue.add(Duration.between(unit_start, unit_end).toMillis());
        } else {
            Utils.unit_measure_queue.add(Duration.between(unit_start, unit_end).toMillis());
        }
        long avg_dur = Utils.unit_measure_queue.stream().mapToLong(Long::intValue).sum()/Utils.unit_measure_queue.size();
        dur = avg_dur*units;
        sec = (int)(dur / 1000L) % 60;
        min = (int)(dur / (60L*1000L)) % 60;
        hr = (int)(dur / (60L*60L*1000L) % 24);
        day = (int)(dur / (24L*60L*60L*1000L));
        String et = "";
        if (day > 0) {
            et += day+"  ";
        }
        if (hr >= 0) {
            et += Configs.time_df.format(hr)+":";
        }
        if (min >= 0) {
            et += Configs.time_df.format(min)+":";
        }
        if (sec >= 0) {
            et += Configs.time_df.format(sec)+".";
        }
        return et;
    }
    
    /*
    public static String update_escapetime_every_sec() {
        sec --;
        String et = "";
        if (day > 0) {
            et += day+"  ";
        }
        if (hr >= 0) {
            et += Configs.time_df.format(hr)+":";
        }
        if (min >= 0) {
            et += Configs.time_df.format(min)+":";
        }
        if (sec >= 0) {
            et += Configs.time_df.format(sec)+".";
        }
        return et;
    }*/
}
