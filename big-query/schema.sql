-- create a new dataset
create schema if not exists form_results;

-- create the results table
create or replace table form_results.results(
  favourite_food string not null,
  kotlin_interest_level string not null,
  comment string not null
);