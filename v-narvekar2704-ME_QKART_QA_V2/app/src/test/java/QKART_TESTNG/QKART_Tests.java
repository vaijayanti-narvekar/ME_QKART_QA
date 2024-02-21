package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.StringStartsWith;
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

@Listeners(ListenerClass.class)
public class QKART_Tests {

    public static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    public static RemoteWebDriver getDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        return driver;
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
         @Test(priority = 1,description = "Verify registration happens correctly",groups = {"Sanity_test"})
         @Parameters({"userName","password"})
         public void TestCase01(@Optional("testUser")String userName, @Optional("abc@123")String password) throws InterruptedException {
        Boolean status;
        //  logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
        //  takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        // status = registration.registerUser("testUser", "abc@123", true);
         status = registration.registerUser(userName, password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, password);
        //  logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        assertTrue(status, "Failed to Login");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        //  logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status
        //  ? "PASS" : "FAIL");
        //  takeScreenshot(driver, "EndTestCase", "TestCase1");
    }


    /*
     * Testcase02: Verify that an existing user is not allowed to re-register on QKart
     */
    @Test(priority = 2 ,description="Verify re-registering an already registered user fails",groups = {"Sanity_test"})
    public void TestCase02() throws InterruptedException {
   Boolean status;
   //  logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
   //  takeScreenshot(driver, "StartTestCase", "TestCase1");

   // Visit the Registration page and register a new user
   Register registration = new Register(driver);
   registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
   assertTrue(status, "Failed to register new user");

   // Save the last generated username
   lastGeneratedUserName = registration.lastGeneratedUsername;

   registration.navigateToRegisterPage();
   status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

   assertFalse(status, "Failed to register with existing username");

   // Visit the home page and log out the logged in user
   Home home = new Home(driver);
   status = home.PerformLogout();

}

//Verify the functionality of search text box
@Test(priority = 3 ,description="Verify the functionality of search text box",groups = {"Sanity_test"})
public void TestCase03() throws InterruptedException {
Boolean status;
    SoftAssert sa = new SoftAssert();
    Home homePage = new Home(driver);
    homePage.navigateToHome();
    Thread.sleep(2000);
    status = homePage.searchForProduct("yonex");
    sa.assertTrue(status , "Unable to search given product");

    List<WebElement> searchResults = homePage.getSearchResults();
    sa.assertFalse(searchResults.size()==0, "Thre are no results for given product");

    for (WebElement webElement : searchResults) {
        // Create a SearchResult object from the parent element
        SearchResult resultelement = new SearchResult(webElement);

        // Verify that all results contain the searched text
        String elementText = resultelement.getTitleofResult();
        sa.assertTrue(elementText.toUpperCase().contains("YONEX"),"Test Case Failure. Test Results contains un-expected values");
    }

    status = homePage.searchForProduct("Gesundheit");
    sa.assertFalse(!status,"Invalid keyword returned results");
    searchResults = homePage.getSearchResults();
    sa.assertTrue(searchResults.size() == 0,"Expected: no results , actual: Results were available");

    sa.assertAll();

// Visit the home page and log out the logged in user
Home home = new Home(driver);
status = home.PerformLogout();

}

//Verify the existence of size chart for certain items and validate contents of size chart
@Test(priority = 4 , groups = {"Regression_Test"},description="Verify the existence of size chart for certain items and validate contents of size chart")
public void TestCase04() throws InterruptedException {
Boolean status;

    Home homePage = new Home(driver);
    homePage.navigateToHome();
    Thread.sleep(2000);
    status = homePage.searchForProduct("UNIFACTOR Mens Running Shoes");
    assertTrue(status , "Unable to search given product");

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
            // if (result.verifySizeChartExists()) {
            //     logStatus("Step Success", "Successfully validated presence of Size Chart Link", "PASS");
            assertTrue(result.verifySizeChartExists(),"Failed to validate presence of Size Chart Link");

                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
            //    logStatus("Step Success", "Validated presence of drop down", status ? "PASS" : "FAIL");
            assertTrue(status,"Failed to validate presence of drop down");
                // Open the size chart
                //if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
            assertTrue(result.openSizechart(),"Failure while validating contents of Size Chart Link");    

                    // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver)) {
                    //     logStatus("Step Success", "Successfully validated contents of Size Chart Link", "PASS");
                    // } else {
                    //     logStatus("Step Failure", "Failure while validating contents of Size Chart Link", "FAIL");
                    //     status = false;
                    // }
            status = result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver);
            assertTrue(status,"Failure while validating contents of Size Chart Link 2");

