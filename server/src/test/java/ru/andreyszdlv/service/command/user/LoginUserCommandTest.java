package ru.andreyszdlv.service.command.user;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.UserService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserCommandTest {

    @Mock
    UserService userService;

    @Mock
    MessageService messageService;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    LoginUserCommand loginUserCommand;

    @Test
    void execute_SendErrorMessage_WhenUserAlreadyExists() {
        String username = "user";
        String[] params = new String[]{"-u=" + username};
        Channel channel = mock(Channel.class);

        when(ctx.channel()).thenReturn(channel);
        when(userService.createUserIfNotExist(channel, username)).thenReturn(false);

        loginUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.user.already_exist", username);
        verify(userService, times(1)).createUserIfNotExist(channel, username);
        verifyNoMoreInteractions(userService, messageService, ctx);
    }

    @Test
    void execute_SaveUserAndSendSuccessMessage_WhenUserNotExist() {
        String username = "user";
        String[] params = new String[]{"-u=" + username};
        Channel channel = mock(Channel.class);

        when(ctx.channel()).thenReturn(channel);
        when(userService.createUserIfNotExist(channel, username)).thenReturn(true);

        loginUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "command.login.success", username);
        verify(userService, times(1)).createUserIfNotExist(channel, username);
        verifyNoMoreInteractions(userService, messageService, ctx);
    }
}