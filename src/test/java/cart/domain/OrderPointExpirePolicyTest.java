package cart.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.JavaTimeConversionPattern;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPointExpirePolicyTest {

    @DisplayName("3개월 뒤의 마지막 날로 만료일자를 구할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"2023.03.13:2023.06.30", "2023.02.28:2023.05.31", "2023.10.13:2024.01.31",
            "2023.12.31:2024.03.31", "2023.11.21:2024.02.29"}, delimiter = ':')
    void calculateExpireDate(@JavaTimeConversionPattern("yyyy.MM.dd")LocalDate now,
                             @JavaTimeConversionPattern("yyyy.MM.dd")LocalDate expected) {
        OrderPointExpirePolicy orderPointExpirePolicy = new OrderPointExpirePolicy();

        assertThat(orderPointExpirePolicy.calculateExpireDate(now)).isEqualTo(expected);
    }

    @DisplayName("동일한 달에 만료가 된다면 true를 반환하고, 아니라면 false를 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"2023.03.13:2023.03.30:true", "2023.02.28:2023.02.28:true", "2023.10.13:2024.01.31:false",
            "2023.11.31:2024.11.31:false", "2023.11.21:2023.12.29:false"}, delimiter = ':')
    void calculateExpireDate(@JavaTimeConversionPattern("yyyy.MM.dd")LocalDate now,
                             @JavaTimeConversionPattern("yyyy.MM.dd")LocalDate other,
                             boolean expected) {
        OrderPointExpirePolicy orderPointExpirePolicy = new OrderPointExpirePolicy();

        assertThat(orderPointExpirePolicy.isSoonExpireDate(now, other)).isEqualTo(expected);
    }
}
