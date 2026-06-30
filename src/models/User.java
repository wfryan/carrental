package models;

public abstract class User {
    private String firstName;
    private String lastName;
    private int id;

    User(String firstName, String lastName, int id){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public int getID(){return this.id;}
    public String getFirstName(){return this.firstName;}
    public String getLastName(){return this.lastName;}
    public String getName(){
        return this.lastName + ", " + this.firstName + ", ";
    }


}
