package sn.estm.managingrestauranttickets.exceptions;

public class InvalidQRCodeException extends RuntimeException{

    public InvalidQRCodeException(String message) {
        super(message);
    }
}
