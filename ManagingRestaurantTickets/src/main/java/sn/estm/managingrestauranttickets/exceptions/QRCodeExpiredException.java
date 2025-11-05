package sn.estm.managingrestauranttickets.exceptions;

public class QRCodeExpiredException extends RuntimeException{

    public QRCodeExpiredException(String message) {
        super(message);
    }
}
