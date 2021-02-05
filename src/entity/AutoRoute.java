package entity;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class AutoRoute implements Comparable<AutoRoute> {
    public static String NAME_BLUE = "Blue";
    public static String NAME_RED = "Red";
    public static String NAME_NEW = "New";
    private int nameIndex = 0;
    private String name = NAME_BLUE;
    private boolean selected;
    private boolean temp;
    private ArrayList<AutoStep> steps = new ArrayList<>();
    private  transient ObservableList<AutoStep> visibleSteps = null;
    private int startX;
    private int startY;
    private long lastRunTime = 0;
    private String description;


    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static AutoRoute deserialize(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, AutoRoute.class);
    }

    @Override
    public String toString() {
        return this.getRouteName();
    }

    public AutoRoute clone(){
        AutoRoute cloned = new AutoRoute();
        cloned.setName(this.getName());
        cloned.setNameIndex(this.getNameIndex());
        cloned.setStartX(this.getStartX());
        cloned.setStartY(this.getStartY());
        cloned.setSelected(this.isSelected());
        cloned.setTemp(this.isTemp());
        cloned.setDescription(this.getDescription());
        for(int i = 0; i < steps.size(); i++){
            cloned.steps.add(this.steps.get(i).clone());
        }
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoRoute autoRoute = (AutoRoute) o;
        return getRouteName().equals(autoRoute.getRouteName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRouteName());
    }


    public String getRouteName()
    {
        if (this.isTemp()){
            return  String.format("%s-%d-%s", name, nameIndex, NAME_NEW);
        }
        return  String.format("%s-%d", name, nameIndex);
    }

    public String getRouteFullName()
    {
        String fullName = getRouteName();
        if(this.getDescription() != null && this.getDescription().isEmpty() == false){
            fullName = String.format("%s-%s",fullName, this.getDescription());
        }
        return  fullName;
    }


    public ArrayList<AutoStep> getSteps() {
        return steps;
    }

    public ObservableList<AutoStep> getVisibleSteps(){
        return visibleSteps;
    }

    public ObservableList<AutoStep> buildVisibleSteps(String condition){
        if (visibleSteps == null){
            visibleSteps = FXCollections.observableArrayList(steps);
        }
        updateMatchingSteps(condition);
        return visibleSteps;
    }

    public void updateMatchingSteps(String condition){
        visibleSteps.clear();
        int counter = 0;
        for (AutoStep step : getSteps()){
            step.setOriginalIndex(counter);
            if (step.meetsCondition(condition)){
                visibleSteps.add(step);
            }
            counter++;
        }
    }

    public void setSteps(ArrayList<AutoStep> steps) {
        this.steps = steps;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public Point getStart(){
        return new Point(startX, startY);
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public long getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(long lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public AutoDot findLocationPointer(AutoStep step, String condition){
        AutoDot locationPointer = new AutoDot();
        locationPointer.setX(this.getStartX());
        locationPointer.setY(this.getStartY());

        int stepIndex = findStepIndex(step);

        if (stepIndex > 0){
            int previousIndex = stepIndex - 1;
            AutoStep previous = this.getSteps().get(previousIndex);
            while (!stepApplies(previous, condition)){
                if (previousIndex > 0){
                    previousIndex--;
                }
                previous = this.getSteps().get(previousIndex);
                if (previousIndex == 0){
                    break;
                }
            }


            if (previous.isSameTarget(step)){
                locationPointer.setX(step.getTargetX());
                locationPointer.setY(step.getTargetY());
            }
            else{
                locationPointer.setX(previous.getTargetX());
                locationPointer.setY(previous.getTargetY());
            }

        }

        return locationPointer;
    }

    public boolean stepApplies(AutoStep step, String condition){
        return !step.hasCondition() || (step.hasCondition() && step.getConditionValue().equals(condition));
    }

    public int findStepIndex(AutoStep step){
        int stepIndex = -1;
        for (int i = 0; i < this.getSteps().size(); i++){
            AutoStep s = this.getSteps().get(i);
            if (s == step){
                stepIndex = i;
                break;
            }
        }
        return stepIndex;
    }

    @Override
    public int compareTo(AutoRoute o) {
        return this.getRouteName().compareTo(o.getRouteName());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
