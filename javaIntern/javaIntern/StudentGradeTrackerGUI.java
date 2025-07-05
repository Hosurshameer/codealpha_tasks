import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Student {
    private String name;
    private double grade;
    private ImageIcon image;

    public Student(String name, double grade, ImageIcon image) {
        this.name = name;
        this.grade = grade;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }

    public ImageIcon getImage() {
        return image;
    }
}

public class StudentGradeTrackerGUI extends JFrame {
    private ArrayList<Student> students = new ArrayList<>();
    private JTextField nameField;
    private JTextField gradeField;
    private ImageIcon selectedImage = null;
    private JLabel imagePreview;

    public StudentGradeTrackerGUI() {
        setTitle("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input Panel with compact height
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nameField.setPreferredSize(new Dimension(150, 28));
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JLabel gradeLabel = new JLabel("Student Grade:");
        gradeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gradeField = new JTextField();
        gradeField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gradeField.setPreferredSize(new Dimension(150, 28));
        gradePanel.add(gradeLabel);
        gradePanel.add(gradeField);

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JButton uploadButton = new JButton("Upload Image");
        imagePanel.add(uploadButton);
        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(48, 48));
        imagePanel.add(imagePreview);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JButton addButton = new JButton("Add Student");
        JButton summaryButton = new JButton("Show Summary");
        buttonPanel.add(addButton);
        buttonPanel.add(summaryButton);

        inputPanel.add(namePanel);
        inputPanel.add(gradePanel);
        inputPanel.add(imagePanel);
        inputPanel.add(buttonPanel);

        add(inputPanel, BorderLayout.NORTH);

        // Button Actions
        uploadButton.addActionListener(e -> uploadImage());
        addButton.addActionListener(e -> addStudent());
        summaryButton.addActionListener(e -> showSummary());
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            selectedImage = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH));
            imagePreview.setIcon(selectedImage);
        }
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();
        if (name.isEmpty() || gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both name and grade.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double grade;
        try {
            grade = Double.parseDouble(gradeText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Grade must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Store the original image path for summary scaling
        ImageIcon summaryImage = null;
        if (selectedImage != null) {
            Image img = selectedImage.getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH);
            summaryImage = new ImageIcon(img);
        }
        students.add(new Student(name, grade, summaryImage));
        nameField.setText("");
        gradeField.setText("");
        imagePreview.setIcon(null);
        selectedImage = null;
        JOptionPane.showMessageDialog(this, "Student added: " + name + " (" + grade + ")");
    }

    private void showSummary() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to display.", "Summary Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        double total = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        String highestName = "";
        String lowestName = "";

        for (Student s : students) {
            JPanel studentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel imageLabel = new JLabel();
            if (s.getImage() != null) {
                imageLabel.setIcon(s.getImage());
                imageLabel.setPreferredSize(new Dimension(96, 96));
            } else {
                imageLabel.setPreferredSize(new Dimension(96, 96));
            }
            studentPanel.add(imageLabel);
            JLabel infoLabel = new JLabel(s.getName() + ": " + s.getGrade());
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
            studentPanel.add(infoLabel);
            summaryPanel.add(studentPanel);

            total += s.getGrade();
            if (s.getGrade() > highest) {
                highest = s.getGrade();
                highestName = s.getName();
            }
            if (s.getGrade() < lowest) {
                lowest = s.getGrade();
                lowestName = s.getName();
            }
        }
        double average = total / students.size();

        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(new JLabel(String.format("Average Score: %.2f", average)));
        summaryPanel.add(new JLabel(String.format("Highest Score: %.2f (%s)", highest, highestName)));
        summaryPanel.add(new JLabel(String.format("Lowest Score: %.2f (%s)", lowest, lowestName)));

        JScrollPane scrollPane = new JScrollPane(summaryPanel);
        scrollPane.setPreferredSize(new Dimension(420, 350));
        JOptionPane.showMessageDialog(this, scrollPane, "Summary Report", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentGradeTrackerGUI().setVisible(true);
        });
    }
} 