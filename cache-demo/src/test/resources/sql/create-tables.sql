/*
 * This sql file contains DML to create the tables that map to the demonstration schema.
 * This file must be loaded into the DB before testing the CommandLoadRegionFromDatabse
 * If running h2 then you get a sql window where you can paste this file via http://localhost:8082
 */
create table attribute (     pk varchar(255) primary key, name varchar(255));
create table activity_legal( pk varchar(255) primary key, id1 varchar(255), id2 varchar(255), activity_type varchar(255));
create table contact (       pk varchar(255) primary key, id1 varchar(255), first_name varchar(255), last_name varchar(255));
create table relationship (  pk varchar(255) primary key, id1 varchar(255), id2 varchar(255), relationship_type varchar (255));
create table transform(      pk varchar(255) primary key, id1 varchar(255), id2 varchar(255), transform_type varchar (255));

insert into ATTRIBUTE VALUES('key1','UR1 Financial');
insert into ATTRIBUTE VALUES('key1','Duplicat should fail'); /* should fail insert */
insert into ATTRIBUTE VALUES('key2','URGreat Financial');
insert into relationship VALUES('r1','key1','key2','siblings');
insert into activity_legal VALUES('al1','key1','key2','takeover');
insert into contact VALUES('contactkey1','key1','Bigdog','UR1Last');
insert into contact VALUES('contactkey2','key1','Littledog','UR1Last');
insert into contact VALUES('contactkey3','key2','Hotdog','UR2Last');
insert into contact VALUES('contactkey4','key2','Minidog','UR2Last');
