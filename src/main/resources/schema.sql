create table if not exists posts(
id bigserial primary key,
title varchar(256) not null,
text varchar not null,
likes_count int default 0
);

create table if not exists tags(
post_id bigint references posts(id) on delete cascade,
tag varchar(256)
);

create table if not exists comments(
id bigserial primary key,
post_id bigint references posts(id) on delete cascade,
text varchar not null
);

create table if not exists images(
post_id bigint references posts(id) on delete cascade,
image bytea not null
);