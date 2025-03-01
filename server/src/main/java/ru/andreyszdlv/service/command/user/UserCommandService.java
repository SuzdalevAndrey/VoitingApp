package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.InMemoryTopicRepository;
import ru.andreyszdlv.repo.InMemoryUserRepository;
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
            ctx.writeAndFlush("Ошибка: неизвестная команда: " + trimmedCommand);
            return;
        }

        if(!command.equals(UserCommandType.LOGIN)
                && !authenticationValidator.isAuthenticated(ctx.channel())) {
            ctx.writeAndFlush("Ошибка: перед выполнением действий надо войти в систему. " +
                    "Пример: login -u=user");
            return;
        }

        String paramsPart = trimmedCommand.substring(command.getName().length()).trim();
        String[] params = paramsPart.isEmpty() ? new String[0] : paramsPart.split("\\s+");

        commands.getOrDefault(command,
                (c, p) -> ctx.writeAndFlush("Ошибка: неизвестная команда: " + trimmedCommand))
                .execute(ctx, params);
    }
}