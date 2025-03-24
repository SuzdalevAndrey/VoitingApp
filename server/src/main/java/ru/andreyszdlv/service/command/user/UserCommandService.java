package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.validator.AuthenticationValidator;
import ru.andreyszdlv.validator.CommandValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final List<UserCommandHandler> commands;
    private final AuthenticationValidator authenticationValidator;
    private final CommandValidator commandValidator;
    private final MessageService messageService;

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();
        log.info("Received command: {}", trimmedCommand);

        if (!commandValidator.validate(trimmedCommand)) {
            log.warn("Invalid command received: \"{}\"", trimmedCommand);
            messageService.sendMessageByKey(ctx, "error.invalid_command", trimmedCommand);
            return;
        }

        UserCommandHandler command = commands.stream()
                .filter(c -> trimmedCommand.startsWith(c.getType().getName()))
                .findFirst()
                .get();

        if (!(command.getType() == UserCommandType.LOGIN || command.getType() == UserCommandType.HELP)
                && !authenticationValidator.isAuthenticated(ctx.channel())) {
            log.warn("Unauthorized access \"{}\" command by channelID \"{}\"",
                    command, ctx.channel().id().asLongText());
            messageService.sendMessageByKey(ctx, "error.invalid_authentication");
            return;
        }

        String paramsPart = trimmedCommand.substring(command.getType().getName().length()).trim();
        String[] params = paramsPart.isEmpty() ? new String[0] : paramsPart.split("\\s+");

        log.info("Executing \"{}\" command with parameters: {}", command.getType().getName(), params);
        command.execute(ctx, params);
    }
}