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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.afterschoolclub.controller.MainController;
import com.afterschoolclub.data.Session;



@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AfterSchoolClubApplicationTests5 {

	static Logger logger = LoggerFactory.getLogger(MainController.class);
	static int timeOut = 5000;
	
	private WebDriver driver;
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
	  @Order(400)	  
	  
	  public void T400PrepareSessionForRegister  () {
		    driver.get("http://afterschool-club.com/");
		    driver.manage().window().setSize(new Dimension(2575, 1407));
		    driver.findElement(By.id("inputEmail")).click();
		    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
		    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
		    driver.findElement(By.cssSelector(".btn")).click();
		    driver.findElement(By.linkText("07:30 Breakfast Club")).click();
		    driver.findElement(By.linkText("Book Attendance")).click();
		    
		    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		    sleep() ;		    
		    
		    driver.findElement(By.id("student-2-Attending")).click();
		    driver.findElement(By.id("student-3-Attending")).click();
		    driver.findElement(By.cssSelector("#student-1 > span")).click();
		    driver.findElement(By.id("student-1-mg-3-mo-8")).click();
		    driver.findElement(By.cssSelector("#student-1-Container > div:nth-child(3) > div:nth-child(2) .col-sm-2:nth-child(5)")).click();
		    driver.findElement(By.id("student-1-mg-2-mo-12")).click();
		    driver.findElement(By.id("student-1-mg-1-mo-3")).click();
		    driver.findElement(By.cssSelector("#student-2 > span")).click();
		    driver.findElement(By.id("student-2-mg-3-mo-6")).click();
		    driver.findElement(By.cssSelector("#student-2-Container > div:nth-child(3) > div:nth-child(3) .col-sm-2:nth-child(5)")).click();
		    driver.findElement(By.id("student-2-mg-2-mo-10")).click();
		    driver.findElement(By.id("student-2-mg-1-mo-2")).click();
		    driver.findElement(By.cssSelector("#student-3 > span")).click();
		    driver.findElement(By.id("student-3-mg-3-mo-9")).click();
		    driver.findElement(By.id("student-3-mg-2-mo-14")).click();
		    driver.findElement(By.id("student-3-mg-1-mo-1")).click();
		    driver.findElement(By.cssSelector("#submitButton > span")).click();
		    driver.findElement(By.id("dropdownUser1")).click();
		    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
		    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Booked 1 session(s) at Breakfast Club for Ruth, Jonny and Tommy"));

		    driver.findElement(By.id("dropdownUser1")).click();
		    driver.findElement(By.linkText("Sign out")).click();		  		  
			  
			//  session.setStartDateTime(orig);
		  
	  }
	  
	  @Test
	  @Order(401)	
	  public void T401ChangeStartDate  () {
		  Session session = Session.findById(25);
		  LocalDateTime orig = session.getStartDateTime();			  		  
		  session.setStartDateTime(LocalDateTime.of(LocalDate.of(2025, 4, 26), orig.toLocalTime()));
		  session.setEndDateTime(LocalDateTime.of(LocalDate.of(2025, 4, 28), session.getEndDateTime().toLocalTime()));
		  
		  session.save();
	  }
	  
	  @Test
	  @Order(402)	
	  
	  public void T402TakeandViewRegister() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".monthlydate:nth-child(6) .sessionTitle")).click();
	    driver.findElement(By.linkText("Take Register")).click();
	    driver.findElement(By.id("PresentButton_25")).click();
	    driver.findElement(By.id("PresentButton_24")).click();
	    driver.findElement(By.id("AbsentButton_26")).click();
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Recorded register"));
	    driver.findElement(By.cssSelector(".monthlydate:nth-child(6) .sessionTitle")).click();
	    driver.findElement(By.linkText("View Session")).click();
	   // assertThat(driver.findElement(By.cssSelector(".row:nth-child(3) > .col-sm-2 > .col-sm-2")).isSelected(), true);
	   // assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) > .col-sm-2 > .col-sm-2")).isSelected(), true);
	   // assertThat(driver.findElement(By.cssSelector(".row:nth-child(5) > .col-sm-2 > .col-sm-2")).isSelected(), false);
	    driver.findElement(By.cssSelector("#\\32 > .fa-solid")).click();
	    {
	      String value = driver.findElement(By.id("allergyNote")).getAttribute("value");
	      assertThat(value, is("Hayfever"));
	    }
	    driver.findElement(By.id("otherNote")).click();
	    driver.findElement(By.cssSelector(".row:nth-child(9) .btn")).click();
	    
	    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	    sleep() ;	
	    
	    driver.findElement(By.linkText("Back")).click();
	    
	    
	    driver.findElement(By.cssSelector(".d-none:nth-child(2)")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }	  
	  
	  @Test
	  @Order(410)		  
	  public void T410RecordAndViewIncident() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(2575, 1407));
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".monthlydate:nth-child(6) .sessionTitle")).click();
	    driver.findElement(By.linkText("Record Incident")).click();
	    driver.findElement(By.id("incidentSummary")).click();
	    driver.findElement(By.id("incidentSummary")).sendKeys("Brother and sister had a disagreement");
	    driver.findElement(By.id("newAttendee")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("newAttendee"));
	      dropdown.findElement(By.xpath("//option[. = 'Jonny Jones']")).click();
	    }
	    driver.findElement(By.cssSelector("option:nth-child(2)")).click();
	    driver.findElement(By.id("attendeeNotes")).click();
	    driver.findElement(By.id("attendeeNotes")).sendKeys("Hurt his hand");
	    driver.findElement(By.cssSelector(".fa-plus")).click();
	    driver.findElement(By.id("newAttendee")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("newAttendee"));
	      dropdown.findElement(By.xpath("//option[. = 'Ruth Jones']")).click();
	    }
	    driver.findElement(By.cssSelector("option:nth-child(3)")).click();
	    driver.findElement(By.cssSelector(".col-sm-9:nth-child(2) > #attendeeNotes")).click();
	    driver.findElement(By.cssSelector(".col-sm-9:nth-child(2) > #attendeeNotes")).sendKeys("Ruth hurt her knee");
	    driver.findElement(By.name("submit")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Incident has been recorded and all parents notified"));
	    driver.findElement(By.cssSelector(".nav-item:nth-child(5) .ms-1")).click();
	    
	    //assertThat(driver.findElement(By.cssSelector("td > div:nth-child(0)")).getText(), is("Jonny Jones"));
	    driver.findElement(By.cssSelector(".fa-eye")).click();	    
	    driver.findElement(By.linkText("Back")).click();
	    
	    driver.findElement(By.cssSelector(".fa-pencil")).click();
	    driver.findElement(By.id("attendeeNotes-1")).click();
	    sendKeys(findElement(By.id("attendeeNotes-1")), "Ruth hurt her knee and her head");
	    driver.findElement(By.name("submit")).click();
	    
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.id("inputEmail")).sendKeys("peterjones@hattonsplace.co.uk");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".nav-item:nth-child(3) .ms-1")).click();
	    assertThat(driver.findElement(By.cssSelector(".filterrow > td:nth-child(3)")).getText(), is("Ruth hurt her knee and her head"));
	    driver.findElement(By.id("students")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("students"));
	      dropdown.findElement(By.xpath("//option[. = 'Jonny']")).click();
	    }
	    driver.findElement(By.cssSelector("option:nth-child(2)")).click();
	    assertThat(driver.findElement(By.cssSelector(".filterrow > td:nth-child(3)")).getText(), is("Hurt his hand"));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }

	  
	  
}
