package logic;

import entity.AutoDot;
import entity.AutoRoute;
import entity.AutoStep;
import entity.BotActionObj;
import io.FileLoader;

import java.util.ArrayList;

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

    public ArrayList<AutoDot> getTargetReferences() {
        ArrayList<AutoDot> refs = new ArrayList<>();
        for (AutoDot d : this.namedDots){
            refs.add(d.clone());
        }
        for(BotActionObj actionObj : this.botActions){
            if (actionObj.isGeo() && !actionObj.getReturnRef().isEmpty()){
                AutoDot dot = findNamedDot(actionObj.getReturnRef());
                if (dot != null){
                    AutoDot clone = dot.clone();
                    clone.setDotName(actionObj.getMethodName());
                    refs.add(clone);
                }
            }
        }
        return namedDots;
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
}
