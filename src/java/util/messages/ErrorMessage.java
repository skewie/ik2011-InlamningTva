/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.messages;

/**
 *
 * @author Jeff
 */
public enum ErrorMessage {
    EMPTY_FIELD("emptyField"), 
    NOT_A_NUMBER("notNumber"),
    NOT_A_YEAR("notYear"),
    NEGATIVE_NUMBER("negativeNumber");
    
    private String value;
    private ErrorMessage(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
