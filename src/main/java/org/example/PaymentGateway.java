package org.example;

import java.util.Map;

public interface PaymentGateway {
    Map<String, String> process(Map<String, Object> paymentDetails);
}
