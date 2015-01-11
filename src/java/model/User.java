/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author h11tobbu
 */
@Named("user")
@SessionScoped
public class User implements Serializable {
    
    private int uid;
    private boolean loggedIn = false;
    private String username, password, firstname, 
            lastname, signature, email;
    
    public User(){
        //tom konst.
    }
    
    public void clear(){
        this.setID(0);
        this.setUsername(null);
        this.setPassword(null);
        this.setFirstname(null);
        this.setLastname(null);
        this.setEmail(null);
        this.setLoggedIn(false);
    }
    
    public void setID(int uid){
        this.uid = uid;
    }
    
    public int getID(){
        return this.uid;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public String getPassword(){
        return this.password;
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
    
    public void setSignature(String signature){
        this.signature = signature;
    }
    
    public String getSignature(){
        return this.signature;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public void setLoggedIn(boolean state){
        this.loggedIn = state;
    }
    
    public boolean getLoggedIn(){
        return this.loggedIn;
    }
    
}
