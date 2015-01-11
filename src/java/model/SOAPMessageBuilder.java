/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.xml.soap.*;

/**
 *
 * @author h11tobbu
 */
public class SOAPMessageBuilder implements Serializable{
    
    private SOAPPart part;
    private SOAPBody body;
    private SOAPBodyElement bodyElement;
    private SOAPElement element;
    private SOAPEnvelope envelope;
    private SOAPHeader header;
    private SOAPHeaderElement headerElement;
    private SOAPMessage message;
    private final static String API_VERSION = "";
    private final static String CALL_METHOD = "setExpressCheckout";
    private final static String CANCEL_URL = "";
    private final static String RETURN_URL = "";
    
    public SOAPMessageBuilder(){
        //init
    }
    
    public SOAPMessage buildMessage(Order order, String username, String password, String signature){
        //CRITICAL REQUIREMENTS 
        /*
            USER        — The API User name credential.
            PWD         — The API Password credential.
            SIGNATURE   — The Signature credential.
            METHOD      — The API operation you are addressing.
            VERSION     — The version of the API to which you are making your request.
            AMT         — The cash amount of the transaction.
            cancelUrl   — The URL address to which the user is returned if they cancel the transaction.
            returnUrl   — The URL address to which the user is returned when the complete the transaction.
        */
        
        
        
        return message;
    }
    
    
    
}
