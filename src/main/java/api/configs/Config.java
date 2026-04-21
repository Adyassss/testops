package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();
    public static final String ADMIN_USERNAME_KEY = "admin.username";
    public static final String ADMIN_PASSWORD_KEY = "admin.password";


    private Config(){
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if(input == null){
                throw new RuntimeException("config.properties not found");
            }
            properties.load(input);
        }
        catch (IOException e){
            throw new RuntimeException("config.properties not load");
        }
    }

    public static String getProperty(String key) {
        String fromFile = INSTANCE.properties.getProperty(key);
        if (fromFile == null) {
            return null;
        }
        return switch (key) {
            // Keep backward compatibility: some scripts/export use NBANK_*,
            // while Dockerfile/build args use APIBASEURL/UIBASEURL.
            case "server" -> firstNonBlank(
                    firstNonBlank(System.getenv("NBANK_SERVER"), System.getenv("APIBASEURL")),
                    fromFile
            );
            case "baseUrl" -> firstNonBlank(
                    firstNonBlank(System.getenv("NBANK_BASE_URL"), System.getenv("UIBASEURL")),
                    fromFile
            );
            default -> fromFile;
        };
    }

    private static String firstNonBlank(String override, String fallback) {
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        if (fallback == null) {
            return null;
        }
        return fallback.trim();
    }
}
