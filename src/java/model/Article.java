/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import util.messages.ErrorMessage;
import util.messages.MessageHandler;

/**
 *
 * @author Jeff
 */
@ManagedBean
public class Article implements Serializable {

    private int articleId;
    private int catId;
    private String name;
    private double price;
    private String publishedYear;
    private String imageUrl;
    
    private boolean editable;
    
    public Article() {
        editable = false;
    }

    public Article(int articleId, int catId, String name, double price) {
        this.articleId = articleId;
        this.catId = catId;
        this.name = name;
        this.price = price;
        editable = false;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
    
    public void setCatId(int catId){
        this.catId = catId;
    }
    
    public int getCatId(){
        return this.catId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void validateName(FacesContext context, UIComponent uic, Object value) {
        String val = prepareStringValue(value);
        performBasicValidation(context, uic, val);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public void validatePrice(FacesContext context, UIComponent uic, Object value) {
        if (value == null)
            return;
        
        try {
            if ((Double)value < 0)
                MessageHandler.throwErrorMessage(context, ErrorMessage.NEGATIVE_NUMBER);
        } catch (NumberFormatException nfe) {
            MessageHandler.throwErrorMessage(context, ErrorMessage.NOT_A_NUMBER);
        }
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(String publishedYear) {
        this.publishedYear = publishedYear;
    }
    
    public void validatePublishedYear(FacesContext context, UIComponent uic, Object value) {
        String val = prepareStringValue(value);
        performBasicValidation(context, uic, value);
        
        try {
            Integer.parseInt(val);
        } catch(NumberFormatException nfe) {
            MessageHandler.throwErrorMessage(context, ErrorMessage.NOT_A_YEAR);
        }
        
        if (val.length() != 4)
            MessageHandler.throwErrorMessage(context, ErrorMessage.NOT_A_YEAR);
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void validateImageUrl(FacesContext context, UIComponent uic, Object value) {
    }
    
    private void performBasicValidation(FacesContext context, UIComponent uic, Object value) {
        if (value.equals(""))
            MessageHandler.throwErrorMessage(context, ErrorMessage.EMPTY_FIELD);
    }
    
    private String prepareStringValue(Object value) {
        String val = (String)value;
        return val.trim();
    }
}
