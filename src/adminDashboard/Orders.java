package adminDashboard;


import java.math.BigDecimal;

public class Orders {
    private final String name;
    private final BigDecimal contactNo;
    private final String  emailId;
    private final String address;
    private final String products;
    private final Integer cost;


    public Orders(String name, BigDecimal contactNo, String emailId, String address, String products, Integer cost) {
        super();
        this.name = name;
        this.contactNo = contactNo;
        this.emailId = emailId;
        this.address = address;
        this.products = products;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getContactNo() {
        return contactNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getAddress() {
        return address;
    }

    public String getProducts() {
        return products;
    }

    public Integer getCost() {
        return cost;
    }
}
