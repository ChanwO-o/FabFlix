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

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>star name: " + resultData[0]["star_name"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["star_name"] + "</th>";
    rowHTML += "<th>" + resultData[0]["star_dob"] + "</th>";
    rowHTML += "<th>" + resultData[0]["star_movies"] + "</th>";
    // stars hyperlinks
    rowHTML += "<th>";

    var stars_array = resultData[0]["star_movies"].split(',');
    for (let i = 0; i < stars_array.length; ++i)
    {
        if(i==stars_array.length-1)
        {
            rowHTML +=
                '<a href="single-movie.html?name=' + stars_array[i] + '">'  + stars_array[i]
                + '</a>';
        }
        else {
            rowHTML +=
                '<a href="single-movie.html?name=' + stars_array[i] + '">'
                + stars_array[i] + ',' +   // display star_name for the link text
                '</a>';
        }
    }
    rowHTML += "</th>";

    // Append the row created to the table body, which will refresh the page
    starTableBodyElement.append(rowHTML);

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let star_id = getParameterByName('id');

console.log(star_id);
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + star_id,
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
});