package br.com.compras.app.repositoryImp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.IUser;

public class ShareSql extends AbstractRepositorySql<Share> implements IShare {
	
	private static SQLiteDatabase bd;
	private IUser userSql;
	
	public ShareSql(Context context) {
		super(context);
		bd = getWritableDatabase();
		userSql = new UserSql(context);
	}

	public boolean shareExist(Share share) {
		try {
			Cursor c = bd.rawQuery("SELECT * FROM share WHERE buy_id = " + share.getBuy().getId()
					+" AND buy_user_phone = '" + share.getBuyUser().getUser_phone() + "'"
					+" AND user_phone = '" + share.getUser().getUser_phone()+"'", null);

			if(c.moveToNext())
				return true;
		} catch(Exception erro){
			erro.printStackTrace();
		}

		return false;
	}
	@Override
	public boolean insert(Share share) {
		try {
			if(shareExist(share)){
				return true;
			} else {
				ContentValues vlr = new ContentValues();
				vlr.put("buy_id", share.getBuy().getId());
				vlr.put("buy_user_phone", share.getBuyUser().getUser_phone());
				vlr.put("user_phone", share.getUser().getUser_phone());

				return bd.insert("share", null, vlr) > 0;
			}
		} catch(Exception e){
			e.getStackTrace();
		}

		return false;
	}

	@Override
	public boolean delete(Share share) {
		try {
			return (bd.delete("share","buy_id = ? AND user_phone = ?", 
					new String[] { ""+share.getBuy().getId(), share.getUser().getUser_phone() }) > 0) ? true : false;
		}catch(Exception erro){
			erro.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<Share> list(Buy buy) {
		ArrayList<Share> list = new ArrayList<Share>();
		try{
			Cursor c = bd.rawQuery("SELECT * FROM share WHERE buy_id = " + buy.getId() + " AND user_phone <> '" +SessionRepository.getUser().getUser_phone()+"'", null);
		
			while(c.moveToNext()) {
				Buy b1 = Buy.reload(c.getLong(0));
					b1.setUser(User.reload(c.getString(1)));

				User user = userSql.findById(c.getString(2));
				Share share = Share.reload(b1, b1.getUser(), user);
		
				list.add(share);
			}
		}catch(Exception erro){
			erro.printStackTrace();
		}
		return list;
	}

	@Override
	public ArrayList<Share> list(User user) {
		// TODO Auto-generated method stub
		return null;
	}
}
