
function deleteStudent() {
	if (!confirm("Are you sure you want to delete this student?") == true) {
		window.event.preventDefault();
	}
}


function activateStudentTab(tabId)
{	
	$(".parentTab").each(function() {
		this.classList.remove("active");			
	});

	$("#"+tabId+"Tab")[0].classList.add("active");
	
	$(".parentPane").hide();
	
	$("#" + tabId.replace("Tab", "List")).show();
	
}  

function showMedical(button)
{
	window.event.preventDefault();
	id = button.id;
	
	$("#allergyNote")[0].innerText=$("#allergyNote-"+id).val();
	$("#healthNote")[0].innerText=$("#healthNote-"+id).val();
	$("#dietNote")[0].innerText=$("#dietNote-"+id).val();
	$("#careNote")[0].innerText=$("#careNote-"+id).val();
	$("#medicationNote")[0].innerText=$("#medicationNote-"+id).val();	
	$("#otherNote")[0].innerText=$("#otherNote-"+id).val();
	$("#healthModal").show();

		
}  

function dismissMedical()
{	
	$("#healthModal").hide();		
}  


