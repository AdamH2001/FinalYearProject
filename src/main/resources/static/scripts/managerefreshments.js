
function validateInput(data) {	
	validateRow("#row-", data.menuOptionId);
		
}

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

function validateRow(rowPrefix, rowId) {
	changed = false;
	changed |= validateCell($("#name-" + rowId));
	changed |= validateCell($("#description-" + rowId));
	changed |= validateCell($("#allergyInformation-" + rowId));
	changed |= validateCell($("#additionalCost-" + rowId));
	
	if (!changed) {
		$(rowPrefix+rowId).find("#undo-"+ rowId).hide();
	}
	else {
		$(rowPrefix+rowId).find("#undo-" + rowId).show();
	}		 	
}
	           
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

function copyValue(fieldName, newData) {
	id = newData.menuOptionId;
	
	if ($("#"+fieldName)[0].getAttribute("edittype") == "money") {
		$("#row-" + id).find("#"+fieldName)[0].innerHTML= pounds.format(newData[fieldName]/100);
	}
	else {
		$("#row-" + id).find("#"+fieldName)[0].innerHTML=newData[fieldName];	
	}		
		
	$("#row-" + id).find("#"+fieldName)[0].id=fieldName + "-" + id;	
	
}


function getDataFromControl(control)
{
	result =control.value;
	if (control.getAttribute("money") == "yes") {
		result = result.replace(".", "").replace("£", "");			
	}
	return result;
}


function getDataFromRow(id) {
	var data = {};
	$("#row-" + id).find(".editable").each(function() {
		fieldName=this.id.substring(0, this.id.indexOf("-"));
		data[fieldName] = this.innerText;		
		if (this.getAttribute("edittype") == "money") {
			data[fieldName] = data[fieldName].replace(".", "").replace("£", "");			
		}			
	});
	data["state"] = "ACTIVE";

	data["type"] = "MENUOPTION";
	data["menuOptionId"] = id;
	data["url"] = "./api/menuoption/" + id;
	
	return data;
}

function getOrigDataFromRow(id) {
	var data = {};
	$("#row-" + id).find(".editable").each(function() {
		fieldName=this.id.substring(0, this.id.indexOf("-"));		
		data[fieldName] = this.getAttribute("orig-data");	
		if (this.getAttribute("edittype") == "money") {
			data[fieldName] = data[fieldName].replace(".", "").replace("£", "");			
		}			
	});
	data["state"] = "ACTIVE";

	data["type"] = "MENUOPTION";
	data["menuOptionId"] = id;
	data["url"] = "./api/menuoption/" + id;
	
	return data;
}


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
			
			$("#row-" + resourceId).find(".editable").each(function() {
				this.innerHTML = this.getAttribute("orig-data");
			});			
			
			showValidationMessage("Successfully undone changes to " + data.type.toLowerCase());
			validateInput(data);								
			
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("PUT ERROR");
			console.log(jqXHR);
			showValidationMessage("Failed to undo changes to menu item, original name re-used");
		}
	});
		
}





