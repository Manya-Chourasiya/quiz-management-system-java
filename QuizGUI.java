import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

class Question {
    String question, answer;

    Question(String q, String a) {
        this.question = q;
        this.answer = a;
    }

    @Override
    public String toString() {
        return question + " | Answer: " + answer;
    }
}

class Student {
    String id, name;
    int score;

    Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
    }

    public void takeQuiz(JFrame parent, ArrayList<Question> quiz) {
        int correct = 0;
        for (Question q : quiz) {
            String ans = JOptionPane.showInputDialog(parent, q.question);
            if (ans != null && ans.equalsIgnoreCase(q.answer)) {
                correct++;
            }
        }
        this.score = correct;
        JOptionPane.showMessageDialog(parent, "Your Score: " + score + "/" + quiz.size());

        // Save result permanently
        saveResult();
    }

    private void saveResult() {
        try (FileWriter fw = new FileWriter("results.txt", true)) {
            fw.write("Student: " + name + " (" + id + ") | Score: " + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Student: " + name + " (" + id + ") | Score: " + score;
    }
}

public class QuizGUI {
    private static ArrayList<Question> quiz = new ArrayList<>();
    private static ArrayList<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        loadQuiz(); // Load quiz from file if exists

        JFrame frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton teacherBtn = new JButton("Teacher Login");
        JButton studentBtn = new JButton("Student Login");
        JButton exitBtn = new JButton("Exit");

        teacherBtn.addActionListener(e -> openTeacherFrame());
        studentBtn.addActionListener(e -> openStudentFrame());
        exitBtn.addActionListener(e -> System.exit(0));

        frame.setLayout(new GridLayout(3, 1));
        frame.add(teacherBtn);
        frame.add(studentBtn);
        frame.add(exitBtn);

        frame.setVisible(true);
    }

    private static void openTeacherFrame() {
        JFrame teacherFrame = new JFrame("Teacher Panel");
        teacherFrame.setSize(400, 400);
        teacherFrame.setLayout(new GridLayout(4, 1));

        JButton addQuizBtn = new JButton("Set Quiz");
        JButton showQuizBtn = new JButton("Show Quiz");
        JButton showResultsBtn = new JButton("Show Student Results");
        JButton backBtn = new JButton("Back");

        addQuizBtn.addActionListener(e -> {
            String q = JOptionPane.showInputDialog("Enter Question:");
            String a = JOptionPane.showInputDialog("Enter Answer:");
            if (q != null && a != null) {
                quiz.add(new Question(q, a));
                saveQuiz(); // Save after adding
            }
        });

        showQuizBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Question ques : quiz) sb.append(ques).append("\n");
            JOptionPane.showMessageDialog(teacherFrame, sb.length() > 0 ? sb.toString() : "No Questions");
        });

        showResultsBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader("results.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException ex) {
                sb.append("No results yet!");
            }
            JOptionPane.showMessageDialog(teacherFrame, sb.toString());
        });

        backBtn.addActionListener(e -> teacherFrame.dispose());

        teacherFrame.add(addQuizBtn);
        teacherFrame.add(showQuizBtn);
        teacherFrame.add(showResultsBtn);
        teacherFrame.add(backBtn);

        teacherFrame.setVisible(true);
    }

    private static void openStudentFrame() {
        JFrame studentFrame = new JFrame("Student Panel");
        studentFrame.setSize(400, 400);
        studentFrame.setLayout(new GridLayout(3, 1));

        JButton takeQuizBtn = new JButton("Take Quiz");
        JButton showResultBtn = new JButton("Show My Result");
        JButton backBtn = new JButton("Back");

        String id = JOptionPane.showInputDialog("Enter Student ID:");
        String name = JOptionPane.showInputDialog("Enter Student Name:");
        Student student = new Student(id, name);
        students.add(student);

        takeQuizBtn.addActionListener(e -> {
            if (quiz.isEmpty()) {
                JOptionPane.showMessageDialog(studentFrame, "Quiz not set by teacher yet!");
            } else {
                student.takeQuiz(studentFrame, quiz);
            }
        });

        showResultBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(studentFrame, student.toString());
        });

        backBtn.addActionListener(e -> studentFrame.dispose());

        studentFrame.add(takeQuizBtn);
        studentFrame.add(showResultBtn);
        studentFrame.add(backBtn);

        studentFrame.setVisible(true);
    }

    private static void saveQuiz() {
        try (FileWriter fw = new FileWriter("quiz.txt")) {
            for (Question q : quiz) {
                fw.write(q.question + "|" + q.answer + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadQuiz() {
        try (BufferedReader br = new BufferedReader(new FileReader("quiz.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    quiz.add(new Question(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("No quiz file found, starting fresh.");
        }
    }
}
