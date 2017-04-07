package com.quark.app.bean;

public class ScheduleBean {

	private String hour = "";
	public int hour_index;
	// 1-unavaliable，2-busy,3-有空
	private int schedule_state;
	private String schedule_state_message = "";
	// 课程
	private String remarks = "";// 备注
	private int course_id;// 课程ID
	private String course_name = "";// 课程名称
	private String course_time = "";// 课程时间
	private String course_location = "";// 课程地址
	private String orders_schedule_id = "0";//
	private String username = "";// 
	
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public int getHour_index() {
		return hour_index;
	}

	public void setHour_index(int hour_index) {
		this.hour_index = hour_index;
	}

	public int getSchedule_state() {
		return schedule_state;
	}

	public void setSchedule_state(int schedule_state) {
		this.schedule_state = schedule_state;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getCourse_id() {
		return course_id;
	}

	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}

	public String getCourse_name() {
		return course_name;
	}

	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}

	public String getCourse_time() {
		return course_time;
	}

	public void setCourse_time(String course_time) {
		this.course_time = course_time;
	}

	public String getCourse_location() {
		return course_location;
	}

	public void setCourse_location(String course_location) {
		this.course_location = course_location;
	}

	public String getSchedule_state_message() {
		return schedule_state_message;
	}

	public void setSchedule_state_message(String schedule_state_message) {
		this.schedule_state_message = schedule_state_message;
	}

	public String getOrders_schedule_id() {
		return orders_schedule_id;
	}

	public void setOrders_schedule_id(String orders_schedule_id) {
		this.orders_schedule_id = orders_schedule_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
