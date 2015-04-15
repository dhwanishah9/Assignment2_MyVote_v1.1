package myvote;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Moderator {
	@Id
	int id;

	String name;

	@NotNull(message = "Email value should not be Null\n")
	@NotEmpty(message = "Email value cannot be blank")
	String email;

	@NotNull(message = "Password value should not be Null\n")
	@NotEmpty(message = "Password value cannot be blank\n")
	String password;

	String created_at;

	@JsonIgnore
	ArrayList<String> pollslist = new ArrayList<String>();

	public ArrayList<String> getPollslist() {
		return pollslist;
	}

	public void setPollslist(ArrayList<String> pollslist) {
		this.pollslist = pollslist;
	}

	public Moderator() {
	}

	public String getCreated_at() {

		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;

	}

	public Moderator(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

}
