DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies(
id VARCHAR(10) NOT NULL default '',
title VARCHAR(100) NOT NULL default '',
year integer NOT NULL,
director VARCHAR(100) NOT NULL default '',
PRIMARY KEY (id)
);

CREATE TABLE stars(
id VARCHAR(10) default '',
name VARCHAR(100) not NULL,
birthYear integer,
PRIMARY KEY (id)
);

CREATE TABLE stars_in_movies(
starId VARCHAR(10) not NULL default '',
movieId VARCHAR(10) not NULL default '',
FOREIGN KEY (starId) REFERENCES stars (id) ON DELETE CASCADE,
FOREIGN KEY (movieId) REFERENCES movies (id) ON DELETE CASCADE
);

CREATE TABLE genres(
id integer not NULL AUTO_INCREMENT,
name VARCHAR(32) not NULL  default '',
primary key(id)
);

CREATE TABLE genres_in_movies(
genreId integer not NULL,
movieId varchar(10) not NULL default '',
foreign key (genreId) references genres (id) ON DELETE CASCADE,
foreign key (movieId) references movies (id) ON DELETE CASCADE
);

CREATE TABLE creditcards(
id varchar(20) not NULL default '',
firstName varchar(50) not NULL default '',
lastName varchar(50) not NUll default '',
expiration date not null,
primary key(id)
);
CREATE TABLE customers(
id integer not null auto_increment,
firstName varchar(50) not NULL default '',
lastName varchar(50) not NULL default '',
ccId varchar(20) not NULL default '',
address varchar(200) not NULL default '',
email varchar(50) not null default '',
password varchar(20) not null default '',
foreign key (ccId) references creditcards (id) ON DELETE CASCADE,
primary key(id)
);

CREATE TABLE sales(
id integer not null auto_increment,
customerId integer not null,
movieId varchar(10) not null default '',
saleDate date not null,
foreign key (customerId) references customers (id) ON DELETE CASCADE,
foreign key (movieId) references movies (id) ON DELETE CASCADE,
primary key(id)
);

CREATE TABLE ratings(
movieId varchar(10) not NULL default '', 
rating float not Null,
numVotes integer not null,
foreign key (movieId) references movies (id) ON DELETE CASCADE
);
