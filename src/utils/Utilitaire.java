package src.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import src.annotation.Controller;
import src.annotation.UrlMapping;
import src.exception.MethodNotFoundException;
import src.exception.UrlException;

public class Utilitaire {

    public static List<Class<?>> getClasses(String packageName)
            throws ClassNotFoundException, MethodNotFoundException, IOException {
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

    public static List<Class<?>> listeController(String packagee)
            throws ClassNotFoundException, MethodNotFoundException, IOException {
        List<Class<?>> listeClasses = Utilitaire.getClasses(packagee);
        List<Class<?>> listeClassesController = new ArrayList<>();

        for (Class<?> classz : listeClasses) {
            if (classz.isAnnotationPresent(Controller.class)) {
                listeClassesController.add(classz);
            }
        }
        return listeClassesController;
    }


    public static HashMap<UrlMethod, RouteMapping> listeFunctionController(String packagee)
            throws ClassNotFoundException, MethodNotFoundException, IOException, UrlException {

        HashMap<UrlMethod, RouteMapping> urlFunction = new HashMap<>();
        List<Class<?>> listeClassesController = Utilitaire.listeController(packagee);

        for (Class<?> classz : listeClassesController) {
            Method[] listeMethods = classz.getDeclaredMethods();
            for (Method m : listeMethods) {
                if (m.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping uM = m.getAnnotation(UrlMapping.class);
                    RouteMapping routeMapping = new RouteMapping(m, classz);
                    UrlMethod urlMethod = new UrlMethod(uM.url(), uM.methodHttp());
                    System.out.println(urlMethod);
                    if(urlFunction.containsKey(urlMethod)){
                        throw new RuntimeException("L'url "+urlMethod.getUrl()+" / "+urlMethod.getMethodHttp()+" est deja present");
                    } 
                    urlFunction.put(urlMethod, routeMapping);
                }
            }
        }
        return urlFunction;
    }

    public void inVokeMethod(RouteMapping routeMapping) throws NoSuchMethodException{
        Class<?> classe = routeMapping.getClassz();
        Constructor<?> c = classe.getDeclaredConstructor();
        Method m = routeMapping.getMethod();
        try {
            m.invoke(c.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // public static UrlMethod gMethodHttpByUrl(String url, HashMap<UrlMethod, RouteMapping> urlMethodList )
    //         throws Exception {
    //     for (UrlMethod key : urlMethodList.keySet()) {
    //         if (key.getUrl().equals(url)) {
    //             return key;
    //         } else {
    //             throw new Exception();
    //         }
    //     }
    //     return null; 
    // }
}
