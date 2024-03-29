package foodbank.it.keycloak;

import javax.persistence.*;

@NamedQueries({
		@NamedQuery(name="getUserByUsername", query="select u from UserEntity u where u.id = :username"),
		@NamedQuery(name="getUserByEmail", query="select u from UserEntity u where u.email = :email"),
		@NamedQuery(name="getUserCount", query="select count(u) from UserEntity u"),
		@NamedQuery(name="getAllUsers", query="select u from UserEntity u"),
		@NamedQuery(name="searchForUser", query="select u from UserEntity u where " +
				"( lower(u.id) like :search or lower(u.firstAndLastName) like :search or u.email like :search ) order by u.firstAndLastName"),
		@NamedQuery(name="searchForGroupMembers", query="select u from UserEntity u where u.rights like :search order by u.firstAndLastName")
})
@Entity
@Table(name = "t_user")
public class UserEntity {

	@Id
	@Column(name = "id_user")
	private String id;
	@Column(name = "user_name")
	private String firstAndLastName;
	@Column(name = "email")
	private String email;
	@Column(name = "password")
	private String password;
	@Column(name = "rights")
	private String rights;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstAndLastName() {
		return firstAndLastName;
	}

	public void setFirstAndLastName(String firstAndLastName) {
		this.firstAndLastName = firstAndLastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRights() {
		return rights;
	}

	public UserEntity setRights(String rights) {
		this.rights = rights;
		return this;
	}
}