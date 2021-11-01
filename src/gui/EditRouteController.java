package gui;

import entity.AutoRoute;
import entity.AutoStep;
import entity.MoveStrategy;
import io.FileLoader;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import logic.RouteController;

import java.text.DecimalFormat;
import java.text.ParsePosition;


public class EditRouteController {
    private RouteController routeController;
    private AutoRoute selectedRoute;
    boolean add = false;

    @FXML
    protected ComboBox boxName;

    @FXML
    protected TextField tfDescription;

    @FXML
    protected TextField tfIndex;

    @FXML
    protected TextField tfX;

    @FXML
    protected TextField tfY;

    @FXML
    protected TextField tfRotation;

    @FXML
    protected void onNameChange(ActionEvent event){
        String name = boxName.getSelectionModel().getSelectedItem().toString();
        int index = routeController.getMinAvailableIndex(name);
        tfIndex.setText(Integer.toString(index));
    }

    @FXML
    protected void btnSaveClicked(ActionEvent event){
        try {
            updateRoute();
            if (add) {
                this.routeController.addRoute(this.selectedRoute);
            }
            else{
                this.routeController.updateRoute(this.selectedRoute);
            }
            FileLoader.saveRoute(selectedRoute);
            closeStage(event);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    protected void btnCloseClicked(ActionEvent event){
        closeStage(event);
    }

    @FXML
    public void initialize() {
        try {
            boxName.getItems().add(AutoRoute.NAME_BLUE);
            boxName.getItems().add(AutoRoute.NAME_RED);
        }
        catch (Exception ex){

        }
    }

    public void setRouteController(RouteController routeController, AutoRoute route, boolean addRoute) {
        this.routeController = routeController;
        this.selectedRoute = route;
        add = addRoute;

        boxName.getSelectionModel().select(this.selectedRoute.getName());
        tfDescription.setText(this.selectedRoute.getDescription());
        tfIndex.setText(Integer.toString(this.selectedRoute.getNameIndex()));
        tfX.setText(Integer.toString(this.selectedRoute.getStartX()));
        tfY.setText(Integer.toString(this.selectedRoute.getStartY()));
        tfRotation.setText(Integer.toString(this.selectedRoute.getInitRotation()));
    }

    private void updateRoute(){
        this.selectedRoute.setName(this.boxName.getSelectionModel().getSelectedItem().toString());
        this.selectedRoute.setDescription(this.tfDescription.getText());
        this.selectedRoute.setNameIndex(Integer.valueOf(tfIndex.getText()));
        this.selectedRoute.setStartX(Integer.valueOf(tfX.getText()));
        this.selectedRoute.setStartY(Integer.valueOf(tfY.getText()));
        this.selectedRoute.setInitRotation(Integer.valueOf(tfRotation.getText()));
    }


    private void closeStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
