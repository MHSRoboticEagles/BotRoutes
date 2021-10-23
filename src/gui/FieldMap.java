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
import javafx.util.Duration;

import java.awt.*;

public class FieldMap {
    private Canvas mapFlow;
    private static final int MAP_SCALE = 4;
    private double diam = 20;
    private CoordinateChangeListener coordinateChangeListener = null;
    private Timeline timeline = null;
    private AnimationTimer timer = null;
    public FieldMap (Canvas canvas){
        this.mapFlow = canvas;
    }

    public void init(){
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
        if (this.timeline != null) {
            this.timeline.stop();
            if (this.timer != null) {
                this.timer.stop();
            }
        }
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        drawField(gc);
        if (selectedRoute != null){
            drawRoute(selectedRoute, gc, conditionValue);
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
        Image original = new Image(getClass().getResourceAsStream("FreightFrenzyField.png"));
        gc.drawImage(original, 0, 0, mapFlow.getWidth(), mapFlow.getHeight());
    }

    protected void drawRoute(AutoRoute selectedRoute, GraphicsContext gc, String conditionValue){
        double height = mapFlow.getHeight();
        if (selectedRoute != null){
            gc.setStroke(Color.DARKGREEN.brighter());
            gc.setFill(Color.YELLOWGREEN);
            gc.setLineWidth(5);
            gc.beginPath();
            double startX = selectedRoute.getStartX() * MAP_SCALE;
            double startY = height - selectedRoute.getStartY() * MAP_SCALE;
            gc.fillOval(startX - diam/2, startY - diam/2, diam, diam);
            gc.moveTo(startX, startY);
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
                    Point target = getCoordinate(step, previousTarget);
                    double x = target.getX();
                    double y = target.getY();
                    gc.lineTo(x, y);
                    gc.fillOval(x - diam / 2, y - diam / 2, diam, diam);
                }
            }
            gc.stroke();
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

    private Point getCoordinate(AutoStep step, Point previousTarget){
        double height = mapFlow.getHeight();
        Point target = new Point();
        if (step.getMoveStrategy() == MoveStrategy.StraightRelative){
            if (step.getRobotDirection() == RobotDirection.Forward){
                target.x = (int)(previousTarget.getX() + step.getTargetX());
            }
            else{
                target.x = (int)(previousTarget.getX() - step.getTargetX());
            }
            target.y= (int)(previousTarget.getY());
            step.setRelX(target.x);
            step.setRelY(target.y);
            target.x = target.x * MAP_SCALE;
            target.y = (int) (height - target.y * MAP_SCALE);
        }
        else if (step.getMoveStrategy() == MoveStrategy.StrafeRelative){
            if (step.getRobotDirection() == RobotDirection.Left){
                target.x = (int)(previousTarget.getX() + step.getTargetX());
            }
            else{
                target.x = (int)(previousTarget.getX() - step.getTargetX());
            }
            target.y= (int)(previousTarget.getY());
            step.setRelX(target.x);
            step.setRelY(target.y);
            target.x = target.x * MAP_SCALE;
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
            step.setRelX(target.x);
            step.setRelY(target.y);
            target.x = target.x * MAP_SCALE;
            target.y = (int) (height - target.y * MAP_SCALE);
        }
        else {
            step.setRelX(step.getTargetX());
            step.setRelY(step.getTargetY());
            target.x = step.getTargetX() * MAP_SCALE;
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
    }

    public void animateSelectedStep(AutoRoute selectedRoute, AutoStep selectedStep, String conditionValue){

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
        pixelDot.setX(inches.getX()*MAP_SCALE);
        pixelDot.setY((int)Math.round(height - inches.getY()*MAP_SCALE));
        return pixelDot;
    }

    private int inchesToPixels(int inches){
        return inches * MAP_SCALE;
    }

    public void setCoordinateChangeListener(CoordinateChangeListener coordinateChangeListener) {
        this.coordinateChangeListener = coordinateChangeListener;
    }
}
