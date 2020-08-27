package softpak.gdms_fft;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import tools.Utils;

public class FX_FreeField_Controller implements Initializable {

    long st, et;
    //DecimalFormat df = new DecimalFormat("#,#0.0");

    @FXML private TableView FreeField_Table;
    @FXML private ContextMenu ff_cont_menu;
    @FXML private MenuItem ff_cont_menu_new;
    @FXML private TableColumn StationName_Col;
    @FXML private TableColumn Vs30_Col;
    @FXML private TableColumn GroundLevel_Col;
    @FXML private TableColumn Z10_Col;
    @FXML private TableColumn Kappa_Col;
    @FXML private TableColumn Longitude_Col;
    @FXML private TableColumn Latitude_Col;
    @FXML private TextArea logs;
    @FXML private TextArea sp_logs;
    @FXML private Label label_status;
    
    
    @FXML
    private void import_data_ButtonAction(ActionEvent event) throws IOException {
        FileChooser station_fileChooser = new FileChooser();
        station_fileChooser.setTitle("Station File");
        station_fileChooser.setInitialDirectory(new File(System.getProperty(".")));                 
        station_fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xls", "*.xls")
        );
        File dir = station_fileChooser.showOpenDialog(MainApp.stage);
        if (dir != null) {
            try {
                label_status.setText(dir.getCanonicalPath());
                Utils.set_ProjectPath(dir.getCanonicalPath());
            } catch (IOException ex) {
                Utils.logger.fatal(ex);
            }
        } else {
            label_status.setText("idle");
        }
        
    }
    
    @FXML
    private void ff_del_MenuAction(ActionEvent event) throws IOException {
        System.out.println(FreeField_Table.getSelectionModel().getSelectedIndex());
        /*Station station_data = (Station) FreeField_Table.getItems().get(wNroIdxRow);
        String wMyRowData = station_data.getStationName();*/
        if (FreeField_Table.getSelectionModel().getSelectedIndex() < 0) {
            System.out.println("add new row");
        }
        /*
        if (FreeField_Table.getItems().get().toString() == null) {//last Station is not null
            init_data.add(new Station("", "", "", "", ""));
        }*/
    }

    @FXML
    private void ok_ButtonAction(ActionEvent event) throws IOException {
        //save data into sqllite
        
    }
    
    @FXML
    private void cancel_ButtonAction(ActionEvent event) throws IOException {
        //save none
        
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FreeField_Table.setEditable(true);
        
        /*
        FreeField_Table.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t) -> {
            System.out.println(FreeField_Table.getSelectionModel().getSelectedIndex());
            if(t.getButton() == MouseButton.SECONDARY) {
                if (FreeField_Table.getSelectionModel().getSelectedIndex() <= 0) {
                    ff_cont_menu_new.setVisible(false);
                    ff_cont_menu.show(FreeField_Table, t.getScreenX(), t.getScreenY());
                } else {
                    ff_cont_menu_new.setVisible(true);
                    ff_cont_menu.show(FreeField_Table, t.getScreenX(), t.getScreenY());
                }
                
            }
        });*/
        
        
        StationName_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        Vs30_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        GroundLevel_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        Z10_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        Kappa_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        Longitude_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        Latitude_Col.setCellFactory(TextFieldTableCell.forTableColumn());
        
        
        StationName_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStationName(t.getNewValue());
                }
            }
        );
        Vs30_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setVs30(t.getNewValue());
                }
            }
        );
        GroundLevel_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setGround_Level(t.getNewValue());
                }
            }
        );
        Z10_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setGround_Level(t.getNewValue());
                }
            }
        );
        Kappa_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setGround_Level(t.getNewValue());
                }
            }
        );
        Longitude_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLongitude(t.getNewValue());
                }
            }
        );
        Latitude_Col.setOnEditCommit(new EventHandler<CellEditEvent<Station, String>>() {
                @Override
                public void handle(CellEditEvent<Station, String> t) {
                    ((Station) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLatitude(t.getNewValue());
                }
            }
        );
        
        FreeField_Table.setItems(Utils.get_StationList());
        
    }
}
