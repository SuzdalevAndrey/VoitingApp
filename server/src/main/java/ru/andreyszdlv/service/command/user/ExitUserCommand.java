package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class ExitUserCommand implements UserCommandHandler {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        System.out.println(userRepository.removeUser(ctx.channel().id().asLongText()));
    }
}
