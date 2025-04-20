package com.afterschoolclub;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.afterschoolclub.controller.MainController;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)

class AfterSchoolClubApplicationTests {

	@Autowired
	private MainController controller;

	static Logger logger = LoggerFactory.getLogger(MainController.class);
	static int timeOut = 5000;
	
	private WebDriver driver;
	private Map<String, Object> vars;
	JavascriptExecutor js;
	WebDriverWait wait;	

	@Test
	@Order(1)
	void contextLoads() {

		int a = 1;
		int b = 2;
		int c = 3;

		assertThat(a == 1).isTrue();

		assertThat(b == 2).isTrue();

		assertThat(c == 3).isTrue();

	}

	public String is(String s) {
		return s;
	}

	
	public List<WebElement> findElements(By selector) {
		
		
	    wait.until(ExpectedConditions.presenceOfElementLocated(selector));

	    return driver.findElements(selector);
	}
	
	
	public WebElement findElement(By selector) {
		
		
	    wait.until(ExpectedConditions.presenceOfElementLocated(selector));

	    return driver.findElement(selector);
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
	    driver.manage().window().setSize(new Dimension(1983, 1231));		
	}

	@AfterEach
	public void tearDown() {
		driver.quit();
	}

	@Test
	@Order(2)
	public void testHomePage() {
	    // Test name: Test-HomePage
 

	    // 3 | assertElementPresent | css=a > img | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector("a > img"));
	      assert(elements.size() > 0);
	    }
	    // 4 | click | id=inputEmail | 
	    findElement(By.id("inputEmail")).click();
	    // 5 | assertElementPresent | id=inputEmail3 | 
	    {
	      List<WebElement> elements = findElements(By.id("inputEmail"));
	      assert(elements.size() > 0);
	    }
	    // 6 | click | id=inputPassword | 
	    findElement(By.id("inputPassword")).click();
	    // 7 | assertElementPresent | id=inputPassword3 | 
	    {
	      List<WebElement> elements = findElements(By.id("inputPassword"));
	      assert(elements.size() > 0);
	    }
	    // 8 | assertElementPresent | linkText=Forgot password | 
	    {
	      List<WebElement> elements = findElements(By.linkText("Forgot password"));
	      assert(elements.size() > 0);
	    }
	    // 9 | assertElementPresent | css=.btn | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".btn"));
	      assert(elements.size() > 0);
	    }
	    // 10 | assertElementPresent | css=.nav-item:nth-child(1) .ms-1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".nav-item:nth-child(1) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    // 11 | assertElementPresent | css=li:nth-child(2) .ms-1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector("li:nth-child(2) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    // 12 | assertElementPresent | css=.nav-item:nth-child(3) .ms-1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".nav-item:nth-child(3) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    // 13 | assertElementPresent | css=.nav-item:nth-child(4) .ms-1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".nav-item:nth-child(4) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    // 14 | click | css=.asc-home-box-white > h1 | 
	    findElement(By.cssSelector(".asc-home-box-white > h1")).click();
	    // 15 | click | css=body | 
	    findElement(By.cssSelector("body")).click();
	    // 16 | assertElementPresent | css=.asc-home-box-white > h1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".asc-home-box-white > h1"));
	      assert(elements.size() > 0);
	    }
	    // 17 | click | css=.asc-home-box-lightblue | 
	    findElement(By.cssSelector(".asc-home-box-lightblue")).click();
	    // 18 | assertElementPresent | css=.asc-home-box-lightblue > h1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".asc-home-box-lightblue > h1"));
	      assert(elements.size() > 0);
	    }
	    // 19 | click | css=.asc-home-box-blue > h1 | 
	    findElement(By.cssSelector(".asc-home-box-blue > h1")).click();
	    // 20 | assertElementPresent | css=.asc-home-box-blue > h1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".asc-home-box-blue > h1"));
	      assert(elements.size() > 0);
	    }
	    // 21 | click | css=#main tr:nth-child(2) | 
	    // findElement(By.cssSelector("#main tr:nth-child(2)")).click();
	    // 22 | click | css=.asc-home-box-orange > h1 | 
	    findElement(By.cssSelector(".asc-home-box-orange > h1")).click();
	    // 23 | click | css=.asc-home-box-orange > h1 | 
	    findElement(By.cssSelector(".asc-home-box-orange > h1")).click();
	    // 24 | assertElementPresent | css=.asc-home-box-orange > h1 | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector(".asc-home-box-orange > h1"));
	      assert(elements.size() > 0);
	    }
	    // 25 | assertElementPresent | linkText=Privacy Statement | 
	    {
	      List<WebElement> elements = findElements(By.linkText("Privacy Statement"));
	      assert(elements.size() > 0);
	    }
	    // 26 | assertElementPresent | linkText=Terms and Conditions | 
	    {
	      List<WebElement> elements = findElements(By.linkText("Terms and Conditions"));
	      assert(elements.size() > 0);
	    }
	    // 27 | assertElementPresent | css=tr:nth-child(1) .nav-link > span | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector("tr:nth-child(1) .nav-link > span"));
	      assert(elements.size() > 0);
	    }
	    // 28 | assertElementPresent | css=tr:nth-child(2) span | 
	    {
	      List<WebElement> elements = findElements(By.cssSelector("tr:nth-child(2) span"));
	      assert(elements.size() > 0);
	    }
	    // 29 | assertElementPresent | linkText=Policies and Procedures | 
	    {
	      List<WebElement> elements = findElements(By.linkText("Policies and Procedures"));
	      assert(elements.size() > 0);
	    }
	    // 30 | click | linkText=Privacy Statement | 
	    findElement(By.linkText("Privacy Statement")).click();
	    // 31 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 32 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 33 | doubleClick | css=.asc-header | 
	    {
	      WebElement element = findElement(By.cssSelector(".asc-header"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    // 34 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 35 | assertText | css=.asc-header | Privacy Statement
	    assertEquals(findElement(By.cssSelector(".asc-header")).getText(), is("Privacy Statement"));
	    // 36 | click | linkText=Terms and Conditions | 
	    findElement(By.linkText("Terms and Conditions")).click();
	    // 37 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 38 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 39 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 40 | doubleClick | css=.asc-header | 
	    {
	      WebElement element = findElement(By.cssSelector(".asc-header"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    // 41 | click | css=.asc-header | 
	    findElement(By.cssSelector(".asc-header")).click();
	    // 42 | assertText | css=.asc-header | Terms and Conditions
	    assertEquals(findElement(By.cssSelector(".asc-header")).getText(), is("Terms and Conditions"));
	  }


}
