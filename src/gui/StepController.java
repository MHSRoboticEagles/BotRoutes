package gui;

import entity.*;
import io.FileLoader;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.RouteController;

import java.text.DecimalFormat;
import java.text.ParsePosition;


public class StepController {
    private RouteController routeController;
    private AutoStep selectedStep;
    private AutoRoute selectedRoute;
    private String condition;
    private ListView lstSteps;
    private int addIndex = -1;

    @FXML
    private ComboBox boxConditionAction;

    @FXML
    private TextField tfConditionValue;

    @FXML
    private TextField tfWait;

    @FXML
    private TextField tfSpeed;

    @FXML
    private TextField tfX;

    @FXML
    private TextField tfY;

    @FXML
    private ComboBox boxStrategy;

    @FXML
    private ComboBox boxDirection;

    @FXML
    private ComboBox boxAction;

    @FXML
    private ComboBox boxTargets;

    @FXML
    private TextField tfHead;

    @FXML
    private CheckBox boxContinuous;


    @FXML
    public void initialize() {
        try {
            boxStrategy.getItems().setAll(MoveStrategy.values());
            boxDirection.getItems().setAll(RobotDirection.values());
            DecimalFormat format = new DecimalFormat( "#.0" );
            tfWait.setTextFormatter(new TextFormatter<Object>(c -> {
                if ( c.getControlNewText().isEmpty() )
                {
                    return c;
                }

                ParsePosition parsePosition = new ParsePosition( 0 );
                Object object = format.parse( c.getControlNewText(), parsePosition );

                if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
                {
                    return null;
                }
                else
                {
                    return c;
                }
            }));
            boxTargets.setOnAction( (event) -> {
                if (this.boxTargets.getSelectionModel().getSelectedItem() != null){
                    if (this.boxTargets.getSelectionModel().getSelectedItem() instanceof TargetReference){
                        TargetReference tr = (TargetReference)this.boxTargets.getSelectionModel().getSelectedItem();
                        if (tr != null){
                            this.tfX.setText(Integer.toString(tr.getX()));
                            this.tfY.setText(Integer.toString(tr.getY()));
                        }
                    }
                }
            });
        }
        catch (Exception ex){

        }
    }

    @FXML
    protected void btnSaveClicked(ActionEvent event){
        try {
            updateStep();
            if (addIndex >= 0){
                this.routeController.addStep(selectedRoute, selectedStep, addIndex, condition);
            }
            FileLoader.saveRoute(selectedRoute);
            this.routeController.notifyStepUpdate(selectedRoute, selectedStep);
            this.lstSteps.refresh();
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

    private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    protected void updateStep(){
        this.selectedStep.setWaitMS(Integer.valueOf(this.tfWait.getText()));
        this.selectedStep.setTopSpeed(Double.valueOf(this.tfSpeed.getText()));
        this.selectedStep.setMoveStrategy((MoveStrategy)this.boxStrategy.getSelectionModel().getSelectedItem());
        this.selectedStep.setRobotDirection((RobotDirection) this.boxDirection.getSelectionModel().getSelectedItem());
        this.selectedStep.setTargetX(Integer.valueOf(this.tfX.getText()));
        this.selectedStep.setTargetY(Integer.valueOf(this.tfY.getText()));
        if (this.boxAction.getSelectionModel().getSelectedItem() != null) {
            String actionName = "";
            if (this.boxAction.getSelectionModel().getSelectedItem() instanceof BotActionObj) {
                BotActionObj selAction = (BotActionObj) this.boxAction.getSelectionModel().getSelectedItem();
                actionName = selAction.getMethodName();
            }
            else{
                actionName = this.boxAction.getSelectionModel().getSelectedItem().toString();
            }
            this.selectedStep.setAction(actionName);
        }

        this.selectedStep.setConditionValue(this.tfConditionValue.getText());
        if (this.boxConditionAction.getSelectionModel().getSelectedItem() != null) {
            String actionName = "";
            if (this.boxConditionAction.getSelectionModel().getSelectedItem() instanceof BotActionObj) {
                BotActionObj selAction = (BotActionObj) this.boxConditionAction.getSelectionModel().getSelectedItem();
                actionName = selAction.getMethodName();
            }
            else{
                actionName = this.boxConditionAction.getSelectionModel().getSelectedItem().toString();
            }
            this.selectedStep.setConditionFunction(actionName);
        }

        if (this.boxTargets.getSelectionModel().getSelectedItem() != null){
            if (this.boxTargets.getSelectionModel().getSelectedItem() instanceof TargetReference){
                TargetReference tr = (TargetReference)this.boxTargets.getSelectionModel().getSelectedItem();
                this.selectedStep.setTargetReference(tr.getDotName());
            }
        }
        else{
            this.selectedStep.setTargetReference("");
        }

        this.selectedStep.setDesiredHead(Double.valueOf(this.tfHead.getText()));
        this.selectedStep.setContinuous(this.boxContinuous.isSelected());
    }

    public void setSelectedStep(AutoStep selectedStep, int addIndex) {
        this.selectedStep = selectedStep;
        this.addIndex = addIndex;
        this.tfSpeed.setText(String.format("%.2f", this.selectedStep.getTopSpeed()));
        this.tfWait.setText(Integer.toString(this.selectedStep.getWaitMS()));
        this.boxStrategy.getSelectionModel().select(selectedStep.getMoveStrategy());
        this.boxDirection.getSelectionModel().select(selectedStep.getRobotDirection());
        this.boxAction.getSelectionModel().select(selectedStep.getAction());
        this.boxTargets.getSelectionModel().select(selectedStep.getTargetReference());
        this.tfX.setText(Integer.toString(selectedStep.getTargetX()));
        this.tfY.setText(Integer.toString(selectedStep.getTargetY()));
        this.tfHead.setText(selectedStep.getDesiredHeadString());
        this.tfConditionValue.setText(selectedStep.getConditionValue());
        this.boxConditionAction.getSelectionModel().select(selectedStep.getConditionFunction());
        this.boxContinuous.setSelected(selectedStep.isContinuous());
    }

    public void setSelectedRoute(AutoRoute selectedRoute) {
        this.selectedRoute = selectedRoute;
    }

    public void setLstSteps(ListView lstSteps) {
        this.lstSteps = lstSteps;
    }

    public void setRouteController(RouteController routeController) {
        this.routeController = routeController;
        boxAction.setItems(FXCollections.observableArrayList(routeController.getBotActions()));
        boxConditionAction.setItems(FXCollections.observableArrayList(routeController.getBotActions()));
        boxTargets.setItems(FXCollections.observableArrayList(routeController.getTargetReferences(this.selectedRoute)));
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
