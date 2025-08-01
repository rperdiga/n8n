package com.company.mendix.langflow;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Mendix Java Action for Langflow API integration
 * 
 * This action allows Mendix applications to make REST calls to Langflow,
 * pass input parameters, and receive output parameters.
 * 
 * Based on the Python example provided, this implementation provides:
 * - REST API call to Langflow endpoint
 * - API key authentication
 * - Input/output parameter handling
 * - Error handling and logging
 * 
 * Note: This version uses only built-in Java libraries for maximum compatibility
 */
public class LangflowAction {
    
    // Input parameters
    private final String apiKey;
    private final String apiEndpoint;
    private final String inputText;
    private final String outputType;
    private final String inputType;
    private final int timeoutMinutes;
    
    /**
     * Constructor for Langflow Action with custom timeout
     * 
     * @param apiKey The Langflow API key for authentication
     * @param apiEndpoint The complete Langflow API endpoint URL
     * @param userPrompt The user prompt text to send to Langflow
     * @param outputType The expected output type (default: "text")
     * @param inputType The input type (default: "text")
     * @param timeoutMinutes Timeout in minutes for API response (default: 10)
     */
    public LangflowAction(String apiKey, String apiEndpoint, String userPrompt, 
                          String outputType, String inputType, int timeoutMinutes) {
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint;
        this.inputText = userPrompt;  // Internal variable still uses inputText for API
        this.outputType = outputType != null ? outputType : "text";
        this.inputType = inputType != null ? inputType : "text";
        this.timeoutMinutes = timeoutMinutes > 0 ? timeoutMinutes : 10;
    }
    
    /**
     * Constructor for Langflow Action
     * 
     * @param apiKey The Langflow API key for authentication
     * @param apiEndpoint The complete Langflow API endpoint URL
     * @param userPrompt The user prompt text to send to Langflow
     * @param outputType The expected output type (default: "text")
     * @param inputType The input type (default: "text")
     */
    public LangflowAction(String apiKey, String apiEndpoint, String userPrompt, 
                          String outputType, String inputType) {
        this(apiKey, apiEndpoint, userPrompt, outputType, inputType, 10);
    }
    
    /**
     * Simplified constructor with default types
     * 
     * @param apiKey The Langflow API key for authentication
     * @param apiEndpoint The complete Langflow API endpoint URL
     * @param userPrompt The user prompt text to send to Langflow
     */
    public LangflowAction(String apiKey, String apiEndpoint, String userPrompt) {
        this(apiKey, apiEndpoint, userPrompt, "text", "text", 10);
    }
    
