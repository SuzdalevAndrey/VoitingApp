package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserCommandService {

    private final Map<UserCommandType, UserCommandHandler> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator = new AuthenticationValidator();

    public UserCommandService() {
        commands.put(UserCommandType.LOGIN, new LoginUserCommand());
        commands.put(UserCommandType.EXIT, new ExitUserCommand());
        commands.put(UserCommandType.CREATE_TOPIC,
                new CreateTopicUserCommand(new TopicRepository()));
        commands.put(UserCommandType.CREATE_VOTE, new CreateVoteUserCommand(new TopicRepository()));
        commands.put(UserCommandType.VIEW, new ViewUserCommand());
        commands.put(UserCommandType.VOTE, new VoteUserCommand());
        commands.put(UserCommandType.DELETE,
                new DeleteUserCommand(new TopicRepository(), new UserRepository()));
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