// Determine whether should filter a row based on search terms and row text
	function shouldFilter(row, words) {
		filter = true;
		index = 0;
		rowText = $(row)[0].innerText.toLowerCase();
		while (index < words.length && filter) {
			value = words[index]
			index++
			filter = rowText.indexOf(value) > -1;
		}
		return filter;
	}

// Need to re-apply stripes after doing a search and filtering
	function reapplyStripes() {
		$(".stripedrow:visible").each(function (index) {
			  if (index % 2 == 0) {
			            $(this).addClass('evenStripe').removeClass('oddStripe');
			  }
			else {
			            $(this).addClass('oddStripe').removeClass('evenStripe');
			}
			});			
	}

// return an array of search terms
	function getSearchTerms(text) {

		//The parenthesis in the regex creates a captured group within the quotes
		var myRegexp = /[^\s"]+|"([^"]*)"/gi;
		var myString = text.toLowerCase().trim();
		var myArray = [];

		do {
			//Each call to exec returns the next regex match as an array
			var match = myRegexp.exec(myString);
			if (match != null) {
				//Index 1 in the array is the captured group if it exists
				//Index 0 is the matched text, which we use if no captured group exists
				myArray.push(match[1] ? match[1] : match[0]);
			}
		} while (match != null);
		return myArray;
	}
	

// when hit a key in search box apply filter
	$(document).ready(function() {
		reapplyStripes()
		$("#filter").on("keyup", function() {
			var words = getSearchTerms($(this).val());

			$(".filterrow").filter(function() {
				$(this).toggle(shouldFilter(this, words))
			});
			reapplyStripes()

		});

	});