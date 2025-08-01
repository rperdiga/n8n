package com.company.mendix.langflow.examples;

import com.company.mendix.langflow.LangflowAction;

/**
 * Example class demonstrating how to use LangflowAction in different scenarios
 * 
 * This class shows practical examples of integrating with Langflow from Java/Mendix
 */
public class LangflowExample {
    
    public static void main(String[] args) {
        System.out.println("=== Langflow Java Action Examples ===\n");
        
        // Example 1: Basic usage with minimal parameters
        basicExample();
        
        // Example 2: Full usage with all parameters
        fullExample();
        
        // Example 3: Usage with custom timeout for long-running processes
        timeoutExample();
        
        // Example 4: Error handling examples
        errorHandlingExamples();
    }
    
    /**
     * Basic example with minimal parameters (matches Python example)
     */
    public static void basicExample() {
        System.out.println("1. Basic Example (matching Python implementation):");
        System.out.println("   This matches the Python code you provided");
        
        try {
            // This matches your Python example
            String apiKey = "your-langflow-api-key-here";
            String apiEndpoint = "http://localhost:7860/api/v1/run/16340530-8436-4e90-a130-7afbaba5bfee";
            String userPrompt = "hello world!";
            
            String result = LangflowAction.execute(apiKey, apiEndpoint, userPrompt);
            
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
        System.out.println("2. Full Example (with all parameters):");
        
        try {
            String apiKey = "your-langflow-api-key-here";
            String apiEndpoint = "http://localhost:7860/api/v1/run/16340530-8436-4e90-a130-7afbaba5bfee";
            String userPrompt = "What can you tell me about machine learning?";
            String outputType = "text";
            String inputType = "text";
            
            String result = LangflowAction.execute(apiKey, apiEndpoint, userPrompt, outputType, inputType);
            
            System.out.println("   Result: " + result);
            
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Example with custom timeout for long-running Langflow processes
     */
    public static void timeoutExample() {
        System.out.println("3. Custom Timeout Example (for long-running processes):");
        
        try {
            String apiKey = "your-langflow-api-key-here";
            String apiEndpoint = "http://localhost:7860/api/v1/run/16340530-8436-4e90-a130-7afbaba5bfee";
            String userPrompt = "Generate a detailed analysis of machine learning trends in 2025";
            int timeoutMinutes = 15; // 15 minutes for complex processing
            
            String result = LangflowAction.executeWithTimeout(apiKey, apiEndpoint, userPrompt, timeoutMinutes);
            
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
        
        // Example with empty API key
        System.out.println("   a) Empty API key:");
        String result1 = LangflowAction.execute("", "http://localhost:7860/api/v1/run/test", "hello");
        System.out.println("      " + result1);
        
        // Example with invalid URL
        System.out.println("   b) Invalid URL:");
        String result2 = LangflowAction.execute("test-key", "invalid-url", "hello");
        System.out.println("      " + result2);
        
        // Example with empty input
        System.out.println("   c) Empty input text:");
        String result3 = LangflowAction.execute("test-key", "http://localhost:7860/api/v1/run/test", "");
        System.out.println("      " + result3);
        
        System.out.println();
    }
    
    /**
     * Example of how this would be implemented in a Mendix Java Action
     * 
     * In Mendix Studio Pro, you would:
     * 1. Create a new Java Action
     * 2. Add these parameters:
     *    - apiKey (String)
     *    - apiEndpoint (String) 
     *    - userPrompt (String)
     * 3. Set return type to String
     * 4. Use this method in the Java Action implementation
     */
    public static String mendixJavaActionExample(String apiKey, String apiEndpoint, String userPrompt) {
        // This is the exact code you would put in your Mendix Java Action
        return LangflowAction.execute(apiKey, apiEndpoint, userPrompt);
    }
    
    /**
     * Advanced Mendix Java Action with all parameters
     */
    public static String mendixJavaActionAdvanced(String apiKey, String apiEndpoint, String userPrompt,
                                                 String outputType, String inputType) {
        // Advanced version with all parameters
        return LangflowAction.execute(apiKey, apiEndpoint, userPrompt, outputType, inputType);
    }
    
    /**
     * Mendix Java Action with custom timeout for long-running processes
     */
    public static String mendixJavaActionWithTimeout(String apiKey, String apiEndpoint, String userPrompt,
                                                    int timeoutMinutes) {
        // Version with custom timeout for complex Langflow processes
        return LangflowAction.executeWithTimeout(apiKey, apiEndpoint, userPrompt, timeoutMinutes);
    }
}
