package com.blsoft.homecycling.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blsoft.homecycling.entitites.TrainingPeek;

import java.util.Date;

/**
 * Created by bartek on 16.03.2018.
 */

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context) {
        super(context, "homycycling.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getTrainings() {
        String[] columns = {DBNames.COL_ID, DBNames.COL_DATE_START};
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.query(DBNames.TABLE_TRAINING, columns, null, null, null, null, null);

        return result;
    }

    public long addTraining() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBNames.COL_DATE_START, new Date().toString());
        values.put(DBNames.COL_TIME, 0);
        values.put(DBNames.COL_DISTANCE, 0);
        values.put(DBNames.COL_AVG_CADENCE, 0);
        values.put(DBNames.COL_AVG_POWER, 0);
        values.put(DBNames.COL_AVG_SPEED, 0);
        values.put(DBNames.COL_MAX_CADENCE, 0);
        values.put(DBNames.COL_MAX_POWER, 0);
        values.put(DBNames.COL_MAX_SPEED, 0);
        return db.insertOrThrow(DBNames.TABLE_TRAINING, null, values);
    }

    public long addTrainingPeek(long idTraining, TrainingPeek peek) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBNames.COL_ID_TRAINING, idTraining);
        values.put(DBNames.COL_PEEKTIME, peek.getPeekTime());
        values.put(DBNames.COL_SPEED, peek.getSpeed().doubleValue());
        values.put(DBNames.COL_DISTANCE, peek.getDistance().doubleValue());
        values.put(DBNames.COL_CADENCE, peek.getCadence().doubleValue());
        values.put(DBNames.COL_POWER, peek.getPower().doubleValue());
        return db.insertOrThrow(DBNames.TABLE_TRAINING_PEEKS, null, values);
    }

    private void createTables(SQLiteDatabase db) {
        createTrainingsTable(db);
        createTrainigPeeksTable(db);
    }

    private void createTrainingsTable(SQLiteDatabase db) {
        DBTable tTrainings = new DBTable(DBNames.TABLE_TRAINING);
        tTrainings.addColumn(new DBColumn(DBNames.COL_ID, DBDataType.INTEGER, true, true));
        tTrainings.addColumn(new DBColumn(DBNames.COL_DATE_START, DBDataType.TEXT));
        tTrainings.addColumn(new DBColumn(DBNames.COL_TIME, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_DISTANCE, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_AVG_CADENCE, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_AVG_POWER, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_AVG_SPEED, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_MAX_CADENCE, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_MAX_POWER, DBDataType.REAL));
        tTrainings.addColumn(new DBColumn(DBNames.COL_MAX_SPEED, DBDataType.REAL));

        db.execSQL(tTrainings.getCreateQuery());
    }

    private void createTrainigPeeksTable(SQLiteDatabase db) {
        DBTable tTrainingPeeks = new DBTable(DBNames.TABLE_TRAINING_PEEKS);
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_ID, DBDataType.INTEGER, true, true));
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_ID_TRAINING, DBDataType.INTEGER));
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_PEEKTIME, DBDataType.REAL));
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_SPEED, DBDataType.REAL));
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_DISTANCE, DBDataType.REAL));
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_POWER, DBDataType.REAL));
        tTrainingPeeks.addColumn(new DBColumn(DBNames.COL_CADENCE, DBDataType.REAL));

        db.execSQL(tTrainingPeeks.getCreateQuery());
    }
}
