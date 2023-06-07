package cart.repository;

import cart.dao.OrderDao;
import cart.dao.OrderItemDao;
import cart.dao.PointHistoryDao;
import cart.dao.ProductDao;
import cart.domain.*;
import cart.entity.*;
import cart.exception.OrderException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MysqlOrderRepository implements OrderRepository {

    private static final String INVALID_DELETE_MESSAGE = "이미 해당 주문을 취소했습니다.";
    private static final String INVALID_FIND_MESSAGE = "주문 정보를 올바르게 입력해주세요.";

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final PointHistoryDao pointHistoryDao;
    private final ProductDao productDao;

    public MysqlOrderRepository(OrderDao orderDao, OrderItemDao orderItemDao, PointHistoryDao pointHistoryDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.pointHistoryDao = pointHistoryDao;
        this.productDao = productDao;
    }

    public Long save(Long memberId, Order order) {
        try {
            OrderEntity orderEntity = new OrderEntity(memberId, order.getOrderStatus().getOrderStatusId());
            Long orderId = orderDao.save(orderEntity);

            List<OrderItemEntity> orderItemEntities = getOrderItemEntities(order);

            orderItemDao.saveAll(orderId, orderItemEntities);

            List<PointEntity> pointEntities = getPointEntities(order);
            pointHistoryDao.saveAll(orderId, pointEntities);
            return orderId;
        } catch (DataAccessException exception) {
            throw new OrderException(INVALID_FIND_MESSAGE);
        }
    }

    private List<OrderItemEntity> getOrderItemEntities(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> new OrderItemEntity(orderItem.getProduct().getId(), orderItem.getQuantity(), orderItem.getTotalPrice()))
                .collect(Collectors.toList());
    }

    private List<PointEntity> getPointEntities(Order order) {
        return order.getUsedPoints()
                .stream()
                .map(point -> new PointEntity(point.getId(), point.getValue(), point.getComment(),
                        point.getCreateAt(), point.getExpiredAt()))
                .collect(Collectors.toList());
    }

    public void delete(Long memberId, Long orderId) {
        validateDelete(memberId, orderId);
        orderDao.delete(memberId, orderId);
    }

    private void validateDelete(Long memberId, Long orderId) {
        OrderEntity orderEntity;
        try {
            orderEntity = orderDao.findBy(memberId, orderId);
        } catch (DataAccessException dataAccessException) {
            throw new OrderException(INVALID_FIND_MESSAGE);
        }
        if (orderEntity.getOrderStatusId() == OrderStatus.CANCELED.getOrderStatusId()) {
            throw new OrderException(INVALID_DELETE_MESSAGE);
        }
    }

    public List<Order> findAllByMemberId(Long memberId) {
        List<OrderEntity> orderEntities = orderDao.findAllByMemberId(memberId);
        List<Long> orderIds = getOrderIds(orderEntities);

        List<OrderItemEntity> orderItemEntities = orderItemDao.findAllByOrderIds(orderIds);

        Map<Long, List<OrderItemEntity>> itemsByOrder = getItemsByOrder(orderEntities, orderItemEntities);

        return getOrders(orderEntities, itemsByOrder);
    }

    private Map<Long, List<OrderItemEntity>> getItemsByOrder(List<OrderEntity> orderEntities, List<OrderItemEntity> orderItems) {
        Map<Long, List<OrderItemEntity>> itemsByOrder = new HashMap<>();
        for (OrderEntity orderEntity : orderEntities) {
            itemsByOrder.put(orderEntity.getId(), new ArrayList<>());
        }

        for (OrderItemEntity orderItemEntity : orderItems) {
            List<OrderItemEntity> orderItemEntities = itemsByOrder.get(orderItemEntity.getOrderId());
            orderItemEntities.add(orderItemEntity);
        }
        return itemsByOrder;
    }

    private List<Order> getOrders(List<OrderEntity> orderEntities, Map<Long, List<OrderItemEntity>> itemsByOrder) {
        List<Order> orders = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            Long id = orderEntity.getId();
            OrderStatus orderStatus = OrderStatus.findOrderStatusById(orderEntity.getOrderStatusId());
            List<OrderItem> orderItems = getOrderItems(itemsByOrder, id);
            Points points = getPoints(id);
            LocalDate createAt = orderEntity.getCreateAt();
            orders.add(new Order(id, orderStatus, points, orderItems, createAt));
        }
        return orders;
    }

    private List<OrderItem> getOrderItems(Map<Long, List<OrderItemEntity>> itemsByOrder, Long id) {
        return itemsByOrder.get(id).stream()
                .map(orderItemEntity -> new OrderItem(getProduct(orderItemEntity),
                        orderItemEntity.getQuantity(), orderItemEntity.getTotalPrice()))
                .collect(Collectors.toList());
    }

    private Points getPoints(Long id) {
        List<PointHistoryEntity> pointHistoryEntities = pointHistoryDao.findByOrderId(id);
        return new Points(pointHistoryEntities.stream()
                .map(pointHistoryEntity -> Point.from(pointHistoryEntity.getUsedPoint()))
                .collect(Collectors.toList()));
    }

    public Order findOrder(Long memberId, Long orderId) {
        try {
            OrderEntity orderEntity = orderDao.findBy(memberId, orderId);
            OrderStatus orderStatus = OrderStatus.findOrderStatusById(orderEntity.getOrderStatusId());
            Points points = getPoints(orderId);
            List<OrderItem> orderItems = getOrderItems(orderId);
            LocalDate createAt = orderEntity.getCreateAt();
            return new Order(orderId, orderStatus, points, orderItems, createAt);
        } catch (DataAccessException exception) {
            throw new OrderException(INVALID_FIND_MESSAGE);
        }
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        List<OrderItemEntity> orderItemEntities = orderItemDao.findByOrderId(orderId);
        return orderItemEntities.stream()
                .map(orderItemEntity -> new OrderItem(getProduct(orderItemEntity),
                        orderItemEntity.getQuantity(), orderItemEntity.getTotalPrice()))
                .collect(Collectors.toList());
    }

    private Product getProduct(OrderItemEntity orderItemEntity) {
        Long productId = orderItemEntity.getProductId();
        ProductEntity productEntity = productDao.getProductById(productId);
        return new Product(productEntity.getId(), productEntity.getName(),
                productEntity.getPrice(), productEntity.getImageUrl());
    }

    private List<Long> getOrderIds(List<OrderEntity> orderEntities) {
        return orderEntities.stream()
                .map(OrderEntity::getId)
                .collect(Collectors.toList());
    }
}
