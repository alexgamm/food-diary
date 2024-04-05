package fooddiary.database;

import com.github.vdybysov.ydb.typed.annotation.Mapper;
import com.github.vdybysov.ydb.typed.mapper.BytesToStringMapper;

public record PersonIdParam(
        @Mapper(BytesToStringMapper.class)
        String personId
) {
}
