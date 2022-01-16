package gui;

import entity.BotCalibConfig;
import entity.FreightFrenzyConfig;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;

public class FrenzyAttachmentsController {

    //region Dropper Positions
    @FXML
    private TextField tfDropperPositionDrop;
    @FXML
    private TextField tfDropperPositionTransport;
    @FXML
    private TextField tfDropperPositionReset;
    //endregion

    //region Intake Positions
    @FXML
    private TextField tfIntakePositionUp;
    @FXML
    private TextField tfIntakePositionDown;
    @FXML
    private TextField tfIntakePositionSafety;
    //endregion

    //region Intake Speeds
    @FXML
    private TextField tfIntakeSpeedIn;
    @FXML
    private TextField tfIntakeSpeedOut;
    @FXML
    private TextField tfIntakeSpeedSlowIn;
    //endregion

    //region Lift Extension Positions
    @FXML
    private TextField tfLiftExtensionUnder;
    @FXML
    private TextField tfLiftExtensionNone;
    @FXML
    private TextField tfLiftExtensionMin;
    @FXML
    private TextField tfLiftExtensionSharedHub;
    @FXML
    private TextField tfLiftExtensionLevel1;
    @FXML
    private TextField tfLiftExtensionLevel2;
    @FXML
    private TextField tfLiftExtensionLevel3;
    //endregion

    //region Lift Aim Angles
    @FXML
    private TextField tfLiftAimAngleRed;
    @FXML
    private TextField tfLiftAimAngleCenter;
    @FXML
    private TextField tfLiftAimAngleBlue;
    //endregion

    //region Duck Loop Parameters
    @FXML
    private TextField tfStartSpeed;
    @FXML
    private TextField tfSpeedIncrement;
    @FXML
    private TextField tfMaximumSpeed;
    @FXML
    private TextField tfLoopDelayMs;
    @FXML
    private TextField tfLoopCount;
    @FXML
    private TextField tfFirstDuckSlowdownFactor;
    @FXML
    private TextField tfLoopTime;
    @FXML
    private LineChart<Double, Double> tfDuckLoopChart;
    //endregion

    public FrenzyAttachmentsController() {
    }

    public void init(BotCalibConfig config) {
        FreightFrenzyConfig frenzyConfig = config.getFreightFrenzyConfig();
        initDuckLoop(frenzyConfig);
        initLiftPositions(frenzyConfig);
        initLiftAngles(frenzyConfig);
        initDropperPositions(frenzyConfig);
        initIntakePositions(frenzyConfig);
        initIntakeSpeeds(frenzyConfig);
    }

    public void save(BotCalibConfig config) {
        FreightFrenzyConfig frenzyConfig = config.getFreightFrenzyConfig();
        saveDuckLoop(frenzyConfig);
        saveLiftPositions(frenzyConfig);
        saveLiftAngles(frenzyConfig);
        saveDropperPositions(frenzyConfig);
        saveIntakePositions(frenzyConfig);
        saveIntakeSpeeds(frenzyConfig);
    }

    public void refreshSpeedProfile() {
        calculateLoopTime();
        showSpeedChart();
    }

    private void initIntakeSpeeds(FreightFrenzyConfig config) {
        tfIntakeSpeedIn.setText(String.format("%.2f", config.getIntakeSpeedIn()));
        tfIntakeSpeedOut.setText(String.format("%.2f", config.getIntakeSpeedOut()));
        tfIntakeSpeedSlowIn.setText(String.format("%.2f", config.getIntakeSpeedSlowIn()));
    }

    private void initIntakePositions(FreightFrenzyConfig config) {
        tfIntakePositionUp.setText(String.format("%.2f", config.getIntakePositionUp()));
        tfIntakePositionDown.setText(String.format("%.2f", config.getIntakePositionDown()));
        tfIntakePositionSafety.setText(String.format("%.2f", config.getIntakePositionSafety()));
    }

    private void initDropperPositions(FreightFrenzyConfig config) {
        tfDropperPositionDrop.setText(String.format("%.2f", config.getDropperPositionDrop()));
        tfDropperPositionReset.setText(String.format("%.2f", config.getDropperPositionReset()));
        tfDropperPositionTransport.setText(String.format("%.2f", config.getDropperPositionTransport()));
    }

    private void initLiftAngles(FreightFrenzyConfig config) {
        tfLiftAimAngleCenter.setText(String.format("%.2f", config.getLiftAimAngleCenter()));
        tfLiftAimAngleRed.setText(String.format("%.2f", config.getLiftAimAngleRed()));
        tfLiftAimAngleBlue.setText(String.format("%.2f", config.getLiftAimAngleBlue()));
    }

    private void initLiftPositions(FreightFrenzyConfig config) {
        tfLiftExtensionUnder.setText(String.format("%.0f", config.getLiftExtensionUnder()));
        tfLiftExtensionNone.setText(String.format("%.0f", config.getLiftExtensionNone()));
        tfLiftExtensionMin.setText(String.format("%.0f", config.getLiftExtensionMin()));
        tfLiftExtensionSharedHub.setText(String.format("%.0f", config.getLiftExtensionSharedHub()));
        tfLiftExtensionLevel1.setText(String.format("%.0f", config.getLiftExtensionLevel1()));
        tfLiftExtensionLevel2.setText(String.format("%.0f", config.getLiftExtensionLevel2()));
        tfLiftExtensionLevel3.setText(String.format("%.0f", config.getLiftExtensionLevel3()));
    }

