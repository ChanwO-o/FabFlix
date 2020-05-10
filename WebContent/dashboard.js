function handleResult(resultData) {
	console.log("resultData: ", resultData);
}

function onAddStarSubmit(formSubmitEvent) {
	console.log("submit addStar form");
	formSubmitEvent.preventDefault();

	let addStarData = addStarForm.serialize();
	var n = addStarData.indexOf("&");
	var starName = addStarData.substring(5, n); // extract just the star name
	// console.log("starName: ", starName);

	if (starName === "") {
		alert("Star name field is required!");
		addStarForm.trigger('reset');
	}
	else {
		$.ajax(
			"api/dashboard", {
				method: "GET",
				data: addStarForm.serialize(),
				success: (resultData) => {
					console.log(resultData);
					resultData=resultData.substring(1,resultData.length-1);
					resultData=JSON.parse(resultData);
					alert("Added new star with Id " + resultData['starId']);
					addStarForm.trigger('reset');
				}
			}
		);
	}
}
function handleResultData(resultData)
{
	console.log(typeof(resultData));
	console.log(resultData);
	resultData=resultData.substring(1,resultData.length-1);
	console.log(resultData);
	resultData=JSON.parse(resultData);
	console.log(resultData['addedMovieId']);
	if (resultData['message'] === "movie exists") {
		alert("Movie exists! No changes to database made.");
		addMovieForm.trigger('reset');
	}
	else {
		alert("Added new movies with Movie id:"+  resultData['addedMovieId']+ ", Star Id: "+ resultData['addedStarId']+ ", Genre Id: "+ resultData['addedGenreId']);
		addMovieForm.trigger('reset');
	}
}
function onAddMovieSubmit(formSubmitEvent) {
	console.log("submit addMovie form");
	formSubmitEvent.preventDefault();

	let addMovieData = addMovieForm.serialize();
	var startIndex = addMovieData.indexOf("title") + 6;
	var endIndex = addMovieData.indexOf("&year");
	var movieTitle = addMovieData.substring(startIndex, endIndex); // extract just the star name
	startIndex = addMovieData.indexOf("&year") + 6;
	endIndex = addMovieData.indexOf("&director");
	var movieYear = addMovieData.substring(startIndex, endIndex);
	startIndex = addMovieData.indexOf("&director") + 10;
	endIndex = addMovieData.indexOf("&star_name");
	var movieDirector = addMovieData.substring(startIndex, endIndex);
	startIndex = addMovieData.indexOf("&star_name") + 11;
	endIndex = addMovieData.indexOf("&genre");
	var movieStar = addMovieData.substring(startIndex, endIndex);
	startIndex = addMovieData.indexOf("&genre") + 7;
	var movieGenre = addMovieData.substring(startIndex);

	console.log("addMovieData: ", addMovieData); // title=tt&year=yy&director=dd&star_name=sn&genre=gn
	console.log("movieTitle: ", movieTitle, "movieYear: ", movieYear, "movieDirector: ", movieDirector, "movieStar: ", movieStar, "movieGenre: ", movieGenre);

	if (movieTitle === "")
		alert("Movie Title field is required!");
	else if (movieYear === "")
		alert("Movie Year field is required!");
	else if (movieDirector === "")
		alert("Movie Director field is required!");
	else if (movieStar === "")
		alert("Movie Star field is required!");
	else if (movieGenre === "")
		alert("Movie Genre field is required!");
	else {
		console.log("FAFAFAFAF");
		$.ajax(
			"api/dashboard", {
				method: "GET",
				data: addMovieForm.serializeArray(),
				success: (resultData) => handleResultData(resultData)

			}
		);
	}
}

let addStarForm = jQuery("#addstar_form");
addStarForm.submit(onAddStarSubmit);
let addMovieForm = jQuery("#addmovie_form");
addMovieForm.submit(onAddMovieSubmit);