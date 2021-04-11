

-- # idquotes, quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545, DTE, expiration_rank
-- '9063625', '2018-12-04', 'RUT', '2019-01-18', '1420', '1480.6', 'P', '22.8', '23.6', '-0.2885', '-0.4399', '45', '1'

-- SHORT_PUT exit query
SELECT idquotes, quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545
	, DATEDIFF(expiration, quote_date) AS DTE
    , (bid_1545 + ask_1545) / 2 AS current
	FROM quotes
	WHERE quote_date 		> '2018-12-04'
		AND root 			= 'RUT'
        AND expiration 		= '2019-01-18'
        AND strike 			= '1420'
        AND option_type 	= 'P'
        AND ( 
			(bid_1545 + ask_1545) / 2 > 2 * (22.8 + 23.6) / 2
            OR 
            DATEDIFF(expiration, quote_date) < 21
        )
	ORDER BY quote_date;

-- # idquotes, quote_date, root, expiration, strike, active_underlying_price_1545, option_type, bid_1545, ask_1545, delta_1545, theta_1545, DTE, expiration_rank_delta_low, expiration_rank_delta_high
-- '7057106', '2017-10-26', 'RUTW', '2017-12-08', '1530', '1497.82', 'C', '12.5', '13.3', '0.3183', '-0.2785', '43', '89', '1'
-- '7057093', '2017-10-26', 'RUTW', '2017-12-08', '1460', '1497.82', 'P', '14.7', '15.5', '-0.3', '-0.2983', '43', '1', '92'

-- STRANGLE exit query
SELECT quotesA.idquotes AS idquotesA
	, quotesB.idquotes AS idquotesB
	, quotesA.quote_date
    , quotesA.active_underlying_price_1545 AS underlying_price
    , quotesA.root
    , quotesA.expiration
    , quotesA.option_type AS option_typeA
    , quotesA.strike AS strikeA
    , quotesB.option_type AS option_typeB
    , quotesB.strike AS strikeB    
    , quotesA.bid_1545 AS bid_1545A 
    , quotesA.ask_1545 AS ask_1545A
	, quotesB.bid_1545 AS bid_1545B
    , quotesB.ask_1545 AS ask_1545B    
--     , quotesA.delta_1545
--     , quotesA.theta_1545
	, DATEDIFF(quotesA.expiration, quotesA.quote_date) AS DTEA
    , ((14.7 + 15.5) / 2) + ((12.5 + 13.3) / 2) AS origin
    , ((quotesA.bid_1545 + quotesA.ask_1545) / 2) + ((quotesB.bid_1545 + quotesB.ask_1545) / 2) AS current
	FROM quotes AS quotesA, quotes AS quotesB
	WHERE quotesA.quote_date = quotesB.quote_date
		AND quotesA.quote_date 		> '2017-10-26'
        
		AND quotesA.root 			= 'RUTW'
        AND quotesA.expiration 		= '2017-12-08'
        AND quotesA.strike 			= '1460'
        AND quotesA.option_type 	= 'P'
        
		AND quotesB.root 			= 'RUTW'
        AND quotesB.expiration 		= '2017-12-08'
        AND quotesB.strike 			= '1530'
        AND quotesB.option_type 	= 'C'        
        
        AND ( 
			((quotesA.bid_1545 + quotesA.ask_1545) / 2) + ((quotesB.bid_1545 + quotesB.ask_1545) / 2)
				> 2 * (((14.7 + 15.5) / 2) + ((12.5 + 13.3) / 2)) 
            OR 
            DATEDIFF(quotesA.expiration, quotesA.quote_date) < 21
        )
	ORDER BY quotesA.quote_date;
