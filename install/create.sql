create sequence article_gen;
create sequence news_gen;
create domain "boolean"
as smallint
check (value in (0, -1));
create table article
(
id integer not null,
contents varchar(1000) not null,
is_read "boolean" not null,
news integer,
constraint u_article_news unique (news),
constraint pk_article primary key (id)
);
create table net_node
(
id integer not null,
cur_news integer not null,
cert blob sub_type 0 not null,
host varchar(1000) not null,
port smallint not null,
constraint pk_net_node primary key (id),
constraint u_net_node_cert unique (cert),
constraint u_net_node_address unique (host, port)
);
