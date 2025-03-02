package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.factory.RepositoryFactory;
import ru.andreyszdlv.service.command.user.createvote.CreateVoteUserCommand;
import ru.andreyszdlv.service.command.user.vote.VoteUserCommand;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserCommandService {

    private final Map<UserCommandType, UserCommandHandler> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator;

    public UserCommandService(AuthenticationValidator authenticationValidator) {
        commands.put(UserCommandType.LOGIN, new LoginUserCommand(RepositoryFactory.getUserRepository()));
        commands.put(UserCommandType.EXIT, new ExitUserCommand(RepositoryFactory.getUserRepository()));
        commands.put(UserCommandType.CREATE_TOPIC,
                new CreateTopicUserCommand(RepositoryFactory.getTopicRepository()));
        commands.put(UserCommandType.CREATE_VOTE, new CreateVoteUserCommand(RepositoryFactory.getTopicRepository()));
        commands.put(UserCommandType.VIEW, new ViewUserCommand(RepositoryFactory.getTopicRepository()));
        commands.put(UserCommandType.VOTE, new VoteUserCommand(RepositoryFactory.getTopicRepository()));
        commands.put(UserCommandType.DELETE,
                new DeleteUserCommand(RepositoryFactory.getTopicRepository(), RepositoryFactory.getUserRepository()));
        this.authenticationValidator = authenticationValidator;
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();
        log.info("Received command: {}", trimmedCommand);

        UserCommandType command = Arrays.stream(UserCommandType.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            log.warn("Unknown command received: \"{}\"", trimmedCommand);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.unknown_command", trimmedCommand));
            return;
        }

        log.info("Detected command: {}", command.getName());

        if(!command.equals(UserCommandType.LOGIN)
                && !authenticationValidator.isAuthenticated(ctx.channel())) {
            log.warn("Unauthorized access \"{}\" command by channelID \"{}\"",
                    command, ctx.channel().id().asLongText());
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.invalid_authentication"));
            return;
        }

        String paramsPart = trimmedCommand.substring(command.getName().length()).trim();
        String[] params = paramsPart.isEmpty() ? new String[0] : paramsPart.split("\\s+");

        log.info("Executing \"{}\" command with parameters: {}", command.getName(), params);
        commands.getOrDefault(command,
                (c, p) -> {
                    log.error("Command \"{}\" not registered in system", command);
                    ctx.writeAndFlush(MessageProviderUtil
                            .getMessage("error.unknown_command", trimmedCommand));
        }).execute(ctx, params);
    }
}