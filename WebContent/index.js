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
        // stars hyperlinks
        rowHTML += "<th>";
        var stars_array = resultData[i]["movie_stars"].split(',');
        var stars_id_array=resultData[i]["star_id"].split(',');

        for (let j = 0; j < 3; ++j) {
            if(j==2)
            {
                rowHTML +=
                    '<a href="single-star.html?id=' + stars_id_array[j] + '">'  + stars_array[j]
                + '</a>';
            }
            else {
                rowHTML +=
                    '<a href="single-star.html?id=' + stars_id_array[j] + '">'
                    + stars_array[j] + ',' +   // display star_name for the link text
                    '</a>';
            }
        }
        rowHTML += "</th>";
        // rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


let queryString = window.location.href.split('?')[1];
console.log(queryString);
let params = queryString.split('&');
if (params.length > 0) {
    let title = params[0].substr(6);
    let year = params[1].substr(5);
    let director = params[2].substr(9);
    let star = params[3].substr(5);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movies",
        data: {
            title: title,
            year: year,
            director: director,
            star: star
        },
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
    });
}
else {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movies",
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
    });
}
