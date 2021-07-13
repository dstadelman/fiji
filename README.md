# fiji

Fast, interactive, backtest, framework of options strategies in a command shell using *Cboe End-of-Day Option Quotes with Calcs*

Supports trades with short/long underlying with for four short/long option legs.

Currently supported trades

* Buy and Hold
* Put (Short/Long)
* Call (Short/Long)
* Strangle (Short/Long)

Fiji is tightly coupled with hibernate + postgreSQL - as a software engineer, this is gross, but speed of implementation is priority over "really nice code."

## DISCLAIMER 

This isn't trading or investment advice. Please see LICENSE, especially the sections *Disclaimer of Warranty* and *Limitation of Liability*. 

There are probably errors in this code! As everything in life, take your time and start small. If something seems too good to be true, you're probably missing something.

There is no Cboe data in this repository and it won't be given away!

## Data

Where to find data

[End of Day Option Quotes with Calcs, Market Implied Volatility and Greeks](https://datashop.cboe.com/option-quotes-end-of-day-with-calcs)
[Cboe DataShop End-of-Day Option Quotes with Calcs Specifications](https://datashop.cboe.com/documents/end_of_day_option_quotes_with_calcs_layout.pdf)

Fiji has been tested with symbol "^RUT".

## References

### Data

https://datashop.cboe.com/option-quotes-end-of-day-with-calcs

### Previous Work

https://python-backtest.blogspot.com/2019/10/cboe-data-review.html?m=1

### Java Reference

https://www.baeldung.com/thread-pool-java-and-guava

https://www.baeldung.com/java-properties

https://www.baeldung.com/jackson-object-mapper-tutorial

https://www.baeldung.com/hibernate-criteria-queries


https://stackoverflow.com/questions/5272966/jfreechart-image

https://www.tutorialspoint.com/jfreechart/jfreechart_line_chart.htm

https://www.boraji.com/jfreechart-time-series-chart-example


http://hibernatepojoge.sourceforge.net


https://www.baeldung.com/a-simple-guide-to-connection-pooling-in-java

https://spring.io/projects/spring-cloud-dataflow


LocalDate to Date conversions
https://stackoverflow.com/questions/22929237/convert-java-time-localdate-into-java-util-date-type

### Cost Functions

https://en.wikipedia.org/wiki/Sharpe_ratio

## PostgreSQL Table Creation / Operations

This project was originally created using MySQL, but alas, it was too slow.

* Conversion 
   * https://en.wikibooks.org/wiki/Converting_MySQL_to_PostgreSQL
* Create quotes table
   * `/c/Program\ Files/PostgreSQL/13/bin/psql -h localhost -d fiji -U postgres -f quotes_create.sql`
* Importing Cboe DataShop End-of-Day Option Quotes with Calcs into PostgreSQL fiji Database
   * https://www.postgresqltutorial.com/import-csv-file-into-posgresql-table/
   * Create queries from CSV
      * `./postgresql/quotes_import_csv.sh quotes /e/MyProjects/Cboe/RUT_2004-01_2021-01/ E:\\MyProjects\\Cboe\\RUT_2004-01_2021-01\\ > quotes_import_csv.sql`
   * Import CSVs
      * `/c/Program\ Files/PostgreSQL/13/bin/psql -h localhost -d fiji -U postgres -f quotes_import_csv.sql`

* Used DBeaver for SQL editing / testing
   * https://dbeaver.io/
