package gui;

import entity.*;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.awt.*;

public class FieldMap {
    private Canvas mapFlow;
    private double MAP_SCALE;
    private double diam = 20;
    private CoordinateChangeListener coordinateChangeListener = null;
    private Timeline timeline = null;
    private AnimationTimer timer = null;

    private double robotWidth = 18;
    private double robotLength = 18;

    private double FIELD_SIZE_IN = 144;

    public FieldMap (Canvas canvas){
        this.mapFlow = canvas;
        double avgCanvasSize = (canvas.getHeight() + canvas.getWidth()) / 2;
        this.MAP_SCALE = avgCanvasSize / FIELD_SIZE_IN;
    }

    public void init() {
        init(18,18);
    }

    public void init(int robotLengthInches, int robotWidthInches) {
        this.robotLength = robotLengthInches;
        this.robotWidth = robotWidthInches;

        mapFlow.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mapFlow.getScene().setCursor(Cursor.CROSSHAIR);
                AutoDot selected = mouseClickToDot(mouseEvent);
                String output= String.format("Inches: %d : %d", selected.getX(), selected.getY());
                if (coordinateChangeListener != null){
                    coordinateChangeListener.onCoordinateChanged(selected, output);
                }
            }
        });

        mapFlow.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mapFlow.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        mapFlow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                        mouseEvent.getClickCount() == 2){
                    if (coordinateChangeListener != null){
                        AutoDot selected = mouseClickToDot(mouseEvent);
                        coordinateChangeListener.onCoordinatePicked(selected);
                    }
                }
            }
        });
    }

    private AutoDot mouseClickToDot(MouseEvent mouseEvent){
        double height = mapFlow.getHeight();
        double pX = mouseEvent.getX();
        double pY = height - mouseEvent.getY();
        int x = (int)Math.round(pX/MAP_SCALE);
        int y = (int)Math.round(pY/MAP_SCALE);
        AutoDot dot = new AutoDot();
        dot.setX(x);
        dot.setY(y);
        return dot;
    }


    public void displaySelectedRoute(AutoRoute selectedRoute, String conditionValue){
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        drawField(gc);
        if (selectedRoute != null){
            drawRoute(selectedRoute, gc, conditionValue, null);
        }
    }

    public void drawSelectedDot(AutoDot dot){
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        if (this.timeline != null) {
            this.timeline.stop();
            if (this.timer != null) {
                this.timer.stop();
            }
        }
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        drawField(gc);
        if (dot != null){
            drawDot(dot, gc);
        }
    }

    protected void drawField(GraphicsContext gc) {
        gc.save();
        Image original = new Image(getClass().getResourceAsStream("PowerPlayField.png"));
//        Rotate r = new Rotate(90, mapFlow.getWidth()/2, mapFlow.getHeight()/2);
//        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(original, 0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        gc.restore();
    }

    protected void drawRoute(AutoRoute selectedRoute, GraphicsContext gc, String conditionValue, AutoStep selectedStep){
        double height = mapFlow.getHeight();
        if (selectedRoute != null){
            gc.setStroke(Color.DARKGREEN.brighter());
            gc.setFill(Color.YELLOWGREEN);
            gc.setLineWidth(5);
            double robotHeading = selectedRoute.getInitRotation();
            double previousHeading = robotHeading;
            double strokeStartX = selectedRoute.getStartX() * MAP_SCALE;
            double strokeStartY = height - selectedRoute.getStartY() * MAP_SCALE;
            gc.fillOval(strokeStartX - diam/2, strokeStartY - diam/2, diam, diam);

//            drawRobotAroundDot(selectedRoute.getStartX() , selectedRoute.getStartY() , selectedRoute.getInitRotation(), gc, Color.YELLOWGREEN);

            int selectedStepIndex = -1;
            if (selectedStep != null) {
                selectedStepIndex = selectedRoute.findStepIndex(selectedStep);
            }

            for(int i = 0; i < selectedRoute.getSteps().size(); i++){
                AutoStep step = selectedRoute.getSteps().get(i);
                if (step.meetsCondition(conditionValue)) {
                    Point previousTarget = new Point(selectedRoute.getStartX(), selectedRoute.getStartY());
                    if (i > 0) {
                        AutoStep prevStep = getPreviousStep(selectedRoute, i, conditionValue);
                        if (prevStep != null) {
                            previousTarget = new Point(prevStep.getRelX(), prevStep.getRelY());
                        }
                    }
                    Point target = getCoordinate(step, previousTarget, previousHeading);

                    double x = target.getX();
                    double y = target.getY();

                    Color robotColor = Color.YELLOWGREEN;
                    if (i == selectedStepIndex) {
                        robotColor = Color.DARKRED;
                    }

                    gc.setLineWidth(5);
                    gc.setFill(robotColor.brighter());
                    gc.setStroke(robotColor);
                    gc.strokeLine(strokeStartX, strokeStartY, x, y);
                    gc.fillOval(x - diam / 2, y - diam / 2, diam, diam);

                    if (step.getDesiredHead() != -1) {
                        robotHeading = step.getDesiredHead();
                    } else if (step.getMoveStrategy() != MoveStrategy.None) {
                        robotHeading = previousHeading;
                    }

                    if (i == selectedStepIndex) {
                        drawRobotAroundDot(x / MAP_SCALE, (mapFlow.getHeight() - y) / MAP_SCALE, robotHeading, gc, robotColor);
                    }

                    strokeStartX = x;
                    strokeStartY = y;

                    previousHeading = robotHeading;
                }
            }
        }
    }

    private AutoStep getPreviousStep(AutoRoute selectedRoute, int currentIndex, String conditionValue){
        AutoStep prevStep = null;
        for(int prevIndex = currentIndex - 1; prevIndex >= 0; prevIndex--){
            AutoStep step = selectedRoute.getSteps().get(prevIndex);
            if (step.meetsCondition(conditionValue)){
                prevStep = step;
                break;
            }
        }
        return prevStep;
    }

    private int constrainedCoordinate(int value) {
        int v = Math.max(0, value);
        return (int) Math.min(v, FIELD_SIZE_IN);
    }

    private Point getCoordinate(AutoStep step, Point previousTarget, double previousHeading){
        double height = mapFlow.getHeight();
        Point target = new Point();
        double prevHeadingRadians = Math.toRadians(previousHeading);
        if (step.getMoveStrategy() == MoveStrategy.StraightRelative){
            // for relative moves, step.X is the only value that matters. step.Y is ignored
            if (step.getRobotDirection() == RobotDirection.Forward){
                target.x = (int)(previousTarget.getX() + step.getTargetX() * Math.sin(prevHeadingRadians));
                target.y = (int)(previousTarget.getY() + step.getTargetX() * Math.cos(prevHeadingRadians));
            }
            else{
                target.x = (int)(previousTarget.getX() - step.getTargetX() * Math.sin(prevHeadingRadians));
                target.y = (int)(previousTarget.getY() - step.getTargetX() * Math.cos(prevHeadingRadians));
            }
            target.x = constrainedCoordinate(target.x);
            target.y = constrainedCoordinate(target.y);
            step.setRelX(target.x);
            step.setRelY(target.y);
            target.x = (int) (target.x * MAP_SCALE);
            target.y = (int) (height - target.y * MAP_SCALE);
        }
        else if (step.getMoveStrategy() == MoveStrategy.StrafeRelative){
            // for relative moves, step.X is the only value that matters. step.Y is ignored
            if (step.getRobotDirection() == RobotDirection.Left){
                target.x = (int)(previousTarget.getX() - step.getTargetX() * Math.cos(prevHeadingRadians));
                target.y = (int)(previousTarget.getY() - step.getTargetX() * Math.sin(prevHeadingRadians));
            }
            else{
                target.x = (int)(previousTarget.getX() + step.getTargetX() * Math.cos(prevHeadingRadians));
                target.y = (int)(previousTarget.getY() + step.getTargetX() * Math.sin(prevHeadingRadians));
            }
            target.x = constrainedCoordinate(target.x);
            target.y = constrainedCoordinate(target.y);
            step.setRelX(target.x);
            step.setRelY(target.y);
            target.x = (int) (target.x * MAP_SCALE);
            target.y = (int) (height - target.y * MAP_SCALE);
        }
        else if (step.getMoveStrategy() == MoveStrategy.AutoLine){
            if (step.getTargetX() > 0){
                target.x = step.getTargetX();
                target.y = (int)previousTarget.getY();
            }
            else{
                target.x = (int)previousTarget.getX();
                target.y = step.getTargetY();
            }
            target.x = constrainedCoordinate(target.x);
            target.y = constrainedCoordinate(target.y);
            step.setRelX(target.x);
            step.setRelY(target.y);
            target.x = (int) (target.x * MAP_SCALE);
            target.y = (int) (height - target.y * MAP_SCALE);
        }
        else {
            step.setRelX(step.getTargetX());
            step.setRelY(step.getTargetY());
            target.x = (int) (step.getTargetX() * MAP_SCALE);
            target.y = (int) (height - step.getTargetY() * MAP_SCALE);
        }
        return target;
    }

    protected void drawDot(AutoDot dot, GraphicsContext gc){
        gc.setFill(Color.YELLOWGREEN);
        double height = mapFlow.getHeight();
        double x = dot.getX()*MAP_SCALE;
        double y = height - dot.getY()*MAP_SCALE;
        gc.fillOval(x - diam/2, y - diam/2, diam, diam);
        drawRobotAroundDot(dot.getX(), dot.getY(), dot.getHeading(), gc, Color.YELLOWGREEN);
    }

    protected void drawRobotAroundDot(double centerX, double centerY, double robotHeading, GraphicsContext gc, Color robotColor) {

        double halfWidth = this.robotWidth/2;
        double halfLength = this.robotLength/2;
        double halfDiagnonal = Math.sqrt(halfLength*halfLength + halfWidth*halfWidth);

        double robotBodyAngle = Math.atan2(halfWidth, halfLength); // center to corner angle

        double x = centerX*MAP_SCALE;
        double y = mapFlow.getHeight() - centerY*MAP_SCALE;

        if (robotHeading != -1) {
            double aRadians = Math.toRadians(robotHeading + 90);

            gc.setFill(robotColor);
            gc.setStroke(robotColor);
            gc.setLineWidth(2);

            double x1 = x + halfDiagnonal * MAP_SCALE * Math.cos(aRadians + robotBodyAngle);
            double y1 = y - halfDiagnonal * MAP_SCALE * Math.sin(aRadians + robotBodyAngle);
            double x2 = x + halfDiagnonal * MAP_SCALE * Math.cos(aRadians + Math.PI - robotBodyAngle);
            double y2 = y - halfDiagnonal * MAP_SCALE * Math.sin(aRadians + Math.PI - robotBodyAngle);
            double x3 = x + halfDiagnonal * MAP_SCALE * Math.cos(aRadians + Math.PI + robotBodyAngle);
            double y3 = y - halfDiagnonal * MAP_SCALE * Math.sin(aRadians + Math.PI + robotBodyAngle);
            double x4 = x + halfDiagnonal * MAP_SCALE * Math.cos(aRadians - robotBodyAngle);
            double y4 = y - halfDiagnonal * MAP_SCALE * Math.sin(aRadians - robotBodyAngle);

            double xc = x + 9 * MAP_SCALE * Math.cos(aRadians);
            double yc = y - 9 * MAP_SCALE * Math.sin(aRadians);

            gc.strokeLine(x1, y1, x2, y2);
            gc.strokeLine(x2, y2, x3, y3);
            gc.strokeLine(x3, y3, x4, y4);
            gc.strokeLine(x4, y4, x1, y1);

            gc.strokeLine(x, y, xc, yc);
        } else {
            gc.setStroke(robotColor);
            gc.setLineWidth(2);

            double diam = MAP_SCALE * (this.robotLength + this.robotWidth) / 2;

            gc.strokeOval(x - diam/2, y - diam/2, diam, diam);
        }

    }

    public void animateSelectedStep(AutoRoute selectedRoute, AutoStep selectedStep, String conditionValue) {
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        drawField(gc);
        drawRoute(selectedRoute, gc, conditionValue, selectedStep);
    }

    public void animateSelectedStep_orig(AutoRoute selectedRoute, AutoStep selectedStep, String conditionValue){

        displaySelectedRoute(selectedRoute, conditionValue);

        AutoDot locationPointer = selectedRoute.findLocationPointer(selectedStep, conditionValue);
        AutoDot destination = inchesToPixels(new AutoDot(selectedStep.getRelX(), selectedStep.getRelY()));
        boolean drawLine = !selectedStep.isSameTarget(locationPointer);
        AutoDot pixels = inchesToPixels(locationPointer);
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        DoubleProperty opacity  = new SimpleDoubleProperty();
        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(opacity, 1)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(opacity, 0)
                )
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.setFill(Color.YELLOWGREEN);
                gc.fillOval(pixels.getX() - diam/2, pixels.getY() - diam/2, diam, diam);
                if (drawLine){
                    gc.fillOval(destination.getX() - diam/2, destination.getY() - diam/2, diam, diam);
                }
                gc.setFill(Color.DARKRED.deriveColor(0, 1, 1, opacity.get()));
                gc.fillOval(pixels.getX() - diam/2, pixels.getY() - diam/2, diam, diam);
                if (drawLine){
                    gc.fillOval(destination.getX() - diam/2, destination.getY() - diam/2, diam, diam);
                }
            }
        };
        timer.start();
        timeline.play();
    }

    private AutoDot inchesToPixels(AutoDot inches){
        double height = mapFlow.getHeight();
        AutoDot pixelDot = new AutoDot();
        pixelDot.setX((int) (inches.getX()*MAP_SCALE));
        pixelDot.setY((int)Math.round(height - inches.getY()*MAP_SCALE));
        return pixelDot;
    }

    private int inchesToPixels(int inches){
        return (int) (inches * MAP_SCALE);
    }

    public void setCoordinateChangeListener(CoordinateChangeListener coordinateChangeListener) {
        this.coordinateChangeListener = coordinateChangeListener;
    }
}
