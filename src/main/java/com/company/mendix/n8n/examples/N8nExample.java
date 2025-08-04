package com.company.mendix.n8n.examples;

import com.company.mendix.n8n.N8nAction;

/**
 * Example class demonstrating how to use N8nAction in different scenarios
 * 
 * This class shows practical examples of integrating with n8n webhooks from Java/Mendix
 */
public class N8nExample {
    
    public static void main(String[] args) {
        System.out.println("=== n8n Java Action Examples ===\n");
        
        // Example 1: Basic usage with minimal parameters
        basicExample();
        
        // Example 2: Full usage with all parameters
        fullExample();
        
        // Example 3: Usage with custom timeout for long-running processes
        timeoutExample();
        
        // Example 4: Error handling examples
        errorHandlingExamples();
        
        // Example 5: Different content types
        contentTypeExamples();
    }
    
    /**
     * Basic example with minimal parameters
     */
    public static void basicExample() {
        System.out.println("1. Basic Example (JSON payload to n8n webhook):");
        System.out.println("   Sending JSON data to n8n webhook");
        
        try {
            String apiKey = null; // No API key needed for public webhooks
            String webhookEndpoint = "https://your-n8n-instance.com/webhook/your-webhook-id";
            String inputData = "{\"message\": \"Hello from Mendix!\", \"user\": \"test-user\"}";
            
            String result = N8nAction.execute(apiKey, webhookEndpoint, inputData);
            
            System.out.println("   Result: " + result);
            
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Full example with all parameters
     */
    public static void fullExample() {
        System.out.println("2. Full Example (with authentication and custom content type):");
        
        try {
            String apiKey = "your-n8n-api-key-here";
            String webhookEndpoint = "https://your-n8n-instance.com/webhook/secure-webhook-id";
            String inputData = "{\"action\": \"process_data\", \"payload\": {\"name\": \"John\", \"email\": \"john@example.com\"}}";
            String contentType = "application/json";
            
            String result = N8nAction.execute(apiKey, webhookEndpoint, inputData, contentType);
            
            System.out.println("   Result: " + result);
            
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Example with custom timeout for long-running n8n workflows
     */
    public static void timeoutExample() {
        System.out.println("3. Custom Timeout Example (for long-running workflows):");
        
        try {
            String apiKey = "your-n8n-api-key-here";
            String webhookEndpoint = "https://your-n8n-instance.com/webhook/long-running-workflow";
            String inputData = "{\"task\": \"generate_report\", \"data_size\": \"large\"}";
            int timeoutMinutes = 20; // 20 minutes for complex processing
            
            String result = N8nAction.executeWithTimeout(apiKey, webhookEndpoint, inputData, timeoutMinutes);
            
            System.out.println("   Result: " + result);
            
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Error handling examples
     */
    public static void errorHandlingExamples() {
        System.out.println("4. Error Handling Examples:");
        
        // Example with invalid URL
        System.out.println("   a) Invalid webhook URL:");
        String result1 = N8nAction.execute(null, "invalid-url", "{\"test\": \"data\"}");
        System.out.println("      " + result1);
        
        // Example with empty endpoint
        System.out.println("   b) Empty webhook endpoint:");
        String result2 = N8nAction.execute(null, "", "{\"test\": \"data\"}");
        System.out.println("      " + result2);
        
        System.out.println();
    }
    
    /**
     * Examples with different content types
     */
    public static void contentTypeExamples() {
        System.out.println("5. Different Content Type Examples:");
        
        // JSON example (most common)
        System.out.println("   a) JSON content:");
        try {
            String result = N8nAction.execute(null, 
                "https://your-n8n-instance.com/webhook/json-handler",
                "{\"type\": \"order\", \"amount\": 100}",
                "application/json");
            System.out.println("      " + result);
        } catch (Exception e) {
            System.out.println("      Error: " + e.getMessage());
        }
        
        // Plain text example
        System.out.println("   b) Plain text content:");
        try {
            String result = N8nAction.execute(null,
                "https://your-n8n-instance.com/webhook/text-handler", 
                "Simple text message",
                "text/plain");
            System.out.println("      " + result);
        } catch (Exception e) {
            System.out.println("      Error: " + e.getMessage());
        }
        
        // XML example
        System.out.println("   c) XML content:");
        try {
            String result = N8nAction.execute(null,
                "https://your-n8n-instance.com/webhook/xml-handler",
                "<?xml version=\"1.0\"?><order><id>123</id><amount>100</amount></order>",
                "application/xml");
            System.out.println("      " + result);
        } catch (Exception e) {
            System.out.println("      Error: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Example of how this would be implemented in a Mendix Java Action
     * 
     * In Mendix Studio Pro, you would:
     * 1. Create a new Java Action
     * 2. Add these parameters:
     *    - apiKey (String) - optional for public webhooks
     *    - webhookEndpoint (String) 
     *    - inputData (String)
     * 3. Set return type to String
     * 4. Use this method in the Java Action implementation
     */
    public static String mendixJavaActionExample(String apiKey, String webhookEndpoint, String inputData) {
        // This is the exact code you would put in your Mendix Java Action
        return N8nAction.execute(apiKey, webhookEndpoint, inputData);
    }
    
    /**
     * Advanced Mendix Java Action with all parameters
     */
    public static String mendixJavaActionAdvanced(String apiKey, String webhookEndpoint, String inputData,
                                                 String contentType) {
        // Advanced version with custom content type
        return N8nAction.execute(apiKey, webhookEndpoint, inputData, contentType);
    }
    
    /**
     * Mendix Java Action with custom timeout for long-running workflows
     */
    public static String mendixJavaActionWithTimeout(String apiKey, String webhookEndpoint, String inputData,
                                                    int timeoutMinutes) {
        // Version with custom timeout for complex n8n workflows
        return N8nAction.executeWithTimeout(apiKey, webhookEndpoint, inputData, timeoutMinutes);
    }
}
