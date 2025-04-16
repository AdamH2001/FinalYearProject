
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

  window.addEventListener('resize', function(event) {
    marginHeader()
  }, true);

  $(window).on('load', function(){
     marginHeader();
  });
  
  
  function shouldFilter(row, words) {
  	filter = true;
  	index = 0;
  	while (index < words.length && filter) {
  		value = words[index]
  		index++
  		filter = $(row).text().toLowerCase().indexOf(value) > -1;
  	}
  	return filter;
  }
  
  function isLoggedOn() {
	return $("#loggedOnUser")[0].innerText.length>0;

  }
  
  async function reloadImg(url) {
  	  await fetch(url, { cache: 'reload', mode: 'no-cors' })
  	  document.body.querySelectorAll(`img[src='${url}']`)
  	    .forEach(img => img.src = url)
  	}  
  
	function showValidationMessage(message)
	{
		$("#validationMessage")[0].innerText = message;
		$(".afterschoolclubflash").show()	
	}
    
   function updateFinanceSummary() {
		if ($("#financeSummary")[0].checked) {
			window.location.href = "./showFinanceSummary?show=true";
		}
		else {
			window.location.href = "./showFinanceSummary?show=false";
		}
   }