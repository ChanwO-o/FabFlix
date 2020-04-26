
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

function handleCartResult(resultDataJson) {
    for (let i = 0; i < resultDataJson.length; i++) {
        let rowHTML = "<th>" + resultDataJson[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_genres"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_stars"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_rating"] + "</th>";

        $.getJSON("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + resultDataJson[i]["movie_title"], function(json) {
            let posterPath = "";
            for (let i = 0; i < json.results.length ; ++i) {
                if (json.results[i].poster_path != null) {
                    posterPath = json.results[i].poster_path;
                    break;
                }
            }
            if (posterPath !== "")
                rowHTML = "<th><img src=\"http://image.tmdb.org/t/p/w500/" + posterPath + "\" width=100 height=100/></th>" + rowHTML;
            else
                rowHTML = "<th><img src=\"no_image.png\" width=100 height=100/></th>" + rowHTML ;
            rowHTML = "<tr>" + rowHTML + "</tr>"; // surround row with tr tags
            cartTableBodyElement.append(rowHTML); // add finished row to cart table
        });
    }
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

    $.ajax("api/index", {
        method: "POST",
        data: cart.serialize(),
        success: handleCartArray
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

let cartTableBodyElement = jQuery("#cart_table_body");
$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
});
