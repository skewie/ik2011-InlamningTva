/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAO.ArticleDAO;
import DAO.PayPalDAO;
import com.paypal.core.rest.PayPalRESTException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import model.AdminOrders;
import model.Article;
import model.Order;
import model.OrderRow;
import model.User;
import util.messages.ErrorMessage;
import util.messages.MessageHandler;

/**
 *
 * @author Jeff
 */
@ManagedBean(name = "cartBean")
@SessionScoped
public class CartBean implements Serializable {

    private Order order;
    private ArticleDAO dao;

    public CartBean() {
        order = new Order();
        try {
            dao = ArticleDAO.getInstance();
        } catch (Exception e) {
            System.out.println(e.getClass() + " - " + e.getMessage());
            System.out.println("StackTrace:");
            for (StackTraceElement ste : e.getStackTrace()) {
                System.out.println(ste.toString());
            }
        }
    }
    
    public CartBean(Order order) {
        this.order = order;
    }

    /*public CartBean(ArrayList<OrderRow> order.getOrderRows()) {
     this.order.getOrderRows() = order.getOrderRows();
     }*/
    public ArrayList<OrderRow> getCartRows() {
        return order.getOrderRows();
    }

    public void addArticle(Article article) {
        if (inCart(article.getArticleId())) {
            increaseItemAmount(article.getArticleId());
        } else {
            order.add(new OrderRow(article));
        }
    }

    public void removeArticle(Article article) {
        int index = 0;
        if ((index = getArticleIndex(article)) != -1) {
            order.remove(index);
        } else {
            System.out.println("pointing at index: " + index);
        }
    }

    public int getTotalCartRows() {
        return order.getOrderRows().size();
    }

    public boolean inCart(int articleId) {
        for (OrderRow row : order.getOrderRows()) {
            if (row.getArticle().getArticleId() == articleId) {
                return true;
            }
        }

        return false;
    }

    private int getArticleIndex(Article article) {
        for (int i = 0; i < order.getOrderRows().size(); i++) {
            if (order.getOrderRows().get(i).getArticle().getArticleId() == article.getArticleId()) {
                return i;
            }
        }

        return -1;
    }

    public void increaseItemAmount(int articleId) {
        for (OrderRow row : order.getOrderRows()) {
            if (row.getArticle().getArticleId() == articleId) {
                row.increaseAmount();
                break;
            }
        }
    }

    public void reduceItemAmount(int articleId) {
        for (OrderRow row : order.getOrderRows()) {
            if (row.getArticle().getArticleId() == articleId) {
                row.reduceAmount();
                if (row.getAmount() <= 0) {
                    order.getOrderRows().remove(row);
                }
                break;
            }
        }
    }

    public void setRowAmount(Article article, int amount) {
        if (amount <= 0) { // Reduceras det till noll eller mindre tar vi bort varan helt.
            removeArticle(article);
        } else {
            for (OrderRow row : order.getOrderRows()) {
                if (row.getArticle().getArticleId() == article.getArticleId()) {
                    row.setAmount(amount);
                    break;
                }
            }
        }
    }

    public int getCartTotalItems() {
        int tot = 0;

        for (OrderRow row : order.getOrderRows()) {
            tot += row.getAmount();
        }

        return tot;
    }

    public String getCartTotalPrice() {
        double tot = getTotalPrice();
        return NumberFormat.getCurrencyInstance().format(tot);
    }

    public double getTotalPrice() {
        double tot = 0.0;

        for (OrderRow row : order.getOrderRows()) {
            tot += row.getAmount() * row.getArticle().getPrice();
        }

        return tot;
    }

    public boolean isCartEmpty() {
        return order.getOrderRows().isEmpty();
    }

    public String checkOut(User user) {

        this.order.setUser(user);

        PayPalDAO dpp = new PayPalDAO();

        try {
            int orderId = dao.placeOrder(this.order);
            String approveUrl = dpp.getApprovalLink(this, orderId);
            
            this.order = new Order();
            forward(approveUrl);
            
        } catch (PayPalRESTException ppreste) {
        } catch (SQLException ex) {
            //Logger.getLogger(CartBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        // // återställer kundvagnen.
        return "";
    }

    public void forward(String url) {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect(url);
        } catch (IOException ex) {
        }
    }

    public ArrayList<AdminOrders> getAllOrders() {
        return dao.getAllOrders();
    }

    public void validateFirstName(FacesContext context, UIComponent uic, Object value) {
        String val = prepareStringValue(value);
        performBasicValidation(context, uic, value);
    }

    private String prepareStringValue(Object value) {
        String val = (String) value;
        return val.trim();
    }

    private void performBasicValidation(FacesContext context, UIComponent uic, Object value) {
        if (value.equals("")) {
            MessageHandler.throwErrorMessage(context, ErrorMessage.EMPTY_FIELD);
        }
    }
}
