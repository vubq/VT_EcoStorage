-----
CREATE OR REPLACE FUNCTION generate_custom_user_id()
RETURNS TEXT AS $$
DECLARE
ts TEXT;
    rnd TEXT;
BEGIN
    ts := to_char(NOW(), 'YYYYMMDDHH24MISS');
    rnd := gen_random_uuid()::TEXT;
RETURN  rnd || '-' || 'user-' || ts;
END;
$$ LANGUAGE plpgsql;
-----
-----
CREATE OR REPLACE FUNCTION generate_custom_id()
RETURNS TEXT AS $$
DECLARE
ts TEXT;
    rnd TEXT;
BEGIN
    ts := to_char(NOW(), 'YYYYMMDDHH24MISS');
    rnd := gen_random_uuid()::TEXT;
RETURN rnd || '-' || 'vt-ecostorage-' || ts;
END;
$$ LANGUAGE plpgsql;
-----
-----
CREATE OR REPLACE FUNCTION generate_custom_permission_group_id()
RETURNS TEXT AS $$
DECLARE
ts TEXT;
    rnd TEXT;
BEGIN
    ts := to_char(NOW(), 'YYYYMMDDHH24MISS');
    rnd := gen_random_uuid()::TEXT;
RETURN rnd || '-' || 'PERMISSION_GROUP-' || ts;
END;
$$ LANGUAGE plpgsql;