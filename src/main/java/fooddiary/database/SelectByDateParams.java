package fooddiary.database;

import com.github.vdybysov.ydb.typed.annotation.Mapper;
import com.github.vdybysov.ydb.typed.mapper.BytesToStringMapper;
import com.github.vdybysov.ydb.typed.mapper.DateToLocalDateMapper;

import java.time.LocalDate;

public record SelectByDateParams(
        @Mapper(DateToLocalDateMapper.class) LocalDate date,
        @Mapper(BytesToStringMapper.class) String personId
) {
}
