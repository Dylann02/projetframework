package src.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.exception.MethodNotFoundException;
import src.utils.Utilitaire;

public class FrontControllerServlet extends HttpServlet {
    private List<Class<?>> listeClassesController;
    private HashMap<String, Method> listeMethodClass;
    private String packagee;

    @Override
    public void init() {
        try {
            packagee = "test";
            listeClassesController = Utilitaire.listeController(packagee);
            listeMethodClass = Utilitaire.listeFunctionController(packagee);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MethodNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ClassNotFoundException {
        String url = req.getRequestURI();
        // String url = req.getContextPath();

        PrintWriter out = resp.getWriter();
        for (Class<?> c : listeClassesController) {
            out.println(c.getName());
        }

        String[] concat = url.split("/");
        StringBuilder urlPattern = new StringBuilder();
        
        if (concat.length >= 2) {
            for (int i = 2; i < concat.length; i++) {
                urlPattern.append("/");
                urlPattern.append(concat[i]);
            }
            // out.print(urlPattern);
            Method method = listeMethodClass.get(urlPattern.toString());
            
            if(method != null){
                out.println("Le package de la method :"+packagee);
                out.println("Method :"+method.getName());

            } else {
                listeMethodClass.forEach((cle, valeur) -> {
        
                    // out.println("Voila la liste des url disponibles :");
                    out.println(cle + " / " + valeur);

            });
            }
                // throw new MethodNotFoundException(listeMethodClass);



            // listeMethodClass.forEach((cle, valeur) -> {
            //     try {
            //         if (cle.equals(urlPattern)) {
            //             out.println(cle + " / " + valeur);
            //         } else {
            //             throw new MethodNotFoundException(listeMethodClass);
            //         }
            //     } catch (MethodNotFoundException e) {
            //         out.println(e.getMessage());
            //     }
            // });
        }


        // out.println(url);
        // out.println(urlPattern);
        // out.println(concat[1]);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processRequest(req, resp);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            processRequest(req, resp);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
