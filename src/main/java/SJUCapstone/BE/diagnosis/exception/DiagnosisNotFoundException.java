package SJUCapstone.BE.diagnosis.exception;

public class DiagnosisNotFoundException extends RuntimeException {
    public DiagnosisNotFoundException(String message) {
        super(message);
    }
}