package com.afterschoolclub.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 
 */
@Service
@RequiredArgsConstructor
public class PaypalService {

    /**
     * The default web profile specifically configured for AfterSWchoolCLub on PayPal  
     */
    @Value("${asc.paypal.webProfileId}")
    private String webProfileId;
    
    
    /**
     * apiContext for paypal transactions
     */
    private final APIContext apiContext;

    
    /**
     * Create a PayPal payment 
     * 
     * @param total - total amount in defined currency eg 100 is £1.00 
     * @param currency - currency for transaction 
     * @param method - e.g. Paypal
     * @param intent - e.g "sale"
     * @param description - description for payment
     * @param cancelUrl - url to return to if user cancels transaction 
     * @param successUrl - url to return to if transaction is a success
     * @param webExperienceId -
     * @return payment object
     * @throws PayPalRESTException
     */
    public Payment createPayment(
            Integer total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl,
            String webExperienceId
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", Double.valueOf(total.intValue()/100))); // 9.99$ - 9,99€

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Payer payer = new Payer();
        payer.setPaymentMethod(method);
        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setExperienceProfileId(webExperienceId);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);        
        return payment.create(apiContext);
    }

    /**
     * Execute the actual payment 
     * @param paymentId - paymentId for the payment
     * @param payerId - id for the payer
     * @return the payment object 
     * @throws PayPalRESTException
     */
    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
    
    
    /**
     * @param paymentId - uique key for the payment
     * @return the payment object for a payment id
     * @throws PayPalRESTException
     */
    public Payment getPayment(String paymentId) throws PayPalRESTException {
    	
    	return Payment.get(apiContext, paymentId);
    }
    
    /**
     * Tries to refund the transaction up to a maximum value 
     * @param salesId - salesId of transaction to refund
     * @param amountInPence - max amount to refund
     * @return the amount refunded 
     * @throws PayPalRESTException
     */
    public int refundSale(
            String salesId,
            int	amountInPence
    ) throws PayPalRESTException {
    	Sale s = Sale.get(apiContext, salesId);
    	
    	RefundRequest rr = new RefundRequest();    	
    	rr.setReason("Services not used");
    	rr.setDescription("Services not used");    				
		Amount a = new Amount();
		a.setCurrency("GBP");				
		DecimalFormat df = new DecimalFormat("#0.00");
		double amountInPounds =  amountInPence;
		amountInPounds = amountInPounds / 100;		
		a.setTotal(df.format(amountInPounds));		
		rr.setAmount(a);				
		String requestId = String.format("REFUND-%s-%d", salesId, LocalDateTime.now().hashCode());
		apiContext.setRequestId(requestId);		
		DetailedRefund dr = s.refund(apiContext, rr);
		String refundedAmount = dr.getAmount().getTotal();
		System.out.println("Refunded ".concat(refundedAmount));
		double d = Double.parseDouble(refundedAmount);
		int refundedAmountInPence = (int)(d * 100);
		
		return refundedAmountInPence;
    }
    
    
    /**
     * @return
     * @throws PayPalRESTException
     */
    public String getDefaultWebProfileId() throws PayPalRESTException 
    {
    	return webProfileId; 	
    }

	
    
}