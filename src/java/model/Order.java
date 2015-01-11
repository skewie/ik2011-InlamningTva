/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import DAO.ArticleDAO;
import model.User;
import controller.CartBean;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Jeff
 */
@ManagedBean(name = "order")
@SessionScoped
public class Order implements Serializable{
    
    private String firstName;
    private String lastName;
    private User user;
    private ArrayList<OrderRow> rows;
    private ArticleDAO dao;
    
    public Order(User user, ArrayList<OrderRow> row){
        this.user = user;
        this.rows = row;
    }
    
    public Order(){
        rows = new ArrayList();
    }
    
    public void add(OrderRow row) {
        rows.add(row);
    }
    
    public void remove(int index) {
        rows.remove(index);
    }
    
    public void setUser(User user){
        this.user = user;
    }
    
    public User getUser(){
        return this.user;
    }
    
    /*
    public void setFirstName(User user){
        this.user.setFirstname(user.getFirstname());
    }
    
    public String getFirstName(){
        return this.user.getFirstname();
    }
    
    public void setLastName(User user){
        this.user.setLastname(user.getLastname());
    }
    
    public String getLastName(){
        return this.user.getLastname();
    }*/
    
    public void setOrderRow(ArrayList<OrderRow> rows){
        this.rows = rows;
    }
    
    public ArrayList<OrderRow> getOrderRows(){
        return this.rows;
    }
}
