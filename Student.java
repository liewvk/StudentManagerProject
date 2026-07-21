public class Student {
    private String studentId;
    private String name;
    private String course;
    private double mark;

    public Student(String studentId, String name, String course, double mark) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.mark = mark;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public double getMark() {
        return mark;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public String getGrade() {
        if (mark >= 80) {
            return "A";
        } else if (mark >= 70) {
            return "B";
        } else if (mark >= 60) {
            return "C";
        } else if (mark >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    public String getResult() {
        if (mark >= 50) {
            return "Pass";
        } else {
            return "Fail";
        }
    }

    public String toCsv() {
        return studentId + "," + name + "," + course + "," + mark;
    }

    public static Student fromCsv(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length != 4) {
            return null;
        }

        try {
            String studentId = parts[0];
            String name = parts[1];
            String course = parts[2];
            double mark = Double.parseDouble(parts[3]);

            return new Student(studentId, name, course, mark);

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
