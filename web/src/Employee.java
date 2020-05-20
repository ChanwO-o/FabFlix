public class Employee {
	private final String email;
	private final String fullname;

	public Employee(String email) {
		this.email = email;
		this.fullname = "";
	}

	public String getEmail() {
		return email;
	}

	public String getFullname() {
		return fullname;
	}
}
