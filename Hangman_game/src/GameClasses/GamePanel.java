package GameClasses;

import javax.swing.*;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random;

public class GamePanel extends JPanel {
    private final String[] cities = {"Antalya", "İstanbul", "Ankara", "Muş", "Van", "İzmir", "Samsun", "Bursa", "Adana", "Hatay"};

    private String selectedCity;

    private int errorCount = 0;

    private JLabel[] nameLabels;
    private JLabel errorLabel;
    private JTextField guessField;
    private JLabel timerLabel;
    private JLabel wrongGuessLabel;

    private Timer timer;
    private int timeLeft = 300; 

    private AudioPlayer audioPlayer;

    public GamePanel() 
    {
        setLayout(null); //(absolute positioning) it is a small app not gonna be a problem
        initializeGame();
        audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("music/Queen.wav", 0.2f);
    }

    private void initializeGame() 
    {
        
        Random random = new Random();
        selectedCity = cities[random.nextInt(cities.length)];
        
        //labels for city name
        nameLabels = new JLabel[selectedCity.length()];

        for (int i = 0; i < selectedCity.length(); i++) 
        {
            nameLabels[i] = new JLabel("_");
            nameLabels[i].setBounds(100 + i*50, 200, 50, 50);
            add(nameLabels[i]);
        }

        errorLabel = new JLabel(getScaledImageIcon("/stickman/error0.png", 300, 300));
        errorLabel.setBounds(500, 50, 300, 300);
        add(errorLabel);

        //guess input field
        guessField = new JTextField(10);
        guessField.setBounds(150, 350, 120, 30);
        add(guessField);

        //timer label
        timerLabel = new JLabel("Kalan Zaman: " + timeLeft);
        timerLabel.setBounds(500, 400, 200, 30);
        add(timerLabel);

        //guess button
        JButton guessButton = new JButton("Tahmin");
        guessButton.setBounds(300, 350, 90, 30);
        guessButton.addActionListener(new GuessButtonListener());
        add(guessButton);

        //wrong guess label
        wrongGuessLabel = new JLabel("Hatalı Tahmin: ");
        wrongGuessLabel.setBounds(150, 400, 500, 30);
        add(wrongGuessLabel);
        startTimer();
    }

    private void startTimer() 
    {
        timer = new Timer(1000, new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if (timeLeft > 0) 
                {
                    timeLeft--;
                    timerLabel.setText("Kalan Zaman: " + timeLeft);
                } else 
                {
                    timer.stop();
                    int score = 10;
                    JOptionPane.showMessageDialog(null, "Zaman doldu! Oyun Bitti! Puanınız: " + score);
                    closeGame();
                }
            }
        });
        timer.start();
    }

    private class GuessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String guess = guessField.getText();
            guessField.setText("");

            //if user give an input something like number 
            if (guess.length() == 1 && !Character.isLetter(guess.charAt(0))) 
            {
                JOptionPane.showMessageDialog(null, "Harf girin lütfen!.");
                return;
            }
    
            //if the guess is a single letter
            if (guess.length() == 1) 
            {
                boolean match = false;
                StringBuilder currentGuess = new StringBuilder();
                for (int i = 0; i < selectedCity.length(); i++) 
                {
                    //case-sensitive
                    if (selectedCity.charAt(i) == guess.charAt(0)) 
                    {
                        nameLabels[i].setText(guess);
                        match = true;
                        
                        audioPlayer.playSound("music/correct.wav",0.2f); // Play correct guess sound
                    }
                    currentGuess.append(nameLabels[i].getText().isEmpty() ? "_" : nameLabels[i].getText());
                }
    
                if (!match) 
                {
                    errorCount++;
                    wrongGuessLabel.setText(wrongGuessLabel.getText() + " " + guess); //new wrong guess

                    //Update error image with smooth scaling ,its sometimes get broken so i try to do with absolute position from this moment

                    errorLabel.setIcon(getScaledImageIcon("/stickman/error" + errorCount + ".png", 300, 300));

                    audioPlayer.playSound("music/wrong.wav",0.2f); // Play wrong guess sound


                } else if (currentGuess.toString().equals(selectedCity)) 
                {
                    //User has found all characters
                    timer.stop();
                    int score = calculateScore(true);
                    JOptionPane.showMessageDialog(null, "Tebrikler! Şehir: " + selectedCity + "\nPuanınız: " + score);
                    closeGame();
                }
            } else if (guess.equals(selectedCity)) 
            { 
                // Full name guess
                for (int i = 0; i < selectedCity.length(); i++) 
                {
                    nameLabels[i].setText(String.valueOf(selectedCity.charAt(i)));
                }
                timer.stop();
                JOptionPane.showMessageDialog(null, "Tebrikler! Şehir: " + selectedCity);
                closeGame();
            } else 
            {
                errorCount++;
            }
    
            if (errorCount+1 > 8 || guess.equals(selectedCity)) 
            {
                timer.stop();
                int score = calculateScore(guess.equals(selectedCity));
                String message = guess.equals(selectedCity)
                                 ? "Tebrikler! Şehir: " + selectedCity + "\nPuanınız: " + score
                                 : "Oyun Bitti! Şehir: " + selectedCity + "\nPuanınız: " + score;
                JOptionPane.showMessageDialog(null, message);
                closeGame();
            }
        }
    }
    

    private void closeGame() 
    {
        //dispose dont work on jpane directly ?
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) 
        {
            topFrame.dispose();
        }
    }


    private ImageIcon getScaledImageIcon(String path, int width, int height) 
    {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) 
        {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else 
        {
            System.err.println("Resource not found: " + path);
            return null;
        }
    }      

    private int calculateScore(boolean gameWon) 
    {
        //İf lose or time runs out score is 10 if win just calc
        if (gameWon) 
        {   
            return (timeLeft / 10) * 3 + 10 - (errorCount*5);
        } else 
        {
            return 10; 
        }
    }
}
