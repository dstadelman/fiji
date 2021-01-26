#!/bin/bash

if [ $# -lt 3 ]; then
    echo "$0 <BASH_PATH> <WINDOWS_PATH>" 
    echo ""
    echo "EXAMPLE: "
    echo "./quotes_import_csv.sh quotes /e/MyProjects/Cboe/ E:\\MyProjects\\Cboe\\"
    exit 0
fi

TABLE_NAME=$1
CSV_PATH_BASH=$2
CSV_PATH_WINDOWS=$3

CURRENT_DIR=`pwd`

cd ${CSV_PATH_BASH}

for f in `ls *.csv`; do

    echo """
LOAD DATA INFILE '${CSV_PATH_WINDOWS}$f' 
INTO TABLE ${TABLE_NAME}
FIELDS TERMINATED BY ',' ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES (
underlying_symbol,
quote_date,
root,
expiration,
strike,
option_type,
open,
high,
low,
close,
trade_volume,
bid_size_1545,
bid_1545,
ask_size_1545,
ask_1545,
underlying_bid_1545,
underlying_ask_1545,
implied_underlying_price_1545,
active_underlying_price_1545,
implied_volatility_1545,
delta_1545,
gamma_1545,
theta_1545,
vega_1545,
rho_1545,
bid_size_eod,
bid_eod,
ask_size_eod,
ask_eod,
underlying_bid_eod,
underlying_ask_eod,
vwap,
open_interest,
delivery_code);

"""

done

cd ${CURRENT_DIR}