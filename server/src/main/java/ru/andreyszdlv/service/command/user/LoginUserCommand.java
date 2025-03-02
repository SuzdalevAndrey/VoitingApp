package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class LoginUserCommand implements UserCommandHandler {

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if (paramsCommand.length != 1) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.login.invalid"));
            return;
        }

        String username = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-u=");

        if(username == null) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.login.invalid"));
            return;
        }

        if(username.isBlank()){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.user.name.empty"));
            return;
        }

        if(userRepository.containsUserByName(username)) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.user.already_exist", username));
            return;
        }

        userRepository.saveUser(ctx.channel().id().asLongText(), username);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.login.success", username));
    }
}