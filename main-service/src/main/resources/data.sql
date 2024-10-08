DELETE FROM comment where id > 0;
DELETE FROM participation_request where id > 0;
DELETE FROM compilation_event where event_id > 0;
DELETE FROM event where id > 0;
DELETE FROM category where id > 0;
DELETE FROM app_user where id > 0;
DELETE FROM compilation where id > 0;
DELETE FROM location where id > 0;

ALTER SEQUENCE comment_id_seq RESTART WITH 1;
ALTER SEQUENCE participation_request_id_seq RESTART WITH 1;
ALTER SEQUENCE event_id_seq RESTART WITH 1;
ALTER SEQUENCE category_id_seq RESTART WITH 1;
ALTER SEQUENCE APP_USER_ID_SEQ RESTART WITH 1;
ALTER SEQUENCE compilation_id_seq RESTART WITH 1;
ALTER SEQUENCE location_id_seq RESTART WITH 1;