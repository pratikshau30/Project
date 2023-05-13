package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import static org.junit.Assert.assertFalse;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(description = "Verify registration happens correctly", priority = 1,
            groups = {"Sanity_test"})
    @Parameters({"username", "passward"})
    public void TestCase01(@Optional("testUser") String username,
            @Optional("abc@123") String password) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, password);
        // logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        // logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status
        // ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase1");
    }

    @Test(description = "verify re-registering an already existed user fails", priority = 2,
            groups = {"Sanity_test"})
    @Parameters({"TestCase01_un", "TestCase01_pwd"})
    public void TestCase02(@Optional("testUser") String username,
            @Optional("abc@123") String password) throws InterruptedException {
        Boolean status;
        // takeScreenshot(driver, "StartTestCase", "TestCase1");
        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        assertTrue(status);
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;
        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, password, false);
        assertFalse(status);
        // takeScreenshot(driver, "EndTestCase", "TestCase2");
    }

    @Test(description = "Verify the functionality of search text box", priority = 3,
            groups = {"Sanity_test"})
    @Parameters("TC3_ProductNameToSearchFor")
    public void TestCase03(String productName) throws InterruptedException {
        // logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct(productName);
        assertTrue(status, "Unable to search for given product");

        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Unable to search for given product", "FAIL");
        // return false;
        // }

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();
        assertTrue(searchResults.size() > 0, "There were no results for the given search string");

        // Verify the search results are available
        // if (searchResults.size() == 0) {
        // logStatus("TestCase 3", "Test Case Failure. There were no results for the given search
        // string", "FAIL");
        // return false;
        // }

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            assertTrue(elementText.toUpperCase().contains(productName),
                    "Test Results contains un-expected values: " + elementText);
        }
        // if (!elementText.toUpperCase().contains("YONEX")) {
        // logStatus("TestCase 3", "Test Case Failure. Test Results contains un-expected values: " +
        // elementText,
        // "FAIL");
        // // return false;



        // logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        assertFalse(status);
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");
        // return false;
        // }

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        assertTrue(searchResults.size() == 0 && homePage.isNoResultFound(),
                "Test Case Failure. Expected: no results , actual: Results were available");
        // if (searchResults.size() == 0) {
        // if (homePage.isNoResultFound()) {
        // // logStatus("Step Success", "Successfully validated that no products found message is
        // displayed", "PASS");
        // }
        // //logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found for
        // the given text",
        // // "PASS");
        // } else {
        // // logStatus("TestCase 3", "Test Case Fail. Expected: no results , actual: Results were
        // available", "FAIL");
        // //return false;
        // }

        // return true;
    }

    @Test(description = "Verify the existence of size chart for certain items and validate contents of size chart",
            priority = 4, groups = {"Regression_Test"})
    @Parameters({"TC4_ProductNameToSearchFor"})
    public void TestCase04(String productName) throws InterruptedException {
        // logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        // "Running Shoes"
        // Search for product and get card content element of search results
        status = homePage.searchForProduct(productName);
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            assertTrue(result.verifySizeChartExists(), "Size Chart Link does not exist");

            // if (result.verifySizeChartExists()) {
            // logStatus("Step Success", "Successfully validated presence of Size Chart Link",
            // "PASS");

            // Verify if size dropdown exists
            assertTrue(result.verifyExistenceofSizeDropdown(driver),
                    "Size dropdown does not exist");

            // status = result.verifyExistenceofSizeDropdown(driver);
            // logStatus("Step Success", "Validated presence of drop down", status ? "PASS" :
            // "FAIL");

            // Open the size chart
            assertTrue(result.openSizechart() && result
                    .validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver),
                    "Failed to open Size Chart");

            // if (result.openSizechart()) {
            // // Verify if the size chart contents matches the expected values
            // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
            // driver)) {
            // logStatus("Step Success", "Successfully validated contents of Size Chart Link",
            // "PASS");
            // } else {
            // logStatus("Step Failure", "Failure while validating contents of Size Chart Link",
            // "FAIL");
            // status = false;
            // }

            // Close the size chart modal
            assertTrue(result.closeSizeChart(driver), "Failed to close Size Chart");
        }
        // status = result.closeSizeChart(driver);

    }

    @Test(description = "Verify that a new user can add multiple products in to the cart and Checkout",
            priority = 5, groups = {"Sanity_test"})
    @Parameters({"TC5_ProductNameToSearchFor", "TC5_ProductNameToSearchFor2", "TC5_AddressDetails"})


    public void TestCase05(String productName, String productName2, String address)
            throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "TestCase2: Failed to verify user Registration");
        // if (!status) {
        // logStatus("TestCase 5", "Test Case Failure. Happy Flow Test Failed", "FAIL");
        // }

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, " able to login");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ? "PASS" :
        // "FAIL");
        // }

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(productName);
        assertTrue(status);
        homePage.addProductToCart(productName);
        status = homePage.searchForProduct(productName2);
        assertTrue(status);
        homePage.addProductToCart(productName2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status);

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" :
        // "FAIL");
        // return status;
    }

    @Test(description = "Verify that the contents of the cart can be edited", priority = 6,
            groups = {"Regression_Test"})
    @Parameters({"ProductNameToSearch1", "ProductNameToSearch2"})
    public void TestCase06(String product1, String product2) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, " able to registered");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Register Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ?
        // "PASS" : "FAIL");
        // return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status);
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ?
        // "PASS" : "FAIL");
        // return false;
        // }

        homePage.navigateToHome();
        status = homePage.searchForProduct(product1);
        assertTrue(status, "product found");
        homePage.addProductToCart(product1);

        status = homePage.searchForProduct(product2);
        assertTrue(status);
        homePage.addProductToCart(product2);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(product1, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(product2, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(product1, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
            System.out.println("false");
            // return false;
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status);

        homePage.navigateToHome();
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ?
        // "PASS" : "FAIL");
        // return status;
    }

    @Test(description = "Verify that the contents made to the cart are saved against the user's login details",
            priority = 7, groups = {"Regression_Test"})
    @Parameters({"TC7_ListOfProductsToAddToCart"})
    public void TestCase07(String TC7_ListOfProductsToAddToCart) throws InterruptedException {
        Boolean status = false;
        List<String> expectedResult = Arrays.asList(TC7_ListOfProductsToAddToCart.split(";"));


        // logStatus("Start TestCase", "Test Case 7: Verify that cart contents are persisted after
        // logout", "DONE");

        Register registration = new Register(driver);
        Login login = new Login(driver);
        Home homePage = new Home(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, " able to registered");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 7: Verify that cart contents are persited after
        // logout: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, " able to login");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 7: Verify that cart contents are persited after
        // logout: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }

        homePage.navigateToHome();
        status = homePage.searchForProduct(expectedResult.get(0));
        assertTrue(status);
        homePage.addProductToCart(expectedResult.get(0));

        status = homePage.searchForProduct(expectedResult.get(1));
        assertTrue(status);
        homePage.addProductToCart(expectedResult.get(1));

        homePage.PerformLogout();

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");

        status = homePage.verifyCartContents(expectedResult);
        // assertTrue(status);

        // logStatus("End TestCase", "Test Case 7: Verify that cart contents are persisted after
        // logout: ",
        // status ? "PASS" : "FAIL");

        homePage.PerformLogout();

    }

    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough",
            priority = 8, groups = {"Sanity_test"})
    @Parameters({"TC8_ProductName", "TC8_Qty"})
    public void TestCase08(String productName, int qt) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase",
        // "Test Case 8: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough",
        // "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "able to registered");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Registration Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase",
        // "Test Case 8: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "able to login");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase",
        // "Test Case 8: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(productName);
        assertTrue(status);
        homePage.addProductToCart(productName);

        homePage.changeProductQuantityinCart(productName, qt);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

        // logStatus("End TestCase",
        // "Test Case 8: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough: ",
        // status ? "PASS" : "FAIL");

        // return status;
    }

    @Test(dependsOnMethods = "TestCase10",
            description = "verify that a product added to the cart is available when a new tab is added",
            priority = 10, groups = {"Regression_Test"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 9: Verify that product added to cart is available when a new tab is opened",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,
                "Test Case Failure. Verify that product added to cart is available when a new tab is opened");
        // if (!status) {
        // logStatus("TestCase 9",
        // "Test Case Failure. Verify that product added to cart is available when a new tab is
        // opened",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        assertTrue(status,
                "Test Case 9: Verify that product added to cart is available when a new tab is opened");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase09");
        // return status;
    }


    @Test(description = "Verify that privacy policy and about us links are working fine",
            priority = 9, groups = {"Regression_Test"})
    public void TestCase10() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase10");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "able to registered");
        // if (!status) {
        // logStatus("TestCase 10",
        // "Test Case Failure. Verify that the Privacy Policy, About Us are displayed correctly ",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase10");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "able to login");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase10");
        // logStatus("End TestCase",
        // "Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        assertTrue(status);

        // if (!status) {
        // logStatus("Step Failure", "Verifying parent page url didn't change on privacy policy link
        // click failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase10");
        // logStatus("End TestCase",
        // "Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        assertTrue(status);
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page heading
        // failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase10");
        // logStatus("End TestCase",
        // "Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        assertTrue(status);
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page heading
        // failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase10");
        // logStatus("End TestCase",
        // "Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "PASS");
        // takeScreenshot(driver, "EndTestCase", "TestCase10");

        // return status;
    }

    @Test(description = "Verify that the contact us dialog works fine", priority = 11,
            groups = {"Regression_Test"})
    @Parameters({"TC11_ContactusUserName", "TC11_ContactUsEmail", "TC11_QueryContent"})
    public void TestCase11(String contactus, String contactemail, String query)
            throws InterruptedException {
        // logStatus("Start TestCase",
        // "Test Case 11: Verify that contact us option is working correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase11");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(contactus);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(contactemail);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(query);

        WebElement contactUs = driver.findElement(By.xpath(
                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // logStatus("End TestCase",
        // "Test Case 11: Verify that contact us option is working correctly ",
        // "PASS");

        // takeScreenshot(driver, "EndTestCase", "TestCase11");

        // return true;
    }

    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable",
            priority = 12, groups = {"Sanity_test"})
    @Parameters({"ProductNameToSearch", "TC12_AddresstoAdd"})
    public void TestCase12(String product, String address) throws InterruptedException {
        Boolean status = false;
        // logStatus("Start TestCase",
        // "Test Case 12: Ensure that the links on the QKART advertisement are clickable",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase12");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "able to registered");
        // if (!status) {
        // logStatus("TestCase 12",
        // "Test Case Failure. Ensure that the links on the QKART advertisement are clickable",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase12");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "login");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase 12");
        // logStatus("End TestCase",
        // "Test Case 12: Ensure that the links on the QKART advertisement are clickable",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(product);
        assertTrue(status);
        homePage.addProductToCart(product);
        homePage.changeProductQuantityinCart(product, 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        assertTrue(status);
        // logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" :
        // "FAIL");

        WebElement Advertisement1 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        assertTrue(status);
        // logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" :
        // "FAIL");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        assertTrue(status);
        // logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" :
        // "FAIL");

        // logStatus("End TestCase",
        // "Test Case 12: Ensure that the links on the QKART advertisement are clickable",
        // status ? "PASS" : "FAIL");
        // return status;
    }



    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    // public static void logStatus(String type, String message, String status) {

    // System.out.println(String.format("%s | %s | %s | %s",
    // String.valueOf(java.time.LocalDateTime.now()), type,
    // message, status));
    // }

    public static void takeScreenshot(String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
