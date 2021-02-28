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


public class DotController {
    private RouteController routeController;
    private AutoDot selectedDot;
    private AutoRoute selectedRoute;
    private ListView lstSteps;
    private int addIndex = -1;

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
        }
        catch (Exception ex){

        }
    }

    @FXML
    protected void btnSaveClicked(ActionEvent event){
        try {
            updateStep();

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

    private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    protected void updateStep(){

        if (this.boxAction.getSelectionModel().getSelectedItem() != null) {
            String actionName = "";
            if (this.boxAction.getSelectionModel().getSelectedItem() instanceof BotActionObj) {
                BotActionObj selAction = (BotActionObj) this.boxAction.getSelectionModel().getSelectedItem();
                actionName = selAction.getMethodName();
            }
            else{
                actionName = this.boxAction.getSelectionModel().getSelectedItem().toString();
            }

        }



        this.lstSteps.setItems(null);
        this.lstSteps.setItems(FXCollections.observableArrayList(this.selectedRoute.getSteps()));
    }

    public void setSelectedStep(AutoStep selectedStep, int addIndex) {

        this.boxStrategy.getSelectionModel().select(selectedStep.getMoveStrategy());
        this.boxDirection.getSelectionModel().select(selectedStep.getRobotDirection());
        this.boxAction.getSelectionModel().select(selectedStep.getAction());
        this.boxTargets.getSelectionModel().select(selectedStep.getTargetReference());
        this.tfX.setText(Integer.toString(selectedStep.getTargetX()));
        this.tfY.setText(Integer.toString(selectedStep.getTargetY()));
        this.tfHead.setText(selectedStep.getDesiredHeadString());
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
        boxTargets.setItems(FXCollections.observableArrayList(routeController.getTargetReferences(this.selectedRoute)));
    }
}
