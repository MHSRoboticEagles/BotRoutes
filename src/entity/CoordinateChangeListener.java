package entity;

public interface CoordinateChangeListener {
    void onCoordinateChanged(AutoDot dot,  String description);
    void onCoordinatePicked(AutoDot selected);
}
