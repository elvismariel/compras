package br.com.mariel.compras.repositoryImp;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.ISynchronizer;
import br.com.mariel.compras.util.MD5Generator;

@Repository
@Transactional
public class SynchronizeSql extends AbstractRepositorySql<Synchronizer> implements ISynchronizer {
	
	public Synchronizer mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Synchronizer.reload(
				rs.getString("synchronize_id"), 
				TypeStatus.from(rs.getInt("item_type")), 
				CrudStatus.from(rs.getInt("item_action")),
				rs.getString("item_object"),
				User.reload(rs.getString("user_phone")));
	}

	@Override
	public boolean insert(Synchronizer sync) {
		try {
			Map<String, Object> map = map(
				"synchronize_id", generateTicket(),
				"item_type", sync.getItem_type().getCode(),
				"item_action", sync.getItem_action().getCode(),
				"item_object", sync.getItem_object(),
				"user_phone", sync.getUserToReceive().getUser_phone());

			if(jdbcTemplate.update(SQL_INSERT, new MapSqlParameterSource(map)) > 0)
				return true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean delete(String synchronize_id) {
		try {
			Map<String, Object> map = map("synchronize_id", synchronize_id);
			if(jdbcTemplate.update(SQL_DELETE, new MapSqlParameterSource(map)) > 0)
				return true;
		} 
		catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return false;
	}

	@Override
	public ArrayList<Synchronizer> listToSync(User user) {
		List<Synchronizer> list = null;
		try {
			list = jdbcTemplate.query(SQL_SELECT, map("user_phone", user.getUser_phone()), this);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		return (ArrayList<Synchronizer>) list;
	}
	
	private Synchronizer findById(String synchronize_id) {
		Synchronizer synchronizer = null;
		try {
			synchronizer = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, map("synchronize_id", synchronize_id), this);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return synchronizer;
	}

	private String generateTicket() {
		
		String fullString = String.valueOf(Calendar.getInstance().getTime());
		
		try {
			String hash = MD5Generator.generate(fullString);
			Random r = new Random();
			while (findById(hash) != null) {
				hash = MD5Generator.generate(fullString + r.nextInt());
			}
			return hash;
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String SQL_DELETE = "DELETE FROM synchronize WHERE synchronize_id = :synchronize_id;";
	private static final String SQL_SELECT_BY_ID = "SELECT * FROM synchronize WHERE synchronize_id = :synchronize_id;";
	private static final String SQL_SELECT = "SELECT * FROM synchronize WHERE user_phone = :user_phone;";
	private static final String SQL_INSERT = "INSERT INTO synchronize VALUES (:synchronize_id, :item_type, :item_action, :item_object, :user_phone);";
}
