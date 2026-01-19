-- identity_service
GRANT
  SELECT,
  INSERT,
  UPDATE,
  DELETE,
  CREATE,
  ALTER,
  DROP
ON identity_service.*
TO 'onlyone'@'%';

-- user_service
GRANT
  SELECT,
  INSERT,
  UPDATE,
  DELETE,
  CREATE,
  ALTER,
  DROP
ON user_service.*
TO 'onlyone'@'%';

GRANT REFERENCES ON identity_service.* TO 'onlyone'@'%';
GRANT REFERENCES ON user_service.* TO 'onlyone'@'%';

FLUSH PRIVILEGES;
