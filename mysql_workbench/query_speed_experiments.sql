LOCK TABLE quotes WRITE;

ALTER TABLE quotes ADD COLUMN mid_1545 				FLOAT GENERATED ALWAYS AS ((bid_1545 + ask_1545) / 2) STORED;
ALTER TABLE quotes ADD COLUMN underlying_mid_1545 	FLOAT GENERATED ALWAYS AS ((underlying_bid_1545 + underlying_ask_1545) / 2) STORED;
ALTER TABLE quotes ADD COLUMN mid_eod 				FLOAT GENERATED ALWAYS AS ((bid_eod + ask_eod ) / 2) STORED;
ALTER TABLE quotes ADD COLUMN underlying_mid_eod 	FLOAT GENERATED ALWAYS AS ((underlying_bid_eod + underlying_ask_eod) / 2) STORED;
ALTER TABLE quotes ADD COLUMN dte 					INTEGER GENERATED ALWAYS AS (DATEDIFF(expiration, quote_date)) STORED;
ALTER TABLE quotes ADD COLUMN delta_binned_1545		FLOAT GENERATED ALWAYS AS (ROUND( CEIL( (delta_1545) * 20) / 20, 2)) STORED;

UNLOCK TABLES;

-- binning delta might really speed up looking for the correct delta
SELECT delta_1545 FROM quotes LIMIT 1;
SELECT ROUND(delta_1545, 2) FROM quotes LIMIT 1;
SELECT ROUND(delta_1545 * 100) FROM quotes LIMIT 1;
SELECT ROUND( CEIL( (delta_1545) * 20) / 20, 2) FROM quotes LIMIT 1;