    private void initDuckLoop(FreightFrenzyConfig config) {
        tfLoopCount.setText(String.format("%d", config.getDuckLoopCount()));
        tfLoopDelayMs.setText(String.format("%d", config.getDuckLoopDelayMs()));
        tfFirstDuckSlowdownFactor.setText(String.format("%.3f", config.getDuckLoopFirstDuckSlowdownFactor()));
        tfStartSpeed.setText(String.format("%.3f", config.getDuckLoopStartSpeed()));
        tfSpeedIncrement.setText(String.format("%.3f", config.getDuckLoopSpeedIncrement()));
        tfMaximumSpeed.setText(String.format("%.3f", config.getDuckLoopMaximumSpeed()));

        refreshSpeedProfile();
    }

   private void saveIntakeSpeeds(FreightFrenzyConfig config) {
        config.setIntakeSpeedIn(Double.parseDouble(tfIntakeSpeedIn.getText()));
        config.setIntakeSpeedOut(Double.parseDouble(tfIntakeSpeedOut.getText()));
        config.setIntakeSpeedSlowIn(Double.parseDouble(tfIntakeSpeedSlowIn.getText()));
    }

    private void saveIntakePositions(FreightFrenzyConfig config) {
        config.setIntakePositionUp(Double.parseDouble(tfIntakePositionUp.getText()));
        config.setIntakePositionDown(Double.parseDouble(tfIntakePositionDown.getText()));
        config.setIntakePositionSafety(Double.parseDouble(tfIntakePositionSafety.getText()));
    }

    private void saveDropperPositions(FreightFrenzyConfig config) {
        config.setDropperPositionDrop(Double.parseDouble(tfDropperPositionDrop.getText()));
        config.setDropperPositionReset(Double.parseDouble(tfDropperPositionReset.getText()));
        config.setDropperPositionTransport(Double.parseDouble(tfDropperPositionTransport.getText()));
    }

    private void saveLiftAngles(FreightFrenzyConfig config) {
        config.setLiftAimAngleCenter(Double.parseDouble(tfLiftAimAngleCenter.getText()));
        config.setLiftAimAngleRed(Double.parseDouble(tfLiftAimAngleRed.getText()));
        config.setLiftAimAngleBlue(Double.parseDouble(tfLiftAimAngleBlue.getText()));
    }

    private void saveLiftPositions(FreightFrenzyConfig config) {
        config.setLiftExtensionUnder(Double.parseDouble(tfLiftExtensionUnder.getText()));
        config.setLiftExtensionNone(Double.parseDouble(tfLiftExtensionNone.getText()));
        config.setLiftExtensionMin(Double.parseDouble(tfLiftExtensionMin.getText()));
        config.setLiftExtensionSharedHub(Double.parseDouble(tfLiftExtensionSharedHub.getText()));
        config.setLiftExtensionLevel1(Double.parseDouble(tfLiftExtensionLevel1.getText()));
        config.setLiftExtensionLevel2(Double.parseDouble(tfLiftExtensionLevel2.getText()));
        config.setLiftExtensionLevel3(Double.parseDouble(tfLiftExtensionLevel3.getText()));
    }

    private void saveDuckLoop(FreightFrenzyConfig config) {
        config.setDuckLoopCount(Integer.parseInt(tfLoopCount.getText()));
        config.setDuckLoopDelayMs(Integer.parseInt(tfLoopDelayMs.getText()));

        config.setDuckLoopFirstDuckSlowdownFactor(Double.parseDouble(tfFirstDuckSlowdownFactor.getText()));
        config.setDuckLoopStartSpeed(Double.parseDouble(tfStartSpeed.getText()));
        config.setDuckLoopSpeedIncrement(Double.parseDouble(tfSpeedIncrement.getText()));
        config.setDuckLoopMaximumSpeed(Double.parseDouble(tfMaximumSpeed.getText()));
    }

    private void calculateLoopTime() {
        int loopDelayMs = Integer.parseInt(tfLoopDelayMs.getText());
        int loopCount = Integer.parseInt(tfLoopCount.getText());

        int loopTime = loopDelayMs * loopCount;
        tfLoopTime.setText(String.format("%d", loopTime));
    }

    private void showSpeedChart() {
        double startSpeed = Double.parseDouble(tfStartSpeed.getText());
        double speedIncrement = Double.parseDouble(tfSpeedIncrement.getText());
        double maxSpeed = Double.parseDouble(tfMaximumSpeed.getText());
        double loopDelayMs = Double.parseDouble(tfLoopDelayMs.getText());
        double loopCount = Double.parseDouble(tfLoopCount.getText());

        double currSpeed = startSpeed;
        double currTime = 0.0;

        XYChart.Series<Double, Double> speedProfile = new XYChart.Series<>();
        speedProfile.setName("speed profile");
        speedProfile.getData().add(new XYChart.Data<>(currTime / 1000, currSpeed));

        for (int i = 0; i < loopCount; i++) {
            currTime += loopDelayMs;
            currSpeed += speedIncrement;
            if (currSpeed > maxSpeed) {
                currSpeed = maxSpeed;
            }
            speedProfile.getData().add(new XYChart.Data<>(currTime / 1000, currSpeed));
        }

        tfDuckLoopChart.getData().clear();
        tfDuckLoopChart.getData().add(speedProfile);
    }
}
