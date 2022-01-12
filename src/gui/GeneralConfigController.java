package gui;

import entity.BotCalibConfig;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class GeneralConfigController {

    @FXML
    private TextField tfwheelBaseSeparation;

    @FXML
    private TextField tfleftTicksPerDegree;

    @FXML
    private TextField tfrightTicksPerDegree;

    @FXML
    private TextField tfminRadiusLeft;

    @FXML
    private TextField tfminRadiusRight;

    @FXML
    private TextField tfpositionPIDF;

    @FXML
    private TextField tfpositionToleration;



    public void init(BotCalibConfig config){
        tfwheelBaseSeparation.setText(String.format("%.2f", config.getWheelBaseSeparation()));

        tfleftTicksPerDegree.setText(String.format("%.2f", config.getLeftTicksPerDegree()));
        tfrightTicksPerDegree.setText(String.format("%.2f", config.getRightTicksPerDegree()));

        tfminRadiusLeft.setText(String.format("%.2f", config.getMinRadiusLeft()));
        tfminRadiusRight.setText(String.format("%.2f", config.getMinRadiusRight()));

        tfpositionPIDF.setText(String.format("%.2f", config.getPositionPIDF()));
        tfpositionToleration.setText(String.format("%d", config.getPositionToleration()));
    }

    public void save(BotCalibConfig config){
        config.setWheelBaseSeparation(Double.valueOf(tfwheelBaseSeparation.getText()));
        config.setLeftTicksPerDegree(Double.valueOf(tfleftTicksPerDegree.getText()));
        config.setRightTicksPerDegree(Double.valueOf(tfrightTicksPerDegree.getText()));
        config.setMinRadiusLeft(Double.valueOf(tfminRadiusLeft.getText()));
        config.setMinRadiusRight(Double.valueOf(tfminRadiusRight.getText()));

        config.setPositionPIDF(Double.valueOf(tfpositionPIDF.getText()));
        config.setPositionToleration(Integer.valueOf(tfpositionToleration.getText()));
    }
}
