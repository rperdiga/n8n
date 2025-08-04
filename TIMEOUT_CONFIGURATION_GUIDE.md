# Timeout Configuration Guide for n8n Webhook Integration

This guide explains how to configure timeouts for different types of n8n workflows and use cases.

## Default Timeout Behavior

The n8n Java Action uses a **10-minute default timeout** for all webhook calls. This is suitable for most standard workflows but may need adjustment for complex or long-running processes.

## Timeout Methods

### 1. Basic Method (10-minute default)
```java
N8nAction.execute(apiKey, webhookEndpoint, inputData)
```
- **Timeout**: 10 minutes
- **Use case**: Standard webhooks, simple data processing

### 2. Custom Timeout Method
```java
N8nAction.executeWithTimeout(apiKey, webhookEndpoint, inputData, timeoutMinutes)
```
- **Timeout**: Configurable (in minutes)
- **Use case**: Long-running workflows, file processing, complex operations

### 3. Full Control Method
```java
N8nAction.execute(apiKey, webhookEndpoint, inputData, contentType, timeoutMinutes)
```
- **Timeout**: Configurable with custom content type
- **Use case**: Advanced scenarios with specific content types and timeouts

## Recommended Timeouts by Use Case

### Quick Operations (1-5 minutes)
- **Data validation**
- **Simple notifications**
- **Basic transformations**

```java
// 2-minute timeout for quick operations
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, 2);
```

### Standard Operations (5-15 minutes)
- **API integrations**
- **Database operations**
- **Email sending**
- **Basic file processing**

```java
// 10-minute timeout (default) for standard operations
String result = N8nAction.execute(apiKey, webhookUrl, data);

// Or explicitly set 15 minutes
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, 15);
```

### Complex Operations (15-30 minutes)
- **Large file processing**
- **Multiple API calls**
- **Data analysis**
- **Report generation**

```java
// 25-minute timeout for complex operations
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, 25);
```

### Heavy Operations (30+ minutes)
- **Large dataset processing**
- **Video/audio processing**
- **Machine learning tasks**
- **Bulk operations**

```java
// 45-minute timeout for heavy operations
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, 45);

// Maximum recommended: 60 minutes
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, 60);
```

## Timeout Configuration Examples

### Example 1: E-commerce Order Processing
```java
// Order processing workflow (10-15 minutes)
String orderData = "{\"order_id\": 12345, \"items\": [...]}";
String result = N8nAction.executeWithTimeout(
    apiKey, 
    "https://n8n.company.com/webhook/process-order", 
    orderData, 
    15
);
```

### Example 2: File Upload and Processing
```java
// File processing workflow (20-30 minutes)
String fileData = "{\"file_url\": \"...\", \"processing_type\": \"full\"}";
String result = N8nAction.executeWithTimeout(
    apiKey, 
    "https://n8n.company.com/webhook/process-file", 
    fileData, 
    25
);
```

### Example 3: Data Synchronization
```java
// Large data sync (30-45 minutes)
String syncData = "{\"source\": \"crm\", \"target\": \"warehouse\", \"full_sync\": true}";
String result = N8nAction.executeWithTimeout(
    apiKey, 
    "https://n8n.company.com/webhook/sync-data", 
    syncData, 
    40
);
```

## Timeout Error Handling

### Detecting Timeout Errors
```java
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, 20);

if (result.contains("Request timeout")) {
    // Handle timeout scenario
    logError("n8n workflow timed out after 20 minutes");
    // Implement retry logic or alternative handling
}
```

### Implementing Retry Logic
```java
public String executeWithRetry(String apiKey, String webhookUrl, String data, 
                              int timeoutMinutes, int maxRetries) {
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
        String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, timeoutMinutes);
        
        if (!result.contains("Request timeout") && !result.contains("Error executing")) {
            return result; // Success
        }
        
        if (attempt < maxRetries) {
            // Wait before retry (exponential backoff)
            try {
                Thread.sleep(attempt * 30000); // 30s, 60s, 90s...
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    return "Max retries exceeded";
}
```

## n8n Workflow Timeout Considerations

### n8n Side Configuration

Your n8n workflows should also be configured with appropriate timeouts:

1. **Webhook Node Settings**:
   - Response Mode: "Wait for Workflow to Finish" (for synchronous calls)
   - Timeout: Should be less than Java Action timeout

2. **HTTP Request Nodes**:
   - Set timeouts for external API calls
   - Configure retry logic

3. **Function Nodes**:
   - Avoid infinite loops
   - Set execution limits

### Example n8n Workflow with Timeouts
```json
{
  "nodes": [
    {
      "name": "Webhook",
      "type": "webhook",
      "parameters": {
        "responseMode": "waitForWebhook",
        "timeout": 1800  // 30 minutes in seconds
      }
    },
    {
      "name": "HTTP Request",
      "type": "httpRequest",
      "parameters": {
        "timeout": 300000  // 5 minutes in milliseconds
      }
    }
  ]
}
```

## Performance Optimization

### 1. Optimize n8n Workflows
- Minimize external API calls
- Use efficient data processing
- Avoid unnecessary delays

### 2. Use Appropriate Timeouts
- Don't use excessive timeouts for simple operations
- Don't use too short timeouts for complex operations

### 3. Implement Asynchronous Patterns
For very long operations, consider:
- Immediate webhook response with job ID
- Separate status checking mechanism
- Callback webhooks when complete

```java
// Start long operation (immediate response)
String startResult = N8nAction.execute(apiKey, 
    "https://n8n.company.com/webhook/start-job", jobData);

// Extract job ID from response
String jobId = extractJobId(startResult);

// Check status periodically (shorter timeouts)
String statusResult = N8nAction.executeWithTimeout(apiKey, 
    "https://n8n.company.com/webhook/job-status", 
    "{\"job_id\": \"" + jobId + "\"}", 
    2);
```

## Troubleshooting Timeout Issues

### Common Timeout Problems

1. **Network Latency**:
   - Add buffer time for network delays
   - Test with realistic network conditions

2. **n8n Performance**:
   - Monitor n8n instance resources
   - Optimize workflow efficiency

3. **External Dependencies**:
   - Account for third-party API response times
   - Implement fallbacks for slow services

### Debug Timeout Issues

Enable detailed logging to understand where time is spent:

```java
long startTime = System.currentTimeMillis();
String result = N8nAction.executeWithTimeout(apiKey, webhookUrl, data, timeoutMinutes);
long duration = System.currentTimeMillis() - startTime;

System.out.println("n8n call took: " + duration + "ms");
```

## Best Practices

1. **Start Conservative**: Begin with shorter timeouts and increase as needed
2. **Monitor Performance**: Track actual execution times in production
3. **Set Realistic Expectations**: Consider all processing steps in your workflow
4. **Implement Fallbacks**: Have alternative handling for timeout scenarios
5. **Use Async Patterns**: For very long operations, use asynchronous processing
6. **Test Thoroughly**: Test timeout scenarios in realistic environments

## Timeout Limits

### Java Action Limits
- **Minimum**: 1 minute
- **Maximum**: 60 minutes (recommended)
- **Default**: 10 minutes

### Considerations
- Mendix platform may have its own timeout limits
- Network infrastructure may impose additional timeouts
- n8n instance configuration affects actual processing time
