package logic;

import entity.*;
import io.FileLoader;

import java.util.ArrayList;
import java.util.Collections;

public class RouteController {
    private ArrayList<AutoDot> namedDots = new ArrayList<>();
    private ArrayList<AutoRoute> routes = new ArrayList<>();
    private ArrayList<BotActionObj> botActions = new ArrayList<>();

    public RouteController(){

    }

    public void init() throws Exception{
        FileLoader.ensureAppDirectories();
        this.namedDots = FileLoader.listDots();
        this.routes = FileLoader.listRoutes();
        this.botActions = FileLoader.listBotActions();
        Collections.sort(this.routes);
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
            if (!step.getTargetReference().isEmpty()){
                AutoDot dot = findTargetReference(step.getTargetReference());
                if (dot != null){
                    step.setTargetX(dot.getX());
                    step.setTargetY(dot.getY());
                }
            }
        }
    }

    public AutoDot findTargetReference(String name){
        AutoDot dot = findNamedDot(name);
        if (dot == null){
            //try actions
            BotActionObj actionObj = findAction(name);
            if (actionObj != null && !actionObj.getReturnRef().isEmpty()){
                dot = findNamedDot(actionObj.getReturnRef());
            }
        }
        return dot;
    }

    public AutoDot findNamedDot(String name){
        for(AutoDot d : this.namedDots){
            if (d.getDotName().equals(name)){
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

    public ArrayList<AutoDot> getNamedDots() {
        return namedDots;
    }

    public ArrayList<TargetReference> getTargetReferences() {
        ArrayList<TargetReference> refs = new ArrayList<>();
        for (AutoDot d : this.namedDots){
            TargetReference tr = new TargetReference(d);
            refs.add(tr);
        }
        for(BotActionObj actionObj : this.botActions){
            if (actionObj.isGeo() && !actionObj.getReturnRef().isEmpty()){
                AutoDot dot = findNamedDot(actionObj.getReturnRef());
                if (dot != null){
                    TargetReference tr = new TargetReference(dot);
                    tr.setDotName(actionObj.getMethodName());
                    tr.setDescription(String.format("Result of %s (%s)", actionObj.getDescription(), dot.toString()));
                    refs.add(tr);
                }
            }
        }
        Collections.sort(refs);
        return refs;
    }

    public void setNamedDots(ArrayList<AutoDot> namedDots) {
        this.namedDots = namedDots;
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

    public void deleteRoute(AutoRoute route) throws Exception{
        for(AutoRoute r : routes){
            if(r.getRouteName().equals(route.getRouteName())){
                routes.remove(r);
                FileLoader.deleteRouteFile(route.getRouteName());
                break;
            }
        }
    }
}
