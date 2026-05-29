export interface BookingWindow {
  minDate: string;
  maxDate: string;
}

export function buildBookingWindow(bookingWindowWeeks: number): BookingWindow {
  const today = new Date();
  const max = new Date(today);
  max.setDate(max.getDate() + bookingWindowWeeks * 7);
  return {
    minDate: formatIsoDate(today),
    maxDate: formatIsoDate(max)
  };
}

function formatIsoDate(date: Date): string {
  return date.toISOString().split('T')[0];
}
