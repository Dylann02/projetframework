package src.utils;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public static void listeController(List<Class<?>> listeClassesController,String packagee)
            throws ClassNotFoundException, MethodNotFoundException, IOException {
        List<Class<?>> listeClasses = Utilitaire.getClasses(packagee);

        for (Class<?> classz : listeClasses) {
            if (classz.isAnnotationPresent(Controller.class)) {
                listeClassesController.add(classz);
            }
        }
    }


    public static void listeFunctionController(HashMap<UrlMethod, RouteMapping> urlFunction,List<Class<?>> listeClassesController,String packagee)
            throws ClassNotFoundException, MethodNotFoundException, IOException, UrlException {
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
    }

    
    public static void inVokeMethod(RouteMapping routeMapping ,HttpServletRequest req, HttpServletResponse res) throws Exception{
        Class<?> classe = routeMapping.getClassz();
        Constructor<?> c = classe.getDeclaredConstructor();
        Method m = routeMapping.getMethod();
        Object o =m.invoke(c.newInstance());

        GlobalVariable variable = new GlobalVariable();
        if(o instanceof ModelAndView mv){
            mv.getValue().forEach((key , valeur) -> {
                req.setAttribute(key, o);
            
            StringBuilder path = new StringBuilder();
            path.append(variable.getPrefix());
            path.append(key);
            path.append(variable.getSuffix());
            
            RequestDispatcher dispat = req.getRequestDispatcher(path.toString());
            try {
                dispat.forward(req, res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            });
        }
    }
}
