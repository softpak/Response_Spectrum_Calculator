/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softpak.gdms_fft;

import com.esri.arcgisruntime.geoanalysis.LocationViewshed;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author you
 */
public class FX_3D_Map_Window extends Application {
    
    private SceneView sceneView;
    Stage stage;
    
    Map<String, Map<String, FFT_SnapShot>> select_map;
    
    public FX_3D_Map_Window(Map<String, Map<String, FFT_SnapShot>> select_map) {
        this.select_map = select_map;
    }

    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            this.stage = stage;
            // create stack pane and JavaFX app scene
            StackPane stackPane = new StackPane();
            Scene Map_Scene_3D = new Scene(stackPane);
            stage.setWidth(800);
            stage.setHeight(600);
            
            // set title, size, and add JavaFX scene to stage
            stage.setScene(Map_Scene_3D);
            stage.show();

            // create a scene and add a basemap to it
            ArcGISScene scene = new ArcGISScene();
            scene.setBasemap(Basemap.createImagery());

            // add the SceneView to the stack pane
            sceneView = new SceneView();
            sceneView.setArcGISScene(scene);

            // set the camera
            Camera camera = new Camera(48.3808, -4.49492, 48.2511, 344.488, 74.1212, 0.0);
            sceneView.setViewpointCamera(camera);

            // add base surface for elevation data
            Surface surface = new Surface();
            
            // add a scene layer

            // create a viewshed from the camera
            LocationViewshed viewshed = new LocationViewshed(camera, 1.0, 500.0);
            viewshed.setFrustumOutlineVisible(true);

            // create an analysis overlay to add the viewshed to the scene view
            

            // create a button to update the viewshed with the current camera
            
            //cameraButton.setOnMouseClicked(e -> viewshed.updateFromCamera(sceneView.getCurrentViewpointCamera()));

            // add the sceneview and button to the stackpane
            stackPane.getChildren().addAll(sceneView);
            

        } catch (Exception e) {
            // on any error, display the stack trace.
            e.printStackTrace();
        }
    }
    
    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        if (sceneView != null) {
            sceneView.dispose();
        }
    }
}
