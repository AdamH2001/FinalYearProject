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
class AfterSchoolClubApplicationTests3 {

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
	  @Order(200)	  
	  public void t200Useregistration() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.cssSelector(".nav-item:nth-child(4) .ms-1")).click();
	    driver.findElement(By.id("title")).click();
	    driver.findElement(By.id("title")).sendKeys("Mr");
	    driver.findElement(By.id("firstName")).sendKeys("Peter");
	    driver.findElement(By.id("surname")).sendKeys("Jones");
	    driver.findElement(By.id("email")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.id("password")).sendKeys("ManUtd01");
	    driver.findElement(By.id("conPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("telephoneNum")).sendKeys("01256812222");
	    driver.findElement(By.id("altContactName")).sendKeys("Sarah Jones");
	    driver.findElement(By.id("altTelephoneNum")).sendKeys("07766775611");
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Please verify your email address before attempting to login"));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email has not been verified"));

	    User peter = User.findByEmail("peterjones@hattonsplace.co.uk");
	    String url = String.format("http://afterschool-club.com/validateEmail?userId=%d&validationKey=%d", peter.getUserId(), 4521512);
	    driver.get(url);	    

	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email has not been verified"));
	    
	    
	    url = String.format("http://afterschool-club.com/validateEmail?userId=%d&validationKey=%d", peter.getUserId(), peter.getValidationKey());
	    driver.get(url);
	    
	    
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email is now verified"));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Account has not been verified by administrators"));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.linkText("1")).getText(), is("1"));
	    driver.findElement(By.linkText("1")).click();
	    assertThat(driver.findElement(By.linkText("Peter Jones")).getText(), is("Peter Jones"));
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Peter Jones approved and notified"));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
	  
	  @Test
	  @Order(201)	  
	  public void t201UseregistrationWithSameEmail() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.cssSelector(".nav-item:nth-child(4) .ms-1")).click();
	    driver.findElement(By.id("title")).click();
	    driver.findElement(By.id("title")).sendKeys("Mr");
	    driver.findElement(By.id("firstName")).sendKeys("Peter");
	    driver.findElement(By.id("surname")).sendKeys("Jones");
	    driver.findElement(By.id("email")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.id("password")).sendKeys("ManUtd01");
	    driver.findElement(By.id("conPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("telephoneNum")).sendKeys("01256812222");
	    driver.findElement(By.id("altContactName")).sendKeys("Sarah Jones");
	    driver.findElement(By.id("altTelephoneNum")).sendKeys("07766775611");
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email already in use"));
	    driver.findElement(By.linkText("Cancel")).click();	   
	    driver.close();
	  }	  

	  @Test
	  @Order(210)
	  public void t210RegisterChildRuth() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".nav-item:nth-child(1) .ms-1")).click();
	    driver.findElement(By.id("firstName")).click();
	    driver.findElement(By.id("firstName")).sendKeys("Ruth");
	    driver.findElement(By.id("surname")).sendKeys("Jones");
	    driver.findElement(By.id("className")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("className"));
	      dropdown.findElement(By.xpath("//option[. = 'Year 5']")).click();
	    }
	    driver.findElement(By.cssSelector("option:nth-child(6)")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    {
	      WebElement element = driver.findElement(By.id("dateOfBirth"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).sendKeys("2015-01-01");
	    driver.findElement(By.id("allergyNote")).click();
	    driver.findElement(By.id("allergyNote")).sendKeys("Hayfever");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.name("confirmation")).click();
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.id("consentToShare")).click();
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Child added you will be notified by email when validated by an administrator"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-1:nth-child(5) > .nav-link")).getText(), is("1"));
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-1:nth-child(5) > .nav-link")).click();
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Ruth Jones approved and parent notified"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.id("students"));
	      assert(elements.size() > 0);
	    }
	    {
	      WebElement element = driver.findElement(By.id("students"));
	      String value = element.getAttribute("value");
	      String locator = String.format("option[@value='%s']", value);
	      String selectedText = element.findElement(By.xpath(locator)).getText();
	      assertThat(selectedText, is("Ruth"));
	    }
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
	  
	  @Test
	  @Order(211)
	  public void t210RegisterChildJonny() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".nav-item:nth-child(1) .ms-1")).click();
	    driver.findElement(By.id("firstName")).click();
	    driver.findElement(By.id("firstName")).sendKeys("Jonny");
	    driver.findElement(By.id("surname")).sendKeys("Jones");
	    driver.findElement(By.id("className")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("className"));
	      dropdown.findElement(By.xpath("//option[. = 'Year 5']")).click();
	    }
	    driver.findElement(By.cssSelector("option:nth-child(6)")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    {
	      WebElement element = driver.findElement(By.id("dateOfBirth"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).sendKeys("2015-01-01");
	    driver.findElement(By.id("allergyNote")).click();
	    driver.findElement(By.id("allergyNote")).sendKeys("Hayfever");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.name("confirmation")).click();
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.id("consentToShare")).click();
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Child added you will be notified by email when validated by an administrator"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-1:nth-child(5) > .nav-link")).getText(), is("1"));
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-1:nth-child(5) > .nav-link")).click();
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Jonny Jones approved and parent notified"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();	
	    driver.close();
	  }	 
	  
	  @Test
	  @Order(211)
	  public void t210RegisterChildTommy() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".nav-item:nth-child(1) .ms-1")).click();
	    driver.findElement(By.id("firstName")).click();
	    driver.findElement(By.id("firstName")).sendKeys("Tommy");
	    driver.findElement(By.id("surname")).sendKeys("Jones");
	  
	  
	    driver.findElement(By.id("dateOfBirth")).click();
	    {
	      WebElement element = driver.findElement(By.id("dateOfBirth"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).click();
	    driver.findElement(By.id("dateOfBirth")).sendKeys("2020-02-01");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.name("confirmation")).click();
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.id("consentToShare")).click();
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Child added you will be notified by email when validated by an administrator"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    assertThat(driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-1:nth-child(5) > .nav-link")).getText(), is("1"));
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-1:nth-child(5) > .nav-link")).click();
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Tommy Jones approved and parent notified"));
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();	
	    driver.close();
	  }	  	  
}
