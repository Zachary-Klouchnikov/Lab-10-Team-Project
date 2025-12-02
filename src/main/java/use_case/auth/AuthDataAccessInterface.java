package use_case.auth;

import data_access.SteamAuthException;
import java.util.concurrent.CompletableFuture;

public interface AuthDataAccessInterface {
    public CompletableFuture<Long> authenticate() throws SteamAuthException;
    public void close();
}
