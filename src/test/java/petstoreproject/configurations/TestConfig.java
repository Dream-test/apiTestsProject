package petstoreproject.configurations;

public class TestConfig {
    private static final String BASE_URL = "https://petstore.swagger.io/v2/";
    private static final String API_KEY = "special-key";

    public String getBaseUrl() {
        return BASE_URL;
    }

    public String getApiKey() {
        return API_KEY;
    }
}
