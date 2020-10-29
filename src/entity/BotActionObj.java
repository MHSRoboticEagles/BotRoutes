package entity;

import java.util.Objects;

public class BotActionObj implements Comparable<BotActionObj> {
    private String methodName;
    private String description;
    private boolean isGeo;
    private String returnRef;


    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotActionObj actionObj = (BotActionObj) o;
        return Objects.equals(methodName, actionObj.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGeo() {
        return isGeo;
    }

    public void setGeo(boolean geo) {
        isGeo = geo;
    }

    public String getReturnRef() {
        return returnRef;
    }

    public void setReturnRef(String returnRef) {
        this.returnRef = returnRef;
    }

    @Override
    public int compareTo(BotActionObj o) {
        return this.getDescription().compareTo(o.getDescription());
    }
}
