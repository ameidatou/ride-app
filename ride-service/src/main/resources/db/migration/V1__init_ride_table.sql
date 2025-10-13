CREATE TABLE ride (
    id SERIAL PRIMARY KEY,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    pickup_time TIMESTAMP NOT NULL,
    user_id BIGINT,
    driver_id BIGINT,
    status VARCHAR(50)
);
