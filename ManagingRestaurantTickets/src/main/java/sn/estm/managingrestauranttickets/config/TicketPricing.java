package sn.estm.managingrestauranttickets.config;

import sn.estm.managingrestauranttickets.enumerations.TicketType;

public final class TicketPricing {

    public static final double PRICE_A = 50.0;
    public static final double PRICE_B = 100.0;

    private TicketPricing() {}

    public static double priceOf(TicketType type) {
        return type == TicketType.A ? PRICE_A : PRICE_B;
    }

    public static double total(int countA, int countB) {
        return countA * PRICE_A + countB * PRICE_B;
    }
}