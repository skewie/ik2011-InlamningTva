/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jeff
 */
public class OrderRow {
    private Article article;
    private int amount;

    public OrderRow() {
    }
    
    public OrderRow(Article article) {
        this.article = article;
        this.amount = 1;
    }
    
    public OrderRow(Article article, int amount) {
        this.article = article;
        this.amount = amount;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getAmount() {
        return amount;
    }
    
    public void increaseAmount() {
        amount++;
    }
    
    public void reduceAmount() {
        amount--;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
