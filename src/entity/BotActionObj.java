package entity;

import com.google.gson.Gson;

public class BotActionObj {
    private String methodName;
    private String description;
    private boolean isGeo;


    @Override
    public String toString() {
        return getDescription();
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
}
