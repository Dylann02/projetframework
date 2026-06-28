package src.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import src.annotation.Controller;
import src.annotation.UrlMapping;
import src.exception.MethodNotFoundException;

public class Utilitaire {
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, MethodNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        List<Class<?>> classes = new ArrayList<>();
        
        Enumeration<java.net.URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            java.net.URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                String[] files = directory.list();
                if (files != null) {
                    for (String file : files) {
                        if (file.endsWith(".class")) {
                            String className = packageName + '.' + file.substring(0, file.length() - 6);
                            classes.add(Class.forName(className));
                        }
                    }
                }
            }
        }
        return classes;
    }

    public static List<Class<?>> listeController(String packagee) throws ClassNotFoundException, MethodNotFoundException, IOException{
        List<Class<?>> listeClasses = Utilitaire.getClasses(packagee);
        List<Class<?>> listeClassesController = new ArrayList<>();

            for (Class<?> classz : listeClasses) {
                if (classz.isAnnotationPresent(Controller.class)) {
                    listeClassesController.add(classz);
                }
            }
        return listeClassesController;
    }

    public static HashMap<String , Method> listeFunctionController(String packagee ) throws ClassNotFoundException, MethodNotFoundException, IOException{
        HashMap<String , Method> urlFunction = new HashMap<>();
        List<Class<?>> listeClassesController = Utilitaire.listeController(packagee);

        for(Class<?> classz : listeClassesController){
            Method[] listeMethods = classz.getDeclaredMethods();
            for(Method m : listeMethods){
                if(m.isAnnotationPresent(UrlMapping.class) ){
                    UrlMapping uM = m.getAnnotation(UrlMapping.class);
                    urlFunction.put(uM.url(), m);
                }
            }
        }
        return urlFunction;
    }

    // public static HashMap<String , >
}
