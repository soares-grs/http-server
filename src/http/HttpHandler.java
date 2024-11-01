package http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

import server.RequestRunner;

public class HttpHandler {
    private final Map<String, RequestRunner> routes;

    public HttpHandler(final Map<String, RequestRunner> routes) {
        this.routes = routes;
    }

    public void handleConnection(final InputStream inputStream, final OutputStream outputStream) {
        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        // TODO: implementar o HttpDecoder
        Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
        request.ifPresentOrElse((r) -> handleRequest(r, bufferedWriter), () -> handleInvalidRequest(bufferedWriter));

        bufferedWriter.close();
        inputStream.close();

    }

    private void handleInvalidRequest(final BufferedWriter bufferedWriter) {
        try {
            // TODO: implementar o ResponseWriter
            ResponseWriter.writeResponse(
                    bufferedWriter,
                    "HTTP/1.1 400 Bad Request\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: 15\r\n" +
                            "\r\n" +
                            "Bad Request...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
