function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movies from resultData");

    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++)
    {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        /*let s = resultData[i]["movie_stars"];

        for (i = 0; i < 3; ++i) {
            rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_stars'] + '">' + resultData[i]["movie_stars"] + "</th>";
        }*/
        rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies",
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
});