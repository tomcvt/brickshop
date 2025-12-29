DO $$
BEGIN
  IF current_database() <> 'brickshopdb_test' THEN
    RAISE EXCEPTION 'This script can only be run on brickshopdb_test!';
  END IF;
END
$$;

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
ALTER SCHEMA public OWNER TO brickshop_test;
GRANT CREATE ON SCHEMA public TO brickshop_test;
GRANT ALL ON SCHEMA public TO brickshop_test;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO brickshop_test;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO brickshop_test;