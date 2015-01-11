/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAO.ArticleDAO;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.Article;

/**
 *
 * @author Jeff
 */
@ManagedBean(name = "articleBean")
@SessionScoped
public class ArticleBean implements Serializable {
    private ArrayList<Article> articles = null;
    private ArticleDAO dao;
    private ArrayList<Article> searchedArticles;
    private int catId;
    
    /**
     * Creates a new instance of ArticleBean
     */
    public ArticleBean() {
        try {
            dao = ArticleDAO.getInstance();
            searchedArticles = new ArrayList<>();
        } catch (Exception e) {
            System.out.println(e.getClass()+" - "+e.getMessage());
            System.out.println("StackTrace:");
            for (StackTraceElement ste : e.getStackTrace()) {
                System.out.println(ste.toString());
            }
        }
    }
    
    /**
     * Hämtar alla artiklar från databasen.
     * 
     * @return 
     */
    public ArrayList<Article> getArticles() {
        try {
            if (articles != null) {
                // Ser till så att vi alltid har rätt värden i våra Artikel-objekt.
                // Detta behövs eftersom att "Editable"-variabeln alltid hanteras programmatiskt och inte i databasen.
                // Hade vi hämtat från databasen utan modifikation skulle "Editable" alltid vara false, eftersom referenserna skapas om på nytt vid varje request (även ajax).
                // I övrigt måste vi göra det på detta vis eftersom att en uppdatering kan ha skett i en annan session än den egna.
                // Mycket krångel för att få "Editable"-funktionen att fungera...
                articles = this.getValueMergedArticles();
            } else {
                articles = dao.getArticles();
            }
            return articles;
        } catch(SQLException sqle) {
            return null;
        }
    }
    
    /**
     * Hämtar datat från databasen och ser till att varje artikel har rätt värde i "Editable"-variabeln.
     * 
     * Metoden ser till att datat är fräscht varje gång vi hämtar det, samtidigt som vi har tillgång till sessionsspecifika värden.
     * 
     * @param dbList
     * @param localList
     * @return Artikel med korrekta värden för "editable" variabeln.
     */
    private ArrayList<Article> getValueMergedArticles() throws SQLException {
        ArrayList<Article> dbList = dao.getArticles();
        for (Article art1 : dbList)
        {
            for (Article art2 : articles) {
                if (this.isEqual(art1, art2))
                    art1.setEditable(art2.isEditable());
            }
        }
        
        return dbList;
    }
    
    /**
     * Avgör om två artikelobjekt representerar samma artikel.
     * 
     * @param art1
     * @param art2
     * @return 
     */
    private boolean isEqual(Article art1, Article art2) {
        return art1.getArticleId() == art2.getArticleId();
    }
    
    /**
     * Tar bort ett artikelobjekt från databasen.
     * 
     * @param article
     * @return 
     */
    public String deleteArticle(Article article) {
        articles.remove(article);
        dao.deleteArticle(article.getArticleId());
        return null;
    }
    
    /**
     * Skapar ett nytt artikelobjekt och lagrar den i databasen.
     * 
     * @param article
     * @return 
     */
    public String createAction(Article article) {
        try {
            dao.createArticle(article);
        } catch(SQLException sqle) {
        }
        
        return null;
    }
    
    /**
     * Sparar ändringar som användaren gjort på en artikel till databasen.
     * 
     * @param article
     * @return 
     */
    public boolean updateAction(Article article) {
        return dao.updateArticle(article);
    }
    
    /**
     * 
     * Sparar alla ändringar som användaren gjort till databasen.
     * 
     * @return 
     */
    public boolean saveUpdatesAction() {
        for (Article art : articles) {
            art.setEditable(false);
        }
        
        dao.batchUpdateArticles(articles);
        return true;
    }
    
    /**
     * Tillgängliggör en artikel till att bli redigerad.
     * 
     * Om den redan är redigerbar görs den istället oredigerbar.
     * 
     * @param article
     * @return 
     */
    public String enableEditAction(Article article) {
        article.setEditable(!article.isEditable());
        return null;
    }
    
    /**
     * Kollar om någon artikel (i Sessionslagradelistan) är tillgänglig för att bli redigerad.
     * 
     * @return 
     */
    public boolean isAnyItemEditable() {
        for (Article art : articles) {
            if (art.isEditable())
                return true;
        }
        return false;
    }
    
    /**
     * Sök metoden som kallas i index.xhtml-sök
     * @param inArticle
     * @return 
     */
    public String searchArticle(Article inArticle){
        if(!this.searchedArticles.isEmpty()){
            this.searchedArticles.clear();
            this.search(inArticle);
        }else{
            this.search(inArticle);
        }
        return "";
    }
    
    /**
     * Metod för att söka ut artikel på namn
     * @param inArticle tar emot Artikel objekt
     */
    private void search(Article inArticle){
        for(Article a : this.articles){
            String inName = inArticle.getName().toLowerCase();
            String arrayName = a.getName().toLowerCase();
            int inCatId = inArticle.getCatId();
            System.out.println("Sökt catid: " + inCatId);
            System.out.println("produkt catid: " + a.getCatId());
            if(arrayName.equalsIgnoreCase("") || inArticle.getName().isEmpty()){
                if(a.getCatId() == inCatId){
                    System.out.println("Hittat enbart kategori.");
                    this.searchedArticles.add(a);
                }else if(inCatId == 0){
                    System.out.println("Allt är tomt.");
                    this.searchedArticles.clear();
                    break;
                }
            }else if(arrayName.contains(inName)){
                System.out.println("Found it! It's: " + a.getName());
                this.searchedArticles.add(a);
            }
        }
    }
    
    /**
     * Hämtar sökt artikel arraylista
     * @return 
     */
    public ArrayList<Article> getSearchhedArticles(){
        return this.searchedArticles;
    }
    
}
