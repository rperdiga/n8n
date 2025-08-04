package com.company.mendix.n8n;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Mendix Java Action for n8n Webhook integration
 * 
 * This action allows Mendix applications to make REST calls to n8n webhooks,
 * pass input parameters, and receive output parameters.
 * 
 * This implementation provides:
 * - REST API call to n8n webhook endpoint
 * - API key authentication (optional)
 * - Input/output parameter handling
 * - Error handling and logging
 * 
 * Note: This version uses only built-in Java libraries for maximum compatibility
 */
public class N8nAction {
    
    // Input parameters
    private final String apiKey;
    private final String webhookEndpoint;
    private final String inputData;
    private final String contentType;
    private final int timeoutMinutes;
    
    /**
     * Constructor for n8n Action with custom timeout
     * 
     * @param apiKey The n8n API key for authentication (optional, can be null)
     * @param webhookEndpoint The complete n8n webhook URL
     * @param inputData The data to send to n8n webhook
     * @param contentType The content type (default: "application/json")
     * @param timeoutMinutes Timeout in minutes for API response (default: 10)
     */
    public N8nAction(String apiKey, String webhookEndpoint, String inputData, 
                     String contentType, int timeoutMinutes) {
        this.apiKey = apiKey;
        this.webhookEndpoint = webhookEndpoint;
        this.inputData = inputData;
        this.contentType = contentType != null ? contentType : "application/json";
        this.timeoutMinutes = timeoutMinutes > 0 ? timeoutMinutes : 10;
    }
    
    /**
     * Constructor for n8n Action
     * 
     * @param apiKey The n8n API key for authentication (optional, can be null)
     * @param webhookEndpoint The complete n8n webhook URL
     * @param inputData The data to send to n8n webhook
     * @param contentType The content type (default: "application/json")
     */
    public N8nAction(String apiKey, String webhookEndpoint, String inputData, 
                     String contentType) {
        this(apiKey, webhookEndpoint, inputData, contentType, 10);
    }
    
    /**
     * Simplified constructor with default content type
     * 
     * @param apiKey The n8n API key for authentication (optional, can be null)
     * @param webhookEndpoint The complete n8n webhook URL
     * @param inputData The data to send to n8n webhook
     */
    public N8nAction(String apiKey, String webhookEndpoint, String inputData) {
        this(apiKey, webhookEndpoint, inputData, "application/json", 10);
    }
    
    /**
     * Execute the n8n webhook call
     * 
     * @return The response from n8n webhook
     * @throws Exception If the API call fails
     */
    public String executeAction() throws Exception {
        System.out.println("Executing n8n webhook call to endpoint: " + webhookEndpoint);
        System.out.println("Input data length: " + (inputData != null ? inputData.length() : 0) + " characters");
        
        // Validate inputs
        validateInputs();
        
        try {
            // Create HTTP client with extended timeouts for n8n processing
            HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(60))  // Connection timeout: 60 seconds
                .build();
            
            System.out.println("Request payload: " + inputData);
            
            // Create HTTP request with configurable timeout for n8n processing
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(webhookEndpoint))
                .header("Content-Type", contentType)
                .timeout(Duration.ofMinutes(timeoutMinutes))  // Use configurable timeout
                .POST(HttpRequest.BodyPublishers.ofString(inputData != null ? inputData : "{}"));
            
            // Add API key header if provided
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + apiKey);
            }
            
            HttpRequest request = requestBuilder.build();
            
            System.out.println("API request sent, waiting for n8n response (timeout: " + timeoutMinutes + " minutes)...");
            
            // Execute request
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            System.out.println("Response status code: " + statusCode);
            
            String responseBody = response.body();
            
            // Check if request was successful
            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("n8n webhook call successful");
                return processSuccessResponse(responseBody);
            } else {
                System.err.println("n8n webhook call failed with status code: " + statusCode);
                throw new Exception("Webhook request failed with status code: " + statusCode + 
                                  ". Response: " + responseBody);
            }
            
        } catch (Exception e) {
            System.err.println("Error making n8n webhook request: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error making webhook request: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate input parameters
     */
    private void validateInputs() throws Exception {
        if (webhookEndpoint == null || webhookEndpoint.trim().isEmpty()) {
            throw new Exception("Webhook endpoint is required and cannot be empty");
        }
        
        // Validate URL format
        if (!webhookEndpoint.startsWith("http://") && !webhookEndpoint.startsWith("https://")) {
            throw new Exception("Webhook endpoint must be a valid URL starting with http:// or https://");
        }
    }
    
    /**
     * Process successful API response using simple string parsing
     */
    private String processSuccessResponse(String responseBody) throws Exception {
        try {
            System.out.println("Processing response: " + responseBody);
            
            // For n8n webhooks, often the response is already in the desired format
            // But we can still try to extract common response patterns
            
            // Try to extract text from "result": "value" pattern
            String resultValue = extractJsonValue(responseBody, "result");
            if (resultValue != null && !resultValue.isEmpty()) {
                return unescapeJsonString(resultValue);
            }
            
            // Try to extract from "data": "value" pattern
            String dataValue = extractJsonValue(responseBody, "data");
            if (dataValue != null && !dataValue.isEmpty()) {
                return unescapeJsonString(dataValue);
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
            
            // If no specific patterns found, return the raw response
            System.out.println("Returning raw response from n8n webhook");
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
    public static String execute(String apiKey, String webhookEndpoint, String inputData) {
        try {
            N8nAction action = new N8nAction(apiKey, webhookEndpoint, inputData);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing n8n action: " + e.getMessage();
        }
    }
    
    /**
     * Static method with all parameters
     */
    public static String execute(String apiKey, String webhookEndpoint, String inputData, 
                                String contentType) {
        try {
            N8nAction action = new N8nAction(apiKey, webhookEndpoint, inputData, contentType);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing n8n action: " + e.getMessage();
        }
    }
    
    /**
     * Static method with custom timeout for long-running n8n processes
     */
    public static String execute(String apiKey, String webhookEndpoint, String inputData, 
                                String contentType, int timeoutMinutes) {
        try {
            N8nAction action = new N8nAction(apiKey, webhookEndpoint, inputData, 
                                           contentType, timeoutMinutes);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing n8n action: " + e.getMessage();
        }
    }
    
    /**
     * Static method with custom timeout (simplified version)
     */
    public static String executeWithTimeout(String apiKey, String webhookEndpoint, String inputData, 
                                          int timeoutMinutes) {
        try {
            N8nAction action = new N8nAction(apiKey, webhookEndpoint, inputData, 
                                           "application/json", timeoutMinutes);
            return action.executeAction();
        } catch (Exception e) {
            return "Error executing n8n action: " + e.getMessage();
        }
    }
}
