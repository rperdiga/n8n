# Parameter Migration Summary: Langflow to n8n

This document summarizes the parameter changes made when migrating from Langflow to n8n webhook integration.

## Package Structure Changes

### Old (Langflow)
```
com.company.mendix.langflow.LangflowAction
```

### New (n8n)
```
com.company.mendix.n8n.N8nAction
```

## Method Parameter Changes

### Basic Execute Method

**Langflow (Old):**
```java
LangflowAction.execute(String apiKey, String apiEndpoint, String userPrompt)
```

**n8n (New):**
```java
N8nAction.execute(String apiKey, String webhookEndpoint, String inputData)
```

### Parameter Mapping

| Langflow Parameter | n8n Parameter | Description |
|-------------------|---------------|-------------|
| `apiKey` | `apiKey` | **No change** - Authentication key (optional for n8n public webhooks) |
| `apiEndpoint` | `webhookEndpoint` | **Renamed** - Changed from Langflow API endpoint to n8n webhook URL |
| `userPrompt` | `inputData` | **Renamed & Expanded** - Changed from simple text prompt to flexible JSON payload |

### Full Method Signatures

**Langflow (Old):**
```java
// Basic method
LangflowAction.execute(String apiKey, String apiEndpoint, String userPrompt)

// Full method
LangflowAction.execute(String apiKey, String apiEndpoint, String userPrompt, 
                      String outputType, String inputType, int timeoutMinutes)

// Timeout method
LangflowAction.executeWithTimeout(String apiKey, String apiEndpoint, String userPrompt, 
                                 int timeoutMinutes)
```

**n8n (New):**
```java
// Basic method
N8nAction.execute(String apiKey, String webhookEndpoint, String inputData)

// Full method
N8nAction.execute(String apiKey, String webhookEndpoint, String inputData, 
                 String contentType, int timeoutMinutes)

// Timeout method
N8nAction.executeWithTimeout(String apiKey, String webhookEndpoint, String inputData, 
                            int timeoutMinutes)
```

## Parameter Purpose Changes

### API Key Usage

**Langflow:**
- Always required
- Used for Langflow API authentication

**n8n:**
- Optional (can be null)
- Only needed for secured webhooks
- Used with Bearer token authentication

### Endpoint/URL Parameter

**Langflow:**
- Specific Langflow API endpoint format
- Example: `http://localhost:7860/api/v1/run/flow-id`

**n8n:**
- Generic webhook URL
- Example: `https://your-n8n.com/webhook/workflow-name`

### Input Data Parameter

**Langflow:**
- Simple text prompt
- Used as `input_value` in JSON payload
- Fixed JSON structure: `{"output_type": "text", "input_type": "text", "input_value": "..."}`

**n8n:**
- Flexible JSON payload
- Can be any valid JSON structure
- Examples:
  - `{"message": "hello", "user": "john"}`
  - `{"action": "process", "data": {...}}`
  - Custom XML/text content

### Content Type Support

**Langflow:**
- Fixed to `application/json`
- Used predefined output/input types

**n8n:**
- Flexible content types
- Supports: JSON, XML, plain text, custom types
- Default: `application/json`

## Migration Steps for Existing Code

### Step 1: Update Package Imports
```java
// Old
import com.company.mendix.langflow.LangflowAction;

// New
import com.company.mendix.n8n.N8nAction;
```

### Step 2: Update Method Calls
```java
// Old
String result = LangflowAction.execute(apiKey, endpoint, prompt);

// New
String result = N8nAction.execute(apiKey, webhookUrl, jsonData);
```

### Step 3: Transform Data Format
```java
// Old (simple text)
String userPrompt = "What is machine learning?";

// New (JSON structure)
String inputData = "{\"question\": \"What is machine learning?\", \"context\": \"tutorial\"}";
```

### Step 4: Update Error Handling
```java
// Old error messages
if (result.contains("Error executing Langflow action"))

// New error messages
if (result.contains("Error executing n8n action"))
```

## Breaking Changes

### 1. Authentication
- **Langflow**: API key always required
- **n8n**: API key optional (public webhooks don't need it)

### 2. Data Structure
- **Langflow**: Fixed JSON structure with `input_value`
- **n8n**: Flexible JSON payload structure

### 3. Content Types
- **Langflow**: Only JSON supported
- **n8n**: Multiple content types (JSON, XML, text)

### 4. URL Format
- **Langflow**: Specific API endpoint format
- **n8n**: Standard webhook URL format

## Compatibility Notes

### Backward Compatibility
- **None** - This is a complete replacement, not an upgrade
- All calling code must be updated to use new parameters

### JAR File Changes
- **Old**: `Langflow-1.0.0-mendix.jar`
- **New**: `n8n-1.0.0-mendix.jar`

### Class Names
- **Old**: `LangflowAction`
- **New**: `N8nAction`

## Example Migration

### Before (Langflow)
```java
// Mendix Java Action parameters
String apiKey = "langflow-api-key";
String apiEndpoint = "http://localhost:7860/api/v1/run/flow-123";
String userPrompt = "Analyze this data";

// Method call
String result = LangflowAction.execute(apiKey, apiEndpoint, userPrompt);
```

### After (n8n)
```java
// Mendix Java Action parameters
String apiKey = "n8n-api-key"; // or null for public webhooks
String webhookEndpoint = "https://your-n8n.com/webhook/analyze-data";
String inputData = "{\"task\": \"analyze\", \"data\": \"sample data\", \"user\": \"analyst\"}";

// Method call
String result = N8nAction.execute(apiKey, webhookEndpoint, inputData);
```

## Testing Migration

### 1. Parameter Validation
Test all new parameter combinations to ensure they work as expected.

### 2. Error Handling
Verify error messages and handling work correctly with new parameters.

### 3. Content Types
Test different content types if using the advanced methods.

### 4. Authentication
Test both authenticated and public webhook scenarios.
