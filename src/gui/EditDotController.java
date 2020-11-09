package gui;

import entity.AutoDot;
import entity.AutoRoute;
import io.FileLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.RouteController;


public class EditDotController {
    private RouteController routeController;
    private AutoDot selectedDot;
    boolean add = false;

    @FXML
    protected TextField tfName;

    @FXML
    protected TextField tfHead;

    @FXML
    protected TextField tfX;

    @FXML
    protected TextField tfY;


    @FXML
    protected void btnSaveClicked(ActionEvent event){
        try {
            updateDot();
            if (add) {
                this.routeController.addDot(this.selectedDot);
            }
            FileLoader.saveDot(selectedDot);
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

    }

    public void setRouteController(RouteController routeController, AutoDot dot, boolean addDot) {
        this.routeController = routeController;
        this.selectedDot = dot;
        add = addDot;

        tfName.setText(this.selectedDot.getDotName());
        tfHead.setText(Double.toString(this.selectedDot.getHeading()));
        tfX.setText(Integer.toString(this.selectedDot.getX()));
        tfY.setText(Integer.toString(this.selectedDot.getY()));
    }

    private void updateDot(){
        this.selectedDot.setDotName(this.tfName.getText());
        this.selectedDot.setHeading(Double.valueOf(tfHead.getText()));
        this.selectedDot.setX(Integer.valueOf(tfX.getText()));
        this.selectedDot.setY(Integer.valueOf(tfY.getText()));
    }


    private void closeStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
