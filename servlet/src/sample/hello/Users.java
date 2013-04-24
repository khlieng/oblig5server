package sample.hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Users
{
	private static Connection conn;

	public static void connect(String url, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
		} catch(Exception e) { }
	}

	public static void disconnect() {
		try {
			conn.close();
		} catch(Exception e) { }
	}

	public static int insert(User user) {
		try {
			Statement statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO reg (gcmid, name, color) VALUES ('" + 
					user.getGcmId() + "','" + user.getName() + "','" + user.getColor() + "')", Statement.RETURN_GENERATED_KEYS);
			ResultSet keys = statement.getGeneratedKeys();
			if (keys.next()) {
				return keys.getInt(1);
			}
			else {
				return -2;
			}
		} catch (SQLException e) {
			PostOffice.pushMessageWeb("msg:" + e.getMessage());
			return -1;
		}
	}
	
	public static void update(User user) {
		try {
			conn.createStatement().executeUpdate("UPDATE reg SET gcmid='" + user.getGcmId() + 
					"', name='" + user.getName() + "', lastpos='" + user.getlastPos() + "', color='" + user.getColor() + "' WHERE id=" + user.getId());
		} catch (SQLException e) { }
	}
	
	public static User get(int id) {
		try {
			ResultSet result = conn.createStatement().executeQuery("SELECT * FROM reg WHERE id=" + id);
			if (result.next()) {
				User user = new User(result.getString("gcmid"), result.getString("name"));
				user.setId(id);
				user.setLastPos(result.getString("lastpos"));
				user.setColor(result.getString("color"));
				return user;
			}
			else {
				return null;
			}
		} catch (SQLException e) { 
			return null;
		}
	}
	
	public static User get(String gcmId) {
		try {
			ResultSet result = conn.createStatement().executeQuery("SELECT * FROM reg WHERE gcmid='" + gcmId + "'");
			if (result.next()) {
				User user = new User(gcmId, result.getString("name"));
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
		}
	}
	
	public static User getByName(String name) {
		try {
			ResultSet result = conn.createStatement().executeQuery("SELECT * FROM reg WHERE name='" + name + "'");
			if (result.next()) {
				User user = new User(result.getString("gcmId"), name);
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
		}
	}
	
	public static List<User> getAll() {
		try {
			List<User> users = new ArrayList<User>();
			ResultSet result = conn.createStatement().executeQuery("SELECT * FROM reg");
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
		}
	}
	
	public static List<String> getGcmIds() {
		try {
			List<String> ids = new ArrayList<String>();
			ResultSet result = conn.createStatement().executeQuery("SELECT gcmid FROM reg");
			while (result.next()) {
				ids.add(result.getString("gcmid"));
			}
			return ids;
		} catch (SQLException e) {
			return new ArrayList<String>();
		}
	}
	
	public static void delete(int id) {
		try {
			conn.createStatement().executeUpdate("DELETE FROM reg WHERE id=" + id);
		} catch (SQLException e) { }
	}
}