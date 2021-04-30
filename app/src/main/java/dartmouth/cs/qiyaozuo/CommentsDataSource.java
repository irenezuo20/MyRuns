package dartmouth.cs.qiyaozuo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

class CommentsDataSource {
    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    //all column value
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_COMMENT, MySQLiteHelper.COLUMN_DISTANCE,
            MySQLiteHelper.COLUMN_CALORIES, MySQLiteHelper.COLUMN_HEART,
            MySQLiteHelper.COLUMN_DURATION, MySQLiteHelper.COLUMN_DATE,
            MySQLiteHelper.COLUMN_TYPE, MySQLiteHelper.COLUMN_INPUT_TYPE,
            MySQLiteHelper.COLUMN_SPEED, MySQLiteHelper.COLUMN_CLIMB,
            MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_UNIT};

    //database initialization
    public CommentsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //get all entries from database and return a list
    public List<ExerciseEntryModel> getAllComments() {
        List<ExerciseEntryModel> exerciseEntries = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + MySQLiteHelper.TABLE_COMMENTS, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ExerciseEntryModel row = cursorToComment(cursor);
            exerciseEntries.add(row);
            cursor.moveToNext();
        }

        cursor.close();
        return exerciseEntries;
    }

    public ExerciseEntryModel getEntry(long id) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + id,
                null,
                null,
                null,
                null);
        ExerciseEntryModel newExerciseEntryModel = cursorToComment(cursor);
        cursor.close();
        return newExerciseEntryModel;
    }

    //insert a new row to the database given the entry object
    public ExerciseEntryModel createComment(ExerciseEntryModel entry) {
        ContentValues value = new ContentValues();

        //fill value with column values
        value.put(MySQLiteHelper.COLUMN_COMMENT, entry.getComment());
        value.put(MySQLiteHelper.COLUMN_DISTANCE, entry.getDistance());
        value.put(MySQLiteHelper.COLUMN_CALORIES, entry.getCalories());
        value.put(MySQLiteHelper.COLUMN_HEART, entry.getHeartRate());
        value.put(MySQLiteHelper.COLUMN_DURATION, entry.getDuration());
        value.put(MySQLiteHelper.COLUMN_DATE, entry.getDate());
        value.put(MySQLiteHelper.COLUMN_TYPE, entry.getType());
        value.put(MySQLiteHelper.COLUMN_INPUT_TYPE, entry.getInputType());
        value.put(MySQLiteHelper.COLUMN_SPEED, entry.getSpeed());
        value.put(MySQLiteHelper.COLUMN_CLIMB, entry.getClimb());
        value.put(MySQLiteHelper.COLUMN_LOCATION, entry.getLocation());
        value.put(MySQLiteHelper.COLUMN_UNIT, entry.getUnit());

        //insert row
        long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null, value);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        ExerciseEntryModel newExerciseEntryModel = cursorToComment(cursor);
        cursor.close();
        return newExerciseEntryModel;
    }

    //convert cursor to entry object
    private ExerciseEntryModel cursorToComment(Cursor cursor) {
        ExerciseEntryModel exerciseEntryModel = new ExerciseEntryModel();
        exerciseEntryModel.setId(cursor.getLong(0));
        exerciseEntryModel.setComment(cursor.getString(1));
        exerciseEntryModel.setDistance(cursor.getFloat(2));
        exerciseEntryModel.setCalories(cursor.getInt(3));
        exerciseEntryModel.setHeartRate(cursor.getInt(4));
        exerciseEntryModel.setDuration(cursor.getFloat(5));
        exerciseEntryModel.setDate(cursor.getString(6));
        exerciseEntryModel.setType(cursor.getString(7));
        exerciseEntryModel.setInputType(cursor.getString(8));
        exerciseEntryModel.setSpeed(cursor.getFloat(9));
        exerciseEntryModel.setClimb(cursor.getFloat(10));
        exerciseEntryModel.setLocation(cursor.getString(11));
        exerciseEntryModel.setUnit(cursor.getString(12));
        return exerciseEntryModel;
    }

    //delete selected row given the entry object
    public boolean deleteComment(ExerciseEntryModel exerciseEntryModel) {
        return database.delete(MySQLiteHelper.TABLE_COMMENTS,
                MySQLiteHelper.COLUMN_ID + "=" + exerciseEntryModel.getId(),
                null) > 0;
    }

}
