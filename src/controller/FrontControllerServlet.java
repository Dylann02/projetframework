package src.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.annotation.UrlMapping;
import src.exception.MethodNotFoundException;
import src.exception.UrlException;
import src.utils.Dependance;
import src.utils.MethodHttp;
import src.utils.RouteMapping;
import src.utils.UrlMethod;
import src.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<Class<?>> listeClassesController = new ArrayList<>();
    private HashMap<UrlMethod, RouteMapping> listeMethodClass = new HashMap<>();
    private Dependance dependance;
    
    @Override
    public void init() throws ServletException {
        try {
            // packagee = this.getInitParameter("PackageInit");
            // Utilitaire.listeController(listeClassesController,packagee);
            // Utilitaire.listeFunctionController(listeMethodClass,listeClassesController,packagee);
            ServletContext context = getServletContext();
            dependance = (Dependance)context.getAttribute("dependance");
            
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        resp.setContentType("text/plain");

        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String route = uri.substring(context.length());

        PrintWriter out = resp.getWriter();
        RouteMapping routeMapping = dependance.getListeMethodClass().get(new UrlMethod(route, MethodHttp.GET));

        if (routeMapping != null) {
            out.println("Url :" + route);
            out.println("Method :" + routeMapping.getMethod().getName());
            out.println("class :" + routeMapping.getClassz().getSimpleName());
            Utilitaire.inVokeMethod(routeMapping ,req,resp);
        } else {
            out.print("Liste des url disponibles :\n");
            dependance.getListeMethodClass().forEach((cle, valeur) -> {
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
