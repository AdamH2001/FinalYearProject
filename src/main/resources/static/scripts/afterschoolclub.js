/**
 * 
 */

let pounds = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
});


function refreshBookingSummary() {
	
	var hiddenTotals =  document.getElementsByClassName("hiddenTotal");
	var proposedAttendees = 0;

	for (i = 0; i<hiddenTotals.length; i++) {
		var total = hiddenTotals[i];
		var attendingId = total.id.replace("-TotalHiddenCost",  "-Attending");
		var containerId = total.id.replace("-TotalHiddenCost",  "-Container");
		var container = document.getElementById(containerId);

		
		var attendingControl = document.getElementById(attendingId);
		
		if (attendingControl.checked) {
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
	
	if (proposedAttendees == 0) {
		document.getElementById("submitButton").disabled=true;
	}
	else {
		document.getElementById("submitButton").disabled=false;
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
			option.disabled=false;	
			option.readOnly=false;		
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
	
	document.getElementById("totalCost").innerText = pounds.format(overAllTotal/100);
	document.getElementById("totalHiddenCost").value = overAllTotal;
	
  return;
}

function changedAttendance(element) {
	refreshBookingSummary();
	if (element.checked) {
		activateStudent(element.id.replace("-Attending", ""));
	}
	return true;	
}
	

function activateStudent(elementId) {
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



function initialiseStudentContainers() {
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