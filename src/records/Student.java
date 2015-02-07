package records;

/**
 * Class to store basic student information, as retrieved from the server via
 * the StudentData jar.
 * 
 * @author Max Karasinski
 */
public class Student {

	private int number;
	private String name;
	private String email;
	private String tutorEmail;

	/**
	 * Constructor.
	 * 
	 * @param num the student's ID number
	 * @param tEmail the student's tutor's email address
	 * @param n the student's name
	 * @param e the student's email address
	 */
	public Student(int num, String tEmail, String n, String e) {
		number = num;
		name = n;
		email = e;
		tutorEmail = tEmail;
	}
	
	/**
	 * @return the number for this student
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * @return the student's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the student's email address
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * @return the student's tutor's email address
	 */
	public String getTutorEmail() {
		return tutorEmail;
	}
	
	/**
	 * @return all the info on the student: number, tutor's email, name, email
	 */
	public String toString() {
		return number + " " + tutorEmail + " " + name + " " + email;
	}
	
}
