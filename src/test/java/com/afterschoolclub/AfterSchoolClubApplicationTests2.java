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


@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AfterSchoolClubApplicationTests2 {

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
		try {
			Thread.sleep(1000);
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
	 @Order(100)
	  public void t100ScheduleSession() {
		 login();
	    driver.findElement(By.cssSelector("li:nth-child(3) .ms-1")).click();
	    driver.findElement(By.id("club")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("club"));
	      dropdown.findElement(By.xpath("//option[. = 'Choir Club']")).click();
	    }
	    driver.findElement(By.cssSelector("#club > option:nth-child(3)")).click();
	    driver.findElement(By.id("maxAttendees")).click();
	    sendKeys(driver.findElement(By.id("maxAttendees")), "20");
	    {
	      WebElement dropdown = driver.findElement(By.id("staff"));
	      dropdown.findElement(By.xpath("//option[. = 'Mr A Hatton']")).click();
	    }
	    driver.findElement(By.id("startDate")).click();
	    sendKeys(driver.findElement(By.id("startDate")), "2025-04-29");
	    driver.findElement(By.id("recurringEndDate")).click();
	    sendKeys(driver.findElement(By.id("recurringEndDate")), "2025-04-29");	    
	    driver.findElement(By.id("parentNotes")).click();
	    sendKeys(driver.findElement(By.id("parentNotes")), "Some notes for the parent");
	    sendKeys(driver.findElement(By.id("organiserNotes")), "Some notes for the organiser");
	    {
	      WebElement dropdown = driver.findElement(By.name("menu"));
	      dropdown.findElement(By.xpath("//option[. = 'Drinks Selection']")).click();
	    }
	    driver.findElement(By.id("equipment1")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment1"));
	    //  dropdown.findElement(By.xpath("#equipment1 > option[. = 'Blue bibs']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment1 > option:nth-child(2)")).click();
	    driver.findElement(By.id("equipment1")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment1"));
	      //dropdown.findElement(By.xpath("//option[. = 'None']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment1 > option:nth-child(1)")).click();
	    driver.findElement(By.id("club")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("club"));
	      //dropdown.findElement(By.xpath("#equipment1 > option[. = 'Football Club - Y5']")).click();
	    }
	    driver.findElement(By.cssSelector("#club > option:nth-child(5)")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("staff"));
	      dropdown.findElement(By.xpath("//option[. = 'Mr A Hatton']")).click();
	    }
	    {
	      WebElement dropdown = driver.findElement(By.id("staff"));
	      dropdown.findElement(By.xpath("//option[. = 'Mr V Gunn']")).click();
	    }
	    driver.findElement(By.id("equipment1")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment1"));
	      //dropdown.findElement(By.xpath("#equipment1 > option[. = 'Football Goals Large']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment1 > option:nth-child(3)")).click();
	    sendKeys(driver.findElement(By.id("equipmentQuantity1")), "1");
	    driver.findElement(By.id("equipmentQuantity1")).click();
	    sendKeys(driver.findElement(By.id("equipmentQuantity1")), "2");
	    driver.findElement(By.id("equipmentQuantity1")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity1"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector("#add1 > .fa")).click();
	    driver.findElement(By.id("equipment2")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment2"));
	      //dropdown.findElement(By.xpath("##equipment2 > option[. = 'Football Size 4']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment2 > option:nth-child(7)")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity2"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity2"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity2"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.id("equipmentQuantity2")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity2"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity2"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity2"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.id("equipmentQuantity2")).click();
	    sendKeys(driver.findElement(By.id("equipmentQuantity2")), "1");
	    driver.findElement(By.id("perAttendee2")).click();
	    driver.findElement(By.id("add2")).click();
	    driver.findElement(By.id("equipment3")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment3"));
	      //dropdown.findElement(By.xpath("#equipment5 > option[. = 'Red bibs']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment3 > option:nth-child(10)")).click();
	    driver.findElement(By.id("equipmentQuantity3")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity3"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity3"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity3"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.id("equipmentQuantity3")).click();
	    sendKeys(driver.findElement(By.id("equipmentQuantity3")), "10");
	    driver.findElement(By.cssSelector("#add3 > .fa")).click();
	    driver.findElement(By.id("equipment4")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment4"));
	      //dropdown.findElement(By.xpath("#equipment4 > option[. = 'Green bibs']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment4 > option:nth-child(9)")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity4"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity4"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity4"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

	    driver.findElement(By.id("equipmentQuantity4")).click();
	    sendKeys(driver.findElement(By.id("equipmentQuantity4")), "10");
	    driver.findElement(By.cssSelector("#add4 > .fa")).click();
	    driver.findElement(By.id("equipment5")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("equipment5"));
	      //dropdown.findElement(By.xpath("#equipment5 > option[. = 'Football Size 5']")).click();
	    }
	    driver.findElement(By.cssSelector("#equipment5 > option:nth-child(8)")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity5"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity5"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity5"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.id("equipmentQuantity5")).click();
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity5"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity5"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.id("equipmentQuantity5"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.id("equipmentQuantity5")).click();
	    sendKeys(driver.findElement(By.id("equipmentQuantity5")), "3");
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Need at least one member of staff per 10 students."));
	   
	    {
	      WebElement dropdown = driver.findElement(By.id("staff"));
	      dropdown.findElement(By.xpath("//option[. = 'Mr P Stockwell']")).click();
	    }
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	   	    
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(1) > .col-sm-12")).getText(), is("Requested 20 football size 4 on 29/04/2025 but only 10 available - please choose an alternative"));
	    
        
	    String selId = ""; 	    
	    if (driver.findElement(By.id("equipment1")).getAttribute("value").equals("14")) {
	    	selId ="equipment1";
	    }
	    if (driver.findElement(By.id("equipment2")).getAttribute("value").equals("14")) {
	    	selId ="equipment2";
	    }
	    if (driver.findElement(By.id("equipment3")).getAttribute("value").equals("14")) {
	    	selId ="equipment3";
	    }
	    if (driver.findElement(By.id("equipment4")).getAttribute("value").equals("14")) {
	    	selId ="equipment4";
	    }
	    if (driver.findElement(By.id("equipment5")).getAttribute("value").equals("14")) {
	    	selId ="equipment5";
	    }
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();
	    
	    driver.findElement(By.id(selId)).click();
	    {
	    	WebElement dropdown = driver.findElement(By.id(selId));
	    	
	 //     dropdown.findElement(By.xpath("#equipment2 > option[. = 'Football Size 3']")).click();
	    }
	    driver.findElement(By.cssSelector("#".concat(selId).concat(" > option:nth-child(6)"))).click();
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();	    
	    driver.findElement(By.name("submit")).click();
	    
	    assertThat(driver.findElement(By.cssSelector(".sessionTitle")).getText(), is("15:30 Football Club - Y5"));
	    driver.findElement(By.cssSelector(".sessionTitle")).click();
	    driver.findElement(By.linkText("View Session")).click();
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();	    
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.cssSelector(".sessionTitle")).click();
	    driver.findElement(By.linkText("Edit Session")).click();
	    driver.findElement(By.id("location")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("location"));
	      dropdown.findElement(By.xpath("//option[. = 'Football Pitch Large']")).click();
	    }
	    driver.findElement(By.cssSelector("#location > option:nth-child(4)")).click();
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();	    
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.cssSelector(".sessionTitle")).click();
	    driver.findElement(By.linkText("View Session")).click();
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();	    
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.cssSelector(".d-none:nth-child(2)")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }

	 
	 @Test
	 @Order(115)	 
	  public void t115conflictingResources() {
	    login();
	    driver.findElement(By.linkText("15:30 Football Club - Y5")).click();
	    driver.findElement(By.linkText("Copy Session")).click();
	    driver.findElement(By.id("startDate")).click();
	    driver.findElement(By.id("startDate")).sendKeys("2025-04-29");
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();		    
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(1) > .col-sm-12")).getText(), is("Location football pitch large is already booked on 29/04/2025 - please choose an alternative"));
	    driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(2) > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(2) > .col-sm-12")).getText(), is("Staff member mr p stockwell is already booked on 29/04/2025 - please choose an alternative"));
	    driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(3) > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(3) > .col-sm-12")).getText(), is("Staff member mr v gunn is already booked on 29/04/2025 - please choose an alternative"));
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(4) > .col-sm-12")).getText(), is("Requested 2 football goals large on 29/04/2025 but none available - please choose an alternative"));
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(5) > .col-sm-12")).getText(), is("Requested 20 football size 3 on 29/04/2025 but none available - please choose an alternative"));
	    driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(6) > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(6) > .col-sm-12")).getText(), is("Requested 3 football size 5 on 29/04/2025 but only 1 available - please choose an alternative"));
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();		    
	    driver.findElement(By.linkText("Cancel")).click();
	    driver.findElement(By.cssSelector(".d-none:nth-child(2)")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
	 
	  @Test
	  @Order(120)
	  public void t120RecurringSession() {
	    login();
	    driver.findElement(By.cssSelector(".sessionTitle")).click();
	    driver.findElement(By.linkText("Copy Session")).click();
	    driver.findElement(By.id("recurringEndDate")).click();
	    driver.findElement(By.id("recurringEndDate")).sendKeys("2025-07-31");
	    driver.findElement(By.id("TueRecurring")).click();
	    driver.findElement(By.id("ThurRecurring")).click();
	    driver.findElement(By.id("NonHolidaysOnly")).click();
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();		    
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector(".afterschoolclubflash:nth-child(1) > .col-sm-12")).getText(), is("Location football pitch large is already booked on 29/04/2025 - please choose an alternative"));
	    driver.findElement(By.id("startDate")).click();
	    driver.findElement(By.id("startDate")).sendKeys("2025-04-30");
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep();		    
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Added 22 session(s)"));
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    assertThat(driver.findElement(By.cssSelector(".days:nth-child(1) .sessionTitle")).getText(), is("15:30 Football Club - Y5"));
	    assertThat(driver.findElement(By.cssSelector(".days:nth-child(4) > .monthlydate:nth-child(4) .sessionTitle")).getText(), is("15:30 Football Club - Y5"));
	    assertThat(driver.findElement(By.cssSelector(".days:nth-child(5) > .monthlydate:nth-child(4)")).getText(), is("29"));
	    assertThat(driver.findElement(By.cssSelector(".days:nth-child(5) > .monthlydate:nth-child(2)")).getText(), is("27"));
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    driver.findElement(By.cssSelector(".fa-right-long")).click();
	    assertThat(driver.findElement(By.cssSelector(".days:nth-child(4) .sessionTitle")).getText(), is("15:30 Football Club - Y5"));
	    driver.findElement(By.cssSelector(".days:nth-child(4) > .monthlydate:nth-child(4)")).click();
	    assertThat(driver.findElement(By.cssSelector(".days:nth-child(4) > .monthlydate:nth-child(4)")).getText(), is("24"));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }


	  public void t121DeleteRecurringSession() {
		    login();
		    driver.findElement(By.cssSelector(".fa-right-long")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(1) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(2) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(3) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(3) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".fa-right-long")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(2) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(3) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(3) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(4) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(4) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".fa-right-long")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(1) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(2) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(3) > .monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(1) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".days:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Session cancelled"));
		    driver.findElement(By.cssSelector(".mx-1")).click();
		    driver.findElement(By.linkText("Sign out")).click();
		    driver.close();
		  }

	  
		 @Test
		 @Order(130)	 
		  public void t130DeleteSession() {
		    login();
		    driver.findElement(By.cssSelector(".sessionTitle")).click();
		    driver.findElement(By.linkText("Copy Session")).click();
		    driver.findElement(By.id("startDate")).click();
		    sendKeys(driver.findElement(By.id("startDate")), "2025-04-30");
		    driver.findElement(By.id("recurringEndDate")).click();
		    sendKeys(driver.findElement(By.id("recurringEndDate")), "2025-04-30");
		    
		    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		    sleep();	    
		    driver.findElement(By.name("submit")).click();
		    driver.findElement(By.cssSelector(".monthlydate:nth-child(2) .sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    driver.findElement(By.cssSelector(".sessionTitle")).click();
		    driver.findElement(By.linkText("View Session")).click();
		    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		    sleep();	    
		    driver.findElement(By.linkText("Back")).click();
		    driver.findElement(By.id("dropdownUser1")).click();
		    driver.findElement(By.cssSelector(".sessionTitle")).click();
		    driver.findElement(By.linkText("Cancel Session")).click();
		    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Session cancelled"));
		    driver.findElement(By.cssSelector(".mx-1")).click();
		    driver.findElement(By.linkText("Sign out")).click();
		    driver.close();
		    
		  }
	  
		  @Test
		  @Order(135)
		  public void tT135ScheduleBreakfastClub() {
		    driver.get("http://afterschool-club.com/");
		    driver.manage().window().setSize(new Dimension(2575, 1407));
		    driver.findElement(By.id("inputEmail")).click();
		    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
		    driver.findElement(By.id("inputEmail")).click();
		    driver.findElement(By.id("inputEmail")).click();
		    {
		      WebElement element = driver.findElement(By.id("inputEmail"));
		      Actions builder = new Actions(driver);
		      builder.doubleClick(element).perform();
		    }
		    driver.findElement(By.id("inputEmail")).click();
		    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
		    driver.findElement(By.cssSelector(".btn")).click();
		    driver.findElement(By.cssSelector("li:nth-child(3) .ms-1")).click();
		    driver.findElement(By.id("club")).click();
		    {
		      WebElement dropdown = driver.findElement(By.id("club"));
		      dropdown.findElement(By.xpath("//option[. = 'Breakfast Club']")).click();
		    }
		    driver.findElement(By.cssSelector("#club > option:nth-child(2)")).click();
		    driver.findElement(By.id("maxAttendees")).click();
		    driver.findElement(By.id("maxAttendees")).click();
		    {
		      WebElement element = driver.findElement(By.id("maxAttendees"));
		      Actions builder = new Actions(driver);
		      builder.doubleClick(element).perform();
		    }
		    driver.findElement(By.id("maxAttendees")).sendKeys("30");
		    {
		      WebElement dropdown = driver.findElement(By.id("staff"));
		      dropdown.findElement(By.xpath("//option[. = 'Mr A Hatton']")).click();
		    }
		    {
		      WebElement dropdown = driver.findElement(By.id("staff"));
		      dropdown.findElement(By.xpath("//option[. = 'Mr P Stockwell']")).click();
		    }
		    {
		      WebElement dropdown = driver.findElement(By.id("staff"));
		      dropdown.findElement(By.xpath("//option[. = 'Mr V Gunn']")).click();
		    }
		    driver.findElement(By.id("startTime")).click();
		    driver.findElement(By.id("startTime")).sendKeys("07:30");
		    driver.findElement(By.id("endTime")).click();
		    driver.findElement(By.id("endTime")).sendKeys("09:00");
		    driver.findElement(By.id("startDate")).click();
		    driver.findElement(By.id("startDate")).sendKeys("2025-04-29");
		    driver.findElement(By.id("recurringEndDate")).click();
		    driver.findElement(By.id("recurringEndDate")).sendKeys("2025-07-31");
		    driver.findElement(By.id("MonRecurring")).click();
		    driver.findElement(By.id("TueRecurring")).click();
		    driver.findElement(By.id("WedRecurring")).click();
		    driver.findElement(By.id("ThurRecurring")).click();
		    driver.findElement(By.id("FriRecurring")).click();
		    driver.findElement(By.id("NonHolidaysOnly")).click();
		    {
		      WebElement dropdown = driver.findElement(By.name("menu"));
		      dropdown.findElement(By.xpath("//option[. = 'Cereal Selection']")).click();
		    }
		    {
		      WebElement dropdown = driver.findElement(By.name("menu"));
		      dropdown.findElement(By.xpath("//option[. = 'Drinks Selection']")).click();
		    }
		    {
		      WebElement dropdown = driver.findElement(By.name("menu"));
		      dropdown.findElement(By.xpath("//option[. = 'Fruit Selection']")).click();
		    }
		    driver.findElement(By.name("submit")).click();
		    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
		    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Added 56 session(s)"));
		    driver.findElement(By.id("dropdownUser1")).click();
		    driver.findElement(By.linkText("Sign out")).click();
		    driver.close();
		  }

		  
}
