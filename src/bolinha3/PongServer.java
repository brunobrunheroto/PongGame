package bolinha3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PongServer extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;

    private final int WIDTH = 800, HEIGHT = 600;
    private final int PADDLE_WIDTH = 20, PADDLE_HEIGHT = 100;
    private final int BALL_SIZE = 20;
    private final int PADDLE_SPEED = 5;

    private int paddle1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int paddle2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT / 2 - BALL_SIZE / 2;
    private int ballXSpeed = -2;
    private int ballYSpeed = -2;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public PongServer() {
        JFrame frame = new JFrame("Pong - Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(false);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        try {
            serverSocket = new ServerSocket(1234);
            System.out.println("Waiting for client to connect...");
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            receiveInput();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            inputThread.start();

            while (true) {
                moveBall();
                checkCollisions();
                repaint();

                sendGameState();

                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void moveBall() {
        ballX += ballXSpeed;
        ballY += ballYSpeed;
    }

    private void checkCollisions() {
        // Collisions with walls
        if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
            ballYSpeed *= -1;
        }

        // Collisions with paddles
        if (ballX <= PADDLE_WIDTH && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
            ballXSpeed *= -1;
        }

        if (ballX >= WIDTH - PADDLE_WIDTH - BALL_SIZE && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
            ballXSpeed *= -1;
        }

        // Scoring points
        if (ballX <= 0 || ballX >= WIDTH - BALL_SIZE) {
            // Increment score or handle game over
            resetBall();
        }
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXSpeed *= -1;
        ballYSpeed *= -1;
    }

    private void sendGameState() throws IOException {
        outputStream.writeInt(paddle1Y);
        outputStream.writeInt(paddle2Y);
        outputStream.writeInt(ballX);
        outputStream.writeInt(ballY);
    }

    private void receiveInput() throws IOException {
        if (inputStream.available() > 0) {
            int player = inputStream.readInt(); // Identificador do jogador
            int keyCode = inputStream.readInt(); // Tecla pressionada

            if (player == 1) {
                if (keyCode == KeyEvent.VK_W && paddle1Y > 0) {
                    paddle1Y -= PADDLE_SPEED;
                } else if (keyCode == KeyEvent.VK_S && paddle1Y < HEIGHT - PADDLE_HEIGHT) {
                    paddle1Y += PADDLE_SPEED;
                }
            } else if (player == 2) {
                if (keyCode == KeyEvent.VK_UP && paddle2Y > 0) {
                    paddle2Y -= PADDLE_SPEED;
                } else if (keyCode == KeyEvent.VK_DOWN && paddle2Y < HEIGHT - PADDLE_HEIGHT) {
                    paddle2Y += PADDLE_SPEED;
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw paddles
        g.setColor(Color.BLACK);
        g.fillRect(0, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            int player = 0; // Identificador do jogador (neste caso, servidor)
            outputStream.writeInt(player);
            outputStream.writeInt(e.getKeyCode());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new PongServer();
    }
}
