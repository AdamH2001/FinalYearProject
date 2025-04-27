package com.afterschoolclub;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestClassOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.afterschoolclub.controller.MainController;



@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AfterSchoolClubApplicationTests6 {

	static Logger logger = LoggerFactory.getLogger(MainController.class);
	static int timeOut = 5000;
	
	private WebDriver driver;
	private Map<String, Object> vars;
	JavascriptExecutor js;
	WebDriverWait wait;	

  public void WaitForTextToBeChanged(final WebElement element, String oldValue){
      try {
    	  
          // String oldValueOfCTextOfWebElement = element.getText();
          wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element,oldValue)));
      }
      catch (Exception e){
      }
  }
  
  public void WaitForTextToBeChanged(final WebElement element){
	  WaitForTextToBeChanged(element, "");      
  }  

	public String is(String s) {
		return s;
	}
	
	public void sleep() {
		sleep(1000);
		
	}	

	
	public void sleep(int duration) {
		try {
			Thread.sleep(duration);
		}
		catch (Exception e) {
			
		}
	}	
	public void assertThat(String found, String expected) {
		assertEquals(expected, found);
		
	}
	
	public void assertThat(boolean found, boolean expected) {
		assertEquals(expected, found);
		
	}	

	public void login() {	
	    sendKeys(driver.findElement(By.id("inputEmail")), "admin@afterschool-club.com");
	    sendKeys(driver.findElement(By.id("inputPassword")), "ManUtd01");
	    driver.findElement(By.cssSelector(".btn")).click();
	}
	
	public List<WebElement> findElements(By selector) {
		
		
	    wait.until(ExpectedConditions.presenceOfElementLocated(selector));

	    return driver.findElements(selector);
	}
	
	
	public WebElement findElement(By selector) {
		
		
	    wait.until(ExpectedConditions.presenceOfElementLocated(selector));

	    return driver.findElement(selector);
	}
	
	public void sendKeys(WebElement element, String string) {
		String jScript = String.format("arguments[0].value='%s';", string);
		js.executeScript(jScript, element);
	}
	

	
	@BeforeEach
	public void setUp() {
		driver = new FirefoxDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();

	    wait = new WebDriverWait(driver, java.time.Duration.ofMillis(timeOut));
		
	    // Step # | name | target | value

	    driver.get("http://afterschool-club.com/");
	    

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // 2 | setWindowSize | 1983x1231 | 
		driver.manage().window().maximize();
		
		js = (JavascriptExecutor) driver;
	}

	@AfterEach
	public void tearDown() {
		try {
			driver.quit();
		}
		catch (Exception e) {
			
		}
	}

	  @Test
	  @Order(500)
	  public void T500AddVoucherOverdraftandRefund() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("li:nth-child(9) .ms-1")).click();
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    driver.findElement(By.name("voucherReference")).click();
	    driver.findElement(By.name("voucherReference")).click();
	    driver.findElement(By.name("voucherReference")).click();
	    {
	      WebElement element = driver.findElement(By.name("voucherReference"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.name("voucherReference")).sendKeys("ASDE543");
	    driver.findElement(By.id("voucherAmountInput")).click();
	    driver.findElement(By.id("voucherAmountInput")).sendKeys("£250.00");
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Voucher successfully registered"));
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    driver.findElement(By.name("voucherReference")).click();
	    driver.findElement(By.name("voucherReference")).sendKeys("ASDE543");
	    driver.findElement(By.id("voucherAmountInput")).click();
	    driver.findElement(By.id("voucherAmountInput")).sendKeys("£250.00");
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Voucher has already been used"));
	    driver.findElement(By.cssSelector(".fa-magnifying-glass-dollar")).click();
	    driver.findElement(By.id("overdraftLimitInput")).click();
	    driver.findElement(By.id("overdraftLimitInput")).sendKeys("£100.00");
	    driver.findElement(By.cssSelector(".row:nth-child(3) .btn-primary")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Overdraft successfully updated"));
	    assertThat(driver.findElement(By.cssSelector(".filterrow:nth-child(1) > td:nth-child(2) td:nth-child(1)")).getText(), is("£100.00"));
	    assertThat(driver.findElement(By.cssSelector(".filterrow:nth-child(1) > td:nth-child(3) td:nth-child(1)")).getText(), is("£250.00"));
	    driver.findElement(By.cssSelector(".fa-brands")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("User successfully refunded"));
	    assertThat(driver.findElement(By.cssSelector(".filterrow:nth-child(1) > td:nth-child(4) td:nth-child(1)")).getText(), is("£0.00"));
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }	

	  @Test
	  @Order(510)
	  public void T510UtiliseVoucherAndOverdraft() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(1) > .col-sm-2")).getText(), is("£0.00"));
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(2) > .col-sm-2")).getText(), is("£250.00"));
	    driver.findElement(By.cssSelector("tbody:nth-child(1) td:nth-child(3)")).click();
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    driver.findElement(By.cssSelector(".days:nth-child(2) > .monthlydate:nth-child(2) > .Available:nth-child(3) .sessionTitle")).click();
	    driver.findElement(By.linkText("Book Attendance")).click();
	    driver.findElement(By.id("submitButton")).click();
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(1) > .col-sm-2")).getText(), is("-£2.50"));
	    driver.findElement(By.cssSelector(".days:nth-child(2) > .monthlydate:nth-child(3) .sessionTitle")).click();
	    driver.findElement(By.linkText("Book Attendance")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	    
	    driver.findElement(By.cssSelector("#submitButton > span")).click();
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(2) > .col-sm-2")).getText(), is("£247.50"));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }	  
	  
	  @Test
	  @Order(520)
	  public void T520MiscAdmin() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("li:nth-child(8) .ms-1")).click();
	    assertThat(driver.findElement(By.cssSelector("tr:nth-child(7) > td:nth-child(2)")).getText(), is("£97.80"));
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    driver.findElement(By.cssSelector(".fa-left-long")).click();
	    driver.findElement(By.cssSelector("li:nth-child(9) .ms-1")).click();
	    driver.findElement(By.cssSelector(".btn-success > .fa-solid")).click();
	    driver.findElement(By.cssSelector("tr:nth-child(12) > td:nth-child(7) > b")).click();
	    driver.findElement(By.cssSelector("tr:nth-child(12) > td:nth-child(7) > b")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector("tr:nth-child(12) > td:nth-child(7) > b"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    assertThat(driver.findElement(By.cssSelector("tr:nth-child(12) > td:nth-child(7) > b")).getText(), is("Closing: £247.50"));
	    assertThat(driver.findElement(By.cssSelector("tr:nth-child(12) > td:nth-child(6) > b")).getText(), is("Closing: -£2.50"));
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.id("fullybooked")).click();
	    driver.findElement(By.id("nobookings")).click();
	    driver.findElement(By.id("insufficientResources")).click();
	    driver.findElement(By.id("attending")).click();
	    driver.findElement(By.id("all")).click();
	    driver.findElement(By.id("attending")).click();
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Update Details")).click();
	    driver.findElement(By.id("telephoneNum")).click();
	    driver.findElement(By.id("telephoneNum")).sendKeys("01256812733");
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Profile has been updated"));
	    driver.findElement(By.cssSelector("li:nth-child(2) .ms-1")).click();
	    driver.findElement(By.cssSelector("li:nth-child(10) .ms-1")).click();
	    driver.findElement(By.linkText("Policies and Procedures")).click();
	    driver.findElement(By.id("policy-0")).click();
	    
	    vars.put("window_handles", driver.getWindowHandles());
	    driver.findElement(By.cssSelector("#ap-0 > span")).click();
	    sleep(4000);
	    //vars.put("win756", waitForWindow(2000));
	    vars.put("root", driver.getWindowHandle());
	    //driver.switchTo().window(vars.get("win756").toString());
	    driver.switchTo().window(vars.get("root").toString());
	    driver.findElement(By.cssSelector("div > a > img")).click();
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Change Password")).click();
	    driver.findElement(By.id("password")).sendKeys("ManUtd01");
	    driver.findElement(By.id("conPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("password")).click();
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Password has been changed"));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    driver.findElement(By.cssSelector("li:nth-child(9) .ms-1")).click();
	    driver.findElement(By.cssSelector(".fa-envelope-circle-check")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email(s) sent"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();

	  }	  
}
