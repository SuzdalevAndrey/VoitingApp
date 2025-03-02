package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.InMemoryTopicRepository;
import ru.andreyszdlv.repo.InMemoryUserRepository;
import ru.andreyszdlv.service.command.user.createvote.CreateVoteUserCommand;
import ru.andreyszdlv.service.command.user.vote.VoteUserCommand;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserCommandService {

    private final Map<UserCommandType, UserCommandHandler> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator;

    public UserCommandService(AuthenticationValidator authenticationValidator) {
        commands.put(UserCommandType.LOGIN, new LoginUserCommand(new InMemoryUserRepository()));
        commands.put(UserCommandType.EXIT, new ExitUserCommand(new InMemoryUserRepository()));
        commands.put(UserCommandType.CREATE_TOPIC,
                new CreateTopicUserCommand(new InMemoryTopicRepository()));
        commands.put(UserCommandType.CREATE_VOTE, new CreateVoteUserCommand(new InMemoryTopicRepository()));
        commands.put(UserCommandType.VIEW, new ViewUserCommand(new InMemoryTopicRepository()));
        commands.put(UserCommandType.VOTE, new VoteUserCommand(new InMemoryTopicRepository()));
        commands.put(UserCommandType.DELETE,
                new DeleteUserCommand(new InMemoryTopicRepository(), new InMemoryUserRepository()));
        this.authenticationValidator = authenticationValidator;
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();

        UserCommandType command = Arrays.stream(UserCommandType.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.unknown_command", trimmedCommand));
            return;
        }

        if(!command.equals(UserCommandType.LOGIN)
                && !authenticationValidator.isAuthenticated(ctx.channel())) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.invalid_authentication"));
            return;
        }

        String paramsPart = trimmedCommand.substring(command.getName().length()).trim();
        String[] params = paramsPart.isEmpty() ? new String[0] : paramsPart.split("\\s+");

        commands.getOrDefault(command,
                (c, p) ->
                        ctx.writeAndFlush(MessageProviderUtil
                                .getMessage("error.unknown_command", trimmedCommand)))
                .execute(ctx, params);
    }
}