package br.com.sebratel.pessoas.exceptions;

public class InvalidMassiveRequestException extends RuntimeException {
    public InvalidMassiveRequestException(String message) {
        super(message);
    }
}
