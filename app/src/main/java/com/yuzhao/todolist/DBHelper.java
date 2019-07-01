package com.yuzhao.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public final class DBHelper
{
	private static final String DATABASE_NAME		= "tasks.db";
	private static final int    DATABASE_VERSION	= 1;
	private static final String TABLE_NAME			= "taskdata";
	// Column Names
	public static final String KEY_ID				= "_id";
	public static final String KEY_STATUS		= "status";
	public static final String KEY_TITLE			= "title";
	public static final String KEY_DESCRIPTION	= "description";
	public static final String KEY_DETAIL		= "detail";
	public static final String KEY_DUEDATE		= "duedate";

	// Column Indexes
	public static final int COLUMN_ID         	= 1;
	public static final int COLUMN_STATUS       = 2;
	public static final int COLUMN_TITLE  	  	= 3;
	public static final int COLUMN_DESCRIPTION 	= 4;
	public static final int COLUMN_DETAIL      	= 5;
	public static final int COLUMN_DUEDATE     	= 6;

	private Context context;
	private SQLiteDatabase db;
	private SQLiteStatement m_insert;
	private SQLiteStatement m_update;

	private final String INSERT =	"INSERT INTO " + TABLE_NAME +
			"(" + KEY_STATUS + ", " + KEY_TITLE + ", " + KEY_DESCRIPTION + ", " + KEY_DETAIL + ", " + KEY_DUEDATE + ") VALUES (?, ?, ?, ?, ?)";
	private final String UPDATE = "UPDATE " + TABLE_NAME +
			" SET " + KEY_STATUS + "=?," + KEY_TITLE + "=?," + KEY_DESCRIPTION + "=?," + KEY_DETAIL + "=?," + KEY_DUEDATE + "=?" +
			" WHERE " + KEY_ID + "=?";

	public DBHelper(Context context) throws Exception
	{
		this.context = context;
		try
		{
			OpenHelper openHelper = new OpenHelper(this.context);
			// Open a database for reading and writing
			db = openHelper.getWritableDatabase();
			// Compile a sqlite insert statement into re-usable statement object.
			m_insert = db.compileStatement(INSERT);
			m_update = db.compileStatement(UPDATE);
		}
		catch (Exception e)
		{
			throw (e);
		}
	}
	public long insert(TaskItem task)
	{
		// bind values to the pre-compiled SQL statement "inserStmt"
		m_insert.bindLong(1, task.status);
		m_insert.bindString(2, task.title);
		m_insert.bindString(3, task.description);
		m_insert.bindString(4, task.detail);
		m_insert.bindString(5, task.duedate);

		// Execute the sqlite statement.
		long value = -1;
		try
		{
			value = m_insert.executeInsert();
		}
		catch (Exception e)
		{
		}
		return value;
	}
	public void update(TaskItem task)
	{
		db.execSQL(UPDATE, new String[] { task.status + " ", task.title, task.description,task.detail, task.duedate, task.id + " "});
	}
	/*public void update(TaskItem task)
	{
		// bind values to the pre-compiled SQL statement "inserStmt"
		m_update.bindLong(1, task.status);
		m_update.bindString(2, task.title);
		m_update.bindString(3, task.description);
		m_update.bindString(4, task.detail);
		m_update.bindString(5, task.duedate);
		m_update.bindLong(6, task.id);
		// Execute the sqlite statement.
		int count = m_update.executeUpdateDelete();
	}*/
	public void deleteAll()
	{
		db.delete(TABLE_NAME, null, null);
	}
	public boolean delete(long id)
	{
		return db.delete(TABLE_NAME, KEY_ID + "=" + id, null) > 0;
	}
	public TaskItem select(long id)
	{
		TaskItem task = new TaskItem();
		Cursor cursor = db.query(TABLE_NAME,
				new String[] { KEY_ID, KEY_STATUS, KEY_TITLE, KEY_DESCRIPTION, KEY_DETAIL, KEY_DUEDATE },
				KEY_ID + "=" + id, null, null, null, null);
		if (cursor.moveToFirst())
		{
			task.id = cursor.getLong(0);
			task.status = cursor.getInt(1);
			task.title = cursor.getString(2);
			task.description = cursor.getString(3);
			task.detail = cursor.getString(4);
			task.duedate = cursor.getString(5);
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return task;
	}
	public List<TaskItem> selectAll()
	{
		List<TaskItem> list = new ArrayList<TaskItem>();
		// query takes the following parameters
		// dbName :  the table name
		// columnNames:  a list of which table columns to return
		// whereClause:  filter of selection of data;  null selects all data
		// selectionArg: values to fill in the ? if any are in the whereClause
		// group by:   Filter specifying how to group rows, null means no grouping
		// having:  filter for groups, null means none
		// orderBy:  Table column used to order the data, null means no order.
		// A Cursor provides read-write access to the result set returned by a database query.
		// A Cursor represents the result of the query and points to one row of the query result.
		Cursor cursor = db.query(TABLE_NAME,
				new String[] { KEY_ID, KEY_STATUS, KEY_TITLE, KEY_DESCRIPTION, KEY_DETAIL, KEY_DUEDATE },
				null, null, null, null, null);
		if (cursor.moveToFirst())
		{
			do
			{
				TaskItem task = new TaskItem();
				task.id = cursor.getLong(0);
				task.status = cursor.getInt(1);
				task.title = cursor.getString(2);
				task.description = cursor.getString(3);
				task.detail = cursor.getString(4);
				task.duedate = cursor.getString(5);
				list.add(task);
			}
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return list;
	}

	// Helper class for DB creation/update
	// SQLiteOpenHelper provides getReadableDatabase() and getWritableDatabase() methods
	// to get access to an SQLiteDatabase object; either in read or write mode.
	private static class OpenHelper extends SQLiteOpenHelper
	{
		private static final String CREATE_TABLE =
				"CREATE TABLE " + TABLE_NAME +
				"("   + KEY_ID				+ " INTEGER PRIMARY KEY, " +
						KEY_STATUS 		+ " TEXT, " +
						KEY_TITLE 			+ " TEXT, " +
						KEY_DESCRIPTION	+ " TEXT, " +
						KEY_DETAIL			+ " TEXT, " +
						KEY_DUEDATE		+ " TEXT);";
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// Creates the tables. This function is only run once or after every Clear Data
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
			}
		}
		// This method updating an existing database schema or dropping the existing database and recreating it via the onCreate() method.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			try
			{
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
				onCreate(db);
			}
			catch (Exception e)
			{
			}
		}
	}
}


