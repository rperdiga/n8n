# Mendix Implementation Guide for n8n Webhook Integration

This guide provides step-by-step instructions for implementing the n8n Webhook Java Action in your Mendix application.

## Prerequisites

1. **Mendix Studio Pro** (any recent version with Java 11+ support)
2. **n8n instance** (self-hosted or cloud)
3. **Active n8n workflow with webhook trigger**

⚠️ **Important**: Session ID is mandatory for all webhook calls in this implementation to support n8n Simple Memory functionality.

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
   - `apiKey` (String) - Optional for public webhooks
   - `apiEndPoint` (String) - Your n8n webhook URL
   - `userPrompt` (String) - Any text or JSON data (automatically converted to JSON if needed)
   - `sessionId` (String) - Required for n8n Simple Memory support
4. **Return Type**: String
5. **Java Implementation**:

```java
// BEGIN USER CODE
return com.company.mendix.n8n.N8nAction.execute(apiKey, apiEndPoint, userPrompt, sessionId);
// END USER CODE
```

### Session Support Java Action

For applications requiring conversation continuity with n8n Simple Memory:

```java
// BEGIN USER CODE
// Generate or retrieve session ID
String sessionId = sessionId != null && !sessionId.isEmpty() ? sessionId : 
                   "user-" + $CurrentUser/Name + "-" + System.currentTimeMillis();

return com.company.mendix.n8n.N8nAction.execute(apiKey, apiEndPoint, userPrompt, sessionId);
// END USER CODE
```

Parameters:
- `apiKey` (String)
- `apiEndPoint` (String)
- `userPrompt` (String)
- `sessionId` (String) - For conversation continuity

### Advanced Java Action with Content Type

⚠️ **Note**: This approach is not recommended with the current implementation. Session ID is mandatory. Use the Session + Content Type approach below instead.

### Session + Content Type Java Action

For applications requiring both session support and custom content types:

