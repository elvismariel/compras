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
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.IUser;

@Repository
@Transactional
public class ShareSql extends AbstractRepositorySql<Share> implements IShare {
	
	@Autowired private IUser userSql;
	@Autowired private IBuy buySql;
	
	public Share mapRow(ResultSet rs, int rowNum) throws SQLException {
		Buy buy = buySql.findById(Buy.reload(rs.getLong("shopping_id"), User.reload(rs.getString("shopping_user_phone"))));
		User user = userSql.findById(rs.getString("user_phone"));	
		
		return Share.reload(buy, buy.getUser(), user);
	}

	@Override
	public boolean insert(Share share) {
		try {
			Map<String, Object> map = map(
					"shopping_id", share.getBuy().getId(),
					"shopping_user_phone", share.getBuyUser().getUser_phone(),
					"user_phone", share.getUser().getUser_phone());
			
			if(jdbcTemplate.update(SQL_INSERT, new MapSqlParameterSource(map)) > 0)
				return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean delete(Share share) {
		try {
			Map<String, Object> map = map(
					"user_phone", share.getUser().getUser_phone(), 
					"shopping_id", share.getBuy().getId(),
					"shopping_user_phone", share.getBuyUser().getUser_phone());
			
			if(jdbcTemplate.update(SQL_DELETE, new MapSqlParameterSource(map)) > 0)
				return true;
		} 
		catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return false;
	}
	
	@Override
	public ArrayList<Share> list(User user) {
		ArrayList<Share> list = new ArrayList<Share>();
		try {
			list = (ArrayList<Share>) jdbcTemplate.query(SQL_SELECT_BY_USER, map("user_phone", user.getUser_phone()), this);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public ArrayList<Share> list(Buy buy) {
		ArrayList<Share> list = new ArrayList<Share>();
		try {
			list = (ArrayList<Share>) jdbcTemplate.query(SQL_SELECT_BY_BUY, map("shopping_id", buy.getId(), "shopping_user_phone", buy.getUser().getUser_phone()), this);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private static final String SQL_SELECT_BY_BUY = "SELECT * FROM share WHERE shopping_id = :shopping_id AND shopping_user_phone = :shopping_user_phone ";
	private static final String SQL_SELECT_BY_USER = "SELECT * FROM share WHERE user_phone = :user_phone";
	
	private static final String SQL_INSERT = "INSERT INTO share VALUES (:shopping_id, :shopping_user_phone, :user_phone)";
	private static final String SQL_DELETE = "DELETE FROM share WHERE user_phone = :user_phone AND shopping_id = :shopping_id AND shopping_user_phone = :shopping_user_phone";
}
