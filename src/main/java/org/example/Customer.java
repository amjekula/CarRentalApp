package org.example;

import java.io.*;
public class Customer implements Serializable
{
    private String firstName;
    private String surname;
    private String idNum;
    private String phoneNum;
    private boolean canRent;


    public Customer()
    {

    }

    public Customer(String fName, String lName, String idNum, String phone, boolean can)
    {
        setName(fName);
        setSurname(lName);
        setPhoneNum(phone);
        setIdNum(idNum);
        setCanRent(can);
    }
    public Customer(String fName, String lName, String idNum, String phone)
    {
        setName(fName);
        setSurname(lName);
        setPhoneNum(phone);
        setIdNum(idNum);
        setCanRent(true);
    }

    public void setName(String sFName)
    {
        firstName = sFName;
    }

    public void setSurname(String sSName)
    {
        surname = sSName;
    }

    public void setPhoneNum(String sPhone)
    {
        phoneNum = sPhone;
    }
    public void setIdNum(String id)
    {
        idNum = id;
    }

    public void setCanRent(boolean c)
    {
        canRent = c;
    }

    public String getName()
    {
        return firstName;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getPhoneNum()
    {
        return phoneNum;
    }
    public String getIdNum()
    {
        return idNum;
    }

    public boolean canRent()
    {
        return canRent;
    }

    public String toString()
    {
        return String.format("%-15s\t%-15s\t%-15s\t%-15s\t%-10s", getName(), getSurname(),
                getIdNum(),getPhoneNum(), new Boolean(canRent()).toString());
    }
}
