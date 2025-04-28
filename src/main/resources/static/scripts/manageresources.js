// Return data from row into a JSON data object

function getDataFromRow(id) {
	var data = {};
	$("#row-" + id).find(".editable").each(function() {
		fieldName=this.id.substring(0, this.id.indexOf("-"));
		
		
		data[fieldName] = this.innerText;			
	});
	data["state"] = "ACTIVE";
	var isLocation = $("#locationList").find("#row-"+id).length > 0;
	if (isLocation) {
		data["quantity"] = "1";
		data["type"] = "LOCATION";
		data["resourceId"] = id;				
		data["url"] = "./api/resources/" + id;
	}
	else {
		var isEquipment = $("#equipmentList").find("#row-"+id).length > 0;
		if (isEquipment) {
			data["type"] = "EQUIPMENT";
			data["resourceId"] = id;				
			data["url"] = "./api/resources/" + id;
		}
		else {			
			data["type"] = "STAFF";
			data["userId"] = id.substring(1);				
			data["url"] = "./api/staff/" + id.substring(1);
		}
	}	
	return data;
}

// Get original data from row into JSON data object

function getOrigDataFromRow(id) {
	var data = {};
	$("#row-" + id).find(".editable").each(function() {
		fieldName=this.id.substring(0, this.id.indexOf("-"));		
		data[fieldName] = this.getAttribute("orig-data");			
	});
	data["state"] = "ACTIVE";
	var isLocation = $("#locationList").find("#row-"+id).length > 0;
	if (isLocation) {
		data["quantity"] = "1";
		data["type"] = "LOCATION";
		data["resourceId"] = id;				
		data["url"] = "./api/resources/" + id;
	}
	else {
		var isEquipment = $("#equipmentList").find("#row-"+id).length > 0;
		if (isEquipment) {
			data["type"] = "EQUIPMENT";
			data["resourceId"] = id;				
			data["url"] = "./api/resources/" + id;
		}
		else {			
			data["type"] = "STAFF";
			data["userId"] = id.substring(1);				
			data["url"] = "./api/staff/" + id.substring(1);
		}
	}	
	return data;
}

// Set the orginal data 
function setOrigDataFromRow(id) {
	$("#row-" + id).find(".editable").each(function() {
		if (this.getAttribute("editType") != "file") {
			this.setAttribute("orig-data", this.innerText);
		}
		else {
			id = id.replace("u", "");			
			this.setAttribute("orig-data", $("#profilePicImage-"+id)[0].getAttribute("src"));
		}		
	});		
}

// Validate input and color cells appropriately
function validateInput(data) {	
	if (data.resourceId!= null) {
		validateRow("#row-", data.resourceId);
	}
	else {
		validateRow("#row-u",data.userId);
	}		
}

// Undo and changes and PUT orig data back on server
function undoChanges(button) {
	resourceId=button.id.replace("undo-", "");


	var data = getOrigDataFromRow(resourceId);
			
	$.ajax({
		url: data.url,
		method:"PUT",
	    dataType : "json",
	    contentType: "application/json; charset=utf-8",
		data: JSON.stringify(data), 
		success: function(updatedData,  textStatus, jqXHR){
			console.log("PUT");
			console.log(updatedData);
			showValidationMessage("Successfully undone changes to " + data.type.toLowerCase());
			
			
			$("#row-" + resourceId).find(".editable").each(function() {
				if (this.getAttribute("editType") != "file") {
					this.innerHTML = this.getAttribute("orig-data");
				}
				else {
					id = resourceId.replace("u","");
					$("#profilePicImage-"+id)[0].setAttribute("src", this.getAttribute("orig-data"));						
				}
			});
			validateInput(data);								
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("PUT ERROR");
			console.log(jqXHR);			
			validateInput(data);
			showValidationMessage("Failed to update already exists with same name");
		}
	});
		
}

// Delete resource item from server
function deleteResourceItem(data, type) {
	$.ajax({
		url:"./api/resources/" +id,
		method:"DELETE",
		success: function(data,  textStatus, jqXHR){
			console.log("DELETE");
			console.log(data);
			id = this.url.substr(this.url.lastIndexOf("/")+1);
			$("#row-"+id)[0].outerHTML="";	
			message = type.toUpperCase().substring(0,1) + type.substring(1) + " deleted."; 						
			showValidationMessage(message);						
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("DELETE ERROR");
			console.log(jqXHR);						 					
			showValidationMessage("Failed to delete " + type);						
		}
	});	
}

