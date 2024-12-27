package com.project.makehandshake.model;

import io.netty.util.internal.StringUtil;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HandShake {
    @Nonnull
    String name;
    @Nonnull
    String email;

    public static boolean isValidHandShake(HandShake handShake) {
        return Objects.nonNull(handShake) && !StringUtil.isNullOrEmpty(handShake.name)
                && !StringUtil.isNullOrEmpty(handShake.email);
    }
}
