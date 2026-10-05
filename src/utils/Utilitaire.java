package src.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.annotation.ApiRest;
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
            // Décodage du chemin pour gérer les espaces et caractères spéciaux
            String decodedPath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
            File directory = new File(decodedPath);
            
            if (directory.exists()) {
                String[] files = directory.list();
                if (files != null) {
                    for (String file : files) {
                        File child = new File(directory, file);
                        if (child.isDirectory()) {
                            classes.addAll(getClasses(packageName + '.' + file));
                        } else if (file.endsWith(".class") && !file.contains("$")) {
                            String className = packageName + '.' + file.substring(0, file.length() - 6);
                            classes.add(Class.forName(className));
                        }
                    }
                }
            }
        }
        return classes;
    }

    public static void listeController(List<Class<?>> listeClassesController, String packagee)
            throws ClassNotFoundException, MethodNotFoundException, IOException {
        List<Class<?>> listeClasses = Utilitaire.getClasses(packagee);

        for (Class<?> classz : listeClasses) {
            if (classz.isAnnotationPresent(Controller.class)) {
                listeClassesController.add(classz);
            }
        }
    }

    public static void listeFunctionController(HashMap<UrlMethod, RouteMapping> urlFunction,
            List<Class<?>> listeClassesController, String packagee)
            throws ClassNotFoundException, MethodNotFoundException, IOException, UrlException {
        for (Class<?> classz : listeClassesController) {
            Method[] listeMethods = classz.getDeclaredMethods();
            for (Method m : listeMethods) {
                if (m.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping uM = m.getAnnotation(UrlMapping.class);
                    RouteMapping routeMapping = new RouteMapping(m, classz);
                    UrlMethod urlMethod = new UrlMethod(uM.url(), uM.methodHttp());
                    
                    if (urlFunction.containsKey(urlMethod)) {
                        throw new RuntimeException("L'url " + urlMethod.getUrl() + " / " + urlMethod.getMethodHttp()
                                + " est deja presente");
                    }
                    if (m.isAnnotationPresent(ApiRest.class)) {
                        routeMapping.setApi(true);
                    }

                    urlFunction.put(urlMethod, routeMapping);
                }
            }
        }
    }

    public static void inVokeMethod(RouteMapping routeMapping, HttpServletRequest req, HttpServletResponse res,
            PrintWriter out) throws Exception {
        Class<?> classe = routeMapping.getClassz();
        Constructor<?> c = classe.getDeclaredConstructor();
        Method m = routeMapping.getMethod();
        Object objet = c.newInstance();
        Object o = m.invoke(objet);

        if (o instanceof ModelAndView mv) {
            // 1. Attribuer toutes les variables à la requête
            if (mv.getValue() != null) {
                mv.getValue().forEach(req::setAttribute);
            }
            
            // 2. Effectuer le forward UNE SEULE FOIS après la boucle
            String viewPath = GlobalVariable.getPrefix() + mv.getView() + GlobalVariable.getSuffix();
            RequestDispatcher dispat = req.getRequestDispatcher(viewPath);
            dispat.forward(req, res);
        }
    }

    public static void inVokeMethodJson(RouteMapping routeMapping, HttpServletRequest req, HttpServletResponse res)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, IOException, ServletException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        Class<?> classe = routeMapping.getClassz();
        Constructor<?> c = classe.getDeclaredConstructor();
        Method m = routeMapping.getMethod();
        Object objet = c.newInstance();
        Object o = m.invoke(objet);

        Gson gson = new Gson();
        String jsonString = gson.toJson(o);

        res.getWriter().write(jsonString);
    }
}