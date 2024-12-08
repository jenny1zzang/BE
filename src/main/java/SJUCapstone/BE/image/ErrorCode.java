package SJUCapstone.BE.image;

public enum ErrorCode {
    EMPTY_FILE_EXCEPTION("File is empty."),
    FILE_UPLOAD_FAILED("File upload failed."),
    FILE_NOT_FOUND("File not found."),
    INVALID_FILE_FORMAT("Invalid file format."),
    NO_FILE_EXTENTION("No file extension."),
    INVALID_FILE_EXTENTION("Invalid file extension."),
    PUT_OBJECT_EXCEPTION("put object failed."),
    IO_EXCEPTION_ON_IMAGE_DELETE("io exception on image delete."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD("IO exception on image upload");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

