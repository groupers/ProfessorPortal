package gui;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import records.Student;
import records.StudentRecords;

/**
 * Class to create an interface containing Scrollable list of students
 * Student Search function and Information Pop Up window of each student
 * 
 * @author Phileas Hocquard
 * @author Nikita Vorontsov
 * @author Max Karasinski
 *
 */
public class MainInterface extends JFrame {
	
	Student test = new Student(10, "tutor@tutormail.com", "Test", "studentemail@mail.com");
	private DefaultListModel<String> studentListModel;
	private JList<String> studentList;
	private StudentRecords sr = new StudentRecords();
	
	/**
	 * Constructor for the Interface, setting a size, visibility, exit function and creating the widgets
	 * 
	 */
	public MainInterface(){
		super("PRA Coursework - MNP");
		createStudentList();
		createMenuBar();
		setSize(500,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Creates the scrollable list of students by name and ID Number from StudentRecords
	 * 
	 */
	private void createStudentList(){
		setLayout(new BorderLayout());
		JPanel studentPanel = new JPanel();
		
		studentListModel = new DefaultListModel<String>();
		studentListModel.addElement(test.getName()+ "(" + test.getNumber() + ")");
		
		for (int i = 0; i < sr.numOfStudents()-1; i++) {
			studentListModel.addElement(sr.returnStudent(i).getName() + " (" + sr.returnStudent(i).getNumber() + ")");
		}
		studentList = new JList<String>(studentListModel);
		studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane studentScroll = new JScrollPane(studentList);
		studentPanel.add(studentScroll);
		add(studentScroll, BorderLayout.WEST);
		
	}
	
	/**
	 * Creates the MenuBar - Sets to Frame
	 */
	private void createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu data = new JMenu("Data");
		
		menuBar.add(file);
		menuBar.add(data);
		setJMenuBar(menuBar);
	}
		
	public static void main (String[] args){
		MainInterface mi = new MainInterface();
//		StudentRecords sr = new StudentRecords();
//		System.out.println(sr.returnStudents());
	}
}

/**
JButton buttonTest = new JButton("Test");
window.add(buttonTest);

class StudentPopUpListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(window, "Test Student Dialog");
	}
}

buttonTest.addActionListener(new StudentPopUpListener());
 */