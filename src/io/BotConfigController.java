package io;

import entity.*;
import gui.GeneralConfigController;
import gui.MotorReductionController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.DotsChangeListener;
import logic.RoutesChangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class BotConfigController {
    BotCalibConfig botCalibConfig = null;

    @FXML private GeneralConfigController tabPageGeneralController;

    @FXML private MotorReductionController tabPageMRController;

    @FXML private MotorReductionController tabPageMRBackController;

    @FXML private MotorReductionController tabPageMRStrafeLeftController;

    @FXML private MotorReductionController tabPageMRStrafeRightController;

    @FXML private MotorReductionController tabPageMRDiagLeftController;

    @FXML private MotorReductionController tabPageMRDiagRightController;

    @FXML private MotorReductionController tabPageMRSpinLeftController;

    @FXML private MotorReductionController tabPageMRSpinRightController;


    public BotConfigController(){
    }

    public BotCalibConfig loadBotConfig(){
        try
        {
            botCalibConfig = FileLoader.loadBotConfig();
        }
        catch (Exception ex){

        }
        return botCalibConfig;
    }

    public void saveBotConfig(BotCalibConfig config){
        try
        {
            botCalibConfig = config;
            FileLoader.saveBotConfig(config);
        }
        catch (Exception ex){

        }
    }

    public void publishBotConfig(BotCalibConfig config){
        try
        {
            BotConnector.publishBotConfig(config);
        }
        catch (Exception ex){

        }
    }


    public void init() {
        loadBotConfig();

        //general tab
        if(botCalibConfig != null && tabPageGeneralController != null){
            tabPageGeneralController.init(botCalibConfig);
        }

        //MR tab
        if(botCalibConfig != null && tabPageMRController != null){
            tabPageMRController.init(botCalibConfig, MotorReductionType.MRForward);
        }

        //MR tab back
        if(botCalibConfig != null && tabPageMRBackController != null){
            tabPageMRBackController.init(botCalibConfig, MotorReductionType.MRBack);
        }

        //MR strafe left
        if (botCalibConfig != null && tabPageMRStrafeLeftController != null){
            tabPageMRStrafeLeftController.init(botCalibConfig, MotorReductionType.StrafeLeftReduction);
        }

        //MR strafe right
        if (botCalibConfig != null && tabPageMRStrafeRightController != null){
            tabPageMRStrafeRightController.init(botCalibConfig, MotorReductionType.StrafeRightReduction);
        }

        //MR Diag left
        if (botCalibConfig != null && tabPageMRDiagLeftController != null){
            tabPageMRDiagLeftController.init(botCalibConfig, MotorReductionType.DiagMRLeft);
        }

        //MR Diag right
        if (botCalibConfig != null && tabPageMRDiagRightController != null){
            tabPageMRDiagRightController.init(botCalibConfig, MotorReductionType.DiagMRRight);
        }

        //MR Spin left
        if (botCalibConfig != null && tabPageMRSpinLeftController != null){
            tabPageMRSpinLeftController.init(botCalibConfig, MotorReductionType.SpinLeftConfig);
        }

        //MR Spin right
        if (botCalibConfig != null && tabPageMRSpinRightController != null){
            tabPageMRSpinRightController.init(botCalibConfig, MotorReductionType.SpinRightConfig);
        }
    }

    @FXML
    protected void btnCloseClicked(ActionEvent event){
        closeStage(event);
    }

    @FXML
    protected void btnPushClicked(ActionEvent event){
        publishBotConfig(botCalibConfig);
    }

    @FXML
    protected void btnSaveClicked(ActionEvent event){
        boolean changed = false;
        if (tabPageGeneralController != null){
            tabPageGeneralController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRController != null){
            tabPageMRController.save(botCalibConfig);
            changed = true;
        }

        if (changed) {
            saveBotConfig(botCalibConfig);
        }
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
