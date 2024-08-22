package org.example;

import java.util.Map;
import java.util.List;

public class PaymentProcessor {

    private final ExternalGateway externalGateway;

    public PaymentProcessor(ExternalGateway externalGateway) {
        this.externalGateway = externalGateway;
    }

    public String processPayment(Map<String, Object> paymentDetails) throws IllegalArgumentException, RuntimeException {
        // Validate payment amount
        if (paymentDetails.get("amount") == null || (double) paymentDetails.get("amount") <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        // Validate currency
        if (!List.of("USD", "EUR", "GBP").contains(paymentDetails.get("currency"))) {
            throw new IllegalArgumentException("Unsupported currency");
        }

        // Validate card number length
        String cardNumber = (String) paymentDetails.get("card_number");
        if (cardNumber == null || cardNumber.length() != 16) {
            throw new IllegalArgumentException("Invalid card number length");
        }

        // Validate expiration date
        if (paymentDetails.get("expiration_date") == null) {
            throw new IllegalArgumentException("Missing expiration date");
        }

        // Process payment with external gateway
        Map<String, String> response = externalGateway.process(paymentDetails);

        if ("failed".equals(response.get("status"))) {
            throw new RuntimeException("Payment processing failed");
        }

        return response.get("transaction_id");
    }
}
