package com.nelolik.savecountbot.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "records")
public class Records {
    @Id
    @GeneratedValue(generator = "records_sequence", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(
            name = "records_sequence",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "records_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "userid", nullable = false)
    private Long userid;

    @Column(name = "record_name", nullable = false)
    private String recordName;

    @Override
    public String toString() {
        return "Records{" +
                "userid=" + userid +
                ", recordName='" + recordName + '\'' +
                '}';
    }
}
