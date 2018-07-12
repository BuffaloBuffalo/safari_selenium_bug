package com.zappos.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SafariStaleWaitExampleTest {

    WebDriver driver;
    Wait<WebDriver> wait;

    @BeforeMethod
    public void setup() {
        driver = getDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @AfterMethod
    public void shutdown(ITestResult result) {
        if (driver != null) {
            if (!result.isSuccess()) {
                savePageContent(result.getMethod().getMethodName() + ".html");
            }
            driver.quit();
            driver = null;
            wait = null;
        }
    }

    private void savePageContent(final String name) {
        Path path = Paths.get("", name);
        try {
            String html = driver.getPageSource();
            Files.write(path, html.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException | WebDriverException e) {
            System.out.println("unable to save page html to " + path.toAbsolutePath() + ": " + e);
        }
    }

    @Test
    public void testWaitForStaleZappos() {
        driver.get("https://zappos.com");
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchAll")));
        input.sendKeys("kelty h2go");
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Search']")));
        searchButton.click();
        wait.until(ExpectedConditions.stalenessOf(searchButton));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("h1"), "Kelty"));
        By cartLocator1 = By.xpath("//button[text()='Add to Cart']");
        WebElement cartButton = wait.until(d -> {
            List<WebElement> results = d.findElements(cartLocator1);
            if (results.isEmpty()) {
                return null;
            }
            return results.stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
        });
        System.out.println("Clicking cart button");
        cartButton.click();
        wait.until(ExpectedConditions.stalenessOf(cartButton));
        // This next line is never reached on Safari.
        WebElement continueShopping = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Continue Shopping']")));
        System.out.println("Class of continue shopping link: "
                + continueShopping.getAttribute("class"));
    }

    private static final String CHROME_DRIVER_PROP = "webdriver.chrome.driver";
    private static final String GECKO_DRIVER_PROP = "webdriver.gecko.driver";
    private static final Dimension DESKTOP_DIMENSION = new Dimension(960, 720);

    private WebDriver getDriver() {
        String browser = System.getProperty("browser", "").toLowerCase();
        switch (browser) {
        case "c":
        case "chrome":
            return getChromeDriver();
        case "ff":
        case "firefox":
            return getFirefoxDriver();
        default:
            return getSafariDriver();
        }
    }

    public FirefoxDriver getFirefoxDriver() {
        if (StringUtils.isBlank(System.getProperty(GECKO_DRIVER_PROP))) {
            System.setProperty(GECKO_DRIVER_PROP, "geckodriver-mac");
        }
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setLogLevel(FirefoxDriverLogLevel.ERROR);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().setSize(DESKTOP_DIMENSION);
        return driver;
    }

    public SafariDriver getSafariDriver() {
        SafariOptions safariOptions = new SafariOptions();
        SafariDriver driver = new SafariDriver(safariOptions);
        driver.manage().window().setSize(DESKTOP_DIMENSION);
        return driver;
    }

    public ChromeDriver getChromeDriver() {
        if (StringUtils.isBlank(System.getProperty(CHROME_DRIVER_PROP))) {
            System.setProperty(CHROME_DRIVER_PROP, "chromedriver-mac");
        }

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("no-sandbox");
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(DESKTOP_DIMENSION);
        return driver;
    }
}
