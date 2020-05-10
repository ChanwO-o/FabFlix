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
	}
	else {
		$.ajax(
			"api/dashboard", {
				method: "GET",
				data: addStarForm.serialize(),
				success: (resultData) => {
					alert("Added new star!");
				}
			}
		);
	}
}

function onAddMovieSubmit(formSubmitEvent) {
	console.log("submit addMovie form");
	formSubmitEvent.preventDefault();

	// $.ajax(
	// 	"api/dashboard", {
	// 		method: "GET",
	// 		data: addMovieForm.serialize(),
	// 		success: (resultData) => {
	// 			alert("Added new movie!");
	// 		}
	// 	}
	// );
}

let addStarForm = jQuery("#addstar_form");
addStarForm.submit(onAddStarSubmit);
let addMovieForm = jQuery("#addmovie_form");
addMovieForm.submit(onAddMovieSubmit);