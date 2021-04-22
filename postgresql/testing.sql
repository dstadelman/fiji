-- OPTIMIZE TABLE quotes;

-- SELECT strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545 FROM fiji.quotes;

-- SELECT quote_date, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545 FROM quotes WHERE option_type = 'P' AND delta_1545 < -.25 AND delta_1545 > -.35;

-- CREATE INDEX `idx_quotes_quote_date`  ON `fiji`.`quotes` (quote_date) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_expiration`  ON `fiji`.`quotes` (expiration) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_option_type`  ON `fiji`.`quotes` (option_type) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_strike`  ON `fiji`.`quotes` (strike) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_delta_1545`  ON `fiji`.`quotes` (delta_1545) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_underlying_symbol`  ON `fiji`.`quotes` (underlying_symbol) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_underlying_dte`  ON `fiji`.`quotes` ((DATEDIFF(`expiration`, `quote_date`))) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;

-- CREATE INDEX `idx_quotes_expiration_underlying_symbol`  ON `fiji`.`quotes` (expiration, underlying_symbol) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT	OK	0.000 sec
-- CREATE INDEX `idx_quotes_underlying_symbol_quote_date_expiration_delta_1545`  ON `fiji`.`quotes` (underlying_symbol, quote_date, expiration, delta_1545) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT
-- CREATE INDEX `idx_quotes_underlying_symbol_expiration_option_type_strike`  ON `fiji`.`quotes` (underlying_symbol, expiration, option_type, strike) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT



-- SHOW INDEXES FROM quotes;

-- SELECT quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545 FROM quotes WHERE quote_date > "2020-12-10" AND expiration = "2021-02-19" AND option_type = 'P' AND strike = 2120;

-- SELECT quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545, DATEDIFF(expiration, quote_date) AS DTE, ABS(delta_1545 + .3) AS delta_30 FROM quotes
-- 	WHERE DATEDIFF(expiration, quote_date) > 30 AND DATEDIFF(expiration, quote_date) < 50
-- 		AND root = "RUTW" AND expiration = "2020-12-31" AND strike = "1550"
-- 		AND delta_1545 > -.4 AND delta_1545 < -.2 
-- 		;
        
-- SELECT * FROM (
-- 	SELECT quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545, DATEDIFF(expiration, quote_date) AS DTE, ABS(delta_1545 + .3) AS delta_30 FROM quotes
-- 		WHERE DATEDIFF(expiration, quote_date) > 30 AND DATEDIFF(expiration, quote_date) < 50
-- 			AND quote_date = "2020-11-16" AND root = "RUTW" AND expiration = "2020-12-31"
-- 			AND delta_1545 > -.4 AND delta_1545 < -.2 
-- 		ORDER BY ABS(45 - DATEDIFF(expiration, quote_date)), ABS(delta_1545 + .3), quote_date
-- ) as sub
-- ;
        
-- SELECT quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545, DATEDIFF(expiration, quote_date) AS DTE, ABS(delta_1545 + .3) AS delta_30 FROM quotes
-- 	WHERE quote_date >= "2020-01-01" AND quote_date <= "2020-12-01" AND -- LIMIT RESULTS
--     DATEDIFF(expiration, quote_date) > 30 AND DATEDIFF(expiration, quote_date) < 50
--     AND delta_1545 > -.4 AND delta_1545 < -.2
--     GROUP BY root, expiration
--     ORDER BY ABS(45 - DATEDIFF(expiration, quote_date)), ABS(delta_1545 + .3), quote_date
--     ;

