/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Toppe
 */
public class AdminOrders implements Serializable{
    
    private String firstname, lastname, name, date;
    private int amount;
    private double price;
    
    public AdminOrders(String firstname, String lastname, int amount, 
            String name, double price){
        this.firstname = firstname;
        this.lastname = lastname;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }
    
    public AdminOrders(){
        
    }
    
    public void setFirstname(String firstname){
        this.firstname = firstname;
    }
    
    public String getFirstname(){
        return this.firstname;
    }
    
    public void setLastname(String lastname){
        this.lastname = lastname;
    }
    
    public String getLastname(){
        return this.lastname;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setAmount(int amount){
        this.amount = amount;
    }
    
    public int getAmount(){
        return this.amount;
    }
    
    public void setPrice(double price){
        this.price = price;
    }
    
    public double getPrice(){
        return this.price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        try {
        Date dateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(this.date);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateObj);
        } catch(ParseException e) {
            return this.date;
        }
    }
}
