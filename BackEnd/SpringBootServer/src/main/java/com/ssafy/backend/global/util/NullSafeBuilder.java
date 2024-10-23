package com.ssafy.backend.global.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NullSafeBuilder {
    public static BooleanBuilder build(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException exception) {
            return new BooleanBuilder();
        } catch (NullPointerException ex) {
            return new BooleanBuilder();
        }
    }
}
