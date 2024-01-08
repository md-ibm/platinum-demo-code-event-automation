package com.ibm.eventautomation.demos.acme.data;

import java.util.UUID;

import com.github.javafaker.Faker;

public class Customer {

    private String id;
    private String name;


    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public Customer(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    public Customer(Faker faker) {
        this.id = UUID.randomUUID().toString();
        this.name = faker.name().fullName();
    }


    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }




    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + "]";
    }
}
