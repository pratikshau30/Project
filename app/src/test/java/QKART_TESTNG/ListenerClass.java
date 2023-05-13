package QKART_TESTNG;

import java.sql.Driver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.remote.RemoteWebDriver;


public class ListenerClass implements ITestListener{
    
    
    public void onTestStart(ITestResult result) {
             System.out.println("New Test Started" +result.getName());
             QKART_Tests.takeScreenshot("TC_START", result.getName());

         }
    
        
         public void onTestSuccess(ITestResult result) {
              System.out.println("onSuccess method started");
              QKART_Tests.takeScreenshot("TC_SUCCESS", result.getName());
         }

         public void onTestFailure(ITestResult result) {
           System.out.println("Test Failed : "+ result.getName()+" Taking Screenshot ! ");
           QKART_Tests.takeScreenshot("TC_FAILURE", result.getName());
            }
}