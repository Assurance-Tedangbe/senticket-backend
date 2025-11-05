package sn.estm.managingrestauranttickets.exceptions;

public class QRCodeGenerationException extends RuntimeException{

    public QRCodeGenerationException(String message) {
        super(message);
    }
}
