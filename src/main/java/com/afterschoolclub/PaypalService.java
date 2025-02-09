package com.afterschoolclub;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    
    
    public String getDefaultWebProfileId() throws PayPalRESTException 
    {
    	if (webProfileId == null) {
			InputFields inputFields = new InputFields();
			inputFields.setNoShipping(1);
			WebProfile webProfile   = new WebProfile();
			webProfile.setName(Integer.valueOf(new Random().nextInt()).toString());
			webProfile.setTemporary(true);
			webProfile.setInputFields(inputFields);			
			webProfileId = webProfile.create(apiContext).getId();
    	}
		return webProfileId;
    }

	
    
}