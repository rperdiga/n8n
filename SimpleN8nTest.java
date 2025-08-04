import com.company.mendix.n8n.N8nAction;

/**
 * Simple test class for n8n integration
 */
public class SimpleN8nTest {
    
    public static void main(String[] args) {
        System.out.println("Testing n8n Java Action...");
        
        // Test with sample webhook URL
        String apiKey = null; // Public webhook
        String webhookEndpoint = "https://your-n8n-instance.com/webhook/test";
        String inputData = "{\"message\": \"Hello from Mendix test!\", \"timestamp\": \"" + 
                          System.currentTimeMillis() + "\"}";
        
        String result = N8nAction.execute(apiKey, webhookEndpoint, inputData);
        
        System.out.println("Result: " + result);
    }
}
