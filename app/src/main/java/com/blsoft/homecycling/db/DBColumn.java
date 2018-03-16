package com.blsoft.homecycling.db;

/**
 * Created by bartek on 16.03.2018.
 */

public class DBColumn {
    private String name;
    private DBDataType dataType;
    private boolean primaryKey;
    private boolean autoincrement;

    public DBColumn(String name, DBDataType dataType, boolean primaryKey, boolean autoincrement) {
        this.name = name;
        this.dataType = dataType;
        this.primaryKey = primaryKey;
        this.autoincrement = autoincrement;
    }

    public DBColumn(String name, DBDataType dataType, boolean primaryKey) {
        this(name, dataType, primaryKey, false);
    }

    public DBColumn(String name, DBDataType dataType) {
        this(name, dataType, false, false);
    }

    public String toString() {
        return String.format("%s %s %s %s", name, dataType.toString(), primaryKey ? "primary key" : "", autoincrement ? "autoincrement" : "");
    }
}
