package cart.domain;

import java.util.List;
import java.util.Objects;

public class Order {

    private Long id;
    private final OrderStatus orderStatus;
    private final int usedPoint;
    private final List<OrderItem> orderItems;
    private final Member member;

    public Order(int usedPoint, List<OrderItem> orderItems, Member member) {
        this.orderStatus = OrderStatus.PENDING;
        this.usedPoint = usedPoint;
        this.orderItems = orderItems;
        this.member = member;
    }

    public Order(Long id, OrderStatus orderStatus, int usedPoint, List<OrderItem> orderItems, Member member) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.usedPoint = usedPoint;
        this.orderItems = orderItems;
        this.member = member;
    }

    public Point calculateSavedPoint(PointAccumulationPolicy pointAccumulationPolicy) {
        int totalCost = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum() - usedPoint;

        return pointAccumulationPolicy
                .calculateAccumulationPoint(totalCost);
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public int getUsedPoint() {
        return usedPoint;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return usedPoint == order.usedPoint && Objects.equals(id, order.id) && orderStatus == order.orderStatus && Objects.equals(orderItems, order.orderItems) && Objects.equals(member, order.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderStatus, usedPoint, orderItems, member);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderStatus=" + orderStatus +
                ", usedPoint=" + usedPoint +
                ", orderItems=" + orderItems +
                ", member=" + member +
                '}';
    }
}
