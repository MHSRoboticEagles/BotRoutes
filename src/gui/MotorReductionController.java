package gui;

import entity.BotCalibConfig;
import entity.MotorReductionBot;
import entity.MotorReductionType;
import io.BotConfigController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MotorReductionController {

    private MotorReductionType mrType;

    @FXML
    private TextField tfMRFrontLeft;

    @FXML
    private TextField tfMRBackLeft;

    @FXML
    private TextField tfMRFrontRight;

    @FXML
    private TextField tfMRBackRight;

    @FXML
    private Label lblTitle;



    public void init(BotCalibConfig config, MotorReductionType mrType){
        this.setMrType(mrType);
        MotorReductionBot mr = getReduction(config);
        tfMRFrontLeft.setText(String.format("%.2f", mr.getLF()));
        tfMRBackLeft.setText(String.format("%.2f", mr.getLB()));

        tfMRFrontRight.setText(String.format("%.2f", mr.getRF()));
        tfMRBackRight.setText(String.format("%.2f", mr.getRB()));
    }

    public void save(BotCalibConfig config){
        MotorReductionBot mr = getReduction(config);
        mr.setLF(Double.valueOf(tfMRFrontLeft.getText()));
        mr.setLB(Double.valueOf(tfMRBackLeft.getText()));
        mr.setRF(Double.valueOf(tfMRFrontRight.getText()));
        mr.setRB(Double.valueOf(tfMRBackRight.getText()));
    }

    private MotorReductionBot getReduction(BotCalibConfig config){
        MotorReductionBot mr = null;
        switch (this.mrType){
            case MRForward:
                mr = config.getMoveMRForward();
                lblTitle.setText("Motor Reduction. Move Forward");
                break;
            case MRBack:
                mr = config.getMoveMRBack();
                lblTitle.setText("Motor Reduction. Move Backwards");
                break;
            case DiagMRLeft:
                mr = config.getDiagMRLeft();
                lblTitle.setText("Motor Reduction. Diag Left");
                break;
            case DiagMRRight:
                mr = config.getDiagMRRight();
                lblTitle.setText("Motor Reduction. Diag Right");
                break;
            case SpinLeftConfig:
                mr = config.getSpinLeftConfig();
                lblTitle.setText("Motor Reduction. Spin Left");
                break;
            case SpinRightConfig:
                mr = config.getSpinRightConfig();
                lblTitle.setText("Motor Reduction. Spin Right");
                break;
            case StrafeLeftReduction:
                mr = config.getStrafeLeftReduction();
                lblTitle.setText("Motor Reduction. Strafe Left");
                break;
            case StrafeRightReduction:
                mr = config.getStrafeRightReduction();
                lblTitle.setText("Motor Reduction. Strafe Right");
                break;
        }
        return mr;
    }

    public void setMrType(MotorReductionType mrType) {
        this.mrType = mrType;
    }
}
