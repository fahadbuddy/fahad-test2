package com.db.dataplatform.techtest.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

public class MD5Utils {

  public static Optional<String> createCheckSum(final String input) {
    if (hasText(input)) {
      return Optional.of(DigestUtils
              .md5Hex(input));
    }
    return Optional.empty();
  }
}
