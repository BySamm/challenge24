package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentProcessorTest {

    private PaymentProcessor paymentProcessor;
    private ExternalGateway externalGateway;

    @BeforeEach
    void setUp() {
        externalGateway = mock(ExternalGateway.class);
        paymentProcessor = new PaymentProcessor(externalGateway);
    }

    @Test
    public void testProcessPaymentSuccess() {
        // Arrange
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("amount", 100.0);
        paymentDetails.put("currency", "USD");
        paymentDetails.put("card_number", "1234567812345678");
        paymentDetails.put("expiration_date", "12/25");

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("transaction_id", "12345");

        when(externalGateway.process(paymentDetails)).thenReturn(response);

        // Act
        String transactionId = paymentProcessor.processPayment(paymentDetails);

        // Assert
        assertEquals("12345", transactionId);
    }

    @Test
    public void testProcessPaymentFailure() {
        // Arrange
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("amount", 100.0);
        paymentDetails.put("currency", "USD");
        paymentDetails.put("card_number", "1111222233334444");
        paymentDetails.put("expiration_date", "12/25");

        Map<String, String> response = new HashMap<>();
        response.put("status", "failed");

        when(externalGateway.process(paymentDetails)).thenReturn(response);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            paymentProcessor.processPayment(paymentDetails);
        });
        assertEquals("Payment processing failed", thrown.getMessage());
    }

    @Test
    public void testInvalidPaymentAmount() {
        // Arrange
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("amount", -100.0);
        paymentDetails.put("currency", "USD");
        paymentDetails.put("card_number", "1234567812345678");
        paymentDetails.put("expiration_date", "12/25");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                paymentProcessor.processPayment(paymentDetails));
        assertEquals("Invalid payment amount", thrown.getMessage());
    }

    @Test
    public void testUnsupportedCurrency() {
        // Arrange
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("amount", 100.0);
        paymentDetails.put("currency", "AUD");
        paymentDetails.put("card_number", "1234567812345678");
        paymentDetails.put("expiration_date", "12/25");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                paymentProcessor.processPayment(paymentDetails));
        assertEquals("Unsupported currency", thrown.getMessage());
    }

    @Test
    public void testInvalidCardNumberLength() {
        // Arrange
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("amount", 100.0);
        paymentDetails.put("currency", "USD");
        paymentDetails.put("card_number", "123456");
        paymentDetails.put("expiration_date", "12/25");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                paymentProcessor.processPayment(paymentDetails));
        assertEquals("Invalid card number length", thrown.getMessage());
    }

    @Test
    public void testMissingExpirationDate() {
        // Arrange
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("amount", 100.0);
        paymentDetails.put("currency", "USD");
        paymentDetails.put("card_number", "1234567812345678");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                paymentProcessor.processPayment(paymentDetails));
        assertEquals("Missing expiration date", thrown.getMessage());
    }

}

