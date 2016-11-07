
package br.com.compras.app.repositoryImp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.ISynchronizer;

public class SynchronizerSql extends AbstractRepositorySql<Synchronizer> implements ISynchronizer {
	
	private static SQLiteDatabase bd;
	
	public SynchronizerSql(Context context) {
		super(context);
		bd = getWritableDatabase();
	}

	@Override
	public boolean insert(Synchronizer synchronize) {
		try {
			ContentValues vlr = new ContentValues();
				vlr.put("item_type", synchronize.getItem_type().getCode());
				vlr.put("item_action", synchronize.getItem_action().getCode());
				vlr.put("item_object", synchronize.getItem_object());
		
			return bd.insert("synchronize", null, vlr) > 0 ? true : false;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			bd.close();
		}
		return false;
	}

	@Override
	public ArrayList<Synchronizer> listToSync(User user) {
		ArrayList<Synchronizer> list = new ArrayList<Synchronizer>();
		
		try{
			Cursor c = bd.rawQuery("SELECT * FROM synchronize", null);
		
			while(c.moveToNext()) {
				list.add(Synchronizer.reload(c.getString(0), TypeStatus.from(c.getInt(1)), CrudStatus.from(c.getInt(2)), c.getString(3), null));
			}
		}catch(Exception erro){
			erro.printStackTrace();
		}finally{
			bd.close();
		}
		return list;
	}

	@Override
	public boolean delete(String id) {
		try{
			return bd.delete("synchronize", "_id = ?", new String[] {id}) > 0 ? true : false;
		}catch(Exception erro){
			erro.printStackTrace();
		}finally{
			bd.close();
		}
		return false;
	}
}
