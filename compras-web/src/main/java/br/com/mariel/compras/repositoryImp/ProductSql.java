package br.com.mariel.compras.repositoryImp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.IUser;

@Repository
@Transactional
public class ProductSql extends AbstractRepositorySql<Product> implements IProduct {
	
	@Autowired private IBuy buySql;
	@Autowired private IUser userSql;
	
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = userSql.findById(rs.getString("product_user_phone"));
		User userBuy = userSql.findById(rs.getString("shopping_user_phone"));
		Buy buy = Buy.reload(rs.getLong("shopping_id"), userBuy);

		return Product.reload(rs.getLong("product_id"), rs.getString("product_name"), BuyStatus.from(rs.getInt("product_status")), buy, user, rs.getInt("product_department"));
	}

	@Override
	public Product findById(Product product) {
		try {
			return jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, bindProduct(product), this);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<Product> listByBuy(Buy buy) { // Lista todos os produtos de uma compra
		try {
			return (ArrayList<Product>) jdbcTemplate.query(SQL_SELECT_BY_BUY, 
					map("shopping_id", buy.getId(), "shopping_user_phone", buy.getUser().getUser_phone()), this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(Product product) {
		try {
			return jdbcTemplate.update(SQL_DELETE, bindProduct(product)) > 0;
		}
		catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean deleteAllProductOfBuy(Buy buy) {
		try {
			Map<String, Object> map = map("shopping_id", buy.getId(), "shopping_user_phone", buy.getUser().getUser_phone());
			return jdbcTemplate.update(SQL_DELETE_BY_BUY, map) > 0;
		}
		catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean insert(Product product) {
		try {
			return jdbcTemplate.update(SQL_INSERT, new MapSqlParameterSource(bindProduct(product))) > 0;
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean update(Product product) {
		try {
			return jdbcTemplate.update(SQL_UPDATE, new MapSqlParameterSource(bindProduct(product))) > 0;
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	private Map<String, Object> bindProduct(Product product) {
		Map<String, Object> map = map(
				"product_id", product.getId(), 
				"product_name", product.getName(),
				"product_status", product.getStatus().getCode(),
				"product_department", product.getDepartment(),
				"product_user_phone", product.getUser().getUser_phone(),
				"shopping_id", product.getBuy().getId(),
				"shopping_user_phone", product.getBuy().getUser().getUser_phone());
		return map;
	}
	
	private static final String SQL_SELECT_BY_BUY = "SELECT * FROM product WHERE shopping_id = :shopping_id AND shopping_user_phone = :shopping_user_phone";
	private static final String SQL_SELECT_BY_ID = "SELECT * FROM product "
												  + "WHERE product_id = :product_id "
												  + "  AND shopping_id = :shopping_id "
												  + "  AND shopping_user_phone = :shopping_user_phone "
												  + "  AND product_user_phone = :product_user_phone;";
	
	private static final String SQL_DELETE = "DELETE FROM product "
											+ "WHERE product_id = :product_id "
											+ "  AND shopping_id = :shopping_id "
											+ "  AND shopping_user_phone = :shopping_user_phone "
											+ "  AND product_user_phone = :product_user_phone;";
	
	private static final String SQL_DELETE_BY_BUY = "DELETE FROM product "
											+ "WHERE shopping_id = :shopping_id "
											+ "  AND shopping_user_phone = :shopping_user_phone;";
	
	private static final String SQL_INSERT = "INSERT INTO product VALUES (:product_id, :product_name, :product_status, :product_department, :product_user_phone, :shopping_id, :shopping_user_phone)";
	private static final String SQL_UPDATE = "UPDATE product "
											  + "SET product_name = :product_name, "
											  + "    product_status = :product_status, "
											  + "    product_department = :product_department "
										    + "WHERE product_id = :product_id "
												+ "  AND shopping_id = :shopping_id "
												+ "  AND shopping_user_phone = :shopping_user_phone "
												+ "  AND product_user_phone = :product_user_phone;";
}
