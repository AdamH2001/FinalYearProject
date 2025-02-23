/**
 * 
 */

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
	
	// Update overall total cost 	
	$("#totalCost")[0].innerText = pounds.format(overAllTotal/100);
	$("#totalHiddenCost")[0].value = overAllTotal;
	
  return;
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

*/
	

function activateStudentOld(elementId) {
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



function refreshBookingSummaryOld() {
	
	var hiddenTotals =  document.getElementsByClassName("hiddenTotal");
	var hiddenOriginalTotals =  document.getElementsByClassName("hiddenOriginalTotal");
	var viewOnly = document.getElementById("viewOnly") != null;
	var proposedAttendees = 0;
	var editingOptions = hiddenOriginalTotals.length > 0;
	var totalOriginalCost = 0;
	
	for (i = 0; i<hiddenOriginalTotals.length; i++) {
		var total = hiddenOriginalTotals[i];
				
		var originalCostTextId = total.id.replace("-TotalHiddenOriginalCost",  "-TotalOriginalCost");
		var textContainer = document.getElementById(originalCostTextId);		
		if (textContainer != null) {
			textContainer.innerText = pounds.format(Number(total.value)/100);
		}		
		totalOriginalCost += Number(total.value)			
	}	
	
	for (i = 0; i<hiddenTotals.length; i++) {
		var total = hiddenTotals[i];
		var attendingId = total.id.replace("-TotalHiddenCost",  "-Attending");
		var containerId = total.id.replace("-TotalHiddenCost",  "-Container");
		var container = document.getElementById(containerId);

		
		var attendingControl = document.getElementById(attendingId);
		
		var bProposedAttendee = attendingControl.checked;
		if (bProposedAttendee) {
			var tabId = total.id.replace("-TotalHiddenCost",  "");
			var tab = document.getElementById(tabId);

			if (tab.classList.contains("disabled")) {
				bProposedAttendee = false;
			}	

		}
		if (bProposedAttendee) {						
			proposedAttendees += 1;
		
			total.value = document.getElementById("basePrice").value;
			if (container != null) {
				container.style.color="black";
			}		
		}
		else {
			total.value = 0;
			if (container != null) {			
				container.style.color="lightgray";
			}
		}		
	}
	
	if (document.getElementById("submitButton") != null) {	
		if (proposedAttendees == 0) {
			document.getElementById("submitButton").disabled=true;
		}
		else {
			document.getElementById("submitButton").disabled=false;
		}
	}
	
	var childTotals =  document.getElementsByClassName("childTotal");

	for (i = 0; i<childTotals.length; i++) {
		childTotals[i].style.color="black"
	}
		
	var menuOptions =  document.getElementsByClassName("menuOption");

	
	for (i = 0; i<menuOptions.length; i++) {
		var option = menuOptions[i];
		var attendingId = option.id.substring(0, option.id.indexOf("-Option")) + "-Attending";
		var attendingControl = document.getElementById(attendingId);
		
		if (attendingControl.checked) {
			if (viewOnly) {
				option.disabled=true;	
				option.readOnly=true;
			}
			else
			{
				option.disabled=false;	
				option.readOnly=false;
				
			}		
			if (option.checked) {
				var hiddenId = option.id.substring(0, option.id.indexOf("-Option")) + "-TotalHiddenCost";
				var totalHidden = document.getElementById(hiddenId);				
				var optionValue = document.getElementById(option.id + "-Value").value;						
				totalHidden.value = Number(totalHidden.value) + Number(optionValue);
				
			}
		}
		else {
			option.disabled=true;
			option.readOnly=true;
			//option.checked=false;
		}
			
	}

	var overAllTotal = 0;
	for (i = 0; i<hiddenTotals.length; i++) {
		var total = hiddenTotals[i];
		var textId = total.id.replace("-TotalHiddenCost", "-TotalCost");		
		var textContainer = document.getElementById(textId);
		
		if (textContainer != null) {
			textContainer.innerText = pounds.format(Number(total.value)/100);
		}
		overAllTotal += Number(total.value);
		
	}
	
	if (editingOptions) {
		overAllTotal -= totalOriginalCost;
		if (overAllTotal < 0) {
			document.getElementById("totalDifferenceLabel").innerText = "Total Reduction Of:";
			overAllTotal *= -1;			
		}
		else {
			document.getElementById("totalDifferenceLabel").innerText = "Total Increase Of:";
		}
	}
		
	document.getElementById("totalCost").innerText = pounds.format(overAllTotal/100);
	document.getElementById("totalHiddenCost").value = overAllTotal;
	
  return;
}
