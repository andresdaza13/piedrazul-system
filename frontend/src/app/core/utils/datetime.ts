/**
 * Construye un LocalDateTime ISO (yyyy-MM-ddTHH:mm:ss) para el backend.
 * Las franjas del API pueden venir como "08:30" o "08:30:00".
 */
export function toLocalDateTimeIso(date: string, time: string): string {
  const parts = time.trim().split(':');
  const hours = (parts[0] ?? '00').padStart(2, '0');
  const minutes = (parts[1] ?? '00').padStart(2, '0');
  return `${date}T${hours}:${minutes}:00`;
}
