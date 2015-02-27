package gui;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import records.Assessment;
import records.StudentRecords;
import records.Result;
import gui.PopUpWindow;

/**
 * An object which observes an instance of {@link StudentRecords} to detect
 * when new assignments have been loaded. When this happens, create a table
 * of results for the assessment and put it into a new tab in the results
 * area of the main GUI window.
 * 
 * @author Max Karasinski
 *
 */
public class ResultsTabManager extends JTabbedPane implements Observer {

//	private MainInterface frame;
	private StudentRecords studentRecords;
	
	/**
	 * Creates a new ResultsTabManager, and gives it references to the main GUI
	 * window (so that it can add to the results area), and an instance of
	 * StudentRecords (so that it can find student names).
	 * 
	 * @param frame the main window of the GUI program
	 * @param studentRecords the StudentRecords from the main GUI window
	 */
	public ResultsTabManager(MainInterface frame, StudentRecords studentRecords) {
		studentRecords.addObserver(this);
		this.studentRecords = studentRecords;
		
		setTabLayoutPolicy(this.SCROLL_TAB_LAYOUT);
	}

	/**
	 * Looks through the loaded assessments to see which are not already
	 * displayed in the tabbed pane. Those which are not have their results put
	 * into a {@link JTable}, which is made scrollable before being put into a
	 * new tab.
	 * <p>
	 * The tab will be named according to the module and assessment code of the
	 * underlying {@link Assessment}. For example, "4CCS1PRA-001".
	 */
	@Override
	public void update(Observable o, Object assessments) {
		List<Assessment> assessmentList = (ArrayList<Assessment>) assessments;
		
		String[] columns = {"Student Name", "Student Number", "Mark", "Grade"};
		
		for (Assessment a : assessmentList) {
			// will be, e.g. "4CCS1PRA-001"
			String tabName = a.toString();
			
			// table already exists; skip it
			if (indexOfTab(tabName) >= 0) continue;
			
			
			
			// one row for each result
			Object[][] results = new Object[a.size()][4];
			int rowIndex = 0;
			
			for (ListIterator<Result> it = a.listIterator(); it.hasNext(); ++rowIndex) {
				Result r = it.next();
				String studentNumber = r.getCandKey();
				String studentName;
				
				// possibility that marking codes have not been loaded/are missing
				// so need to avoid having a null Student returned from studentRecords
				if (studentRecords.hasStudent(studentNumber)) {
					studentName = studentRecords.returnStudent(studentNumber).getName();
				} else {
					studentName = "Anonymous Student";
				}
				
				results[rowIndex][0] = studentName;
				results[rowIndex][1] = studentNumber;
				results[rowIndex][2] = r.mark;
				results[rowIndex][3] = r.grade;
			}
			
			/* create the table from the data and give it a mouse listener
			which will make a pop-up window with student info when the name
			is clicked on */
			final JTable table = new JTable(new ResultsTableModel(results, columns));
			
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (table.getSelectedColumn() == 0) {
						int row = table.getSelectedRow();
						String num = (String) table.getValueAt(row, 1);
						
						if (studentRecords.hasStudent(num)) {
							PopUpWindow p = new PopUpWindow(studentRecords.returnStudent(num));
							p.setVisible(true);
						}
					}
				}
			});
			
//			table.setAutoCreateRowSorter(true);
			table.setCellEditor(null);
			JScrollPane scrollPane = new JScrollPane(table);
			addTab(tabName, scrollPane);
		}
	}
}