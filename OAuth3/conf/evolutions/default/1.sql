# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table tweet (
  id                        bigint not null,
  screen_name               varchar(255),
  text                      varchar(255),
  profile_image_url         varchar(255),
  constraint pk_tweet primary key (id))
;

create sequence tweet_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists tweet;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists tweet_seq;

