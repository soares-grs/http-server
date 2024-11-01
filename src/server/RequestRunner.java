package server;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface RequestRunner {
    HttpResponse<?> run(HttpRequest httpRequest);
}
