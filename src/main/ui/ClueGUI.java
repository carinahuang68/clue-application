package ui;

import org.w3c.dom.events.MouseEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClueGUI extends JPanel implements ActionListener {
    JFrame frame = new JFrame("Clue Application");
    ClueApp c;
    private static final int frameWidth = 350;
    private static final int frameHeight = 200;

    public ClueGUI() {
        startPopUpWindow();
        openClueWindow();
    }

    public void openClueWindow() {
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // display JFrame to center of screen
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);
    }

    public void startPopUpWindow() {
        JDialog dialog = new JDialog((Frame) null, "Welcome to Clue!", true);
        dialog.setSize(300, 180);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null); // Center on screen

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(2, 1));
        messagePanel.add(new JLabel("Please select an option:", SwingConstants.CENTER));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        JButton loadButton = new JButton("Load Game");
        JButton newGameButton = new JButton("New Game");

        buttonPanel.add(loadButton);
        buttonPanel.add(newGameButton);

        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> {
            loadGame();
        });

        newGameButton.addActionListener(e -> {
            setUpGame();
        });

        dialog.setVisible(true);
    }

    public void setUpGame() {
        // stub
    }

    public void loadGame() {
        // stub
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

}
