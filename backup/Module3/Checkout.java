package QKART_SANITY_LOGIN.Module1;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
            /*
             * Click on the "Add new address" button, enter the addressString in the address
             * text box and click on the "ADD" button to save the address
             */
            
            WebElement address=driver.findElement(By.xpath("//button[text()='Add new address']"));
            address.click();
           WebElement textfiled= driver.findElement(By.xpath("//textarea[@placeholder='Enter your complete address']"));
           textfiled.sendKeys(addresString);
           driver.findElement(By.xpath("//button[text()='Add']")).click();
            return false;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            /*
             * Iterate through all the address boxes to find the address box with matching
             * text, addressToSelect and click on it
             */
        //    List <WebElement> toSelectAddress=driver.findElements(By.xpath("//div[@class='address-item selected MuiBox-root css-0']/div[1]/p"));
        List <WebElement> toSelectAddress=driver.findElements(By.xpath("/html/body/div/div/div[2]/div[1]/div/div[1]/div[1]/div[1]/p"));

           for(int i=0;i<toSelectAddress.size();i++){
           WebElement add=toSelectAddress.get(i);
String addText=add.getText();
if(addressToSelect.equals(addText)){
    add.click();
    return true;
}

           }

            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find the "PLACE ORDER" button and click on it

            driver.findElement(By.xpath("//button[text()='PLACE ORDER']")).click();
            return false;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 08: MILESTONE 7

        //   WebElement msg=  driver.findElement(By.id("notistack-snackbar"));
        //  String massege= msg.getText();
        //   if(massege.equals("You do not have enough balance in your wallet for this purchase")){
        //     return true;

        //   }
        WebElement msg = driver.findElement(By.id("notistack-snackbar"));
        if (msg.isDisplayed()) {
            if (msg.getText().equals("You do not have enough balance in your wallet for this purchase")) {
                return true;
            }
        }

            return false;
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}
