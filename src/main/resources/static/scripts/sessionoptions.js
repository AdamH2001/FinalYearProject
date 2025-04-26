
function refreshBookingSummary() {
	if (isLoggedOn()) {
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
				var attendingControl = $("#" + this.id.substring(0, this.id.indexOf("-mg")) + "-Attending")[0];			
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
						var totalHidden = $("#"+this.id.substring(0, this.id.indexOf("-mg")) + "-TotalHiddenCost")[0];				
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
			

		$("input[name='recurringSession']").each(function() {
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
	
	if (isLoggedOn()) {
		// ensure correct options and choices are being displayed depending on the selected tab
		var activeTab = $("a.studentTab.active");	
		$("div.studentContainer").hide();
		$("#"+activeTab[0].id+"-Container").show();
	}
	return;
}

function activateStudent(elementId) {
	// Update which tab is active
	$("a.studentTab.active")[0].classList.remove("active");
	$("#"+elementId)[0].classList.add("active");
	initialiseStudentContainers()	
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
	
	if ($("#startDate").length > 0) { // is recurring session


		// ensure if have specified a recurring end date that have also selected a recurring pattern
		
		start = new Date($("#startDate").val());	
		bookingEndDate = new Date($("#bookingEndDate").val());
		if (+start != +bookingEndDate) {
			if (!($("#MonRecurring")[0].checked || $("#TueRecurring")[0].checked || $("#WedRecurring")[0].checked || $("#ThurRecurring")[0].checked || $("#FriRecurring")[0].checked || $("#SatRecurring")[0].checked || $("#SunRecurring")[0].checked)) {
				$("#validationMessage").text("Need to specify a recurring pattern.");
				$("#validationContainer").show();
				event.preventDefault();		
				
			}
		}		
	}
	return;
}
