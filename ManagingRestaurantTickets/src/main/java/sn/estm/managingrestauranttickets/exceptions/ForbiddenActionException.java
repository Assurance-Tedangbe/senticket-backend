package sn.estm.managingrestauranttickets.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;

public class ForbiddenActionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenActionException(HttpStatus status, String message) {
        super(message);
    }
}
