package sk.eea.arttag.service;

public class EmailNotProvidedException extends RuntimeException {

    public EmailNotProvidedException(String message) {
        super(message);
    }

    public EmailNotProvidedException(String message, Throwable cause) {
        super(message, cause);
    }
}
