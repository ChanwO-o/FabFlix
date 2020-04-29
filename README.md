# FabFlix

### CS122b Spring 2020, Team 131

Fabflix is a full stack web application that displays information on movies and stars.

Watch the demos here:
* Project1: https://www.youtube.com/watch?v=ZovyHm_lWuY
* Project2: https://www.youtube.com/watch?v=_Wm3XJblF2s


## Built With

* [Amazon Web Services](https://aws.amazon.com/) - Cloud platform deployment
* [TomCat](https://tomcat.apache.org/) - Web server
* [Maven](https://maven.apache.org/) - Dependency Management
* [MySQL](https://www.mysql.com/) - Database and queries
* HTML, CSS, JavaScript - Frontend technologies


## Installation

1 ) Clone the git repository
```
git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-131.git
cd cs122b-spring20-team-131
```

2 ) Install dependencies
```
mvn package
```

3 ) Export .war file & deploy to Tomcat
```
mv cs122b-spring20-team131.war ~/path-to-your-tomcat-installation/webapps
```

4 ) View app on browser: visit localhost:8080 to view running app in your browser.


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


## Authors

* **Chan Woo Park** - *Frontend & cloud*
* **Sung Soo Kim** - *Backend database*

*Workload was equally distributed*