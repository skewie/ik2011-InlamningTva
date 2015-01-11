/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAO.UserDAO;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import model.User;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import model.AdminOrders;
import util.messages.ErrorMessage;
import util.messages.MessageHandler;

/**
 *
 * @author h11tobbu
 */
@SessionScoped
@Named("userBean")
public class UserBean implements Serializable {

    private User user;

    public UserBean() {
        user = new User();
    }

    public String login(User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                MessageHandler.throwErrorMessage(FacesContext.getCurrentInstance(), ErrorMessage.EMPTY_FIELD);
            } else {
                this.user = UserDAO.getInstance().login(user);
                if (this.user.getID() != 0) {
                    this.user.setLoggedIn(true);
                } else {
                    this.user.setLoggedIn(false);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public String logout() {
        try {
            this.user.clear();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return "./index.xhtml";
    }

    public String createUser(User user) {
        try {
            System.out.println("Creating...");
            //skapar användare
            UserDAO.getInstance().createUser(user);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public User getUser() {
        return this.user;
    }

    //Osäker kring denna!!!
    public void setSignature(String signature) {
        user.setSignature(signature);
    }

    /**
     * Metod som kollar om användaren är inloggad
     *
     * @return skickar tillbaka boolean true om User inte är null.
     */
    public boolean isUserLoggedIn() {
        return this.user.getLoggedIn();
    }

    public ArrayList<AdminOrders> getUserOrders() {
        ArrayList<AdminOrders> list = new ArrayList<>();
        if (this.isUserLoggedIn() == true) {
            try {
                list = UserDAO.getInstance().getUserOrders(this.user);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return list;
    }
}
