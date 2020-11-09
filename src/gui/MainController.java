package gui;

import entity.AutoDot;
import entity.AutoRoute;
import entity.AutoStep;
import entity.CoordinateChangeListener;
import io.BotConnector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.RouteController;
import logic.RoutesChangeListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainController {
    RouteController routeController = new RouteController();
    private FieldMap fieldMap;
    private AutoRoute currentRoute;
    boolean connected = false;

    @FXML
    private Button btnConnect;
    @FXML
    private Button btnSync;
    @FXML
    private Button btnPush;

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
            btnSync.setDisable(true);
            btnPush.setDisable(true);
            routeController.init();
            initFieldMap();
            initRouteList(0);
            routeController.setRoutesChangeListener(new RoutesChangeListener() {
                @Override
                public void onRoutesUpdated(int selectedIndex) {
                    initRouteList(selectedIndex);
                }

                @Override
                public void onStepAdded(AutoRoute route, AutoStep step) {
                    fieldMap.displaySelectedRoute(route);
                }
            });
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
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

            @Override
            public void onCoordinatePicked(AutoDot selected) {
                ListView listView = (ListView) leftNav.getExpandedPane().getContent();
                addStep(listView, selected, currentRoute.getSteps().size());
            }
        });
    }
    protected void initRouteList(int selectedIndex){
        try {
            if (leftNav.getPanes().size() > 0) {
                leftNav.getPanes().removeAll(leftNav.getPanes());
            }
            ArrayList<AutoRoute> routes = routeController.getRoutes();
            for(AutoRoute route : routes){
                TitledPane pane = new TitledPane(route.getRouteName() , new Label(route.getRouteName()));
                leftNav.getPanes().add(pane);
                pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                            if (newVal){
                                lblName.setText(route.getRouteName());
                                fieldMap.displaySelectedRoute(route);
                                currentRoute = route;
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
                                openStepEdit(selectedStep, route, lstSteps, -1);
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
                initRouteListMenu(lstSteps);

            }
            if (leftNav.getPanes().size() > 0) {
                leftNav.setExpandedPane(leftNav.getPanes().get(selectedIndex));
                lblName.setText(routes.get(selectedIndex).getRouteName());
                fieldMap.displaySelectedRoute(routes.get(selectedIndex));
                currentRoute = routes.get(selectedIndex);
            }
            addDotsPane();
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    protected void addDotsPane(){
        TitledPane pane = new TitledPane("Coordinates" , new Label("Coordinates"));
        leftNav.getPanes().add(pane);
        ListView lstDots = new ListView();
        lstDots.setItems(FXCollections.observableArrayList(this.routeController.getNamedDots()));
        pane.setContent(lstDots);
        lstDots.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                        mouseEvent.getClickCount() == 2){
                    AutoDot selectedDot = (AutoDot)lstDots.getSelectionModel().getSelectedItem();
                    if (selectedDot != null){
                        editDot(selectedDot);
                    }
                }
                else if (mouseEvent.getButton() == MouseButton.PRIMARY){
                    AutoDot selectedDot = (AutoDot)lstDots.getSelectionModel().getSelectedItem();
                    if (selectedDot != null){
                        fieldMap.drawSelectedDot(selectedDot);
                    }
                }
            }
        });
    }

    protected void initRouteListMenu(ListView lstSteps){
        ContextMenu routeListMenu = new ContextMenu();
        MenuItem menuDetail = new MenuItem("Edit");
        MenuItem menuAdd = new MenuItem("Add");
        MenuItem menuDelete = new MenuItem("Delete");

        MenuItem menuInsert = new MenuItem("Insert Before");

        menuInsert.setOnAction((event) ->{
            addStep(lstSteps, null, lstSteps.getSelectionModel().getSelectedIndex());
        });

        menuDetail.setOnAction((event) -> {
            if (lstSteps.getSelectionModel().getSelectedItem() != null) {
                AutoStep selectedStep = (AutoStep)lstSteps.getSelectionModel().getSelectedItem();
                openStepEdit(selectedStep, currentRoute, lstSteps, -1);
            }
        });

        menuAdd.setOnAction((event) -> {
            int selectedIndex = lstSteps.getSelectionModel().getSelectedIndex() + 1;
            if (selectedIndex == 0){
                selectedIndex = currentRoute.getSteps().size();
            }

            addStep(lstSteps, null, selectedIndex);
        });


        menuDelete.setOnAction((event) -> {
            if (lstSteps.getSelectionModel().getSelectedItem() != null) {
                AutoStep selectedStep = (AutoStep)lstSteps.getSelectionModel().getSelectedItem();
                deleteStep(selectedStep, lstSteps);

            }
        });
        routeListMenu.getItems().addAll(menuDetail, menuInsert, menuAdd, menuDelete);

        ContextMenu routeListMenuShort = new ContextMenu();
        MenuItem menuNew = new MenuItem("New");
        menuNew.setOnAction((event) -> {
            addStep(lstSteps, null, currentRoute.getSteps().size());
        });
        routeListMenuShort.getItems().addAll(menuNew);

        lstSteps.setOnContextMenuRequested(event -> {
            if (lstSteps.getSelectionModel().getSelectedItem() != null) {
                routeListMenu.show(lstSteps, event.getScreenX(), event.getScreenY());
            }
            else{
                routeListMenuShort.show(lstSteps, event.getScreenX(), event.getScreenY());
            }
        });
    }

    protected void addStep(ListView lstSteps, AutoDot selectedDot, int addIndex){
        openStepEdit(this.routeController.initStep(currentRoute, selectedDot, addIndex), currentRoute, lstSteps, addIndex);
    }

    protected void openStepEdit(AutoStep selectedStep, AutoRoute selectedRoute, ListView lstSteps, int addIndex){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-step.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StepController dialogController = fxmlLoader.<StepController>getController();
        dialogController.setRouteController(this.routeController);
        dialogController.setSelectedStep(selectedStep, addIndex);
        dialogController.setSelectedRoute(selectedRoute);
        dialogController.setLstSteps(lstSteps);

        Scene scene = new Scene(parent, 500, 400);
        Stage stage = new Stage();
        stage.initOwner(leftNav.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Step Info");
        stage.showAndWait();
    }


    @FXML
    protected void connect() {
        try {
            if (!connected) {
                BotConnector.runConnect();
                connected = true;
                btnConnect.setText("Disconnect");
                btnSync.setDisable(false);
                btnPush.setDisable(false);
                showMessage("Connected", Alert.AlertType.INFORMATION);
            }
            else{
                BotConnector.runDisconnect();
                connected = false;
                btnConnect.setText("Connect");
                btnSync.setDisable(true);
                btnPush.setDisable(true);
                showMessage("Disconnected", Alert.AlertType.INFORMATION);
            }
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void pullConfigs(){
        try{
            BotConnector.pullRoutes();
            BotConnector.pullDots();
            BotConnector.pullBotActions();
            BotConnector.pullBotConfig();
            initRouteList(0);
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void pushConfigs(){
        try{
            BotConnector.publishRoute(currentRoute);
            showMessage(String.format("Route %s pushed", currentRoute.getRouteName()), Alert.AlertType.INFORMATION);
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void addRoute() {
        addRoute(this.routeController.initRoute(), "New Route");
    }

    @FXML
    protected void cloneRoute(){
        AutoRoute clone = this.routeController.cloneRoute(currentRoute);
        addRoute(clone, "Clone Route");
    }

    private void addRoute(AutoRoute route, String title){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-route.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditRouteController editRouteController = fxmlLoader.<EditRouteController>getController();
        editRouteController.setRouteController(this.routeController, route, true);


        Scene scene = new Scene(parent, 400, 250);
        Stage stage = new Stage();
        stage.initOwner(leftNav.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.showAndWait();
    }



    @FXML
    protected void editRoute() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-route.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditRouteController editRouteController = fxmlLoader.<EditRouteController>getController();
        editRouteController.setRouteController(this.routeController, this.currentRoute, false);


        Scene scene = new Scene(parent, 400, 250);
        Stage stage = new Stage();
        stage.initOwner(leftNav.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("New Route");
        stage.showAndWait();
    }

    @FXML
    protected void deleteRoute(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + this.currentRoute.getRouteName() + " ?", ButtonType.YES, ButtonType.NO);
        if (leftNav != null && leftNav.getScene() != null) {
            alert.initOwner(leftNav.getScene().getWindow());
        }
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                this.routeController.deleteRoute(this.currentRoute);
            }
            catch (Exception ex){
                showMessage(ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    protected void deleteStep(AutoStep step, ListView lstSteps){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + step.toString() + " ?", ButtonType.YES, ButtonType.NO);
        if (leftNav != null && leftNav.getScene() != null) {
            alert.initOwner(leftNav.getScene().getWindow());
        }
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                this.routeController.deleteRouteStep(this.currentRoute, step);
                lstSteps.getItems().remove(step);
            }
            catch (Exception ex){
                showMessage(ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    protected void editDot(AutoDot dot) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-dot.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditDotController editDotController = fxmlLoader.<EditDotController>getController();
        editDotController.setRouteController(this.routeController, dot, false);


        Scene scene = new Scene(parent, 400, 250);
        Stage stage = new Stage();
        stage.initOwner(leftNav.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Edit dot");
        stage.showAndWait();
    }


    protected void showMessage(String msg, Alert.AlertType type){
        String title = "Info";
        if (type == Alert.AlertType.ERROR){
            title = "Error";
        }
        Alert alert = new Alert(type);
        if (leftNav != null && leftNav.getScene() != null) {
          alert.initOwner(leftNav.getScene().getWindow());
        }
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }


}
