package cart.dao;

import cart.entity.PointEntity;
import cart.entity.PointHistoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql({"classpath:test_init.sql"})
@ActiveProfiles("test")
class PointHistoryDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private final PointHistoryDao pointHistoryDao;

    @Autowired
    public PointHistoryDaoTest(JdbcTemplate jdbcTemplate, PointHistoryDao pointHistoryDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.pointHistoryDao = pointHistoryDao;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("insert into product (name, price, image_url) values ('치킨', 10000, 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80')");
        jdbcTemplate.update("insert into product (name, price, image_url) values ('샐러드', 20000, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80')");
        jdbcTemplate.update("insert into product (name, price, image_url) values ('피자', 13000, 'https://images.unsplash.com/photo-1595854341625-f33ee10dbf94?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80')");

        jdbcTemplate.update("insert into member(email, password) values('konghana@com', '1234')");

        jdbcTemplate.update("insert into orders(member_id, orders_status_id) values(1, 1)");
        jdbcTemplate.update("insert into orders(member_id, orders_status_id) values(1, 1)");
        jdbcTemplate.update("insert into orders(member_id, orders_status_id) values(1, 1)");

        jdbcTemplate.update("insert into orders_item(orders_id, product_id, quantity, total_price) values(1, 1, 3, 30000)");
        jdbcTemplate.update("insert into orders_item(orders_id, product_id, quantity, total_price) values(1, 2, 2, 40000)");
        jdbcTemplate.update("insert into orders_item(orders_id, product_id, quantity, total_price) values(2, 3, 2, 26000)");
        jdbcTemplate.update("insert into orders_item(orders_id, product_id, quantity, total_price) values(1, 1, 3, 30000)");

        jdbcTemplate.update("insert into point(member_id, orders_id, earned_point, comment, create_at, expired_at) values(1, 1, 5600, '주문 포인트 적립', '2023-06-02', '2023-09-30')");
        jdbcTemplate.update("insert into point(member_id, orders_id, earned_point, comment, create_at, expired_at) values(1, 2, 1250, '주문 포인트 적립', '2023-06-15', '2023-09-30')");
        jdbcTemplate.update("insert into point(member_id, orders_id, earned_point, comment, create_at, expired_at) values(1, 3, 1400, '주문 포인트 적립', '2023-06-15', '2023-09-30')");

        jdbcTemplate.update("insert into point_history(orders_id, point_id, used_point) values(2, 1, 1000)");
        jdbcTemplate.update("insert into point_history(orders_id, point_id, used_point) values(3, 1, 2000)");
    }

    @DisplayName("포인트 id를 기준으로 사용한 포인트 이력을 조회할 수 있다.")
    @Test
    void findByPointIds() {
        PointHistoryEntity expected1 = new PointHistoryEntity(1L, 2L, 1L, 1000);
        PointHistoryEntity expected2 = new PointHistoryEntity(2L, 3L, 1L, 2000);

        List<PointHistoryEntity> pointHistoryEntities = pointHistoryDao.findByPointIds(List.of(1L));

        assertThat(pointHistoryEntities).containsExactlyInAnyOrder(expected1, expected2);
    }

    @DisplayName("주문 번호를 기준으로 사용한 포인트 이력을 조회할 수 있다.")
    @Test
    void findByOrderId() {
        PointHistoryEntity expected = new PointHistoryEntity(1L, 2L, 1L, 1000);

        List<PointHistoryEntity> pointHistoryEntities = pointHistoryDao.findByOrderId(2L);

        assertThat(pointHistoryEntities).containsExactlyInAnyOrder(expected);
    }

    @DisplayName("포인트 번호를 기준으로 이미 사용된 포인트인지 확인할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"1:true", "2:false"}, delimiter = ':')
    void isIn(Long pointId, boolean expected) {
        assertThat(pointHistoryDao.isIn(pointId)).isEqualTo(expected);
    }

    @DisplayName("포인트 사용 이력을 저장할 수 있다.")
    @Test
    void save() {
        pointHistoryDao.save(3L, 2L, 5000);

        Integer usedPoint = jdbcTemplate.queryForObject("select used_point from point_history where orders_id = 3 and point_id = 2", Integer.class);

        assertThat(usedPoint).isEqualTo(5000);
    }

    @DisplayName("포인트 사용 이력들을 저장할 수 있다.")
    @Test
    void saveAll() {
        PointEntity pointEntity1 = new PointEntity(1L, 1000, "테스트 주문 포인트", LocalDate.of(2023, 06, 02), LocalDate.of(2023, 9, 30));
        PointEntity pointEntity2 = new PointEntity(2L, 1000, "테스트 주문 포인트", LocalDate.of(2023, 06, 02), LocalDate.of(2023, 9, 30));

        pointHistoryDao.saveAll(3L, List.of(pointEntity1, pointEntity2));

        Integer usedPoint = jdbcTemplate.queryForObject("select sum(used_point) from point_history where orders_id = 3", Integer.class);

        assertThat(usedPoint).isEqualTo(4000);
    }

    @DisplayName("주문 번호를 기준으로 포인트 사용 이력을 삭제할 수 있다.")
    @Test
    void deleteByOrderId() {
        pointHistoryDao.deleteByOrderId(2L);

        assertThatThrownBy(() -> jdbcTemplate.queryForObject("select id from point_history where orders_id = 2 ", Long.class))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
