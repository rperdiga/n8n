# Mendix Implementation Guide for n8n Webhook Integration

This guide provides step-by-step instructions for implementing the n8n Webhook Java Action in your Mendix application.

## Prerequisites

1. **Mendix Studio Pro** (any recent version with Java 11+ support)
2. **n8n instance** (self-hosted or cloud)
3. **Active n8n workflow with webhook trigger**

## Step 1: Deploy the JAR File

### Option A: Use the automated script (Windows)
```powershell
.\deploy-to-mendix.ps1 -MendixProjectPath "C:\Path\To\Your\Mendix\Project"
```

### Option B: Manual deployment
1. Build the JAR: `.\gradlew shadowJar`
2. Copy `build/libs/n8n-1.0.0-mendix.jar` to your Mendix project's `userlib` folder
3. Refresh your Mendix project in Studio Pro

## Step 2: Create Java Action in Mendix Studio Pro

### Basic Java Action Setup

1. **Right-click** on your module → **Add** → **Java Action**
2. **Name**: `CallN8nWebhook`
3. **Add Parameters**:
   - `ApiKey` (String) - Optional for public webhooks
   - `WebhookEndpoint` (String) - Your n8n webhook URL
   - `InputData` (String) - JSON data to send
4. **Return Type**: String
5. **Java Implementation**:

```java
// BEGIN USER CODE
return com.company.mendix.n8n.N8nAction.execute(ApiKey, WebhookEndpoint, InputData);
// END USER CODE
```

### Advanced Java Action with Content Type

For custom content types (XML, plain text, etc.):

```java
// BEGIN USER CODE
return com.company.mendix.n8n.N8nAction.execute(ApiKey, WebhookEndpoint, InputData, ContentType);
// END USER CODE
```

Parameters:
- `ApiKey` (String)
- `WebhookEndpoint` (String)
- `InputData` (String)
- `ContentType` (String) - e.g., "application/xml", "text/plain"

## Step 3: Configure Your n8n Workflow

### Example n8n Workflow

1. **Add Webhook Node**:
   - Type: Webhook
   - HTTP Method: POST
   - Path: your-webhook-path
   - Response Mode: "Respond Immediately" or "Wait for Workflow to Finish"

2. **Process Data Node** (optional):
   ```javascript
   // Example function node to process incoming data
   const inputData = items[0].json;
   
   return [{
     json: {
       result: "processed",
       original_data: inputData,
       processed_at: new Date().toISOString()
     }
   }];
   ```

3. **Response Node**:
   - Type: Respond to Webhook
   - Response Body: Your desired response format

## Step 4: Use in Mendix Microflows

### Basic Usage Pattern

1. **Create Input Variables**:
   - `WebhookURL` (String): Your n8n webhook URL
   - `RequestData` (String): JSON string to send

2. **Call Java Action**:
   - Drag `CallN8nWebhook` into microflow
   - Map parameters:
     - ApiKey: Leave empty for public webhooks or use your API key
     - WebhookEndpoint: $WebhookURL
     - InputData: $RequestData

3. **Process Response**:
   - Store result in variable
   - Parse JSON if needed
   - Handle errors appropriately

### Example Microflow Implementation

```
[Start] → [Create Object] → [Call n8n Webhook] → [Process Response] → [End]
           ↓                    ↓                   ↓
       Create request      Java Action:         Parse result
       JSON string        CallN8nWebhook       and update entity
```

## Step 5: Error Handling

### Common Error Scenarios

1. **Network Issues**:
   ```java
   if (result.contains("Error executing n8n action")) {
       // Handle connection errors
       // Log error, show user message, etc.
   }
   ```

2. **Invalid Webhook URL**:
   ```java
   if (result.contains("Webhook endpoint must be a valid URL")) {
       // Handle URL validation errors
   }
   ```

3. **Timeout Issues**:
   ```java
   // Use extended timeout for long-running workflows
   return com.company.mendix.n8n.N8nAction.executeWithTimeout(
       ApiKey, WebhookEndpoint, InputData, 20 // 20 minutes
   );
   ```

## Example Implementations

### Example 1: Order Processing

**Mendix Side:**
```java
// Create order data
String orderData = "{" +
    "\"order_id\": " + $Order/OrderNumber + "," +
    "\"customer_email\": \"" + $Order/Customer/Email + "\"," +
    "\"items\": [" + /* build items array */ + "]," +
    "\"total_amount\": " + $Order/TotalAmount +
"}";

// Call n8n workflow
String result = com.company.mendix.n8n.N8nAction.execute(
    $ApiKey, 
    "https://your-n8n.com/webhook/process-order", 
    orderData
);
```

**n8n Workflow:**
- Webhook → Validate Order → Send Email → Update Database → Respond

### Example 2: File Processing

**Mendix Side:**
```java
String fileData = "{" +
    "\"file_url\": \"" + $Document/URL + "\"," +
    "\"file_type\": \"" + $Document/FileType + "\"," +
    "\"processing_options\": {" +
        "\"extract_text\": true," +
        "\"generate_thumbnail\": true" +
    "}" +
"}";

// Use extended timeout for file processing
String result = com.company.mendix.n8n.N8nAction.executeWithTimeout(
    $ApiKey,
    "https://your-n8n.com/webhook/process-file",
    fileData,
    30 // 30 minutes timeout
);
```

## Testing Your Implementation

### 1. Test Webhook Connectivity

Create a simple test microflow:
```java
String testData = "{\"test\": \"connection\", \"timestamp\": \"" + 
                  java.time.Instant.now().toString() + "\"}";

String result = com.company.mendix.n8n.N8nAction.execute(
    null, 
    "https://your-n8n.com/webhook/test", 
    testData
);
```

### 2. Validate Response Handling

Test with different response formats from n8n to ensure your parsing logic works.

### 3. Error Scenario Testing

Test with:
- Invalid webhook URLs
- Network disconnection
- n8n instance downtime
- Malformed JSON data

## Best Practices

1. **Always handle errors gracefully**
2. **Use appropriate timeouts for your use case**
3. **Log webhook calls for debugging**
4. **Validate JSON before sending**
5. **Use environment-specific webhook URLs**
6. **Implement retry logic for critical operations**

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| "Class not found" | Refresh Mendix project, check JAR in userlib |
| Connection timeout | Increase timeout, check n8n instance |
| Invalid JSON | Validate JSON structure before sending |
| 404 errors | Verify webhook URL and n8n workflow status |

### Debug Output

Enable debug logging in Mendix to see:
- Request URLs
- Payload data
- Response codes
- Error messages

The Java Action provides detailed logging for troubleshooting.
