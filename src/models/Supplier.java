package models;

public class Supplier {
    private int supplierID;
    private String name;
    private String email;
    private String phone;

    /**
     * For reading supplier object from the database
     *
     * @param supplierID - unique supplier identifier
     * @param name       - the name of the supplier
     * @param email      - email address of the supplier
     * @param phone      - phone number of the supplier
     */
    public Supplier(int supplierID, String name, String email, String phone) {
        this.supplierID = supplierID;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /**
     * For creating new supplier
     *
     * @param name  - the name of the supplier
     * @param email - email address of the supplier
     * @param phone - phone number of the supplier
     */
    public Supplier(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Supplier{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}