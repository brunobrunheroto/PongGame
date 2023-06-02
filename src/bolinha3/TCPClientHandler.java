package bolinha3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class TCPClientHandler extends Thread {

    private Socket socket;
    private PongClientPlayer1 caller;
    private BufferedReader input;

    public TCPClientHandler(Socket socket, PongClientPlayer1 caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = input.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(message, "|");
                int paddle1Y = Integer.parseInt(tokens.nextToken());
                int paddle2Y = Integer.parseInt(tokens.nextToken());
                int ballX = Integer.parseInt(tokens.nextToken());
                int ballY = Integer.parseInt(tokens.nextToken());
                caller.updateGameState(paddle1Y, paddle2Y, ballX, ballY);
                caller.repaint();
            }
        } catch (IOException ex) {
            System.out.println("Error reading input: " + ex.getMessage());
        } finally {
            try {
                input.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
}
