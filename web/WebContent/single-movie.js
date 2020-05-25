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
	let movieInfoElement = jQuery("#movie_info");
	console.log("resultDAta= " + resultData[0]);
	// append two html <p> created to the h3 body, which will refresh the page
	$.getJSON("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + resultData[0]["movie_title"], function (json) {
		//console.log(json.results[0].poster_path);
		let count = 0;
		let k = "";
		let p = 0;
		for (p = 0; p < json.results.length; p++) {
			console.log(json.results[count]);
			if (json.results[p].poster_path != null) {
				count = 1;
				k += json.results[p].poster_path;
				break;
			}
			//  count++;
		}
		rowHTML = "";
		if (count === 1)
			rowHTML = "<img src=" + '"' + "http://image.tmdb.org/t/p/w500/" + k + '" ' + "width=" + "300 " + "height=" + "300/>";
		else {
			rowHTML = "<img src=" + '"' + "no_image.png" + '"' + " width=" + "100 " + "height=" + "100/>";
		}
		var genres_array;
		let temp_1;
		if(resultData[0]["movie_gners"]==null)
		{
			temp_1="<p> Genres: null";
		}
		else {
			genres_array = resultData[0]["movie_genres"].split(',');
			temp_1 = "<p> Genres: ";
			for (let i = 0; i < genres_array.length; ++i) {
				if (i === genres_array.length - 1) {
					temp_1 +=
						'<a href="movielist.html?genres=' + genres_array[i] + '&pn=10&pg=1' + '">' + genres_array[i]
						+ '</a>';
				} else {
					temp_1 +=
						'<a href="movielist.html?genres=' + genres_array[i] + '&pn=10&pg=1' + '">'
						+ genres_array[i] + ',' +   // display star_name for the link text
						'</a>';
				}
			}
		}
		temp_1 += "</p><p> Stars in Movies: ";
		var stars_array = resultData[0]["movie_stars"].split(',');
		var stars_id_array = resultData[0]["stars_id"].split(',');
		for (let i = 0; i < stars_array.length; ++i) {
			let x = parseInt(check_counter) + 1;
			console.log("X = " + x);
			var counter = x.toString();
			console.log(counter);
			if (i === stars_array.length - 1) {

				//console.log("CHECK COUNTER IN SINGLE MOVIE PAGE" + check_counter);
				// check_counter = check_counter +;
				temp_1 +=
					'<a href="single-star.html?id=' + stars_id_array[i] + '&check_counter=' + counter + '">' + stars_array[i]
					+ '</a>';
			} else {
				temp_1 +=
					'<a href="single-star.html?id=' + stars_id_array[i] + '&check_counter=' + counter + '">'
					+ stars_array[i] + ',' +   // display star_name for the link text
					'</a>';
			}
		}
		temp_1 += "</p>";
		temp_1 += "<p> RATING : " + resultData[0]["movie_rating"] + "</p>";
		movieInfoElement.append(rowHTML + "<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
			"<p>Year: " + resultData[0]["movie_year"] + "</p>" + "<p>Movie Director: " + resultData[0]["movie_director"] + "</p>" + temp_1 + "<br><br>");
	});
	// movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
	//     "<p>Year: " + resultData[0]["movie_year"] + "</p>");
}

function handleCartResult(resultData) {
	console.log("handleCartResult()");
}

function addToCart(movie_id) {
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		cache: true,
		url: "api/cart?id=" + movie_id,
		success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
	});
}

// Get id from URL
let movie_id = getParameterByName('id');
let fulltext= getParameterByName('fulltext');
let check_counter = getParameterByName('check_counter');
console.log("CHECK COUNTER IN SINGLE MOVIE PAGE" + check_counter);
if (check_counter ==null)
	check_counter=1;

// Makes the HTTP GET request and registers on success callback function handleResult
if(fulltext!=null && fulltext.length > 0)
{
	console.log("fulltext search");
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		cache: true,
		data:{
			title: fulltext
		},
		url: "api/single-movie?title="+fulltext+"&check_counter="+check_counter,
		// data: {
		// 	first_sortby: first_sortby,
		// 	second_sortby: second_sortby,
		// 	pg: pg,
		// 	pn: pn,
		// 	title_start: title_start
		// },
		success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar

	});
}
else {
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		cache: true,
		url: "api/single-movie?id=" + movie_id + "&check_counter=" + check_counter,
		success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
	});
}