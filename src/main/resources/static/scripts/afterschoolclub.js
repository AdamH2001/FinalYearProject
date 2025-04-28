// Currency format for GB
let pounds = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});






// Return the date formatted in a structure  in a format that can be used to set control value or min or max.

function formatDate(date) {
    var d = new Date(date); 
    var month = '' + (d.getMonth() + 1);
    var day = '' + d.getDate();
	var year = d.getFullYear();

    if (month.length < 2) 
        month = '0' + month;
    if (day.length < 2) 
        day = '0' + day;

    return [year, month, day].join('-');
}

// Return the time formatted in a structure that can be used to set control value or min or max.

function formatTime(date) {
    var d = new Date(date);
	var hours = '' + d.getHours();
	var mins = '' + d.getMinutes();
        

    if (hours.length < 2) 
        hours = '0' + hours;
    if (mins.length < 2) 
        mins = '0' + mins;

    return [hours, mins].join(':');
}


// Resize the inernals so everything stays and tidy 

  function marginHeader() {
	hasError =  $("#errorcontainer").length > 0;
	
	if (document.body.scrollHeight > document.body.clientHeight)
		offset = 30;
	else	
		offset = 15; 

  $("#main").width(window.innerWidth  - $("#leftnav").outerWidth() - offset);

  mainTop =  $("#header").outerHeight();  
  if (hasError) {
	mainTop += 10;
	$("#errorcontainer").width(window.innerWidth  - $("#leftnav").outerWidth() - offset);	
	$("#errorcontainer").css({left: $("#leftnav").outerWidth()});
  }
	
  $("#leftnav").height(window.innerHeight - $("#header").outerHeight() - $("#footer").outerHeight() +1);	

    $("#main").css({top: mainTop , left: $("#leftnav").outerWidth()});
  $("#leftnav").css({top: $("#header").outerHeight()});	
  $(".main").show();

   newPaddingHeight =  $("#footer").outerHeight() - $(".spacer").outerHeight() + parseFloat($(".spacer").css("padding-bottom").replace("px", ""))
  $(".spacer").css("padding-bottom",  newPaddingHeight);
  if ( $("#calendarTable")> 0)  
  	$("#calendarTable").height(window.innerHeight -   $("#calendarTable").offset().top - $("#footer").outerHeight()- 15);	  
  	  

  }
  
  // When window resizes resize internals 

  window.addEventListener('resize', function(event) {
    marginHeader()
  }, true);

  $(window).on('load', function(){
     marginHeader();
  });
  
  
// Function to see if loggedOn
  
  function isLoggedOn() {
	return $("#loggedOnUser")[0].innerText.length>0;

  }
  
  
  // Reload all images with a given url
  async function reloadImg(url) {
  	  await fetch(url, { cache: 'reload', mode: 'no-cors' })
  	  document.body.querySelectorAll(`img[src='${url}']`)
  	    .forEach(img => img.src = url)
  	}  
  
// Function to show validation in top middle as red text
	function showValidationMessage(message)
	{
		$("#validationMessage")[0].innerText = message;
		$(".afterschoolclubflash").show()	
	}
	
// Manage the finance summary slider	
    
   function updateFinanceSummary() {
		if ($("#financeSummary")[0].checked) {
			window.location.href = "./showFinanceSummary?show=true";
		}
		else {
			window.location.href = "./showFinanceSummary?show=false";
		}
   }
   
// My own  money control  entry validation
   	function validateMoneyEntry(e) {
		switch (e.key) {
			case "0":
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			case "6":
			case "7":
			case "8":
			case "9":	
			case "ArrowLeft":
			case "ArrowRight":
			case "ArrowUp":
			case "ArrowDown":
			case "Shift":
			case "CapsLock":
			case "Home":
			case "End":
			case "Delete":
			case "Tab":	
			case "Backspace":
				break;
			case ".":
				if (e.target.value.indexOf(".") >=0  && e.target.value.substring(e.target.selectionStart, e.target.selectionEnd).indexOf(".") < 0)	{
					e.preventDefault();
				}
				break;
			case "£":
				if (e.target.value.indexOf("£") >=0 && e.target.value.substring(e.target.selectionStart, e.target.selectionEnd).indexOf("£") < 0) {
					e.preventDefault();
				}
				break;			
			default:	
				e.preventDefault();
				break;

								
		}

     }

	 // My own  money control  entry validation
	 function validateMoneyChange(e) {		
	     value=e.target.value.replace("£", "").trim();
	     if(isNaN(value)){
	         e.target.value = ''
	     }else{		
	   	$("#" + e.target.getAttribute("for")).val(Math.round(value * 100))
	       e.target.value = pounds.format(value);
	     }
	   }
	   
	   	 