// Delete resource item from server
function deleteResource(button, type) {	
	id=button.id.replace("delete-", "");

	$.ajax({
		url:"./api/resources/" + id,
		method:"GET", 
		success: function(data){
			console.log("GET");
			console.log(data);
			if (data.maxDemand > 0) {
				message = type.toUpperCase().substring(0,1) + type.substring(1) + " required to support already scheduled sessions. \n Do you really want to delete?"; 				
				$('#confirmMessage')[0].innerText = message;
				$('.confirmYes').off('click').on('click', function(e) {
						e.preventDefault();
		
						deleteResourceItem(data, type)					
						$('#confirmModal').hide()											
					})
				$('.confirmNo').off('click').on('click',  function(e) {
						e.preventDefault();					
						$('#confirmModal').hide()							
					})						
				$('#confirmModal').show()					
			}
			else {
				deleteResourceItem(data, type)									
			}

		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
	return;		
}

// Delete staff from server
function deleteStaff(button) {	
	id=button.id.replace("delete-", "");

	$.ajax({
		url:"./api/staff/" + id,
		method:"GET", 
		success: function(data){
			console.log("GET");
			console.log(data);
			if (data.maxDemand > 0) {				
				
				$('#confirmMessage')[0].innerText = "Staff member required to support already scheduled sessions. \n Do you really want to delete?";

				$('.confirmYes').off('click').on('click', function(e) {
						e.preventDefault();
						$.ajax({
							url:"./api/staff/" +id,
							method:"DELETE",
							success: function(data,  textStatus, jqXHR){
								console.log("DELETE");
								console.log(data);
								id = this.url.substr(this.url.lastIndexOf("/")+1);
								$("#row-u"+id)[0].outerHTML="";
								showValidationMessage("Staff member Deleted");
							},
							error: function(jqXHR, textStatus, errorThrown) {
								console.log("DELETE ERROR");
								console.log(jqXHR);
								showValidationMessage("Failed to delete staff member");

							}
						});						
						$('#confirmModal').hide()											
					})
				$('.confirmNo').off('click').on('click',  function(e) {
						e.preventDefault();					
						$('#confirmModal').hide()							
					})						
				$('#confirmModal').show()					
								
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
	return;		
}


// Add new equipment and post to server
function addNewEquipment(event) {
	event.preventDefault();		
	
	data = {
			"description": $("input[name='equipmentDescription']").val(),
			"keywords": $("input[name='equipmentKeywords']").val(),
			"name": $("input[name='equipmentName']").val(),
			"quantity": $("input[name='equipmentQuantity']").val(),
			"type": "EQUIPMENT",
		}		
	postDataAndDisplay(data, "equipment");
	
}

// Add new location and post to server
function addNewLocation(event) {
	event.preventDefault();		
	
	data = {
			"description": $("input[name='locationDescription']").val(),
			"keywords": $("input[name='locationKeywords']").val(),
			"name": $("input[name='locationName']").val(),
			"capacity": $("input[name='locationCapacity']").val(),
			"quantity": "1",
			"type": "LOCATION",
		}		
	postDataAndDisplay(data, "location");
	
}

// copy value from data to html 
function copyValue(fieldName, newData) {
	if (newData.userId == null) {
		prefix = "#row-";
		id = newData.resourceId;
	}
	else {
		prefix = "#row-u";
		id = newData.userId;
	}
	$(prefix + id).find("#"+fieldName)[0].innerHTML=newData[fieldName];	
	$(prefix + id).find("#"+fieldName)[0].id=fieldName + "-" + id;	
	
}

// Add new location and post to server
function addNewStaff(event) {
	event.preventDefault();		
	
	staff = {
			"title": $("input[name='staffTitle']").val(),
			"firstName": $("input[name='staffFirstName']").val(),
			"surname": $("input[name='staffSurname']").val(),
			"email": $("input[name='staffEmail']").val(),
			"telephoneNum": $("input[name='staffTelephoneNum']").val(),
			"description": $("input[name='staffDescription']").val(),
			"keywords": $("input[name='staffKeywords']").val(),
			"state": "ACTIVE",
			"type": "STAFF",
		}		
	
	$.ajax({
		url:"./api/staff",
		method:"POST",
	    dataType : "json",
	    contentType: "application/json; charset=utf-8",
		data: JSON.stringify(staff),

		success: function(data,  textStatus, jqXHR){
			console.log("POST");
			console.log(data);
			type="staff";
			
			outerHTML = $("."+type+ "TemplateRow")[0].outerHTML;
			
			
			$("#" + type + "NewRow")[0].insertAdjacentHTML("beforeBegin", outerHTML);
			
			newRow = $("."+type+ "TemplateRow")[1];
			newRow.id ="row-u" + data.userId;
			newRow.classList.add("filterrow");
			newRow.classList.remove(type+ "TemplateRow");
			
			// Copy value from input fields to new row
			
			copyValue("title", data);
			copyValue("firstName", data);
			copyValue("surname", data);
			copyValue("email", data);
			copyValue("telephoneNum", data);
			copyValue("description", data);
			copyValue("keywords", data);
			

			
			//Resolve image
			$("#row-u"+data.userId).find("#staffProfilePicTemplate")[0].setAttribute("id", "profilePic-" + data.userId);

		    var files = $("input[id='newStaffProfilePic']")[0].files;
		    
			if (files.length==0) { 
				src = "./images/missingProfile.jpg" ;
				$("#row-u"+data.userId).find("img")[0].setAttribute("src", src);			
			}

			$("#row-u"+data.userId).find("img")[0].setAttribute("id", "profilePicImage-" + data.userId);			
			$("#row-u"+data.userId).find("label")[0].setAttribute("for","profilePic-" + data.userId);
			


			
			
			undoButton = $("#row-u"+data.userId).find("#newUndo")[0]; 
			undoButton.id ="undo-u" + data.userId;
			
			deleteButton = $("#row-u"+data.userId).find("#newDelete")[0];
			deleteButton.id = "delete-" + data.userId;
			$("#"+deleteButton.id).show();
			
				
			
			setUpEditHandlersForType('staff');
			

			
			
			
			if (files.length>0) {
				uploadFile(files[0], data.userId);				
	     		$("#profilePicImage-0")[0].setAttribute("src", "./images/missingProfile.jpg");
			}
				
			
			//Clear input fields			
			$(".staffInput").val("");
			$("#row-u"+data.userId).show();
			
			setOrigDataFromRow("u"+data.userId);		

			
			showValidationMessage("Successfully recorded staff member ");
			
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("POST ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to save new staff member");

			
		}
	});	
	

	
}


// User clicked on a cell insert edit control
function insertEditControl(parent) {
	editType =  parent.getAttribute('edittype')
	editlength =  parent.getAttribute('editlength')
	
	if (editType != "file") {
		parent.setAttribute('data-clicked', 'yes');
	    parent.setAttribute('data-text', parent.innerHTML);
	    var input = null;
    
	    if (editType == "textarea") {
	    	input = document.createElement('textarea');			
	    }
	    else {
	    	input = document.createElement('input');
	    	input.setAttribute('type',editType);
			if (editType=="number") {
				input.setAttribute('min',"0");	
			}						
	    }
		if (editlength != null && editlength != "") {
			input.setAttribute("maxlength", editlength)
		}
	    input.classList.add("form-control");
	    input.value = parent.innerHTML;
	    input.style.width = parent.offsetWidth - (parent.clientLeft * 2) - 5 + "px";
	    input.style.height = parent.offsetHeight - (parent.clientTop * 2) + "px";
	    input.style.border = "0px";
	    input.style.fontFamily = "inherit";
	    input.style.fontSize = "inherit";
	    input.style.backgroundColor = "LightGoldenRodYellow";
	
	    input.onblur = function() {
	        var td = input.parentElement;
	        var preEditText = input.parentElement.getAttribute('data-text');
	        var origText = input.parentElement.getAttribute('orig-data');
	
	        var current_text = this.value;
	        
	
	        
	        if (preEditText != current_text) {
	            //there are changes; save to geojson
	
	            td.removeAttribute('data-clicked');
	            td.removeAttribute('data-text');
	            
	            
				putDataAndDisplay(td.parentElement.id.replace("row-", ""), td)	            
	                       		            
	            console.log(preEditText + ' is changed to ' + current_text);
	        } else {
	            td.removeAttribute('data-clicked');
	            td.removeAttribute('data-text');
	            td.innerHTML = preEditText;
	            console.log('no changes');
	        }
	    }
	    input.onkeydown = function() {
	        if (event.key == "Enter") {
	            this.blur();
				event.preventDefault();		
	
	        } 
	        else if (event.key == "Escape") {
	        	console.log("Recieved Escape Key");
	            var td = input.parentElement;
	            input.value= input.parentElement.getAttribute('data-text');
	            this.blur();                
	        }
	    }
	    parent.innerHTML = '';
	    parent.append(input);
	    parent.firstElementChild.select();
    }
    return input;
}

// Set up edit handlers for rows type 
function setUpEditHandlersForType(type) {
	var cells = $("div.editable."+type);
	for (var i = 0; i < cells.length; i++) {
	    cells[i].onclick = function(){
	        if (!this.hasAttribute('data-clicked')) {
		        input = insertEditControl(this);
	        }
	    }
	}
	
}

	
// Set up all edit handlers 
function setUpEditHandlers() {
	setUpEditHandlersForType('equipment');
	setUpEditHandlersForType('location');
	setUpEditHandlersForType('staff');
	return;
}

// Update a resource with new data
function putResource(data, parentCell) {
						$.ajax({
							url: data.url,
							method:"PUT",
						    dataType : "json",
						    contentType: "application/json; charset=utf-8",
							data: JSON.stringify(data), 
							success: function(updatedData,  textStatus, jqXHR){
								if (parentCell != null) {
									parentCell.setAttribute("latest-data", parentCell.children[0].value);				
									parentCell.innerHTML = parentCell.children[0].value;
								}
														
								console.log("PUT");
								console.log(updatedData);
								showValidationMessage("Successfully updated " + data.type.toLowerCase());
								validateInput(data);
												
							},							
							error: function(jqXHR, textStatus, errorThrown) {
								console.log("PUT ERROR");						
								console.log(jqXHR);
								showValidationMessage("Failed to update already exists with same name");																		
								rowId = "#" + parentCell.parentElement.id;
								$(rowId).find("#name")[0].innerHTML = getRestoreValue($("#name"+data.resourceId));											
								validateInput(data);										            	
							}
						})		
}

// Update a resource with new data and display back onscreen if successful

function putDataAndDisplay(resourceId, parentCell) {	
	
	
	var data = getDataFromRow(resourceId);
	fieldName=parentCell.id.substring(0, parentCell.id.indexOf("-"));
	data[fieldName] = parentCell.children[0].value;
	
	
	$.ajax({
		url: data.url,
		method:"GET", 
		success: function(origResource){
			console.log("GET");
			console.log(origResource);
			if (origResource.maxDemand > data.quantity) {
				
				$('#confirmMessage')[0].innerText = "Existing demand is greater than new quantity specified. \n Are you sure you want to update?";
				
				$('.confirmYes').off('click').on('click', function(e) {
						e.preventDefault();
						putResource(data, parentCell)					
						$('#confirmModal').hide()											
					})
				$('.confirmNo').off('click').on('click',  function(e) {
						e.preventDefault();					
						$('#confirmModal').hide()							
						origValue = parentCell.getAttribute("latest-data");
						if (origValue == null) {
							origValue = parentCell.getAttribute('orig-data');
						}
						parentCell.innerHTML = origValue;
					})						
				$('#confirmModal').show()							
			}	
			else {
				putResource(data, parentCell);				
			}		
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
	
	

}

// Get the original data to support uno
function getRestoreValue(element) {
	value = null;
	latestValue = element.getAttribute("latest-data");
	origValue = element.getAttribute("orig-data");
	if (latestValue == null) {
		value = origValue;
	}
	else {
		value = latestValue;
	}
	return value;
	
}

// Validate a cell and update styles

function validateCell(cell) {
	result = true; //indicates changed or not
	if (cell.length > 0) {
		if (cell[0].innerHTML == cell[0].getAttribute("orig-data")) {
			result = false;
			cell[0].style.removeProperty("background-color");
		}
		else {
			cell[0].style.backgroundColor="LightGoldenRodYellow";
		}
	}
	else {
		result = false; //Doesn't exist so not changed
	}
	return result;
}



// Validate a full row and update styles

function validateRow(rowPrefix, rowId) {
	changed = false;
	changed |= validateCell($("#name-" + rowId));
	changed |= validateCell($("#description-" + rowId));
	changed |= validateCell($("#keywords-" + rowId));
	changed |= validateCell($("#quantity-" + rowId));
	changed |= validateCell($("#capacity-" + rowId));
	
	changed |= validateCell($("#title-" + rowId));
	changed |= validateCell($("#firstName-" + rowId));
	changed |= validateCell($("#surname-" + rowId));
	changed |= validateCell($("#email-" + rowId));
	changed |= validateCell($("#telephoneNum-" + rowId));
	
	if (!changed) {
		$(rowPrefix+rowId).find(".undoButton").hide();
	}
	else {
		$(rowPrefix+rowId).find(".undoButton").show();
	}		 	
}
	                
// Post new data for new resource and perform updates on screen

function postDataAndDisplay(data, type) {

	
	$.ajax({
		url:"./api/resources",
		method:"POST",
	    dataType : "json",
	    contentType: "application/json; charset=utf-8",
		data: JSON.stringify(data),

		success: function(data,  textStatus, jqXHR){
			console.log("POST");
			console.log(data);
			
			outerHTML = $("."+type+ "TemplateRow")[0].outerHTML;
			
			
			$("#" + type + "NewRow")[0].insertAdjacentHTML("beforeBegin", outerHTML);
			
			newRow = $("."+type+ "TemplateRow")[1];
			newRow.id ="row-" + data.resourceId;
			newRow.classList.add("filterrow");
			newRow.classList.remove(type+ "TemplateRow");
			
			// Copy value from input fields to new row
			copyValue("name", data);
			copyValue("description", data);
			copyValue("keywords", data);
							
			if (type =="equipment") {
				copyValue("quantity", data);				
			}
			else {
				copyValue("capacity", data);												
			}
			
			
			undoButton = $("#row-"+data.resourceId).find("#newUndo")[0]; 
			undoButton.id ="undo-" + data.resourceId;
			
			deleteButton = $("#row-"+data.resourceId).find("#newDelete")[0];
			deleteButton.id = "delete-" + data.resourceId;
			$("#"+deleteButton.id).show();
			
				
			
			setUpEditHandlersForType(type);
			
			//Clear input fields			
			$("." + type + "Input").val("");			
			$("#row-"+data.resourceId).show();	
			setOrigDataFromRow(data.resourceId);		
			showValidationMessage("Successfully recorded new " + type);
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("POST ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to create already exists with same name");
		}
	});
}





// Change resource tab
function activateResourceTab(tabId)
{	
	$(".resourceTab").each(function() {
		this.classList.remove("active");			
	});

	$("#"+tabId)[0].classList.add("active");
	
	$(".resourcePane").hide();
	
	$("#" + tabId.replace("Tab", "List")).show();
	
	
	
}

// Upload image for staff

function uploadFile(file, id) {
	var data = new FormData();
    
    // Add the uploaded image content to the form data collection

    data.append("file", file);     
    data.append("id", id);

    // Make Ajax request with the contentType = false, and procesDate = false
    var ajaxRequest = $.ajax({
         type: "POST",
         url: "./profilePics",
         contentType: false,
         processData: false,
         data: data,
        });
    ajaxRequest.done(function (xhr, textStatus) {
     			console.log("Uploaded Image");
     			src = "./profilePics/" +id + ".jpg";
     			$("#profilePicImage-"+id)[0].setAttribute("src", src);
     			reloadImg(src);
				showValidationMessage("Successfully updated profile picture");

		   });
}

// Upload image for existing staff

function uploadImage(){
    var files = event.target.files;
    if (files.length > 0) {
    	id = event.target.parentElement.getAttribute("for").replace("profilePic-", "");
		uploadFile(files[0], id);
    }	
  }

  // Upload New image for new staff
function uploadNewImage(){
    var files = event.target.files;
    if (files.length > 0) {    	
		uploadFile(files[0], 0);
    }	
  }  

  // reload img with set url 
  
async function reloadImg(url) {
	  await fetch(url, { cache: 'reload', mode: 'no-cors' })
	  document.body.querySelectorAll(`img[src='${url}']`)
	    .forEach(img => img.src = url)
	}
	

	