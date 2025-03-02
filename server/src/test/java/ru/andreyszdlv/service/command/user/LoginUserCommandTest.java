package ru.andreyszdlv.service.command.user;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserCommandTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    LoginUserCommand loginUserCommand;

    @Test
    void execute_SendErrorMessage_WhenCountParamInvalid() {
        String[] params = new String[]{"-u=use", "-two=param"};

        loginUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.login.invalid"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenParamInvalid() {
        String[] params = new String[]{"-invalid=Name"};

        loginUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.login.invalid"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenUserNameEmpty() {
        String[] params = new String[]{"-u="};

        loginUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.user.name.empty"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenUserAlreadyExists() {
        String username = "user";
        String[] params = new String[]{"-u=" + username};
        when(userRepository.containsUserByName(username)).thenReturn(true);

        loginUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.user.already_exist", username));
        verify(userRepository, times(1)).containsUserByName(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void execute_SaveUserAndSendSuccessMessage_WhenValidDataAndUserNotExist() {
        String username = "user";
        String[] params = new String[]{"-u=" + username};
        Channel channel = mock(Channel.class);
        ChannelId channelId = mock(ChannelId.class);
        when(ctx.channel()).thenReturn(channel);
        when(channel.id()).thenReturn(channelId);
        when(ctx.channel().id().asLongText()).thenReturn("channelId");
        when(userRepository.containsUserByName(username)).thenReturn(false);

        loginUserCommand.execute(ctx, params);

        verify(userRepository, times(1)).containsUserByName(username);
        verify(userRepository, times(1)).saveUser("channelId", username);
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("command.login.success", username));
        verifyNoMoreInteractions(userRepository);
    }
}