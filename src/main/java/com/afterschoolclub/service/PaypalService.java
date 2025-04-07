package com.afterschoolclub.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final APIContext apiContext;

    private String webProfileId = null; 
    
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
    
    
    public Payment getPayment(String paymentId) throws PayPalRESTException {
    	
    	return Payment.get(apiContext, paymentId);
    }
    
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
				
		DetailedRefund dr = s.refund(apiContext, rr);
		String refundedAmount = dr.getAmount().getTotal();
		System.out.println("Refunded ".concat(refundedAmount));
		double d = Double.parseDouble(refundedAmount);
		int refundedAmountInPence = (int)(d * 100);
		
		return refundedAmountInPence;
    }
    
    
    public void getAllWebProfiles() {
		try {

			List<WebProfile> allProfiles = WebProfile.getList(apiContext);
			System.out.print(allProfiles);
		}
		catch (PayPalRESTException e){
			e.printStackTrace();
			webProfileId = null;
		}		
    }
    
    public String getDefaultWebProfileId() throws PayPalRESTException 
    {
    	return "XP-C93W-UV9V-WNMD-HMBF";
    	/*
    	if (webProfileId == null) {
			InputFields inputFields = new InputFields();
			inputFields.setNoShipping(1);
			
			
			
			WebProfile webProfile   = new WebProfile();
			String name = "tmpProfile-".concat(Integer.valueOf(new Random().nextInt()).toString());
			webProfile.setName(name);
			webProfile.setTemporary(true);
			webProfile.setInputFields(inputFields);		
			try {
				webProfileId = webProfile.create(apiContext).getId();
			}
			catch (PayPalRESTException e){
				e.printStackTrace();
				webProfileId = null;
			}
    	}
		return webProfileId; */
    }

	
    
}