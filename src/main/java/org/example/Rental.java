package org.example;

import java.io.Serializable;

public class Rental implements Serializable{
    private long rentalNumber ,custNumber, vehNumber;
    private String dateRental, dateReturned;
    private double pricePerDay, totalRental;

    public Rental() {
    }

    public Rental(long rentalNumber, long custNumber, long vehNumber, String dateRental, String dateReturned, double pricePerDay, double totalRental) {
        this.rentalNumber = rentalNumber;
        this.custNumber = custNumber;
        this.vehNumber = vehNumber;
        this.dateRental = dateRental;
        this.dateReturned = dateReturned;
        this.pricePerDay = pricePerDay;
        this.totalRental = totalRental;
    }

    public long getRentalNumber() {
        return rentalNumber;
    }

    public void setRentalNumber(long rentalNumber) {
        this.rentalNumber = rentalNumber;
    }

    public long getCustNumber() {
        return custNumber;
    }

    public void setCustNumber(long custNumber) {
        this.custNumber = custNumber;
    }

    public long getVehNumber() {
        return vehNumber;
    }

    public void setVehNumber(long vehNumber) {
        this.vehNumber = vehNumber;
    }

    public String getDateRental() {
        return dateRental;
    }

    public void setDateRental(String dateRental) {
        this.dateRental = dateRental;
    }

    public String getDateReturned() {
        return dateReturned;
    }

    public void setDateReturned(String dateReturned) {
        this.dateReturned = dateReturned;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public double getTotalRental() {
        return totalRental;
    }

    public void setTotalRental(double totalRental) {
        this.totalRental = totalRental;
    }

}