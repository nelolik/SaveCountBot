package com.nelolik.savecountbot.repository;


import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.function.BinaryOperator;

@DataJpaTest
public class TestCountsRepository {

    @Autowired
    private CountsRepository repository;


    @Test
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void findByRecordIdTest() {
        List<Counts> id1count = repository.findByRecordid(1l);
        Assertions.assertEquals(3, id1count.size());
        List<Counts> id2count = repository.findByRecordid(2l);
        Assertions.assertEquals(2, id2count.size());
    }

    @Test
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void sumOfCountFieldsTest() {
        List<Counts> counts = repository.findByRecordid(1l);
        Long sum = counts.stream().map(c -> c.getCount()).reduce((x, y) -> x + y).orElse(0l);
        Assertions.assertEquals(400, sum);
    }
}
