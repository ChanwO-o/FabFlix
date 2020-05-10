function handleResult(resultData) {
	console.log("resultData: ", resultData);
}

function onAddStarSubmit(formSubmitEvent) {
	console.log("submit addStar form");
	formSubmitEvent.preventDefault();

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

let addStarForm = jQuery("#addstar_form");
addStarForm.submit(onAddStarSubmit);
