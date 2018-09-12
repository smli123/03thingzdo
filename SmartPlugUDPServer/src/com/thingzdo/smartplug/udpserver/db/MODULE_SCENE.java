package com.thingzdo.smartplug.udpserver.db;
/**
 * mysql> describe module_irscene;
+--------------+---------------------+------+-----+---------+-------+
| Field        | Type                | Null | Key | Default | Extra |
+--------------+---------------------+------+-----+---------+-------+
| id    	   | varchar(32)         | NO   | PRI | NULL    |       |
| module_id    | varchar(32)         | NO   |     | NULL    |       |
| enable       | varchar(32)         | NO   |     | NULL    |       |
| power        | tinyint(1) unsigned | NO   |     | NULL    |       |
| mode         | tinyint(1) unsigned | NO   |     | NULL    |       |
| direction    | tinyint(1) unsigned | NO   |     | NULL    |       |
| temperature  | tinyint(1) unsigned | NO   |     | NULL    |       |
| time         | varchar(32)         | NO   |     | NULL    |       |
| period       | varchar(32)         | NO   |     | NULL    |       |
| irname       | varchar(32)         | NO   |     | NULL    |       |
+--------------+---------------------+------+-----+---------+-------+
7 rows in set (0.00 sec)
 * */
public class MODULE_SCENE {

	public final static String 	TABLE_NAME 				= "module_scene";
	public final static String 	SCENE_ID				= "scene_id";	// 模块ID
	public final static String 	SCENE_NAME				= "scene_name";	// 模块ID
	public final static String 	POWER_ENABLE			= "power_enable";		// 是否使能
	public final static String  POWER_MODULEID			= "power_moduleid";		// 开关
	public final static String  POWER_CONTROL			= "power_control";		// 模式
	public final static String  CURTAIN_ENABLE			= "curtain_enable";	// 风向
	public final static String  CURTAIN_MODULEID		= "curtain_moduleid";		// 风力
	public final static String  CURTAIN_CONTROL			= "curtain_control";// 温度
	public final static String	AIRCON_ENABLE			= "aircon_enable";		// 定时的时间
	public final static String  AIRCON_MODULEID			= "aircon_moduleid";		// 定时周期
	public final static String  AIRCON_TEMPERATURE		= "aircon_temperature";
	public final static String  AIRCON_CONTROL			= "aircon_control";
	public final static String  PC_ENALBE				= "pc_enable";
	public final static String  PC_MODULEID				= "pc_moduleid";
	public final static String  PC_MAC_MODULEID			= "pc_mac_moduleid";
	public final static String  PC_MAC_ADDRESS			= "pc_mac_address";
	public final static String  PC_CONTROL				= "pc_control";
	public final static String  USER_NAME				= "user_name";

	private int    id;
	private int    scene_id;
	private String scene_name;
	private int	   power_enable;
	private String power_moduleid;
	private int	   power_control;
	private int	   curtain_enable;
	private String curtain_moduleid;
	private int	   curtain_control;
	private int    aircon_enable;
	private String aircon_moduleid;
	private int    aircon_temperature;
	private int    aircon_control;
	private int    pc_enable;
	private String pc_moduleid;
	private String pc_mac_moduleid;
	private String pc_mac_address;
	private int    pc_control;
	private String user_name;
	
	public MODULE_SCENE(int sceneid, String scene_name, 
			int power_enable, String power_moduleid, int power_control, 
			int curtain_enable, String curtain_moduleid, int curtain_control, 
			int aircon_enable, String aircon_moduleid, int aircon_temperature, int aircon_control, 
			int pc_enable, String pc_moduleid, String pc_mac_moduleid, String pc_mac_address, int pc_control, String user_name)
	{
		this.setScene_id(sceneid);
		this.setScene_name(scene_name);
		this.setPower_enable(power_enable);
		this.setPower_moduleid(power_moduleid);
		this.setPower_control(power_control);
		this.setCurtain_enable(curtain_enable);
		this.setCurtain_moduleid(curtain_moduleid);
		this.setCurtain_control(curtain_control);
		
		this.setAircon_enable(aircon_enable);
		this.setAircon_moduleid(aircon_moduleid);
		this.setAircon_temperature(aircon_temperature);
		this.setAircon_control(aircon_control);
		
		this.setPc_enable(pc_enable);
		this.setPc_moduleid(pc_moduleid);
		this.setPc_mac_moduleid(pc_mac_moduleid);
		this.setPc_mac_address(pc_mac_address);
		this.setPc_control(pc_control);
		
		this.setUser_name(user_name);
	}
	
