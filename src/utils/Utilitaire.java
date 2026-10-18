package src.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
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
            Class<?> type = parametres[i].getType();

            System.out.println(
                    "set" + Utilitaire.capitalize(parameterName));

            if (type == MapFile.class) {
                Part part = req.getPart(parameterName);
                args[i] = Utilitaire.bindFile(part);
                continue;
            }

            if (type == byte[].class) {
                Part part = req.getPart(parameterName);

                if (part == null
                        || part.getSubmittedFileName() == null
                        || part.getSubmittedFileName().isEmpty()
                        || part.getSize() == 0) {
                    args[i] = null;
                } else {
                    args[i] = Utilitaire.lireOctets(part);
                }

                continue;
            }

            if (parametres[i].isAnnotationPresent(
                    ModelAtttribute.class)) {

                Object model = type.getDeclaredConstructor().newInstance();

                for (Field field : type.getDeclaredFields()) {

                    String nomChamp = field.getName();
                    Class<?> typeChamp = field.getType();
                    String nomSetter = "set" + Utilitaire.capitalize(nomChamp);

                    Object valeurConvertie;

                    if (typeChamp == MapFile.class) {
                        Part part = req.getPart(nomChamp);
                        valeurConvertie = Utilitaire.bindFile(part);
                    }

                    else if (typeChamp == byte[].class) {
                        Part part = req.getPart(nomChamp);

                        if (part == null
                                || part.getSubmittedFileName() == null
                                || part.getSubmittedFileName().isEmpty()
                                || part.getSize() == 0) {
                            valeurConvertie = null;
                        } else {
                            valeurConvertie = Utilitaire.lireOctets(part);
                        }
                    }

                    else if (Utilitaire.estListeMapFile(field)) {
                        valeurConvertie = Utilitaire.bindFiles(req, nomChamp);
                    }

                    else {
                        String valeur = req.getParameter(nomChamp);

                        if (valeur == null) {
                            continue;
                        }

                        valeurConvertie = Utilitaire.bindObject(typeChamp, valeur);
                    }

                    Method setter = type.getMethod(
                            nomSetter, typeChamp);

                    setter.invoke(model, valeurConvertie);
                }

                args[i] = model;

            } else {
                String value = req.getParameter(parameterName);

                if (value == null) {
                    throw new IllegalArgumentException(
                            "Paramètre manquant : " + parameterName);
                }

                Utilitaire.bindParam(type, args, value, i);
            }
        }

        Object result = methode.invoke(objet, args);

        if (result instanceof ModelAndView) {

            ModelAndView mv = (ModelAndView) result;

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

    public static Object bindObject(Class<?> typeChamp, String valeur) {
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
        return valeurConvertie;
    }

    public static void bindParam(Class<?> type, Object[] args, String value, int i) {
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

    public static MapFile bindFile(Part part) throws IOException {

        if (part == null
                || part.getSize() == 0
                || part.getSubmittedFileName() == null
                || part.getSubmittedFileName().isEmpty()) {
            return null;
        }

        String nom = Paths.get(part.getSubmittedFileName())
                .getFileName().toString();

        int index = nom.lastIndexOf('.');
        String extension = index >= 0
                ? nom.substring(index + 1)
                : "";

        byte[] contenu = lireOctets(part);

        return new MapFile(nom, extension, contenu);
    }

    public static byte[] lireOctets(Part part) throws IOException {

        try (InputStream in = part.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int longueur;

            while ((longueur = in.read(buffer)) != -1) {
                out.write(buffer, 0, longueur);
            }

            return out.toByteArray();
        }
    }

    public static String sauvegarderFichier(
            MapFile fichier, String uploadDir) throws IOException {

        if (fichier == null || fichier.getFichier() == null) {
            return null;
        }

        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Le paramètre uploadDir n'est pas configuré.");
        }

        Path dossier = Paths.get(uploadDir);
        Files.createDirectories(dossier);

        String extension = fichier.getExtension();
        String nomStockage = UUID.randomUUID().toString();

        if (extension != null && !extension.isEmpty()) {
            nomStockage += "." + extension;
        }

        Path destination = dossier.resolve(nomStockage);
        Files.write(destination, fichier.getFichier());

        return nomStockage;
    }

    public static List<MapFile> bindFiles(
            HttpServletRequest req, String nomChamp)
            throws IOException, ServletException {

        List<MapFile> fichiers = new ArrayList<MapFile>();

        for (Part part : req.getParts()) {
            if (nomChamp.equals(part.getName())) {
                MapFile fichier = bindFile(part);

                if (fichier != null) {
                    fichiers.add(fichier);
                }
            }
        }

        return fichiers;
    }

    public static boolean estListeMapFile(Field field) {

        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        Type typeGenerique = field.getGenericType();

        if (!(typeGenerique instanceof ParameterizedType)) {
            return false;
        }

        ParameterizedType param = (ParameterizedType) typeGenerique;

        Type[] arguments = param.getActualTypeArguments();

        return arguments.length == 1
                && arguments[0] == MapFile.class;
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