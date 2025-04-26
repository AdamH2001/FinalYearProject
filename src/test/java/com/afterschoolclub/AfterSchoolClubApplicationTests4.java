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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.afterschoolclub.controller.MainController;

import com.afterschoolclub.data.User;



@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AfterSchoolClubApplicationTests4 {

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
	  @Order(300)
	  public void t300RegisterForClub() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.id("students")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("students"));
	      dropdown.findElement(By.xpath("//option[. = 'Jonny']")).click();
	    }
	    driver.findElement(By.cssSelector("#students > option:nth-child(2)")).click();
	    driver.findElement(By.id("students")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("students"));
	      dropdown.findElement(By.xpath("//option[. = 'Tommy']")).click();
	    }
	    driver.findElement(By.cssSelector("#students > option:nth-child(3)")).click();
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    driver.findElement(By.id("students")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("students"));
	      dropdown.findElement(By.xpath("//option[. = 'Jonny']")).click();
	    }
	    driver.findElement(By.cssSelector("#students > option:nth-child(2)")).click();
	    driver.findElement(By.id("students")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("students"));
	      dropdown.findElement(By.xpath("//option[. = 'Ruth']")).click();
	    }
	    driver.findElement(By.cssSelector("#students > option:nth-child(1)")).click();
	    driver.findElement(By.cssSelector(".fa-left-long")).click();
	    driver.findElement(By.linkText("07:30 Breakfast Club")).click();
	    driver.findElement(By.linkText("Book Attendance")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	    
	    driver.findElement(By.cssSelector("#submitButton > span")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Not enough funds to attend this session, please top up your account."));
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	    	    
	    driver.findElement(By.linkText("Cancel")).click();
	    driver.findElement(By.cssSelector(".nav-item:nth-child(7) .ms-1")).click();
	    driver.findElement(By.id("100pounds")).click();	    
	    driver.findElement(By.name("submit")).click();
	    
	    sleep();
	    sleep();
	    
	    driver.findElement(By.id("email")).click();
	    sendKeys(driver.findElement(By.id("email")),"peterjones@hattonsplace.co.uk");
	    WebElement p = driver.findElement(By.id("password"));
	    WebElement b1 = driver.findElement(By.id("btnLogin"));
	    WebElement b2 = driver.findElement(By.id("btnNext"));
	    
	    sendKeys(driver.findElement(By.id("password")),"ManUtd01");

	    try {
	    	driver.findElement(By.id("btnLogin")).click();
	    }
	    catch (Exception e) {
	    	driver.findElement(By.id("btnNext")).click();
	    	sleep();
	    	sleep();
	    	
		    sendKeys(driver.findElement(By.id("password")),"ManUtd01");
		    driver.findElement(By.id("btnLogin")).click();
	    	
	    }
	    sleep();
	    sleep();
	    
	    driver.findElement(By.id("payment-submit-btn")).click();
	    
	    sleep();
	    sleep(10000);
	    
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Payment successful"));
	    

	    		
	    	    
	    driver.findElement(By.cssSelector(".nav-item:nth-child(6) .ms-1")).click();
	    assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(6) > b")).getText(), is("Closing: £100.00"));
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.cssSelector(".monthlydate:nth-child(2) .sessionTitle")).click();
	    driver.findElement(By.linkText("Book Attendance")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    driver.findElement(By.cssSelector("#submitButton > span")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Booked 1 session(s) at Breakfast Club for Ruth"));
	    driver.findElement(By.id("available")).click();
	    driver.findElement(By.linkText("07:30 Breakfast Club")).click();
	    driver.findElement(By.linkText("View Session")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	    
	    assertThat(driver.findElement(By.id("totalCostForAllSessions")).getText(), is("£4.50"));
	    assertThat(driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-3")).getText(), is("Total Cost for 1 Session(s):"));
	    driver.findElement(By.linkText("Back")).click();
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(1) > .col-sm-2")).getText(), is("£95.50"));
	    
	    driver.findElement(By.cssSelector(".d-none:nth-child(2)")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }

	  @Test
	  @Order(310)

	  public void t310EditSessionOptionsandCancel() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    
	    driver.findElement(By.cssSelector(".btn")).click();
	    sleep() ;
	    sleep() ;
	    
	    driver.findElement(By.cssSelector(".Attending .sessionTitle")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    driver.findElement(By.linkText("Edit Options")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 	    
	    
	    driver.findElement(By.id("student-1-Option8")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    driver.findElement(By.id("student-1-Option10")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    driver.findElement(By.id("student-1-Option1")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    driver.findElement(By.id("submitButton")).click();
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(1) > .col-sm-2")).getText(), is("£93.30"));
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Updated Options for Breakfast Club"));
	    driver.findElement(By.linkText("07:30 Breakfast Club")).click();
	    driver.findElement(By.linkText("Cancel Booking")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Cancelled booking for Ruth - account refunded."));
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(1) > .col-sm-2")).getText(), is("£100.00"));
	    driver.findElement(By.cssSelector(".d-none:nth-child(2)")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }	  
	  
	  @Test
	  @Order(320)	  
	  public void t320BookingRecurringSessionForMultipleStudents() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    driver.findElement(By.cssSelector(".fa-left-long")).click();
	    driver.findElement(By.cssSelector(".monthlydate:nth-child(3) #dropdownUser1")).click();
	    driver.findElement(By.linkText("Book Attendance")).click();
	    driver.findElement(By.id("bookingEndDate")).click();
	    driver.findElement(By.id("bookingEndDate")).sendKeys("2025-07-31");
	    driver.findElement(By.id("student-2-Attending")).click();
	    driver.findElement(By.id("student-3-Attending")).click();
	    driver.findElement(By.id("student-3-Attending")).click();
	    
	    
	    driver.findElement(By.cssSelector("#student-1 > span")).click();	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    driver.findElement(By.id("student-1-Option9")).click();
	    driver.findElement(By.cssSelector("#student-2 > span")).click();
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    
	    driver.findElement(By.id("student-2-Option6")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 
	    
	    assertThat(driver.findElement(By.id("totalCostForAllSessions")).getText(), is("£607.75"));
	    assertThat(driver.findElement(By.id("numberSessions")).getText(), is("55"));
	    driver.findElement(By.cssSelector("#submitButton > span")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Not enough funds to attend this session, please top up your account."));
	    driver.findElement(By.id("TueRecurring")).click();
	    driver.findElement(By.id("WedRecurring")).click();
	    driver.findElement(By.id("ThurRecurring")).click();
	    driver.findElement(By.id("FriRecurring")).click();
	    driver.findElement(By.id("student-1-OptionNone")).click();
	    driver.findElement(By.cssSelector("#student-2 > span")).click();
	    driver.findElement(By.id("student-2-OptionNone")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ; 

	    
	    driver.findElement(By.cssSelector("#submitButton > span")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Booked 11 session(s) at Breakfast Club for Ruth and Jonny"));
	    assertThat(driver.findElement(By.cssSelector(".mb-1:nth-child(1) > .col-sm-2")).getText(), is("£1.00"));	    
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
	  
	
}
