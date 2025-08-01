package com.company.mendix.langflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LangflowAction
 * 
 * This class demonstrates how to use the LangflowAction in different scenarios
 */
public class LangflowActionTest {
    
    @Test
    @DisplayName("Test input validation - empty API key")
    public void testEmptyApiKey() {
        String result = LangflowAction.execute("", "http://localhost:7860/api/v1/run/test", "hello world");
        assertTrue(result.contains("API Key is required"));
    }
    
    @Test
    @DisplayName("Test input validation - empty endpoint")
    public void testEmptyEndpoint() {
        String result = LangflowAction.execute("test-key", "", "hello world");
        assertTrue(result.contains("API Endpoint is required"));
    }
    
    @Test
    @DisplayName("Test input validation - empty user prompt")
    public void testEmptyUserPrompt() {
        String result = LangflowAction.execute("test-key", "http://localhost:7860/api/v1/run/test", "");
        assertTrue(result.contains("User prompt is required"));
    }
    
    @Test
    @DisplayName("Test input validation - invalid URL")
    public void testInvalidUrl() {
        String result = LangflowAction.execute("test-key", "invalid-url", "hello world");
        assertTrue(result.contains("API Endpoint must be a valid URL"));
    }
    
    @Test
    @DisplayName("Test constructor with all parameters")
    public void testConstructorWithAllParameters() {
        LangflowAction action = new LangflowAction(
            "test-key", 
            "http://localhost:7860/api/v1/run/test", 
            "hello world",
            "text",
            "text"
        );
        assertNotNull(action);
    }
    
    @Test
    @DisplayName("Test constructor with minimal parameters")
    public void testConstructorWithMinimalParameters() {
        LangflowAction action = new LangflowAction(
            "test-key", 
            "http://localhost:7860/api/v1/run/test", 
            "hello world"
        );
        assertNotNull(action);
    }
    
    @Test
    @DisplayName("Test static execute method with all parameters")
    public void testStaticExecuteWithAllParameters() {
        // This will fail with connection error since there's no actual server
        // but it tests the parameter validation and request formation
        String result = LangflowAction.execute(
            "test-key",
            "http://localhost:7860/api/v1/run/test",
            "hello world",
            "text",
            "text"
        );
        
        // Should contain an error about connection failure, not validation error
        assertFalse(result.contains("is required"));
        assertTrue(result.contains("Error executing Langflow action"));
    }
    
    /**
     * Example of how this would be used in a real Mendix Java Action
     */
    @Test
    @DisplayName("Example usage in Mendix")
    public void exampleMendixUsage() {
        // This is how you would call it from a Mendix Java Action
        
        // Method 1: Simple call with default parameters
        String result1 = LangflowAction.execute(
            "your-api-key-here",
            "http://localhost:7860/api/v1/run/16340530-8436-4e90-a130-7afbaba5bfee",
            "hello world!"
        );
        
        // Method 2: Full call with all parameters
        String result2 = LangflowAction.execute(
            "your-api-key-here",
            "http://localhost:7860/api/v1/run/16340530-8436-4e90-a130-7afbaba5bfee",
            "hello world!",
            "text",  // output_type
            "text"   // input_type
        );
        
        // Both should fail with connection errors since we don't have a real server
        assertTrue(result1.contains("Error executing Langflow action"));
        assertTrue(result2.contains("Error executing Langflow action"));
    }
}
