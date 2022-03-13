package com.nelolik.savecountbot.repositroy;

import com.nelolik.savecountbot.model.Records;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordsRepository extends JpaRepository<Records, Long> {

    List<Records> findAllByUserid(Long userid);
    List<Records> findByRecordNameAndUserid(String recordName, Long userId);
    List<Records> findByRecordName(String recordName);
    List<Records> findByUserid(Long userId);

}
