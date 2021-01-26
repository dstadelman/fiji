OPTIMIZE TABLE quotes;

SELECT strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545 FROM fiji.quotes;

SELECT quote_date, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545 FROM quotes WHERE option_type = 'P' AND delta_1545 < -.25 AND delta_1545 > -.35;

#CREATE INDEX `idx_quotes_quote_date`  ON `fiji`.`quotes` (quote_date) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
#CREATE INDEX `idx_quotes_expiration`  ON `fiji`.`quotes` (expiration) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
#CREATE INDEX `idx_quotes_option_type`  ON `fiji`.`quotes` (option_type) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
#CREATE INDEX `idx_quotes_strike`  ON `fiji`.`quotes` (strike) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;

SHOW INDEXES FROM quotes;

SELECT quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545 FROM quotes WHERE quote_date > "2020-12-10" AND expiration = "2021-02-19" AND option_type = 'P' AND strike = 2120;
