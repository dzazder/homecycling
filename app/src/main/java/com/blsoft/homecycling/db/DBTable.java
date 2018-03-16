package com.blsoft.homecycling.db;

import java.util.ArrayList;

/**
 * Created by bartek on 16.03.2018.
 */

public class DBTable {
    private int version;    // database version when table was created
    private String name;
    private ArrayList<DBColumn> columns;

    public DBTable(String name, int version) {
        this.name = name;
        this.version = version;
        this.columns = new ArrayList<>();
    }

    public DBTable(String name) {
        this(name, 1);
    }

    public void addColumn(DBColumn column) {
        this.columns.add(column);
    }

    public String getCreateQuery() {
        StringBuilder query = new StringBuilder(String.format("create table %s (", name));
        for (DBColumn column: columns) {
            query.append(column.toString());
            query.append(",");
        }

        // remove last comma
        if (query.substring(query.length()-1).equals(",")) {
            query.setLength(query.length() - 1);
        }

        query.append(");");

        return query.toString();
    }
}
