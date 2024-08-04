DELETE FROM event where id > 0;
DELETE FROM category where id > 0;
DELETE FROM app_user where id > 0;
DELETE FROM compilation where id > 0;

ALTER SEQUENCE event_id_seq RESTART WITH 1;
ALTER SEQUENCE category_id_seq RESTART WITH 1;
ALTER SEQUENCE user_id_seq RESTART WITH 1;
ALTER SEQUENCE compilation_id_seq RESTART WITH 1;