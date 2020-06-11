package org.example;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import javax.swing.JOptionPane;

public class RunCreateVehicleSer {

    Connection con;
    PreparedStatement prepState;
    ResultSet resSet;

    //Get and set classes
    Vehicle getVeh = new Vehicle();
    Customer getCust = new Customer();
    Rental getRent = new Rental();

    //Ser Files
    String custFile = "Customers.ser";
    String vehFile = "Vehicles.ser";

    // Server socket
    private ServerSocket listener;
    // Client connection
    private Socket client;
    ObjectOutputStream out;
    ObjectInputStream in;
    String msg;

    public static void main(String[] args) {
        CreateVehicleSer runCreateVehicles = new CreateVehicleSer();
        runCreateVehicles.openFile();
        runCreateVehicles.writeObjects();
        runCreateVehicles.closeFile();

        RunCreateVehicleSer run = new RunCreateVehicleSer();
        run.connect();
        run.createDBTables();
        run.readAndWriteDB();

        run.listen();

    }

    public RunCreateVehicleSer() {
        // Create server socket
        try {
            while (true) {
                listener = new ServerSocket(8520, 20);
            }
        } catch (IOException ioe) {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public void listen() {
        // Start listening for client connections
        try {
            System.out.println("Server is listening");
            client = listener.accept();

            System.out.println("Now moving onto processClient");
            processClient();

        } catch (IOException ioe) {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public Connection connect() {
        String dbLocation = "jdbc:ucanaccess://C:\\Users\\A. Mjeks\\Documents\\NetBeansProjects\\3rd Year\\CarRentalApp\\CarRentalDB.mdb";
        try {
            con = DriverManager.getConnection(dbLocation);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return con;
    }

    public void processClient() {

        try {
            while (true) {
                out = new ObjectOutputStream(client.getOutputStream());
                out.flush();
                in = new ObjectInputStream(client.getInputStream());
                msg = (String) in.readObject();

                if (msg.equalsIgnoreCase("Save Customer")) {
                    String regUserQuery = "insert into Customer(custNumber, fname, lname, "
                            + "idNum, phoneNum, canRent) VALUES (?,?,?,?,?,?)";

                    getCust = (Customer) in.readObject();
                    long custNumber = (Long) in.readObject();

                    prepState = con.prepareStatement(regUserQuery);
                    prepState.setLong(1, custNumber);
                    prepState.setString(2, getCust.getName());
                    prepState.setString(3, getCust.getSurname());
                    prepState.setString(4, getCust.getIdNum());
                    prepState.setString(5, getCust.getPhoneNum());
                    prepState.setBoolean(6, getCust.canRent());

                    prepState.executeUpdate();

                } else if (msg.equalsIgnoreCase("Save Vehicle")) {
                    String regUserQuery = "insert into Vehicle(vehNumber, make, category, "
                            + "rentalPrice, availableForRent) VALUES (?,?,?,?,?)";

                    getVeh = (Vehicle) in.readObject();
                    long vehNumber = (Long) in.readObject();

                    prepState = con.prepareStatement(regUserQuery);
                    prepState.setLong(1, vehNumber);
                    prepState.setString(2, getVeh.getMake());
                    prepState.setString(3, getVeh.getCategory());
                    prepState.setDouble(4, getVeh.getRentalPrice());
                    prepState.setBoolean(5, getVeh.isAvailableForRent());
                    prepState.executeUpdate();

                } else if (msg.equalsIgnoreCase("Rent Vehicle")) {

                    String custQuery = "select * from Customer WHERE fname = ?";
                    String custName = (String) in.readObject();

                    prepState = con.prepareStatement(custQuery);
                    prepState.setString(1, custName);
                    resSet = prepState.executeQuery();

                    Boolean canRent = null;
                    while (resSet.next()) {
                        if (resSet.getString(2).equals(custName)) {
                            long custNum = resSet.getLong(1);
                            canRent = resSet.getBoolean(6);
//                            out.writeObject(canRent);

                            String canRentQuery = "update Customer set canRent = false where custNumber = " + custNum;
                            prepState = con.prepareStatement(canRentQuery);
                            prepState.executeUpdate();

                            out.writeObject(custNum);
                        }
                    }
                    getCust.setCanRent(canRent);
//                    if (getCust.canRent()) {

                    String vehQuery = "select * from Vehicle WHERE make = ?";
                    String module = (String) in.readObject();

                    prepState = con.prepareStatement(vehQuery);
                    prepState.setString(1, module);
                    resSet = prepState.executeQuery();

                    //get selected vehicle make primary key
                    while (resSet.next()) {
                        if (resSet.getString(2).equals(module)) {
                            long vehNum = resSet.getLong(1);

                            String availQuery = "update Vehicle set availableForRent = false where vehNumber = " + vehNum;
                            prepState = con.prepareStatement(availQuery);
                            prepState.executeUpdate();

                            out.writeObject(vehNum);
                        }
                    }

                    String insertRentalQ = "insert into Rental(rentalNumber, custNumber, vehNumber, dateOfRental,"
                            + "returnDate, pricePerDay, totalRental) VALUES(?,?,?,?,?,?,?)";
                    getRent = (Rental) in.readObject();

                    prepState = con.prepareStatement(insertRentalQ);
                    prepState.setLong(1, getRent.getRentalNumber());
                    prepState.setLong(2, getRent.getCustNumber());
                    prepState.setLong(3, getRent.getVehNumber());
                    prepState.setString(4, getRent.getDateRental());
                    prepState.setString(5, getRent.getDateReturned());
                    prepState.setDouble(6, getRent.getPricePerDay());
                    prepState.setDouble(7, getRent.getTotalRental());
                    prepState.executeUpdate();

                    out.writeObject("New Rental is Completed");

//                    } else{
//                        JOptionPane.showMessageDialog(null, "Return Vehicle Due To Us");
//                    }
                } else if (msg.equalsIgnoreCase("Return Vehicle")) {

                    long returnNum = (long) in.readObject();

                    String returnVeh = "delete from Rental WHERE rentalNumber = ?";

                    prepState = con.prepareStatement(returnVeh);
                    prepState.setLong(1, returnNum);
                    prepState.executeUpdate();

//                    String availQuery = "update Vehicle set availableForRent = true where vehNumber = " + getRent.getVehNumber();
//                    prepState = con.prepareStatement(availQuery);
//                    prepState.executeUpdate();
//
//                    String canRentQuery = "update Customer set canRent = true where custNumber = " + getRent.getCustNumber();
//                    prepState = con.prepareStatement(canRentQuery);
//                    prepState.executeUpdate();

                    out.writeObject("Vehicle returned");

                }
            }
            //Last close
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            System.out.println("IOException | ClassNotFoundException | SQLException: " + ex.getMessage());
        }
    }

    public void closeServer() {
        try {
            client.close();
            out.close();
            in.close();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void createDBTables() {

        String custTableQuery = "create table Customer(custNumber LONG PRIMARY KEY, fname VARCHAR(30),"
                + " lname VARCHAR(30), idNum VARCHAR(13), phoneNum VARCHAR(15), canRent BOOLEAN)";

        String vehTableQuery = "create table Vehicle(vehNumber LONG PRIMARY KEY, make VARCHAR(30),"
                + " category VARCHAR(30), rentalPrice DECIMAL, availableForRent BOOLEAN)";

        String rentalTableQuery = "create table Rental(rentalNumber INTEGER PRIMARY KEY, custNumber long FOREIGN KEY REFERENCES Customer(custNumber),"
                + " vehNumber long FOREIGN KEY REFERENCES Vehicle(vehNumber), dateOfRental VARCHAR(20), returnDate VARCHAR(20), pricePerDay DECIMAL, totalRental DECIMAL)";

        try {
            //create tables
            prepState = con.prepareStatement(custTableQuery);
            prepState.executeUpdate();
            prepState = con.prepareStatement(vehTableQuery);
            prepState.executeUpdate();
            prepState = con.prepareStatement(rentalTableQuery);
            prepState.executeUpdate();
            JOptionPane.showMessageDialog(null, "Database Created Successfully");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    public void writeIntoTable() {
        String custQuery = "insert into Customer(custNumber, fname, lname, idNum, phoneNum, canRent) values (?,?,?,?,?,?)";
        String vehQuery = "insert into Vehicle(vehNumber, make, category, rentalPrice, availableForRent) values (?,?,?,?,?)";

        try {
            //Customer
            prepState = con.prepareStatement(custQuery);
            Random rand = new Random();
            long custNumber = Math.abs(1000000 + rand.nextInt(10000000));

            prepState.setLong(1, custNumber);
            prepState.setString(2, getCust.getName());
            prepState.setString(3, getCust.getSurname());
            prepState.setString(4, getCust.getIdNum());
            prepState.setString(5, getCust.getPhoneNum());
            prepState.setBoolean(6, getCust.canRent());
            prepState.executeUpdate();

            //Vehicle
            prepState = con.prepareStatement(vehQuery);
            rand = new Random();
            long vehNumber = Math.abs(10000 + rand.nextInt(100000));

            prepState.setLong(1, vehNumber);
            prepState.setString(2, getVeh.getMake());
            prepState.setString(3, getVeh.getCategory());
            prepState.setDouble(4, getVeh.getRentalPrice());
            prepState.setBoolean(5, getVeh.isAvailableForRent());
            prepState.executeUpdate();

            //Rental
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    public void readAndWriteDB() {

        try {
            ObjectInputStream readCustSer = new ObjectInputStream(new FileInputStream(custFile));
            ObjectInputStream readVehSer = new ObjectInputStream(new FileInputStream(vehFile));

            while (true) {
                try {
//                    Customer cust = new Customer();

                    getCust = (Customer) readCustSer.readObject();
                    getCust.setName(getCust.getName());
                    getCust.setSurname(getCust.getSurname());
                    getCust.setIdNum(getCust.getIdNum());
                    getCust.setPhoneNum(getCust.getPhoneNum());
                    getCust.setCanRent(getCust.canRent());

//                    Vehicle veh = new Vehicle();
                    getVeh = (Vehicle) readVehSer.readObject();
                    getVeh.setMake(getVeh.getMake());

                    //Choose vehicle Category
                    if (getVeh.getCategory().equals("Sedan")) {
                        getVeh.setCategory(1);

                    } else if (getVeh.getCategory().equals("SUV")) {
                        getVeh.setCategory(2);
                    }

                    getVeh.setMake(getVeh.getMake());
                    getVeh.setRentalPrice(getVeh.getRentalPrice());
                    getVeh.setAvailableForRent(getVeh.isAvailableForRent());

                    writeIntoTable();
                } catch (EOFException ex) {
                    return;
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException: " + ex.getMessage());
        }
    }
}
