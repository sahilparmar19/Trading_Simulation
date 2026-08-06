-- ============================================================
--  Trading Simulation - PostgreSQL Full Export
--  Compatible with: PostgreSQL 12+
--  Database: tradingdb
--
--  HOW TO IMPORT (two options):
--
--  Option A — Create DB then import (Recommended):
--    psql -U postgres -c "CREATE DATABASE tradingdb;"
--    psql -U postgres -d tradingdb -f tradingdb.sql
--
--  Option B — One liner:
--    psql -U postgres -c "DROP DATABASE IF EXISTS tradingdb; CREATE DATABASE tradingdb;" && psql -U postgres -d tradingdb -f tradingdb.sql
--
--  Default login credentials in the app (config.properties):
--    db.url      = jdbc:postgresql://localhost:5432/tradingdb
--    db.user     = postgres
--    db.password = sahil
--
--  Seeded user accounts (username / password):
--    admin   / AdminPassword123!
--    alice   / AlicePassword123!
--    bob     / BobPassword123!
--    charlie / CharliePassword123!
-- ============================================================

-- Use UTC timezone for consistent timestamp behaviour
SET timezone = 'UTC';

-- Stop on first error so partial imports are caught
\set ON_ERROR_STOP 1

-- ============================================================
--  STEP 1: Drop old tables (safe, ordered by FK dependencies)
-- ============================================================
DROP TABLE IF EXISTS dividend_payments  CASCADE;
DROP TABLE IF EXISTS dividends          CASCADE;
DROP TABLE IF EXISTS ipo_applications   CASCADE;
DROP TABLE IF EXISTS ipos               CASCADE;
DROP TABLE IF EXISTS trades             CASCADE;
DROP TABLE IF EXISTS price_history      CASCADE;
DROP TABLE IF EXISTS watchlist          CASCADE;
DROP TABLE IF EXISTS portfolio          CASCADE;
DROP TABLE IF EXISTS stop_loss_orders   CASCADE;
DROP TABLE IF EXISTS orders             CASCADE;
DROP TABLE IF EXISTS users              CASCADE;
DROP TABLE IF EXISTS stocks             CASCADE;
DROP TABLE IF EXISTS sectors            CASCADE;

-- Drop old functions / triggers if they exist
DROP FUNCTION IF EXISTS get_user_portfolio_value(INT) CASCADE;
DROP FUNCTION IF EXISTS log_price_change()            CASCADE;
DROP FUNCTION IF EXISTS check_order_balance()         CASCADE;

-- ============================================================
--  STEP 2: Create Tables
-- ============================================================

-- Sectors
CREATE TABLE sectors (
    sector_id   SERIAL PRIMARY KEY,
    sector_name VARCHAR(100) NOT NULL UNIQUE
);

-- Stocks
CREATE TABLE stocks (
    ticker         VARCHAR(20)    PRIMARY KEY,
    company_name   VARCHAR(255)   NOT NULL,
    sector_id      INT            REFERENCES sectors(sector_id) ON DELETE SET NULL,
    current_price  NUMERIC(15,2)  NOT NULL,
    open_price     NUMERIC(15,2)  NOT NULL,
    prev_close     NUMERIC(15,2)  NOT NULL,
    market_cap     NUMERIC(20,2)  NOT NULL,
    pe_ratio       NUMERIC(10,2),
    pb_ratio       NUMERIC(10,2),
    roe            NUMERIC(5,4),
    roa            NUMERIC(5,4),
    total_shares   BIGINT         NOT NULL,
    promoter_hold  NUMERIC(5,2),
    inst_hold      NUMERIC(5,2),
    retail_hold    NUMERIC(5,2),
    is_listed      BOOLEAN        NOT NULL DEFAULT TRUE,
    exchange       VARCHAR(10)    DEFAULT 'NSE'
);

