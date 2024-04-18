package models;

public class Product {
    private int productID;
    private String name;
    private String description;
    private double price;
    private int quantityInStock;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    /**
     * For reading product object from the database
     * @param productID - uniques product identifier
     * @param name - the name of the product
     * @param description - product description
     * @param price - The price of the product
     * @param quantityInStock - The quantity for the product remaining u
     */
    public Product(int productID, String name, String description, double price, int quantityInStock) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }
    /**
     * For creating new product
     * @param name - the name of the product
     * @param description - product description
     * @param price - The price of the product
     * @param quantityInStock - The quantity for the product remaining u
     */
    public Product(String name, String description, double price, int quantityInStock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantityInStock=" + quantityInStock +
                '}';
    }
}