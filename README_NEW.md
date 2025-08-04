# Mendix n8n Integration

A Java Action for Mendix Studio Pro that enables seamless integration with n8n webhooks. This library provides a simple, drag-and-drop solution for calling n8n workflows from Mendix applications.

## 🚀 Features

- ✅ **Easy Integration**: Simple Java Action for Mendix Studio Pro
- ✅ **Webhook Support**: Call n8n webhooks with custom payloads
- ✅ **Optional Authentication**: Support for secured webhooks with API keys
- ✅ **Extended Timeouts**: Configurable timeouts (up to 60 minutes) for long-running workflows
- ✅ **Multiple Content Types**: Support for JSON, XML, plain text, and custom content types
- ✅ **Error Handling**: Comprehensive validation and error messages
- ✅ **Zero Dependencies**: Pure Java implementation using built-in HTTP client
- ✅ **Lightweight**: Small JAR file (~7KB) with no external dependencies

## 📋 Requirements

- **Java 11+**
- **Mendix Studio Pro**
- **n8n Instance** (self-hosted or cloud)

## 🛠️ Installation

### 1. Build the JAR

```bash
./gradlew shadowJar
```

This creates `build/libs/n8n-1.0.0-mendix.jar`

### 2. Deploy to Mendix Project

#### Option A: Manual Copy
```bash
cp build/libs/n8n-1.0.0-mendix.jar /path/to/your/mendix/project/userlib/
```

#### Option B: Automated Deployment (Windows)
```powershell
# Edit deploy-to-mendix.ps1 to set your Mendix project path
.\deploy-to-mendix.ps1
```

#### Option C: Quick Deploy (Windows)
```batch
quick-deploy.bat
```

### 3. Refresh Mendix Project

Refresh your Mendix project in Studio Pro to recognize the new JAR file.

## 🔧 Usage in Mendix Studio Pro

### Create Java Action

1. **Right-click** on a module → **Add** → **Java Action**
2. **Name**: `CallN8nWebhook` (or your preferred name)
3. **Add Parameters**:
   - `apiKey` (String) - Your n8n API key (optional for public webhooks)
   - `webhookEndpoint` (String) - Complete n8n webhook URL
   - `inputData` (String) - JSON payload to send to n8n
4. **Set Return Type**: `String`
5. **Java Code**:
   ```java
   // BEGIN USER CODE
   return com.company.mendix.n8n.N8nAction.execute(apiKey, webhookEndpoint, inputData);
   // END USER CODE
   ```

### Use in Microflows

1. Drag the `CallN8nWebhook` Java Action into your microflow
2. Set the input parameters:
   - **apiKey**: Your n8n API key (leave empty for public webhooks)
   - **webhookEndpoint**: Your n8n webhook URL (e.g., `https://your-n8n.com/webhook/workflow-id`)
   - **inputData**: JSON string with your data (e.g., `{"user": "john", "action": "process"}`)
3. Use the returned string in your application logic

## 📚 API Reference

### Basic Method (10-minute timeout)
```java
N8nAction.execute(String apiKey, String webhookEndpoint, String inputData)
```

### Custom Content Type Method
```java
N8nAction.execute(String apiKey, String webhookEndpoint, String inputData, String contentType)
```

### Custom Timeout Method
```java
N8nAction.executeWithTimeout(String apiKey, String webhookEndpoint, String inputData, int timeoutMinutes)
```

### Full Control Method
```java
N8nAction.execute(String apiKey, String webhookEndpoint, String inputData, String contentType, int timeoutMinutes)
```

## ⏱️ Timeout Configuration

| Use Case | Recommended Timeout | Method |
|----------|-------------------|--------|
| Simple webhook calls | 10 minutes (default) | `execute()` |
| Data processing workflows | 15-20 minutes | `executeWithTimeout()` |
| File processing | 20-30 minutes | `executeWithTimeout()` |
| Large data workflows | 30+ minutes | `executeWithTimeout()` |

## 🔗 Webhook Integration

### Request Format
The library sends HTTP POST requests to n8n webhooks with your custom payload:

```json
{
    "user": "john_doe",
    "action": "process_order",
    "order_id": 12345,
    "items": ["item1", "item2"]
}
```

### Headers
```
Content-Type: application/json (or custom)
Authorization: Bearer your-api-key-here (if provided)
```

### Response Processing
The library automatically processes n8n webhook responses:
1. Searches for structured response in common fields like `result`, `data`, `message`, `response`
2. Returns raw response if no structured data found
3. Handles various content types returned by n8n

## 🛡️ Error Handling

The library provides comprehensive validation:

