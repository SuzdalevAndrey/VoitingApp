package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.UserRepository;

@RequiredArgsConstructor
public class ExitUserCommand implements UserCommandHandler {

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        System.out.println(userRepository.removeUser(ctx.channel().id().asLongText()));
    }
}
