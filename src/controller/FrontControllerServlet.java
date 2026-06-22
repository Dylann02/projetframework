package src.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<Class<?>> listeClassesController;

    public void init() {
        listeClassesController = new ArrayList<>();
        try {
            List<Class<?>> listeClasses = Utilitaire.getClasses("test");
            for (Class<?> c : listeClasses) {
                if (Utilitaire.verifController(c)) {
                    listeClassesController.add(c);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getRequestURI();
        PrintWriter out = resp.getWriter();
        for (Class<?> c : listeClassesController) {
            out.println(c.getName());
        }
        out.println(url);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }
}
