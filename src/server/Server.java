package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import http.HttpHandler;
import http.HttpMethod;

public class Server {
    private final Map<String, RequestRunner> routes;
    private final ServerSocket serverSocket;
    private final Executor threadPoolExecutor;
    private HttpHandler httpHandler;

    /*
     * a porta está hardcoded,
     * mas se fosse para fazer da maneira correta, seria definida pelo usuário final
     */
    public Server(int port) throws IOException {
        this.routes = new HashMap<>();
        this.threadPoolExecutor = Executors.newFixedThreadPool(100);
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        httpHandler = new HttpHandler(this.routes);

        while (true) {
            Socket clientConnection = serverSocket.accept();
            handleConnection(clientConnection);
        }
    }

    /*
     * Essa foi a versão inicial do método, mas foi descontinuada pois iria travar o
     * server
     * se fosse feita mais de 1 request
     * public void handleConnection(Socket clientConnection) {
     * try {
     * httpHandler.handleConnection(clientConnection.getInputStream(),
     * clientConnection.getOutputStream());
     * } catch (IOException e) {
     * }
     * }
     */

    public void handleConnection(Socket clientConnection) {
        // Garante que as requisições sejam processadas paralelamente na mesma thread
        Runnable httRequestRunner = () -> {
            try {
                this.httpHandler.handleConnection(clientConnection.getInputStream(),
                        clientConnection.getOutputStream());
            } catch (IOException e) {
            }
        };
        this.threadPoolExecutor.execute(httRequestRunner);
    }

    public void addRoute(HttpMethod opCode, String route, RequestRunner runner) {
        this.routes.put(opCode.name().concat(route), runner);
    }
}
