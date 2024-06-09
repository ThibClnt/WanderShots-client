package fr.efrei.wandershots.client.exceptions;

/**
 * Exception thrown when an error occurs during credentials management.
 */
public class CredentialsManagmentException extends Exception {

        public CredentialsManagmentException(String message) {
            super(message);
        }

        public CredentialsManagmentException(String message, Throwable cause) {
            super(message, cause);
        }
}
