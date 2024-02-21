package QKART_TESTNG;

import QKART_TESTNG.QKART_Tests;
import java.io.File;
import java.net.MalformedURLException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass implements ITestListener {
    public RemoteWebDriver driver;

    public void onTestStart(ITestResult result) {
        System.out.println("New Test Started" +result.getName());   
        try {
            driver = QKART_Tests.getDriver();
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        QKART_Tests src = new QKART_Tests();
        src.takeScreenshot(driver, "onTestStart", result.getName());     
    }

    public void onTestFailure(ITestResult result) {
        System.out.println("onTestFailure Method" +result.getName());
        try {
            driver = QKART_Tests.getDriver();
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        QKART_Tests src = new QKART_Tests();
        src.takeScreenshot(driver, "onTestFailure", result.getName());
    }    

    public void onTestSuccess(ITestResult result){
        System.out.println("onTestSuccess Method" +result.getName());
       
        try {
            driver = QKART_Tests.getDriver();
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        QKART_Tests src = new QKART_Tests();
        src.takeScreenshot(driver, "onTestSuccess", result.getName());
       
    }

}

