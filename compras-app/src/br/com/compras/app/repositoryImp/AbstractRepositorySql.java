package br.com.compras.app.repositoryImp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class AbstractRepositorySql<T> extends SQLiteOpenHelper {

	private static final String DATABASE = "compras";
	private static final int version = 1;
	
	public AbstractRepositorySql(Context context) {
		super(context, DATABASE, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE IF NOT EXISTS user (phone TEXT PRIMARY KEY, name TEXT, email TEXT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS buy ("
				    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT, status INTEGER, phone TEXT, "
				    + "FOREIGN KEY(phone) REFERENCES user(phone))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS product ( "
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT, status INTEGER, buy_id INTEGER, buy_phone TEXT, phone TEXT, department INTEGER, "
					+ "FOREIGN KEY(buy_id) REFERENCES buy(_id), "
					+ "FOREIGN KEY(buy_phone) REFERENCES buy(phone), "
					+ "FOREIGN KEY(phone) REFERENCES user(phone))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS share (buy_id INTEGER, buy_user_phone TEXT, user_phone TEXT, "
				+ "PRIMARY KEY (buy_id, buy_user_phone, user_phone), "
				+ "FOREIGN KEY(buy_id) REFERENCES user(buy), "
				+ "FOREIGN KEY(buy_user_phone) REFERENCES user(phone), "
				+ "FOREIGN KEY(user_phone) REFERENCES buy(phone))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS synchronize ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "item_type INTEGER, "
				+ "item_action INTEGER, "
				+ "item_object TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE user; DROP TABLE synchronize; DROP TABLE product; DROP TABLE buy; DROP TABLE share;");
		onCreate(db);
	}
}