                    // Close the size chart modal
            status = result.closeSizeChart(driver);

        }
        

// Visit the home page and log out the logged in user
Home home = new Home(driver);
status = home.PerformLogout();

}
//Verify that a new user can add multiple products in to the cart and Checkout
@Test(priority = 5 ,groups = {"Sanity_test"},description="Verify that a new user can add multiple products in to the cart and Checkout")
@Parameters({"productName1","productName2","address"})
public void TestCase05(String productName1, String productName2, String address) throws InterruptedException {
    Boolean status;
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();

    // Register a new user
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status,"Test Case Failure. Happy Flow Test Failed");

    // Save the username of the newly registered user
    lastGeneratedUserName = registration.lastGeneratedUsername;

    // Go to the login page
    Login login = new Login(driver);
    login.navigateToLoginPage();

    // Login with the newly registered user's credentials
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status,"User Perform Login Failed");

    // Go to the home page
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    // Find required products by searching and add them to the user's cart
    // status = homePage.searchForProduct("YONEX");
    // homePage.addProductToCart("YONEX Smash Badminton Racquet");
    status = homePage.searchForProduct(productName1);
    homePage.addProductToCart(productName1);

    // status = homePage.searchForProduct("Tan");
    // homePage.addProductToCart("Tan Leatherette Weekender Duffle");
    status = homePage.searchForProduct(productName2);
    homePage.addProductToCart(productName2);

    // Click on the checkout button
    homePage.clickCheckout();

    // Add a new address on the Checkout page and select it
    Checkout checkoutPage = new Checkout(driver);
    // checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
    // checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
    checkoutPage.addNewAddress(address);
    checkoutPage.selectAddress(address);

    // Place the order
    checkoutPage.placeOrder();

    WebDriverWait wait = new WebDriverWait(driver, 30);
    wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

    // Check if placing order redirected to the Thansk page
    status = driver.getCurrentUrl().endsWith("/thanks");

    assertTrue(status,"Test Case 5: Happy Flow Test Failed");

    // Visit the home page and log out the logged in user
    Home home = new Home(driver);
    status = home.PerformLogout();

}

//Verify that a new user can add multiple products in to the cart and Checkout
@Test(priority = 6 , groups = {"Regression_Test"},description="Verify that the contents of the cart can be edited")
@Parameters({"product1","product2"})
public void TestCase06(String product1,String product2) throws InterruptedException {
    Boolean status;
    
    Home homePage = new Home(driver);
    Register registration = new Register(driver);
    Login login = new Login(driver);

    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status,"User Perform Register Failed");
    
    lastGeneratedUserName = registration.lastGeneratedUsername;
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status,"User Perform Login Failed");

    homePage.navigateToHome();
    // status = homePage.searchForProduct("Xtend");
    // homePage.addProductToCart("Xtend Smart Watch");
    status = homePage.searchForProduct(product1);
    homePage.addProductToCart(product1);

    // status = homePage.searchForProduct("Yarine");
    // homePage.addProductToCart("Yarine Floor Lamp");
    status = homePage.searchForProduct(product2);
    homePage.addProductToCart(product2);

    // update watch quantity to 2
    // homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);
    homePage.changeProductQuantityinCart(product1, 2);

    // update table lamp quantity to 0
    // homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);
    homePage.changeProductQuantityinCart(product2, 0);

    // update watch quantity again to 1
    // homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);
    homePage.changeProductQuantityinCart(product1, 1);
    homePage.clickCheckout();

    Checkout checkoutPage = new Checkout(driver);
    checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
    checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

    checkoutPage.placeOrder();

    try {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
    } catch (TimeoutException e) {
        System.out.println("Error while placing order in: " + e.getMessage());
        assertTrue(false);
    }

    status = driver.getCurrentUrl().endsWith("/thanks");
    assertTrue(status,"Failed to Verify that cart can be edited");
       
    // Visit the home page and log out the logged in user
    // Home home = new Home(driver);
    homePage.navigateToHome();
    status = homePage.PerformLogout();

}

