-- auto-generated definition
create table Persons
(
    PersonID  int auto_increment
        primary key,
    LastName  varchar(255) null,
    FirstName varchar(255) null,
    Address   varchar(255) null,
    City      varchar(255) null,
    Age       int          not null
);