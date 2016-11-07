package br.com.compras.app.repositoryImp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IUser;

public class BuySql extends AbstractRepositorySql<Buy> implements IBuy {

	private static SQLiteDatabase bd;
	
	private IUser userSql;
	
	public BuySql(Context context){
		super(context);
		bd = getWritableDatabase();
		userSql = new UserSql(context);
	}
	
	@Override
	public Buy findById(Buy buy) {
		try {
			Cursor c = bd.rawQuery("SELECT * FROM buy WHERE _id = "+ buy.getId()+" AND phone = '"+buy.getUser().getUser_phone()+"'", null);
		
			if(c.moveToNext()) 
				return Buy.reload(c.getLong(0), c.getString(1), BuyStatus.from(c.getInt(2)), userSql.findById(c.getString(3)));
		} catch(Exception erro){
			erro.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public ArrayList<Buy> list() {
		ArrayList<Buy> list = new ArrayList<Buy>();
		
		try {
			Cursor c = bd.rawQuery("SELECT * FROM buy", null);
		
			while(c.moveToNext()){
				Buy buy = Buy.reload(c.getLong(0), c.getString(1), BuyStatus.from(c.getInt(2)), userSql.findById(c.getString(3)));
				list.add(buy);
			}
		} catch(Exception erro){
			erro.printStackTrace();
		}
		
		return list;
	}

	@Override
	public boolean delete(Buy buy) {
		try {
			return bd.delete("buy", "_id = ?", new String[] {String.valueOf(buy.getId())}) > 0;
		}
		catch(Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean update(Buy buy) {
		try {
			ContentValues vlr = new ContentValues();
				vlr.put("name", buy.getName());
				vlr.put("status", buy.getStatus().getCode());
				vlr.put("phone", buy.getUser().getUser_phone());
			
			return bd.update("buy", vlr, "_id = ? AND phone = ?", new String[]{String.valueOf(buy.getId()), buy.getUser().getUser_phone()}) > 0;
		}
		catch(Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean insert(Buy buy) {
		try {
			ContentValues vlr = new ContentValues();

			if(buy.getId() > 0) {
				if(findById(buy) != null)
					return update(buy);
				else
					vlr.put("_id", buy.getId());
			}

			vlr.put("name", buy.getName());
			vlr.put("status", buy.getStatus().getCode());
			vlr.put("phone", buy.getUser().getUser_phone());

			buy.setId(bd.insert("buy", null, vlr));
			
			return true;
		}
		catch (Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}
}
