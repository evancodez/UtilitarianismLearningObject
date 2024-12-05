import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UtilitarianismLearningObject {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean trackSwitched = false;
    private boolean trolleyStarted = false;
    private int trolleyX = 50;
    private Timer trolleyTimer;
    private boolean trolleyDone = false;
    private boolean trolleyAnswerCorrect = false;
    private int quizScore = 0;
    private int currentQuestionIndex = 0;
    private JTextArea resultTextArea; 

    private boolean topPeopleAlive = true;
    private boolean bottomPersonAlive = true;

    private String[][] quizData = {
        {"Utilitarianism primarily focuses on:", "Consequences", "Intentions", "Divine Command", "Consequences"},
        {"In the Trolley Problem, utilitarians would likely:", "Save the 5 by sacrificing 1", "Let the 5 die to save 1", "Do nothing", "Save the 5 by sacrificing 1"},
        {"Utilitarianism suggests we should:", "Maximize overall happiness", "Maximize personal gain", "Always follow rules regardless of outcome", "Maximize overall happiness"}
    };

    private ButtonGroup answerGroup;
    private JRadioButton[] options;
    private JLabel questionLabel;

    private JTextArea trolleyFeedbackTextArea;
    private JButton retryButton;
    private JButton continueButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UtilitarianismLearningObject().showGUI();
        });
    }

    private void showGUI() {
        frame = new JFrame("Understanding Utilitarianism");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createIntroPanel(), "Intro");
        mainPanel.add(createTrolleyPanel(), "Trolley");
        mainPanel.add(createTrolleyFeedbackPanel(), "TrolleyFeedback");
        mainPanel.add(createMoreInfoPanel(), "MoreInfo");
        mainPanel.add(createQuizPanel(), "Quiz");
        mainPanel.add(createResultPanel(), "Result");

        frame.add(mainPanel);
        frame.setSize(800,600);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "Intro");
    }

    private JPanel createIntroPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Welcome to Utilitarianism 101!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        p.add(title, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(
            "Utilitarianism is the ethical theory that we should choose the action that creates the greatest " +
            "overall happiness or well-being for the most people.\n\n" +
            "Consider the Trolley Problem: There's a runaway trolley heading towards five people. " +
            "You can pull a lever to switch its path so it only hits one person instead of five. " +
            "From a utilitarian viewpoint, the action that results in fewer deaths is often seen as better.\n\n" +
            "Click 'Next' to see a simple visual representation and make your choice."
        );
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setEditable(false);
        ta.setMargin(new Insets(10,10,10,10));
        p.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton next = new JButton("Next");
        next.addActionListener(e -> cardLayout.show(mainPanel, "Trolley"));
        p.add(next, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createTrolleyPanel() {
        JPanel panel = new JPanel(null);

        JLabel instructions = new JLabel("<html><b>The Trolley Problem:</b><br>The red trolley moves to the right. " +
            "If you do nothing, it continues straight on the top track and will hit five people.<br>" +
            "If you pull the lever, it diverts onto the bottom track, hitting only one person.<br>" +
            "From a utilitarian perspective, saving more people is typically considered the better outcome.<br>" +
            "What would you do?</html>");
        instructions.setBounds(20,20,760,100);
        panel.add(instructions);

        JButton pullLever = new JButton("Pull Lever (Switch Track)");
        pullLever.setBounds(200, 500, 180, 30);
        panel.add(pullLever);

        JButton doNothing = new JButton("Do Nothing");
        doNothing.setBounds(420, 500, 140, 30);
        panel.add(doNothing);

        JPanel drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawLine(50, 200, 750, 200); // top track
                g.drawLine(50, 300, 750, 300); // bottom track
                g.drawLine(200, 200, 250, 300); // connecting line

                g.setColor(Color.BLUE);
                if (topPeopleAlive) {
                    for (int i = 0; i < 5; i++) {
                        int px = 600 + i*10;
                        int py = 200;
                        g.drawOval(px, py-15, 5,5); 
                        g.drawLine(px+2, py-10, px+2, py);
                    }
                }
                if (bottomPersonAlive) {
                    int px=600; int py=300;
                    g.drawOval(px, py-15, 5,5);
                    g.drawLine(px+2, py-10, px+2, py);
                }

                g.setColor(Color.RED);
                int ty = trackSwitched ? 300 : 200; 
                g.fillRect(trolleyX, ty-10, 30, 20);
            }
        };
        drawPanel.setBounds(0,100,800,380);
        panel.add(drawPanel);

        pullLever.addActionListener(e -> {
            trackSwitched = true;
            trolleyAnswerCorrect = true; 
            startTrolley(drawPanel);
        });

        doNothing.addActionListener(e -> {
            trackSwitched = false;
            trolleyAnswerCorrect = false; 
            startTrolley(drawPanel);
        });

        trolleyTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (trolleyStarted && !trolleyDone) {
                    trolleyX += 10;
                    if (!trackSwitched && trolleyX >= 600 && topPeopleAlive) {
                        topPeopleAlive = false; 
                    } else if (trackSwitched && trolleyX >= 600 && bottomPersonAlive) {
                        bottomPersonAlive = false; 
                    }

                    if (trolleyX > 700) {
                        trolleyTimer.stop();
                        trolleyDone = true;
                        updateTrolleyFeedback();
                        cardLayout.show(mainPanel, "TrolleyFeedback");
                    }
                    drawPanel.repaint();
                }
            }
        });

        return panel;
    }

    private void startTrolley(JPanel drawPanel) {
        trolleyStarted = true;
        trolleyDone = false;
        trolleyX = 50;
        topPeopleAlive = true;
        bottomPersonAlive = true;
        drawPanel.repaint();
        trolleyTimer.start();
    }

    private JPanel createTrolleyFeedbackPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Trolley Problem Feedback", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD,20));
        p.add(header, BorderLayout.NORTH);

        trolleyFeedbackTextArea = new JTextArea();
        trolleyFeedbackTextArea.setWrapStyleWord(true);
        trolleyFeedbackTextArea.setLineWrap(true);
        trolleyFeedbackTextArea.setEditable(false);
        trolleyFeedbackTextArea.setMargin(new Insets(10,10,10,10));
        p.add(new JScrollPane(trolleyFeedbackTextArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        retryButton = new JButton("Retry");
        continueButton = new JButton("Continue");
        btnPanel.add(retryButton);
        btnPanel.add(continueButton);

        p.add(btnPanel, BorderLayout.SOUTH);

        retryButton.addActionListener(e -> cardLayout.show(mainPanel, "Trolley"));
        continueButton.addActionListener(e -> cardLayout.show(mainPanel, "MoreInfo"));

        return p;
    }

    private void updateTrolleyFeedback() {
        if (trolleyAnswerCorrect) {
            // User pulled the lever
            trolleyFeedbackTextArea.setText(
                "You pulled the lever, causing the trolley to switch tracks and save five people at the cost of one life.\n\n" +
                "This choice reflects utilitarian thinking: by preventing greater harm and saving more lives, " +
                "you're maximizing overall well-being. Although it's a tough choice, utilitarianism prioritizes " +
                "the outcome that results in fewer deaths.\n\n" +
                "Click 'Continue' to learn more about utilitarianism."
            );
            retryButton.setVisible(false);
        } else {
            // User did nothing
            trolleyFeedbackTextArea.setText(
                "You did not pull the lever, resulting in the trolley killing five people instead of one.\n\n" +
                "From a utilitarian viewpoint, this leads to more total harm. Minimizing harm and maximizing " +
                "well-being is key to utilitarianism, so allowing five people to die instead of one is not the " +
                "optimal outcome.\n\n" +
                "Would you like to retry and choose the option that better reflects utilitarian thinking?"
            );
            retryButton.setVisible(true);
        }
    }

    private JPanel createMoreInfoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel header = new JLabel("More on Utilitarianism", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        p.add(header, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(
            "Utilitarianism is associated with philosophers like Jeremy Bentham and John Stuart Mill, who proposed that " +
            "we weigh the outcomes of our actions to achieve the greatest good for the greatest number.\n\n" +
            "The Trolley Problem is a thought experiment that challenges us to consider whether it's morally right " +
            "to sacrifice one person to save more lives. While it's unsettling, utilitarianism guides us towards " +
            "choosing the action that yields fewer total deaths and greater overall well-being.\n\n" +
            "Now, let's test what you've learned with a short quiz."
        );
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setEditable(false);
        ta.setMargin(new Insets(10,10,10,10));
        p.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton quizButton = new JButton("Take Quiz");
        quizButton.addActionListener(e -> {
            currentQuestionIndex=0; 
            quizScore=0; 
            loadQuestion();
            cardLayout.show(mainPanel, "Quiz");
        });
        p.add(quizButton, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createQuizPanel() {
        JPanel quizPanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD,16));
        quizPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(3,1));
        options = new JRadioButton[3];
        answerGroup = new ButtonGroup();
        for (int i=0; i<3; i++) {
            options[i] = new JRadioButton();
            answerGroup.add(options[i]);
            optionsPanel.add(options[i]);
        }
        quizPanel.add(optionsPanel, BorderLayout.CENTER);

        JButton submit = new JButton("Submit");
        submit.addActionListener(e->checkAndProceed());
        quizPanel.add(submit, BorderLayout.SOUTH);

        return quizPanel;
    }

    private JPanel createResultPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel res = new JLabel("Quiz Results", SwingConstants.CENTER);
        res.setFont(new Font("SansSerif", Font.BOLD,20));
        p.add(res, BorderLayout.NORTH);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setWrapStyleWord(true);
        resultTextArea.setLineWrap(true);
        resultTextArea.setMargin(new Insets(10,10,10,10));
        p.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

        JButton restart = new JButton("Restart");
        restart.addActionListener(ev->{
            resultTextArea.setText("");
            cardLayout.show(mainPanel, "Intro");
        });
        p.add(restart, BorderLayout.SOUTH);

        return p;
    }

    private void loadQuestion() {
        if (currentQuestionIndex < quizData.length) {
            String question = quizData[currentQuestionIndex][0];
            questionLabel.setText("Q"+(currentQuestionIndex+1)+": "+question);
            for (int i=0; i<3; i++) {
                options[i].setText(quizData[currentQuestionIndex][i+1]);
            }
            answerGroup.clearSelection();
        } else {
            showResult();
        }
    }

    private void checkAndProceed() {
        if (currentQuestionIndex < quizData.length) {
            String correct = quizData[currentQuestionIndex][4];
            for (JRadioButton opt : options) {
                if (opt.isSelected() && opt.getText().equals(correct)) {
                    quizScore++;
                }
            }
            currentQuestionIndex++;
            if (currentQuestionIndex < quizData.length) {
                loadQuestion();
            } else {
                showResult();
            }
        }
    }

    private void showResult() {
        cardLayout.show(mainPanel, "Result");
        resultTextArea.setText("Your score: "+quizScore+" out of "+quizData.length+"\n\n" +
                               "If you got some questions wrong, consider revisiting the explanations. " +
                               "You now have a foundational understanding of utilitarianism, its focus on outcomes, " +
                               "and how it applies to moral dilemmas like the Trolley Problem.");
    }
}
