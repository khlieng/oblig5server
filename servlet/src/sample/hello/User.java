package sample.hello;

public class User {
	private int id;
	private String gcmId;
	private String name;
	private String lastPos = "";
	private String color = "";
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getGcmId() { return gcmId; }
	public String getName() { return name; }
	public String getlastPos() { return lastPos; }
	public void setLastPos(String lastPos) { this.lastPos = lastPos; }
	public String getColor() { return color; }
	public void setColor(String color) { this.color = color; }
		
	public User(String gcmId, String name) {
		this.gcmId = gcmId;
		this.name = name;
	}
	
	public boolean equals(Object object) {
		return gcmId.equals(((User)object).gcmId);
	}
}
