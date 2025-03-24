package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.MessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpUserCommandTest {

    @Mock
    MessageService messageService;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    HelpUserCommand helpUserCommand;

    @Test
    void execute_SendMessage() {
        helpUserCommand.execute(ctx, new String[]{});

        verify(messageService, times(1)).sendMessageByKey(ctx, "help.text");
        verifyNoInteractions(ctx);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void getType_ReturnHelpType() {
        UserCommandType response = helpUserCommand.getType();

        assertEquals(response, UserCommandType.HELP);
    }
}