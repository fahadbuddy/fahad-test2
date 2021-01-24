package com.db.dataplatform.techtest.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MD5UtilsTest {

  @Test
  void canCreateMD5CheckSum() {
    // Given
    String input = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    String expectedCheckSum = "cecfd3953783df706878aaec2c22aa70";

    // When
    Optional<String> checkSum = MD5Utils.createCheckSum(input);

    // Then

    assertThat(checkSum).isPresent();
    assertThat(checkSum.get()).isEqualTo(expectedCheckSum);
  }

}