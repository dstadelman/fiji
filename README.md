# fiji

Options strategy backtest framework using *Cboe End-of-Day Option Quotes with Calcs*

Framework supports underlying with four legs of options.

Fiji is tightly coupled with hibernate + MySQL 8 queries - a little gross, but speed of implementation was considered over "really nice code." Expect hibernate and MySQL 8 all over the code base!

## DISCLAIMER 

This isn't trading or investment advice. Please see LICENSE, especially the sections *Disclaimer of Warranty* and *Limitation of Liability*. 

As everything in life, take your time and start small. 

There is no Cboe data in this repository and it won't be given away. Cboe must be paid for their work. Please support folks that make good data available for such a small amount!

## Assumptions

A programmer has purchased some *Cboe End-of-Day Option Quotes with Calcs* data and would like to load this data into MySQL and run some options strategies *that they have programmed themselves* against it. 

## Data

[Cboe End-of-Day Option Quotes with Calcs Specifications](https://datashop.cboe.com/documents/end_of_day_option_quotes_with_calcs_layout.pdf)

## References

### Data

https://datashop.cboe.com/option-quotes-end-of-day-with-calcs

### Previous Work

https://python-backtest.blogspot.com/2019/10/cboe-data-review.html?m=1

### MySQL

https://dev.mysql.com/doc/mysql-windows-excerpt/5.7/en/windows-installation.html
https://dev.mysql.com/doc/refman/8.0/en/window-functions-usage.html
https://levelup.gitconnected.com/how-and-when-to-write-mysql-subqueries-8d5d580b1729

JOIN table to itself 
SELECT * FROM a AS a1, a AS a2 WHERE a1.price - a2.price = x

### Java Reference

https://www.baeldung.com/java-connect-mysql
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

## MySQL Table Creation / Operations

* Create tables
   * Create `quotes` table: `mysql_workbench/quotes_create.sql`
   * Create `tradestrats` table: `mysql_workbench/tradestrats_create.sql`
   * Create `trades` table: `mysql_workbench/trades_create.sql`
* Sample CSV import: `mysql_workbench/quotes_import_csv.sql`
* Bash script to make SQL file to insert all CSV files into database (use Git Bash include in Git for Windows): `mysql_workbench/quotes_import_csv.sh`

Some operations are slow and MySQL Workbench gives up: https://stackoverflow.com/questions/10563619/error-code-2013-lost-connection-to-mysql-server-during-query
* New versions of MySQL WorkBench have an option to change specific timeouts.
* For me it was under Edit → Preferences → SQL Editor → DBMS connection read time out (in seconds): 600 - Changed the value to 6000.
* Also unchecked limit rows as putting a limit in every time I want to search the whole data set gets tiresome.

All values to 0 in this dialog as MySQL database is local and I don't mind waiting.

