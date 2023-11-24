-- create a new dataset
create schema if not exists form_results;

-- create the results table
create or replace table form_results.results(
  favourite_food string not null,
  kotlin_interest_level string not null,
  comment string not null,
  sentiment string not null,
  submission_time timestamp default current_timestamp() not null
);

-- create the result aggregation view
create or replace view form_results.aggregated_results as (
    select
        sum(case when favourite_food = 'BURGER' then 1 else 0 end) / count(favourite_food) as burger_pct,
        sum(case when favourite_food = 'PIZZA' then 1 else 0 end) /count(favourite_food) as pizza_pct,
        sum(case when kotlin_interest_level = 'HIGH' then 1 else 0 end) / count(kotlin_interest_level) as high_interest_pct,
        sum(case when kotlin_interest_level = 'MEDIUM' then 1 else 0 end) / count(kotlin_interest_level) as medium_interest_pct,
        sum(case when kotlin_interest_level = 'LOW' then 1 else 0 end) / count(kotlin_interest_level) as low_interest_pct,
        sum(case when sentiment = 'POSITIVE' then 1 else 0 end) / count(sentiment) as positive_sentiment_pct,
        sum(case when sentiment = 'NEGATIVE' then 1 else 0 end) / count(sentiment) as negative_sentiment_pct,
    from
        `form_results.results`
)

