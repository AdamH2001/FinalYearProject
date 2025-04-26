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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.afterschoolclub.controller.MainController;
import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.User;



@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AfterSchoolClubApplicationTests6 {

	@Autowired
	private MainController controller;

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
	  public void t500AddVoucherOverdraftandRefund() {
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

}