	public MODULE_SCENE(String user_name)
	{
		this.setScene_id(0);
		this.setScene_name("");
		this.setPower_enable(0);
		this.setPower_moduleid("");
		this.setPower_control(0);
		this.setCurtain_enable(0);
		this.setCurtain_moduleid("");
		this.setCurtain_control(0);
		
		this.setAircon_enable(0);
		this.setAircon_moduleid("");
		this.setAircon_control(0);
		
		this.setPc_enable(0);
		this.setPc_moduleid("");
		this.setPc_mac_moduleid("");
		this.setPc_mac_address("");
		this.setPc_control(0);
		
		this.setUser_name(user_name);
	}
	
	public boolean Equal(MODULE_SCENE info)
	{
		if(info == null)
		{
			return false;
		}
		if( this.user_name.equalsIgnoreCase(info.getUser_name()) &&
			(this.scene_id == info.getScene_id()))
		{
			return true;
		}
		return false;
	}
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getScene_id() {
		return scene_id;
	}
	public void setScene_id(int scene_id) {
		this.scene_id = scene_id;
	}
	public String getScene_name() {
		return scene_name;
	}
	public void setScene_name(String scene_name) {
		this.scene_name = scene_name;
	}
	public int getPower_enable() {
		return power_enable;
	}
	public void setPower_enable(int power_enable) {
		this.power_enable = power_enable;
	}
	public String getPower_moduleid() {
		return power_moduleid;
	}
	public void setPower_moduleid(String power_moduleid) {
		this.power_moduleid = power_moduleid;
	}
	public int getPower_control() {
		return power_control;
	}
	public void setPower_control(int power_control) {
		this.power_control = power_control;
	}
	public int getPc_enable() {
		return pc_enable;
	}
	public void setPc_enable(int pc_enable) {
		this.pc_enable = pc_enable;
	}
	public String getPc_moduleid() {
		return pc_moduleid;
	}
	public void setPc_moduleid(String pc_moduleid) {
		this.pc_moduleid = pc_moduleid;
	}
	public String getPc_mac_moduleid() {
		return pc_mac_moduleid;
	}
	public void setPc_mac_moduleid(String pc_mac_moduleid) {
		this.pc_mac_moduleid = pc_mac_moduleid;
	}
	public String getPc_mac_address() {
		return pc_mac_address;
	}
	public void setPc_mac_address(String pc_mac_address) {
		this.pc_mac_address = pc_mac_address;
	}
	public int getPc_control() {
		return pc_control;
	}
	public void setPc_control(int pc_control) {
		this.pc_control = pc_control;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getCurtain_enable() {
		return curtain_enable;
	}
	public void setCurtain_enable(int curtain_enable) {
		this.curtain_enable = curtain_enable;
	}
	public int getCurtain_control() {
		return curtain_control;
	}
	public void setCurtain_control(int curtain_control) {
		this.curtain_control = curtain_control;
	}
	public int getAircon_enable() {
		return aircon_enable;
	}
	public void setAircon_enable(int aircon_enable) {
		this.aircon_enable = aircon_enable;
	}
	public String getAircon_moduleid() {
		return aircon_moduleid;
	}
	public void setAircon_moduleid(String aircon_moduleid) {
		this.aircon_moduleid = aircon_moduleid;
	}
	public int getAircon_control() {
		return aircon_control;
	}
	public void setAircon_control(int aircon_control) {
		this.aircon_control = aircon_control;
	}
	public String getCurtain_moduleid() {
		return curtain_moduleid;
	}
	public void setCurtain_moduleid(String curtain_moduleid) {
		this.curtain_moduleid = curtain_moduleid;
	}

	public int getAircon_temperature() {
		return aircon_temperature;
	}

	public void setAircon_temperature(int aircon_temperature) {
		this.aircon_temperature = aircon_temperature;
	}
}