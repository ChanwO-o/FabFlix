
# FabFlix

### CS122b Spring 2020, Team 131

Fabflix is a full stack web application that displays information on movies and stars.

Watch the demos here:
* Project1: https://www.youtube.com/watch?v=ZovyHm_lWuY
* Project2: https://www.youtube.com/watch?v=_Wm3XJblF2s
* Project3 (part 1): https://youtu.be/zswZRMRpi0A
* Project3 (part 2): https://www.youtube.com/watch?v=oLcK7vvRGkI
* Project4: https://www.youtube.com/watch?v=dgpF1f1_ezs


## Built With

* [Amazon Web Services](https://aws.amazon.com/) - Cloud platform deployment
* [TomCat](https://tomcat.apache.org/) - Web server
* [Maven](https://maven.apache.org/) - Dependency Management
* [MySQL](https://www.mysql.com/) - Database and queries
* HTML, CSS, JavaScript - Frontend technologies


## Installation - web

1 ) Clone the git repository
```
git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131.git
cd cs122b-spring20-team-131
```

2 ) Install dependencies
```
mvn clean package
```

3 ) Export .war file & deploy to Tomcat
```
mv cs122b-spring20-team131.war ~/path-to-your-tomcat-installation/webapps
```

4 ) View app on browser: visit localhost:8080 to view running app in your browser.


## Installation - Android

1 ) Transfer .apk file to your physical device

2 ) Launch .apk file. Give permission to install from external sources


## Substring matching design

When searching for movies, users have the following options for performing advanced search queries
(Database queries are called in **MovieListServlet.java**):

Substrings are passed through url parameters. One default behavior of url parameters is that it treats spaces as '+' characters.
So first we must replace these back to spaces:
```
if(title!=null)
    title= title.replace('+',' ');
```
Then we can query for substrings using the **'LIKE'** keyword in SQL.

* title starts with character
```
 ..and movies.title like 'A%';
```
* title includes string
```
 ..and movies.title like '%A%';
```
* director/star includes string
```
 ..and movies.director like '%cam%';
```


## Prepared Statements
Prepared Statements are used to read/write to the database.
Links to files that use Prepared Statements:
* [https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/MainPageServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/MainPageServlet.java)
* https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/MovieListServlet.java
* https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/LoginServlet.java
* https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/SingleStarServlet.java
* https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/SingleMovieServlet.java
* https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/src/PaymentServlet.java


## Inconsistency Data
Inconsistently parsed XML data is logged into files in (name-of-xml-file).txt format.
Example: 
[https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/inconsistencies-mains243.xml.txt](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131/blob/master/inconsistencies-mains243.xml.txt)



## Optimization Strategies
* #### Set conditions for existing (duplicate) data
e.g. Treat 'Drama' and 'drama ' as the same genre
We defined a Genre class where the .equals() and hashcode() methods were overridden to return the same values for similar names.
```
@Override
public boolean equals(Object o) {
	if (o == this)
		return true;
	if (!(o instanceof Genre))
		return false;
	Genre g = (Genre) o;
	// treat same-spelled names as the same genre
	return g.getName().toLowerCase().equals(getName().toLowerCase());
}

public int hashCode() {
    return name.toLowerCase().hashCode();
}
```
Then, adding to the Set() of Genre objects will automatically negate duplicate adds.

* #### Organize data into HashMap
HashMaps are great for searching data in O(1) time. When parsing cast.xml, data is organized by movies to stars, in a 1 : n ratio. So, the data can be placed as such:
```
Map<String movieTitle, List<Star> stars>
```



## Authors & Contribution

* **Chan Woo Park** - *Frontend, Cloud, Tomcat, Android*
* **Sung Soo Kim** - *Backend database queries, Servlets, Full-text search*


*Workload was equally distributed (pair-programming)*
*Notice to TA: Sung Soo Kim had to leave back to his home country due to COVID-19. He did not have access to an environment for the week. *
