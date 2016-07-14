package sk.eea.arttag.rest.api;


public class ErrorResponseDTO {

    private String errorMessage;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorResponseDTO{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
