/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.ArrayList;
import model.AdminOrders;
import model.Article;
import model.Order;
import model.OrderRow;
import model.User;

/**
 *
 * @author Jeff
 */
public class ArticleDAO implements Serializable {

    private static ArticleDAO instance;

    private Connection con = null;

    public static synchronized ArticleDAO getInstance() throws SQLException {

        if (instance == null) {
            instance = new ArticleDAO();
        }

        return instance;
    }

    /*public ArticleDAO() {
     try {
     connect();
     } catch(Exception e) {
            
     }
     }*/
    private ArticleDAO() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        //Class.forName("org.mariadb.jdbc.Driver"); //driver - Vi använder en extern MariaDB server, därför behövs denna driver istället.
        String url = "jdbc:mysql://jval.synology.me/ik2011_inl2"; //server/db
        String username = "school"; //username
        String pass = "TobJaf"; //pass
        con = DriverManager.getConnection(url, username, pass);
        con.setAutoCommit(false);
    }

    public void disconnect() throws SQLException {
        this.con.rollback();
        this.con.close();
    }

    public Article findArticleById(int id) throws SQLException {
        CallableStatement stmt = con.prepareCall("{ call p_getArticle('" + id + "') }");
        stmt.executeQuery();
        ResultSet rs = stmt.getResultSet();

        Article article = new Article();
        while (rs.next()) {

            article.setArticleId(rs.getInt("id"));
            article.setName(rs.getString("name"));
            article.setPublishedYear(rs.getString("published_year"));
            article.setPrice(rs.getDouble("price"));
            article.setImageUrl(rs.getString("image_url"));
        }

        return article;
    }

    public boolean createArticle(Article article) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("INSERT INTO article (name, published_year, price, image_url) VALUES (?, ?, ?, ?, ?)");
        stmt.setInt(1, article.getCatId());
        stmt.setString(2, article.getName());
        stmt.setString(3, article.getPublishedYear());
        stmt.setDouble(4, article.getPrice());
        stmt.setString(5, article.getImageUrl());
        return stmt.execute();
    }

    public boolean updateArticle(Article article) {
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE article SET name = ?, published_year = ?, price = ?, image_url= ? WHERE id = ?");
            this.setupStatement(stmt, article);
            int result = stmt.executeUpdate();
            con.commit();
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void setupStatement(PreparedStatement stmt, Article article) throws SQLException {
        stmt.setString(1, article.getName());
        stmt.setString(2, article.getPublishedYear());
        stmt.setDouble(3, article.getPrice());
        stmt.setString(4, article.getImageUrl());
        stmt.setInt(5, article.getArticleId());
    }

    private void setupStatement(PreparedStatement stmt, ArrayList<Article> articles) throws SQLException {
        for (Article art : articles) {
            this.setupStatement(stmt, art);
            stmt.addBatch();
        }
    }

    public boolean batchUpdateArticles(ArrayList<Article> articles) {
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE article SET name = ?, published_year = ?, price = ?, image_url= ? WHERE id = ?");
            setupStatement(stmt, articles);
            int[] result = stmt.executeBatch();
            con.commit();
            return result.length > 0;
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
            return false;
        }
    }

    public boolean deleteArticle(int articleId) {
        /*try {
         Article art = findArticleById(articleId);
         art.setPrice(art.getPrice()+1);
         updateArticle(art);
         return true;
         } catch(Exception e) {
         return false;
         }*/
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM article WHERE id = ?");
            stmt.setInt(1, articleId);

            boolean result = stmt.execute();
            con.commit();
            return result;
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
            return false;
        }
    }

    public ArrayList<Article> getArticles() throws SQLException {

        try {
            CallableStatement stmt = con.prepareCall("{ call p_getArticles() }");
            stmt.executeQuery();
            return parseResultSet(stmt.getResultSet());
        } catch (SQLNonTransientConnectionException ntce) { // om anslutningen dött ut.
            this.connect();
            return this.getArticles();
        }
    }

    public ArrayList<Article> searchArticles(String name) throws SQLException {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM article WHERE name LIKE ?");
            stmt.setString(1, "%" + name + "%");
            stmt.execute();
            return parseResultSet(stmt.getResultSet());
        } catch (SQLNonTransientConnectionException ntce) { // om anslutningen dött ut.
            this.connect();
            return this.searchArticles(name);
        }
    }

    private ArrayList<Article> parseResultSet(ResultSet rs) throws SQLException {
        ArrayList<Article> list = new ArrayList<>();
        while (rs.next()) {
            Article article = new Article();
            article.setArticleId(rs.getInt("id"));
            article.setCatId(rs.getInt("cat_id"));
            article.setName(rs.getString("name"));
            article.setPrice(rs.getDouble("price"));
            article.setPublishedYear(rs.getString("published_year"));
            article.setImageUrl(rs.getString("image_url"));
            list.add(article);
        }
        return list;
    }

    public int placeOrder(Order order) throws SQLException {
        PreparedStatement orderList = con.prepareStatement(" INSERT INTO order_list (user_id) VALUES (?) ", Statement.RETURN_GENERATED_KEYS);
        //orderList.setString(1, order.getFirstName());
        //orderList.setString(2, order.getLastName());
        orderList.setInt(1, order.getUser().getID());

        orderList.executeQuery();
        ResultSet rs = orderList.getGeneratedKeys();
        int id = -1;

        rs.first();
        id = rs.getInt(1);

        if (id > 0) {
            //lägger till rader till order med nyckel id från orderlist
            PreparedStatement stmt = con.prepareStatement(" INSERT INTO orderrow (list_id, id, amount) VALUES (?, ?, ?) ");
            for (OrderRow row : order.getOrderRows()) {
                stmt.setInt(1, id);
                stmt.setInt(2, row.getArticle().getArticleId());
                stmt.setInt(3, row.getAmount());
                stmt.addBatch();
            }
            stmt.executeBatch();
            con.commit();
            rs.close();
            stmt.close();

            return id;
        }

        return -1;
    }

    public ArrayList<AdminOrders> getAllOrders() {
        ArrayList<AdminOrders> orderlist = new ArrayList<>();

        try {
            CallableStatement stmt = con.prepareCall(" { call p_getAllOrders } ");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AdminOrders a = new AdminOrders();

                a.setFirstname(rs.getString("firstname"));
                a.setLastname(rs.getString("lastname"));
                a.setAmount(rs.getInt("amount"));
                a.setName(rs.getString("name"));
                a.setPrice(rs.getInt("price"));
                a.setDate(rs.getString("orderdate"));

                orderlist.add(a);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderlist;
    }

    public Order getOrderById(int orderId) throws SQLException {
        CallableStatement stmnt = con.prepareCall("{ call p_getOrderById(?)}");
        stmnt.setInt(1, orderId);
        stmnt.execute();
        ResultSet rs = stmnt.getResultSet();

        ArrayList<OrderRow> orderRows = new ArrayList();
        while (rs.next()) {
            Article article = new Article();
            article.setName(rs.getString("name"));
            article.setPrice(rs.getDouble("price"));
            article.setPublishedYear(rs.getString("published_year"));
            article.setImageUrl("image_url");

            orderRows.add(new OrderRow(article, rs.getInt("amount")));
        }
        rs.close();
        stmnt.close();
        User user = getUserByOrderId(orderId);
        return new Order(user, orderRows);
    }

    public User getUserByOrderId(int orderId) throws SQLException {
        CallableStatement stmnt = con.prepareCall(" {call p_getUserByOrderId(?)} ");
        stmnt.setInt(1, orderId);
        stmnt.execute();

        ResultSet rs = stmnt.getResultSet();

        rs.next();
        
        User u = new User();
        u.setID(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFirstname(rs.getString("firstname"));
        u.setLastname(rs.getString("lastname"));
        u.setEmail(rs.getString("email"));
        rs.close();
        stmnt.close();
        
        return u;
    }
    
    public void removeOrderById(int orderId) throws SQLException {
        
        PreparedStatement stOr = con.prepareStatement("DELETE FROM orderrow WHERE list_id = ?");
        stOr.setInt(1, orderId);
        stOr.execute();
        PreparedStatement stOl = con.prepareStatement("DELETE FROM order_list WHERE list_id = ?");
        stOl.setInt(1, orderId);
        stOl.execute();
    }
}
