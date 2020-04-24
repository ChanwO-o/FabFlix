let movieResults = null;

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movies from resultData");
    movieResults = resultData;

    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData
    console.log('movieResults: ', movieResults);

    for (let i = 0; i < movieResults.length; i++)
    {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + movieResults[i]['movie_id'] + '">'
            + movieResults[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + movieResults[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + movieResults[i]["movie_director"] + "</th>";

        rowHTML += "<th>" + movieResults[i]["movie_genres"] + "</th>";
        // stars hyperlinks
        rowHTML += "<th>";
        var stars_array = movieResults[i]["movie_stars"].split(',');
        var stars_id_array=movieResults[i]["star_id"].split(',');

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

        rowHTML += "<th>" + movieResults[i]["movie_rating"] + "</th>";

        $.getJSON("https://api.themoviedb.org/3/search/movie?api_key=15d2ea6d0dc1d476efbca3eba2b9bbfb&query=" + movieResults[i]["movie_title"],function(json)
        {
            console.log('json:', json);
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
            else
                rowHTML="<th>"+"<img src="+'"'+"no_image.png"+ '"' + " width=" + "100 " +"height="+"100/>"+"</th>"+  rowHTML;
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
    movieResults.sort(function(obj1, obj2) {
        console.log('sorting title: ', obj1.movie_title, ' vs ', obj2.movie_title);
        return obj1.movie_title.localeCompare(obj2.movie_title);
    });
    console.log("after sort by title: ", movieResults);
    clearMovieListTable();
    handleMovieResult(movieResults);
}

function sortByTitleDescending() {
    movieResults.sort(function(obj1, obj2) {
        return obj2.movie_title.localeCompare(obj1.movie_title);
    });
    clearMovieListTable();
    handleMovieResult(movieResults);
}

function sortByRatingAscending() {
    movieResults.sort(function(obj1, obj2) {
        console.log('sorting rating: ', obj1.movie_rating, ' vs ', obj2.movie_rating);
        if (obj1.movie_rating == null)
            return -1;
        else if (obj2.movie_rating == null)
            return 1;
        return obj1.movie_rating - obj2.movie_rating;
    });
    console.log("after sort by rating: ", movieResults);
    clearMovieListTable();
    handleMovieResult(movieResults);
}

function sortByRatingDescending() {
    movieResults.sort(function(obj1, obj2) {
        if (obj2.movie_rating == null)
            return -1;
        else if (obj1.movie_rating == null)
            return 1;
        return obj2.movie_rating - obj1.movie_rating;
    });
    console.log("after sort by rating: ", movieResults);
    clearMovieListTable();
    handleMovieResult(movieResults);
}

function clearMovieListTable() {
    // remove all rows from table
    $("#movie_table_body tr").remove();
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
