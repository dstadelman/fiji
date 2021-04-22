DROP TABLE IF EXISTS quotes;

CREATE TABLE quotes (
  idquotes SERIAL PRIMARY KEY,
  underlying_symbol varchar(16) NOT NULL,
  quote_date date NOT NULL,
  root varchar(16) NOT NULL,
  expiration date NOT NULL,
  strike float NOT NULL,
  option_type varchar(1) NOT NULL,
  open float NOT NULL,
  high float NOT NULL,
  low float NOT NULL,
  close float NOT NULL,
  trade_volume int NOT NULL,
  bid_size_1545 int NOT NULL,
  bid_1545 float NOT NULL,
  ask_size_1545 int NOT NULL,
  ask_1545 float NOT NULL,
  underlying_bid_1545 float NOT NULL,
  underlying_ask_1545 float NOT NULL,
  implied_underlying_price_1545 float NOT NULL,
  active_underlying_price_1545 float NOT NULL,
  implied_volatility_1545 float NOT NULL,
  delta_1545 float NOT NULL,
  gamma_1545 float NOT NULL,
  theta_1545 float NOT NULL,
  vega_1545 float NOT NULL,
  rho_1545 float NOT NULL,
  bid_size_eod int NOT NULL,
  bid_eod float NOT NULL,
  ask_size_eod int NOT NULL,
  ask_eod float NOT NULL,
  underlying_bid_eod float NOT NULL,
  underlying_ask_eod float NOT NULL,
  vwap float NOT NULL,
  open_interest int NOT NULL,
  delivery_code varchar(64) DEFAULT NULL,
  mid_1545 float GENERATED ALWAYS AS (((bid_1545 + ask_1545) / 2)) STORED,
  underlying_mid_1545 float GENERATED ALWAYS AS (((underlying_bid_1545 + underlying_ask_1545) / 2)) STORED,
  mid_eod float GENERATED ALWAYS AS (((bid_eod + ask_eod) / 2)) STORED,
  underlying_mid_eod float GENERATED ALWAYS AS (((underlying_bid_eod + underlying_ask_eod) / 2)) STORED,
  dte int GENERATED ALWAYS AS ((expiration - '1970-01-01'::date) - (quote_date - '1970-01-01'::date)) STORED,
  delta_binned_1545 int GENERATED ALWAYS AS ((round(delta_1545 * 20) * 5)) STORED
);

CREATE INDEX idx_quotes_underlying_symbol_quote_date ON quotes (underlying_symbol,quote_date);
CREATE INDEX idx_quotes_underlying_symbol_expiration ON quotes (underlying_symbol,expiration);
CREATE INDEX idx_quotes_underlying_symbol_quote_date_delta_binned_1545_dte ON quotes (underlying_symbol,quote_date,delta_binned_1545,dte);
CREATE INDEX idx_quotes_symbol_quote_date_expiration_delta_binned_1545 ON quotes (underlying_symbol,quote_date,expiration,delta_binned_1545);
CREATE INDEX idx_quotes_delta_binned_1545 ON quotes (delta_binned_1545);
CREATE INDEX idx_quotes_expiration ON quotes (expiration);
