package org.example;

import java.util.Map;
import java.util.HashMap;

public class ExternalGateway implements PaymentGateway{

    // Mock process method
    @Override
    public  Map<String, String> process(Map<String, Object> paymentDetails) {
        Map<String, String> response = new HashMap<>();

        // Simulate a successful payment
        response.put("status", "success");
        response.put("transaction_id", "12345");

        // Example of a simulated failure
        if (paymentDetails.get("card_number").equals("111122223333444")) {
            response.put("status", "failed");
        }

        return response;
    }
}
