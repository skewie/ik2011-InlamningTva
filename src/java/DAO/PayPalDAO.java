/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;
import controller.CartBean;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import model.OrderRow;

/**
 *
 * @author Jeff
 */
public class PayPalDAO {

    private final String CLIENT_ID = "AZy7BhAYg1f6OqEapEKK67ukbKFlrvA8Z09S7DzN3-EuaRmta7GqYcLXrhsx";
    private final String CLIENT_SECRET = "EKEG4RCW1aH28_2DCVWKc0PuQdRkGmdwxrkZcmjegL4Vip-mmvNn9DlXn1DO";

    /////////////////////////////////////////////////////////////////
    /// TODO: SET RETURN PAYPAL URL                               ///
    /// OBS!!!! PayPal kommer ej åt localhost eller något annat   ///
    /// lokalt, därför behöver man sätta en extern ip-adress här. ///
    /// Sätt <ip-adress>:port - exempelvis "83.255.164.113:8080"  ///
    private final String IP_ADDRESS = "83.255.164.113:8080";
    /////////////////////////////////////////////////////////////////
    

    private final String SITE_CONTEXT_ROOT = "Bradspelsshopen";

    private final String RETURN_URL = "http://" + IP_ADDRESS + "/" + SITE_CONTEXT_ROOT + "/PaymentProcessor?approved=true";
    private final String CANCEL_URL = "http://" + IP_ADDRESS + "/" + SITE_CONTEXT_ROOT + "/?order_cancelled=true";

    public PayPalDAO() {
    }

    private String getAccessToken() throws PayPalRESTException {

        String accessToken = new OAuthTokenCredential(CLIENT_ID, CLIENT_SECRET, getConfig()).getAccessToken();
        return accessToken;
    }

    private Map<String, String> getConfig() {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");
        return sdkConfig;
    }

    public boolean processPayment(String paymentId, String payerId) throws PayPalRESTException {
        String accessToken = getAccessToken();
        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(getConfig());

        Payment payment = Payment.get(apiContext, paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        payment.execute(apiContext, paymentExecute);

        return true;
    }

    public String getApprovalLink(CartBean cart, int orderId) throws PayPalRESTException {

        String accessToken = getAccessToken();
        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(getConfig());

        Amount amount = new Amount();
        amount.setCurrency("SEK");
        amount.setTotal(formatPayPalDouble(cart.getTotalPrice()));

        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        for (OrderRow or : cart.getCartRows()) {
            Item i = new Item();
            i.setQuantity(String.valueOf(or.getAmount()));
            i.setName(or.getArticle().getName());
            i.setPrice(formatPayPalDouble(or.getArticle().getPrice()));
            i.setCurrency("SEK");
        }

        Transaction transaction = new Transaction();
        //transaction.setDescription(cart.getCartRows().get(0).getArticle().getName());

        transaction.setItemList(new ItemList().setItems(items));

        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(CANCEL_URL);
        redirectUrls.setReturnUrl(RETURN_URL+"&order_id="+orderId);
        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment = payment.create(apiContext);

        String approvalLink = null;
        for (Links l : createdPayment.getLinks()) {
            if ("approval_url".equals(l.getRel())) {
                approvalLink = l.getHref();
                break;
            }
        }

        return approvalLink;
    }

    /**
     * PayPal behöver ha priser i ett specifikt format, nämligen amerikanska
     * formatet.
     *
     * Denna metod används för att generera en PayPal-säker sträng som kan
     * skickas till API:et.
     *
     * @param amount
     * @return
     */
    private String formatPayPalDouble(double amount) {
        NumberFormat nf = NumberFormat.getInstance(Locale.US); // Formatterar till ett format som PayPal accepterar
        return nf.format(amount);
    }
}
