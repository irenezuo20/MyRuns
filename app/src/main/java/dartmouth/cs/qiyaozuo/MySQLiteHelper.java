package dartmouth.cs.qiyaozuo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_COMMENTS = "comments_table";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_HEART = "heartRate";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_INPUT_TYPE = "input";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_CLIMB = "climb";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_LOCATION = "location";

    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 1;

    //SQL create table
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_COMMENTS + "(" + COLUMN_ID + " integer primary key, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_DISTANCE + " float, "
            + COLUMN_CALORIES + " integer, "
            + COLUMN_HEART + " integer, "
            + COLUMN_DURATION + " float, "
            + COLUMN_DATE + " text, "
            + COLUMN_TYPE + " text, "
            + COLUMN_INPUT_TYPE + " text, "
            + COLUMN_SPEED + " float, "
            + COLUMN_CLIMB + " float, "
            + COLUMN_LOCATION + " text, "
            + COLUMN_UNIT + " text);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }
}
