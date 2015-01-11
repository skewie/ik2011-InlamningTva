/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.User;

/**
 *
 * @author h11tobbu
 */
public class EmailDistributor implements Serializable {

    private static final String senderAddress = "h11tobbu@gmail.com";

    public static final String USERNAME = "h11tobbu@gmail.com";
    public static final String PASSWORD = "V3rryImp0r74NT!";
    public static final String ADDRESS_SMPT = "smtp.gmail.com";

    public static Properties getSendProperties() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.trust", ADDRESS_SMPT);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", ADDRESS_SMPT);
        prop.put("mail.smtp.port", "587");

        return prop;
    }

    public static void sendConfirmationMail(CartBean cart, User user) {
        //mail inits
        Properties props = System.getProperties();
        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        Transport transport = null;

        try {
            //set reciever
            Address address = new InternetAddress(user.getEmail());
            //build message
            message.setFrom(new InternetAddress(USERNAME, "Brädspelsshoppen"));
            message.setRecipient(Message.RecipientType.TO, address);
            message.setSubject("Orderbekräftelse");
            message.setText(prepareMessage(cart, user));
            Transport.send(message);
            
            //transporter settings
            //transport = session.getTransport("smtps");
            //sender settings
            //transport.connect("smtp.gmail.com", "h11tobbu@gmail.com", "V3rryImp0r74NT!");
            //sends message
            
            //transport.sendMessage(message, message.getAllRecipients());
        } catch (AddressException | UnsupportedEncodingException e) {
            System.out.println("Address or Encoding Error!!!");
            System.out.println(e.getMessage());
        } catch (MessagingException e) {
            System.out.println("Message Error!!!");
            System.out.println(e.getMessage());
        }

    }

    private static String prepareMessage(CartBean cart, User user) {
        //skickar mail med orderinfon (message, subject, reciever)
        String message = "Hej " + user.getFirstname() + "!\nHär kommer din order specifikation.\n\n";
        for (int i = 0; i < cart.getCartRows().size(); i++) {
            message += "Artikel: " + cart.getCartRows().get(i).getArticle().getName() + ", Antal: " + cart.getCartRows().get(i).getAmount() + ".\n";
        }
        message += "\nTotalt pris att betala: " + cart.getCartTotalPrice() + "\n";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        message += "Order placerad: " + dateFormat.format(cal.getTime());
        return message;
    }

    public static Session getSession() {
        return Session.getInstance(getSendProperties(), new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }
}
