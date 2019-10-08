package net.spintechs.qimmos.fingerprint.employe.model;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String departement;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

}

