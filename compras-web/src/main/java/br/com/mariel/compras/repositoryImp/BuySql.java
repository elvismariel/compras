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
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IUser;

@Repository
@Transactional
public class BuySql extends AbstractRepositorySql<Buy> implements IBuy {
	
	@Autowired IUser userSql;
	
	public Buy mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = userSql.findById(rs.getString("user_phone"));
		
		return Buy.reload(rs.getLong("shopping_id"), 
				rs.getString("shopping_name"), 
				BuyStatus.from(rs.getInt("shopping_status")), user);
	}

	@Override
	public boolean delete(Buy buy) {
		try {
			return (jdbcTemplate.update(SQL_DELETE, map("shopping_id", buy.getId(), "user_phone", buy.getUser().getUser_phone())) > 0);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return false;
	}
	
	@Override
	public Buy findById(Buy buy) {
		try {
			return jdbcTemplate.queryForObject(SQL_SELECT, map("shopping_id", buy.getId(), "user_phone", buy.getUser().getUser_phone()), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean insert(Buy buy) {
		try {
			Map<String, Object> map = map(
					"shopping_id", buy.getId(), 
					"shopping_name", buy.getName(),
					"shopping_status", buy.getStatus().getCode(), 
					"user_phone", buy.getUser().getUser_phone());

			return jdbcTemplate.update(SQL_INSERT, new MapSqlParameterSource(map)) > 0;
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean update(Buy buy) {
		try {
			Map<String, Object> map = map("shopping_id", buy.getId(), "shopping_name", buy.getName(),
				"shopping_status", buy.getStatus().getCode(), "user_phone", buy.getUser().getUser_phone());
			
			return (jdbcTemplate.update(SQL_UPDATE, map) > 0);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return false;
	}

	@Override
	public ArrayList<Buy> list() {
		// TODO Auto-generated method stub
		return null;
	}

	private static final String SQL_SELECT = "SELECT * FROM shopping WHERE shopping_id = :shopping_id AND user_phone = :user_phone;";
	private static final String SQL_DELETE = "DELETE FROM shopping WHERE shopping_id = :shopping_id AND user_phone = :user_phone;";
	private static final String SQL_INSERT = "INSERT INTO shopping VALUES (:shopping_id, :shopping_name, :shopping_status, :user_phone)";
	private static final String SQL_UPDATE = "UPDATE shopping SET shopping_name = :shopping_name, shopping_status = :shopping_status "
			+ "WHERE shopping_id = :shopping_id AND user_phone = :user_phone;";
}
