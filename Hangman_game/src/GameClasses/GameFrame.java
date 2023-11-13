package GameClasses;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private GamePanel gamePanel;

    public GameFrame() 
    {
        setTitle("Adam Asmaca");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }
}

