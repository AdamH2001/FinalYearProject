
// Validate session form ensuring have enought staff for attendees
// Also ensure dates make sense

function validateSessionForm(event) {
	
	var maxMaxAttendees = $("#staff").val().length * 10;
	var maxAttendees = 	$("#maxAttendees").val();
	
	// ensure have enough staff for attendees 
	
	if (maxAttendees > maxMaxAttendees) {
		$("#validationMessage").text("Need at least one member of staff per 10 students.");
		$("#validationContainer").show();
		window.scrollTo(0, 0);		
		event.preventDefault();		
	}
	
	// ensure if have specified a recurring end date that have also selected a recurring pattern
	
	start = new Date($("#startDate").val());	
	recurringEndDate = new Date($("#recurringEndDate").val());
	if (+start != +recurringEndDate) {
		if (!($("#MonRecurring")[0].checked || $("#TueRecurring")[0].checked || $("#WedRecurring")[0].checked || $("#ThurRecurring")[0].checked || $("#FriRecurring")[0].checked || $("#SatRecurring")[0].checked || $("#SunRecurring")[0].checked)) {
			$("#validationMessage").text("Need to specify a recurring pattern.");
			$("#validationContainer").show();
			window.scrollTo(0, 0);
			event.preventDefault();		
			

		}
	}		
	return;
}


// UPdate style when click on Abset and Present reigrstation buttons

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


// Set Date and time limits for session
function setDateLimits(){
	
	if ($("#viewing").length==0) {
		var shortestPeriod = 30;
		  
		var start = new Date($("#startDate").val());
		var now = new Date();
		var isToday = +start == +now;
		
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
  
// Funtions to manage the adding of an equipment row  
  function addRow(button) {
  			
  	var index = Number(button.id.replace("add", ""));
  	
  	$("#item" + (index + 1)).show();
  	$("#item" + index +"buttons").hide();
  	
  	
  	return;	 	
  }

  // Funtions to manage the removing of an equipment row
  function removeRow(button) {
  			
  	var index = Number(button.id.replace("remove", ""));

  	$("#item" + (index)).hide();
  	$( "#item" + (index-1) +"buttons").show();

  	$('#equipment' + index).val(0);
  	$('#equipmentQuantity' + index).val(0);
  	$('#perAttendee' + index).prop('checked' , false);
  	
  	return;	 	
  }
  
  
// copt per attendee checkbox to hidden field
  function copyPerAttendee() {
  	$('input[name="hiddenPerAttendee').each(function(i, obj) {
  			obj.value = $('input[name="perAttendee')[i].checked;
  		});
  }
  
