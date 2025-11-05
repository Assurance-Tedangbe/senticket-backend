package sn.estm.managingrestauranttickets.exceptions;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(String message) {
        super(message);
    }

}
