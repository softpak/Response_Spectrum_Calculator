package softpak.gdms_fft;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tools.Configs;
import tools.OP_Engine;
import tools.Utils;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import tools.LinearRegressionModel;
import tools.OP_Task;
import tools.RegressionModel;

public class MainApp extends Application {

    public static Stage stage;
    public static Stage freefield_window = new Stage();
    public static Stage new_project_window = new Stage();
    public static Stage cancel_window = new Stage();
    public static FXMLLoader loader;
    Parent root;
    
    
    @Override
    public void start(Stage stage) throws Exception {
        /*
        double[] x = { 2, 3, 4, 5, 6, 8, 10, 11 };

        double[] y = { 21.05, 23.51, 24.23, 27.71, 30.86, 45.85, 52.12, 55.98 };

        System.out.println("Expected output from Excel: y = 9.4763 + 4.1939x");

        RegressionModel model = new LinearRegressionModel(x, y);
        model.fix_slope(1D);
        model.compute();  
        double[] coefficients = model.getCoefficients();
        double constant = coefficients[0];
        System.out.println(coefficients[0] + "," + coefficients[1]);
        
        model.fix_slope(0D);
        model.compute();  
        coefficients = model.getCoefficients();
        double point_y = coefficients[0];
        System.out.println(coefficients[0] + "," + coefficients[1]);
        System.out.println("T0: "+(point_y-constant));*/
        //35.16375 = 29.03875 + 1x;
        
        /*
        CategoryChart nor_all_chart = new CategoryChart(1920, 1080);
        nor_all_chart.getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Scatter);
        nor_all_chart.getStyler().setLegendPosition(LegendPosition.InsideN);
        nor_all_chart.getStyler().setOverlapped(true);
        nor_all_chart.getStyler().setMarkerSize(8);
        nor_all_chart.setTitle("FFT nor(N)");
        nor_all_chart.setXAxisTitle("Station");
        nor_all_chart.setYAxisTitle("Hz");
        List<String> fft_Staion_queue = Lists.newArrayList();
        List<Number> fft_N_queue = Lists.newArrayList();
        
        fft_Staion_queue.add("st01");
        fft_N_queue.add(1);
        fft_Staion_queue.add("st01");
        fft_N_queue.add(2);
        fft_Staion_queue.add("st01");
        fft_N_queue.add(3);
        fft_Staion_queue.add("st02");
        fft_N_queue.add(1);
        fft_Staion_queue.add("st02");
        fft_N_queue.add(2);
        fft_Staion_queue.add("st02");
        fft_N_queue.add(3);
            
        
        CategorySeries nor_all_series = nor_all_chart.addSeries("Station", fft_Staion_queue, fft_N_queue);
        
       
        //System.out.println(file_path+"/"+file_name+"_A.png");
        BitmapEncoder.saveBitmap(nor_all_chart, "I:/Desktop/nor_N.png", BitmapEncoder.BitmapFormat.PNG);
        System.exit(0);*/
        
        
        Utils.set_MainPath(System.getProperty("user.dir"));
        loader = new FXMLLoader(getClass().getResource("/fxml/Main_bp.fxml"));
        root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        //stage.setTitle("FFT tool for seismic by Department of Architecture NCKU - "+Utils.get_ProjectPath());
        if (Utils.get_ProjectPath() == null) {
            stage.setTitle("FFT tool for seismic by Department of Architecture NCKU - Empty Project");
        } else {
            stage.setTitle("FFT tool for seismic by Department of Architecture NCKU - "+Utils.get_ProjectPath());
        }
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.setX(Utils.ScreenBounds.getHeight()*0.05D);
        //stage.setHeight(Utils.ScreenBounds.getHeight()*0.8D);
        //stage.setMinHeight(350D);
        //stage.setMinWidth(510D);
        stage.show();

        stage.setResizable(false);
        this.stage = stage;
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        
        Parent freefield = FXMLLoader.load(getClass().getResource("/fxml/FreeField.fxml"));
        Parent new_project = FXMLLoader.load(getClass().getResource("/fxml/NewProject.fxml"));
        Parent cancel = FXMLLoader.load(getClass().getResource("/fxml/Cancel.fxml"));
        
        
        Scene freefield_scene = new Scene(freefield);
        Scene new_project_scene = new Scene(new_project);
        Scene cancel_scene = new Scene(cancel);
        freefield_scene.getStylesheets().add("/styles/Styles.css");
        new_project_scene.getStylesheets().add("/styles/Styles.css");
        cancel_scene.getStylesheets().add("/styles/Styles.css");
        freefield_window.setTitle("Free-Field Station Data Editor");
        freefield_window.setScene(freefield_scene);
        freefield_window.initOwner(stage);
        
        new_project_window.setTitle("Set Up New Project");
        new_project_window.setScene(new_project_scene);
        new_project_window.initOwner(stage);
        
        cancel_window.setTitle("Cancel Task");
        cancel_window.setScene(cancel_scene);
        cancel_window.initOwner(stage);
        
        
        Configs.dbpool_executor.execute(new OP_Engine(loader));
        Utils.init();
        Utils.add_to_op_task_queue(new OP_Task(OP_Engine.load_from_temp_file));
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, IOException {
        
        
        Configs.init();
        launch(args);
        
        //1 data need 1.728s/per core
    }

    
}
