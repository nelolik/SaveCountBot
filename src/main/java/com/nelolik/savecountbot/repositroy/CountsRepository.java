package com.nelolik.savecountbot.repositroy;

import com.nelolik.savecountbot.model.Counts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountsRepository extends JpaRepository<Counts, Long> {

    List<Counts> findByRecordid(Long recordId);

    void deleteAllByRecordid(Long recordId);

}
