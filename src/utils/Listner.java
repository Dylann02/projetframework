package src.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
@WebListener
public class Listner implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce){
        System.out.println("===== LISTENER EXECUTE =====");
        ServletContext context = sce.getServletContext();
        String packagee = context.getInitParameter("PackageInit");
        String prefix = context.getInitParameter("prefix");
        String suffix = context.getInitParameter("suffix");

        GlobalVariable.setPrefix(prefix);        
        GlobalVariable.setSuffix(suffix);
        Dependance dependance = new Dependance();
        try {
            Utilitaire.listeController(dependance.getListeClassesController(),packagee);
            Utilitaire.listeFunctionController(dependance.getListeMethodClass(),dependance.getListeClassesController(),packagee);
            // System.out.println(dependance);
            System.out.println("Routes enregistrées : " + dependance.getListeMethodClass().keySet());
            context.setAttribute("dependance", dependance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
}   

