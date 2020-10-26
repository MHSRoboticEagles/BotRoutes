package entity;

import java.awt.*;
import com.google.gson.Gson;

public class AutoDot {
    private String dotName = "A";
    private boolean selected;
    private int x;
    private int y;
    private double heading = -1;


    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static AutoDot deserialize(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, AutoDot.class);
    }

    @Override
    public String toString() {
        if (heading != -1){
            return String.format("%d : %d. Heading: %.2f", x, y, heading);
        }
        return String.format("%d : %d", x, y);
    }

    public String getDotName() {
        return dotName;
    }

    public void setDotName(String dotName) {
        this.dotName = dotName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point getPoint(){
        return new Point(x, y);
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }
}