//Verify that insufficient balance error is thrown when the wallet balance is not enough
@Test(priority = 7,groups = {"Sanity_test"} ,description="Verify that insufficient balance error is thrown when the wallet balance is not enough")
@Parameters({"product07","productQty"})
public void TestCase07(String product07, int productQty) throws InterruptedException {
    Boolean status;
    
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status,"User Perform Register Failed");
    
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status,"User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        // status = homePage.searchForProduct("Stylecon");
        // homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set ");
        status = homePage.searchForProduct(product07);
        homePage.addProductToCart(product07);

        // homePage.changeProductQuantityinCart("Stylecon 9 Seater RHS Sofa Set ", 10);
        homePage.changeProductQuantityinCart(product07, 10);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        assertTrue(status,"Failed to Verify that insufficient balance error is thrown when the wallet balance is not enough");
        
    // Visit the home page and log out the logged in user
    //Home home = new Home(driver);
    status = homePage.PerformLogout();

}

//Verify that a product added to a cart is available when a new tab is added
@Test(priority = 8, groups = {"Regression_Test"} ,description="Verify that a product added to a cart is available when a new tab is added")
public void TestCase08() throws InterruptedException {
    Boolean status;
    
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status,"User Perform Register Failed");
    
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status,"User Perform Login Failed");

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
        assertTrue(status,"Failed to Verify that product added to cart is available when a new tab is opened");
    
    // Visit the home page and log out the logged in user
    //Home home = new Home(driver);
    status = homePage.PerformLogout();

}


//Verify that privacy policy and about us links are working fine
@Test(priority = 9 , groups = {"Regression_Test"},description="Verify that privacy policy and about us links are working fine")
public void TestCase09() throws InterruptedException {
    Boolean status;
    
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status,"User Perform Register Failed");
    
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status,"User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
    
        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        SoftAssert sa = new SoftAssert();
        sa.assertTrue(status,"Verifying parent page url didn't change on privacy policy link click failed");

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        sa.assertTrue(status,"Verifying new tab opened has Privacy Policy page heading failed");

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        sa.assertTrue(status,"Verifying new tab opened has Terms Of Service page heading failed");

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        sa.assertAll("Failed to Verify that privacy policy and about us links are working fine");

    // Visit the home page and log out the logged in user
    //Home home = new Home(driver);
    status = homePage.PerformLogout();

}

//Verify that the contact us dialog works fine
@Test(priority = 10 ,groups = {"Regression_Test"},description="Verify that the contact us dialog works fine")
public void TestCase10() throws InterruptedException {
    Boolean status;
    
    Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        assertTrue(true,"Verify that contact us option is working correctly");
    // Visit the home page and log out the logged in user
    //Home home = new Home(driver);
    status = homePage.PerformLogout();

}

//Ensure that the Advertisement Links on the QKART page are clickable
@Test(priority = 11, groups = {"Sanity_test"} ,description="Ensure that the Advertisement Links on the QKART page are clickable")
public void TestCase11() throws InterruptedException {
    Boolean status;
    
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status,"User Perform Register Failed");
    
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status,"User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        SoftAssert sa = new SoftAssert();
        // logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" : "FAIL");
        sa.assertTrue(status,"Failed to Verify that 3 Advertisements are available");
        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        // logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" : "FAIL");

        sa.assertTrue(status,"Failed to Verify that Advertisement 1 is clickable");
        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        // logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" : "FAIL");
        sa.assertTrue(status,"Failed to Verify that Advertisement 2 is clickable");
        // logStatus("End TestCase",
        //         "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
        //         status ? "PASS" : "FAIL");
        
        sa.assertAll("Failed to Ensure that the links on the QKART advertisement are clickable");
    // Visit the home page and log out the logged in user
    //Home home = new Home(driver);
    status = homePage.PerformLogout();

}

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }

    public void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

