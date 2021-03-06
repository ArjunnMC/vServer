package me.arjunn_.type;

import me.arjunn_.Server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Request<T> {

    private CompletableFuture<T> result;

    public Request() {
        result = new CompletableFuture<>();
    }

    public void setResponse(T completionValue) {
        result.complete(completionValue);
    }

    public T getResponse() {
        try {
            return result.get(40, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            Server.futuresToResolve.values().remove(this);
        }
        return null;
    }

}
