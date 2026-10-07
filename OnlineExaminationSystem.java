import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Question class
class Question {
    private String question;
    private ArrayList<String> options;
    private int correctAnswer;

    public Question(String question, String[] options, int correctAnswer) {
        this.question = question;
        this.options = new ArrayList<>();

        for (String option : options) {
            this.options.add(option);
        }

        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}

// Main class
public class OnlineExaminationSystem extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private ArrayList<Question> questions = new ArrayList<>();
    private int[] selectedAnswers;

    private int currentQuestion = 0;

    private JLabel questionLabel;
    private JLabel questionNumberLabel;
    private JLabel timerLabel;

    private JRadioButton[] optionButtons = new JRadioButton[4];
    private ButtonGroup optionGroup;

    private JButton previousButton;
    private JButton nextButton;
    private JButton submitButton;

    // 5-minute examination
    private int timeLeft = 5 * 60;
    private Timer timer;

    private String studentName;

    public OnlineExaminationSystem() {
        setTitle("Online Examination System");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadQuestions();
        showLoginScreen();
    }

    // Add questions to ArrayList
    private void loadQuestions() {

        questions.add(new Question(
                "Which language is used for Android development?",
                new String[]{"Java", "HTML", "CSS", "SQL"},
                0
        ));

        questions.add(new Question(
                "Which collection stores elements in a dynamic array?",
                new String[]{"HashSet", "ArrayList", "TreeSet", "HashMap"},
                1
        ));

        questions.add(new Question(
                "Which keyword is used to inherit a class in Java?",
                new String[]{"implements", "inherits", "extends", "super"},
                2
        ));

        questions.add(new Question(
                "Which method is the entry point of a Java program?",
                new String[]{"start()", "run()", "main()", "execute()"},
                2
        ));

        questions.add(new Question(
                "Which of the following is not a primitive data type?",
                new String[]{"int", "float", "String", "char"},
                2
        ));

        selectedAnswers = new int[questions.size()];

        // -1 means no answer selected
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;
        }
    }

    // Login screen
    private void showLoginScreen() {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("ONLINE EXAMINATION SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> login());
    }

    // Login validation
    private void login() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
            );
            return;
        }

        // Simple login validation
        if (username.equals("student") && password.equals("1234")) {

            studentName = username;

            getContentPane().removeAll();

            showExamScreen();

            revalidate();
            repaint();

            startTimer();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid login!\nUsername: student\nPassword: 1234"
            );
        }
    }

    // Examination screen
    private void showExamScreen() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());

        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.RED);

        topPanel.add(questionNumberLabel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);

        // Center panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 17));

        questionPanel.add(questionLabel);
        questionPanel.add(Box.createVerticalStrut(20));

        optionGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {

            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 15));

            optionGroup.add(optionButtons[i]);
            questionPanel.add(optionButtons[i]);

            questionPanel.add(Box.createVerticalStrut(10));
        }

        // Bottom buttons
        JPanel buttonPanel = new JPanel();

        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit Exam");

        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(submitButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        previousButton.addActionListener(e -> previousQuestion());
        nextButton.addActionListener(e -> nextQuestion());
        submitButton.addActionListener(e -> confirmSubmit());

        displayQuestion();
    }

    // Display current question
    private void displayQuestion() {

        Question q = questions.get(currentQuestion);

        questionNumberLabel.setText(
                "Question " + (currentQuestion + 1) +
                        " of " + questions.size()
        );

        questionLabel.setText(
                "<html>" + q.getQuestion() + "</html>"
        );

        optionGroup.clearSelection();

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q.getOptions().get(i));
        }

        // Restore previously selected answer
        if (selectedAnswers[currentQuestion] != -1) {
            optionButtons[selectedAnswers[currentQuestion]].setSelected(true);
        }

        previousButton.setEnabled(currentQuestion > 0);

        if (currentQuestion == questions.size() - 1) {
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }
    }

    // Save selected answer
    private void saveAnswer() {

        for (int i = 0; i < 4; i++) {

            if (optionButtons[i].isSelected()) {
                selectedAnswers[currentQuestion] = i;
                return;
            }
        }
    }

    // Next question
    private void nextQuestion() {

        saveAnswer();

        if (currentQuestion < questions.size() - 1) {
            currentQuestion++;
            displayQuestion();
        }
    }

    // Previous question
    private void previousQuestion() {

        saveAnswer();

        if (currentQuestion > 0) {
            currentQuestion--;
            displayQuestion();
        }
    }

    // Start countdown timer
    private void startTimer() {

        timer = new Timer(1000, e -> {

            timeLeft--;

            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;

            timerLabel.setText(
                    String.format("Time Left: %02d:%02d", minutes, seconds)
            );

            if (timeLeft <= 0) {

                timer.stop();

                JOptionPane.showMessageDialog(
                        this,
                        "Time is up! Your examination will be submitted automatically."
                );

                submitExam();
            }
        });

        timer.start();

        timerLabel.setText("Time Left: 05:00");
    }

    // Confirm submission
    private void confirmSubmit() {

        saveAnswer();

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit the examination?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    // Submit examination
    private void submitExam() {

        if (timer != null) {
            timer.stop();
        }

        saveAnswer();

        int marks = 0;

        for (int i = 0; i < questions.size(); i++) {

            if (selectedAnswers[i] ==
                    questions.get(i).getCorrectAnswer()) {

                marks++;
            }
        }

        double percentage =
                ((double) marks / questions.size()) * 100;

        String grade = calculateGrade(percentage);

        saveResult(marks, percentage, grade);

        showResult(marks, percentage, grade);
    }

    // Calculate grade
    private String calculateGrade(double percentage) {

        if (percentage >= 90)
            return "A+";
        else if (percentage >= 80)
            return "A";
        else if (percentage >= 70)
            return "B";
        else if (percentage >= 60)
            return "C";
        else if (percentage >= 50)
            return "D";
        else
            return "F";
    }

    // Display result
    private void showResult(
            int marks,
            double percentage,
            String grade) {

        getContentPane().removeAll();

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(
                new BoxLayout(resultPanel, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("EXAMINATION RESULT");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel =
                new JLabel("Student: " + studentName);

        JLabel marksLabel =
                new JLabel("Marks: " + marks + " / " + questions.size());

        JLabel percentageLabel =
                new JLabel(String.format(
                        "Percentage: %.2f%%", percentage
                ));

        JLabel gradeLabel =
                new JLabel("Grade: " + grade);

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        marksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultPanel.add(Box.createVerticalStrut(40));
        resultPanel.add(title);
        resultPanel.add(Box.createVerticalStrut(30));
        resultPanel.add(nameLabel);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(marksLabel);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(percentageLabel);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(gradeLabel);

        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultPanel.add(Box.createVerticalStrut(30));
        resultPanel.add(exitButton);

        exitButton.addActionListener(e -> System.exit(0));

        add(resultPanel);

        revalidate();
        repaint();
    }

    // Save result to text file
    private void saveResult(
            int marks,
            double percentage,
            String grade) {

        try {

            FileWriter writer =
                    new FileWriter("exam_results.txt", true);

            String date =
                    new SimpleDateFormat(
                            "dd-MM-yyyy HH:mm:ss"
                    ).format(new Date());

            writer.write("====================================\n");
            writer.write("ONLINE EXAMINATION RESULT\n");
            writer.write("Date: " + date + "\n");
            writer.write("Student: " + studentName + "\n");
            writer.write(
                    "Marks: " + marks +
                            " / " + questions.size() + "\n"
            );
            writer.write(
                    String.format(
                            "Percentage: %.2f%%\n",
                            percentage
                    )
            );
            writer.write("Grade: " + grade + "\n");
            writer.write("====================================\n\n");

            writer.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving result: " + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            OnlineExaminationSystem exam =
                    new OnlineExaminationSystem();

            exam.setVisible(true);
        });
    }
}