```java
// BEGIN USER CODE
return com.company.mendix.n8n.N8nAction.execute(apiKey, apiEndPoint, userPrompt, sessionId, contentType);
// END USER CODE
```

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
   const sessionId = $request.headers['x-session-id'];
   
   return [{
     json: {
       result: "processed",
       original_data: inputData,
       session_id: sessionId,
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
   - `webhookURL` (String): Your n8n webhook URL
   - `requestData` (String): Any text content (automatically converted to JSON)
   - `sessionId` (String): Session ID for conversation continuity (required)

2. **Call Java Action**:
   - Drag `CallN8nWebhook` into microflow
   - Map parameters:
     - apiKey: Leave empty for public webhooks or use your API key
     - apiEndPoint: $webhookURL
     - userPrompt: $requestData (can be plain text or JSON)
     - sessionId: $sessionId (required - generate if not provided)

3. **Process Response**:
   - Store result in variable
   - Parse JSON if needed
   - Handle errors appropriately

## Automatic JSON Formatting

### ✅ **Great News**: No More JSON Formatting Required!

The Java Action now automatically handles JSON formatting for you. You can pass either:

### **Option 1: Plain Text (Recommended)**
Just pass your text directly - it will be automatically wrapped in JSON:

**Input:**
```text
We're in our annual strategic planning process and the C-suite is divided...
```

**Automatically becomes:**
```json
{"message": "We're in our annual strategic planning process and the C-suite is divided..."}
```

### **Option 2: Pre-formatted JSON (Advanced)**
If you need custom JSON structure, you can still pass JSON directly:

**Input:**
```json
{"message": "Your text", "user": "john", "priority": "high"}
```

**Result:**
Your JSON is passed through unchanged.

### **How It Works:**
- ✅ **Plain Text Detection**: If input doesn't start with `{` or `[`, it's wrapped as `{"message": "your text"}`
- ✅ **JSON Passthrough**: If input starts with `{` or `[`, it's sent as-is
- ✅ **Automatic Escaping**: Special characters (quotes, newlines, etc.) are automatically escaped
- ✅ **Safe Handling**: Empty/null input becomes `{"message": ""}`

### **No More 422 Errors!**
The automatic JSON wrapping eliminates the "Failed to parse request body" error you encountered.

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

4. **Missing Session ID**:
   ```java
   if (result.contains("Session ID is required")) {
       // Handle missing session ID error
       // Generate session ID or prompt user
   }
   ```

3. **Timeout Issues**:
   ```java
   // Use extended timeout for long-running workflows
   return com.company.mendix.n8n.N8nAction.executeWithTimeout(
       apiKey, apiEndPoint, userPrompt, sessionId, 20 // 20 minutes
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
String sessionId = "order-" + $Order/OrderNumber + "-" + System.currentTimeMillis();
String result = com.company.mendix.n8n.N8nAction.execute(
    $apiKey, 
    "https://your-n8n.com/webhook/process-order", 
    orderData,
    sessionId
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
String sessionId = "file-" + $Document/Name + "-" + System.currentTimeMillis();
String result = com.company.mendix.n8n.N8nAction.executeWithTimeout(
    $apiKey,
    "https://your-n8n.com/webhook/process-file",
    fileData,
    sessionId,
    30 // 30 minutes timeout
);
```

### Example 3: Chatbot with Session Memory

**Mendix Side:**
```java
// Generate or retrieve session ID for user
String sessionId = $ChatSession/SessionId;
if (sessionId == null || sessionId.isEmpty()) {
    sessionId = "chat-" + $CurrentUser/Name + "-" + System.currentTimeMillis();
    // Save session ID to ChatSession entity
    $ChatSession/SessionId = sessionId;
}

String chatData = "{" +
    "\"message\": \"" + $UserMessage + "\"," +
    "\"user_id\": \"" + $CurrentUser/Name + "\"," +
    "\"timestamp\": \"" + java.time.Instant.now().toString() + "\"" +
"}";

// Call n8n chatbot with session support
String result = com.company.mendix.n8n.N8nAction.execute(
    $apiKey,
    "https://your-n8n.com/webhook/chatbot",
    chatData,
    sessionId
);
```

**n8n Workflow with Simple Memory:**
- Webhook → Simple Memory → AI Chatbot → Respond
- The Simple Memory node automatically uses the x-session-id header to maintain conversation context

## Testing Your Implementation

### 1. Test Webhook Connectivity

Create a simple test microflow with your n8n webhook URL and session ID.

### 2. Validate Response Handling

Test with different response formats from n8n to ensure your parsing logic works.

### 3. Error Scenario Testing

Test with:
- Invalid webhook URLs
- Network disconnection
- n8n instance downtime
- Malformed JSON data
- Empty or null session IDs

## Best Practices

1. **Always handle errors gracefully**
2. **Use appropriate timeouts for your use case**
3. **Log webhook calls for debugging**
4. **Validate JSON before sending**
5. **Use environment-specific webhook URLs**
6. **Implement retry logic for critical operations**
7. **Manage session IDs appropriately for conversational flows**
8. **Generate unique session IDs per user/conversation**
9. **Store session IDs in persistent entities for ongoing conversations**

## Session ID Management

### Generating Session IDs
Always ensure you have a valid session ID before calling the n8n webhook:

```java
// Option 1: Generate based on user and timestamp
String sessionId = "user-" + $CurrentUser/Name + "-" + System.currentTimeMillis();

// Option 2: Generate based on conversation context
String sessionId = "chat-" + $ConversationId + "-" + System.currentTimeMillis();

// Option 3: Use existing session ID from entity
String sessionId = $ChatSession/SessionId != null && !$ChatSession/SessionId.isEmpty() ? 
                   $ChatSession/SessionId : 
                   "new-session-" + System.currentTimeMillis();
```

### Session ID Validation
```java
// Validate session ID before calling
if (sessionId == null || sessionId.trim().isEmpty()) {
    // Generate a new session ID
    sessionId = "generated-" + System.currentTimeMillis();
}
```

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| "Class not found" | Refresh Mendix project, check JAR in userlib |
| "Session ID is required" | Ensure SessionId parameter is provided and not empty |
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