- **Endpoint Validation**: "Webhook endpoint is required and cannot be empty"
- **URL Validation**: "Webhook endpoint must be a valid URL"
- **HTTP Errors**: "Webhook request failed with status code: XXX"
- **Timeout Errors**: "Request timeout after X minutes"

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Examples
```bash
./gradlew run
# or
java -cp "build/classes" com.company.mendix.n8n.examples.N8nExample
```

## 📁 Project Structure

```
src/
├── main/java/com/company/mendix/n8n/
│   ├── N8nAction.java                   # Main implementation
│   └── examples/
│       └── N8nExample.java              # Usage examples
└── test/java/com/company/mendix/n8n/
    └── N8nActionTest.java               # Unit tests

deploy-to-mendix.ps1                     # Automated deployment script
quick-deploy.bat                         # Quick deployment batch file
build.gradle                             # Build configuration
```

## 🚀 Quick Start Examples

### Example 1: Basic Webhook Call
```java
// In your Mendix Java Action
String apiKey = null; // No authentication needed
String webhookEndpoint = "https://your-n8n.com/webhook/simple-workflow";
String inputData = "{\"message\": \"Hello from Mendix!\", \"user\": \"john\"}";

String result = N8nAction.execute(apiKey, webhookEndpoint, inputData);
```

### Example 2: Authenticated Webhook
```java
String apiKey = "your-n8n-api-key";
String webhookEndpoint = "https://your-n8n.com/webhook/secure-workflow";
String inputData = "{\"action\": \"process_order\", \"order_id\": 12345}";

String result = N8nAction.execute(apiKey, webhookEndpoint, inputData);
```

### Example 3: Custom Timeout for Long Workflows
```java
String result = N8nAction.executeWithTimeout(apiKey, webhookEndpoint, inputData, 20);
```

### Example 4: Custom Content Type
```java
String xmlData = "<?xml version=\"1.0\"?><order><id>123</id></order>";
String result = N8nAction.execute(apiKey, webhookEndpoint, xmlData, "application/xml");
```

## 🔧 Development

### Build Commands
```bash
# Compile only
./gradlew compileJava

# Build JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

### Deployment Scripts
- **PowerShell**: `deploy-to-mendix.ps1` - Full deployment with verification
- **Batch**: `quick-deploy.bat` - Quick rebuild and deploy

## 🐛 Troubleshooting

### Common Issues

1. **"Class not found" in Mendix**
   - Ensure JAR is in `userlib` folder
   - Refresh Mendix Studio Pro

2. **Timeout errors**
   - Increase timeout using `executeWithTimeout()`
   - Check n8n workflow performance

3. **Connection errors**
   - Verify n8n instance is running and accessible
   - Check webhook URL and API key (if required)
   - Confirm network connectivity

4. **Authentication errors**
   - Verify API key is correct
   - Check if webhook requires authentication
   - Ensure webhook is published and active

### Debug Output
The library outputs debug information to help with troubleshooting:
```
Executing n8n webhook call to endpoint: https://your-n8n.com/webhook/...
Input data length: 45 characters
Request payload: {"message":"Hello from Mendix!"}
API request sent, waiting for n8n response (timeout: 10 minutes)...
Response status code: 200
```

## 📊 n8n Workflow Examples

### Example Workflow 1: Data Processing
```json
{
  "trigger": "webhook",
  "nodes": [
    {
      "name": "Webhook",
      "type": "webhook",
      "parameters": {
        "path": "process-data"
      }
    },
    {
      "name": "Process Data",
      "type": "function",
      "parameters": {
        "code": "return [{ json: { result: 'processed', data: items[0].json } }];"
      }
    },
    {
      "name": "Return Response",
      "type": "respond",
      "parameters": {
        "response": "{{ $json.result }}"
      }
    }
  ]
}
```

### Example Workflow 2: Email Notification
```json
{
  "trigger": "webhook",
  "nodes": [
    {
      "name": "Webhook",
      "type": "webhook",
      "parameters": {
        "path": "send-notification"
      }
    },
    {
      "name": "Send Email",
      "type": "emailSend",
      "parameters": {
        "to": "{{ $json.email }}",
        "subject": "Notification from Mendix",
        "text": "{{ $json.message }}"
      }
    }
  ]
}
```

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 🔗 Links

- [n8n Documentation](https://docs.n8n.io/)
- [n8n Webhook Documentation](https://docs.n8n.io/integrations/builtin/core-nodes/n8n-nodes-base.webhook/)
- [Mendix Documentation](https://docs.mendix.com/)
- [Java Action Documentation](https://docs.mendix.com/howto/logic-business-rules/java-actions/)

---

**Made with ❤️ for Mendix and n8n integration**
