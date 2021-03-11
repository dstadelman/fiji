-- OPTIMIZE TABLE quotes;

-- SELECT strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545 FROM fiji.quotes;

-- SELECT quote_date, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545 FROM quotes WHERE option_type = 'P' AND delta_1545 < -.25 AND delta_1545 > -.35;

-- CREATE INDEX `idx_quotes_quote_date`  ON `fiji`.`quotes` (quote_date) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_expiration`  ON `fiji`.`quotes` (expiration) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_option_type`  ON `fiji`.`quotes` (option_type) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_strike`  ON `fiji`.`quotes` (strike) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;
-- CREATE INDEX `idx_quotes_delta_1545`  ON `fiji`.`quotes` (delta_1545) COMMENT '' ALGORITHM DEFAULT LOCK DEFAULT;


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

SELECT quotesA.idquotes, quotesA.underlying_symbol, quotesA.quote_date, quotesA.root, quotesA.expiration, quotesA.strike, quotesA.option_type, quotesA.open, quotesA.high, quotesA.low, quotesA.close, quotesA.trade_volume, quotesA.bid_size_1545, quotesA.bid_1545, quotesA.ask_size_1545, quotesA.ask_1545, quotesA.underlying_bid_1545, quotesA.underlying_ask_1545, quotesA.implied_underlying_price_1545, quotesA.active_underlying_price_1545, quotesA.implied_volatility_1545, quotesA.delta_1545, quotesA.gamma_1545, quotesA.theta_1545, quotesA.vega_1545, quotesA.rho_1545, quotesA.bid_size_eod, quotesA.bid_eod, quotesA.ask_size_eod, quotesA.ask_eod, quotesA.underlying_bid_eod, quotesA.underlying_ask_eod, quotesA.vwap, quotesA.open_interest, quotesA.delivery_code, (quotesA.bid_1545 + quotesA.ask_1545) / 2 AS quotesA_mid_1545, (quotesA.underlying_bid_1545 + quotesA.underlying_ask_1545) / 2 AS quotesA_underlying_mid_1545, (quotesA.bid_eod + quotesA.ask_eod) / 2 AS quotesA_mid_eod, (quotesA.underlying_bid_eod + quotesA.underlying_ask_eod) / 2 AS quotesA_underlying_mid_eod, DATEDIFF(quotesA.expiration, quotesA.quote_date) / 2 AS quotesA_dte, quotesB.idquotes, quotesB.underlying_symbol, quotesB.quote_date, quotesB.root, quotesB.expiration, quotesB.strike, quotesB.option_type, quotesB.open, quotesB.high, quotesB.low, quotesB.close, quotesB.trade_volume, quotesB.bid_size_1545, quotesB.bid_1545, quotesB.ask_size_1545, quotesB.ask_1545, quotesB.underlying_bid_1545, quotesB.underlying_ask_1545, quotesB.implied_underlying_price_1545, quotesB.active_underlying_price_1545, quotesB.implied_volatility_1545, quotesB.delta_1545, quotesB.gamma_1545, quotesB.theta_1545, quotesB.vega_1545, quotesB.rho_1545, quotesB.bid_size_eod, quotesB.bid_eod, quotesB.ask_size_eod, quotesB.ask_eod, quotesB.underlying_bid_eod, quotesB.underlying_ask_eod, quotesB.vwap, quotesB.open_interest, quotesB.delivery_code, (quotesB.bid_1545 + quotesB.ask_1545) / 2 AS quotesB_mid_1545, (quotesB.underlying_bid_1545 + quotesB.underlying_ask_1545) / 2 AS quotesB_underlying_mid_1545, (quotesB.bid_eod + quotesB.ask_eod) / 2 AS quotesB_mid_eod, (quotesB.underlying_bid_eod + quotesB.underlying_ask_eod) / 2 AS quotesB_underlying_mid_eod, DATEDIFF(quotesB.expiration, quotesB.quote_date) / 2 AS quotesB_dte FROM quotes AS quotesA, quotes AS quotesB LIMIT 1;

SELECT quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545, DATEDIFF(expiration, quote_date) AS DTE, ABS(delta_1545 + .3) AS delta_30 FROM quotes
	WHERE quote_date >= "2020-01-01" AND quote_date <= "2020-12-01" AND -- LIMIT RESULTS
    DATEDIFF(expiration, quote_date) > 30 AND DATEDIFF(expiration, quote_date) < 50
    ORDER BY ABS(45 - DATEDIFF(expiration, quote_date)), ABS(delta_1545 + .3), quote_date

