package src.utils;

import java.lang.reflect.Method;
import java.net.URL;

public class RouteMapping {
    private Method method;
    private Class<?> classz;

    public RouteMapping(Method method, Class<?> classz) {
        this.method = method;
        this.classz = classz;
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
}
