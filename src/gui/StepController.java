package gui;

import entity.AutoRoute;
import entity.AutoStep;
import io.FileLoader;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class StepController {
    private AutoStep selectedStep;
    private AutoRoute selectedRoute;
    private ListView lstSteps;

    @FXML
    private TextField tfWait;

    @FXML
    private TextField tfSpeed;

    @FXML
    private TextField tfX;

    @FXML
    private TextField tfY;


    @FXML
    public void initialize() {
        try {

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
        this.selectedStep.setWaitMS(Integer.valueOf(this.tfWait.getText()));
        this.selectedStep.setTopSpeed(Double.valueOf(this.tfSpeed.getText()));
        this.lstSteps.setItems(null);
        this.lstSteps.setItems(FXCollections.observableArrayList(this.selectedRoute.getSteps()));
    }

    public void setSelectedStep(AutoStep selectedStep) {
        this.selectedStep = selectedStep;
        this.tfSpeed.setText(Double.toString(this.selectedStep.getTopSpeed()));
        this.tfWait.setText(Integer.toString(this.selectedStep.getWaitMS()));
    }

    public void setSelectedRoute(AutoRoute selectedRoute) {
        this.selectedRoute = selectedRoute;
    }

    public void setLstSteps(ListView lstSteps) {
        this.lstSteps = lstSteps;
    }
}
