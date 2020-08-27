/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import org.gavaghan.geodesy.Ellipsoid;

/**
 *
 * @author you
 */
public class Configs {
    //6371.01D instead of 6378.1370D
    public static final double R = 6371.01D;// Radius of the earth WGS84
    public static final double rock_vs30 = 760D;// m/s
    public static int step_distance = 100;//meter
    public static int level_of_detail = 0;
    public static int station_distance_to_epiccenter_vector = 10*1000;//m
    public static int nearby_staiotn_distance = 2*1000;//1 km gets nothing... 2km radius is min
    public static int num_of_samples = 30;
    public static double vs30_max = 0;
    public static double vs30_min = 0;
    public static Ellipsoid reference = Ellipsoid.WGS84;
    
    public static int map_type_2d_seismic_station = 0;
    public static int map_type_2d_seismic = 1;
    public static int map_type_2d_station = 2;
    
    //load file task thread
    static final ForkJoinPool.ForkJoinWorkerThreadFactory ftcp = new ForkJoinPool.ForkJoinWorkerThreadFactory() {
        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            final ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setName("FFT_Pool-" + thread.getPoolIndex());
            return thread;
        }
    };
    public static ForkJoinPool Work_Pool;
    
    //save project temp session thread
    static final ForkJoinPool.ForkJoinWorkerThreadFactory ftpp = new ForkJoinPool.ForkJoinWorkerThreadFactory() {
        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            final ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setName("Project_Session_Pool-" + thread.getPoolIndex());
            return thread;
        }
    };
    public static ForkJoinPool Project_Session_Pool;
    
    static final ThreadFactory tfnp = new ThreadFactoryBuilder().setNameFormat("Work_Pool-%d").build();
    static public final ExecutorService dbpool_executor = Executors.newFixedThreadPool(1, tfnp);
    public static int log_textarea_max_lines = 100;
    public static Queue<Integer> log_textarea_line_pos = Queues.newConcurrentLinkedQueue();
    public static Queue<String[]> db_del_queue = Queues.newConcurrentLinkedQueue();
    public static DecimalFormat df = new DecimalFormat("#,##0.00");
    public static DecimalFormat data_df = new DecimalFormat("#,###0.000");
    public static DecimalFormat coord_df = new DecimalFormat("#,####0.0000");
    public static DecimalFormat time_df = new DecimalFormat("##00");
    public static boolean detrend_least_square_selected = false;
    public static boolean detrend_del_mean_selected = false;
    public static int global_lod = 0;
    public static int smoothing_mode = 1;//Hanning is default
    public static int smoothing_times = 5;//5 is default
    public static Map<String, Kriging> temp_kriging = Maps.newConcurrentMap();
    
    
    public static void init() {
        //Chunk_Pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()-2, ftcp, null, true);
        Work_Pool = new ForkJoinPool(Math.max((int)((double)Runtime.getRuntime().availableProcessors()*0.75D),2), ftcp, null, true);
        Project_Session_Pool = new ForkJoinPool(1, ftpp, null, true);
    }
    
}
