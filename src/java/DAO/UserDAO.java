/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import model.User;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.AdminOrders;

/**
 *
 * @author h11tobbu
 */
public class UserDAO implements Serializable {

    private static UserDAO instance;
    private Connection con = null;

    public static synchronized UserDAO getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    private UserDAO() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
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

    //TODO: implementera queryn nedan i en procedur i databasen vid namn p_login()
    public User login(User user) throws SQLException {
        /*
         --QUERY--
         SELECT * FROM siteusers
         WHERE username = p_username
         AND
         password = p_password;
         */
        System.out.println("Loggar in: " + user.getUsername() + ", " + user.getPassword());
        CallableStatement stmt = con.prepareCall(" { call p_login(?, ?) } ");
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        ResultSet rs = stmt.executeQuery();

        User newUser = new User();
        user.setPassword(null); //säkerhet

        while (rs.next()) {
            newUser.setID(rs.getInt("id"));
            //inget password lagras på session
            newUser.setUsername(rs.getString("username"));
            newUser.setFirstname(rs.getString("firstname"));
            newUser.setLastname(rs.getString("lastname"));
            newUser.setEmail(rs.getString("email"));
        }
        System.out.println("Hämtat: " + newUser.getID() + ", " + newUser.getFirstname() + " " + newUser.getLastname() + ", " + newUser.getEmail());

        return newUser;
    }

    //TODO: implementera queryn nedan i en procedur i databasen vid namn 
    public void createUser(User user) throws SQLException {
        System.out.println("inside DAO-CreateUser");
        /*
         --QUERY--
         INSERT INTO siteusers(username, password, firstname, lastname, email)
         VALUES(p_username, p_password, p_firstname, p_lastname, p_email);
         */
        CallableStatement stmt = con.prepareCall(" { call p_createUser(?, ?, ?, ?, ?) } ");
        System.out.println("Skapar användaren: " + user.getUsername());
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getFirstname());
        stmt.setString(4, user.getLastname());
        stmt.setString(5, user.getEmail());

        stmt.executeQuery();
    }

    public ArrayList<AdminOrders> getUserOrders(User user) {

        System.out.println("Hämtar " + user.getFirstname() + "...");
        ArrayList<AdminOrders> orderlist = new ArrayList<>();

        try {
            CallableStatement stmt = con.prepareCall("{ call p_getUserOrders(?) }");
            stmt.setInt(1, user.getID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AdminOrders a = new AdminOrders();

                a.setFirstname(rs.getString("firstname"));
                a.setLastname(rs.getString("lastname"));
                a.setName(rs.getString("name"));
                a.setAmount(rs.getInt("amount"));
                a.setPrice(rs.getInt("price"));
                a.setDate(rs.getString("date"));

                System.out.println("Recieved: ");
                System.out.println(a.getFirstname() + ", " + a.getLastname() + ", " + a.getName() + ", " + a.getAmount());

                orderlist.add(a);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderlist;
    }
}
