package entity;

import java.util.Objects;

public class TargetReference extends AutoDot {
    private String description = "";

    public TargetReference(){

    }

    public TargetReference(AutoDot model){
        this.setDotName(model.getDotName());
        this.setX(model.getX());
        this.setY(model.getY());
        this.setHeading(model.getHeading());
        this.setSelected(model.isSelected());
        this.setDescription(model.getDotName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String){
            return Objects.equals(getDotName(), o.toString());
        }
        else if (o instanceof TargetReference){
            return Objects.equals(getDotName(), ((TargetReference) o).getDotName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDotName());
    }


    @Override
    public String toString() {
        return getDescription();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
