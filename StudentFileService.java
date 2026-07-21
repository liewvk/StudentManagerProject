import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StudentFileService {
    private static final String FILE_NAME = "students.csv";

    public ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Student student = Student.fromCsv(line);

                if (student != null) {
                    students.add(student);
                }
            }

        } catch (IOException e) {
            // If the file does not exist yet, start with an empty list.
        }

        return students;
    }

    public void saveStudents(ArrayList<Student> students) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.println(student.toCsv());
            }
        }
    }
}
