CREATE SEQUENCE  IF NOT EXISTS counts_seq START WITH 1 INCREMENT BY 1;

insert into counts (id, recordid, count, date)
        values (nextval('counts_seq'), 1, 100, current_timestamp() ),
               (nextval('counts_seq'), 1, 200, current_timestamp()),
               (nextval('counts_seq'), 1, 100, current_timestamp()),
               (nextval('counts_seq'), 2, 100, current_timestamp()),
               (nextval('counts_seq'), 2, 300, current_timestamp()),
               (nextval('counts_seq'), 3, 500, current_timestamp()),
               (nextval('counts_seq'), 4, 20, current_timestamp()),
               (nextval('counts_seq'), 4, 20, current_timestamp());

