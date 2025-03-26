

function getDataFromRow(id) {
	var data = {};
	$("#row-" + id).find(".editable").each(function() {
		data[this.id] = this.innerText;			
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
			var isStaff = $("#staffList").find("#row-"+id).length > 0;
			if (isStaff) {
			
				data["type"] = "STAFF";
				data["userId"] = id.substring(1);				
				data["url"] = "./api/staff/" + id.substring(1);
			}
			else {
				data["type"] = "MENUOPTION";
				data["menuOptionId"] = id;
				data["url"] = "./api/menuoption/" + id;
			}
		}
	}	
	return data;
}



function undoChanges(button) {
	resourceId=button.id.replace("undo-", "");
	$("#row-" + resourceId).find(".editable").each(function() {
		if (this.getAttribute("editType") != "file") {
			this.innerHTML = this.getAttribute("orig-data");
			this.style.removeProperty("background-color");
		}
		else {
			innerHTML = "<img src='" + this.getAttribute("orig-data") + "' width='30' height='30' class='rounded-circle'/>";
			this.innerHTML = innerHTML;
		}
	});

	var data = getDataFromRow(resourceId);
			
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
			$("#undo-"+resourceId).hide();					
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("PUT ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to update " + data.type.toLowerCase());
		}
	});
		
}


