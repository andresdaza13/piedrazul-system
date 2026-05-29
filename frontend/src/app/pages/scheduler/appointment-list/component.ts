import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AppointmentService, AppointmentResponseDTO } from '../../../core/services/appointment/service';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';
import { buildBookingWindow } from '../../../core/utils/booking-window';
import { toLocalDateTimeIso } from '../../../core/utils/datetime';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './component.html'
})
export class AppointmentListComponent implements OnInit {
  doctors: DoctorDTO[] = [];
  selectedDoctorId: number | null = null;
  selectedDate: string = '';
  appointments: AppointmentResponseDTO[] = [];
  total: number = 0;
  loading: boolean = false;
  searched: boolean = false;
  minDate = '';
  maxDate = '';

  showRescheduleModal = false;
  selectedAppointment: AppointmentResponseDTO | null = null;
  rescheduleDate = '';
  rescheduleTime = '';
  rescheduleSlots: string[] = [];
  responsibleName = '';
  responsibleUserId = 1;
  rescheduleError = '';
  rescheduleLoading = false;
  rescheduleHistory: any[] = [];

  constructor(
    private appointmentService: AppointmentService,
    private availabilityService: AvailabilityService
  ) {}

  ngOnInit() {
    this.loadDoctors();
    this.loadBookingWindow();
  }

  private loadDoctors() {
    this.availabilityService.getDoctors().subscribe({
      next: (doctors) => { this.doctors = doctors; }
    });
  }

  private loadBookingWindow() {
    this.availabilityService.getSystemConfig().subscribe({
      next: (config) => {
        const window = buildBookingWindow(config.bookingWindowWeeks);
        this.minDate = window.minDate;
        this.maxDate = window.maxDate;
      }
    });
  }

  search() {
    if (!this.selectedDoctorId || !this.selectedDate) return;
    this.loading = true;
    this.searched = true;
    this.appointmentService.getAppointmentsByDoctorAndDate(
      this.selectedDoctorId, this.selectedDate
    ).pipe(finalize(() => { this.loading = false; }))
      .subscribe({
        next: (response) => {
          this.appointments = response.appointments;
          this.total = response.total;
        }
      });
  }

  exportCsv() {
    if (!this.selectedDoctorId || !this.selectedDate) return;
    this.appointmentService.exportCsv(this.selectedDoctorId, this.selectedDate)
      .subscribe(blob => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `citas_medico_${this.selectedDoctorId}_${this.selectedDate}.csv`;
        link.click();
        window.URL.revokeObjectURL(url);
      });
  }

  openReschedule(apt: AppointmentResponseDTO) {
    this.selectedAppointment = apt;
    const dt = new Date(apt.appointmentDate);
    this.rescheduleDate = dt.toISOString().split('T')[0];
    this.rescheduleTime = '';
    this.rescheduleSlots = [];
    this.responsibleName = '';
    this.rescheduleError = '';
    this.showRescheduleModal = true;
    this.appointmentService.getRescheduleHistory(apt.id).subscribe(history => {
      this.rescheduleHistory = history;
    });
    this.loadRescheduleSlots();
  }

  loadRescheduleSlots() {
    if (!this.selectedAppointment || !this.rescheduleDate) return;
    this.availabilityService.getAvailableSlots(
      this.selectedAppointment.doctorId,
      this.rescheduleDate
    ).subscribe(slots => {
      this.rescheduleSlots = slots;
      if (this.rescheduleTime && !slots.includes(this.rescheduleTime)) {
        this.rescheduleTime = '';
      }
    });
  }

  confirmReschedule() {
    if (!this.selectedAppointment || !this.rescheduleDate || !this.rescheduleTime || !this.responsibleName) {
      this.rescheduleError = 'Complete todos los campos y seleccione una franja disponible.';
      return;
    }
    const newDate = toLocalDateTimeIso(this.rescheduleDate, this.rescheduleTime);
    this.rescheduleLoading = true;
    this.rescheduleError = '';

    this.appointmentService.reschedule(this.selectedAppointment.id, {
      newAppointmentDate: newDate,
      responsibleUserId: this.responsibleUserId,
      responsibleName: this.responsibleName
    }).pipe(finalize(() => { this.rescheduleLoading = false; }))
      .subscribe({
        next: () => {
          this.showRescheduleModal = false;
          this.search();
        },
        error: (err) => {
          this.rescheduleError = err.error?.message || 'No se pudo re-agendar la cita.';
        }
      });
  }

  closeRescheduleModal() {
    this.showRescheduleModal = false;
    this.selectedAppointment = null;
    this.rescheduleHistory = [];
    this.rescheduleSlots = [];
  }
}
