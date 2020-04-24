
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movies from resultData");

    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    console.log(resultData);
    for (let i = 0; i < resultData.length; i++)
    {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
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

        $.getJSON("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + resultData[i]["movie_title"],function(json)
        {
            //console.log(json.results[0].poster_path);
            let count =0;
            let k="";
            let p=0;
            for(p=0; p< json.results.length ; p++)
            {
                console.log(json.results[count]);
                if(json.results[p].poster_path !=null)
                {
                    count=1;
                    k += json.results[p].poster_path;
                    break;
                }
              //  count++;
            }
            if(count ==1)
                rowHTML="<th>"+"<img src="+ '"'+"http://image.tmdb.org/t/p/w500/" + k +'" '+ "width=" + "100 " +"height="+"100/>"+"</th>"+  rowHTML;
            else {
                rowHTML = "<th>" + "<img src=" + '"' + "no_image.png" + '"' + " width=" + "100 " + "height=" + "100/>" + "</th>" + rowHTML ;
            }
                rowHTML = "<tr>" + rowHTML + "</tr>"; // surround row with tr tags
            movieTableBodyElement.append(rowHTML);
        });

        // Append the row created to the table body, which will refresh the page
        // movieTableBodyElement.append(rowHTML);
    }
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

function sortByTitleAscending() {
    clearMovieListTable();
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movies" + window.location.search + "&sortby=title&sortorder=asc",
        success: (resultData) => handleMovieResult(resultData)
    });
}

function sortByTitleDescending() {
    clearMovieListTable();
    const queryString = window.location.search;
    console.log(queryString);
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movies" + window.location.search + "&sortby=title&sortorder=desc",
        success: (resultData) => handleMovieResult(resultData)
    });
}

function sortByRatingAscending() {
    clearMovieListTable();
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movies" + window.location.search + "&sortby=rating&sortorder=asc",
        success: (resultData) => handleMovieResult(resultData)
    });
}

function sortByRatingDescending() {
    clearMovieListTable();
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movies" + window.location.search + "&sortby=rating&sortorder=desc",
        success: (resultData) => handleMovieResult(resultData)
    });
}

function clearMovieListTable() {
    $("#movie_table_body tr").remove(); // remove all rows from table
}

let title_start=getParameterByName('title_start');
console.log(title_start);


if(title_start!=null && title_start.length >0)
{
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/movies?title_start=" + title_start,
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
    });
}
else
{
    let test = getParameterByName('genres');
    if(test!=null && test.length >1 )
    {
        jQuery.ajax({
            dataType: "json",  // Setting return data type
            method: "GET",// Setting request method
            url: "api/movies?genres=" + test,
            success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the singleMovieStar
        });
    }
    else {
        let queryString = window.location.href.split('?');
        console.log(queryString);

        if (queryString.length > 1) {

            let params = queryString[1].split('&');
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
            } else {
                jQuery.ajax({
                    dataType: "json", // Setting return data type
                    method: "GET", // Setting request method
                    url: "api/movies",
                    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
                });
            }
        } else {
            jQuery.ajax({
                dataType: "json", // Setting return data type
                method: "GET", // Setting request method
                url: "api/movies",
                success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
            });
        }
    }
}