function deleteResource(button, type) {	
	id=button.id.replace("delete-", "");

	$.ajax({
		url:"./api/resources/" + id,
		method:"GET", 
		success: function(data){
			console.log("GET");
			console.log(data);
			shouldDelete = true;
			if (data.maxDemand > 0) {
				message = type.toUpperCase().substring(0,1) + type.substring(1) + " required to support already scheduled sessions. \n Do you really want to delete?"; 
				shouldDelete = confirm(message);
			}
			if (shouldDelete) {				
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
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
	return;		
}

function deleteStaff(button) {	
	id=button.id.replace("delete-", "");

	$.ajax({
		url:"./api/staff/" + id,
		method:"GET", 
		success: function(data){
			console.log("GET");
			console.log(data);
			shouldDelete = true;
			if (data.maxDemand > 0) {				
				shouldDelete = confirm("Staff member required to support already scheduled sessions. \n Do you really want to delete?");				
			}
			if (shouldDelete) {								
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
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
	return;		
}



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
			"capacity": $("input[name='staffCapacity']").val(),
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
			$("#row-u"+data.userId).find("#title")[0].innerHTML=data.title;
			$("#row-u"+data.userId).find("#firstName")[0].innerHTML=data.firstName;
			$("#row-u"+data.userId).find("#surname")[0].innerHTML=data.surname;
			$("#row-u"+data.userId).find("#email")[0].innerHTML=data.email;
			$("#row-u"+data.userId).find("#telephoneNum")[0].innerHTML=data.telephoneNum;
			$("#row-u"+data.userId).find("#description")[0].innerHTML=data.description;
			$("#row-u"+data.userId).find("#keywords")[0].innerHTML=data.keywords;
			$("#row-u"+data.userId).find("#capacity")[0].innerHTML=data.capacity;
			
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
			
				
			
			//TODO more efficiently
			setUpEditHandlersForType('staff');
			

			
			
			
			if (files.length>0) {
				uploadFile(files[0], data.userId);				
	     		$("#profilePicImage-0")[0].setAttribute("src", "./images/missingProfile.jpg");
			}
				
			
			//Clear input fields			
			$(".staffInput").val("");
			$("#row-u"+data.userId).show();
			showValidationMessage("Successfully recorded staff member ");
			
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("POST ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to save new staff member");

			
		}
	});	
	

	
}





function insertEditControl(parent) {
	parent.setAttribute('data-clicked', 'yes');
    parent.setAttribute('data-text', parent.innerHTML);
    editType =  parent.getAttribute('edittype')
    var input = null;
    
    if (editType != "file") {
	    if (editType == "textarea") {
	    	input = document.createElement('textarea');
	    }
	    else {
	    	input = document.createElement('input');
	    	input.setAttribute('type',editType);
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
	            
	            
	            
	            
	            if (origText != current_text) {
	            		                
	                putDataAndDisplay(td.parentElement.id.replace("row-", ""), td)
	            }
	            else {
	            	td.style.removeProperty("background-color");	              
	            	rowId = "#" + td.parentElement.id;
	            	
	            	if ($(rowId).find("#name")[0].innerHTML == $(rowId).find("#name")[0].getAttribute("orig-data") &&
	            		$(rowId).find("#description")[0].innerHTML == $(rowId).find("#description")[0].getAttribute("orig-data") &&
	            		$(rowId).find("#quantity")[0].innerHTML == $(rowId).find("#quantity")[0].getAttribute("orig-data") &&
	            		$(rowId).find("#keywords")[0].innerHTML == $(rowId).find("#keywords")[0].getAttribute("orig-data")) {
	            		$(rowId).find("#button").hide(); 	
	            	}	                
	            }
	            	
	            
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

	

function setUpEditHandlers() {
	setUpEditHandlersForType('menuOption');
	return;
}





function fetchDataAndDisplay() {
	$.ajax({
		url:"./api/resources",
		method:"GET", 
		success: function(data){
			console.log("GET");
			console.log(data);
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
}

function undoDataAndDisplay(resourceId) {
	
	data = {
			"description": $("#row-" + resourceId).find("#description")[0].innerText,
			"keywords": $("#row-" + resourceId).find("#keywords")[0].innerText,
			"name": $("#row-" + resourceId).find("#name")[0].innerText,
			"quantity": $("#row-" + resourceId).find("#quantity")[0].innerText,
			"type": "EQUIPMENT",
			"resourceId": resourceId
		}
	
	$.ajax({
		url:"./api/resources/" + resourceId,
		method:"PUT",
	    dataType : "json",
	    contentType: "application/json; charset=utf-8",
		data: JSON.stringify(data), 
		success: function(data,  textStatus, jqXHR){
			console.log("PUT");
			console.log(data);
			showValidationMessage("Successfully updated equipment");
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("PUT ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to update equipment");
		}
	});
}



function putDataAndDisplay(resourceId, parentCell) {	
	
	
	var data = getDataFromRow(resourceId);	
	data[parentCell.id] = parentCell.children[0].value;
			
	
	$.ajax({
		url: data.url,
		method:"GET", 
		success: function(origResource){
			console.log("GET");
			console.log(origResource);
			shouldEdit = true;
			if (origResource.maxDemand > data.quantity) {
				shouldEdit = confirm("Existing demand is greater than new quantity. \n Are you sure you want to update?");				  
			}
			if (shouldEdit) {
				if (parentCell != null) {
					parentCell.setAttribute("latest-data", parentCell.children[0].value);				
					parentCell.innerHTML = parentCell.children[0].value;
					parentCell.style.backgroundColor="LightGoldenRodYellow";
				}
				
				$.ajax({
					url: data.url,
					method:"PUT",
				    dataType : "json",
				    contentType: "application/json; charset=utf-8",
					data: JSON.stringify(data), 
					success: function(updatedData,  textStatus, jqXHR){
						console.log("PUT");
						console.log(updatedData);
						showValidationMessage("Successfully updated " + data.type.toLowerCase());						
						$("#undo-"+resourceId).show();					
					},
					
					error: function(jqXHR, textStatus, errorThrown) {
						console.log("PUT ERROR");						
						console.log(jqXHR);
						showValidationMessage("Failed to update " + data.type.toLowerCase());						
					}
				});								
			}
			else {
				origValue = parentCell.getAttribute("latest-data");
				if (origValue == null) {
					origValue = parentCell.getAttribute('orig-data');
				}
				parentCell.innerHTML = origValue;
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("GET ERROR");
			console.log(jqXHR);
			
		}
	});	
	
	

}

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
			$("#row-"+data.resourceId).find("#name")[0].innerHTML=data.name;
			$("#row-"+data.resourceId).find("#description")[0].innerHTML=data.description;
			$("#row-"+data.resourceId).find("#keywords")[0].innerHTML=data.keywords;
			if (type =="equipment") {
				$("#row-"+data.resourceId).find("#quantity")[0].innerHTML=data.quantity;
			}
			else {
				$("#row-"+data.resourceId).find("#capacity")[0].innerHTML=data.capacity;
			}
			
			
			undoButton = $("#row-"+data.resourceId).find("#newUndo")[0]; 
			undoButton.id ="undo-" + data.resourceId;
			
			deleteButton = $("#row-"+data.resourceId).find("#newDelete")[0];
			deleteButton.id = "delete-" + data.resourceId;
			$("#"+deleteButton.id).show();
			
				
			
			//TODO more efficiently
			setUpEditHandlersForType(type);
			
			//Clear input fields			
			$("." + type + "Input").val("");			
			$("#row-"+data.resourceId).show();			
			showValidationMessage("Successfully recorded new " + type);
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("POST ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to record new " + type);

			
		}
	});
}



function showValidationMessage(message)
{
	$("#validationMessage")[0].innerText = message;
	$(".afterschoolclubflash").show()	
}





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
		   });
}


function uploadImage(){
    var files = event.target.files;
    if (files.length > 0) {
    	id = event.target.parentElement.getAttribute("for").replace("profilePic-", "");
		uploadFile(files[0], id);
    }	
  }
  
function uploadNewImage(){
    var files = event.target.files;
    if (files.length > 0) {    	
		uploadFile(files[0], 0);
    }	
  }  

async function reloadImg(url) {
	  await fetch(url, { cache: 'reload', mode: 'no-cors' })
	  document.body.querySelectorAll(`img[src='${url}']`)
	    .forEach(img => img.src = url)
	}
	

	