package Snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    // Oyun alanının boyutları
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 25;

    // Yılanın koordinatları
    private final int[] x = new int[WIDTH * HEIGHT / UNIT_SIZE];
    private final int[] y = new int[WIDTH * HEIGHT / UNIT_SIZE];

    private int bodyParts = 3;
    private int applesEaten = 0;

    private int appleX;
    private int appleY;

    private char direction = 'R';
    private boolean running = false;

    private Timer timer;
    private final Random random = new Random();

    public SnakeGame() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new MyKeyAdapter());

        startGame();
    }

    // Oyunu başlat
    public void startGame() {

        bodyParts = 3;
        applesEaten = 0;
        direction = 'R';

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * UNIT_SIZE;
            y[i] = 100;
        }

        newApple();

        running = true;

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(100, this);
        timer.start();

        repaint();
    }

    // Yeni elma oluştur
    public void newApple() {

        boolean onSnake;

        do {
            appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;

            onSnake = false;

            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    onSnake = true;
                    break;
                }
            }

        } while (onSnake);
    }

    // Ekranı çiz
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {

            // Elmayı çiz
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Yılanı çiz
            for (int i = 0; i < bodyParts; i++) {

                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }

                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Skor
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 25));

            g.drawString("Score: " + applesEaten * 10, 20, 30);

        } else {

            gameOver(g);
        }
    }

    // Yılanı hareket ettir
    public void move() {

        for (int i = bodyParts; i > 0; i--) {

            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {

            case 'U':
                y[0] -= UNIT_SIZE;
                break;

            case 'D':
                y[0] += UNIT_SIZE;
                break;

            case 'L':
                x[0] -= UNIT_SIZE;
                break;

            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    // Elma kontrolü
    public void checkApple() {

        if (x[0] == appleX && y[0] == appleY) {

            bodyParts++;
            applesEaten++;

            newApple();
        }
    }

    // Çarpışma kontrolü
    public void checkCollisions() {

        // Kendi vücuduna çarpma
        for (int i = bodyParts - 1; i > 0; i--) {

            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // Duvarlara çarpma
        if (x[0] < 0 || x[0] >= WIDTH ||
            y[0] < 0 || y[0] >= HEIGHT) {

            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    // Oyun bitiş ekranı
    public void gameOver(Graphics g) {

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 45));
        g.drawString("GAME OVER", 150, 270);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Score: " + applesEaten * 10, 230, 320);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press R to Restart", 205, 370);
    }

    // Oyun döngüsü
    @Override
    public void actionPerformed(ActionEvent e) {

        if (running) {

            move();
            checkApple();
            checkCollisions();
        }

        repaint();
    }

    // Klavye kontrolü
    public class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {

                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;

                case KeyEvent.VK_R:
                    if (!running) startGame();
                    break;
            }
        }
    }

    // Programın başlangıç noktası
    public static void main(String[] args) {

        JFrame frame = new JFrame("Snake Game");

        SnakeGame game = new SnakeGame();

        frame.add(game);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.requestFocusInWindow();
    }
}