create table if not exists posts(
id bigserial primary key,
title varchar(256) not null,
description varchar not null,
likes_count int default 0,
image bytea
);

create table if not exists tags(
post_id bigint references posts(id) on delete cascade,
tag varchar(256)
);

create table if not exists comments(
id bigserial primary key,
post_id bigint references posts(id) on delete cascade,
description varchar not null
);