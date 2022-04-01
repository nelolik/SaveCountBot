DELETE FROM counts;
ALTER SEQUENCE hibernate_sequence RESTART;

insert into counts (id, recordid, count, date)
        values (nextval('hibernate_sequence'), 1, 100, current_timestamp() ),
               (nextval('hibernate_sequence'), 1, 200, current_timestamp()),
               (nextval('hibernate_sequence'), 1, 100, current_timestamp()),
               (nextval('hibernate_sequence'), 2, 100, current_timestamp()),
               (nextval('hibernate_sequence'), 2, 300, current_timestamp()),
               (nextval('hibernate_sequence'), 3, 500, current_timestamp()),
               (nextval('hibernate_sequence'), 4, 20, current_timestamp()),
               (nextval('hibernate_sequence'), 4, 20, current_timestamp());

