package com.github.dstadelman.fiji.controllers;

public class DBController {
    public static String f(String table, String field) {
        if (table != null)
            return table + "." + field;
        else
            return field;
    }

    public static void f(StringBuffer s, String table, String field) {
        if (table != null)
            s.append(table + ".");
        s.append(field);
    }

    public static String n(String table, String field) {
        if (table != null)
            return table + "_" + field;
        else
            return field;
    }

    public static void n(StringBuffer s, String table, String field) {
        if (table != null)
            s.append(table + "_");
        s.append(field);
    }
}
