package br.com.sebratel.pessoas.exceptions;

public class FeatureNotImplementedException extends RuntimeException {
    public FeatureNotImplementedException(String message) {
        super(message);
    }
}