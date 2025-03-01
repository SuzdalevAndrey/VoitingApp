package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.command.CommandStrategy;

public class LogoutCommand implements CommandStrategy {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        System.out.println(userRepository.removeUser(ctx.channel().id().asLongText()));
    }
}
