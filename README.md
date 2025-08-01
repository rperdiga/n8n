# Mendix Langflow Integration

A Java Action for Mendix Studio Pro that enables seamless integration with Langflow APIs. This library provides a simple, drag-and-drop solution for calling Langflow workflows from Mendix applications.

## 🚀 Features

- ✅ **Easy Integration**: Simple Java Action for Mendix Studio Pro
- ✅ **API Key Authentication**: Secure authentication with Langflow APIs  
- ✅ **Extended Timeouts**: Configurable timeouts (up to 60 minutes) for long-running processes
- ✅ **Error Handling**: Comprehensive validation and error messages
- ✅ **Zero Dependencies**: Pure Java implementation using built-in HTTP client
- ✅ **Lightweight**: Small JAR file (~7KB) with no external dependencies

## 📋 Requirements

- **Java 11+**
- **Mendix Studio Pro**
- **Langflow Server** (running and accessible)

## 🛠️ Installation

### 1. Build the JAR

```bash
./gradlew shadowJar
```

This creates `build/libs/Langflow-1.0.0-mendix.jar`

### 2. Deploy to Mendix Project

#### Option A: Manual Copy
```bash
cp build/libs/Langflow-1.0.0-mendix.jar /path/to/your/mendix/project/userlib/
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
2. **Name**: `CallLangflow` (or your preferred name)
3. **Add Parameters**:
   - `apiKey` (String) - Your Langflow API key
   - `apiEndpoint` (String) - Complete Langflow API endpoint URL
   - `userPrompt` (String) - User prompt/question to send to Langflow
4. **Set Return Type**: `String`
5. **Java Code**:
   ```java
   // BEGIN USER CODE
   return com.company.mendix.langflow.LangflowAction.execute(apiKey, apiEndpoint, userPrompt);
   // END USER CODE
   ```

### Use in Microflows

1. Drag the `CallLangflow` Java Action into your microflow
2. Set the input parameters:
   - **apiKey**: Your Langflow API key
   - **apiEndpoint**: Your Langflow endpoint URL (e.g., `http://localhost:7860/api/v1/run/your-flow-id`)
   - **userPrompt**: The user's question or prompt
3. Use the returned string in your application logic

## 📚 API Reference

### Basic Method (10-minute timeout)
```java
LangflowAction.execute(String apiKey, String apiEndpoint, String userPrompt)
```

### Custom Timeout Method
```java
LangflowAction.executeWithTimeout(String apiKey, String apiEndpoint, String userPrompt, int timeoutMinutes)
```

### Full Control Method
```java
LangflowAction.execute(String apiKey, String apiEndpoint, String userPrompt, String outputType, String inputType, int timeoutMinutes)
```

## ⏱️ Timeout Configuration

| Use Case | Recommended Timeout | Method |
|----------|-------------------|--------|
| Simple text processing | 10 minutes (default) | `execute()` |
| Complex AI workflows | 15-20 minutes | `executeWithTimeout()` |
| Document analysis | 20-30 minutes | `executeWithTimeout()` |
| Large data processing | 30+ minutes | `executeWithTimeout()` |

## 🔗 API Integration

### Request Format
The library sends HTTP POST requests to Langflow with this structure:

```json
{
    "output_type": "text",
    "input_type": "text", 
    "input_value": "user prompt here"
}
```

### Headers
```
Content-Type: application/json
x-api-key: your-api-key-here
```

### Response Processing
The library automatically extracts meaningful content from Langflow responses:
1. Searches for structured response in `outputs[].results.message.text`
2. Falls back to common fields like `text`, `message`, `response`
3. Returns raw response if parsing fails

## 🛡️ Error Handling

The library provides comprehensive validation:

- **API Key Validation**: "API Key is required and cannot be empty"
- **Endpoint Validation**: "API Endpoint must be a valid URL"
- **Input Validation**: "User prompt is required and cannot be empty"
- **HTTP Errors**: "API request failed with status code: XXX"
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
java -cp "build/classes" com.company.mendix.langflow.examples.LangflowExample
```

## 📁 Project Structure

```
src/
├── main/java/com/company/mendix/langflow/
│   ├── LangflowAction.java          # Main implementation
│   └── examples/
│       └── LangflowExample.java     # Usage examples
└── test/java/com/company/mendix/langflow/
    └── LangflowActionTest.java      # Unit tests

deploy-to-mendix.ps1                 # Automated deployment script
quick-deploy.bat                     # Quick deployment batch file
build.gradle                         # Build configuration
```

## 🚀 Quick Start Example

```java
// In your Mendix Java Action
String apiKey = "your-langflow-api-key";
String apiEndpoint = "http://localhost:7860/api/v1/run/your-flow-id";
String userPrompt = "What is machine learning?";

// Simple call (10-minute timeout)
String result = LangflowAction.execute(apiKey, apiEndpoint, userPrompt);

// Custom timeout for complex processes
String result = LangflowAction.executeWithTimeout(apiKey, apiEndpoint, userPrompt, 20);
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
   - Check Langflow server performance

3. **Connection errors**
   - Verify Langflow server is running
   - Check endpoint URL and API key
   - Confirm network connectivity

### Debug Output
The library outputs debug information to help with troubleshooting:
```
Executing Langflow API call to endpoint: http://localhost:7860/...
User prompt length: 25 characters
Request payload: {"output_type":"text",...}
API request sent, waiting for Langflow response (timeout: 10 minutes)...
Response status code: 200
```

## Python to Java Migration

This Java implementation replicates the functionality of this Python code:

```python
payload = {
    "output_type": "text",
    "input_type": "text", 
    "input_value": "hello world!"
}

headers = {
    "Content-Type": "application/json",
    "x-api-key": api_key
}

response = requests.request("POST", url, json=payload, headers=headers)
```

**Equivalent Java Action call:**
```java
String result = LangflowAction.execute(apiKey, apiEndpoint, userPrompt);
```

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 🔗 Links

- [Langflow Documentation](https://docs.langflow.org/)
- [Mendix Documentation](https://docs.mendix.com/)
- [Java Action Documentation](https://docs.mendix.com/howto/logic-business-rules/java-actions/)

---

**Made with ❤️ for Mendix and Langflow integration**
