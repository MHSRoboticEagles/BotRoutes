package io;

import entity.*;
import gui.FrenzyAttachmentsController;
import gui.GeneralConfigController;
import gui.MotorReductionController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.DotsChangeListener;
import logic.RoutesChangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class BotConfigController {
    BotCalibConfig botCalibConfig = null;

    @FXML private GeneralConfigController tabPageGeneralController;

    @FXML private FrenzyAttachmentsController tabPageFrenzyAttachmentsController;

    @FXML private MotorReductionController tabPageVelocityController;

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

    public void publishBotConfig(BotCalibConfig config, ActionEvent event){
        try
        {
            BotConnector.publishBotConfig(config);
            showMessage("Bot config pushed", Alert.AlertType.INFORMATION, event);
        }
        catch (Exception ex){
            showMessage(String.format("Unable to push bot config. %s", ex.getMessage()), Alert.AlertType.ERROR, event);
        }
    }


    public void init() {
        loadBotConfig();

        //general tab
        if(botCalibConfig != null && tabPageGeneralController != null){
            tabPageGeneralController.init(botCalibConfig);
        }

        // Freight Frenzy Attachments tab
        if(botCalibConfig != null && tabPageFrenzyAttachmentsController != null) {
            tabPageFrenzyAttachmentsController.init(botCalibConfig);
        }

        //Velocity tab
        if(botCalibConfig != null && tabPageVelocityController != null){
            tabPageVelocityController.init(botCalibConfig, MotorReductionType.Velocity);
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
        publishBotConfig(botCalibConfig, event);
    }

    @FXML
    protected void btnSaveClicked(ActionEvent event){
        boolean changed = false;
        if (tabPageGeneralController != null){
            tabPageGeneralController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageFrenzyAttachmentsController != null){
            tabPageFrenzyAttachmentsController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageVelocityController != null){
            tabPageVelocityController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRController != null){
            tabPageMRController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRBackController != null){
            tabPageMRBackController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRStrafeLeftController != null){
            tabPageMRStrafeLeftController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRStrafeRightController != null){
            tabPageMRStrafeRightController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRDiagLeftController != null){
            tabPageMRDiagLeftController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRDiagRightController != null){
            tabPageMRDiagRightController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRSpinLeftController != null){
            tabPageMRSpinLeftController.save(botCalibConfig);
            changed = true;
        }

        if (tabPageMRSpinRightController != null){
            tabPageMRSpinRightController.save(botCalibConfig);
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

    protected void showMessage(String msg, Alert.AlertType type, ActionEvent event){
        String title = "Info";
        if (type == Alert.AlertType.ERROR){
            title = "Error";
        }
        Alert alert = new Alert(type);
        Node source = (Node)  event.getSource();
        if (source != null) {
            alert.initOwner(source.getScene().getWindow());
        }
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }
}
