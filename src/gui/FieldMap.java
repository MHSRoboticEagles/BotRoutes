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
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class FieldMap {
    private Canvas mapFlow;
    private static final int MAP_SCALE = 4;
    private static final int TILES_NUM_Y = 6;
    private static final int TILES_NUM_X = 4;
    ContextMenu canvasMenu = null;
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


    public void displaySelectedRoute(AutoRoute selectedRoute){
        GraphicsContext gc = mapFlow.getGraphicsContext2D();
        if (this.timeline != null) {
            this.timeline.stop();
            if (this.timer != null) {
                this.timer.stop();
            }
        }
        gc.clearRect(0, 0, mapFlow.getWidth(), mapFlow.getHeight());
        drawFieldOutline(gc);
        drawFieldElements(gc, selectedRoute);
        if (selectedRoute != null){
            drawRoute(selectedRoute, gc);
        }
    }

    protected void drawFieldOutline(GraphicsContext gc){
        double height = mapFlow.getHeight();
        double width = mapFlow.getWidth();

        gc.setStroke(Color.BLACK.brighter());
        gc.setLineWidth(2);
        gc.beginPath();
        double startX = 0;
        double startY = 0;
        gc.moveTo(startX, startY);
        gc.lineTo(0, height);
        gc.lineTo(width, height);
        gc.lineTo(width, 0);
        gc.lineTo(0, 0);
        //horizontal
        for (int i = 0; i < TILES_NUM_Y; i++){
            int y = (i+1) * inchesToPixels(24);
            gc.moveTo(0, y);
            gc.lineTo(width, y);
        }
        //vertical
        for (int i = 0; i < TILES_NUM_X; i++){
            int x = (i+1) * inchesToPixels(24);
            gc.moveTo(x, 0);
            gc.lineTo(x, height);
        }
        gc.stroke();
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
            gc.fillOval(startX - diam/2, startY - diam/2, diam, diam);
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

    private void drawFieldElements(GraphicsContext gc, AutoRoute selectedRoute) {
        if (selectedRoute.getName().equals(AutoRoute.NAME_RED)){
            drawFieldElementsRed(gc);
        }
        else{
            drawFieldElementsBlue(gc);
        }
    }

    private void drawFieldElementsBlue(GraphicsContext gc){
        //line 2
        AutoDot startDot = new AutoDot(48, 24);
        AutoDot startPixels = inchesToPixels(startDot);
        Rectangle line = new Rectangle(startPixels.getX(), startPixels.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, line, Color.DARKBLUE);

        //line 1
        AutoDot startDot2 = new AutoDot(24, 24);
        AutoDot startPixel2s = inchesToPixels(startDot2);
        Rectangle line2 = new Rectangle(startPixel2s.getX(), startPixel2s.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, line2, Color.DARKBLUE);

        //rings
        AutoDot startDotRings = new AutoDot(35, 52);
        AutoDot startPixelRings = inchesToPixels(startDotRings);
        Rectangle rings = new Rectangle(startPixelRings.getX(), startPixelRings.getY(), inchesToPixels(2), inchesToPixels(4));
        drawRectangle(gc, rings, Color.DARKBLUE);

        //Zone A
        AutoDot startDotA = new AutoDot(0, 96);
        AutoDot startPixelA = inchesToPixels(startDotA);
        Rectangle lineA = new Rectangle(startPixelA.getX(), startPixelA.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineA, Color.DARKBLUE);
        Rectangle lineAU = new Rectangle(startPixelA.getX(), startPixelA.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineAU, Color.DARKBLUE);

        AutoDot startDotAL = new AutoDot(0, 74);
        AutoDot startPixelAL = inchesToPixels(startDotAL);
        Rectangle lineAL = new Rectangle(startPixelAL.getX(), startPixelAL.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineAL, Color.DARKBLUE);

        AutoDot startDotAR = new AutoDot(22, 96);
        AutoDot startPixelAR = inchesToPixels(startDotAR);
        Rectangle lineAR = new Rectangle(startPixelAR.getX(), startPixelAR.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineAR, Color.DARKBLUE);

        //Zone B
        AutoDot startDotB = new AutoDot(24, 120);
        AutoDot startPixelB = inchesToPixels(startDotB);
        Rectangle lineB = new Rectangle(startPixelB.getX(), startPixelB.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineB, Color.DARKBLUE);
        Rectangle lineBU = new Rectangle(startPixelB.getX(), startPixelB.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineBU, Color.DARKBLUE);

        AutoDot startDotBL = new AutoDot(24, 98);
        AutoDot startPixelBL = inchesToPixels(startDotBL);
        Rectangle lineBL = new Rectangle(startPixelBL.getX(), startPixelBL.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineBL, Color.DARKBLUE);

        AutoDot startDotBR = new AutoDot(46, 120);
        AutoDot startPixelBR = inchesToPixels(startDotBR);
        Rectangle lineBR = new Rectangle(startPixelBR.getX(), startPixelBR.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineBR, Color.DARKBLUE);

        //Zone C
        AutoDot startDotC = new AutoDot(0, 144);
        AutoDot startPixelC = inchesToPixels(startDotC);
        Rectangle lineC = new Rectangle(startPixelC.getX(), startPixelC.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineC, Color.DARKBLUE);
        Rectangle lineCU = new Rectangle(startPixelC.getX(), startPixelC.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineCU, Color.DARKBLUE);

        AutoDot startDotCL = new AutoDot(0, 122);
        AutoDot startPixelCL = inchesToPixels(startDotCL);
        Rectangle lineCL = new Rectangle(startPixelCL.getX(), startPixelCL.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineCL, Color.DARKBLUE);

        AutoDot startDotCR = new AutoDot(22, 144);
        AutoDot startPixelCR = inchesToPixels(startDotCR);
        Rectangle lineCR = new Rectangle(startPixelCR.getX(), startPixelCR.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineCR, Color.DARKBLUE);

        // wobble left
        AutoDot centerWL = new AutoDot(21, 30);
        AutoDot centerWLPix = inchesToPixels(centerWL);
        Circle wobbleLeft = new Circle(centerWLPix.getX(), centerWLPix.getY(), inchesToPixels(4));
        drawCircle(gc, wobbleLeft, Color.DARKBLUE);

        // wobble right
        AutoDot centerWR = new AutoDot(45, 30);
        AutoDot centerWRPix = inchesToPixels(centerWR);
        Circle wobbleRight = new Circle(centerWRPix.getX(), centerWRPix.getY(), inchesToPixels(4));
        drawCircle(gc, wobbleRight, Color.DARKBLUE);
    }

    private void drawFieldElementsRed(GraphicsContext gc){
        //line 2
        AutoDot startDot = new AutoDot(72, 24);
        AutoDot startPixels = inchesToPixels(startDot);
        Rectangle line = new Rectangle(startPixels.getX(), startPixels.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, line, Color.DARKRED);

        //line 1
        AutoDot startDot2 = new AutoDot(48, 24);
        AutoDot startPixel2s = inchesToPixels(startDot2);
        Rectangle line2 = new Rectangle(startPixel2s.getX(), startPixel2s.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, line2, Color.DARKRED);

        //rings
        AutoDot startDotRings = new AutoDot(59, 52);
        AutoDot startPixelRings = inchesToPixels(startDotRings);
        Rectangle rings = new Rectangle(startPixelRings.getX(), startPixelRings.getY(), inchesToPixels(2), inchesToPixels(4));
        drawRectangle(gc, rings, Color.DARKRED);

        //Zone A
        AutoDot startDotA = new AutoDot(72, 96);
        AutoDot startPixelA = inchesToPixels(startDotA);
        Rectangle lineA = new Rectangle(startPixelA.getX(), startPixelA.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineA, Color.DARKRED);
        Rectangle lineAU = new Rectangle(startPixelA.getX(), startPixelA.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineAU, Color.DARKRED);

        AutoDot startDotAL = new AutoDot(72, 74);
        AutoDot startPixelAL = inchesToPixels(startDotAL);
        Rectangle lineAL = new Rectangle(startPixelAL.getX(), startPixelAL.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineAL, Color.DARKRED);

        AutoDot startDotAR = new AutoDot(94, 96);
        AutoDot startPixelAR = inchesToPixels(startDotAR);
        Rectangle lineAR = new Rectangle(startPixelAR.getX(), startPixelAR.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineAR, Color.DARKRED);

        //Zone B
        AutoDot startDotB = new AutoDot(48, 120);
        AutoDot startPixelB = inchesToPixels(startDotB);
        Rectangle lineB = new Rectangle(startPixelB.getX(), startPixelB.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineB, Color.DARKRED);
        Rectangle lineBU = new Rectangle(startPixelB.getX(), startPixelB.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineBU, Color.DARKRED);

        AutoDot startDotBL = new AutoDot(48, 98);
        AutoDot startPixelBL = inchesToPixels(startDotBL);
        Rectangle lineBL = new Rectangle(startPixelBL.getX(), startPixelBL.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineBL, Color.DARKRED);

        AutoDot startDotBR = new AutoDot(70, 120);
        AutoDot startPixelBR = inchesToPixels(startDotBR);
        Rectangle lineBR = new Rectangle(startPixelBR.getX(), startPixelBR.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineBR, Color.DARKRED);

        //Zone C
        AutoDot startDotC = new AutoDot(72, 144);
        AutoDot startPixelC = inchesToPixels(startDotC);
        Rectangle lineC = new Rectangle(startPixelC.getX(), startPixelC.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineC, Color.DARKRED);
        Rectangle lineCU = new Rectangle(startPixelC.getX(), startPixelC.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineCU, Color.DARKRED);

        AutoDot startDotCL = new AutoDot(72, 122);
        AutoDot startPixelCL = inchesToPixels(startDotCL);
        Rectangle lineCL = new Rectangle(startPixelCL.getX(), startPixelCL.getY(), inchesToPixels(24), inchesToPixels(2));
        drawRectangle(gc, lineCL, Color.DARKRED);

        AutoDot startDotCR = new AutoDot(94, 144);
        AutoDot startPixelCR = inchesToPixels(startDotCR);
        Rectangle lineCR = new Rectangle(startPixelCR.getX(), startPixelCR.getY(), inchesToPixels(2), inchesToPixels(24));
        drawRectangle(gc, lineCR, Color.DARKRED);

        // wobble left
        AutoDot centerWL = new AutoDot(45, 30);
        AutoDot centerWLPix = inchesToPixels(centerWL);
        Circle wobbleLeft = new Circle(centerWLPix.getX(), centerWLPix.getY(), inchesToPixels(4));
        drawCircle(gc, wobbleLeft, Color.DARKRED);

        // wobble right
        AutoDot centerWR = new AutoDot(69, 30);
        AutoDot centerWRPix = inchesToPixels(centerWR);
        Circle wobbleRight = new Circle(centerWRPix.getX(), centerWRPix.getY(), inchesToPixels(4));
        drawCircle(gc, wobbleRight, Color.DARKRED);
    }

    private void drawRectangle(GraphicsContext gc, Rectangle rect, Paint paint){
        gc.setFill(paint);
        gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    private void drawCircle(GraphicsContext gc, Circle circle, Paint paint){
        gc.setFill(paint);
        gc.fillOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius() * 2, circle.getRadius() * 2);
    }

    public void animateSelectedStep(AutoRoute selectedRoute, AutoStep selectedStep){

        displaySelectedRoute(selectedRoute);

        AutoDot locationPointer = selectedRoute.findLocationPointer(selectedStep);
        AutoDot destination = inchesToPixels(new AutoDot(selectedStep.getTargetX(), selectedStep.getTargetY()));
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
