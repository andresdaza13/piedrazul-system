package com.groupsoft.piedrazul.booking.domain.exception;

// Excepción personalizada para manejar conflictos de horario en citas.
// Extiende RuntimeException, lo que significa que es una excepción no verificada (unchecked).
// Esto permite lanzarla sin necesidad de declararla en la firma de los métodos.
public class AppointmentOverlapException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje descriptivo del error.
     * Este mensaje se utiliza para informar al cliente qué ocurrió.
     *
     * @param message Texto explicativo del conflicto (ej. "El doctor ya tiene una cita en esa fecha y hora").
     */
    public AppointmentOverlapException(String message) {
        super(message); // Llama al constructor de RuntimeException con el mensaje.
    }
}
