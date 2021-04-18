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


SELECT `expiration` 
FROM quotes 
WHERE 	DATEDIFF(`expiration`, '2004-01-02') > 55							-- first date of data
	AND `expiration` < "2021-01-15"											-- last date of data
GROUP BY `expiration`;


-- SELECT * FROM (

SELECT quotesPut.quote_date AS quote_date_put, quotesPut.underlying_symbol AS underlying_symbol_put, quotesPut.expiration expiration_put, quotesPut.strike AS strike_put, quotesPut.active_underlying_price_1545 AS active_underlying_price_1545_put, quotesPut.option_type AS option_type_put, quotesPut.delta_1545 AS delta_1545_put
	, DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) AS PutDTE
	, quotesCall.quote_date AS quote_date_call, quotesCall.underlying_symbol AS underlying_symbol_call, quotesCall.expiration AS expiration_call, quotesCall.strike AS strike_call, quotesCall.active_underlying_price_1545 AS active_underlying_price_1545_call, quotesCall.option_type AS option_type_call, quotesCall.delta_1545 AS delta_1545_call
	, DATEDIFF(`quotesCall`.`expiration`, `quotesCall`.`quote_date`) AS CallDTE
-- 	, RANK() OVER (PARTITION BY `quotesPut`.`expiration` ORDER BY 
-- 		ABS(DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) - 45), ABS(`quotesPut`.`delta_1545` - -.3) + ABS(`quotesCall`.`delta_1545` - .3)
-- 	) AS dte_delta_1545_rank
	,	((ABS(`quotesPut`.`delta_1545` - -.3) + ABS(`quotesCall`.`delta_1545` - .3)) / .05) + ABS(DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) - 45) AS dte_delta_1545_rank
FROM quotes AS quotesPut, quotes AS quotesCall
WHERE   `quotesPut`.`quote_date` = `quotesCall`.`quote_date`
	AND `quotesPut`.`expiration` = `quotesCall`.`expiration`
    AND ((ABS(`quotesPut`.`delta_1545` - -.3) + ABS(`quotesCall`.`delta_1545` - .3)) / .05) + ABS(DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) - 45) < 5
	AND DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) > 35				-- make query faster
	AND DATEDIFF(`quotesPut`.`expiration`, `quotesPut`.`quote_date`) < 55				-- make query faster
	AND `quotesPut`.`delta_1545` < -.3 + .2 AND `quotesPut`.`delta_1545` > -.3 - .2		-- make query faster
	AND `quotesCall`.`delta_1545` < .3 + .2 AND `quotesCall`.`delta_1545` > .3 - .2		-- make query faster
	-- AND expiration >= "2020-01-03" AND expiration <= "2020-12-31" -- LIMIT RESULTS
	-- AND `quotesPut`.`expiration` < "2010-02-21"  -- LIMIT RESULTS
	AND `quotesPut`.`expiration` = '2007-01-20'
ORDER BY dte_delta_1545_rank
LIMIT 1

-- ) sub WHERE dte_delta_1545_rank = 1 ORDER BY `quote_date_put`, `quote_date_put`