    /**
     * Execute the Langflow API call
     * 
     * @return The response from Langflow API
     * @throws Exception If the API call fails
     */
    public String executeAction() throws Exception {
        System.out.println("Executing Langflow API call to endpoint: " + apiEndpoint);
        System.out.println("User prompt length: " + (inputText != null ? inputText.length() : 0) + " characters");
        
        // Validate inputs
        validateInputs();
        
        try {
            // Create HTTP client with extended timeouts for Langflow processing
            HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(60))  // Connection timeout: 60 seconds
                .build();
            
            // Create request payload
            String jsonPayload = createRequestPayload();
            System.out.println("Request payload: " + jsonPayload);
            
            // Create HTTP request with configurable timeout for Langflow processing
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .timeout(Duration.ofMinutes(timeoutMinutes))  // Use configurable timeout
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
            
            System.out.println("API request sent, waiting for Langflow response (timeout: " + timeoutMinutes + " minutes)...");
            
            // Execute request
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            System.out.println("Response status code: " + statusCode);
            
            String responseBody = response.body();
            
            // Check if request was successful
            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Langflow API call successful");
                return processSuccessResponse(responseBody);
            } else {
                System.err.println("Langflow API call failed with status code: " + statusCode);
                throw new Exception("API request failed with status code: " + statusCode + 
                                  ". Response: " + responseBody);
            }
            
        } catch (Exception e) {
            System.err.println("Error making Langflow API request: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error making API request: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate input parameters
     */
    private void validateInputs() throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("API Key is required and cannot be empty");
        }
        
        if (apiEndpoint == null || apiEndpoint.trim().isEmpty()) {
            throw new Exception("API Endpoint is required and cannot be empty");
        }
        
        if (inputText == null || inputText.trim().isEmpty()) {
            throw new Exception("User prompt is required and cannot be empty");
        }
        
        // Validate URL format
        if (!apiEndpoint.startsWith("http://") && !apiEndpoint.startsWith("https://")) {
            throw new Exception("API Endpoint must be a valid URL starting with http:// or https://");
        }
    }
    
    /**
     * Create JSON request payload using simple string concatenation
     */
    private String createRequestPayload() throws Exception {
        try {
            // Escape JSON string values
            String escapedInputValue = escapeJsonString(this.inputText);
            String escapedOutputType = escapeJsonString(this.outputType);
            String escapedInputType = escapeJsonString(this.inputType);
            
            // Create JSON payload manually (avoiding external dependencies)
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"output_type\":\"").append(escapedOutputType).append("\",");
            json.append("\"input_type\":\"").append(escapedInputType).append("\",");
            json.append("\"input_value\":\"").append(escapedInputValue).append("\"");
            json.append("}");
            
            return json.toString();
            
        } catch (Exception e) {
            System.err.println("Error creating request payload: " + e.getMessage());
            throw new Exception("Error creating request payload: " + e.getMessage(), e);
        }
    }
    
    /**
     * Escape string for JSON
     */
    private String escapeJsonString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f");
    }
    
    /**
     * Process successful API response using simple string parsing
     */
    private String processSuccessResponse(String responseBody) throws Exception {
        try {
            System.out.println("Processing response: " + responseBody);
            
            // Simple JSON parsing without external libraries
            // Look for common response patterns
            
            // Try to extract text from "text": "value" pattern
            String textValue = extractJsonValue(responseBody, "text");
            if (textValue != null && !textValue.isEmpty()) {
                return unescapeJsonString(textValue);
            }
            
            // Try to extract from "message": "value" pattern
            String messageValue = extractJsonValue(responseBody, "message");
            if (messageValue != null && !messageValue.isEmpty()) {
                return unescapeJsonString(messageValue);
            }
            
            // Try to extract from "response": "value" pattern
            String responseValue = extractJsonValue(responseBody, "response");
            if (responseValue != null && !responseValue.isEmpty()) {
                return unescapeJsonString(responseValue);
            }
            
            // If no specific patterns found, try to find any text content
            // Look for patterns like "text":"content" in nested structures
            if (responseBody.contains("\"text\":")) {
                int start = responseBody.indexOf("\"text\":\"");
                if (start > -1) {
                    start += 8; // Skip "text":"
                    int end = findJsonStringEnd(responseBody, start);
                    if (end > start) {
                        String extractedText = responseBody.substring(start, end);
                        return unescapeJsonString(extractedText);
                    }
                }
            }
            
            // If all parsing attempts fail, return the raw response
            System.out.println("Could not extract structured content from response, returning raw response");
            return responseBody;
            
        } catch (Exception e) {
            System.out.println("Could not parse response, returning raw response: " + e.getMessage());
            return responseBody;
        }
    }
    
    /**
     * Extract value for a given JSON key using simple string parsing
     */
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        
        start += pattern.length();
        int end = findJsonStringEnd(json, start);
        if (end == -1) return null;
        
        return json.substring(start, end);
    }
    
    /**
     * Find the end of a JSON string value, handling escaped quotes
     */
    private int findJsonStringEnd(String json, int start) {
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '"') {
                // Check if it's escaped
                int backslashCount = 0;
                int j = i - 1;
                while (j >= start && json.charAt(j) == '\\') {
                    backslashCount++;
                    j--;
                }
                // If even number of backslashes (including 0), the quote is not escaped
                if (backslashCount % 2 == 0) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }
    
    /**
     * Unescape JSON string
     */
    private String unescapeJsonString(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t")
                  .replace("\\b", "\b")
                  .replace("\\f", "\f");
    }
    
    /**
     * Static method for easy invocation from Mendix
     */
    public static String execute(String apiKey, String apiEndpoint, String userPrompt) {
        try {
            LangflowAction action = new LangflowAction(apiKey, apiEndpoint, userPrompt);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing Langflow action: " + e.getMessage();
        }
    }
    
    /**
     * Static method with all parameters
     */
    public static String execute(String apiKey, String apiEndpoint, String userPrompt, 
                                String outputType, String inputType) {
        try {
            LangflowAction action = new LangflowAction(apiKey, apiEndpoint, userPrompt, 
                                                       outputType, inputType);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing Langflow action: " + e.getMessage();
        }
    }
    
    /**
     * Static method with custom timeout for long-running Langflow processes
     */
    public static String execute(String apiKey, String apiEndpoint, String userPrompt, 
                                String outputType, String inputType, int timeoutMinutes) {
        try {
            LangflowAction action = new LangflowAction(apiKey, apiEndpoint, userPrompt, 
                                                       outputType, inputType, timeoutMinutes);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing Langflow action: " + e.getMessage();
        }
    }
    
    /**
     * Static method with custom timeout (simplified version)
     */
    public static String executeWithTimeout(String apiKey, String apiEndpoint, String userPrompt, 
                                          int timeoutMinutes) {
        try {
            LangflowAction action = new LangflowAction(apiKey, apiEndpoint, userPrompt, 
                                                       "text", "text", timeoutMinutes);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing Langflow action: " + e.getMessage();
        }
    }
}
