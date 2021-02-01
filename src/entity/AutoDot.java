package entity;

import java.awt.*;
import java.util.Objects;

import com.google.gson.Gson;

public class AutoDot implements Comparable<AutoDot>{
    private String dotName = "A";
    public static int asciiA = 65;
    public static int asciiZ = 90;
    private boolean selected;
    private int x;
    private int y;
    private double heading = -1;
    private String fieldSide = AutoRoute.NAME_RED;

    public AutoDot(){

    }

    public AutoDot(int x, int y){
        this.setX(x);
        this.setY(y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String){
            return Objects.equals(getDotName(), o.toString());
        }
        else if (o instanceof AutoDot){
            return Objects.equals(getDotName(), ((AutoDot) o).getDotName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dotName);
    }

    @Override
    public AutoDot clone() {
        AutoDot dot = new AutoDot();
        dot.setDotName(this.getDotName());
        dot.setX(this.getX());
        dot.setY(this.getY());
        dot.setHeading(this.getHeading());
        dot.setSelected(this.isSelected());
        dot.setFieldSide(this.getFieldSide());
        return dot;
    }

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
            return String.format("%s. %d : %d. Heading: %.2f", getDotName(), x, y, heading);
        }
        return String.format("%s. %d : %d", getDotName(), x, y);
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

    @Override
    public int compareTo(AutoDot o) {
        return this.getDotName().compareTo(o.getDotName());
    }

    public String getFieldSide() {
        return fieldSide;
    }

    public void setFieldSide(String fieldSide) {
        this.fieldSide = fieldSide;
    }
}
