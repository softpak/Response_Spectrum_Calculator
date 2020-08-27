/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author you
 */
public class OP_Task {
    private int mission_type;
    private Object obj;
    private String str;
    
     public OP_Task(int mission_type) {
        this.mission_type = mission_type;
    }
    
    
    public OP_Task(int mission_type, Object obj) {
        this.mission_type = mission_type;
        this.obj = obj;
    }
    
    public OP_Task(int mission_type, Object obj, String str) {
        this.mission_type = mission_type;
        this.obj = obj;
        this.str = str;
    }
    
    //1 insert 2 select 3 update 4 delete
    //
    public int get_mission_type() {
        return  mission_type;   
    }
    
    public Object get_Mission_Object() {
        return obj;
    }
    
    public String get_String() {
        return str;
    }
}
