package bean;

import java.util.ArrayList;
import java.util.Date;

public class Institute {
	private String loginid,password,university_name,college_name,state,city,email_id,phone_no,address;
	private ArrayList<ArrayList<String>> departments;
	private ArrayList<String> tags,project_id,question_id,question_asked;
	public ArrayList<String> getProject_id() {
		return project_id;
	}
	public void setProject_id(ArrayList<String> project_id) {
		this.project_id = project_id;
	}
	public ArrayList<String> getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(ArrayList<String> question_id) {
		this.question_id = question_id;
	}
	public ArrayList<String> getQuestion_asked() {
		return question_asked;
	}
	public void setQuestion_asked(ArrayList<String> question_asked) {
		this.question_asked = question_asked;
	}
	private Date date;
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUniversity_name() {
		return university_name;
	}
	public void setUniversity_name(String university_name) {
		this.university_name = university_name;
	}
	public String getCollege_name() {
		return college_name;
	}
	public void setCollege_name(String college_name) {
		this.college_name = college_name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ArrayList<ArrayList<String>> getDepartments() {
		return departments;
	}
	public void setDepartments(ArrayList<ArrayList<String>> departments) {
		this.departments = departments;
	}
	public Institute(String loginid, String password, String university_name, String college_name, String state,
			String city, String email_id, String phone_no, String address, ArrayList<ArrayList<String>> departments,
			ArrayList<String> tags, Date date) {
		super();
		this.loginid = loginid;
		this.password = password;
		this.university_name = university_name;
		this.college_name = college_name;
		this.state = state;
		this.city = city;
		this.email_id = email_id;
		this.phone_no = phone_no;
		this.address = address;
		this.departments = departments;
		this.tags = tags;
		this.date = date;
	}
	public Institute() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Institute [loginid=" + loginid + ", password=" + password + ", university_name=" + university_name
				+ ", college_name=" + college_name + ", state=" + state + ", city=" + city + ", email_id=" + email_id
				+ ", phone_no=" + phone_no + ", address=" + address + ", departments=" + departments + ", tags=" + tags
				+ ", date=" + date + "]";
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
