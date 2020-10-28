package gui;

import entity.AutoRoute;
import entity.AutoStep;
import entity.MoveStrategy;
import io.FileLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.ParsePosition;


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
    private ComboBox boxStrategy;


    @FXML
    public void initialize() {
        try {
            boxStrategy.getItems().setAll(MoveStrategy.values());
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
        this.selectedStep.setWaitMS(Integer.valueOf(this.tfWait.getText()));
        this.selectedStep.setTopSpeed(Double.valueOf(this.tfSpeed.getText()));
        this.selectedStep.setMoveStrategy((MoveStrategy)this.boxStrategy.getSelectionModel().getSelectedItem());
        this.selectedStep.setTargetX(Integer.valueOf(this.tfX.getText()));
        this.selectedStep.setTargetY(Integer.valueOf(this.tfY.getText()));

        this.lstSteps.setItems(null);
        this.lstSteps.setItems(FXCollections.observableArrayList(this.selectedRoute.getSteps()));

    }

    public void setSelectedStep(AutoStep selectedStep) {
        this.selectedStep = selectedStep;
        this.tfSpeed.setText(String.format("%.2f", this.selectedStep.getTopSpeed()));
        this.tfWait.setText(Integer.toString(this.selectedStep.getWaitMS()));
        this.boxStrategy.getSelectionModel().select(selectedStep.getMoveStrategy());
        this.tfX.setText(Integer.toString(selectedStep.getTargetX()));
        this.tfY.setText(Integer.toString(selectedStep.getTargetY()));
    }

    public void setSelectedRoute(AutoRoute selectedRoute) {
        this.selectedRoute = selectedRoute;
    }

    public void setLstSteps(ListView lstSteps) {
        this.lstSteps = lstSteps;
    }
}
