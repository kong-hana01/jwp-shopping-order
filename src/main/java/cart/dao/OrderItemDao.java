package cart.dao;

import cart.domain.Product;
import cart.entity.OrderItemEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderItemDao {

    private static final String DUMMY = "-1";

    private final JdbcTemplate jdbcTemplate;

    public OrderItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrderItemEntity> findAll() {
        String sql = "select orders_id, product_id, quantity, total_price from orders_item" +
                " left join product on orders_item.product_id = product.id";
        return jdbcTemplate.query(sql, new OrderItemRowMapper());
    }

    public List<OrderItemEntity> findByOrderId(Long orderId) {
        String sql = "select orders_id, product_id, quantity, total_price from orders_item" +
                " left join product on orders_item.product_id = product.id where orders_id = ?";
        return jdbcTemplate.query(sql, new OrderItemRowMapper(), orderId);
    }

    public List<OrderItemEntity> findAllByOrderIds(List<Long> orderIds) {
        String inSql = getInSql(orderIds);
        String sql = String.format("select orders_id, product_id, quantity, total_price from orders_item" +
                " left join product on orders_item.product_id = product.id where orders_item.orders_id in (%s)", inSql);
        return jdbcTemplate.query(sql, new OrderItemRowMapper());
    }

    private String getInSql(List<Long> orderIds) {
        String inSql = orderIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (inSql.isEmpty()) {
            inSql = DUMMY;
        }
        return inSql;
    }

    public void saveAll(Long orderId, List<OrderItemEntity> orderItems) {
        jdbcTemplate.batchUpdate("insert into orders_item(orders_id, product_id, quantity, total_price) values(?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, orderId);
                        ps.setLong(2, orderItems.get(i).getProductId());
                        ps.setInt(3, orderItems.get(i).getQuantity());
                        ps.setInt(4, orderItems.get(i).getTotalPrice());
                    }

                    @Override
                    public int getBatchSize() {
                        return orderItems.size();
                    }
                });
    }

    private static class OrderItemRowMapper implements RowMapper<OrderItemEntity> {

        @Override
        public OrderItemEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            long productId = rs.getLong("product_id");
            long orderId = rs.getLong("orders_id");
            int quantity = rs.getInt("quantity");
            int totalPrice = rs.getInt("total_price");
            return new OrderItemEntity(orderId, productId, quantity, totalPrice);
        }
    }
}
