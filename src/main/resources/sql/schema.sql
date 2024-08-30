create table brand
(
    id bigint primary key auto_increment,
    name varchar(255) not null
);

create table product
(
    id bigint primary key auto_increment,
    brand_id bigint not null,
    category varchar(50) not null,
    price int not null
);
