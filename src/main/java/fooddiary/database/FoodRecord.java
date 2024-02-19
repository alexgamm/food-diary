package fooddiary.database;

import com.github.vdybysov.ydb.typed.annotation.Mapper;
import com.github.vdybysov.ydb.typed.mapper.BytesToStringMapper;
import com.github.vdybysov.ydb.typed.mapper.FloatMapper;
import com.github.vdybysov.ydb.typed.mapper.TextToStringMapper;
import com.github.vdybysov.ydb.typed.mapper.TimestampToInstantMapper;

import java.time.Instant;

public record FoodRecord(
        @Mapper(BytesToStringMapper.class)
        String id,
        @Mapper(BytesToStringMapper.class)
        String personId,
        @Mapper(TextToStringMapper.class)
        String name,
        @Mapper(TimestampToInstantMapper.class)
        Instant date,
        @Mapper(FloatMapper.class)
        Float grams,
        @Mapper(FloatMapper.class)
        Float kcal,
        @Mapper(FloatMapper.class)
        Float fat,
        @Mapper(FloatMapper.class)
        Float protein,
        @Mapper(FloatMapper.class)
        Float carbohydrate
) {
}
