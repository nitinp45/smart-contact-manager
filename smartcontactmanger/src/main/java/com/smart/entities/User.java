package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="User")
public class User {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		private int id;
		
		
		@NotBlank(message="Name field is required!")
		@Size(min = 2, max = 10, message = "length shoud be in between 2 to 10")
		private String name;
		

		@NotEmpty(message = "Email field should not be empty")
		@Email(regexp = "^(.+)@(.+)$", message = "Invalid email pattern")
		private String email;
		private String role;
		private String password;
		private boolean enabled;
		
		@Column(length=500)
		private String about;
		
		@OneToMany(cascade=CascadeType.ALL,mappedBy="user")
		private List<Contact> contacts=new ArrayList<>();

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getAbout() {
			return about;
		}

		public void setAbout(String about) {
			this.about = about;
		}
		

		public List<Contact> getContacts() {
			return contacts;
		}

		public void setContacts(List<Contact> contacts) {
			this.contacts = contacts;
		}

		public User() {
			super();
			// TODO Auto-generated constructor stub
		}

//		@Override
//		public String toString() {
//			return "User [id=" + id + ", name=" + name + ", email=" + email + ", role=" + role + ", password="
//					+ password + ", enabled=" + enabled + ", about=" + about + ", contacts=" + contacts + "]";
//		}
		
		
		
		
}
