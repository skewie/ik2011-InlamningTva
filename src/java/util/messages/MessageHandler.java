/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.messages;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Jeff
 */
public abstract class MessageHandler {
    private static final String ERROR_MESSAGES = "errors";
    
    public static String getErrorString(FacesContext context, ErrorMessage errorType) {
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, ERROR_MESSAGES);
        return bundle.getString(errorType.getValue());
    }
    
    public static void throwErrorMessage(FacesContext context, ErrorMessage errorType) throws ValidatorException {
        throw new ValidatorException(new FacesMessage(getErrorString(context, errorType)));
    }
}