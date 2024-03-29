package logic;

import entity.*;
import io.FileLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;

public class RouteController {
    private ObservableList<AutoDot> namedDots = null;

    private ArrayList<AutoRoute> routes = new ArrayList<>();
    private ArrayList<BotActionObj> botActions = new ArrayList<>();

    private RoutesChangeListener routesChangeListener = null;
    private DotsChangeListener dotsChangeListener = null;

    private String CONDITION_FUNCTION = "getDetectionResult"; //this should not be hardcoded ideally

    public RouteController(){

    }

    public void init() throws Exception{
        FileLoader.ensureAppDirectories();
        this.namedDots = FXCollections.observableArrayList(FileLoader.listDots());
        this.routes = FileLoader.listRoutes();
        this.botActions = FileLoader.listBotActions();
        Collections.sort(this.routes);
        Collections.sort(this.namedDots);
        Collections.sort(this.botActions);
        reconcileData();
    }

    private void reconcileData(){
        for(AutoRoute r : this.routes){
            reconcileRoute(r);
        }
    }

    public void reconcileRoute(AutoRoute route){
        for(AutoStep step: route.getSteps()){
           reconcileStep(step, route);
        }
    }

    public void reconcileStep(AutoStep step, AutoRoute route){
        if (!step.getTargetReference().isEmpty()){
            AutoDot dot = findTargetReference(step.getTargetReference(), route);
            if (dot != null){
                step.setTargetX(dot.getX());
                step.setTargetY(dot.getY());
            }
        }
    }

    public void reconcileRouteSteps(AutoRoute route, String condition){
        for(AutoStep step: route.buildVisibleSteps(condition)){
            reconcileVisibleStep(step, route, condition);
        }
    }

    public void reconcileVisibleStep(AutoStep step, AutoRoute route, String condition){
        if (!step.getTargetReference().isEmpty() && !condition.isEmpty()){
            AutoDot dot = findNamedDot(condition, route.getName());
            if (dot != null){
                step.setTargetX(dot.getX());
                step.setTargetY(dot.getY());
            }
        }
    }

    public AutoDot findTargetReference(String name, AutoRoute route){
        AutoDot dot = findNamedDot(name, route.getName());
        if (dot == null){
            //try actions
            BotActionObj actionObj = findAction(name);
            if (actionObj != null && !actionObj.getReturnRef().isEmpty()){
                dot = findNamedDot(actionObj.getReturnRef(), route.getName());
            }
        }
        return dot;
    }

    public AutoDot findNamedDot(String name, String fieldSide){
        for(AutoDot d : this.namedDots){
            if (d.getDotName().equals(name) && d.getFieldSide().equals(fieldSide)){
                return d;
            }
        }
        return null;
    }

    public BotActionObj findAction(String name){
        for(BotActionObj actionObj : this.botActions){
            if (actionObj.getMethodName().equals(name)){
                return actionObj;
            }
        }
        return null;
    }

    public ObservableList<AutoDot> getNamedDots() {
        return namedDots;
    }

    public ArrayList<TargetReference> getTargetReferences(AutoRoute route) {
        ArrayList<TargetReference> refs = new ArrayList<>();
        TargetReference dummy = new TargetReference();
        dummy.setDotName("");
        dummy.setDescription("None");
        refs.add(dummy);
        for (AutoDot d : this.namedDots){
            if (d.getFieldSide().equals(route.getName())) {
                TargetReference tr = new TargetReference(d);
                refs.add(tr);
            }
        }
        for(BotActionObj actionObj : this.botActions){
            if (actionObj.isGeo() && !actionObj.getReturnRef().isEmpty()){
                AutoDot dot = findNamedDot(actionObj.getReturnRef(), route.getName());
                TargetReference tr = createTargetReference( dot, actionObj);
                if (tr != null){
                    refs.add(tr);
                }
            }
        }
        Collections.sort(refs);
        return refs;
    }

