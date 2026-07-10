package src.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dependance {
    private  List<Class<?>> listeClassesController = new ArrayList<>();
    private HashMap<UrlMethod, RouteMapping> listeMethodClass = new HashMap<>();
    
    public List<Class<?>> getListeClassesController() {
        return listeClassesController;
    }
    public void setListeClassesController(List<Class<?>> listeClassesController) {
        this.listeClassesController = listeClassesController;
    }
    public HashMap<UrlMethod, RouteMapping> getListeMethodClass() {
        return listeMethodClass;
    }
    public void setListeMethodClass(HashMap<UrlMethod, RouteMapping> listeMethodClass) {
        this.listeMethodClass = listeMethodClass;
    }
}