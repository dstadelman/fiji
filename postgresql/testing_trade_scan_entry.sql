## SHORT_PUT
SELECT * FROM (
SELECT idquotes, quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545
	, DATEDIFF(expiration, quote_date) AS DTE
	, RANK() OVER (PARTITION BY expiration ORDER BY 
		ABS(DATEDIFF(expiration, quote_date) - 45)
		, ABS(delta_1545 - -.3)
	) AS expiration_rank
	FROM quotes
	WHERE DATEDIFF(expiration, "2004-01-08") > 45 AND expiration < "2021-01-15"
		AND option_type = 'P'
		AND delta_1545 <> 0
        AND DATEDIFF(expiration, quote_date) > 35
        AND DATEDIFF(expiration, quote_date) < 55
		-- AND expiration >= "2020-01-03" AND expiration <= "2020-12-31" -- LIMIT RESULTS
) sub
WHERE expiration_rank = 1
ORDER BY quote_date;

## STRANGLE
SELECT * FROM (
SELECT idquotes, quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545
	, DATEDIFF(expiration, quote_date) AS DTE
	, RANK() OVER (PARTITION BY expiration ORDER BY 
		root
		, ABS(DATEDIFF(expiration, quote_date) - 45)
		, ABS(delta_1545 - -.3)
	) AS expiration_rank_delta_low
	, RANK() OVER (PARTITION BY expiration ORDER BY 
		root
        , ABS(DATEDIFF(expiration, quote_date) - 45)
		, ABS(delta_1545 - .3)
	) AS expiration_rank_delta_high
	FROM quotes
	WHERE `expiration` > DATE_ADD("2004-01-08", INTERVAL 45 DAY) 
		AND expiration < "2021-01-15"
		AND DATEDIFF(expiration, quote_date) > 35
        AND DATEDIFF(expiration, quote_date) < 55
        AND delta_1545 <> 0
		AND expiration >= "2020-01-03" AND expiration <= "2020-12-31" -- LIMIT RESULTS
) sub
WHERE expiration_rank_delta_low = 1 OR expiration_rank_delta_high = 1
ORDER BY quote_date, option_type;

## IRON_CONDOR
-- not complete
SELECT
	quotesA.idquotes AS idquotesA, quotesB.idquotes AS idquotesB,
	quotesA.quote_date, quotesA.underlying_symbol, 
	quotesA.expiration AS expirationA, 
	quotesB.expiration AS expirationB, 
	quotesA.strike AS strikeA, quotesB.strike AS strikeB, 
	quotesA.active_underlying_price_1545, 
    quotesA.option_type, 
	quotesA.bid_1545   AS bid_1545A,   quotesA.ask_1545   AS ask_1545A,   quotesB.bid_1545   AS bid_1545B,   quotesB.ask_1545   AS ask_1545B, 
	quotesA.delta_1545 AS delta_1545A, quotesA.theta_1545 AS theta_1545A, quotesB.delta_1545 AS delta_1545B, quotesB.theta_1545 AS theta_1545B
	, DATEDIFF(quotesA.expiration, quotesA.quote_date) AS DTE
    , (quotesA.bid_1545 + quotesA.ask_1545) / 2 AS mid_priceA
    , (quotesB.bid_1545 + quotesB.ask_1545) / 2 AS mid_priceB
    , ABS(((quotesA.bid_1545 + quotesA.ask_1545) / 2) - ((quotesB.bid_1545 + quotesB.ask_1545) / 2)) AS mid_price_diff
    , quotesA.strike - quotesB.strike AS strike_diff
	FROM quotes AS quotesA, quotes AS quotesB
    WHERE 
		quotesA.underlying_symbol = quotesB.underlying_symbol AND quotesA.expiration = quotesB.expiration AND quotesA.option_type = quotesB.option_type AND quotesA.strike - quotesB.strike > 0
        AND quotesA.strike - quotesB.strike < 100
        AND ((quotesA.option_type = 'C' AND quotesA.strike > quotesA.active_underlying_price_1545 AND quotesB.strike > quotesA.active_underlying_price_1545) OR (quotesA.option_type = 'P' AND quotesA.strike < quotesA.active_underlying_price_1545 AND quotesB.strike < quotesA.active_underlying_price_1545))
		AND quotesA.quote_date = "2020-01-02" AND quotesA.expiration = "2020-02-14" -- LIMIT RESULTS
	ORDER BY 
		ABS( (ABS(((quotesA.bid_1545 + quotesA.ask_1545) / 2) - ((quotesB.bid_1545 + quotesB.ask_1545) / 2)) / (quotesA.strike - quotesB.strike)) - (1/3) )
    ;
-- 	WHERE DATEDIFF(expiration, "2004-01-08") > 45 AND expiration < "2021-01-15"
-- 		AND DATEDIFF(expiration, quote_date) > 35
--         AND delta_1545 <> 0
-- 		AND expiration >= "2020-01-03" AND expiration <= "2020-12-31" -- LIMIT RESULTS

-- WHERE expiration_rank_delta_low = 1 OR expiration_rank_delta_high = 1
-- ORDER BY expiration;
