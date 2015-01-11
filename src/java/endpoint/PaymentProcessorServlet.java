/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import DAO.ArticleDAO;
import DAO.PayPalDAO;
import com.paypal.core.rest.PayPalRESTException;
import controller.CartBean;
import controller.EmailDistributor;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Order;

/**
 *
 * @author Jeff
 */
public class PaymentProcessorServlet extends HttpServlet {

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

        boolean approved = request.getParameter("approved").equals("true");
        int orderId = Integer.parseInt(request.getParameter("order_id"));

        String paymentId = request.getParameter("paymentId");
        String payerId = request.getParameter("PayerID");
        try {
            PayPalDAO paypalDAO = new PayPalDAO();

            paypalDAO.processPayment(paymentId, payerId);

            try {
                ArticleDAO artDAO = ArticleDAO.getInstance();
                // Eftersom att vi inte behåller sessionen hämtar vi ordern vi skapat.
                Order order = artDAO.getOrderById(orderId);
                EmailDistributor.sendConfirmationMail(new CartBean(order), order.getUser()); // Object som skickar mail bekräftelsemailet.
                response.sendRedirect("/Bradspelsshopen/?order_complete=true");
            } catch (Exception e) {
                printException(response, e);
                //response.sendRedirect("/Bradspelsshopen/?error=true&message=" + e.getMessage());
            }
        } catch (PayPalRESTException ex) {
            try {
                ArticleDAO artDAO = ArticleDAO.getInstance();
                artDAO.removeOrderById(orderId); // om något går fel tar vi bort ordern.
                //response.sendRedirect("/Bradspelsshopen/?error=true&message=" + ex.getMessage());
                printException(response, new Exception("approved=" + approved + " order_id=" + orderId + " PayerID=" + payerId + " paymentId=" + paymentId));
                printException(response, ex);
            } catch (SQLException ex1) {
                //Logger.getLogger(PaymentProcessorServlet.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        printException(response, new Exception("approved=" + approved + " order_id=" + orderId + " PayerID=" + payerId + " paymentId=" + paymentId));
    }

    private void printException(HttpServletResponse response, Exception e) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println("<h1>Oh noes! Nånting gick fel!</h1><br /><br />");
            out.println("<b>Typ:</b><br>");
            out.println(e.getClass() + " - " + e.getMessage() + "<br><br>");
            out.println("<b>StackTrace:</b><br>");
            for (StackTraceElement ste : e.getStackTrace()) {
                out.println(ste.toString() + "<br>");
            }
        } catch (IOException ex) {
            Logger.getLogger(PaymentProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        }   
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
        processRequest(request, response);
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

}
