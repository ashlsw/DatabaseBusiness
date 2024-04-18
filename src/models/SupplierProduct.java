package models;

public class SupplierProduct {
    private Supplier supplier;
    private Product product;

    /**
     * For reading supplier product object from the database
     *
     * @param supplier - Product Supplier
     * @param product  - Product
     */
    public SupplierProduct(Supplier supplier, Product product) {
        this.supplier = supplier;
        this.product = product;
    }

    /**
     * For creating new supplier product
     *
     * @param product - Product
     */
    public SupplierProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "SupplierProduct{" +
                "supplier=" + supplier +
                ", product=" + product +
                '}';
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}