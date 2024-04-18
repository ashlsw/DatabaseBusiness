package models;

public class SalesProduct {
    private Product product;
    private int quantitySold;
    private int saleID;

    /**
     * For reading sales product object from the database
     *
     * @param saleID       - unique sale identifier
     * @param product      - the product sold in the sale
     * @param quantitySold - quantity of the product sold in the sale
     */
    public SalesProduct(int saleID, Product product, int quantitySold) {
        this.saleID = saleID;
        this.product = product;
        this.quantitySold = quantitySold;
    }

    /**
     * For creating new sales product
     *
     * @param product      - the product sold in the sale
     * @param quantitySold - quantity of the product sold in the sale
     */
    public SalesProduct(Product product, int quantitySold) {
        this.product = product;
        this.quantitySold = quantitySold;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public int getSaleID() {
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    @Override
    public String toString() {
        return "SalesProduct{" +
                "quantitySold=" + quantitySold +
                ", saleID=" + saleID +
                '}';
    }
}

