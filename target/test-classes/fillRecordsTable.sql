--CREATE SEQUENCE  IF NOT EXISTS records_seq START WITH 1 INCREMENT BY 1;

insert into records (id, userid, record_name)
    values (nextval('records_sequence'), 1, 'prostrations'),
           (nextval('records_sequence'), 1, 'dordje sempa'),
           (nextval('records_sequence'), 2, 'guru yoga'),
           (nextval('records_sequence'), 3, 'sitdowns'),
           (nextval('records_sequence'), 3, 'prostrations');