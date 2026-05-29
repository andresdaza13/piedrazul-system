package com.groupsoft.piedrazul.booking.application.command;

public interface CommandHandler<C extends Command<R>, R> {

    boolean supports(Command<?> command);

    R execute(C command);
}
