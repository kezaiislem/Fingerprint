package net.spintechs.qimmos.fingerprint.admin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class User implements Parcelable {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String departement;
    private String recrutmentDate;
    private String createdOn;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getRecrutmentDate() {
        return recrutmentDate;
    }

    public void setRecrutmentDate(String recrutmentDate) {
        this.recrutmentDate = recrutmentDate;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}

