package cart.dto;

import java.time.LocalDate;
import java.util.Objects;

public class OrderResponse {

    private final Long orderId;
    private final int payAmount;
    private final LocalDate orderAt;
    private final String orderStatus;
    private final String productName;
    private final String productImageUrl;
    private final int totalProductCount;

    public OrderResponse(Long orderId, int payAmount, LocalDate orderAt, String orderStatus, String productName, String productImageUrl, int totalProductCount) {
        this.orderId = orderId;
        this.payAmount = payAmount;
        this.orderAt = orderAt;
        this.orderStatus = orderStatus;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.totalProductCount = totalProductCount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public int getPayAmount() {
        return payAmount;
    }

    public LocalDate getOrderAt() {
        return orderAt;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public int getTotalProductCount() {
        return totalProductCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponse that = (OrderResponse) o;
        return payAmount == that.payAmount && totalProductCount == that.totalProductCount && Objects.equals(orderId, that.orderId) && Objects.equals(orderAt, that.orderAt) && Objects.equals(orderStatus, that.orderStatus) && Objects.equals(productName, that.productName) && Objects.equals(productImageUrl, that.productImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, payAmount, orderAt, orderStatus, productName, productImageUrl, totalProductCount);
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId=" + orderId +
                ", payAmount=" + payAmount +
                ", orderAt=" + orderAt +
                ", orderStatus='" + orderStatus + '\'' +
                ", productName='" + productName + '\'' +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", totalProductCount=" + totalProductCount +
                '}';
    }
}
