function handleMovieResult(resultData) {
	// Find the empty table body by id "movie_table_body"
	let movieTableBodyElement = jQuery("#movie_table_body");
	if (pg == null || pg === "") {
		pg = "1";
		pn = "10";
		location.replace(window.location.search + "&pn=10&pg=1");
	}
	for (let i = (pg - 1) * pn; i < (pg * pn); i++)
	{
		let rowHTML = "<tr>";
		// let rowHTML = "";
		console.log("Result Data = " + resultData);
		if (i > resultData.length - 1) {
			// rowHTML += "<tr>";

			rowHTML += "</tr>";
			rowHTML = "<br><br><p id='no_more_search_results' style =" + '"' + "color:red;" + '"' + ">NO MORE SEARCH RESULT </p>" + rowHTML;
			movieTableBodyElement.append(rowHTML);
			break;
		}

		// Concatenate the html tags with resultData jsonObject
		// let rowHTML = "<tr>";
		rowHTML +=
			"<td>" +
			// Add a link to single-movie.html with id passed with GET url parameter
			'<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '&check_counter=1' + '">'
			+ resultData[i]["movie_title"] +     // display movie_title for the link text
			'</a>' +
			"</td>";
		rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
		rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
		let genres_array = resultData[i]["movie_genres"].split(',');
		rowHTML += "<td>";
		for (let j = 0; j < 3; ++j) {
			if (genres_array[j] !== undefined) {
				if (j === 2 || (genres_array[j + 1] === undefined && (j + 1) < 3)) {
					rowHTML +=
						'<a href="movielist.html?pn=10&pg=1&genres=' + genres_array[j] + '">' + genres_array[j]
						+ '</a>';
				} else {
					rowHTML +=
						'<a href="movielist.html?pn=10&pg=1&genres=' + genres_array[j] + '">' + genres_array[j]
						+ ',' +   // display star_name for the link text
						'</a>';
				}
			}
		}

		rowHTML += "</td>";
		// stars hyperlinks
		rowHTML += "<td>";
		var stars_array = resultData[i]["movie_stars"].split(',');
		var stars_id_array = resultData[i]["star_id"].split(',');

		for (let j = 0; j < 3; ++j) {
			if (stars_array[j] !== undefined) {
				if (j === 2 || (stars_array[j + 1] === undefined && (j + 1) < 3)) {
					rowHTML +=
						'<a href="single-star.html?id=' + stars_id_array[j] + '&check_counter=1' + '">' + stars_array[j]
						+ '</a>';
				} else {
					rowHTML +=
						'<a href="single-star.html?id=' + stars_id_array[j] + '&check_counter=1' + '">'
						+ stars_array[j] + ',' +   // display star_name for the link text
						'</a>';
				}
			}
		}
		rowHTML += "</td>";
		rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";
		rowHTML += "<td><input name=\"addToCart\" type=\"submit\" value=\"Add to Cart\" onclick=\"addToCart('" + resultData[i]['movie_id'] + "')\"></td>";
		rowHTML += "</tr>"; // close row tag

		// Append the row created to the table body, which will refresh the page
		movieTableBodyElement.append(rowHTML);

	}
}

function addToCart(movie_id) {
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		cache: true,
		url: "api/cart?id=" + movie_id,
		success: (resultData) => {
			alert("Movie added to cart!");
		},
		error: (resultData) => {
			alert("Failed to add movie to cart!");
		}
	});
}

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

// let first_sortby=getParameterByName('first_sortby');
// let second_sortvy=getParameterByName('second_sortby');
//
//
// console.log(title_start);
//
// if(first_sortby!=null && first_sortby.length>0)
// {
//     jQuery.ajax({
//         dataType: "json",  // Setting return data type
//         method: "GET",// Setting request method
//         cache: true,
//         url: "api/movies?first_sortby=" + first_sortby+"&title",
//         success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
//     });
// }

let title_start = getParameterByName('title_start');
let first_sortby = getParameterByName('first_sortby');
let pn = getParameterByName('pn');
let pg = getParameterByName('pg');

let second_sortby = getParameterByName('second_sortby');

if (title_start != null && title_start.length > 0) {
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		cache: true,
		url: "api/movies",
		data: {
			first_sortby: first_sortby,
			second_sortby: second_sortby,
			pg: pg,
			pn: pn,
			title_start: title_start
		},
		success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar

	});
} else {
	let test = getParameterByName('genres');
	let first_sortby = getParameterByName('first_sortby');
	let second_sortby = getParameterByName('second_sortby');
	var start_time= new Date().getTime();
	if (test != null && test.length > 1) {

		jQuery.ajax({
			dataType: "json",  // Setting return data type
			method: "GET",// Setting request method
			cache: true,
			url: "api/movies",
			data: {
				first_sortby: first_sortby,
				second_sortby: second_sortby,
				genres: test,
			},
			success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
		});
	} else {
		let queryString = window.location.href.split('?');


		if (queryString.length > 1) {
			let params = queryString[1].split('&');
			if (params.length > 0) {
				let first_sortby = getParameterByName('first_sortby');
				let second_sortby = getParameterByName('second_sortby');
				let title = params[0].substr(6);
				let year = params[1].substr(5);
				let director = params[2].substr(9);
				let star = params[3].substr(5);
				jQuery.ajax({
					dataType: "json", // Setting return data type
					method: "GET", // Setting request method
					cache: true,
					url: "api/movies",
					data: {
						first_sortby: first_sortby,
						second_sortby: second_sortby,
						title: title,
						year: year,
						director: director,
						star: star,
						pn: "10",
						pg: "1"
					},
					success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
				});
			} else {
				jQuery.ajax({
					dataType: "json", // Setting return data type
					method: "GET", // Setting request method
					cache: true,
					url: "api/movies",
					success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
				});
			}
		} else {
			jQuery.ajax({
				dataType: "json", // Setting return data type
				method: "GET", // Setting request method
				cache: true,
				url: "api/movies",
				success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
			});
		}
	}
}
