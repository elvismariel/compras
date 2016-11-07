package br.com.compras.app.repositoryImp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.repository.IProduct;

public class ProductSql extends AbstractRepositorySql<Product> implements IProduct {
	
	private static SQLiteDatabase bd;
	
	public ProductSql(Context context) {
		super(context);
		bd = getWritableDatabase();
	}
	
	@Override
	public Product findById(Product prod) {
		try {
			Cursor c = bd.rawQuery("SELECT * FROM product "
					              + "WHERE _id = "+prod.getId()
					              + "  AND buy_id = "+prod.getBuy().getId()
					              + "  AND buy_phone = '"+prod.getBuy().getUser().getUser_phone()+"'"
					              + "  AND phone = '"+prod.getUser().getUser_phone()+"'", null);
			
			if(c.moveToNext())
				return Product.reload(c.getInt(0), c.getString(1), BuyStatus.from(c.getInt(2)), prod.getBuy(), User.reload(c.getString(4)), c.getInt(5));
		}
		catch(Exception erro){
			erro.printStackTrace();
		}

		return null;
	}

	@Override
	public ArrayList<Product> listByBuy(Buy buy) {
		ArrayList<Product> list = new ArrayList<Product>();
		try {
			Cursor c = bd.rawQuery("SELECT * FROM product WHERE buy_id = " + buy.getId() + " AND buy_phone = '" + buy.getUser().getUser_phone() + "' ORDER BY department, status ", null);
			
			while(c.moveToNext()){
				Product product = Product.reload(c.getInt(0), c.getString(1), BuyStatus.from(c.getInt(2)), buy, User.reload(c.getString(5)), c.getInt(6));
				list.add(product);
			}
		} catch(Exception erro){
			erro.printStackTrace();
		}
		
		return list;
	}

	@Override
	public boolean insert(Product product) {
		try {
			ContentValues vlr = new ContentValues();

			vlr.put("name", product.getName());
			vlr.put("status", product.getStatus().getCode());
			vlr.put("buy_id", product.getBuy().getId());
			vlr.put("buy_phone", product.getBuy().getUser().getUser_phone());
			vlr.put("phone", product.getUser().getUser_phone());
			vlr.put("department", product.getDepartment());

			if(product.getId() > 0)
				vlr.put("_id", product.getId());

			if(product.getId() > 0 && findById(product) != null) {
				bd.update("product", vlr, "_id = ? AND buy_id = ? AND phone = ?",
						new String[]{String.valueOf(product.getId()), String.valueOf(product.getBuy().getId()), product.getUser().getUser_phone()});
			}else{
				product.setId(bd.insert("product", null, vlr));
			}

			return true;
		}
		catch(Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean update(Product product) {
		try {
			ContentValues vlr = new ContentValues();
				vlr.put("name", product.getName());
				vlr.put("status", product.getStatus().getCode());
				vlr.put("department", product.getDepartment());
		
			return bd.update("product", vlr, "_id = ? AND buy_id = ? AND buy_phone = ? AND phone = ?", 
					new String[]{""+product.getId(), ""+product.getBuy().getId(), product.getBuy().getUser().getUser_phone(), product.getUser().getUser_phone()}) > 0;
		}
		catch(Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean delete(Product product) {
		try {
			return bd.delete("product", "_id = ? AND buy_id = ? AND buy_phone = ? AND phone = ?", 
					new String[]{""+product.getId(), ""+product.getBuy().getId(), product.getBuy().getUser().getUser_phone(), product.getUser().getUser_phone()}) > 0;
		} catch(Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean deleteAllProductOfBuy(Buy buy) {
		try {
			return bd.delete("product", "buy_id = ? AND buy_phone = ?", new String[] { String.valueOf(buy.getId()), buy.getUser().getUser_phone()}) > 0;
		} catch(Exception erro){
			erro.printStackTrace();
		}
		
		return false;
	}
}
