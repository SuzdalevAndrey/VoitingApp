package ru.andreyszdlv.service.commands;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class LogoutCommand implements CommandStrategy {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] parts) {
        System.out.println(userRepository.removeUser(ctx.channel()));
    }
}
