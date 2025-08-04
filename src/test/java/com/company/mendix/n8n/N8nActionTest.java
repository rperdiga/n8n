package com.company.mendix.n8n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for N8nAction
 * 
 * This class demonstrates how to use the N8nAction in different scenarios
 */
public class N8nActionTest {
    
    @Test
    @DisplayName("Test input validation - empty webhook endpoint")
    public void testEmptyWebhookEndpoint() {
        String result = N8nAction.execute(null, "", "{\"test\": \"data\"}");
        assertTrue(result.contains("Webhook endpoint is required"));
    }
    
    @Test
    @DisplayName("Test input validation - invalid URL")
    public void testInvalidUrl() {
        String result = N8nAction.execute(null, "invalid-url", "{\"test\": \"data\"}");
        assertTrue(result.contains("Webhook endpoint must be a valid URL"));
    }
    
    @Test
    @DisplayName("Test constructor with all parameters")
    public void testConstructorWithAllParameters() {
        N8nAction action = new N8nAction(
            "test-key", 
            "https://your-n8n-instance.com/webhook/test", 
            "{\"message\": \"hello world\"}",
            "application/json"
        );
        assertNotNull(action);
    }
    
    @Test
    @DisplayName("Test constructor with minimal parameters")
    public void testConstructorWithMinimalParameters() {
        N8nAction action = new N8nAction(
            null, 
            "https://your-n8n-instance.com/webhook/test", 
            "{\"message\": \"hello world\"}"
        );
        assertNotNull(action);
    }
    
    @Test
    @DisplayName("Test static execute method with all parameters")
    public void testStaticExecuteWithAllParameters() {
        // This will fail with connection error since there's no actual server
        // but it tests the parameter validation and request formation
        String result = N8nAction.execute(
            "test-key",
            "https://your-n8n-instance.com/webhook/test",
            "{\"message\": \"hello world\"}",
            "application/json"
        );
        
        // Should contain an error about connection failure, not validation error
        assertFalse(result.contains("is required"));
        assertTrue(result.contains("Error executing n8n action"));
    }
    
    @Test
    @DisplayName("Test without API key (public webhook)")
    public void testWithoutApiKey() {
        String result = N8nAction.execute(
            null,
            "https://your-n8n-instance.com/webhook/public-test",
            "{\"message\": \"hello world\"}"
        );
        
        // Should not complain about missing API key
        assertFalse(result.contains("API Key is required"));
        assertTrue(result.contains("Error executing n8n action")); // Connection error expected
    }
    
    @Test
    @DisplayName("Test with custom timeout")
    public void testWithCustomTimeout() {
        String result = N8nAction.executeWithTimeout(
            null,
            "https://your-n8n-instance.com/webhook/long-running",
            "{\"task\": \"long_process\"}",
            15 // 15 minutes timeout
        );
        
        assertTrue(result.contains("Error executing n8n action")); // Connection error expected
    }
    
    @Test
    @DisplayName("Test different content types")
    public void testDifferentContentTypes() {
        // Test JSON content type
        String result1 = N8nAction.execute(
            null,
            "https://your-n8n-instance.com/webhook/json",
            "{\"type\": \"json\"}",
            "application/json"
        );
        
        // Test plain text content type
        String result2 = N8nAction.execute(
            null,
            "https://your-n8n-instance.com/webhook/text",
            "Simple text message",
            "text/plain"
        );
        
        // Test XML content type
        String result3 = N8nAction.execute(
            null,
            "https://your-n8n-instance.com/webhook/xml",
            "<?xml version=\"1.0\"?><data><message>test</message></data>",
            "application/xml"
        );
        
        // All should fail with connection errors since we don't have real servers
        assertTrue(result1.contains("Error executing n8n action"));
        assertTrue(result2.contains("Error executing n8n action"));
        assertTrue(result3.contains("Error executing n8n action"));
    }
    
    /**
     * Example of how this would be used in a real Mendix Java Action
     */
    @Test
    @DisplayName("Example usage in Mendix")
    public void exampleMendixUsage() {
        // This is how you would call it from a Mendix Java Action
        
        // Method 1: Simple call for public webhook
        String result1 = N8nAction.execute(
            null, // No API key needed for public webhooks
            "https://your-n8n-instance.com/webhook/your-webhook-id",
            "{\"message\": \"Hello from Mendix!\", \"user\": \"test-user\"}"
        );
        
        // Method 2: With authentication for secure webhook
        String result2 = N8nAction.execute(
            "your-n8n-api-key",
            "https://your-n8n-instance.com/webhook/secure-webhook",
            "{\"action\": \"process_order\", \"order_id\": 12345}",
            "application/json"
        );
        
        // Method 3: With custom timeout for long-running workflows
        String result3 = N8nAction.executeWithTimeout(
            "your-n8n-api-key",
            "https://your-n8n-instance.com/webhook/long-process",
            "{\"task\": \"generate_report\", \"complexity\": \"high\"}",
            20 // 20 minutes timeout
        );
        
        // All should fail with connection errors since we don't have real servers
        assertTrue(result1.contains("Error executing n8n action"));
        assertTrue(result2.contains("Error executing n8n action"));
        assertTrue(result3.contains("Error executing n8n action"));
    }
}
