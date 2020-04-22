let advancedsearch_form = $("#advancedsearch_form");
let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
}

/**
 * Handle the items in item list
 * @param resultDataString jsonObject, needs to be parsed to html
 */
function handleCartArray(resultDataString) {
    const resultArray = resultDataString.split(",");
    console.log(resultArray);
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/mainpage", {
        method: "POST",
        data: cart.serialize(),
        success: handleCartArray
    });
}

/**
 * Submit advanced search form
 */
function submitAdvancedSearchForm(searchEvent) {
    console.log("submit advanced search form");
    searchEvent.preventDefault();
    $.ajax("api/movies", {
        method: "GET",
        data: advancedsearch_form.serialize(),
        success: handleSearchSubmit
    });
}

function handleSearchSubmit(resultDataString) {
    console.log("success submit advanced search form");
    window.location.replace("index.html");
}

$.ajax("api/mainpage", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the forms to a event handler function
cart.submit(handleCartInfo);
// advancedsearch_form.submit(submitAdvancedSearchForm);