/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Nikolaj
 */
@WebServlet(urlPatterns = {"/api/quote/*"})
public class QuoteSetvlet extends HttpServlet {

    private Map<Integer, String> quotes = new HashMap() {
        {
            put(1, "Friends are kisses blown to us by angels");
            put(2, "Do not take life too seriously. You will never get out of it alive");
            put(3, "Behind every great man, is a woman rolling her eyes");
        }
    };

    Random rand = new Random();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String[] split = request.getRequestURI().split("/");
            String parameter = split.length == 4 ? split[4] : "all";

            JsonObject quote = new JsonObject();

            if (parameter.equals("random")) {
                int randIndex = rand.nextInt(quotes.size()) + 1;
                quote.addProperty("quote", quotes.get(randIndex));
            } else {
                quote.addProperty("quote", quotes.get(Integer.parseInt(parameter)));
            }

            String jsonResponse = new Gson().toJson(quote);
            out.println(jsonResponse);
        } finally {
            out.close();
        }
    }

    private void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JsonObject newQuote = getJson(request);
               
        int newId = quotes.size() + 1;
        newQuote.addProperty("id", newId);
        
        String quote = newQuote.get("quote").getAsString();
        quotes.put(newId, quote);

        out.println(newQuote);
    }
    
    private void processDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
               
        int id = Integer.parseInt(request.getRequestURI().split("/")[3]);    
        
        JsonObject quote = new JsonObject();
        quote.addProperty("quote", quotes.get(id));
        out.println(quote);
        quotes.remove(id);
    }

    private JsonObject getJson(HttpServletRequest request) throws IOException, JsonSyntaxException {
        Scanner jsonScanner = new Scanner(request.getInputStream());
        String json = "";
        while (jsonScanner.hasNext()) {
            json += jsonScanner.nextLine();
        }
        //Get the quote text from the provided Json
        JsonObject newQuote = new JsonParser().parse(json).getAsJsonObject();
        return newQuote;
    }
    
    private void processPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        PrintWriter out = response.getWriter();
        int id = Integer.parseInt(request.getRequestURI().split("/")[3]);
        
        JsonObject newQuote = getJson(request);
        String quote = newQuote.get("quote").getAsString();
        
        quotes.put(id, quote);
        
        newQuote.addProperty("id", id);
        out.println(newQuote);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPost(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processDelete(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processPut(req, resp);
    }

}
