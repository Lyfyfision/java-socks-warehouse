CREATE TABLE socks
(
    id                SERIAL PRIMARY KEY,
    color             VARCHAR(50) NOT NULL,
    cotton_percentage INT         NOT NULL CHECK (cotton_percentage >= 0 AND cotton_percentage <= 100),
    quantity          INT         NOT NULL CHECK (quantity >= 0)
);
