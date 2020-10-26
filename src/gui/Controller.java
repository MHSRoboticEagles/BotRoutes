package gui;

import entity.AutoDot;
import entity.AutoRoute;
import entity.AutoStep;
import entity.CoordinateChangeListener;
import io.FileLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    private FieldMap fieldMap;

    @FXML
    private Accordion leftNav;

    @FXML
    private Label lblName;

    @FXML
    private Label lblCoordinate;

    @FXML
    private Canvas mapFlow;

    @FXML
    public void initialize() {
        try {
            FileLoader.ensureAppDirectories();
            initFieldMap();
            initRouteList();
        }
        catch (Exception ex){
            showMessage(ex.getMessage());
        }
    }

    protected void initFieldMap(){
        fieldMap = new FieldMap(this.mapFlow);
        fieldMap.init();
        fieldMap.setCoordinateChangeListener(new CoordinateChangeListener() {
            @Override
            public void onCoordinateChanged(AutoDot dot, String description) {
                lblCoordinate.setText(description);
            }
        });
    }
    protected void initRouteList(){
        try {
            ArrayList<AutoRoute> routes = FileLoader.listRoutes();
            for(AutoRoute route : routes){
                TitledPane pane = new TitledPane(route.getRouteName() , new Label(route.getRouteName()));
                leftNav.getPanes().add(pane);
                pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                            if (newVal){
                                lblName.setText(route.getRouteName());
                                fieldMap.displaySelectedRoute(route);
                            }
                    }
                });

                ListView lstSteps = new ListView();
                lstSteps.setItems(FXCollections.observableArrayList(route.getSteps()));
                pane.setContent(lstSteps);
                lstSteps.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                        mouseEvent.getClickCount() == 2){
                            AutoStep selectedStep = (AutoStep)lstSteps.getSelectionModel().getSelectedItem();
                            if (selectedStep != null){
                                openStepEdit(selectedStep, route, lstSteps);
                            }
                        }
                        else if (mouseEvent.getButton() == MouseButton.PRIMARY){
                            AutoStep selectedStep = (AutoStep)lstSteps.getSelectionModel().getSelectedItem();
                            if (selectedStep != null){
                                fieldMap.animateSelectedStep(route, selectedStep);
                            }
                        }
                    }
                });

            }
            if (leftNav.getPanes().size() > 0) {
                leftNav.setExpandedPane(leftNav.getPanes().get(0));
                lblName.setText(routes.get(0).getRouteName());
                fieldMap.displaySelectedRoute(routes.get(0));
            }
        }
        catch (Exception ex){
            showMessage(ex.getMessage());
        }
    }

    protected void openStepEdit(AutoStep selectedStep, AutoRoute selectedRoute, ListView lstSteps){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-step.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StepController dialogController = fxmlLoader.<StepController>getController();
        dialogController.setSelectedStep(selectedStep);
        dialogController.setSelectedRoute(selectedRoute);
        dialogController.setLstSteps(lstSteps);

        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }


    @FXML
    protected void connect() {
        System.out.println("The button was clicked!");
    }

    protected void showMessage(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.show();
    }


}
