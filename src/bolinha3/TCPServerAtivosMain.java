package bolinha3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServerAtivosMain extends Thread {

    private List<TCPServerConnection> clientes;
    private ServerSocket server;
    int x, y, s, d;

    public TCPServerAtivosMain(int porta) throws IOException {
        x = y = 0;
        s = 20;
        d = 5;
        this.server = new ServerSocket(porta);
        System.out.println(this.getClass().getSimpleName() + " rodando na porta: " + server.getLocalPort());
        this.clientes = new ArrayList<>();
    }

    @Override
    public void run() {
        Socket socket;
        while (true) {
            try {
                socket = this.server.accept();
                TCPServerConnection cliente = new TCPServerConnection(socket);
                novoCliente(cliente);
                (new TCPServerAtivosHandler(cliente, this)).start();
            } catch (IOException ex) {
                System.out.println("Erro 4: " + ex.getMessage());
            }
        }
    }

    public synchronized void novoCliente(TCPServerConnection cliente) throws IOException {
        clientes.add(cliente);
    }

    public synchronized void removerCliente(TCPServerConnection cliente) {
        clientes.remove(cliente);
        try {
            cliente.getInput().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        cliente.getOutput().close();
        try {
            cliente.getSocket().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List getClientes() {
        return clientes;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.server.close();
    }


}
