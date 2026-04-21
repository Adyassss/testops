package ui;

import java.util.Map;
import java.net.ServerSocket;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;

import static org.assertj.core.api.Assertions.assertThat;

import api.BaseTest;
import api.configs.Config;

import static com.codeborne.selenide.Selenide.switchTo;

@ExtendWith(BrowserMatchExtension.class)
@ExtendWith(AdminSessionExtension.class)
public class BaseUITest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.baseUrl = Config.getProperty("baseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
        Configuration.headless = true;
        String remote = firstNonBlank(
                System.getenv("SELENIUM_REMOTE_URL"),
                System.getProperty("SELENIUM_REMOTE_URL")
        );
        if (remote != null && !remote.isBlank()) {
            Configuration.remote = remote.trim();
            return;
        }

        // In some restricted environments (including this Codex sandbox),
        // binding a local port is forbidden. Starting a local ChromeDriver will then fail
        // with "Unable to find a free port". If no remote Selenium is configured, skip UI tests.
        if (!canBindLocalPort()) {
            Assumptions.assumeTrue(false,
                    "UI tests require remote Selenium here. Set SELENIUM_REMOTE_URL " +
                            "(e.g. http://localhost:4444/wd/hub) or run infra/run-tests-with-docker-compose.sh ui");
        }
    }

    private static boolean canBindLocalPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }

    public <T> T checkAllertMassageAndAccept(String bankAllert) {
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAllert);
        alert.accept();
        return (T) this;
    }

    public static String getAuthToken() {
        return Selenide.executeJavaScript(
                "return window.localStorage.getItem('authToken');");
    }


}
