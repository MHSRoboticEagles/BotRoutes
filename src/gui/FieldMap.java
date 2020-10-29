package gui;

import entity.AutoDot;
import entity.AutoRoute;
import entity.AutoStep;
import entity.CoordinateChangeListener;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class FieldMap {
    private Canvas mapFlow;
    private static int MAP_SCALE = 4;
    ContextMenu canvasMenu = null;
    private double diam = 20;
    private CoordinateChangeListener coordinateChangeListener = null;
    private Timeline timeline = null;
    private AnimationTimer timer = null;
    public FieldMap (Canvas canvas){
        this.mapFlow = canvas;
    }

    public void init(){
        double height = mapFlow.getHeight();
        mapFlow.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double pX = mouseEvent.getX();
                double pY = height - mouseEvent.getY();
                int x = (int)Math.round(pX/MAP_SCALE);
                int y = (int)Math.round(pY/MAP_SCALE);
                String output= String.format("Pixels: %.0f : %.0f. Inches: %d : %d", pX, pY, x, y);
                AutoDot dot = new AutoDot();
                dot.setX(x);
                dot.setY(y);
                if (coordinateChangeListener != null){
                    coordinateChangeListener.onCoordinateChanged(dot, output);
                }
            }
        });

        mapFlow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });

        initCanvasMenu();
    }

    protected void initCanvasMenu(){
        canvasMenu = new ContextMenu();
        MenuItem menuDetail = new MenuItem("Details");
        MenuItem menuInsertAfter = new MenuItem("Insert After");
        MenuItem menuInsertBefore = new MenuItem("Insert Before");
        MenuItem menuDelete = new MenuItem("Delete");

        menuDetail.setOnAction((event) -> {
            System.out.println("Show detail");
        });

        menuInsertAfter.setOnAction((event) -> {
            System.out.println(menuInsertAfter.getText());
        });

        menuInsertBefore.setOnAction((event) -> {
            System.out.println(menuInsertBefore.getText());
        });

        menuDelete.setOnAction((event) -> {
            System.out.println(menuDelete.getText());
        });
        canvasMenu.getItems().addAll(menuDetail,menuInsertAfter,menuInsertBefore, menuDelete);
        mapFlow.setOnContextMenuRequested(event -> {
            canvasMenu.show(mapFlow, event.getScreenX(), event.getScreenY());
        });
    }

    public void displaySelectedRoute(AutoRoute selectedRoute){
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        if (this.timeline != null) {
            this.timeline.stop();
            if (this.timer != null) {
                this.timer.stop();
            }
        }
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        if (selectedRoute != null){
            drawRoute(selectedRoute, gc);
        }
    }

    protected void drawRoute(AutoRoute selectedRoute, GraphicsContext gc){
        double height = mapFlow.getHeight();
        if (selectedRoute != null){
            gc.setStroke(Color.DARKGREEN.brighter());
            gc.setFill(Color.YELLOWGREEN);
            gc.setLineWidth(5);
            gc.beginPath();
            double startX = selectedRoute.getStartX() * MAP_SCALE;
            double startY = height - selectedRoute.getStartY() * MAP_SCALE;
            gc.moveTo(startX, startY);
            for(AutoStep step: selectedRoute.getSteps()){
                double x = step.getTargetX()*MAP_SCALE;
                double y = height - step.getTargetY()*MAP_SCALE;
                gc.lineTo(x, y);
                gc.fillOval(x - diam/2, y - diam/2, diam, diam);
            }
            gc.stroke();
        }
    }

    public void animateSelectedStep(AutoRoute selectedRoute, AutoStep selectedStep){

        displaySelectedRoute(selectedRoute);

        AutoDot locationPointer = selectedRoute.findLocationPointer(selectedStep);
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
                gc.setFill(Color.DARKRED.deriveColor(0, 1, 1, opacity.get()));
                gc.fillOval(pixels.getX() - diam/2, pixels.getY() - diam/2, diam, diam
                );
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

    public void setCoordinateChangeListener(CoordinateChangeListener coordinateChangeListener) {
        this.coordinateChangeListener = coordinateChangeListener;
    }
}
