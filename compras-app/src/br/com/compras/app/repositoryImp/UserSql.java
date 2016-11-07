package br.com.compras.app.repositoryImp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IUser;

public class UserSql extends AbstractRepositorySql<User> implements IUser {

	private static SQLiteDatabase bd;
	
	public UserSql(Context context){
		super(context);
		bd = getWritableDatabase();
	}
	
	@Override
	public User findById(String phone) {
		try{
		Cursor c = bd.rawQuery("SELECT * FROM user WHERE phone = '" + phone+"'", null);
		
		if(c.moveToNext())
			return User.reload(c.getString(1), c.getString(2), c.getString(0));
		}catch(Exception erro){
			erro.printStackTrace();
		}
		return null;
	}

	@Override
	public User insert(User user) {
		try {	
			ContentValues vlr = new ContentValues();
				vlr.put("name", user.getUser_name());
				vlr.put("email", user.getUser_email());
				vlr.put("phone", user.getUser_phone());
		
			if(bd.insert("user", null, vlr) > 0) {
				return user;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public User findByIdToLogin() {
		try{
			Cursor c = bd.rawQuery("SELECT * FROM user ORDER BY rowid LIMIT 1", null);
		
			if(c.moveToNext())
				return User.reload(c.getString(1), c.getString(2), c.getString(0));
		}
		catch(Exception erro){
			erro.printStackTrace();
		}
		return null;
	}
}
