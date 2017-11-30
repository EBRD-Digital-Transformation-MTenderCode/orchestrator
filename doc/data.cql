CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.submission_rules (
    country text,
    method text,
    parameter text,
    value text,
    primary key(country, method, parameter)
);

INSERT INTO ocds.submission_rules (country, method, parameter, value)  VALUES ('UA', 'micro', 'interval', '15');

CREATE TABLE IF NOT EXISTS  ocds.submission_period (
    oc_id text,
    start_date timestamp,
    end_date timestamp,
    primary key(oc_id)
);

CREATE TABLE IF NOT EXISTS  ocds.submission_bid (
    oc_id text,
    bid_id UUID,
    json_data text,
    bid_status text,
    primary key(oc_id, bid_id)
)WITH CLUSTERING ORDER BY (bid_id DESC);