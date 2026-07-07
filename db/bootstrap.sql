-- One-time local dev bootstrap for an EXISTING PostgreSQL instance (e.g. the
-- native PostgreSQL service on port 5432). Creates the role + database the app
-- expects (matches application-dev.yml). Run once as the `postgres` superuser:
--
--   & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -f backend\db\bootstrap.sql
--
-- (psql prompts for the postgres password you set during install.)
-- Skip this entirely if you use docker-compose instead.

CREATE ROLE transfer WITH LOGIN PASSWORD 'transfer';
CREATE DATABASE transfer_tourist OWNER transfer;
GRANT ALL PRIVILEGES ON DATABASE transfer_tourist TO transfer;
