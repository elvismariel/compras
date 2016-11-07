package br.com.mariel.compras.repositoryImp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IUser;

@Repository
@Transactional
public class UserSql extends AbstractRepositorySql<User> implements IUser {
	
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		return User.reload(rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_phone"));
	}

	@Override
	public User findById(String phone) {
		User user = null;
		try {
			user = jdbcTemplate.queryForObject(SQL_SELECT_BY_PHONE, map("user_phone", phone), this);
		} 
		catch(DataAccessException e){
			System.out.println("Usuário não encontrada, messagem: " + e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public User insert(User user) {
		
		if(findById(user.getUser_phone()) != null)
			return user;
		
		try {
			Map<String, Object> map = map("user_name", user.getUser_name(),
				"user_email", user.getUser_email(),
				"user_phone", user.getUser_phone());

			if(jdbcTemplate.update(SQL_INSERT, new MapSqlParameterSource(map)) > 0)
				return user;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String SQL_SELECT_BY_PHONE = "SELECT * FROM user WHERE user_phone = :user_phone";
	private static final String SQL_INSERT = "INSERT INTO user (user_name, user_email, user_phone) VALUES (:user_name, :user_email, :user_phone)";
	@Override
	public User findByIdToLogin() {
		// TODO Auto-generated method stub
		return null;
	}
}
