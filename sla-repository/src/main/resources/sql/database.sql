-- SQL file to create default database and user
CREATE DATABASE ${db.name};
CREATE USER ${db.username}@localhost
  IDENTIFIED BY '${db.password}';
GRANT ALL PRIVILEGES ON ${db.name}.* TO ${db.username}@localhost;

