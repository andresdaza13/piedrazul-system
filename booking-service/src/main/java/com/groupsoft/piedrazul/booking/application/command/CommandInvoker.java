package com.groupsoft.piedrazul.booking.application.command;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PATRON COMMAND - Invoker: despacha el comando al manejador adecuado (Open/Closed).
 */
@Component
public class CommandInvoker {

    private final List<CommandHandler<?, ?>> handlers;

    public CommandInvoker(List<CommandHandler<?, ?>> handlers) {
        this.handlers = handlers;
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Command<R> command) {
        return handlers.stream()
                .filter(handler -> handler.supports(command))
                .findFirst()
                .map(handler -> ((CommandHandler<Command<R>, R>) handler).execute(command))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe manejador para el comando: " + command.getClass().getSimpleName()));
    }
}
