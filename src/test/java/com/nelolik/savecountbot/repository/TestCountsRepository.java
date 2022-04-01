package com.nelolik.savecountbot.repository;


import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.BinaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TestCountsRepository {

    @Autowired
    private CountsRepository repository;


    @Test
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void findByRecordIdTest() {
        List<Counts> id1count = repository.findByRecordid(1L);
        Assertions.assertEquals(3, id1count.size());
        List<Counts> id2count = repository.findByRecordid(2L);
        Assertions.assertEquals(2, id2count.size());
    }

    @Test
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void sumOfCountFieldsTest() {
        List<Counts> counts = repository.findByRecordid(1L);
        Long sum = counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
        Assertions.assertEquals(400, sum);
    }

    @Test
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void saveNewCountTest() {
        Counts counts = new Counts(0L, 2L, 500L, new Date(System.currentTimeMillis()));
        repository.save(counts);
        List<Counts> fromDb = repository.findByRecordid(2L);
        assertThat(fromDb).isNotNull().hasSize(3).map(Counts::getCount).containsExactlyInAnyOrder(100L, 300L, 500L);
    }

    @Test
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void deleteAllByRecordIdTest() {
        Long recordId = 1L;
        List<Counts> countsBeforeDelete = repository.findByRecordid(recordId);
        Assertions.assertNotNull(countsBeforeDelete);
        Assertions.assertEquals(3, countsBeforeDelete.size());
        repository.deleteAllByRecordid(recordId);
        List<Counts> afterDelete = repository.findByRecordid(recordId);
        Assertions.assertTrue(afterDelete.isEmpty());
    }
}
