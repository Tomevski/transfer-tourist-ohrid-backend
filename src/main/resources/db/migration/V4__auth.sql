-- V4 auth (Milestone 2.4).
-- Adds the display name used in the login response `admin.name` (the frontend
-- AuthResult carries { email, name }). The app_user table is empty at this
-- point (the admin is seeded at application startup, not in SQL, so the bcrypt
-- hash is produced by the configured PasswordEncoder rather than embedded here),
-- so adding a NOT NULL column with a transient default is safe.

ALTER TABLE app_user ADD COLUMN name VARCHAR(120) NOT NULL DEFAULT '';
ALTER TABLE app_user ALTER COLUMN name DROP DEFAULT;
