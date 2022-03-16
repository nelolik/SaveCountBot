package com.nelolik.savecountbot.repository;

import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TestRecordsRepository {

    @Autowired
    private RecordsRepository repository;

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    void findAllByUserIdTest() {
        List<Records> records = repository.findAllByUserid(1l);
        assertThat(records).isNotNull()
                .extracting(r -> r.getRecordName())
                .containsExactlyInAnyOrder("prostrations", "dordje sempa");
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    void findUserIdByRecordNameTest() {
        List<Records> records1 = repository.findByRecordNameAndUserid("prostrations", 1l);
        assertThat(records1).isNotNull().extracting(r -> r.getUserid()).contains(1l);
        List<Records> records2 = repository.findByRecordNameAndUserid("guru yoga", 2l);
        assertThat(records2).isNotNull().extracting(r -> r.getId()).contains(3l);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    void findByRecordNameTest() {
        List<Records> records = repository.findByRecordName("prostrations");
        assertThat(records).isNotNull().extracting(r -> r.getUserid()).contains(1l, 3l);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    void findByUseridTest() {
        List<Records> records = repository.findByUserid(1l);
        assertThat(records).isNotNull().extracting(r -> r.getUserid()).containsOnly(1l);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    void testSaveNewRecord() {
        String newName = "New record";
        Records record = new Records();
        record.setId(0l);
        record.setUserid(3l);
        record.setRecordName(newName);
        repository.save(record);
        List<Records> fromDb = repository.findByRecordName(newName);
        assertThat(fromDb).isNotNull().extracting(r -> Tuple.tuple(r.getRecordName(), r.getId()))
                .contains(Tuple.tuple(newName, 6l));
    }
}
