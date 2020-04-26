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
function handleResult(resultData) {
    console.log(resultData[0]['genre_name']);
    let starInfoElement = jQuery("#genre_list");
    let rowHTML = "";
    for (let i = 0; i < resultData.length; i++)
    {
        console.log(resultData[i]['genre_name']);
        rowHTML += '<a href="movielist.html?genres=' + resultData[i]['genre_name']  + '">' + resultData[i]['genre_name']+ " | "

            +'</a>';
        if(i%4==0 && i!=0)
            rowHTML += "<br>"
    }
    starInfoElement.append(rowHTML);
   // $("#movie_info").html("<a href=\"html_images.asp\">HTML bbb</a>");
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    cache: true,
    url: "api/mainpage",
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
});