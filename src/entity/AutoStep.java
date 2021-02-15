package entity;

import logic.RouteController;

public class AutoStep {
    public static final String NO_ACTION = "";
    private int waitMS = 0;
    private int targetX;
    private int targetY;
    private double topSpeed = 0;
    private MoveStrategy moveStrategy = MoveStrategy.Curve;
    private RobotDirection robotDirection = RobotDirection.Optimal;
    private String action = NO_ACTION;
    private double desiredHead = -1;
    private boolean continuous = false;
    private String targetReference = "";
    private String conditionValue = "";
    private String conditionFunction = "";
    private transient int originalIndex = -1;
    private transient int relX;
    private transient int relY;

    @Override
    public String toString() {
        String description = String.format("Wait %d ms\n", waitMS);

        if (action.equals("")){
            action = "None";
        }

        if (moveStrategy == MoveStrategy.Spin){
            description = String.format("%s%s to %.2f degrees. Speed %.1f\n", description, moveStrategy, desiredHead, topSpeed);
        }else {
            description = String.format("%s%s to %s. Speed %.1f\n", description, moveStrategy, getDestination(), topSpeed);
        }

        description = String.format("%sAction: %s\n", description, action);
        return description;
    }


    public int getWaitMS() {
        return waitMS;
    }

    public void setWaitMS(int waitMS) {
        this.waitMS = waitMS;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(double topSpeed) {
        this.topSpeed = topSpeed;
    }

    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }

    public void setMoveStrategy(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDestination(){
        if (targetReference.equals("")) {
            return String.format("%d : %d", getTargetX(), getTargetY());
        }
        else{
            return targetReference;
        }
    }

    public String getTopSpeedString(){
        return String.format("%.2f", getTopSpeed());
    }

    public String getMoveStrategyString(){
        return getMoveStrategy().name();
    }

    public String getWaitString(){
        return String.format("%d", getWaitMS());
    }

    public double getDesiredHead() {
        return desiredHead;
    }

    public String getDesiredHeadString() {
        return String.format("%.2f", desiredHead);
    }

    public void setDesiredHead(double desiredHead) {
        this.desiredHead = desiredHead;
    }

    public AutoStep clone(){
        AutoStep clone = new AutoStep();
        clone.setWaitMS(this.getWaitMS());
        clone.setTargetX(this.getTargetX());
        clone.setTargetY(this.getTargetY());
        clone.setTopSpeed(this.getTopSpeed());
        clone.setMoveStrategy(this.getMoveStrategy());
        clone.setRobotDirection(this.getRobotDirection());
        clone.setAction(this.getAction());
        clone.setDesiredHead(this.getDesiredHead());
        clone.setContinuous(this.isContinuous());
        clone.setTargetReference(this.getTargetReference());
        clone.setConditionFunction(this.getConditionFunction());
        clone.setConditionValue(this.getConditionValue());
        return clone;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public String isContinuousAsString() {
        return String.format("%b", continuous);
    }


    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public String getTargetReference() {
        return targetReference;
    }

    public void setTargetReference(String targetReference) {
        this.targetReference = targetReference;
    }

    public RobotDirection getRobotDirection() {
        return robotDirection;
    }

    public void setRobotDirection(RobotDirection robotDirection) {
        this.robotDirection = robotDirection;
    }

    public boolean isSameTarget(AutoStep s){
        return this.getTargetX() == s.getTargetX() &&
                this.getTargetY() == s.getTargetY();
    }

    public boolean isSameTarget(AutoDot dot){
        return this.getTargetX() == dot.getX() &&
                this.getTargetY() == dot.getY();
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getConditionFunction() {
        return conditionFunction;
    }

    public void setConditionFunction(String conditionFunction) {
        this.conditionFunction = conditionFunction;
    }

    public boolean hasCondition(){
        return this.conditionFunction.isEmpty() == false;
    }

    public boolean meetsCondition(String condition){
        return !this.hasCondition() || (this.hasCondition() && this.getConditionValue().equals(condition));
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }

    public int getRelX() {
        return relX;
    }

    public void setRelX(int relX) {
        this.relX = relX;
    }

    public int getRelY() {
        return relY;
    }

    public void setRelY(int relY) {
        this.relY = relY;
    }
}
