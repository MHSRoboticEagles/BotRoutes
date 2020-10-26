package gui;

import entity.AutoRoute;
import entity.AutoStep;
import io.FileLoader;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    private static int MAP_SCALE = 4;
    ContextMenu canvasMenu = null;
    private double diam = 20;
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
            initRouteList();
            initCanvas();
        }
        catch (Exception ex){
            showMessage(ex.getMessage());
        }
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
                                displaySelectedRoute(route);
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
                    }
                });

            }
            if (leftNav.getPanes().size() > 0) {
                leftNav.setExpandedPane(leftNav.getPanes().get(0));
                lblName.setText(routes.get(0).getRouteName());
                displaySelectedRoute(routes.get(0));
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

    protected void initCanvas(){
        initCanvasMenu();

        double height = mapFlow.getHeight();
        mapFlow.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double pX = mouseEvent.getX();
                double pY = height - mouseEvent.getY();
                double x = pX/MAP_SCALE;
                double y = pY/MAP_SCALE;
                String output= String.format("Pixels: %.0f : %.0f. Inches: %.0f : %.0f", pX, pY, x, y);
                lblCoordinate.setText(output);
            }
        });

        mapFlow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });

    }

    protected void animateSelectedStep(GraphicsContext gc, int centerX, int centerY){
        DoubleProperty opacity  = new SimpleDoubleProperty();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(opacity, 1)
                ),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(opacity, 0)
                )
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.setFill(Color.FORESTGREEN.deriveColor(0, 1, 1, opacity.get()));
                gc.fillOval(centerX, centerY, diam, diam
                );
            }
        };
        timer.start();
        timeline.play();
    }

    protected void initCanvasMenu(){
        canvasMenu = new ContextMenu();
        MenuItem menuDetail = new MenuItem("Details");
        MenuItem menuInsertAfter = new MenuItem("Insert After");
        MenuItem menuInsertBefore = new MenuItem("Insert Before");
        MenuItem menuDelete = new MenuItem("Delete");

        menuDetail.setOnAction((event) -> {
            System.out.println("Show detail");
        });

        menuInsertAfter.setOnAction((event) -> {
            System.out.println(menuInsertAfter.getText());
        });

        menuInsertBefore.setOnAction((event) -> {
            System.out.println(menuInsertBefore.getText());
        });

        menuDelete.setOnAction((event) -> {
            System.out.println(menuDelete.getText());
        });
        canvasMenu.getItems().addAll(menuDetail,menuInsertAfter,menuInsertBefore, menuDelete);
        mapFlow.setOnContextMenuRequested(event -> {
            canvasMenu.show(mapFlow, event.getScreenX(), event.getScreenY());
        });
    }

    protected void displaySelectedRoute(AutoRoute selectedRoute){
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        if (selectedRoute != null){
            lblName.setText(selectedRoute.getRouteName());
            drawRoute(selectedRoute, gc);
        }
    }

    protected void drawRoute(AutoRoute selectedRoute, GraphicsContext gc){
        double height = mapFlow.getHeight();
        if (selectedRoute != null){
            gc.setStroke(Color.DARKGREEN.brighter());
            gc.setFill(Color.YELLOWGREEN);
            gc.setLineWidth(5);
            gc.beginPath();
            double startX = selectedRoute.getStartX() * MAP_SCALE;
            double startY = height - selectedRoute.getStartY() * MAP_SCALE;
            gc.moveTo(startX, startY);
            for(AutoStep step: selectedRoute.getSteps()){
                double x = step.getTargetX()*MAP_SCALE;
                double y = height - step.getTargetY()*MAP_SCALE;
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                gc.lineTo(x, y);
                gc.fillOval(x - diam/2, y - diam/2, diam, diam);
            }
            gc.stroke();
        }
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
