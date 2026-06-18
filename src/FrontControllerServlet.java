package src;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet{
    public void processRequest(HttpServletRequest req , HttpServletResponse resp) throws IOException{
        String url = req.getRequestURI();
        PrintWriter out = resp.getWriter();
        out.println(url);
    }
    
    @Override
    protected void doGet(HttpServletRequest req , HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req , HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }
}
