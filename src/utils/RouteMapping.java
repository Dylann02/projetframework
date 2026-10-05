package src.utils;

import java.lang.reflect.Method;

public class RouteMapping {
    private Method method;
    private Class<?> classz;
    private boolean isApi;

    public RouteMapping(Method method, Class<?> classz) {
        this.method = method;
        this.classz = classz;
        this.isApi = false;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getClassz() {
        return classz;
    }

    public void setClassz(Class<?> classz) {
        this.classz = classz;
    }
    public boolean isApi() {
        return isApi;
    }

    public void setApi(boolean isApi) {
        this.isApi = isApi;
    }
}
