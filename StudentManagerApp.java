import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class StudentManagerApp extends JFrame {
    private ArrayList<Student> students;
    private StudentFileService fileService;

    private JTextField studentIdField;
    private JTextField nameField;
    private JTextField markField;
    private JTextField searchField;

    private JComboBox<String> courseBox;

    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentManagerApp() {
        fileService = new StudentFileService();
        students = fileService.loadStudents();

        setTitle("Student Manager App");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildInterface();
        refreshTable();

        setVisible(true);
    }

    private void buildInterface() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        studentIdField = new JTextField();
        nameField = new JTextField();
        markField = new JTextField();
        searchField = new JTextField();

        String[] courses = {
                "Java Programming",
                "Python Programming",
                "Web Development",
                "Database",
                "Software Engineering"
        };

        courseBox = new JComboBox<>(courses);

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseBox);

        formPanel.add(new JLabel("Mark:"));
        formPanel.add(markField);

        formPanel.add(new JLabel("Search ID/Name:"));
        formPanel.add(searchField);

        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(showAllButton);
        buttonPanel.add(clearButton);

        String[] columns = {
                "Student ID",
                "Name",
                "Course",
                "Mark",
                "Grade",
                "Result"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        showAllButton.addActionListener(e -> refreshTable());
        clearButton.addActionListener(e -> clearFields());

        studentTable.getSelectionModel().addListSelectionListener(e -> fillFieldsFromSelectedRow());
    }

    private void addStudent() {
        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String course = (String) courseBox.getSelectedItem();

        Double mark = readMark();

        if (studentId.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and name cannot be empty.");
            return;
        }

        if (mark == null) {
            return;
        }

        if (findStudentById(studentId) != null) {
            JOptionPane.showMessageDialog(this, "Student ID already exists.");
            return;
        }

        Student student = new Student(studentId, name, course, mark);
        students.add(student);

        saveData();
        refreshTable();
        clearFields();

        JOptionPane.showMessageDialog(this, "Student added successfully.");
    }

    private void updateStudent() {
        String studentId = studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter a student ID.");
            return;
        }

        Student student = findStudentById(studentId);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        String name = nameField.getText().trim();
        String course = (String) courseBox.getSelectedItem();
        Double mark = readMark();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            return;
        }

        if (mark == null) {
            return;
        }

        student.setName(name);
        student.setCourse(course);
        student.setMark(mark);

        saveData();
        refreshTable();
        clearFields();

        JOptionPane.showMessageDialog(this, "Student updated successfully.");
    }

    private void deleteStudent() {
        String studentId = studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter a student ID.");
            return;
        }

        Student student = findStudentById(studentId);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
        this,
        "Delete student " + student.getName() + "?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION
);
        if (  confirm == JOptionPane.YES_OPTION) {
            students.remove(student);

            saveData();
            refreshTable();
            clearFields();

            JOptionPane.showMessageDialog(this, "Student deleted successfully.");
        }
    }

    private void searchStudent() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student ID or name to search.");
            return;
        }

        ArrayList<Student> results = new ArrayList<>();

        for (Student student : students) {
            if (student.getStudentId().toLowerCase().contains(keyword)
                    || student.getName().toLowerCase().contains(keyword)) {
                results.add(student);
            }
        }

        refreshTable(results);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching student found.");
        }
    }

    private Double readMark() {
        String markText = markField.getText().trim();

        if (markText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mark cannot be empty.");
            return null;
        }

        try {
            double mark = Double.parseDouble(markText);

            if (mark < 0 || mark > 100) {
                JOptionPane.showMessageDialog(this, "Mark must be between 0 and 100.");
                return null;
            }

            return mark;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid mark.");
            return null;
        }
    }

    private Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(studentId)) {
                return student;
            }
        }

        return null;
    }

    private void refreshTable() {
        refreshTable(students);
    }

    private void refreshTable(ArrayList<Student> studentList) {
        tableModel.setRowCount(0);

        for (Student student : studentList) {
            Object[] row = {
                    student.getStudentId(),
                    student.getName(),
                    student.getCourse(),
                    student.getMark(),
                    student.getGrade(),
                    student.getResult()
            };

            tableModel.addRow(row);
        }
    }

    private void fillFieldsFromSelectedRow() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow >= 0) {
            studentIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            courseBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
            markField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    private void clearFields() {
        studentIdField.setText("");
        nameField.setText("");
        markField.setText("");
        searchField.setText("");

        if (courseBox.getItemCount() > 0) {
            courseBox.setSelectedIndex(0);
        }

        studentTable.clearSelection();
    }

    private void saveData() {
        try {
            fileService.saveStudents(students);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerApp());
    }
}
