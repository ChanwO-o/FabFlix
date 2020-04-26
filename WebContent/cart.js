function handleCartResult(resultDataJson) {
    for (let i = 0; i < resultDataJson.length; i++) {
        grandTotal += resultDataJson[i]["movie_price"] * resultDataJson[i]["movie_quantity"]; // increment total price

        let rowHTML = "<td>" + resultDataJson[i]["movie_title"] + "</td>";
        // rowHTML += "<td>" + resultDataJson[i]["movie_year"] + "</td>";
        // rowHTML += "<td>" + resultDataJson[i]["movie_director"] + "</td>";
        // rowHTML += "<td>" + resultDataJson[i]["movie_genres"] + "</td>";
        // rowHTML += "<td>" + resultDataJson[i]["movie_stars"] + "</td>";
        // rowHTML += "<td>" + resultDataJson[i]["movie_rating"] + "</td>";
        rowHTML += "<td>$" + resultDataJson[i]["movie_price"] + "</td>";
        rowHTML += "<td><input name=\"quantity\" type=\"number\" value='" + resultDataJson[i]["movie_quantity"] + "'></td>";
        rowHTML += "<td><input name=\"update\" type=\"submit\" value=\"Update\"></td>";
        rowHTML += "<td><input name=\"remove\" type=\"submit\" value=\"Remove\"></td>";

        $.getJSON("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + resultDataJson[i]["movie_title"], function(json) {
            let posterPath = "";
            for (let i = 0; i < json.results.length ; ++i) {
                if (json.results[i].poster_path != null) {
                    posterPath = json.results[i].poster_path;
                    break;
                }
            }
            if (posterPath !== "")
                rowHTML = "<td><img src=\"http://image.tmdb.org/t/p/w500/" + posterPath + "\" width=100 height=100/></td>" + rowHTML;
            else
                rowHTML = "<td><img src=\"no_image.png\" width=100 height=100/></td>" + rowHTML ;
            rowHTML = "<tr>" + rowHTML + "</tr>"; // surround row with tr tags
            cartTableBodyElement.append(rowHTML); // add finished row to cart table
        });
    }
    let grandTotalHTML = "<p>" + "Total: $" + grandTotal + "</p>"; // display total price
    grandTotalElement.append(grandTotalHTML);
    // console.log("updated total: " + grandTotal);
}

let cartTableBodyElement = jQuery("#cart_table_body");
let grandTotalElement = jQuery("#grand_total");
let grandTotal = 0.00;

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
});