    private TargetReference createTargetReference(AutoDot dot, BotActionObj actionObj){
        TargetReference tr = null;
        if (dot != null){
            tr = new TargetReference(dot);
            tr.setDotName(actionObj.getMethodName());
            tr.setDescription(String.format("Result of %s (%s)", actionObj.getDescription(), dot.toString()));
        }
        return tr;
    }

    public ArrayList<AutoRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<AutoRoute> routes) {
        this.routes = routes;
    }

    public ArrayList<BotActionObj> getBotActions() {
        return botActions;
    }

    public void setBotActions(ArrayList<BotActionObj> botActions) {
        this.botActions = botActions;
    }

    public AutoRoute initRoute(){
        AutoRoute route = new AutoRoute();
        route.setName(AutoRoute.NAME_BLUE);
        route.setNameIndex(getMinAvailableIndex(AutoRoute.NAME_BLUE));
        return route;
    }

    public AutoDot initDot(){
        AutoDot dot = new AutoDot();
        int asciiCode = AutoDot.asciiA;
        for(int i = 0; i < this.namedDots.size(); i++){
            AutoDot d = this.namedDots.get(i);
            int ascii = d.getDotName().charAt(0);
            if (ascii != asciiCode){
                break;
            }
            asciiCode++;
        }
        // in an unlikely scenario that we ran out of letters, overwrite Z
        if (asciiCode > AutoDot.asciiZ){
            asciiCode = AutoDot.asciiZ;
        }
        dot.setDotName(Character.toString((char)asciiCode));
        return dot;
    }

    public AutoRoute cloneRoute(AutoRoute route){
        int nextIndex = getMinAvailableIndex(route.getName());
        AutoRoute clone = route.clone();
        clone.setNameIndex(nextIndex);
        return clone;
    }

    public AutoRoute mirrorRoute(AutoRoute route){
        String name = route.getName();
        if(name.equals(AutoRoute.NAME_RED)){
            name = AutoRoute.NAME_BLUE;
        }
        else{
            name = AutoRoute.NAME_RED;
        }
        int nextIndex = getMinAvailableIndex(name);
        AutoRoute clone = route.clone();
        clone.setName(name);
        clone.setNameIndex(nextIndex);
        clone.setStartY(getMirrorY(clone.getStartY()));
        clone.setInitRotation((int)getMirrorDegree(clone.getInitRotation()));
        for(AutoStep step : clone.getSteps()){
            step.setTargetY(getMirrorY(step.getTargetY()));
//            if (step.getMoveStrategy().equals(MoveStrategy.Spin)){
//                step.setDesiredHead(getMirrorDegree(step.getDesiredHead()));
//            }
            if (step.getRobotDirection().equals(RobotDirection.Left)){
                step.setRobotDirection(RobotDirection.Right);
            }
            if (step.getRobotDirection().equals(RobotDirection.Right)){
                step.setRobotDirection(RobotDirection.Left);
            }
        }
        return clone;
    }

    protected int getMirrorY(int targetY){
        int MAX = 144;
        int mirrorY = MAX - targetY;
        return mirrorY;
    }

    protected double getMirrorDegree(double heading){
        double mirror = (heading + 180) % 360;
        return mirror;
    }

    public void addRoute(AutoRoute route){
        this.routes.add(route);
        Collections.sort(this.routes);
        fireUpdateEvent(route);
    }

    public void updateRoute(AutoRoute route){
        fireUpdateEvent(route);
    }

    private void fireUpdateEvent(AutoRoute route){
        if (routesChangeListener != null){
            int index = 0;
            if (route != null) {
                for (int i = 0; i < this.routes.size(); i++) {
                    AutoRoute r = this.routes.get(i);
                    if (route.equals(r)) {
                        index = i;
                        break;
                    }
                }
            }
            routesChangeListener.onRoutesUpdated(index);
        }
    }

    public int getMinAvailableIndex(String name){
        int i = 1;
        for(int x = 0; x < this.routes.size(); x++){
            AutoRoute r = this.routes.get(x);
            if (r.getName().equals(name)
                    && r.getNameIndex() == i){
                i++;
            }
            else{
                break;
            }
        }
        return i;
    }

    public void deleteRoute(AutoRoute route) throws Exception{
        for(AutoRoute r : routes){
            if(r.getRouteName().equals(route.getRouteName())){
                routes.remove(r);
                FileLoader.deleteRouteFile(route.getRouteName());
                fireUpdateEvent(null);
                break;
            }
        }
    }

    public void deleteDot(AutoDot dot) throws Exception{
        for(AutoDot d : namedDots){
            if(d.getDotName().equals(dot.getDotName())){
                namedDots.remove(d);
                FileLoader.deleteDotFile(dot.getDotName(), dot.getFieldSide());
                break;
            }
        }
    }

    public void setRoutesChangeListener(RoutesChangeListener routesChangeListener) {
        this.routesChangeListener = routesChangeListener;
    }

    public AutoStep initStep (AutoRoute route, AutoDot selectedDot, int addIndex, String condition){
        AutoStep step = new AutoStep();
        step.setConditionValue(condition);
        if (!condition.isEmpty()){
            step.setConditionFunction(CONDITION_FUNCTION);
        }
        if (selectedDot != null){
            step.setTargetX(selectedDot.getX());
            step.setTargetY(selectedDot.getY());
        }
        else {
            step.setTargetX(route.getStartX());
            step.setTargetY(route.getStartY());
            if (route.getSteps().size() > 0) {
                if (addIndex < 0 || addIndex >= route.getSteps().size()){
                    addIndex = route.getSteps().size() - 1;
                }
                AutoStep last = route.getSteps().get(addIndex);
                step.setTargetX(last.getTargetX());
                step.setTargetY(last.getTargetY());
            }
        }
        return step;
    }

    public void addStep(AutoRoute route, AutoStep newStep, int index, String condition){
        reconcileStep(newStep, route);
        int realIndex = 0;
        if (index - 1 >= 0){
            AutoStep prevStep = route.getVisibleSteps().get(index - 1);
            if (prevStep != null){
                realIndex = prevStep.getOriginalIndex() + 1;
            }
        }
        else{
            if (route.getVisibleSteps().size() > index + 1) {
                AutoStep nextStep = route.getVisibleSteps().get(index + 1);
                if (nextStep != null) {
                    realIndex = nextStep.getOriginalIndex() - 1;
                }
            }
        }
        newStep.setOriginalIndex(realIndex);

        if (realIndex > route.getSteps().size()){
            route.getSteps().add(newStep);
        }
        else{
            route.getSteps().add(realIndex, newStep);
        }

        // or updateVisibleSteps on the route
        if (index >= route.getVisibleSteps().size()){
            route.getVisibleSteps().add(newStep);
        }
        else {
            route.getVisibleSteps().add(index, newStep);
        }
    }

    public void notifyStepUpdate(AutoRoute route, AutoStep step){
        if (this.routesChangeListener != null){
            this.routesChangeListener.onStepAdded(route, step);
        }
    }

    public void deleteRouteStep(AutoRoute route, AutoStep step) throws Exception{
        route.getSteps().remove(step);
        route.getVisibleSteps().remove(step);
        FileLoader.saveRoute(route);
        if (this.routesChangeListener != null){
            this.routesChangeListener.onStepDeleted(route, step);
        }
    }

    public void addDot(AutoDot dot){
        this.namedDots.add(dot);
        Collections.sort(this.namedDots);
    }

    public void updateDot(AutoDot dot){
        if (dotsChangeListener != null){
            dotsChangeListener.onDotUpdated(dot);
        }
    }

    public boolean hasConditions(AutoRoute route){
        boolean hasConditions = false;
        for (AutoStep step : route.getSteps()){
            if (step.hasCondition()){
                hasConditions = true;
                break;
            }
        }
        return hasConditions;
    }

    public void setDotsChangeListener(DotsChangeListener dotsChangeListener) {
        this.dotsChangeListener = dotsChangeListener;
    }
}
