# Wall Street Stocks
<img src="" alt="logo" width="48"/>

A simple android application that allows users to have a quick at various sector performances for the day 
( e.g. Health sector), as well as lookup stock values for specific companies using their ticker symbols.

* Sector performances are given on a day-to-day basis.

* Stock values are updated on a 5 minute interval so querying for the same stock within a 5 minute period will 
return the same values. This is to ensure the application does not flood the [Alpha Vantage](https://www.alphavantage.co/) API with requests 
especially since it is a free resource available to us.

## How to use

If you have not yet built the project, please look below and ensure you have a compiled apk and the application is installed.

Once the application is installed it is very straight forward to use. Open the application and you will notice 
that there are 2 tabs:
* The first tab ("Sector Performances"), has a refresh button that lets you load the latest changes for all sectors.
<img src="" alt="Sector Performances tab"/>

* The second tab("Lookup Stocks"), allows you to enter a company stock ticker and then retrieve the latest quotes. 
<img src="" alt="Lookup Stocks tab"/>

## Build

In order to get an apk and install the application you need to build the project. Before building ensure to navigate to /app/src/main/java/com/wallstreetstocks and add 'Config.java'. The Config.java file should have the following code.
```java
package com.wallstreetstocks;

public class Config {

    public static final String API_KEY = "YOUR KEY HERE";
}
```
**Note: You must visit <a href="https://www.alphavantage.co/support/#api-key" target="_blank>Alpha Vantage</a> and get your own API key and replace the value for API_KEY**
