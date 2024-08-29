create table brand
(
    id bigint primary key not null,
    name varchar(255) not null
);

create table product
(
    id bigint primary key not null,
    brand_id bigint not null,
    category varchar(50) not null,
    price int not null
);
