package com.akebrett;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class Users
{
	private static ComboPooledDataSource ds;

	public static void connect(String url, String username, String password) {
		try {
			ds = new ComboPooledDataSource();
			ds.setDriverClass("com.mysql.jdbc.Driver");
			ds.setJdbcUrl("jdbc:mysql://" + url);
			ds.setUser(username);
			ds.setPassword(password);
		} catch(Exception e) { }
	}

	public static void disconnect() {
		try {
			DataSources.destroy(ds);
		} catch(Exception e) { }
	}

	public static int insert(User user) {
		Connection conn = null;
		Statement statement = null;
		ResultSet keys = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO reg (gcmid, name, color) VALUES ('" + 
					user.getGcmId() + "','" + user.getName() + "','" + user.getColor() + "')", Statement.RETURN_GENERATED_KEYS);
			keys = statement.getGeneratedKeys();
			if (keys.next()) {
				return keys.getInt(1);
			}
			else {
				return -2;
			}
		} catch (SQLException e) {
			return -1;
		} finally {
			try {
				if (keys != null) keys.close();
				if (statement != null) statement.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) { }
		}
	}
	
	public static void update(User user) {
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			statement.executeUpdate("UPDATE reg SET gcmid='" + user.getGcmId() + 
					"', name='" + user.getName() + "', lastpos='" + user.getlastPos() + "', color='" + user.getColor() + "' WHERE id=" + user.getId());
		} catch (SQLException e) {			
		} finally {
			try {
				if (statement != null) statement.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) { }
		}
	}
	
	public static User get(int id) {
		return query("SELECT * FROM reg WHERE id=" + id);
	}
	
	public static User get(String gcmId) {
		return query("SELECT * FROM reg WHERE gcmid='" + gcmId + "'");
	}
	
	public static User getByName(String name) {
		return query("SELECT * FROM reg WHERE name='" + name + "'");
	}
		
	private static User query(String sql) {
		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			result = statement.executeQuery(sql);
			if (result.next()) {
				User user = new User(result.getString("gcmid"), result.getString("name"));
				user.setId(result.getInt("id"));
				user.setLastPos(result.getString("lastpos"));
				user.setColor(result.getString("color"));
				return user;
			}
			else {
				return null;
			}
		} catch (SQLException e) { 
			return null;
		} finally {
			try {
				if (result != null) result.close();
				if (statement != null) statement.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) { }
		}
	}
	
	public static List<User> getAll() {
		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			result = statement.executeQuery("SELECT * FROM reg");
			
			List<User> users = new ArrayList<User>();
			while (result.next()) {
				User user = new User(result.getString("gcmid"), result.getString("name"));
				user.setId(result.getInt("id"));
				user.setLastPos(result.getString("lastpos"));
				user.setColor(result.getString("color"));
				users.add(user);
			}
			return users;
		} catch (SQLException e) { 
			return new ArrayList<User>();
		} finally {
			try {
				if (result != null) result.close();
				if (statement != null) statement.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) { }
		}
	}
	
	public static List<String> getGcmIds() {
		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			result = statement.executeQuery("SELECT gcmid FROM reg");
			
			List<String> ids = new ArrayList<String>();
			while (result.next()) {
				ids.add(result.getString("gcmid"));
			}
			return ids;
		} catch (SQLException e) {
			return new ArrayList<String>();
		} finally {
			try {
				if (result != null) result.close();
				if (statement != null) statement.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) { }
		}
	}
	
	public static void delete(int id) {
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			statement.executeUpdate("DELETE FROM reg WHERE id=" + id);
		} catch (SQLException e) { 
		} finally {
			try {
				if (statement != null) statement.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) { }
		}
	}
}