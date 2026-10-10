package src.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import java.lang.reflect.Field;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.annotation.ApiRest;
import src.annotation.Controller;
import src.annotation.ModelAtttribute;
import src.annotation.UrlMapping;
import src.exception.MethodNotFoundException;
import src.exception.UrlException;

public class Utilitaire {
    public static String capitalize(String texte) {
        if (texte == null || texte.isEmpty()) {
            return texte;
        }

        return texte.substring(0, 1).toUpperCase()
                + texte.substring(1);
    }

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

    public static void inVokeMethod(
            RouteMapping routeMapping,
            HttpServletRequest req,
            HttpServletResponse res,
            PrintWriter out) throws Exception {

        Class<?> classe = routeMapping.getClassz();
        Constructor<?> constructeur = classe.getDeclaredConstructor();
        Method methode = routeMapping.getMethod();
        Object objet = constructeur.newInstance();

        Parameter[] parametres = methode.getParameters();
        Object[] args = new Object[parametres.length];

        for (int i = 0; i < parametres.length; i++) {
            String parameterName = parametres[i].getName();
            String value = req.getParameter(parameterName);
            Class<?> type = parametres[i].getType();
            System.out.println("set" + Utilitaire.capitalize(parameterName));

            if (parametres[i].isAnnotationPresent(ModelAtttribute.class)) {
                Object model = type.getDeclaredConstructor().newInstance();

                for (Field field : type.getDeclaredFields()) {
                    String valeur = req.getParameter(field.getName());
                    if (valeur == null) {
                        continue;
                    }

                    String nomSetter = "set"+ Utilitaire.capitalize(field.getName());
                    Class<?> typeChamp = field.getType();
                    
                    Method setter = type.getMethod(nomSetter, typeChamp);

                    Object valeurConvertie;
                    if (typeChamp == String.class) {
                        valeurConvertie = valeur;
                    } else if (typeChamp == int.class || typeChamp == Integer.class) {
                        valeurConvertie = Integer.parseInt(valeur);
                    } else if (typeChamp == double.class || typeChamp == Double.class) {
                        valeurConvertie = Double.parseDouble(valeur);
                    } else if (typeChamp == boolean.class || typeChamp == Boolean.class) {
                        valeurConvertie = Boolean.parseBoolean(valeur);
                    } else {
                        throw new IllegalArgumentException(
                                "Type de champ non supporté : " + typeChamp.getName());
                    }
                    setter.invoke(model, valeurConvertie);
                }

                args[i] = model;
            } else {
                if (value == null) {
                    throw new IllegalArgumentException(
                            "Paramètre manquant : " + parameterName);
                }

                if (type == Integer.class || type == int.class) {
                    args[i] = Integer.parseInt(value);
                } else if (type == Double.class || type == double.class) {
                    args[i] = Double.parseDouble(value);
                } else if (type == String.class) {
                    args[i] = value;
                } else {
                    throw new IllegalArgumentException(
                            "Type de paramètre non supporté : " + type.getName());
                }
            }

        }

        Object result = methode.invoke(objet, args);

        if (result instanceof ModelAndView mv) {
            if (mv.getValue() != null) {
                mv.getValue().forEach(req::setAttribute);
            }

            String viewPath = GlobalVariable.getPrefix()
                    + mv.getView()
                    + GlobalVariable.getSuffix();

            RequestDispatcher dispatcher = req.getRequestDispatcher(viewPath);
            dispatcher.forward(req, res);
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