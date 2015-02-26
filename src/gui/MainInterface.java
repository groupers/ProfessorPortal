package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.*;

import java.io.File;

import gui.ResultsTabManager;
import records.Student;
import records.StudentRecords;
import io.CSVLoader;

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

	private DefaultListModel<Student> studentListModel;
	private JList<Student> studentList;
	private StudentRecords sr = new StudentRecords();
	private JPanel studentPanel;
	private JPanel dataPanel;
	private JScrollPane studentScroll;
	private JTextField searchText;

	private JMenuBar menuBar;
	private JMenuItem loadCodes;
	private JMenuItem loadResults;
	private JMenuItem averageResults;
	private JMenuItem emailToStudents;
	private JMenuItem emailSettings;

	/**
	 * Constructor for the Interface, setting a size, visibility, exit function and creating the widgets
	 * 
	 */
	public MainInterface(){
		super("PRA Coursework - MNP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000,600);

		createMenuBar();
		createStudentList();
		createDataBox();

		setVisible(true);
	}

	/**
	 * Creates the scrollable list of students by name and ID Number from StudentRecords
	 * 
	 */
	private void createStudentList(){
		setLayout(new BorderLayout());

		studentPanel = new JPanel();
		studentPanel.setLayout(new BorderLayout());
		searchText = new JTextField(25);
		studentListModel = new DefaultListModel<Student>();

		//Adds all students from StudentRecords into the studentListModel
		for (Student student : sr.returnStudents().values()) {
			studentListModel.addElement(student);
		}

		studentList = new JList<Student>(studentListModel);
		studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		studentScroll = new JScrollPane(studentList);

		studentPanel.add(searchText, BorderLayout.NORTH);
		studentPanel.add(studentScroll);

		add(studentPanel, BorderLayout.WEST);

		studentList.addMouseListener(new StudentPressListener());

		SearchFilter sfl = new SearchFilter();
	}

	/**
	 * Creates the MenuBar - Sets to Frame
	 */
	private void createMenuBar(){
		menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu data = new JMenu("Data");

		// menu items
		loadCodes = new JMenuItem("Load anonymous marking codes");
		loadResults = new JMenuItem("Load exam results");
		averageResults = new JMenuItem("Compare to Average");
		emailToStudents = new JMenuItem("Email to Students");
		emailSettings = new JMenuItem("Email Settings");

		// want this to remain greyed-out until at least one set of marking codes
		// has been loaded
		loadResults.setEnabled(false);

		loadCodes.addActionListener(new CSVLoaderListener());
		loadResults.addActionListener(new CSVLoaderListener());

		// add menus to bar, and items to menus
		menuBar.add(file);
		menuBar.add(data);

		file.add(loadCodes);
		file.add(loadResults);
		data.add(averageResults);
		data.add(emailToStudents);
		data.add(emailSettings);

		setJMenuBar(menuBar);
	}

	private void createDataBox() {
		dataPanel = new JPanel();
		dataPanel.setLayout(new BorderLayout());

		ResultsTabManager resultTabs = new ResultsTabManager(this, sr);

		dataPanel.add(resultTabs);
		add(dataPanel);
	}

	/**
	 * MouseListener to make a pop up window of the Student Information appear
	 */
	class StudentPressListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			int index;
			Student stu;
			index = studentList.getSelectedIndex();
			stu = studentListModel.elementAt(index);
			PopUpWindow studentInfo = new PopUpWindow(stu);
			studentInfo.setVisible(true);
		}
	}

	/**
	 * Listener attached to File -> Load X menu items. Constructs a {@link JFileChooser}
	 * which is restricted to files that have a .csv extension. After selecting a
	 * valid file, a new {@link CSVLoader} is constructed which loads the file.
	 * Multiple file selection is allowed.
	 * <p>
	 * Some code taken/inspired by this 
	 * <a href="http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html">Java tutorial</a>.
	 *  
	 *
	 */
	class CSVLoaderListener implements ActionListener {

		private String currentPath = null;

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc;
			if (currentPath != null) {
				fc = new JFileChooser(currentPath);
			} else {
				fc = new JFileChooser();
			}

			// make only folders and csv files visible
			fc.addChoosableFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
			fc.setAcceptAllFileFilterUsed(false);
			fc.setMultiSelectionEnabled(true);

			int returnVal = fc.showOpenDialog(MainInterface.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				CSVLoader loader = new CSVLoader(sr);

				// will start file chooser in this directory next time
				currentPath = files[0].getAbsolutePath();

				for (File file : files) {

					if (e.getSource() == loadCodes) {
						loader.readCSV(file.getAbsolutePath(), CSVLoader.MARKING_CODES);
					} else if (e.getSource() == loadResults) {
						loader.readCSV(file.getAbsolutePath(), CSVLoader.RESULTS);
					}
				}

				if (!loadResults.isEnabled()) loadResults.setEnabled(true);
			}
		}
	}

	/**
	 *SearchFilter keeps track of searchText textArea  by using 
	 *a DocumentListener and updates studentListModel using filteringList().
	 */
	class SearchFilter {

		public SearchFilter(){
			searchText.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					filteringList();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					filteringList();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					filteringList();
				}
			});
		}
	}

	/**
	 * Method to change studentListModel
	 */
	public void filteringList(){
		if (searchText.getText().equals("")){
			for (Student student : sr.returnStudents().values()) {
				studentListModel.addElement(student);
			}
		} else {
			//Compare searchtext Text with student name and student number
			studentListModel.removeAllElements();
			for (Student student : sr.returnStudents().values()) {
				if(( (student.getName()).toLowerCase() ).contains( (searchText.getText()).toLowerCase())
						|| student.getNumber().toString().contains(searchText.getText()) ){
					studentListModel.addElement(student);
				}
			}
		}
	}


	public static void main (String[] args){
		MainInterface mi = new MainInterface();
	}
}