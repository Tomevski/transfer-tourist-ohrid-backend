package com.transfertourist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Contact details of the person who submitted a booking. Embedded in {@link Booking}. */
@Embeddable
public class Customer {

    @Column(name = "customer_first_name", nullable = false, length = 120)
    private String firstName;

    @Column(name = "customer_last_name", nullable = false, length = 120)
    private String lastName;

    @Column(name = "customer_email", nullable = false, length = 160)
    private String email;

    @Column(name = "customer_phone", nullable = false, length = 40)
    private String phone;

    public Customer() {
        // JPA + service construction
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
