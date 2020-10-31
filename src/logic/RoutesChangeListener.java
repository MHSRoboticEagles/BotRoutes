package logic;

import entity.AutoRoute;
import entity.AutoStep;

public interface RoutesChangeListener {
    void onRoutesUpdated(int selectedIndex);
    void onStepAdded(AutoRoute route, AutoStep step);
}