-- Users
-- NOTE: passwords are stored as plain text for simplicity (Semester 2 project).
CREATE TABLE users (
    user_id       SERIAL PRIMARY KEY,
    username      VARCHAR(50)   NOT NULL UNIQUE,
    password_hash VARCHAR(128)  NOT NULL,
    name          VARCHAR(100)  NOT NULL,
    balance       NUMERIC(15,2) NOT NULL DEFAULT 100000.00,
    is_admin      BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Orders
CREATE TABLE orders (
    order_id   SERIAL PRIMARY KEY,
    user_id    INT            REFERENCES users(user_id)  ON DELETE CASCADE,
    ticker     VARCHAR(20)    REFERENCES stocks(ticker)  ON DELETE CASCADE,
    is_buy     BOOLEAN        NOT NULL,
    order_type VARCHAR(20)    NOT NULL,
    price      NUMERIC(15,2)  NOT NULL,
    quantity   INT            NOT NULL,
    stop_price NUMERIC(15,2)  DEFAULT 0.0,
    status     VARCHAR(20)    NOT NULL,
    timestamp  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Portfolio
CREATE TABLE portfolio (
    user_id       INT            REFERENCES users(user_id)  ON DELETE CASCADE,
    ticker        VARCHAR(20)    REFERENCES stocks(ticker)  ON DELETE CASCADE,
    quantity      INT            NOT NULL,
    avg_buy_price NUMERIC(15,2)  NOT NULL,
    PRIMARY KEY (user_id, ticker)
);

-- Watchlist
CREATE TABLE watchlist (
    user_id INT         REFERENCES users(user_id)  ON DELETE CASCADE,
    ticker  VARCHAR(20) REFERENCES stocks(ticker)  ON DELETE CASCADE,
    PRIMARY KEY (user_id, ticker)
);

-- Price History
CREATE TABLE price_history (
    history_id  SERIAL PRIMARY KEY,
    ticker      VARCHAR(20)   REFERENCES stocks(ticker) ON DELETE CASCADE,
    price       NUMERIC(15,2) NOT NULL,
    recorded_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Trades
CREATE TABLE trades (
    trade_id      SERIAL PRIMARY KEY,
    buy_order_id  INT            REFERENCES orders(order_id),
    sell_order_id INT            REFERENCES orders(order_id),
    ticker        VARCHAR(20)    REFERENCES stocks(ticker) ON DELETE CASCADE,
    executed_price NUMERIC(15,2) NOT NULL,
    quantity      INT            NOT NULL,
    executed_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- IPOs
CREATE TABLE ipos (
    ipo_id           SERIAL PRIMARY KEY,
    ticker           VARCHAR(20)    REFERENCES stocks(ticker) ON DELETE CASCADE,
    company_name     VARCHAR(255)   NOT NULL,
    sector_id        INT            REFERENCES sectors(sector_id),
    ipo_price        NUMERIC(15,2)  NOT NULL,
    total_shares     BIGINT         NOT NULL,
    shares_remaining BIGINT         NOT NULL,
    open_time        TIMESTAMP      NOT NULL,
    close_time       TIMESTAMP      NOT NULL,
    status           VARCHAR(20)    NOT NULL
);

-- IPO Applications
CREATE TABLE ipo_applications (
    app_id      SERIAL PRIMARY KEY,
    ipo_id      INT REFERENCES ipos(ipo_id)  ON DELETE CASCADE,
    user_id     INT REFERENCES users(user_id) ON DELETE CASCADE,
    applied_qty INT NOT NULL,
    allotted_qty INT NOT NULL DEFAULT 0
);

-- Dividends
CREATE TABLE dividends (
    dividend_id      SERIAL PRIMARY KEY,
    ticker           VARCHAR(20)    REFERENCES stocks(ticker) ON DELETE CASCADE,
    amount_per_share NUMERIC(15,2)  NOT NULL,
    declared_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at          TIMESTAMP
);

-- Dividend Payments
CREATE TABLE IF NOT EXISTS dividend_payments (
    payment_id  SERIAL PRIMARY KEY,
    dividend_id INT            REFERENCES dividends(dividend_id) ON DELETE CASCADE,
    user_id     INT            REFERENCES users(user_id)         ON DELETE CASCADE,
    shares_held INT            NOT NULL,
    total_paid  NUMERIC(15,2)  NOT NULL,
    paid_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Stop Loss Orders
CREATE TABLE stop_loss_orders (
    sl_id      SERIAL PRIMARY KEY,
    user_id    INT            REFERENCES users(user_id)  ON DELETE CASCADE,
    ticker     VARCHAR(20)    REFERENCES stocks(ticker)  ON DELETE CASCADE,
    quantity   INT            NOT NULL,
    stop_price NUMERIC(15,2)  NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  STEP 3: Seed Data
-- ============================================================

-- ----- Sectors -----
INSERT INTO sectors (sector_id, sector_name) VALUES
(1, 'Energy'),
(2, 'IT'),
(3, 'Banking'),
(4, 'Automobile'),
(5, 'Pharmaceuticals');

-- Keep sequence in sync
SELECT setval('sectors_sector_id_seq', 5);

-- ----- Stocks (25 stocks, 5 per sector) -----
INSERT INTO stocks (ticker, company_name, sector_id, current_price, open_price, prev_close, market_cap,
                    pe_ratio, pb_ratio, roe, roa, total_shares, promoter_hold, inst_hold, retail_hold,
                    is_listed, exchange) VALUES
-- Sector 1: Energy
('RELIANCE',   'Reliance Industries Ltd.',       1, 2450.00, 2430.00, 2420.00, 16500000.00, 25.40, 2.10, 0.1100, 0.0600, 6700000, 50.00, 25.00, 25.00, TRUE, 'NSE'),
('ONGC',       'Oil & Natural Gas Corp Ltd.',    1,  165.50,  163.00,  162.00,  2080000.00,  5.20, 0.80, 0.1400, 0.0800,12500000, 60.00, 20.00, 20.00, TRUE, 'NSE'),
('NTPC',       'NTPC Limited',                   1,  210.00,  208.50,  207.00,  2030000.00, 12.10, 1.40, 0.1200, 0.0400, 9600000, 51.00, 29.00, 20.00, TRUE, 'NSE'),
('POWERGRID',  'Power Grid Corp of India Ltd.',  1,  245.00,  244.00,  243.50,  1700000.00, 11.50, 2.20, 0.1800, 0.0700, 6900000, 51.30, 28.70, 20.00, TRUE, 'NSE'),
('ADANIGREEN', 'Adani Green Energy Ltd.',        1,  950.00,  930.00,  920.00,  1500000.00,150.00,15.00, 0.1000, 0.0200, 1580000, 60.50, 24.50, 15.00, TRUE, 'NSE'),
-- Sector 2: IT
('TCS',    'Tata Consultancy Services Ltd.',2, 3300.00, 3280.00, 3270.00,12000000.00, 28.50, 8.40, 0.3500, 0.2500, 3600000, 72.30, 17.70, 10.00, TRUE, 'NSE'),
('INFY',   'Infosys Limited',              2, 1450.00, 1440.00, 1435.00, 6000000.00, 24.10, 6.20, 0.2900, 0.2000, 4100000, 15.10, 54.90, 30.00, TRUE, 'NSE'),
('WIPRO',  'Wipro Limited',               2,  385.00,  382.00,  381.00, 2100000.00, 18.20, 3.10, 0.1600, 0.1100, 5400000, 73.00, 17.00, 10.00, TRUE, 'NSE'),
('HCLTECH','HCL Technologies Ltd.',       2, 1150.00, 1140.00, 1135.00, 3100000.00, 20.80, 4.50, 0.2200, 0.1500, 2700000, 60.70, 29.30, 10.00, TRUE, 'NSE'),
('TECHM',  'Tech Mahindra Ltd.',          2, 1050.00, 1040.00, 1045.00, 1000000.00, 21.00, 3.80, 0.1500, 0.1000,  970000, 35.20, 44.80, 20.00, TRUE, 'NSE'),
-- Sector 3: Banking
('HDFCBANK', 'HDFC Bank Limited',        3, 1600.00, 1590.00, 1585.00,12000000.00, 18.50, 2.80, 0.1600, 0.0200, 7500000, 25.60, 54.40, 20.00, TRUE, 'NSE'),
('ICICIBANK','ICICI Bank Limited',       3,  950.00,  942.00,  940.00, 6600000.00, 17.20, 3.10, 0.1700, 0.0200, 6900000,  0.00, 75.20, 24.80, TRUE, 'NSE'),
('SBIN',     'State Bank of India',      3,  580.00,  575.00,  572.00, 5100000.00,  9.80, 1.50, 0.1500, 0.0100, 8900000, 57.60, 24.40, 18.00, TRUE, 'NSE'),
('AXISBANK', 'Axis Bank Limited',        3,  960.00,  952.00,  950.00, 2900000.00, 14.50, 2.20, 0.1400, 0.0100, 3000000,  8.20, 61.80, 30.00, TRUE, 'NSE'),
('KOTAKBANK','Kotak Mahindra Bank Ltd.', 3, 1850.00, 1835.00, 1830.00, 3600000.00, 22.10, 3.50, 0.1300, 0.0200, 1900000, 26.00, 54.00, 20.00, TRUE, 'NSE'),
-- Sector 4: Automobile
('TATAMOTORS','Tata Motors Ltd.',        4,  620.00,  612.00,  610.00, 2200000.00, 15.60, 3.20, 0.1800, 0.0500, 3500000, 46.40, 28.60, 25.00, TRUE, 'NSE'),
('MARUTI',    'Maruti Suzuki India Ltd.',4, 9500.00, 9450.00, 9400.00, 2800000.00, 28.20, 4.20, 0.1400, 0.1000,  300000, 56.40, 23.60, 20.00, TRUE, 'NSE'),
('M&M',       'Mahindra & Mahindra Ltd.',4,1550.00, 1540.00, 1530.00, 1900000.00, 19.10, 3.50, 0.1700, 0.0700, 1200000, 19.30, 50.70, 30.00, TRUE, 'NSE'),
('BAJAJ-AUTO','Bajaj Auto Limited',      4, 4700.00, 4680.00, 4650.00, 1300000.00, 20.40, 5.20, 0.2400, 0.1800,  280000, 54.80, 25.20, 20.00, TRUE, 'NSE'),
('HEROCO',    'Hero MotoCorp Limited',   4, 3100.00, 3080.00, 3075.00,  620000.00, 18.00, 3.90, 0.2000, 0.1400,  200000, 34.80, 45.20, 20.00, TRUE, 'NSE'),
-- Sector 5: Pharmaceuticals
('SUNPHARMA','Sun Pharmaceutical Industries Ltd.',5, 1150.00, 1140.00, 1135.00, 2700000.00, 29.50, 4.10, 0.1500, 0.1000, 2400000, 54.50, 25.50, 20.00, TRUE, 'NSE'),
('CIPLA',    'Cipla Limited',                     5, 1200.00, 1190.00, 1185.00,  960000.00, 26.20, 3.80, 0.1400, 0.0900,  800000, 33.60, 41.40, 25.00, TRUE, 'NSE'),
('REDDY',    'Dr. Reddys Laboratories Ltd.',      5, 5200.00, 5170.00, 5150.00,  860000.00, 18.40, 3.20, 0.1600, 0.1100,  160000, 26.70, 53.30, 20.00, TRUE, 'NSE'),
('DIVISLAB', 'Divis Laboratories Ltd.',            5, 3600.00, 3580.00, 3570.00,  950000.00, 32.10, 7.20, 0.2000, 0.1500,  260000, 51.90, 28.10, 20.00, TRUE, 'NSE'),
('BIOCON',   'Biocon Limited',                    5,  260.00,  258.00,  257.00,  310000.00, 35.50, 2.40, 0.0700, 0.0300, 1200000, 60.60, 19.40, 20.00, TRUE, 'NSE');

-- ----- Users -----
-- Passwords are stored as plain text (Semester 2 project — no hashing).
-- Signup validation (enforced in AuthManager.validatePassword):
--   min 8 chars | 1 uppercase | 1 lowercase | 1 digit | 1 special char
--
--   admin   / AdminPassword123!
--   alice   / AlicePassword123!
--   bob     / BobPassword123!
--   charlie / CharliePassword123!
INSERT INTO users (user_id, username, password_hash, name, balance, is_admin, created_at) VALUES
(1, 'admin',   'AdminPassword123!',   'System Administrator', 1000000.00, TRUE,  NOW() - INTERVAL '10 days'),
(2, 'alice',   'AlicePassword123!',   'Alice Sharma',          150000.00, FALSE, NOW() - INTERVAL '5 days'),
(3, 'bob',     'BobPassword123!',     'Bob Patel',              85000.00, FALSE, NOW() - INTERVAL '4 days'),
(4, 'charlie', 'CharliePassword123!', 'Charlie Sen',           200000.00, FALSE, NOW() - INTERVAL '3 days');

-- Keep sequence in sync with hardcoded IDs
SELECT setval('users_user_id_seq', 4);

-- ----- Portfolio -----
INSERT INTO portfolio (user_id, ticker, quantity, avg_buy_price) VALUES
(2, 'RELIANCE',   10, 2400.00),
(2, 'TCS',         5, 3250.00),
(3, 'HDFCBANK',   20, 1570.00),
(4, 'TATAMOTORS', 50,  600.00),
(4, 'SUNPHARMA',  15, 1120.00);

-- ----- Price History (14 data points per stock, ~52 week coverage) -----

-- Sector 1: Energy
INSERT INTO price_history (ticker, price, recorded_at) VALUES
('RELIANCE', 2180.00, NOW() - INTERVAL '52 weeks'), ('RELIANCE', 2220.00, NOW() - INTERVAL '26 weeks'),
('RELIANCE', 2250.00, NOW() - INTERVAL '6 months'),  ('RELIANCE', 2320.00, NOW() - INTERVAL '1 month'),
('RELIANCE', 2350.00, NOW() - INTERVAL '3 weeks'),   ('RELIANCE', 2370.00, NOW() - INTERVAL '2 weeks'),
('RELIANCE', 2390.00, NOW() - INTERVAL '1 week'),    ('RELIANCE', 2400.00, NOW() - INTERVAL '6 days'),
('RELIANCE', 2410.00, NOW() - INTERVAL '5 days'),    ('RELIANCE', 2415.00, NOW() - INTERVAL '4 days'),
('RELIANCE', 2425.00, NOW() - INTERVAL '3 days'),    ('RELIANCE', 2430.00, NOW() - INTERVAL '2 days'),
('RELIANCE', 2420.00, NOW() - INTERVAL '1 day'),     ('RELIANCE', 2450.00, NOW()),

('ONGC', 140.00, NOW() - INTERVAL '52 weeks'), ('ONGC', 145.00, NOW() - INTERVAL '26 weeks'),
('ONGC', 148.00, NOW() - INTERVAL '6 months'),  ('ONGC', 155.00, NOW() - INTERVAL '1 month'),
('ONGC', 158.00, NOW() - INTERVAL '3 weeks'),   ('ONGC', 159.50, NOW() - INTERVAL '2 weeks'),
('ONGC', 161.00, NOW() - INTERVAL '1 week'),    ('ONGC', 162.00, NOW() - INTERVAL '6 days'),
('ONGC', 162.50, NOW() - INTERVAL '5 days'),    ('ONGC', 163.00, NOW() - INTERVAL '4 days'),
('ONGC', 163.50, NOW() - INTERVAL '3 days'),    ('ONGC', 164.00, NOW() - INTERVAL '2 days'),
('ONGC', 162.00, NOW() - INTERVAL '1 day'),     ('ONGC', 165.50, NOW()),

('NTPC', 175.00, NOW() - INTERVAL '52 weeks'), ('NTPC', 180.00, NOW() - INTERVAL '26 weeks'),
('NTPC', 185.00, NOW() - INTERVAL '6 months'),  ('NTPC', 195.00, NOW() - INTERVAL '1 month'),
('NTPC', 198.00, NOW() - INTERVAL '3 weeks'),   ('NTPC', 200.00, NOW() - INTERVAL '2 weeks'),
('NTPC', 203.00, NOW() - INTERVAL '1 week'),    ('NTPC', 204.50, NOW() - INTERVAL '6 days'),
('NTPC', 205.00, NOW() - INTERVAL '5 days'),    ('NTPC', 206.50, NOW() - INTERVAL '4 days'),
('NTPC', 207.00, NOW() - INTERVAL '3 days'),    ('NTPC', 208.00, NOW() - INTERVAL '2 days'),
('NTPC', 207.00, NOW() - INTERVAL '1 day'),     ('NTPC', 210.00, NOW()),

('POWERGRID', 210.00, NOW() - INTERVAL '52 weeks'), ('POWERGRID', 215.00, NOW() - INTERVAL '26 weeks'),
('POWERGRID', 220.00, NOW() - INTERVAL '6 months'),  ('POWERGRID', 232.00, NOW() - INTERVAL '1 month'),
('POWERGRID', 235.00, NOW() - INTERVAL '3 weeks'),   ('POWERGRID', 237.00, NOW() - INTERVAL '2 weeks'),
('POWERGRID', 239.00, NOW() - INTERVAL '1 week'),    ('POWERGRID', 240.00, NOW() - INTERVAL '6 days'),
('POWERGRID', 241.00, NOW() - INTERVAL '5 days'),    ('POWERGRID', 242.00, NOW() - INTERVAL '4 days'),
('POWERGRID', 242.50, NOW() - INTERVAL '3 days'),    ('POWERGRID', 243.00, NOW() - INTERVAL '2 days'),
('POWERGRID', 243.50, NOW() - INTERVAL '1 day'),     ('POWERGRID', 245.00, NOW()),

('ADANIGREEN', 750.00, NOW() - INTERVAL '52 weeks'), ('ADANIGREEN', 780.00, NOW() - INTERVAL '26 weeks'),
('ADANIGREEN', 810.00, NOW() - INTERVAL '6 months'),  ('ADANIGREEN', 870.00, NOW() - INTERVAL '1 month'),
('ADANIGREEN', 890.00, NOW() - INTERVAL '3 weeks'),   ('ADANIGREEN', 900.00, NOW() - INTERVAL '2 weeks'),
('ADANIGREEN', 915.00, NOW() - INTERVAL '1 week'),    ('ADANIGREEN', 920.00, NOW() - INTERVAL '6 days'),
('ADANIGREEN', 925.00, NOW() - INTERVAL '5 days'),    ('ADANIGREEN', 930.00, NOW() - INTERVAL '4 days'),
('ADANIGREEN', 935.00, NOW() - INTERVAL '3 days'),    ('ADANIGREEN', 940.00, NOW() - INTERVAL '2 days'),
('ADANIGREEN', 920.00, NOW() - INTERVAL '1 day'),     ('ADANIGREEN', 950.00, NOW()),

-- Sector 2: IT
('TCS', 2900.00, NOW() - INTERVAL '52 weeks'), ('TCS', 2980.00, NOW() - INTERVAL '26 weeks'),
('TCS', 3050.00, NOW() - INTERVAL '6 months'),  ('TCS', 3180.00, NOW() - INTERVAL '1 month'),
('TCS', 3200.00, NOW() - INTERVAL '3 weeks'),   ('TCS', 3220.00, NOW() - INTERVAL '2 weeks'),
('TCS', 3250.00, NOW() - INTERVAL '1 week'),    ('TCS', 3260.00, NOW() - INTERVAL '6 days'),
('TCS', 3265.00, NOW() - INTERVAL '5 days'),    ('TCS', 3270.00, NOW() - INTERVAL '4 days'),
('TCS', 3275.00, NOW() - INTERVAL '3 days'),    ('TCS', 3280.00, NOW() - INTERVAL '2 days'),
('TCS', 3270.00, NOW() - INTERVAL '1 day'),     ('TCS', 3300.00, NOW()),

('INFY', 1250.00, NOW() - INTERVAL '52 weeks'), ('INFY', 1280.00, NOW() - INTERVAL '26 weeks'),
('INFY', 1310.00, NOW() - INTERVAL '6 months'),  ('INFY', 1380.00, NOW() - INTERVAL '1 month'),
('INFY', 1400.00, NOW() - INTERVAL '3 weeks'),   ('INFY', 1410.00, NOW() - INTERVAL '2 weeks'),
('INFY', 1420.00, NOW() - INTERVAL '1 week'),    ('INFY', 1425.00, NOW() - INTERVAL '6 days'),
('INFY', 1430.00, NOW() - INTERVAL '5 days'),    ('INFY', 1432.00, NOW() - INTERVAL '4 days'),
('INFY', 1435.00, NOW() - INTERVAL '3 days'),    ('INFY', 1438.00, NOW() - INTERVAL '2 days'),
('INFY', 1435.00, NOW() - INTERVAL '1 day'),     ('INFY', 1450.00, NOW()),

('WIPRO', 340.00, NOW() - INTERVAL '52 weeks'), ('WIPRO', 348.00, NOW() - INTERVAL '26 weeks'),
('WIPRO', 355.00, NOW() - INTERVAL '6 months'),  ('WIPRO', 370.00, NOW() - INTERVAL '1 month'),
('WIPRO', 374.00, NOW() - INTERVAL '3 weeks'),   ('WIPRO', 376.00, NOW() - INTERVAL '2 weeks'),
('WIPRO', 378.00, NOW() - INTERVAL '1 week'),    ('WIPRO', 379.00, NOW() - INTERVAL '6 days'),
('WIPRO', 380.00, NOW() - INTERVAL '5 days'),    ('WIPRO', 381.00, NOW() - INTERVAL '4 days'),
('WIPRO', 382.00, NOW() - INTERVAL '3 days'),    ('WIPRO', 383.00, NOW() - INTERVAL '2 days'),
('WIPRO', 381.00, NOW() - INTERVAL '1 day'),     ('WIPRO', 385.00, NOW()),

('HCLTECH', 980.00, NOW() - INTERVAL '52 weeks'),  ('HCLTECH', 1000.00, NOW() - INTERVAL '26 weeks'),
('HCLTECH', 1030.00, NOW() - INTERVAL '6 months'), ('HCLTECH', 1090.00, NOW() - INTERVAL '1 month'),
('HCLTECH', 1100.00, NOW() - INTERVAL '3 weeks'),  ('HCLTECH', 1110.00, NOW() - INTERVAL '2 weeks'),
('HCLTECH', 1120.00, NOW() - INTERVAL '1 week'),   ('HCLTECH', 1125.00, NOW() - INTERVAL '6 days'),
('HCLTECH', 1130.00, NOW() - INTERVAL '5 days'),   ('HCLTECH', 1135.00, NOW() - INTERVAL '4 days'),
('HCLTECH', 1138.00, NOW() - INTERVAL '3 days'),   ('HCLTECH', 1140.00, NOW() - INTERVAL '2 days'),
('HCLTECH', 1135.00, NOW() - INTERVAL '1 day'),    ('HCLTECH', 1150.00, NOW()),

('TECHM', 920.00, NOW() - INTERVAL '52 weeks'),  ('TECHM', 940.00, NOW() - INTERVAL '26 weeks'),
('TECHM', 960.00, NOW() - INTERVAL '6 months'),  ('TECHM', 1010.00, NOW() - INTERVAL '1 month'),
('TECHM', 1020.00, NOW() - INTERVAL '3 weeks'),  ('TECHM', 1025.00, NOW() - INTERVAL '2 weeks'),
('TECHM', 1030.00, NOW() - INTERVAL '1 week'),   ('TECHM', 1035.00, NOW() - INTERVAL '6 days'),
('TECHM', 1038.00, NOW() - INTERVAL '5 days'),   ('TECHM', 1040.00, NOW() - INTERVAL '4 days'),
('TECHM', 1042.00, NOW() - INTERVAL '3 days'),   ('TECHM', 1045.00, NOW() - INTERVAL '2 days'),
('TECHM', 1045.00, NOW() - INTERVAL '1 day'),    ('TECHM', 1050.00, NOW()),

-- Sector 3: Banking
('HDFCBANK', 1380.00, NOW() - INTERVAL '52 weeks'), ('HDFCBANK', 1420.00, NOW() - INTERVAL '26 weeks'),
('HDFCBANK', 1450.00, NOW() - INTERVAL '6 months'), ('HDFCBANK', 1530.00, NOW() - INTERVAL '1 month'),
('HDFCBANK', 1550.00, NOW() - INTERVAL '3 weeks'),  ('HDFCBANK', 1560.00, NOW() - INTERVAL '2 weeks'),
('HDFCBANK', 1570.00, NOW() - INTERVAL '1 week'),   ('HDFCBANK', 1575.00, NOW() - INTERVAL '6 days'),
('HDFCBANK', 1578.00, NOW() - INTERVAL '5 days'),   ('HDFCBANK', 1580.00, NOW() - INTERVAL '4 days'),
('HDFCBANK', 1585.00, NOW() - INTERVAL '3 days'),   ('HDFCBANK', 1590.00, NOW() - INTERVAL '2 days'),
('HDFCBANK', 1585.00, NOW() - INTERVAL '1 day'),    ('HDFCBANK', 1600.00, NOW()),

('ICICIBANK', 810.00, NOW() - INTERVAL '52 weeks'), ('ICICIBANK', 830.00, NOW() - INTERVAL '26 weeks'),
('ICICIBANK', 855.00, NOW() - INTERVAL '6 months'), ('ICICIBANK', 910.00, NOW() - INTERVAL '1 month'),
('ICICIBANK', 920.00, NOW() - INTERVAL '3 weeks'),  ('ICICIBANK', 925.00, NOW() - INTERVAL '2 weeks'),
('ICICIBANK', 932.00, NOW() - INTERVAL '1 week'),   ('ICICIBANK', 935.00, NOW() - INTERVAL '6 days'),
('ICICIBANK', 937.00, NOW() - INTERVAL '5 days'),   ('ICICIBANK', 938.00, NOW() - INTERVAL '4 days'),
('ICICIBANK', 940.00, NOW() - INTERVAL '3 days'),   ('ICICIBANK', 942.00, NOW() - INTERVAL '2 days'),
('ICICIBANK', 940.00, NOW() - INTERVAL '1 day'),    ('ICICIBANK', 950.00, NOW()),

('SBIN', 480.00, NOW() - INTERVAL '52 weeks'), ('SBIN', 495.00, NOW() - INTERVAL '26 weeks'),
('SBIN', 510.00, NOW() - INTERVAL '6 months'), ('SBIN', 548.00, NOW() - INTERVAL '1 month'),
('SBIN', 555.00, NOW() - INTERVAL '3 weeks'),  ('SBIN', 560.00, NOW() - INTERVAL '2 weeks'),
('SBIN', 565.00, NOW() - INTERVAL '1 week'),   ('SBIN', 568.00, NOW() - INTERVAL '6 days'),
('SBIN', 570.00, NOW() - INTERVAL '5 days'),   ('SBIN', 571.00, NOW() - INTERVAL '4 days'),
('SBIN', 573.00, NOW() - INTERVAL '3 days'),   ('SBIN', 575.00, NOW() - INTERVAL '2 days'),
('SBIN', 572.00, NOW() - INTERVAL '1 day'),    ('SBIN', 580.00, NOW()),

('AXISBANK', 820.00, NOW() - INTERVAL '52 weeks'), ('AXISBANK', 840.00, NOW() - INTERVAL '26 weeks'),
('AXISBANK', 860.00, NOW() - INTERVAL '6 months'), ('AXISBANK', 920.00, NOW() - INTERVAL '1 month'),
('AXISBANK', 930.00, NOW() - INTERVAL '3 weeks'),  ('AXISBANK', 935.00, NOW() - INTERVAL '2 weeks'),
('AXISBANK', 940.00, NOW() - INTERVAL '1 week'),   ('AXISBANK', 943.00, NOW() - INTERVAL '6 days'),
('AXISBANK', 945.00, NOW() - INTERVAL '5 days'),   ('AXISBANK', 948.00, NOW() - INTERVAL '4 days'),
('AXISBANK', 950.00, NOW() - INTERVAL '3 days'),   ('AXISBANK', 952.00, NOW() - INTERVAL '2 days'),
('AXISBANK', 950.00, NOW() - INTERVAL '1 day'),    ('AXISBANK', 960.00, NOW()),

('KOTAKBANK', 1600.00, NOW() - INTERVAL '52 weeks'), ('KOTAKBANK', 1640.00, NOW() - INTERVAL '26 weeks'),
('KOTAKBANK', 1680.00, NOW() - INTERVAL '6 months'), ('KOTAKBANK', 1770.00, NOW() - INTERVAL '1 month'),
('KOTAKBANK', 1790.00, NOW() - INTERVAL '3 weeks'),  ('KOTAKBANK', 1800.00, NOW() - INTERVAL '2 weeks'),
('KOTAKBANK', 1815.00, NOW() - INTERVAL '1 week'),   ('KOTAKBANK', 1820.00, NOW() - INTERVAL '6 days'),
('KOTAKBANK', 1825.00, NOW() - INTERVAL '5 days'),   ('KOTAKBANK', 1830.00, NOW() - INTERVAL '4 days'),
('KOTAKBANK', 1833.00, NOW() - INTERVAL '3 days'),   ('KOTAKBANK', 1835.00, NOW() - INTERVAL '2 days'),
('KOTAKBANK', 1830.00, NOW() - INTERVAL '1 day'),    ('KOTAKBANK', 1850.00, NOW()),

-- Sector 4: Automobile
('TATAMOTORS', 510.00, NOW() - INTERVAL '52 weeks'), ('TATAMOTORS', 530.00, NOW() - INTERVAL '26 weeks'),
('TATAMOTORS', 550.00, NOW() - INTERVAL '6 months'), ('TATAMOTORS', 590.00, NOW() - INTERVAL '1 month'),
('TATAMOTORS', 598.00, NOW() - INTERVAL '3 weeks'),  ('TATAMOTORS', 602.00, NOW() - INTERVAL '2 weeks'),
('TATAMOTORS', 608.00, NOW() - INTERVAL '1 week'),   ('TATAMOTORS', 610.00, NOW() - INTERVAL '6 days'),
('TATAMOTORS', 612.00, NOW() - INTERVAL '5 days'),   ('TATAMOTORS', 614.00, NOW() - INTERVAL '4 days'),
('TATAMOTORS', 615.00, NOW() - INTERVAL '3 days'),   ('TATAMOTORS', 616.00, NOW() - INTERVAL '2 days'),
('TATAMOTORS', 610.00, NOW() - INTERVAL '1 day'),    ('TATAMOTORS', 620.00, NOW()),

('MARUTI', 8200.00, NOW() - INTERVAL '52 weeks'), ('MARUTI', 8400.00, NOW() - INTERVAL '26 weeks'),
('MARUTI', 8600.00, NOW() - INTERVAL '6 months'), ('MARUTI', 9100.00, NOW() - INTERVAL '1 month'),
('MARUTI', 9200.00, NOW() - INTERVAL '3 weeks'),  ('MARUTI', 9250.00, NOW() - INTERVAL '2 weeks'),
('MARUTI', 9320.00, NOW() - INTERVAL '1 week'),   ('MARUTI', 9350.00, NOW() - INTERVAL '6 days'),
('MARUTI', 9380.00, NOW() - INTERVAL '5 days'),   ('MARUTI', 9400.00, NOW() - INTERVAL '4 days'),
('MARUTI', 9420.00, NOW() - INTERVAL '3 days'),   ('MARUTI', 9440.00, NOW() - INTERVAL '2 days'),
('MARUTI', 9400.00, NOW() - INTERVAL '1 day'),    ('MARUTI', 9500.00, NOW()),

('M&M', 1320.00, NOW() - INTERVAL '52 weeks'), ('M&M', 1360.00, NOW() - INTERVAL '26 weeks'),
('M&M', 1400.00, NOW() - INTERVAL '6 months'), ('M&M', 1480.00, NOW() - INTERVAL '1 month'),
('M&M', 1500.00, NOW() - INTERVAL '3 weeks'),  ('M&M', 1510.00, NOW() - INTERVAL '2 weeks'),
('M&M', 1520.00, NOW() - INTERVAL '1 week'),   ('M&M', 1525.00, NOW() - INTERVAL '6 days'),
('M&M', 1528.00, NOW() - INTERVAL '5 days'),   ('M&M', 1530.00, NOW() - INTERVAL '4 days'),
('M&M', 1535.00, NOW() - INTERVAL '3 days'),   ('M&M', 1540.00, NOW() - INTERVAL '2 days'),
('M&M', 1530.00, NOW() - INTERVAL '1 day'),    ('M&M', 1550.00, NOW()),

('BAJAJ-AUTO', 4100.00, NOW() - INTERVAL '52 weeks'), ('BAJAJ-AUTO', 4200.00, NOW() - INTERVAL '26 weeks'),
('BAJAJ-AUTO', 4300.00, NOW() - INTERVAL '6 months'), ('BAJAJ-AUTO', 4520.00, NOW() - INTERVAL '1 month'),
('BAJAJ-AUTO', 4560.00, NOW() - INTERVAL '3 weeks'),  ('BAJAJ-AUTO', 4590.00, NOW() - INTERVAL '2 weeks'),
('BAJAJ-AUTO', 4620.00, NOW() - INTERVAL '1 week'),   ('BAJAJ-AUTO', 4640.00, NOW() - INTERVAL '6 days'),
('BAJAJ-AUTO', 4650.00, NOW() - INTERVAL '5 days'),   ('BAJAJ-AUTO', 4660.00, NOW() - INTERVAL '4 days'),
('BAJAJ-AUTO', 4670.00, NOW() - INTERVAL '3 days'),   ('BAJAJ-AUTO', 4680.00, NOW() - INTERVAL '2 days'),
('BAJAJ-AUTO', 4650.00, NOW() - INTERVAL '1 day'),    ('BAJAJ-AUTO', 4700.00, NOW()),

('HEROCO', 2700.00, NOW() - INTERVAL '52 weeks'), ('HEROCO', 2760.00, NOW() - INTERVAL '26 weeks'),
('HEROCO', 2820.00, NOW() - INTERVAL '6 months'), ('HEROCO', 2980.00, NOW() - INTERVAL '1 month'),
('HEROCO', 3000.00, NOW() - INTERVAL '3 weeks'),  ('HEROCO', 3020.00, NOW() - INTERVAL '2 weeks'),
('HEROCO', 3040.00, NOW() - INTERVAL '1 week'),   ('HEROCO', 3050.00, NOW() - INTERVAL '6 days'),
('HEROCO', 3060.00, NOW() - INTERVAL '5 days'),   ('HEROCO', 3065.00, NOW() - INTERVAL '4 days'),
('HEROCO', 3070.00, NOW() - INTERVAL '3 days'),   ('HEROCO', 3075.00, NOW() - INTERVAL '2 days'),
('HEROCO', 3075.00, NOW() - INTERVAL '1 day'),    ('HEROCO', 3100.00, NOW()),

-- Sector 5: Pharmaceuticals
('SUNPHARMA', 980.00, NOW() - INTERVAL '52 weeks'),  ('SUNPHARMA', 1000.00, NOW() - INTERVAL '26 weeks'),
('SUNPHARMA', 1030.00, NOW() - INTERVAL '6 months'), ('SUNPHARMA', 1090.00, NOW() - INTERVAL '1 month'),
('SUNPHARMA', 1100.00, NOW() - INTERVAL '3 weeks'),  ('SUNPHARMA', 1110.00, NOW() - INTERVAL '2 weeks'),
('SUNPHARMA', 1120.00, NOW() - INTERVAL '1 week'),   ('SUNPHARMA', 1125.00, NOW() - INTERVAL '6 days'),
('SUNPHARMA', 1128.00, NOW() - INTERVAL '5 days'),   ('SUNPHARMA', 1130.00, NOW() - INTERVAL '4 days'),
('SUNPHARMA', 1135.00, NOW() - INTERVAL '3 days'),   ('SUNPHARMA', 1138.00, NOW() - INTERVAL '2 days'),
('SUNPHARMA', 1135.00, NOW() - INTERVAL '1 day'),    ('SUNPHARMA', 1150.00, NOW()),

('CIPLA', 1020.00, NOW() - INTERVAL '52 weeks'), ('CIPLA', 1050.00, NOW() - INTERVAL '26 weeks'),
('CIPLA', 1080.00, NOW() - INTERVAL '6 months'), ('CIPLA', 1140.00, NOW() - INTERVAL '1 month'),
('CIPLA', 1155.00, NOW() - INTERVAL '3 weeks'),  ('CIPLA', 1165.00, NOW() - INTERVAL '2 weeks'),
('CIPLA', 1175.00, NOW() - INTERVAL '1 week'),   ('CIPLA', 1178.00, NOW() - INTERVAL '6 days'),
('CIPLA', 1180.00, NOW() - INTERVAL '5 days'),   ('CIPLA', 1183.00, NOW() - INTERVAL '4 days'),
('CIPLA', 1185.00, NOW() - INTERVAL '3 days'),   ('CIPLA', 1188.00, NOW() - INTERVAL '2 days'),
('CIPLA', 1185.00, NOW() - INTERVAL '1 day'),    ('CIPLA', 1200.00, NOW()),

('REDDY', 4500.00, NOW() - INTERVAL '52 weeks'), ('REDDY', 4600.00, NOW() - INTERVAL '26 weeks'),
('REDDY', 4700.00, NOW() - INTERVAL '6 months'), ('REDDY', 4950.00, NOW() - INTERVAL '1 month'),
('REDDY', 5000.00, NOW() - INTERVAL '3 weeks'),  ('REDDY', 5050.00, NOW() - INTERVAL '2 weeks'),
('REDDY', 5100.00, NOW() - INTERVAL '1 week'),   ('REDDY', 5120.00, NOW() - INTERVAL '6 days'),
('REDDY', 5130.00, NOW() - INTERVAL '5 days'),   ('REDDY', 5140.00, NOW() - INTERVAL '4 days'),
('REDDY', 5150.00, NOW() - INTERVAL '3 days'),   ('REDDY', 5160.00, NOW() - INTERVAL '2 days'),
('REDDY', 5150.00, NOW() - INTERVAL '1 day'),    ('REDDY', 5200.00, NOW()),

('DIVISLAB', 3100.00, NOW() - INTERVAL '52 weeks'), ('DIVISLAB', 3180.00, NOW() - INTERVAL '26 weeks'),
('DIVISLAB', 3260.00, NOW() - INTERVAL '6 months'), ('DIVISLAB', 3440.00, NOW() - INTERVAL '1 month'),
('DIVISLAB', 3480.00, NOW() - INTERVAL '3 weeks'),  ('DIVISLAB', 3510.00, NOW() - INTERVAL '2 weeks'),
('DIVISLAB', 3540.00, NOW() - INTERVAL '1 week'),   ('DIVISLAB', 3550.00, NOW() - INTERVAL '6 days'),
('DIVISLAB', 3560.00, NOW() - INTERVAL '5 days'),   ('DIVISLAB', 3570.00, NOW() - INTERVAL '4 days'),
('DIVISLAB', 3575.00, NOW() - INTERVAL '3 days'),   ('DIVISLAB', 3580.00, NOW() - INTERVAL '2 days'),
('DIVISLAB', 3570.00, NOW() - INTERVAL '1 day'),    ('DIVISLAB', 3600.00, NOW()),

('BIOCON', 220.00, NOW() - INTERVAL '52 weeks'), ('BIOCON', 225.00, NOW() - INTERVAL '26 weeks'),
('BIOCON', 230.00, NOW() - INTERVAL '6 months'), ('BIOCON', 245.00, NOW() - INTERVAL '1 month'),
('BIOCON', 248.00, NOW() - INTERVAL '3 weeks'),  ('BIOCON', 250.00, NOW() - INTERVAL '2 weeks'),
('BIOCON', 253.00, NOW() - INTERVAL '1 week'),   ('BIOCON', 254.00, NOW() - INTERVAL '6 days'),
('BIOCON', 255.00, NOW() - INTERVAL '5 days'),   ('BIOCON', 256.00, NOW() - INTERVAL '4 days'),
('BIOCON', 257.00, NOW() - INTERVAL '3 days'),   ('BIOCON', 258.00, NOW() - INTERVAL '2 days'),
('BIOCON', 257.00, NOW() - INTERVAL '1 day'),    ('BIOCON', 260.00, NOW());

-- ============================================================
--  STEP 4: Functions, Procedures, and Triggers
-- ============================================================

-- Function: get_user_portfolio_value
CREATE OR REPLACE FUNCTION get_user_portfolio_value(p_user_id INT)
RETURNS NUMERIC AS $$
DECLARE
    total_value NUMERIC(20, 2) := 0.00;
BEGIN
    SELECT COALESCE(SUM(p.quantity * s.current_price), 0.00)
    INTO total_value
    FROM portfolio p
    JOIN stocks s ON p.ticker = s.ticker
    WHERE p.user_id = p_user_id;

    RETURN total_value;
END;
$$ LANGUAGE plpgsql;

-- Procedure: process_trade
CREATE OR REPLACE PROCEDURE process_trade(
    p_buy_order_id  INT,
    p_sell_order_id INT,
    p_executed_price NUMERIC,
    p_quantity      INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_buyer_id      INT;
    v_seller_id     INT;
    v_ticker        VARCHAR(20);
    v_total_cost    NUMERIC(15, 2);
    v_buyer_balance NUMERIC(15, 2);
    v_buyer_qty     INT;
BEGIN
    v_total_cost := p_executed_price * p_quantity;

    SELECT user_id, ticker INTO v_buyer_id, v_ticker FROM orders WHERE order_id = p_buy_order_id;
    SELECT user_id          INTO v_seller_id            FROM orders WHERE order_id = p_sell_order_id;

    SELECT balance INTO v_buyer_balance FROM users WHERE user_id = v_buyer_id FOR UPDATE;
    IF v_buyer_balance < v_total_cost THEN
        RAISE EXCEPTION 'Buyer has insufficient funds. Balance: %, Cost: %', v_buyer_balance, v_total_cost;
    END IF;

    UPDATE users SET balance = balance - v_total_cost WHERE user_id = v_buyer_id;
    UPDATE users SET balance = balance + v_total_cost WHERE user_id = v_seller_id;

    SELECT quantity INTO v_buyer_qty FROM portfolio WHERE user_id = v_buyer_id AND ticker = v_ticker;
    IF FOUND THEN
        UPDATE portfolio
        SET quantity      = quantity + p_quantity,
            avg_buy_price = ((quantity * avg_buy_price) + v_total_cost) / (quantity + p_quantity)
        WHERE user_id = v_buyer_id AND ticker = v_ticker;
    ELSE
        INSERT INTO portfolio (user_id, ticker, quantity, avg_buy_price)
        VALUES (v_buyer_id, v_ticker, p_quantity, p_executed_price);
    END IF;

    UPDATE portfolio SET quantity = quantity - p_quantity WHERE user_id = v_seller_id AND ticker = v_ticker;
    DELETE FROM portfolio WHERE user_id = v_seller_id AND ticker = v_ticker AND quantity <= 0;

    INSERT INTO trades (buy_order_id, sell_order_id, ticker, executed_price, quantity)
    VALUES (p_buy_order_id, p_sell_order_id, v_ticker, p_executed_price, p_quantity);

    UPDATE orders
    SET quantity = quantity - p_quantity,
        status   = CASE WHEN quantity - p_quantity <= 0 THEN 'MATCHED' ELSE 'PENDING' END
    WHERE order_id IN (p_buy_order_id, p_sell_order_id);
END;
$$;

-- Trigger Function: log_price_change
CREATE OR REPLACE FUNCTION log_price_change()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.current_price <> OLD.current_price THEN
        INSERT INTO price_history (ticker, price)
        VALUES (NEW.ticker, NEW.current_price);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger: trg_record_price_history
DROP TRIGGER IF EXISTS trg_record_price_history ON stocks;
CREATE TRIGGER trg_record_price_history
AFTER UPDATE OF current_price ON stocks
FOR EACH ROW
EXECUTE FUNCTION log_price_change();

-- Trigger Function: check_order_balance
CREATE OR REPLACE FUNCTION check_order_balance()
RETURNS TRIGGER AS $$
DECLARE
    v_balance NUMERIC(15, 2);
    v_cost    NUMERIC(15, 2);
BEGIN
    IF NEW.is_buy = TRUE THEN
        SELECT balance INTO v_balance FROM users WHERE user_id = NEW.user_id;
        v_cost := NEW.price * NEW.quantity;
        IF v_balance < v_cost THEN
            RAISE EXCEPTION 'Insufficient balance to place buy order. Required: %, Available: %', v_cost, v_balance;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger: trg_check_balance_before_order
DROP TRIGGER IF EXISTS trg_check_balance_before_order ON orders;
CREATE TRIGGER trg_check_balance_before_order
BEFORE INSERT ON orders
FOR EACH ROW
EXECUTE FUNCTION check_order_balance();

-- ============================================================
--  DONE
--  Verify with: SELECT COUNT(*) FROM stocks;   -- should be 25
--               SELECT COUNT(*) FROM sectors;  -- should be 5
--               SELECT COUNT(*) FROM users;    -- should be 4
-- ============================================================
