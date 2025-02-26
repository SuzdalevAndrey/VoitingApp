package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class LogoutCommand implements Command {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] parts) {
        System.out.println(userRepository.removeUser(ctx.channel()));
    }
}
