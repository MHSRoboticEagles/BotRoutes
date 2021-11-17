package gui;

import entity.*;
import io.BotConnector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import logic.DotsChangeListener;
import logic.RouteController;
import logic.RoutesChangeListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainController {
    RouteController routeController = null;
    private FieldMap fieldMap;
    private AutoRoute currentRoute;
    boolean connected = false;
    boolean dotsMode = false;
    ListView lstDots = new ListView();
    private String conditionValue = "";

    @FXML
    private Button btnConnect;
    @FXML
    private Button btnSync;
    @FXML
    private Button btnPush;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnClone;
    @FXML
    private Button btnMirror;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    @FXML
    private Accordion leftNav;

    @FXML
    private Label lblName;

    @FXML
    private Label lblCoordinate;

    @FXML
    private Canvas mapFlow;

    @FXML
    private ToolBar barConditions;

    @FXML
    private ToggleButton btnA;
    @FXML
    private ToggleButton btnB;
    @FXML
    private ToggleButton btnC;

    @FXML
    public void initialize() {
        try {
            btnSync.setDisable(true);
            btnPush.setDisable(true);
            barConditions.setVisible(false);
            initController();
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    protected void initController(){
        try {
            routeController = new RouteController();
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
                    enableConditionBar(route, step);
                    fieldMap.displaySelectedRoute(route, conditionValue);
                }
                @Override
                public void onStepDeleted(AutoRoute route, AutoStep step) {
                    enableConditionBar(route, null);
                    fieldMap.displaySelectedRoute(route, conditionValue);
                }
            });
            routeController.setDotsChangeListener(new DotsChangeListener() {
                @Override
                public void onDotUpdated(AutoDot dot) {
                    lstDots.refresh();
                }
            });
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    protected void initFieldMap(){
        fieldMap = new FieldMap(this.mapFlow);
        fieldMap.init(18, 13);
        fieldMap.setCoordinateChangeListener(new CoordinateChangeListener() {
            @Override
            public void onCoordinateChanged(AutoDot dot, String description) {
                lblCoordinate.setText(description);
            }

            @Override
            public void onCoordinatePicked(AutoDot selected) {
                if (!dotsMode) {
                    ListView listView = (ListView) leftNav.getExpandedPane().getContent();
                    addStep(listView, selected, currentRoute.getVisibleSteps().size());
                }
                else{
                    AutoDot dot = routeController.initDot();
                    selected.setDotName(dot.getDotName());
                    addDot(selected);
                }
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
                TitledPane pane = new TitledPane(route.getRouteFullName() , new Label(route.getRouteFullName()));
                leftNav.getPanes().add(pane);
                pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                            if (newVal){
                                lblName.setText(route.getRouteFullName());
                                pane.setText(route.getRouteFullName());
                                currentRoute = route;
                                enableConditionBar(currentRoute, null);
                                fieldMap.displaySelectedRoute(currentRoute, conditionValue);
                            }
                    }
                });

                ListView lstSteps = new ListView();
                lstSteps.setItems(route.buildVisibleSteps(conditionValue));
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
                                fieldMap.animateSelectedStep(route, selectedStep, conditionValue);
                            }
                        }
                    }
                });
                initRouteListMenu(lstSteps);

            }
            addDotsPane();
            if (leftNav.getPanes().size() > 0) {
                TitledPane pane = leftNav.getPanes().get(selectedIndex);
                leftNav.setExpandedPane(pane);
                lblName.setText(routes.get(selectedIndex).getRouteFullName());
                pane.setText(routes.get(selectedIndex).getRouteFullName());
                currentRoute = routes.get(selectedIndex);
                enableConditionBar(currentRoute, null);
                fieldMap.displaySelectedRoute(currentRoute, conditionValue);

            }
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    protected void addDotsPane(){
        TitledPane pane = new TitledPane("Coordinates" , new Label("Coordinates"));
        leftNav.getPanes().add(pane);
        ObservableList<AutoDot> dots = this.routeController.getNamedDots();
        lstDots.setItems(dots);
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
        initDotListMenu(lstDots);
        if (dots.size() > 0){
            lblName.setText(dots.get(0).getDotName());
            fieldMap.drawSelectedDot(dots.get(0));
        }
        pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                dotsMode = newVal;
                if (newVal){
                    if (dots.size() > 0){
                        lblName.setText(dots.get(0).getDotName());
                        fieldMap.drawSelectedDot(dots.get(0));
                        lstDots.getSelectionModel().select(0);
                    }
                }
                btnAdd.setDisable(dotsMode);
                btnDelete.setDisable(dotsMode);
                btnEdit.setDisable(dotsMode);
                btnClone.setDisable(dotsMode);
                btnMirror.setDisable(dotsMode);
            }
        });
    }

    protected void initDotListMenu(ListView lstDots){
        ContextMenu dotListMenu = new ContextMenu();
        MenuItem menuDetail = new MenuItem("Edit");
        MenuItem menuAdd = new MenuItem("Add");
        MenuItem menuDelete = new MenuItem("Delete");

        menuDetail.setOnAction((event) -> {
            if (lstDots.getSelectionModel().getSelectedItem() != null) {
                AutoDot selectedDot = (AutoDot)lstDots.getSelectionModel().getSelectedItem();
                editDot(selectedDot);
            }
        });

        menuAdd.setOnAction((event) -> {
            AutoDot dot = this.routeController.initDot();
            addDot(dot);
        });


        menuDelete.setOnAction((event) -> {
            if (lstDots.getSelectionModel().getSelectedItem() != null) {
                AutoDot selectedDot = (AutoDot)lstDots.getSelectionModel().getSelectedItem();
                deleteDot(selectedDot);

            }
        });
        dotListMenu.getItems().addAll(menuDetail, menuAdd, menuDelete);

        lstDots.setOnContextMenuRequested(event -> {
            dotListMenu.show(lstDots, event.getScreenX(), event.getScreenY());
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
                selectedIndex = currentRoute.getVisibleSteps().size();
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
            addStep(lstSteps, null, currentRoute.getVisibleSteps().size());
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
        openStepEdit(this.routeController.initStep(currentRoute, selectedDot, addIndex, conditionValue), currentRoute, lstSteps, addIndex);
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
        dialogController.setSelectedRoute(selectedRoute);
        dialogController.setRouteController(this.routeController);
        dialogController.setSelectedStep(selectedStep, addIndex);
        dialogController.setLstSteps(lstSteps);
        dialogController.setCondition(conditionValue);

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
            StringBuilder sb = new StringBuilder();
            sb.append(BotConnector.pullRoutes());
            sb.append(BotConnector.pullDots());
            sb.append(BotConnector.pullBotActions());
            sb.append(BotConnector.pullBotConfig());
            initController();
            sb.append(BotConnector.pullLogs());
            sb.append(BotConnector.pullConfigs());
            showMessage(String.format("Downloaded robots config files. %s", sb.toString()), Alert.AlertType.INFORMATION);
        }
        catch (Exception ex){
            showMessage(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void pushConfigs(){
        try{
            if (dotsMode){
                if (lstDots.getSelectionModel().getSelectedItem() != null) {
                    AutoDot selectedDot = (AutoDot) lstDots.getSelectionModel().getSelectedItem();
                    BotConnector.publishDot(selectedDot);
                    showMessage(String.format("Coordinate %s pushed", selectedDot.getDotName()), Alert.AlertType.INFORMATION);
                }
            }
            else {
                BotConnector.publishRoute(currentRoute);
                showMessage(String.format("Route %s pushed", currentRoute.getRouteName()), Alert.AlertType.INFORMATION);
            }
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

    @FXML
    protected void mirrorRoute(){
        AutoRoute clone = this.routeController.mirrorRoute(currentRoute);
        addRoute(clone, "Mirror Route");
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

    protected void deleteDot(AutoDot selectedDot){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedDot.getDotName() + " ?", ButtonType.YES, ButtonType.NO);
        if (leftNav != null && leftNav.getScene() != null) {
            alert.initOwner(leftNav.getScene().getWindow());
        }
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                this.routeController.deleteDot(selectedDot);
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
//                lstSteps.getItems().remove(step);
            }
            catch (Exception ex){
                showMessage(ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    protected void addDot(AutoDot dot) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-dot.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditDotController editDotController = fxmlLoader.<EditDotController>getController();
        editDotController.setRouteController(this.routeController, dot, true);


        Scene scene = new Scene(parent, 400, 250);
        Stage stage = new Stage();
        stage.initOwner(leftNav.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("New coordinate");
        stage.showAndWait();
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
        stage.setTitle("Edit coordinate");
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

    @FXML
    protected void onSelectA() {
        btnB.setSelected(false);
        btnC.setSelected(false);
        highlightButton(btnA);
        btnB.setStyle(null);
        btnC.setStyle(null);
        conditionValue = "A";
        updateConditionalView();
    }

    @FXML
    protected void onSelectB() {
        btnA.setSelected(false);
        btnC.setSelected(false);
        highlightButton(btnB);
        btnC.setStyle(null);
        btnA.setStyle(null);
        conditionValue = "B";
        updateConditionalView();
    }

    @FXML
    protected void onSelectC() {
        btnB.setSelected(false);
        btnA.setSelected(false);
        highlightButton(btnC);
        btnB.setStyle(null);
        btnA.setStyle(null);
        conditionValue = "C";
        updateConditionalView();
    }

    protected void updateConditionalView(){
        currentRoute.updateMatchingSteps(conditionValue);
        routeController.reconcileRouteSteps(currentRoute, conditionValue);
        fieldMap.displaySelectedRoute(currentRoute, conditionValue);
    }

    protected void enableConditionBar(AutoRoute route, AutoStep step){
        conditionValue = "";
        boolean hasConditions = routeController.hasConditions(route);
        barConditions.setVisible(hasConditions);
        if (hasConditions) {
            if (step != null){
                switch (step.getConditionValue()){
                    case "A":
                        onSelectA();
                        break;
                    case "B":
                        onSelectB();
                        break;
                    case "C":
                        onSelectC();
                        break;
                }
            }
            else {
                onSelectA();
            }
        }

    }

    protected void highlightButton(ToggleButton btn){
        btn.setStyle(
                "-fx-background-color: green;" + "-fx-text-fill: white");
    }


}
