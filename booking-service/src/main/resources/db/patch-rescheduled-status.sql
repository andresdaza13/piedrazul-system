-- Ejecutar manualmente en booking_db si el parche automatico no aplica:
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check;
ALTER TABLE appointments ADD CONSTRAINT appointments_status_check
  CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','COMPLETED','RESCHEDULED'));
