-- DROP PROCEDURE IF EXISTS add_movie;
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE add_movie(
	IN _id varchar(10),
	IN _title varchar(100),
	IN _year int(11),
	IN _director varchar(100),
	IN _name varchar(25),
	IN _star_id varchar(100),
	IN _genre varchar(50)
)
add_movie:BEGIN
	if _title in (select title from movies) then
		select -1;
		leave add_movie;
	end if;
	
	if _title = '' then
		select -2;
		leave add_movie;
	end if;
	
	if _year = '' then
		select -2;
		leave add_movie;
	end if;
	
	if _director = '' then
		select -2;
		leave add_movie;
	end if;
	
	insert into movies(
		id,
		title,
		year,
		director
	)
	values
	(
		_id,
		_title,
		_year,
		_director
	);
	
	if _genre != '' then
		if _genre not in (select name from genres) then
			insert into genres (name) values (_genre);
		end if;
		
		insert into genres_in_movies
		(
			genreId,
			movieId
		)
		select
			(select id from genres where name = _genre limit 1) as genreId,
			(select id from movies where title = _title limit 1) as movieId;
	end if;
	
	if  _name != '' then
		if (_name) not in (select name from stars) then
			insert into stars (id,name) values (_star_id,_name);
		end if;
		
		insert into stars_in_movies
		(
			starId,
			movieId
		)
		select
			(select id from stars where name=_name limit 1) as starId,
			(select id from movies where title = _title limit 1) as movieId;
	end if;
	select 1;

END$$

DELIMITER ;