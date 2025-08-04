package com.company.mendix.n8n;

/**
 * Test runner for n8n webhook integration
 */
public class N8nTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== n8n Webhook Java Action Test Runner ===\n");
        
        // Test 1: Basic webhook call
        System.out.println("Test 1: Basic webhook call");
        testBasicWebhook();
        
        // Test 2: Webhook with authentication
        System.out.println("\nTest 2: Webhook with authentication");
        testAuthenticatedWebhook();
        
        // Test 3: Custom content type
        System.out.println("\nTest 3: Custom content type");
        testCustomContentType();
        
        // Test 4: Error handling
        System.out.println("\nTest 4: Error handling");
        testErrorHandling();
        
        System.out.println("\n=== Test Runner Complete ===");
    }
    
    private static void testBasicWebhook() {
        try {
            String result = N8nAction.execute(
                null, // No API key for public webhook
                "https://your-n8n-instance.com/webhook/test",
                "{\"message\": \"Hello from test!\", \"timestamp\": " + System.currentTimeMillis() + "}"
            );
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Expected error (no real server): " + e.getMessage());
        }
    }
    
    private static void testAuthenticatedWebhook() {
        try {
            String result = N8nAction.execute(
                "test-api-key",
                "https://your-n8n-instance.com/webhook/secure-test",
                "{\"action\": \"test\", \"data\": {\"user\": \"test-user\"}}"
            );
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Expected error (no real server): " + e.getMessage());
        }
    }
    
    private static void testCustomContentType() {
        try {
            String result = N8nAction.execute(
                null,
                "https://your-n8n-instance.com/webhook/xml-test",
                "<?xml version=\"1.0\"?><test><message>Hello XML</message></test>",
                "application/xml"
            );
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Expected error (no real server): " + e.getMessage());
        }
    }
    
    private static void testErrorHandling() {
        // Test with invalid URL
        String result1 = N8nAction.execute(null, "invalid-url", "{}");
        System.out.println("Invalid URL result: " + result1);
        
        // Test with empty endpoint
        String result2 = N8nAction.execute(null, "", "{}");
        System.out.println("Empty endpoint result: " + result2);
    }
}
