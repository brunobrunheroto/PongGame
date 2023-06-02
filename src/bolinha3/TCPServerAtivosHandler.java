package bolinha3;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

public class TCPServerAtivosHandler extends Thread {

    private TCPServerConnection cliente;
    private TCPServerAtivosMain caller;

    public TCPServerAtivosHandler(TCPServerConnection cliente, TCPServerAtivosMain caller) throws IOException {
        this.cliente = cliente;
        this.caller = caller;
    }

    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }

    private void encerrar() {
        this.caller.removerCliente(this.cliente);
    }

    /**
     * @param message
     * @throws IOException
     */
    public synchronized void messageDispatcher(String message) throws IOException {
        List<TCPServerConnection> clientes = this.caller.getClientes();
        for (TCPServerConnection cli : clientes) {
            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getOutput() != null) {
                cli.getOutput().println(message);
                cli.getOutput().flush();
            }
        }
    }

    @Override
    public void run() {

        String message;
        while (true) {
            try {
                if (this.cliente.getSocket().isConnected() && this.cliente.getInput() != null) {
                    message = this.cliente.getInput().readLine();
                } else {
                    break;
                }
                if (message == null || message.equals("")) {
                    break;
                }
                int codigo = Integer.parseInt(message);
                switch (codigo) {
                    case KeyEvent.VK_UP:
                        caller.y -= caller.d;
                        if (caller.y < 0) {
                            caller.y = 0;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        caller.y += caller.d;
                        if (caller.y > 350 - caller.s) {
                            caller.y = 350 - caller.s;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        caller.x += caller.d;
                        if (caller.x > 550 - caller.s) {
                            caller.x = 550 - caller.s;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        caller.x -= caller.d;
                        if (caller.x < 0) {
                            caller.x = 0;
                        }
                        break;
                    case KeyEvent.VK_S:
                        caller.s++;
                        if (caller.s > 50) {
                            caller.s = 50;
                        }
                        break;
                    case KeyEvent.VK_Z:
                        caller.s--;
                        if (caller.s < 3) {
                            caller.s = 3;
                        }
                        break;
                    case KeyEvent.VK_D:
                        caller.d++;
                        if (caller.d > 20) {
                            caller.d = 20;
                        }
                        break;
                    case KeyEvent.VK_X:
                        caller.d--;
                        if (caller.d < 0) {
                            caller.d = 0;
                        }
                        break;
                }
                message = caller.x + "|"
                        + caller.y + "|"
                        + caller.s + "|"
                        + caller.d;
                messageDispatcher(message);
                System.out.println(message);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        encerrar();
    }
}
