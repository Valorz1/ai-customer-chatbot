package com.chatbot.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the KnowledgeBase class.
 * Verifies pattern matching, response generation, and error handling.
 */
public class KnowledgeBaseTest {

    private KnowledgeBase knowledgeBase;

    // Runs before each test — creates a fresh KnowledgeBase
    @BeforeEach
    public void setUp() {
        knowledgeBase = new KnowledgeBase();
    }

    // Test: greeting input should return a response
    @Test
    @DisplayName("Should return a greeting response for hello")
    public void testGreetingResponse() {
        String response = knowledgeBase.findResponse("hello");
        assertNotNull(response, "Response should not be null for greeting");
    }

    // Test: order status input should return a response
    @Test
    @DisplayName("Should return order status response")
    public void testOrderStatusResponse() {
        String response = knowledgeBase.findResponse("where is my order");
        assertNotNull(response, "Response should not be null for order query");
    }

    // Test: return policy input should return a response
    @Test
    @DisplayName("Should return refund/return response")
    public void testReturnResponse() {
        String response = knowledgeBase.findResponse("return policy");
        assertNotNull(response, "Response should not be null for return query");
    }

    // Test: payment input should return a response
    @Test
    @DisplayName("Should return payment response")
    public void testPaymentResponse() {
        String response = knowledgeBase.findResponse("how to pay");
        assertNotNull(response, "Response should not be null for payment query");
    }

    // Test: unknown input should return null (falls back to AI)
    @Test
    @DisplayName("Should return null for unknown input")
    public void testUnknownInput() {
        String response = knowledgeBase.findResponse("xyzzy random gibberish 12345");
        assertNull(response, "Response should be null for unknown input");
    }

    // Test: empty input should return null
    @Test
    @DisplayName("Should return null for empty input")
    public void testEmptyInput() {
        String response = knowledgeBase.findResponse("");
        assertNull(response, "Response should be null for empty input");
    }

    // Test: null input should return null and not crash
    @Test
    @DisplayName("Should handle null input without crashing")
    public void testNullInput() {
        String response = knowledgeBase.findResponse(null);
        assertNull(response, "Response should be null for null input");
    }

    // Test: input with mixed case should still match
    @Test
    @DisplayName("Should match patterns regardless of case")
    public void testCaseInsensitive() {
        String response = knowledgeBase.findResponse("HELLO");
        assertNotNull(response, "Response should match uppercase input");
    }

    // Test: longer match should win over shorter match
    @Test
    @DisplayName("Should prioritize longer pattern matches")
    public void testLongerMatchPriority() {
        String response = knowledgeBase.findResponse("hi, where is my order");
        assertNotNull(response, "Response should not be null");
        // Should match order_status, not greetings
        assertTrue(
            response.contains("order number") || response.contains("track"),
            "Should return order response, not greeting"
        );
    }

    // Test: input with extra spaces should still work
    @Test
    @DisplayName("Should handle input with extra spaces")
    public void testExtraSpaces() {
        String response = knowledgeBase.findResponse("   hello   ");
        assertNotNull(response, "Response should handle extra spaces");
    }
}