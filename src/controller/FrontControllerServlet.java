package src.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.annotation.UrlMapping;
import src.exception.MethodNotFoundException;
import src.exception.UrlException;
import src.utils.MethodHttp;
import src.utils.RouteMapping;
import src.utils.UrlMethod;
import src.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<Class<?>> listeClassesController;
    private HashMap<UrlMethod, RouteMapping> listeMethodClass;
    private String packagee;

    @Override
    public void init() {
        try {
            packagee = this.getInitParameter("PackageInit");
            listeClassesController = Utilitaire.listeController(packagee);
            listeMethodClass = Utilitaire.listeFunctionController(packagee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        resp.setContentType("text/plain");
    
        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String route = uri.substring(context.length());

        PrintWriter out = resp.getWriter();
        RouteMapping routeMapping = listeMethodClass.get(new UrlMethod(route, MethodHttp.GET));
        
        if (routeMapping != null) {
            out.println("Url :" + route);
            out.println("Method :" + routeMapping.getMethod().getName());
            out.println("class :" + routeMapping.getClassz().getSimpleName());

        } else {
            out.print("Liste des url disponibles :\n");
            listeMethodClass.forEach((cle, valeur) -> {
                out.println("L'url : " + cle.getUrl());
            });
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
