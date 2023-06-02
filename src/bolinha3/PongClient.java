package bolinha3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PongClient extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;

    protected final int WIDTH = 800, HEIGHT = 600;
    protected final int PADDLE_WIDTH = 20, PADDLE_HEIGHT = 100;
    protected final int BALL_SIZE = 20;
    protected final int PADDLE_SPEED = 5;

    protected int paddle1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    protected int paddle2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    protected int ballX = WIDTH / 2 - BALL_SIZE / 2;
    protected int ballY = HEIGHT / 2 - BALL_SIZE / 2;

    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public PongClient() {
        JFrame frame = new JFrame("Pong - Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(false);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        try {
            clientSocket = new Socket("localhost", 1234);

            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            while (true) {
                receiveGameState();
                moveBall();
                checkCollisions();
                repaint();

                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void receiveGameState() throws IOException {
        if (inputStream.available() > 0) {
            paddle1Y = inputStream.readInt();
            paddle2Y = inputStream.readInt();
            ballX = inputStream.readInt();
            ballY = inputStream.readInt();
        }
    }

    protected void moveBall() {
        // Lógica para mover a bola (específico do cliente)
    }

    protected void checkCollisions() {
        // Lógica para verificar colisões (específico do cliente)
    }

    protected void sendInput(int keyCode) throws IOException {
        outputStream.writeInt(keyCode);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenha os paddles
        g.setColor(Color.BLACK);
        g.fillRect(0, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Desenha a bola
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            sendInput(e.getKeyCode());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new PongClient();
    }

    public void updateGameState(int paddle1y2, int paddle2y2, int ballX2, int ballY2) {
    }
}
