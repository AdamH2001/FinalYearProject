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

  public void WaitForTextToBeChanged(final WebElement element){
      try {
          String oldValueOfCTextOfWebElement = element.getText();
          wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element,oldValueOfCTextOfWebElement)));
      }
      catch (Exception e){
      }
  }

	public String is(String s) {
		return s;
	}

	public void assertThat(String found, String expected) {
		assertEquals(expected, found);
		
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
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(1983, 1231));
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
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(1983, 1347));
	    driver.findElement(By.cssSelector(".btn")).click();
	    {
	      List<WebElement> elements = driver.findElements(By.cssSelector(".btn"));
	      assert(elements.size() > 0);
	    }
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.id("inputPassword")).sendKeys("wrongpassword");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Email or password incorrect"));
	    driver.findElement(By.linkText("Forgot password")).click();
	    driver.findElement(By.id("email")).click();
	    driver.findElement(By.id("email")).sendKeys("admin@hattonsplace.com");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("User with that email address does not exist"));
	    driver.findElement(By.id("inputEmail")).click();
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
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
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(1983, 1347));
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
	    driver.findElement(By.id("title")).click();
	    driver.findElement(By.id("title")).sendKeys("Football Club - Y5");
	    driver.findElement(By.id("description")).click();
	    driver.findElement(By.id("description")).sendKeys("The Football Club for Year 6 children offers a fun and energetic environment where kids can develop their skills, teamwork, and love for the game. Whether they’re a beginner or experienced player, our sessions focus on improving technique, fitness, and understanding of the game through drills, practice matches, and friendly competition. It’s a great opportunity for children to build confidence, make new friends, and enjoy the excitement of football in a supportive and inclusive setting.");
	    driver.findElement(By.id("keywords")).click();
	    driver.findElement(By.id("keywords")).sendKeys("Football Soccer Sport Ball Team");
	    driver.findElement(By.id("basepriceinput")).click();
	    
	    driver.findElement(By.id("basepriceinput")).sendKeys("£2.50");
	    driver.findElement(By.id("acceptsVouchers")).click();
	    driver.findElement(By.id("year5")).click();
	    driver.findElement(By.id("keywords")).click();
	    driver.findElement(By.id("keywords")).sendKeys("Football Soccer Sport Outside");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
	    driver.findElement(By.cssSelector("li:nth-child(2) .ms-1")).click();
	    driver.findElement(By.cssSelector(".row > .asc-clubtitle")).click();
	    driver.findElement(By.cssSelector(".row > .asc-clubtitle")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".row > .asc-clubtitle"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector(".row > .asc-clubtitle")).click();
	    assertThat(driver.findElement(By.cssSelector(".row > .asc-clubtitle")).getText(), is("Football Club - Y5"));
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(1)")).click();
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(1)")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(1)"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(1)")).click();
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)")).click();
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)"));
	      Actions builder = new Actions(driver);
	      builder.doubleClick(element).perform();
	    }
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)")).click();
	    driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)")).click();
	    driver.findElement(By.cssSelector(".col-sm-7 > .row:nth-child(2) > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector(".row:nth-child(2) > .col-sm-12 > span:nth-child(2)")).getText(), is("Year 5 only"));
	    driver.findElement(By.cssSelector(".asc-clubprice")).click();
	    assertThat(driver.findElement(By.cssSelector(".asc-clubprice")).getText(), is("£2.50"));
	    driver.findElement(By.linkText("Edit Club...")).click();
	    driver.findElement(By.id("description")).click();
	    
	    
	    js.executeScript("arguments[0].value='The Football Club for Year 5 children offers a fun and energetic environment where kids can develop their skills, teamwork, and love for the game. Whether they’re a beginner or experienced player, our sessions focus on improving technique, fitness, and understanding of the game through drills, practice matches, and friendly competition. It’s a great opportunity for children to build confidence, make new friends, and enjoy the excitement of football in a supportive and inclusive setting.';",  driver.findElement(By.id("description")));
	    
//	    driver.findElement(By.id("description")).sendKeys("The Football Club for Year 5 children offers a fun and energetic environment where kids can develop their skills, teamwork, and love for the game. Whether they’re a beginner or experienced player, our sessions focus on improving technique, fitness, and understanding of the game through drills, practice matches, and friendly competition. It’s a great opportunity for children to build confidence, make new friends, and enjoy the excitement of football in a supportive and inclusive setting.");
	    driver.findElement(By.name("submit")).click();
	    driver.findElement(By.cssSelector(".h-auto > .col-sm-12")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".h-auto > .col-sm-12"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".h-auto > .col-sm-12"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".h-auto > .col-sm-12"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.cssSelector(".h-auto > .col-sm-12")).click();
	    driver.findElement(By.cssSelector(".h-auto > .col-sm-12")).click();
	    assertThat(driver.findElement(By.cssSelector(".h-auto > .col-sm-12")).getText(), is("The Football Club for Year 5 children offers a fun and energetic environment where kids can develop their skills, teamwork, and love for the game. Whether they’re a beginner or experienced player, our sessions focus on improving technique, fitness, and understanding of the game through drills, practice matches, and friendly competition. It’s a great opportunity for children to build confidence, make new friends, and enjoy the excitement of football in a supportive and inclusive setting."));
	    driver.findElement(By.id("dropdownUser1")).click();
	    driver.findElement(By.linkText("Sign out")).click();
	    driver.close();
	  }
  
  @Test
  @Order(41)
  public void T041CreateClubBreakfastClub() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    driver.findElement(By.id("title")).sendKeys("Breakfast Club");
    driver.findElement(By.id("description")).click();
    driver.findElement(By.id("description")).sendKeys("Our Breakfast Club is the perfect start to your child’s day, offering a warm, friendly environment where children can enjoy a nutritious breakfast before school. With a variety of delicious options available, kids can fuel up and socialize with friends in a relaxed setting. The club also provides fun, quiet activities to engage in, ensuring children feel energised and ready to tackle the school day ahead. It’s a great way to ensure your child starts their morning on the right note!");
    driver.findElement(By.id("keywords")).click();
    driver.findElement(By.id("keywords")).sendKeys("Morning Breakfast Early");
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
    {
      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(42)
  public void T042CreateClubArtsClub() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    driver.findElement(By.id("title")).sendKeys("Art Club");
    driver.findElement(By.id("description")).click();
    driver.findElement(By.id("description")).sendKeys("Our Art Club is a creative space where children can explore their imagination and express themselves through a variety of artistic activities. From painting and drawing to sculpture and crafts, kids will have the opportunity to experiment with different materials and techniques while developing their artistic skills. With a focus on fun and creativity, Art Club is the perfect place for children to unleash their inner artist and gain confidence in their artistic abilities.");
    driver.findElement(By.id("keywords")).click();
    driver.findElement(By.id("keywords")).sendKeys("Art Creative Drawing Painting ");
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
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(43)
  public void t043CreateClubCraftClub() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    driver.findElement(By.id("title")).sendKeys("Crafts Club");
    driver.findElement(By.id("description")).click();
    driver.findElement(By.id("description")).sendKeys("Our Crafts Club offers children the chance to get creative and make unique, handmade projects using a variety of materials. From simple paper crafts to exciting DIY creations, kids will learn new techniques while having fun and expressing their individuality. It’s a great way for children to develop fine motor skills, problem-solving abilities, and their imagination, all while enjoying a hands-on, creative experience in a relaxed and friendly environment.");
    driver.findElement(By.id("keywords")).click();
    driver.findElement(By.id("keywords")).sendKeys("Creative");
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
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(44)
  public void t044CreateClubChoirClub() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    driver.findElement(By.id("title")).sendKeys("Choir Club");
    driver.findElement(By.id("description")).click();
    driver.findElement(By.id("description")).sendKeys("The Choir Club is a wonderful opportunity for children to explore their love for music and develop their singing skills in a fun, supportive environment. Through practicing a variety of songs, children will improve their vocal technique, rhythm, and confidence while learning the importance of teamwork and harmony. Whether they’re a budding singer or simply enjoy music, our Choir Club is the perfect place for children to express themselves and make beautiful music together!\\n");
    driver.findElement(By.id("keywords")).click();
    driver.findElement(By.id("keywords")).sendKeys("Music Singing");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£2.00");
    driver.findElement(By.id("acceptsVouchers")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  
  @Test
  @Order(45)
  public void t045CreateClubTennisClub() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(1) .ms-1")).click();
    driver.findElement(By.id("title")).click();
    driver.findElement(By.id("title")).sendKeys("Tennis Club");
    driver.findElement(By.id("description")).click();
    driver.findElement(By.id("description")).sendKeys("Our Tennis Club for young children is a fun and exciting way to introduce them to the sport, focusing on basic skills like hand-eye coordination, balance, and teamwork. Through engaging games and simple drills, children will develop their confidence and love for tennis in a supportive, friendly environment. Whether they’re picking up a racket for the first time or looking to improve their skills, our sessions provide a perfect balance of fun and learning. It’s the ideal way for young kids to stay active and enjoy the game!");
    driver.findElement(By.id("keywords")).click();
    driver.findElement(By.id("keywords")).sendKeys("Ball Outside Sport");
    driver.findElement(By.id("basepriceinput")).click();
    driver.findElement(By.id("basepriceinput")).sendKeys("£3.00");
    driver.findElement(By.id("acceptsVouchers")).click();
    driver.findElement(By.id("year4")).click();
    driver.findElement(By.id("year5")).click();
    driver.findElement(By.id("year6")).click();
    driver.findElement(By.name("submit")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#error > .col-sm-12"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#error > .col-sm-12")).click();
    assertThat(driver.findElement(By.cssSelector("#error > .col-sm-12")).getText(), is("Created club"));
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(50)
  public void T050CreateLocationFootballPitchLarge() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Football Pitch Large");
    driver.findElement(By.name("locationDescription")).sendKeys("Large football pitch to the rear of the school");
    driver.findElement(By.name("locationKeywords")).sendKeys("Football Outside Sport");
    driver.findElement(By.name("locationCapacity")).sendKeys("40");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }


  
  @Test
  @Order(51)
  public void t051CreateLocationFootballPitchSmall() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Football Pitch Small");
    driver.findElement(By.name("locationDescription")).sendKeys("Small football pitch to the left of main entrance");
    driver.findElement(By.name("locationKeywords")).sendKeys("Football Outside Sport");
    driver.findElement(By.name("locationCapacity")).sendKeys("40");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(52)
  public void t052CreateLocationCanteen() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Canteen");
    driver.findElement(By.name("locationDescription")).sendKeys("Main canteen");
    driver.findElement(By.name("locationKeywords")).sendKeys("food");
    driver.findElement(By.name("locationCapacity")).sendKeys("100");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(53)
  public void t053CreateLocationClassroom1() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Classroom 1");
    driver.findElement(By.name("locationDescription")).sendKeys("First class room off main corridor");
    driver.findElement(By.name("locationKeywords")).sendKeys("classroom maths ");
    driver.findElement(By.name("locationCapacity")).sendKeys("30");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
      
  @Test
  @Order(54)
  
  public void t054CreateLocationClassroom2() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Classroom 2");
    driver.findElement(By.name("locationDescription")).sendKeys("Second class room off main corridor");
    driver.findElement(By.name("locationKeywords")).sendKeys("classroom english");
    driver.findElement(By.name("locationCapacity")).sendKeys("30");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(55)
  public void t055CreateLocationMusicRoom() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Music Room");
    driver.findElement(By.name("locationDescription")).sendKeys("Music room linked to main assembly hall");
    driver.findElement(By.name("locationKeywords")).sendKeys("music classroom");
    driver.findElement(By.name("locationCapacity")).sendKeys("100");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  @Test
  @Order(56)
  public void t056CreateLocationSportsHall() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("locationTab")).click();
    driver.findElement(By.name("locationName")).click();
    driver.findElement(By.name("locationName")).sendKeys("Sports Hall");
    driver.findElement(By.name("locationDescription")).sendKeys("The gym");
    driver.findElement(By.name("locationKeywords")).sendKeys("sport gym soccer netball basketball");
    driver.findElement(By.name("locationCapacity")).sendKeys("50");
    driver.findElement(By.cssSelector("#locationNewRow .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new location"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }
  
  
  
  @Test
  @Order(60)
  public void T060CreateStaffMrStockwell() {
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(1983, 1347));
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
	    driver.findElement(By.id("staffTab")).click();
	    driver.findElement(By.name("staffTitle")).click();
	    driver.findElement(By.name("staffTitle")).sendKeys("Mr");
	    driver.findElement(By.name("staffFirstName")).sendKeys("Patrick");
	    driver.findElement(By.name("staffSurname")).sendKeys("Stockwell");
	    driver.findElement(By.name("staffEmail")).sendKeys("pstockwell@afterschool-club.com");
	    driver.findElement(By.name("staffTelephoneNum")).sendKeys("01256811811");
	    driver.findElement(By.name("staffDescription")).sendKeys("Maths Teacher");
	    driver.findElement(By.name("staffKeywords")).sendKeys("Maths Puzzles Games");
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
	    driver.get("http://afterschool-club.com/");
	    driver.manage().window().setSize(new Dimension(1983, 1347));
	    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
	    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
	    driver.findElement(By.cssSelector(".btn")).click();
	    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
	    driver.findElement(By.id("staffTab")).click();
	    driver.findElement(By.name("staffTitle")).click();
	    driver.findElement(By.name("staffTitle")).sendKeys("Mr");
	    driver.findElement(By.name("staffFirstName")).sendKeys("Patrick");
	    driver.findElement(By.name("staffSurname")).sendKeys("Stockwell");
	    driver.findElement(By.name("staffEmail")).sendKeys("pstockwell@afterschool-club.com");
	    driver.findElement(By.name("staffTelephoneNum")).sendKeys("01256811811");
	    driver.findElement(By.name("staffDescription")).sendKeys("Maths Teacher");
	    driver.findElement(By.name("staffKeywords")).sendKeys("Maths Puzzles Games");
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
  public void t062CreateStaffMrGunn() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1983, 1347));
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("staffTab")).click();
    driver.findElement(By.name("staffTitle")).click();
    driver.findElement(By.name("staffTitle")).sendKeys("Mr");
    driver.findElement(By.name("staffFirstName")).sendKeys("Vincent");
    driver.findElement(By.name("staffSurname")).sendKeys("Gunn");
    driver.findElement(By.name("staffEmail")).sendKeys("vgunn@afterschool-club.com");
    driver.findElement(By.name("staffTelephoneNum")).sendKeys("01256811812");
    driver.findElement(By.name("staffDescription")).sendKeys("PE Teacher");
    driver.findElement(By.name("staffKeywords")).sendKeys("Sport Football fitness Rugby");
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
  public void t063EditStaffMrStockwell() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1713, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("staffTab")).click();
    driver.findElement(By.id("keywords-4")).click();
    driver.findElement(By.cssSelector("#keywords-4 > .form-control")).click();
    driver.findElement(By.cssSelector("#keywords-4 > .form-control")).sendKeys("Maths Puzzles Games Chess");
    driver.findElement(By.name("staffTitle")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("staffTab")).click();
    driver.findElement(By.id("keywords-4")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.id("dropdownUser1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  

  @Test
  @Order(70)
  public void T070CreatandEditFootballGoalsLarge() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1713, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Football Goals Large");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Large Football Goal suitable for large pitch");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Football Soccer Goal");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("4");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Football Goals Small");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Small Football Goal suitbale for smaller pitch");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Football Soccer Goal");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("8");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
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
    driver.findElement(By.cssSelector("#description-12 > .form-control")).sendKeys("Small Football Goal suitable for smaller pitch");
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.cssSelector("#undo-12 > .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
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
    driver.findElement(By.cssSelector("#description-12 > .form-control")).sendKeys("Small Football Goal suitable for smaller pitch");
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    driver.findElement(By.id("validationMessage")).click();
    {
      WebElement element = driver.findElement(By.id("validationMessage"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("staffTab")).click();
    driver.findElement(By.id("equipmentTab")).click();
    driver.findElement(By.id("description-12")).click();
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#description-12 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#description-12 > .form-control")).click();
    driver.findElement(By.cssSelector("body")).click();
    driver.findElement(By.id("quantity-11")).click();
    driver.findElement(By.cssSelector("#quantity-11 > .form-control")).click();
    {
      WebElement element = driver.findElement(By.cssSelector("#quantity-11 > .form-control"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    driver.findElement(By.cssSelector("#quantity-11 > .form-control")).sendKeys("2");
    driver.findElement(By.name("equipmentName")).click();
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
  public void t071CreateFootballsAndBibs() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1921, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Football Size 3");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Small fa");
    driver.findElement(By.name("equipmentDescription")).click();
    driver.findElement(By.name("equipmentDescription")).sendKeys("Football suitable for players aged 7-10");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Football Soccer Ball");
    driver.findElement(By.name("equipmentQuantity")).click();
    driver.findElement(By.name("equipmentQuantity")).sendKeys("20");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Football Size 4");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Football suitable for players aged 11 - 14");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Football Soccer Ball");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("10");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Football Size 5");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Standard sized football");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Football Soccer Ball");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("4");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Football Size 2");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Midi balls typically used by players under 5 years old");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Football Soccer Balls");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("15");
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
    driver.findElement(By.name("equipmentName")).sendKeys("Red bibs");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Bibs suitable for distinguishing teams for sports");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Sport bib red");
    driver.findElement(By.name("equipmentQuantity")).click();
    driver.findElement(By.name("equipmentQuantity")).sendKeys("25");
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
    driver.findElement(By.name("equipmentName")).sendKeys("Blue bibs");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Bibs suitable for distinguishing teams for sports");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Sport bib blue");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("25");
    driver.findElement(By.cssSelector("#equipmentNewRow .btn")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Grren bibs");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Bibs suitable for distinguishing teams for sports");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Sport bib green");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("30");
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
    driver.findElement(By.cssSelector("#name-19 > .form-control")).sendKeys("Green bibs");
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Yellow Bibs");
    driver.findElement(By.name("equipmentDescription")).sendKeys("Bibs suitable for distinguishing teams for sports");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("Sport bib yellow");
    driver.findElement(By.name("equipmentQuantity")).click();
    driver.findElement(By.name("equipmentQuantity")).sendKeys("20");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.name("equipmentName")).click();
    driver.findElement(By.name("equipmentName")).sendKeys("Black bibs");
    driver.findElement(By.name("equipmentDescription")).sendKeys("entered in error and will delete");
    driver.findElement(By.name("equipmentKeywords")).sendKeys("black bib");
    driver.findElement(By.name("equipmentQuantity")).sendKeys("5");
    driver.findElement(By.cssSelector("#equipmentNewRow .fa")).click();
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector("li:nth-child(6) .ms-1")).click();
    driver.findElement(By.id("filter")).click();
    driver.findElement(By.id("filter")).sendKeys("bib");
    driver.findElement(By.cssSelector("#delete-21 > .fa")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Equipment deleted."));
    driver.findElement(By.id("name-20")).click();
    driver.findElement(By.cssSelector("#name-20 > .form-control")).click();
    driver.findElement(By.cssSelector("#name-20 > .form-control")).sendKeys("Yellow bibs");
    driver.findElement(By.name("equipmentName")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully updated equipment"));
    driver.findElement(By.cssSelector("#undo-20 > .fa")).click();
    driver.findElement(By.name("equipmentName")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully undone changes to equipment"));
    driver.findElement(By.id("name-20")).click();
    driver.findElement(By.cssSelector("#name-20 > .form-control")).click();
    driver.findElement(By.cssSelector("#name-20 > .form-control")).sendKeys("Yellow bibs");
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
  }

  @Test
  @Order(80)
  public void t080CreateMenuItemsandMenu() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1921, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Fruit Juice");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("One of Orange Juice, Apple Juice or Pineapple Juice");
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("None");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.75");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.id("validationMessage")).click();
    assertThat(driver.findElement(By.id("validationMessage")).getText(), is("Successfully recorded new menu item"));
    driver.findElement(By.linkText("Back")).click();
    driver.findElement(By.cssSelector(".mx-1")).click();
    driver.findElement(By.linkText("Sign out")).click();
    driver.close();
  }  
  
  @Test
  @Order(81)
  public void t081CreateMoreDrinks() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1921, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Milk");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Glass of semi skimmed milk");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains dairy products");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.50");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Water");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Tap water");
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("None");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.00");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Water - bottled");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Bottle of still mineral water ");
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("None");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.25");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Squash");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains artificial sweeteners such as aspartame");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.25");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Glass of orange, lemon, or blackcurrant squash");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
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
  public void t082CreateMoreBreakfastItems() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1921, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Cornflakes");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Kellogs Cornflakes");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains gluten");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£1.10");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Weetabix");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).sendKeys("2 Weetabix biscuits with milk");
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains gluten and dairy");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£1.10");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success")).click();
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
    driver.findElement(By.id("description-6")).click();
    driver.findElement(By.cssSelector("#description-6 > .form-control")).click();
    driver.findElement(By.cssSelector("#description-6 > .form-control")).sendKeys("Kellogs Cornflakes with  milk");
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Coco Pops");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Kellogs Coco Pops");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains gluten. May contain traces of nuts. ");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£1.10");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success")).click();
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
    driver.findElement(By.cssSelector("#allergyInformation-7 > .form-control")).sendKeys("Contains gluten and dairy. May contain traces of nuts. ");
    driver.findElement(By.id("allergyInformation-6")).click();
    driver.findElement(By.cssSelector("#allergyInformation-6 > .form-control")).click();
    driver.findElement(By.cssSelector("#allergyInformation-6 > .form-control")).sendKeys("Contains gluten. May contain traces of nuts. ");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Porridge");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains Oats");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Quakers porridge");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.95");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Apple");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Granny Smith apple");
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("None");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.60");
    driver.findElement(By.cssSelector(".col-sm-1 > .btn-success > .fa")).click();
    driver.findElement(By.name("menuOptionName")).click();
    driver.findElement(By.name("menuOptionName")).sendKeys("Orange");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Juicy Jaffa Orange");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains Cit s 1, Cit s 2 and Cit s 3.");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.70");
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
    driver.findElement(By.name("menuOptionName")).sendKeys("Banana");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains Mus a1, Mus a2");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Medium sized banana");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£0.65");
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
    driver.findElement(By.name("menuOptionName")).sendKeys("Grapes");
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Small tub of mixed black and green grapes");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("Contains several allergenic proteins including lipid transfer protein(LPD)");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£1.00");
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
    driver.findElement(By.name("menuOptionName")).sendKeys("Fruit Salad");
    driver.findElement(By.name("menuOptionDescription")).click();
    driver.findElement(By.name("menuOptionDescription")).sendKeys("Mixed salad bow including Strawberries, blueberries, grapes and kiwi");
    driver.findElement(By.name("menuOptionAllergyInformation")).click();
    driver.findElement(By.name("menuOptionAllergyInformation")).sendKeys("None");
    driver.findElement(By.id("menuOptionAdditionalCostInput")).click();
    driver.findElement(By.id("menuOptionAdditionalCostInput")).sendKeys("£1.25");
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
  public void t083EditMenuItemFruitJuice() {
    driver.get("http://afterschool-club.com/");
    driver.manage().window().setSize(new Dimension(1921, 1281));
    driver.findElement(By.id("inputEmail")).click();
    driver.findElement(By.id("inputPassword")).sendKeys("ManUtd01");
    driver.findElement(By.id("inputEmail")).sendKeys("admin@afterschool-club.com");
    driver.findElement(By.cssSelector(".btn")).click();
    driver.findElement(By.cssSelector("li:nth-child(7) .ms-1")).click();
    driver.findElement(By.id("menuOptionTab")).click();
    driver.findElement(By.id("name-1")).click();
    driver.findElement(By.id("additionalCost-1")).click();
    driver.findElement(By.cssSelector("#additionalCost-1 > .form-control")).sendKeys("£0.50");
    driver.findElement(By.name("menuOptionName")).click();
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
}
