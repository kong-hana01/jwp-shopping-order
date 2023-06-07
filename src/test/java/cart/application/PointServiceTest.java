package cart.application;

import cart.domain.Member;
import cart.domain.Point;
import cart.domain.Points;
import cart.dto.PointResponse;
import cart.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    private PointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new PointService(pointRepository);
    }

    @DisplayName("임박한 포인트를 구할 수 있다.")
    @Test
    void findByMemberId() {
        Point point1 = Point.of(1L, 1000, "테스트", LocalDate.of(2023, 3, 5), LocalDate.of(2023, LocalDate.now().getMonth().getValue(), 20));
        Point point2 = Point.of(2L, 2000, "테스트", LocalDate.of(2023, 5, 5), LocalDate.of(2099, 8, 31));

        when(pointRepository.findUsablePointsByMemberId(1L)).thenReturn(new Points(List.of(point1, point2)));

        PointResponse expected = new PointResponse(3000, 1000);

        assertThat(pointService.findBy(new Member(1L, "kong", "1234"))).isEqualTo(expected);
    }
}
