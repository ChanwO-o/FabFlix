function getParameterByName(target) {
	// Get request URL
	let url = window.location.href;
	// Encode target parameter name to url encoding
	target = target.replace(/[\[\]]/g, "\\$&");

	// Ues regular expression to find matched parameter value
	let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
		results = regex.exec(url);
	if (!results) return null;
	if (!results[2]) return '';

	// Return the decoded parameter value
	return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
	console.log("handleResult: populating movie info from resultData");

	// populate the movie info h3
	// find the empty h3 body by id "movie_info"
	let starInfoElement = jQuery("#star_info");
	let movie_array = resultData[0]["star_movies"].split(',');
	let movie_id_array = resultData[0]["movie_id"].split(',');
	let rowHTML = "";
	for (let i = 0; i < movie_array.length; ++i) {
		let x = parseInt(check_counter) + 1;
		console.log("X = " + x);
		var counter = x.toString();
		console.log(counter);
		if (i === movie_array.length - 1) {
			rowHTML +=
				'<a href="single-movie.html?id=' + movie_id_array[i] + '&check_counter=' + counter + '">' + movie_array[i]
				+ '</a>';
		} else {
			rowHTML +=
				'<a href="single-movie.html?id=' + movie_id_array[i] + '&check_counter=' + counter + '">'
				+ movie_array[i] + ',' +   // display star_name for the link text
				'</a>';
		}
	}
	// append two html <p> created to the h3 body, which will refresh the page
	starInfoElement.append("<p>Star name: " + resultData[0]["star_name"] + "</p>" + "<br>" + "<p>Year of Birth: " + resultData[0]["star_dob"] + "</p>"
		+ "<br>" + "<p>Films: " + rowHTML + "</p>");

	console.log("handleResult: populating movie table from resultData");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
// Get id from URL
let star_id = getParameterByName('id');
let check_counter = getParameterByName('check_counter');
console.log(star_id);
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
	dataType: "json",  // Setting return data type
	method: "GET",// Setting request method
	cache: true,
	url: "api/single-star?id=" + star_id + "&check_counter=" + check_counter,
	success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
});
