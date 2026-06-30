package models;

import java.util.ArrayList;

public class Customer extends User{

    private int age;

    public Customer(String firstName, String lastName, int id, int age){
        super(firstName, lastName, id);
        this.age = age;
    }

    @Override
    public String toString(){
        return "Name: " + this.getName() + "\n Age: " + this.age;
    }
    public String exportString(){
        return this.getID() + "," + this.getLastName() + "," + this.getFirstName() + "," + this.age + "\n";
    }

    @Override
    public boolean equals(Object o){
        Customer target = (Customer) o;
        return (target.getID() == this.getID());
    }
}