function insertEditControl(parent) {
	parent.setAttribute('data-clicked', 'yes');
    parent.setAttribute('data-text', parent.innerHTML);
    editType =  parent.getAttribute('edittype')
	editlength =  parent.getAttribute('editlength')
	
    var input = null;
    
    if (editType != "file") {
	    if (editType == "textarea") {
	    	input = document.createElement('textarea');
			input.setAttribute("maxlength", "1024")
	    }
	    else {
	    	input = document.createElement('input');
			
			if (editType == "money") {

				input.onchange = function() {
					validateMoneyChange(event);			
				  };
				input.setAttribute("maxlength", "6")
				input.setAttribute("money", "yes")
			}
			else {
				
				if (editType=="number") {
					input.setAttribute('min',"0");	
				}					
	    		input.setAttribute('type',editType);
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
	        } else
			if (this.getAttribute("money") == "yes") {
				validateMoneyEntry(event);					
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





function putDataAndDisplay(resourceId, parentCell) {	
	var data = getDataFromRow(resourceId);	
	fieldName=parentCell.id.substring(0, parentCell.id.indexOf("-"));
	data[fieldName] = getDataFromControl(parentCell.children[0]);
	
	$.ajax({
		url: data.url,
		method:"PUT",
	    dataType : "json",
	    contentType: "application/json; charset=utf-8",
		data: JSON.stringify(data), 
		success: function(updatedData,  textStatus, jqXHR){
			console.log("PUT");
			console.log(updatedData);
			if (parentCell != null) {
				parentCell.setAttribute("latest-data", parentCell.children[0].value);				
				parentCell.innerHTML = parentCell.children[0].value;
			}
			showValidationMessage("Successfully updated menu item");
			validateInput(data);						
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("PUT ERROR");						
			console.log(jqXHR);
			
			rowId = "#" + parentCell.parentElement.id;
			$("#name-"+data.menuOptionId)[0].innerHTML = getRestoreValue($("#name-"+data.menuOptionId)[0]);											
			validateInput(data);										            	
			
			
			showValidationMessage("Failed to update menu item name already in use");						
		}
	});											

}




function addItems() {
	
	newMenuItem ={
			"menuOptionId" : 3,
			"menuGroupId" : 1					
		}
	
	
	
	
	$('#myModal').hide();
	menuGroupId = $(".accordion-collapse:visible")[0].id.replace("flush-collapse-", "");
	templateHTML = $("#row-MENUGROUPID-MENUOPTIONID")[0].outerHTML;
	outerHTML = ""
	
	
	rows = $("#menuOptionList2").find(".filterrow").find("input:checked").parent().parent();
	names = rows.find(".name")

	
	thisMenuGroup = $("#menuGroupList").find("#row-" + menuGroupId); 
	
	for (let i = 0;  i<rows.length; i++) {
		name = names[i].innerText;
		menuItemId = rows[i].id.replace("row-", "");
		if (thisMenuGroup.find("#row-"+menuGroupId+"-"+menuItemId).length==0) { // Ensure not already Added
			outerHTML += templateHTML.replaceAll("MENUGROUPID", menuGroupId).replaceAll("MENUOPTIONID", menuItemId).replaceAll("MENUITEMNAME", name);
		
			newMenuItem ={
					"menuOptionId" : menuItemId,
					"menuGroupId" : menuGroupId					
				}		
		
			$.ajax({
				url:"./api/menugroupoption",
				method:"POST",
			    dataType : "json",
			    contentType: "application/json; charset=utf-8",
				data: JSON.stringify(newMenuItem),
		
				success: function(data,  textStatus, jqXHR){
					$(".ascmenuitem[id='row-"+data.menuGroupId+"-"+data.menuOptionId+"']")[0].id="row-"+data.menuGroupOptionId;
					$("#delete-"+data.menuGroupId+"-"+data.menuOptionId)[0].id="delete-"+data.menuGroupOptionId;
					showValidationMessage("Successfully added new menu item");

				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log("POST ERROR");
					console.log(jqXHR);
					showValidationMessage("Failed to add new menu item");				
				}
			});
		
		
		}				
	}
	
	
	$("#newItem-" + menuGroupId)[0].insertAdjacentHTML("beforeBegin", outerHTML);
	
}

function addMenuGroup(event) {
	event.preventDefault();
	
	menuGroup = {
			"name": $("input[name=menuGroupName]").val()
		}		
	
	if (menuGroup.name != "") {

			
		$.ajax({
			url:"./api/menugroup",
			method:"POST",
		    dataType : "json",
		    contentType: "application/json; charset=utf-8",
			data: JSON.stringify(menuGroup),
	
			success: function(data,  textStatus, jqXHR){
				templateHTML = $("#row-MENUGROUPID")[0].outerHTML;
				outerHTML = templateHTML.replaceAll("MENUGROUPNAME", data.name).replaceAll("MENUGROUPID", data.menuGroupId);
				$("#row-NewMenuGroup")[0].insertAdjacentHTML("beforeBegin", outerHTML);
				value = $("input[name=menuGroupName]").val("");	
				showValidationMessage("Successfully added new menu group");

			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("POST ERROR");
				console.log(jqXHR);
				showValidationMessage("Failed to save new menu group name already in use");				
			}
		});
	}		
}

		function modalShow() {
			$("#menuOptionList2")[0].innerHTML = $("#menuOptionList")[0].innerHTML;
			
			// Remove items already in menu
			menuGroupId = $(".accordion-collapse:visible")[0].id.replace("flush-collapse-", "");
			$(".accordion-collapse:visible").find(".ascmenuitem").each(function() {
					alreadyExistingRow = this.getAttribute("orig-id").replace("row-"+menuGroupId + "-", "#row-");
					$("#menuOptionList2").find(alreadyExistingRow)[0].outerHTML="";
				});

			$("#menuOptionList2").find(".filterrow").show();	// Original list may have been filtered need to undo impact of that		



			
			$("#menuOptionList2").find(".filterrow").find("input").prop("checked", false);			
			$("#menuOptionList2").find(".asc-menuitembutton").hide();
			$("#menuOptionList2").find(".asc-menuitemcheckbox").show();
			$("#menuOptionList2").find("#menuOptionNewRow").hide();			
			$('#myModal').show();
			
		}

		function modalHide() {
			$('#myModal').hide();
			
		}
		

		function addMenuItem(button) {

			modalShow();
		}
		
		
		
		

		function deleteMenuGroup(image) {
			window.event.cancelBubble = true;
			window.event.preventDefault();
			id = image.id.replace("delete-", "");

			$.ajax({
						url : "./api/menugroup/" + id,
						method : "DELETE",

						success : function(data, textStatus, jqXHR) {
							$(".accordion-item[id='row-" + id + "']")[0].outerHTML = "";

							showValidationMessage("Successfully deleted menu group");

						},
						error : function(jqXHR, textStatus, errorThrown) {
							console.log("DELETE ERROR");
							console.log(jqXHR);
							showValidationMessage("Failed to delete menu group");
						}
					});

		}

		function removeMenuItemFromGroup(image) {
			window.event.cancelBubble = true;
			window.event.preventDefault();
			id = image.id.replace("delete-", "")
			
			$.ajax({
				url : "./api/menugroupoption/" + id,
				method : "DELETE",

				success : function(data, textStatus, jqXHR) {
					$(".ascmenuitem[id='row-" + id + "']")[0].outerHTML=""					
					showValidationMessage("Successfully removed menu item");

				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log("DELETE ERROR");
					console.log(jqXHR);
					showValidationMessage("Failed to remove menu item");
				}
			});			
		}

		

		
		function addNewmenuOption(event) {
			event.preventDefault();		
			data = {
					"name": $("input[name='menuOptionName']").val(),					
					"description": $("input[name='menuOptionDescription']").val(),
					"additionalCost": $("input[name='menuOptionAdditionalCost']").val().replace(".", "").replace("£", ""),
					"allergyInformation": $("input[name='menuOptionAllergyInformation']").val(),
					"state": "ACTIVE"
				}		

			
			$.ajax({
				url:"./api/menuoption",
				method:"POST",
			    dataType : "json",
			    contentType: "application/json; charset=utf-8",
				data: JSON.stringify(data),

				success: function(data,  textStatus, jqXHR){
					console.log("POST");
					console.log(data);
					
					outerHTML = $("#row-MENUOPTIONID")[0].outerHTML;
					outerHTML = outerHTML.replaceAll("MENUOPTIONID", data.menuOptionId)
					
					
					$("#menuOptionNewRow")[0].insertAdjacentHTML("beforeBegin", outerHTML);
					
					// Copy value from input fields to new row
					
					copyValue("name", data);
					copyValue("description", data);
					copyValue("additionalCost", data);
					copyValue("allergyInformation", data);
					
		
					setUpEditHandlersForType("menuOption");
					
					//Clear input fields			
					$(".menuOptionInput").val("");			
					$("#row-"+data.menuOptionId).show();			
					showValidationMessage("Successfully recorded new menu item");
					
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log("POST ERROR");
					console.log(jqXHR);
					showValidationMessage("Failed to record new menu item ");

					
				}
			});			
		}
		
		function deleteMenuItem(button) {	
			id=button.id.replace("delete-", "");

			$.ajax({
				url:"./api/menuoption/" + id,
				method:"GET", 
				success: function(data){
					console.log(data);
				
					$.ajax({
						url:"./api/menuoption/" +id,
						method:"DELETE",
						success: function(data,  textStatus, jqXHR){
							console.log("DELETE");
							console.log(data);
							id = this.url.substr(this.url.lastIndexOf("/")+1);
							$("#row-"+id)[0].outerHTML="";												
							showValidationMessage("Menu item deleted");						
						},
						error: function(jqXHR, textStatus, errorThrown) {
							console.log("DELETE ERROR");
							console.log(jqXHR);						 					
							showValidationMessage("Failed to delete menu item" );						
						}
					});																
					
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log("GET ERROR");
					console.log(jqXHR);
					
				}
			});	
			return;		
		}
		
		$(document).ready(function(){
			  $("#addFilter").on("keyup", function() {
			    var words = $(this).val().toLowerCase().trim().split(/\s+/);
			    
			    $("#menuOptionList2").find(".filterrow").filter(function() {
			    	$(this).toggle(shouldFilter(this, words))
			    });			    			    
			  });
			  
			  $("#mainFilter").on("keyup", function() {
				    var words = $(this).val().toLowerCase().trim().split(/\s+/);
				    
				    $("#menuOptionList").find(".filterrow").filter(function() {
				    	$(this).toggle(shouldFilter(this, words))
				    });
				    
				    $("#menuGroupList").find(".filterrow").filter(function() {
				    	$(this).toggle(shouldFilter(this, words))
				    });

				    
				  });
			  $('.asc-moneyinput').change(function(e) {
					validateMoneyChange(e);			
				  });

				$('.asc-moneyinput').keydown(function(e) {
					validateMoneyEntry(e);	
				  });			  
			  
			});
		
		
		
		function activateResourceTab(tabId)
		{	
			$("#mainFilter").val("");
			$("#menuOptionList").find(".filterrow").show();
			$("#menuGroupList").find(".filterrow").show();
			 
			$(".resourceTab").each(function() {
				this.classList.remove("active");			
			});

			$("#"+tabId)[0].classList.add("active");
			
			$(".resourcePane").hide();
			
			$("#" + tabId.replace("Tab", "List")).show();
			
			
			
		}



	

	