
let pounds = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});


function refreshBookingSummary() {
	var hiddenTotals =  $("input.hiddenTotal")
	var viewOnly = $("#viewOnly").length>0;
	
	var proposedAttendees = 0;
	var editingOptions = false;	
	var totalOriginalCost = 0;
	
	//Calculate total original cost and update display values for each student. 
	$("input.hiddenOriginalTotal").each(function() {
		editingOptions = true; 
		$("#" + this.id.replace("-TotalHiddenOriginalCost", "-TotalOriginalCost")).text(pounds.format(Number(this.value)/100));
		totalOriginalCost += Number(this.value);});
	
	// Update display for each proposed attendee 		
	hiddenTotals.each(function() {			
			var bProposedAttendee = $("#"+this.id.replace("-TotalHiddenCost",  "-Attending"))[0].checked;
					
			if (bProposedAttendee) {
				var tab = $("#"+this.id.replace("-TotalHiddenCost",  ""))[0];
				if (tab.classList.contains("disabled")) {
					bProposedAttendee = false;
				}	
			}
			if (bProposedAttendee) {						
				proposedAttendees += 1;		
				this.value = $("#basePrice")[0].value;
				$("#"+this.id.replace("-TotalHiddenCost",  "-Container")).css("color", "black");
			}
			else {
				this.value = 0;
				$("#"+this.id.replace("-TotalHiddenCost",  "-Container")).css("color", "lightgray");
			}		
		});
	$("div.childTotal").css("color", "black");
	
	// If no new proposed attendees then disable submit button	
	$("#submitButton").attr("disabled", proposedAttendees == 0)
	
	// Calculate cost for all options chosen
	$("input.menuOption").each(function() {						
			var attendingControl = $("#" + this.id.substring(0, this.id.indexOf("-Option")) + "-Attending")[0];			
			if (attendingControl.checked) {
				if (viewOnly) {
					this.disabled=true;	
					this.readOnly=true;
				}
				else
				{
					this.disabled=false;	
					this.readOnly=false;					
				}		
				if (this.checked) {					
					var totalHidden = $("#"+this.id.substring(0, this.id.indexOf("-Option")) + "-TotalHiddenCost")[0];				
					var optionValue = $("#"+this.id + "-Value")[0].value;						
					totalHidden.value = Number(totalHidden.value) + Number(optionValue);					
				}
			}
			else {
				this.disabled=true;
				this.readOnly=true;				
			}				
		});

	// Update cost being displayed for each attendee to reflect updated options cost
	var overAllTotal = 0;
	hiddenTotals.each(function() {
		$("#" + this.id.replace("-TotalHiddenCost", "-TotalCost")).text(pounds.format(Number(this.value)/100));
		overAllTotal += Number($(this).val());});	
	
	// Update text to be more meaningful id editing and cost has increased of decreased
	if (editingOptions) {
		overAllTotal -= totalOriginalCost;
		if (overAllTotal < 0) {
			$("#totalDifferenceLabel").text("Total Reduction Of:");
			overAllTotal *= -1;			
		}
		else {
			$("#totalDifferenceLabel").text("Total Increase Of:");
		}
	}
	else {
		$("#numberSessions")[0].innerText = numberRecurringSessions();
		$("#totalCostForAllSessions")[0].innerText = pounds.format(overAllTotal * numberRecurringSessions() /100);		
	}
	
	
	// Update overall total cost 	
	$("#totalCost")[0].innerText = pounds.format(overAllTotal/100);
	$("#totalHiddenCost")[0].value = overAllTotal;
	
	
	
	
	if (($("#startDate").val() == $("#bookingEndDate").val())) {
		$(".recurSpecifier").prop("disabled", true);
		$(".recurSpecifier").css("color", "grey");
	//	$(".recurSpecifier").prop("checked", false);
	}
	else {
		$(".recurSpecifier").prop("disabled", false);
		$(".recurSpecifier").css("color", "black");
	}
		
  return;
}


