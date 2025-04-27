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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.afterschoolclub.controller.MainController;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(10)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AfterSchoolClubApplicationTests {

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
  @Order(10)
  public void T010HomePage() {
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector("a > img"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.id("inputEmail")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.id("inputEmail"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.id("inputPassword")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.id("inputPassword"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.linkText("Forgot password"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".btn"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".nav-item:nth-child(1) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector("li:nth-child(2) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".nav-item:nth-child(3) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".nav-item:nth-child(4) .ms-1"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.cssSelector(".asc-home-box-white > h1")).click();
	    driver.findElement(By.cssSelector("body")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".asc-home-box-white > h1"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.cssSelector(".asc-home-box-lightblue")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".asc-home-box-lightblue > h1"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.cssSelector(".asc-home-box-blue > h1")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".asc-home-box-blue > h1"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".asc-home-box-orange > h1"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.linkText("Privacy Statement"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.linkText("Terms and Conditions"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector("tr:nth-child(1) .nav-link > span"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector("tr:nth-child(2) span"));
	      assert(elements.size() > 0);
	    }
	    {
	      List<WebElement> elements = driver.findElements(By.linkText("Policies and Procedures"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.linkText("Privacy Statement")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".asc-header"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    assertThat(driver.findElement(By.cssSelector(".asc-header")).getText(), is("Privacy Statement"));
	    driver.findElement(By.linkText("Terms and Conditions")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".asc-header"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    assertThat(driver.findElement(By.cssSelector(".asc-header")).getText(), is("Terms and Conditions"));
	    driver.close();
	  }


  @Test
  @Order(30)
  public void T030AdministratorLogonLogoff() {

	    driver.findElement(By.cssSelector(".btn")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".btn"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    sendKeys(driver.findElement(By.id("inputEmail")), "admin@afterschool-club.com");
	    sendKeys(driver.findElement(By.id("inputPassword")), "wrongpassword");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email or password incorrect"));
	    driver.findElement(By.linkText("Forgot password")).click();
	    driver.findElement(By.id("email")).click();
	    sendKeys(driver.findElement(By.id("email")), "admin@hattonsplace.com");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("User with that email address does not exist"));
	    driver.findElement(By.id("inputEmail")).click();
	    sendKeys(driver.findElement(By.id("inputEmail")), "admin@afterschool-club.com");
	    sendKeys(driver.findElement(By.id("inputPassword")), "ManUtd01");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector(".asc-header")).click();
	    assertThat(driver.findElement(By.cssSelector(".asc-header")).getText(), is("Club Timetable"));
	    assertThat(driver.findElement(By.cssSelector(".mx-1")).getText(), is("Adam Hatton"));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.findElement(By.cssSelector(".asc-home-box-white > h1")).click();
	    driver.findElement(By.cssSelector(".asc-home-box-white > h1")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".asc-home-box-white > h1"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector(".asc-home-box-white > h1")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".btn"));
	      assert(elements.size() > 0);
	    }
	    driver.close();
	  }

  @Test
  @Order(40)
  public void T040CreateEditFootballClub() {
	    login();
	    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
	    driver.findElement(By.id("title")).click();
	    sendKeys(driver.findElement(By.id("title")), "Football Club - Y5");
	    driver.findElement(By.id("description")).click();
	    sendKeys(driver.findElement(By.id("description")), "The Football Club for Year 5 children offers a fun and energetic environment where kids can develop their skills, teamwork, and love for the game. Whether they’re a beginner or experienced player, our sessions focus on improving technique, fitness, and understanding of the game through drills, practice matches, and friendly competition. It’s a great opportunity for children to build confidence, make new friends, and enjoy the excitement of football in a supportive and inclusive setting.");
	    driver.findElement(By.id("keywords")).click();
	    sendKeys(driver.findElement(By.id("keywords")), "Football Soccer Sport Ball Team Outside");
	    driver.findElement(By.id("basepriceinput")).click();	    
	    driver.findElement(By.id("basepriceinput")).sendKeys("£2.50");
	    driver.findElement(By.id("acceptsVouchers")).click();
	    driver.findElement(By.id("year5")).click();
	    driver.findElement(By.name("submit")).click();
	    WaitForTextToBeChanged(driver.findElement(By.id("error")));	    
	    assertThat(driver.findElement(By.id("error")).getText(), is("Created club"));
	    driver.findElement(By.cssSelector("li:nth-child(2) .ms-1")).click();
	    
	    assertThat(driver.findElement(By.cssSelector(".row > .asc-clubtitle")).getText(), is("Football Club - Y5"));	  
	    assertThat(driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)")).getText(), is("Year 5 only"));	  
	    assertThat(driver.findElement(By.cssSelector(".asc-clubprice")).getText(), is("£2.50"));	
	    assertThat(driver.findElement(By.cssSelector(".h-auto > .col-sm-12")).getText(), is("The Football Club for Year 5 children offers a fun and energetic environment where kids can develop their skills, teamwork, and love for the game. Whether they’re a beginner or experienced player, our sessions focus on improving technique, fitness, and understanding of the game through drills, practice matches, and friendly competition. It’s a great opportunity for children to build confidence, make new friends, and enjoy the excitement of football in a supportive and inclusive setting."));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
  
  @Test
  @Order(41)
  public void T041CreateClubBreakfastClub() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    sendKeys(driver.findElement(By.id("title")), "Breakfast Club");
    driver.findElement(By.id("description")).click();
    sendKeys(driver.findElement(By.id("description")), "Our Breakfast Club is the perfect start to your child’s day, offering a warm, friendly environment where children can enjoy a nutritious breakfast before school. With a variety of delicious options available, kids can fuel up and socialize with friends in a relaxed setting. The club also provides fun, quiet activities to engage in, ensuring children feel energised and ready to tackle the school day ahead. It’s a great way to ensure your child starts their morning on the right note!");
    driver.findElement(By.id("keywords")).click();
    sendKeys(driver.findElement(By.id("keywords")), "Morning Breakfast Early");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£4.50");
    driver.findElement(By.id("yearR")).click();
    driver.findElement(By.cssSelector(".row:nth-child(7) > .col-sm-9")).click();
    driver.findElement(By.id("year1")).click();
    driver.findElement(By.id("year2")).click();
    driver.findElement(By.id("year3")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("error")));	    
    assertThat(driver.findElement(By.id("error")).getText(), is("Created club"));	    	    
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(42)
  public void T042CreateClubArtsClub() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    sendKeys(driver.findElement(By.id("title")), "Art Club");
    driver.findElement(By.id("description")).click();
    sendKeys(driver.findElement(By.id("description")), "Our Art Club is a creative space where children can explore their imagination and express themselves through a variety of artistic activities. From painting and drawing to sculpture and crafts, kids will have the opportunity to experiment with different materials and techniques while developing their artistic skills. With a focus on fun and creativity, Art Club is the perfect place for children to unleash their inner artist and gain confidence in their artistic abilities.");
    driver.findElement(By.id("keywords")).click();
    sendKeys(driver.findElement(By.id("keywords")), "Art Creative Drawing Painting ");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£2.50");
    driver.findElement(By.id("acceptsVouchers")).click();
    driver.findElement(By.id("yearR")).click();
    driver.findElement(By.id("year1")).click();
    driver.findElement(By.id("year2")).click();
    driver.findElement(By.id("year3")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("error")));	    
    assertThat(driver.findElement(By.id("error")).getText(), is("Created club"));	
    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(43)
  public void T043CreateClubCraftClub() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    sendKeys(driver.findElement(By.id("title")), "Crafts Club");
    driver.findElement(By.id("description")).click();
    sendKeys(driver.findElement(By.id("description")), "Our Crafts Club offers children the chance to get creative and make unique, handmade projects using a variety of materials. From simple paper crafts to exciting DIY creations, kids will learn new techniques while having fun and expressing their individuality. It’s a great way for children to develop fine motor skills, problem-solving abilities, and their imagination, all while enjoying a hands-on, creative experience in a relaxed and friendly environment.");
    driver.findElement(By.id("keywords")).click();
    sendKeys(driver.findElement(By.id("keywords")), "Creative");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£2.50");
    driver.findElement(By.id("acceptsVouchers")).click();
    driver.findElement(By.id("year1")).click();
    driver.findElement(By.id("year2")).click();
    driver.findElement(By.id("year3")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("error")));	    
    assertThat(driver.findElement(By.id("error")).getText(), is("Created club"));	
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(44)
  public void T044CreateClubChoirClub() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    sendKeys(driver.findElement(By.id("title")), "Choir Club");
    driver.findElement(By.id("description")).click();
    sendKeys(driver.findElement(By.id("description")), "The Choir Club is a wonderful opportunity for children to explore their love for music and develop their singing skills in a fun, supportive environment. Through practicing a variety of songs, children will improve their vocal technique, rhythm, and confidence while learning the importance of teamwork and harmony. Whether they’re a budding singer or simply enjoy music, our Choir Club is the perfect place for children to express themselves and make beautiful music together!\\n");
    driver.findElement(By.id("keywords")).click();
    sendKeys(driver.findElement(By.id("keywords")), "Music Singing");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£2.00");
    driver.findElement(By.id("acceptsVouchers")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("error")));	    
    assertThat(driver.findElement(By.id("error")).getText(), is("Created club"));	
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  
  @Test
  @Order(45)
  public void T045CreateClubTennisClub() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    sendKeys(driver.findElement(By.id("title")), "Tennis Club");
    driver.findElement(By.id("description")).click();
    sendKeys(driver.findElement(By.id("description")), "Our Tennis Club for young children is a fun and exciting way to introduce them to the sport, focusing on basic skills like hand-eye coordination, balance, and teamwork. Through engaging games and simple drills, children will develop their confidence and love for tennis in a supportive, friendly environment. Whether they’re picking up a racket for the first time or looking to improve their skills, our sessions provide a perfect balance of fun and learning. It’s the ideal way for young kids to stay active and enjoy the game!");
    driver.findElement(By.id("keywords")).click();
    sendKeys(driver.findElement(By.id("keywords")), "Ball Outside Sport");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£3.00");
    driver.findElement(By.id("acceptsVouchers")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("error")));	    
    assertThat(driver.findElement(By.id("error")).getText(), is("Created club"));	
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(50)
  public void T050CreateLocationFootballPitchLarge() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Football Pitch Large");
    sendKeys(driver.findElement(By.name("locationDescription")), "Large football pitch to the rear of the school");
    sendKeys(driver.findElement(By.name("locationKeywords")), "Football Outside Sport");
    sendKeys(driver.findElement(By.name("locationCapacity")), "40");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }


  
  @Test
  @Order(51)
  public void T051CreateLocationFootballPitchSmall() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Football Pitch Small");
    sendKeys(driver.findElement(By.name("locationDescription")), "Small football pitch to the left of main entrance");
    sendKeys(driver.findElement(By.name("locationKeywords")), "Football Outside Sport");
    sendKeys(driver.findElement(By.name("locationCapacity")), "40");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(52)
  public void T052CreateLocationCanteen() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Canteen");
    sendKeys(driver.findElement(By.name("locationDescription")), "Main canteen");
    sendKeys(driver.findElement(By.name("locationKeywords")), "food");
    sendKeys(driver.findElement(By.name("locationCapacity")), "100");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(53)
  public void T053CreateLocationClassroom1() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Classroom 1");
    sendKeys(driver.findElement(By.name("locationDescription")), "First class room off main corridor");
    sendKeys(driver.findElement(By.name("locationKeywords")), "classroom maths ");
    sendKeys(driver.findElement(By.name("locationCapacity")), "30");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
      
  @Test
  @Order(54)
  
  public void T054CreateLocationClassroom2() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Classroom 2");
    sendKeys(driver.findElement(By.name("locationDescription")), "Second class room off main corridor");
    sendKeys(driver.findElement(By.name("locationKeywords")), "classroom english");
    sendKeys(driver.findElement(By.name("locationCapacity")), "30");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(55)
  public void T055CreateLocationMusicRoom() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Music Room");
    sendKeys(driver.findElement(By.name("locationDescription")), "Music room linked to main assembly hall");
    sendKeys(driver.findElement(By.name("locationKeywords")), "music classroom");
    sendKeys(driver.findElement(By.name("locationCapacity")), "100");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(56)
  public void T056CreateLocationSportsHall() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    sendKeys(driver.findElement(By.name("locationName")), "Sports Hall");
    sendKeys(driver.findElement(By.name("locationDescription")), "The gym");
    sendKeys(driver.findElement(By.name("locationKeywords")), "sport gym soccer netball basketball");
    sendKeys(driver.findElement(By.name("locationCapacity")), "50");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  
  
  @Test
  @Order(60)
  public void T060CreateStaffMrStockwell() {
	    login();

	    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
	    driver.findElement(By.id("staffTab")).click();
	    driver.findElement(By.name("staffTitle")).click();
	    sendKeys(driver.findElement(By.name("staffTitle")), "Mr");
	    sendKeys(driver.findElement(By.name("staffFirstName")), "Patrick");
	    sendKeys(driver.findElement(By.name("staffSurname")), "Stockwell");
	    sendKeys(driver.findElement(By.name("staffEmail")), "pstockwell@afterschool-club.com");
	    sendKeys(driver.findElement(By.name("staffTelephoneNum")), "01256811811");
	    sendKeys(driver.findElement(By.name("staffDescription")), "Maths Teacher");
	    sendKeys(driver.findElement(By.name("staffKeywords")), "Maths Puzzles Games");
	    driver.findElement(By.cssSelector(".col-sm-1:nth-child(9) > .btn-success > .fa")).click();	    
	    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
	    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded staff member"));	    
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
  
  @Test
  @Order(61)
  public void T061TryCreateStaffMrStockwellAgain() {
	    login();

	    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
	    driver.findElement(By.id("staffTab")).click();
	    driver.findElement(By.name("staffTitle")).click();
	    sendKeys(driver.findElement(By.name("staffTitle")), "Mr");
	    sendKeys(driver.findElement(By.name("staffFirstName")), "Patrick");
	    sendKeys(driver.findElement(By.name("staffSurname")), "Stockwell");
	    sendKeys(driver.findElement(By.name("staffEmail")), "pstockwell@afterschool-club.com");
	    sendKeys(driver.findElement(By.name("staffTelephoneNum")), "01256811811");
	    sendKeys(driver.findElement(By.name("staffDescription")), "Maths Teacher");
	    sendKeys(driver.findElement(By.name("staffKeywords")), "Maths Puzzles Games");
	    driver.findElement(By.cssSelector(".col-sm-1:nth-child(9) > .btn-success > .fa")).click();	    
	    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
	    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Failed to save new staff member"));	    
	    driver.findElement(By.linkText("Back")).click();
	    driver.findElement(By.cssSelector(".mx-1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }  
  
  @Test
  @Order(62)
  public void T062CreateStaffMrGunn() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("staffTab")).click();
    driver.findElement(By.name("staffTitle")).click();
    sendKeys(driver.findElement(By.name("staffTitle")), "Mr");
    sendKeys(driver.findElement(By.name("staffFirstName")), "Vincent");
    sendKeys(driver.findElement(By.name("staffSurname")), "Gunn");
    sendKeys(driver.findElement(By.name("staffEmail")), "vgunn@afterschool-club.com");
    sendKeys(driver.findElement(By.name("staffTelephoneNum")), "01256811812");
    sendKeys(driver.findElement(By.name("staffDescription")), "PE Teacher");
    sendKeys(driver.findElement(By.name("staffKeywords")), "Sport Football fitness Rugby");
    driver.findElement(By.cssSelector(".col-sm-1:nth-child(9) > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	        
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded staff member"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  
  @Test
  @Order(63)
  public void T063EditStaffMrStockwell() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("staffTab")).click();
    driver.findElement(By.id("keywords-4")).click();
    driver.findElement(By.cssSelector("#keywords-4 > .form-control")).click();
    sendKeys(driver.findElement(By.cssSelector("#keywords-4 > .form-control")), "Maths Puzzles Games Chess");
    driver.findElement(By.name("staffTitle")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	        
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated staff"));    
    driver.close();
  }  

  @Test
  @Order(70)
  public void T070CreateAndEditFootballGoalsLarge() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Football Goals Large");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Large Football Goal suitable for large pitch");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Football Soccer Goal");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "4");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new equipment"));    

    
    
    
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Football Goals Small");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Small Football Goal suitbale for smaller pitch");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Football Soccer Goal");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "8");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new equipment"));     
    
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("name-11")).click();
    driver.findElement(By.cssSelector("#name-11 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#name-11 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#name-11 > .form-control")).click();
    driver.findElement(By.cssSelector("#name-11 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#name-11 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#name-11 > .form-control")).click();
    driver.findElement(By.id("description-11")).click();
    driver.findElement(By.cssSelector("#description-11 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#description-11 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#description-11 > .form-control")).click();
    driver.findElement(By.id("keywords-11")).click();
    driver.findElement(By.cssSelector("#keywords-11 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#keywords-11 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#keywords-11 > .form-control")).click();
    driver.findElement(By.id("quantity-11")).click();
    driver.findElement(By.id("name-12")).click();
    driver.findElement(By.cssSelector("#name-12 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#name-12 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#name-12 > .form-control")).click();
    driver.findElement(By.id("description-12")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#description-12 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("keywords-12")).click();
    driver.findElement(By.cssSelector("#keywords-12 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#keywords-12 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#keywords-12 > .form-control")).click();
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("quantity-12")).click();
    driver.findElement(By.cssSelector("#quantity-12 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#quantity-12 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("description-12")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();    
    sendKeys(driver.findElement(By.cssSelector("#description-12 > .form-control")), "Small Football Goal suitable for smaller pitch");
    
    driver.findElement(By.name("equipmentName")).click();    
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    
    
    
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.cssSelector("#undo-12 > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully undone changes to equipment"));
    
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("description-12")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#description-12 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("description-12")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    driver.findElement(By.id("description-12")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();    
    sendKeys(driver.findElement(By.cssSelector("#description-12 > .form-control")), "Small Football Goal suitable for smaller pitch");
    driver.findElement(By.name("equipmentName")).click();
    

    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    
    
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("quantity-11")).click();
    driver.findElement(By.cssSelector("#quantity-11 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#quantity-11 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    sendKeys(driver.findElement(By.cssSelector("#quantity-11 > .form-control")), "2");    
    driver.findElement(By.name("equipmentName")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("quantity-11")).click();
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  

  @Test
  @Order(71)
  public void T071CreateFootballsAndBibs() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Football Size 3");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Small fa");
    driver.findElement(By.name("equipmentDescription")).click();
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Football suitable for players aged 7-10");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Football Soccer Ball");
    driver.findElement(By.name("equipmentQuantity")).click();
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "20");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Football Size 4");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Football suitable for players aged 11 - 14");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Football Soccer Ball");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "10");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Football Size 5");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Standard sized football");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Football Soccer Ball");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "4");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Football Size 2");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Midi balls typically used by players under 5 years old");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Football Soccer Balls");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "15");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#equipmentNewRow .fa"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Red bibs");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Bibs suitable for distinguishing teams for sports");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Sport bib red");
    driver.findElement(By.name("equipmentQuantity")).click();
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "25");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#equipmentNewRow .btn"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Blue bibs");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Bibs suitable for distinguishing teams for sports");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Sport bib blue");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "25");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Gren bibs");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Bibs suitable for distinguishing teams for sports");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Sport bib green");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "30");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#equipmentNewRow .fa"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.id("name-19")).click();
    driver.findElement(By.cssSelector("#name-19 > .form-control")).click();
    
    
    sendKeys(driver.findElement(By.cssSelector("#name-19 > .form-control")),"Green bibs");
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Yellow Bibs");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "Bibs suitable for distinguishing teams for sports");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "Sport bib yellow");
    driver.findElement(By.name("equipmentQuantity")).click();
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "20");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.name("equipmentName")).click();
    sendKeys(driver.findElement(By.name("equipmentName")), "Black bibs");
    sendKeys(driver.findElement(By.name("equipmentDescription")), "entered in error and will delete");
    sendKeys(driver.findElement(By.name("equipmentKeywords")), "black bib");
    sendKeys(driver.findElement(By.name("equipmentQuantity")), "5");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("filter")).click();
    sendKeys(driver.findElement(By.id("filter")), "bib");
    driver.findElement(By.cssSelector("#delete-21 > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Equipment deleted."));
    driver.findElement(By.id("name-20")).click();
    driver.findElement(By.cssSelector("#name-20 > .form-control")).click();
    sendKeys(driver.findElement(By.cssSelector("#name-20 > .form-control")), "Yellow bibs");
    driver.findElement(By.name("equipmentName")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    driver.findElement(By.cssSelector("#undo-20 > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully undone changes to equipment"));
    driver.findElement(By.id("name-20")).click();
    driver.findElement(By.cssSelector("#name-20 > .form-control")).click();
    sendKeys(driver.findElement(By.cssSelector("#name-20 > .form-control")), "Yellow bibs");
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
  }

  @Test
  @Order(80)
  public void T080CreateMenuItemsandMenu() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Fruit Juice");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "One of Orange Juice, Apple Juice or Pineapple Juice");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "None");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.75");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));       
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  
  @Test
  @Order(81)
  public void T081CreateMoreDrinks() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    
    
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Milk");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Glass of semi skimmed milk");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains dairy products");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.50");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Water");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Tap water");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "None");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.00");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Water - bottled");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Bottle of still mineral water ");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "None");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.25");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    
    driver.findElement(By.name("menuOptionName")).click();    
    sendKeys(driver.findElement(By.name("menuOptionName")), "Squash");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains artificial sweeteners such as aspartame");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.25");    
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Glass of orange, lemon, or blackcurrant squash");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    
    
    assertThat(driver.findElement(By.id("name-5")).getText(), is("Squash"));
    assertThat(driver.findElement(By.id("additionalCost-4")).getText(), is("£0.25"));
    assertThat(driver.findElement(By.id("allergyInformation-2")).getText(), is("Contains dairy products"));
    assertThat(driver.findElement(By.id("description-2")).getText(), is("Glass of semi skimmed milk"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  } 
  
  @Test
  @Order(82)
  public void T082CreateMoreBreakfastItems() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();        
    driver.findElement(By.id("menuOptionTab")).click();
    
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Kellogs Cornflakes");    
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Bowl of cornflakw with semi skimmed milf");    
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains gluten");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£1.10");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Weetabix");    
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "2 Weetabix biscuits with milk");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains gluten and dairy");    
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£1.10");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));    
    
    driver.findElement(By.id("description-6")).click();
    driver.findElement(By.cssSelector("#description-6 > .form-control")).click();
    sendKeys(driver.findElement(By.cssSelector("#description-6 > .form-control")), "Kellogs Cornflakes with  milk");
    
    driver.findElement(By.name("menuOptionName")).click();       
    sendKeys(driver.findElement(By.name("menuOptionName")), "Coco Pops");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Kellogs Coco Pops");    
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains gluten. May contain traces of nuts. ");    
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£1.10");    
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));	    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));    
    
    
    {
      WebElement element = driver.findElement(By.cssSelector(".col-sm-1 > .btn-success"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.id("allergyInformation-7")).click();
    driver.findElement(By.cssSelector("#allergyInformation-7 > .form-control")).click();
    sendKeys(driver.findElement(By.cssSelector("#allergyInformation-7 > .form-control")), "Contains gluten and dairy. May contain traces of nuts. ");
    driver.findElement(By.id("allergyInformation-6")).click();
    driver.findElement(By.cssSelector("#allergyInformation-6 > .form-control")).click();
    sendKeys(driver.findElement(By.cssSelector("#allergyInformation-6 > .form-control")), "Contains gluten. May contain traces of nuts. ");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(    driver.findElement(By.name("menuOptionName")), "Porridge");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains Oats");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).click();
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Quakers porridge");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.95");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Apple");
    driver.findElement(By.name("menuOptionDescription")).click();
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Granny Smith apple");
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "None");
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.60");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Orange");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Juicy Jaffa Orange");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains Cit s 1, Cit s 2 and Cit s 3.");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.70");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    {
      WebElement element = driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Banana");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains Mus a1, Mus a2");
    driver.findElement(By.name("menuOptionDescription")).click();
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Medium sized banana");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£0.65");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    {
      WebElement element = driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Grapes");
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Small tub of mixed black and green grapes");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "Contains several allergenic proteins including lipid transfer protein(LPD)");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£1.00");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    {
      WebElement element = driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.name("menuOptionName")).click();
    sendKeys(driver.findElement(By.name("menuOptionName")), "Fruit Salad");
    driver.findElement(By.name("menuOptionDescription")).click();
    sendKeys(driver.findElement(By.name("menuOptionDescription")), "Mixed salad bow including Strawberries, blueberries, grapes and kiwi");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    sendKeys(driver.findElement(By.name("menuOptionAllergyInformation")), "None");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    sendKeys(driver.findElement(By.id("menuOptionAdditionalCostInput")), "£1.25");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    assertThat(driver.findElement(By.id("name-14")).getText(), is("Fruit Salad"));
    assertThat(driver.findElement(By.id("additionalCost-14")).getText(), is("£1.25"));
    assertThat(driver.findElement(By.id("allergyInformation-11")).getText(), is("Contains Cit s 1, Cit s 2 and Cit s 3."));
    assertThat(driver.findElement(By.id("description-10")).getText(), is("Granny Smith apple"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  
  @Test
  @Order(83)
  public void T083EditMenuItemFruitJuice() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.id("name-1")).click();
    driver.findElement(By.id("additionalCost-1")).click();
    sendKeys(driver.findElement(By.cssSelector("#additionalCost-1 > .form-control")), "£0.50");
    driver.findElement(By.name("menuOptionName")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated menu item"));
    assertThat(driver.findElement(By.id("additionalCost-1")).getText(), is("£0.50"));
    driver.findElement(By.cssSelector(".spacer")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    assertThat(driver.findElement(By.id("additionalCost-1")).getText(), is("£0.50"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  

  @Test
  @Order(90)
  public void T090CreateBreakfastMenu() {
	    login();

    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.name("menuGroupName")).click();
    sendKeys(driver.findElement(By.name("menuGroupName")), "Drinks Selection");
    driver.findElement(By.cssSelector(".accordion-button > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully added new menu group"));
    driver.findElement(By.name("menuGroupName")).click();
    sendKeys(driver.findElement(By.name("menuGroupName")), "Fruit Selection");
    driver.findElement(By.cssSelector(".accordion-button > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully added new menu group"));
    driver.findElement(By.name("menuGroupName")).click();
    sendKeys(driver.findElement(By.name("menuGroupName")), "Cereal Selection");
    driver.findElement(By.cssSelector(".accordion-button > .btn-success > .fa")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully added new menu group"));
    
    driver.findElement(By.cssSelector("#mgrow-1 .accordion-button")).click();
    driver.findElement(By.id("add-1")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-1 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-2 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-3 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-4 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-5 input")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully added new menu item"));
    driver.findElement(By.cssSelector("#mgrow-2 .accordion-button")).click();
    driver.findElement(By.cssSelector("#add-2 > .fa")).click();
    sleep();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-10 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-12 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-8 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-8 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-13 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-14 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-11 input")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully added new menu item"));
    driver.findElement(By.cssSelector("#mgrow-3 .accordion-button")).click();
    driver.findElement(By.cssSelector("#newItem-3 span:nth-child(3)")).click();
    driver.findElement(By.cssSelector("#add-3 > .fa")).click();
    driver.findElement(By.id("addFilter")).click();
    sendKeys(driver.findElement(By.id("addFilter")), "porr");
    driver.findElement(By.cssSelector("#menuOptionList2 #row-9 input")).click();
    driver.findElement(By.id("addFilter")).click();
    sendKeys(driver.findElement(By.id("addFilter")), "Gluten");
    driver.findElement(By.cssSelector("#menuOptionList2 #row-8 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-6 input")).click();
    driver.findElement(By.cssSelector("#menuOptionList2 #row-7 input")).click();
    driver.findElement(By.name("submit")).click();
    WaitForTextToBeChanged(driver.findElement(By.id("validationMessage")));    
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully added new menu item"));
    driver.findElement(By.cssSelector("#mgrow-2 .accordion-button")).click();
    driver.findElement(By.cssSelector("#mgrow-1 .accordion-button")).click();
    driver.findElement(By.cssSelector("#mgrow-3 .accordion-button")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
  }

  
  
}
