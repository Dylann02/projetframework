package src.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import src.annotation.Controller;

public class Utilitaire {
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        List<Class<?>> classes = new ArrayList<>();
        
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static boolean verifController(Class<?> c){
        if(c.isAnnotationPresent(Controller.class)){
            return true;
        }
        return false;
    }
}
