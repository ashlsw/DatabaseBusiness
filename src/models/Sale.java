package models;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private User customer;
    private LocalDateTime saleDate;
    private int sellerID;
    private List<SalesProduct> products;
    private int saleID;

    /**
     * For reading sale object from the database
     *
     * @param saleID   - unique sale identifier
     * @param customer - User who bought the products
     * @param saleDate - date of the sale
     * @param sellerID - unique identifier of the user who made the sale
     * @param products - List of products sold
     */
    public Sale(int saleID, User customer, LocalDateTime saleDate, int sellerID, List<SalesProduct> products) {
        this.saleID = saleID;
        this.customer = customer;
        this.saleDate = saleDate;
        this.sellerID = sellerID;
        this.products = products;
    }

    /**
     * For creating new sale
     *
     * @param customer - User who bought the products
     * @param saleDate - date of the sale
     * @param sellerID - unique identifier of the user who made the sale
     * @param products - List of products sold
     */
    public Sale(User customer, LocalDateTime saleDate, int sellerID, List<SalesProduct> products) {
        this.customer = customer;
        this.saleDate = saleDate;
        this.sellerID = sellerID;
        this.products = products;
    }

    /**
     * Add a product to products list
     *
     * @param product - Product to add to product list
     */
    public void addProduct(Product product, int quantity) {
        this.products.add(new SalesProduct(product, quantity));
    }

    /**
     * Removes a product from the list of products based on the given product ID.
     *
     * @param productID the unique identifier of the product to be removed
     */
    public void removeProduct(int productID) {
        for (SalesProduct product : products
        ) {
            if (product.getProduct().getProductID() == productID) {
                products.remove(product);
                break;

            }

        }
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public int getSellerID() {
        return sellerID;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public List<SalesProduct> getProducts() {
        return products;
    }

    public void setProducts(List<SalesProduct> products) {
        this.products = products;
    }

    public int getSaleID() {
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    public int totalCost() {
        int total = 0;
        for (SalesProduct p : products
        ) {
            total += p.getQuantitySold() * p.getProduct().getPrice();

        }
        return total;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "customer=" +( customer==null?"N/A":  customer.getFirstName() + " " + customer.getLastName()) +
                ", saleDate=" + saleDate +
                ", Total Amount= $" + totalCost() +
                '}';
    }
}