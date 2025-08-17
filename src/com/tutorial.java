package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class tutorial {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new GamePanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Game settings
    private final int TILE_SIZE = 25;
    private final int GAME_WIDTH = 25; // in tiles
    private final int GAME_HEIGHT = 25;
    private final int START_DELAY = 150; // ms

    // Game state
    private ArrayList<Point> snake;
    private Point food;
    private char direction;
    private boolean running;
    private int score;
    private Timer timer;
    private Random rand;
    private int highScore = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH * TILE_SIZE, GAME_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        rand = new Random();
        startGame();

    }

    public void startGame() {
        snake = new ArrayList<>();
        snake.add(new Point(5, 5)); // head start
        direction = 'R';
        score = 0;
        running = true;
        spawnFood();
        timer = new Timer(START_DELAY, this);
        timer.start();
    }

    private void spawnFood() {
        while (true) {
            Point p = new Point(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT));
            if (!snake.contains(p)) {
                food = p;
                break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
        }
        repaint();
    }

    private void move() {
        Point head = new Point(snake.get(0));
        switch (direction) {
            case 'U':
                head.y -= 1;
                break;
            case 'D':
                head.y += 1;
                break;
            case 'L':
                head.x -= 1;
                break;
            case 'R':
                head.x += 1;
                break;
        }
        snake.add(0, head);

        if (head.equals(food)) {
            score += 10;
            spawnFood();
            increaseSpeed();
        } else {
            snake.remove(snake.size() - 1); // remove tail
        }
    }

    private void increaseSpeed() {
        int delay = Math.max(50, timer.getDelay() - 5); // speed up
        timer.setDelay(delay);
    }

    private void checkCollision() {
        Point head = snake.get(0);
        // Wall collision
        if (head.x < 0 || head.x >= GAME_WIDTH || head.y < 0 || head.y >= GAME_HEIGHT) {
            gameOver();
        }
        // Self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        running = false;
        timer.stop();
        if (score > highScore) {
            highScore = score;
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                if (i == 0)
                    g.setColor(Color.GREEN); // head
                else
                    g.setColor(new Color(0, 150, 0)); // body
                g.fillRect(snake.get(i).x * TILE_SIZE, snake.get(i).y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("High Score: " + highScore, getWidth() - 150, 20);

        } else {
            // Game Over Screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", getWidth() / 2 - 80, getHeight() / 2 - 20);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Score: " + score, getWidth() / 2 - 30, getHeight() / 2 + 10);
            g.drawString("Press SPACE to Restart", getWidth() / 2 - 100, getHeight() / 2 + 40);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (running) {
            if (key == KeyEvent.VK_UP && direction != 'D')
                direction = 'U';
            if (key == KeyEvent.VK_DOWN && direction != 'U')
                direction = 'D';
            if (key == KeyEvent.VK_LEFT && direction != 'R')
                direction = 'L';
            if (key == KeyEvent.VK_RIGHT && direction != 'L')
                direction = 'R';
        } else {
            if (key == KeyEvent.VK_SPACE)
                startGame();
        }

        if (key == KeyEvent.VK_P) {
            if (running) {
                if (timer.isRunning()) {
                    timer.stop(); // Pause
                } else {
                    timer.start(); // Resume
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