function numberRecurringSessions()
{
	var numBookings = 0;
	if (($("#startDate").val() == $("#bookingEndDate").val())) {
			numBookings = 1;
	}
	else {
		startDate = new Date($("#startDate").val());
		endDate = new Date($("#bookingEndDate").val());
			

		$("input[name='recurringEvent']").each(function() {
			thisDate = new Date(this.value);
			if (thisDate >= startDate && thisDate <= endDate) {
					
				switch (thisDate.getDay()) {
					case 0: if ($("#SunRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					case 1: if ($("#MonRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					case 2: if ($("#TueRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					case 3: if ($("#WedRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					case 4: if ($("#ThurRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					case 5: if ($("#FriRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					case 6: if ($("#SatRecurring")[0].checked) {
									numBookings++;		
							}
							break;
					default: 
							break;															
				}
			}
		})
	}
	return numBookings;
}


function changedAttendance(element) {
	refreshBookingSummary();
	
	// actvate the tab if changed to attending
	if (element.checked) {
		activateStudent(element.id.replace("-Attending", ""));
	}
	return true;	
}


function initialiseStudentContainers() {
	// ensure correct options and choices are being displayed depending on the selected tab
	var activeTab = $("a.studentTab.active");	
	$("div.studentContainer").hide();
	$("#"+activeTab[0].id+"-Container").show();
	return;
}

function activateStudent(elementId) {
	// Update which tab is active
	$("a.studentTab.active")[0].classList.remove("active");
	$("#"+elementId)[0].classList.add("active");
	initialiseStudentContainers()	
	return;
}


function copyPerAttendee() {
	$('input[name="hiddenPerAttendee').each(function(i, obj) {
			obj.value = $('input[name="perAttendee')[i].checked;
		});
}


/*
$(function () {
    $(".dropdown li").on('mouseenter mouseleave', function (e) {
        if ($('.dropdown-menu', this).length) {
            var elm = $('.dropdown-menu', this);
            var off = elm.offset();
            var l = off.left;
            var w = elm.width();
            var docW = $(window).width();

            var isEntirelyVisible = (l + w <= docW);

            if (!isEntirelyVisible) {
                $(elm).addClass('dropstart');
            } else {
                $(elm).removeClass('dropStart');
            }
        }
    });
});



$(".mm.dropdown-item").on("click", function() {
  	$("dropdown-menu").hide();
});

$(".mm.dropdown-item.dropdown-toggle").on("click", function() {
  	return true;
});	

*/

function updateRegisterButtons(button) {	
	if (button.classList.contains("RegisterPresentButton")) {
		button.classList.remove("Absent");		
		button.classList.add("Present");
		
		
		
		$("#" + button.id.replace("PresentButton", "attendee")).val("Present");

		
		$("#" + button.id.replace("PresentButton", "AbsentButton"))[0].classList.remove("Absent");
		$("#" + button.id.replace("PresentButton", "AbsentButton"))[0].classList.add("Present");		
			
	}
	else {
		button.classList.remove("Present");
		button.classList.add("Absent");
		
		$("#" + button.id.replace("AbsentButton", "attendee")).val("Absent");
		$("#" + button.id.replace("AbsentButton", "PresentButton"))[0].classList.remove("Present");
		$("#" + button.id.replace("AbsentButton", "PresentButton"))[0].classList.add("Absent");																	
	}	
	return false;
}

function addRow(button) {
			
	var index = Number(button.id.replace("add", ""));
	
	$("#item" + (index + 1)).show();
	$("#item" + index +"buttons").hide();
	
	
	return;	 	
}


function removeRow(button) {
			
	var index = Number(button.id.replace("remove", ""));

	$("#item" + (index)).hide();
	$( "#item" + (index-1) +"buttons").show();

	$('#equipment' + index).val(0);
	$('#equipmentQuantity' + index).val(0);
	$('#perAttendee' + index).prop('checked' , false);
	
	return;	 	
}


function activateStudentOld() {
	var studentTabs =  document.getElementsByClassName("studentTab");
	
	
	for (i = 0; i<studentTabs.length; i++) {
		var tab = studentTabs[i];
		if (tab.id == elementId) {
			tab.classList.add("active");
		}
		else
		{
			tab.classList.remove("active");
		}
		
	}
	
	var studentContainers =  document.getElementsByClassName("studentContainer");

	var containerId = elementId + "-Container";
	for (i = 0; i<studentContainers.length; i++) {
		var container = studentContainers[i];
		if (container.id == containerId) {
			container.style.display="block"
		}
		else
		{
			container.style.display="none"
		}
		
	}	
	

  	return;
}



function initialiseStudentContainersOld() {
	var studentTabs =  document.getElementsByClassName("studentTab");
	var elementId = "";

	for (i = 0; i<studentTabs.length; i++) {
		var tab = studentTabs[i];
		if (tab.classList.contains("active"))
			elementId = tab.id;		
	}
	

	var studentContainers =  document.getElementsByClassName("studentContainer");

	var containerId = elementId + "-Container";
	for (i = 0; i<studentContainers.length; i++) {
		var container = studentContainers[i];
		if (container.id == containerId) {
			container.style.display="block"
		}
		else
		{
			container.style.display="none"
		}
		
	}	


	return;
}


function validateBookingForm(event) {
	

	// ensure if have specified a recurring end date that have also selected a recurring pattern
	
	start = new Date($("#startDate").val());	
	bookingEndDate = new Date($("#bookingEndDate").val());
	if (start != bookingEndDate) {
		if (!($("#MonRecurring")[0].checked || $("#TueRecurring")[0].checked || $("#WedRecurring")[0].checked || $("#ThurRecurring")[0].checked || $("#FriRecurring")[0].checked || $("#SatRecurring")[0].checked || $("#SunRecurring")[0].checked)) {
			$("#validationMessage").text("Need to specify a recurring pattern.");
			$("#validationContainer").show();
			event.preventDefault();		
			
		}
	}		
	return;
}



function validateEventForm(event) {
	
	var maxMaxAttendees = $("#staff").val().length * 8;
	var maxAttendees = 	$("#maxAttendees").val();
	
	// ensure have enough staff for attendees 
	
	if (maxAttendees > maxMaxAttendees) {
		$("#validationMessage").text("Need at least one member of staff per 8 students.");
		$("#validationContainer").show();
		event.preventDefault();		
	}
	
	// ensure if have specified a recurring end date that have also selected a recurring pattern
	
	start = new Date($("#startDate").val());	
	recurringEndDate = new Date($("#recurringEndDate").val());
	if (start != recurringEndDate) {
		if (!($("#MonRecurring")[0].checked || $("#TueRecurring")[0].checked || $("#WedRecurring")[0].checked || $("#ThurRecurring")[0].checked || $("#FriRecurring")[0].checked || $("#SatRecurring")[0].checked || $("#SunRecurring")[0].checked)) {
			$("#validationMessage").text("Need to specify a recurring pattern.");
			$("#validationContainer").show();
			event.preventDefault();		
			
		}
	}		
	return;
}


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

function setDateLimits(){
	
	if ($("#viewing").length==0) {
		var shortestPeriod = 30;
		  
		var start = new Date($("#startDate").val());
		var now = new Date()
		var isToday = start.getDate() == now.getDate() && start.getMonth()== now.getMonth() && start.getYear()==now.getYear();
		
		var earliestTime = new Date()
		earliestTime.setHours(7);
		earliestTime.setMinutes(0);
	
		var lastStartTime = new Date();
		lastStartTime.setHours(18);
		lastStartTime.setMinutes(30);
		
		var lastEndTime = new Date(lastStartTime).setMinutes(lastStartTime.getMinutes()+shortestPeriod);
		
		
		//set minimum start time 
		if (isToday) {
			if (now < earliestTime) {
				$("#startTime").attr('min', formatTime(earliestTime));
			}
			else {
				$("#startTime").attr('min', formatTime(now));
			}
		}
		else{
			$("#startTime").attr('min', formatTime(earliestTime));
		}
		
		//set max end and start time 
		$("#startTime").attr('max', formatTime(lastStartTime));
		$("#endTime").attr('max', formatTime(lastEndTime));			   
	
		//set min time to ensure following shortest period
		start.setHours(parseInt($("#startTime").val().substring(0,2)));
		start.setMinutes(parseInt($("#startTime").val().substring(3,5))+shortestPeriod);	
		$("#endTime").attr('min', formatTime(start) );		
		
		// set min date
		if (now > lastStartTime) {
			$("#startDate").attr('min', formatDate(now.setDate(now.getDate()+1)));
		}	
		else {
			$("#startDate").attr('min', formatDate(now));
		}   
		
		$("#recurringEndDate").attr('min', $("#startDate").val());
		
		// Set maximum year to end of academic year
		year = start.getYear()+1900;
		if (start.getMonth() >=8) {
			year++;
		}
		$("#recurringEndDate").attr('max', year+'-08-31');
		
		
		if ($("#recurringEndDate").val() == "") {
			$("#recurringEndDate").val($("#startDate").val());
		}
		else {
			start = new Date($("#startDate").val());
			recurEnd = new Date($("#recurringEndDate").val()); 
		
			if (recurEnd < start) {
				$("#recurringEndDate").val($("#startDate").val());
			}
		}
		if (($("#startDate").val() == $("#recurringEndDate").val())) {
			$(".recurSpecifier").prop("disabled", true);
			$(".recurSpecifier").css("color", "grey");
			$(".recurSpecifier").prop("checked", false);
		}
		else {
			$(".recurSpecifier").prop("disabled", false);
			$(".recurSpecifier").css("color", "black");
		}
	}
	
		
  };



  function marginHeader() {

	if (document.body.scrollHeight > document.body.clientHeight)
		offset = 45;
	else	
		offset = 30; 

  $("#main").width(window.innerWidth  - $("#leftnav").outerWidth() - offset);	
  //$("#main").width(window.innerWidth -   $("#main").offset().left - 30);	  

  
  $("#leftnav").height(window.innerHeight - $("#header").outerHeight() - $("#footer").outerHeight() );	

  $("#main").css({top: $("#header").outerHeight(), left: $("#leftnav").outerWidth()});
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