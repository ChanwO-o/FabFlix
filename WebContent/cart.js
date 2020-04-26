function handleCartResult(resultDataJson) {
    for (let i = 0; i < resultDataJson.length; i++) {
        let rowHTML = "<th>" + resultDataJson[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_genres"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_stars"] + "</th>";
        rowHTML += "<th>" + resultDataJson[i]["movie_rating"] + "</th>";
        rowHTML += "<th>$" + resultDataJson[i]["movie_price"] + "</th>";
        rowHTML += "<th><input name=\"quantity\" type=\"number\" value='" + resultDataJson[i]["movie_quantity"] + "'></th>";
        rowHTML += "<th><input name=\"update\" type=\"submit\" value=\"Update\"></th>";
        rowHTML += "<th><input name=\"remove\" type=\"submit\" value=\"Remove\"></th>";

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

let cartTableBodyElement = jQuery("#cart_table_body");
$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
});
