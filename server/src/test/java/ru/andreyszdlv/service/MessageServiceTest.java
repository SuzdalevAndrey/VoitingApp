package ru.andreyszdlv.service;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    ChannelHandlerContext ctx;

    MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService();
    }

    @Test
    void sendMessageByKey_SendFormattedMessage() {
        String messageKey = "command.success";
        Object[] args = {"param1", "param2"};
        String expectedMessage = "Success message";

        try (MockedStatic<MessageProviderUtil> mockedMessageProvider = mockStatic(MessageProviderUtil.class)) {
            mockedMessageProvider.when(() -> MessageProviderUtil.getMessage(messageKey, args))
                    .thenReturn(expectedMessage);

            messageService.sendMessageByKey(ctx, messageKey, args);

            verify(ctx, times(1)).writeAndFlush(expectedMessage);
            verifyNoMoreInteractions(ctx);
        }
    }

    @Test
    void sendMessage_SendRawMessage() {
        String message = "message";

        messageService.sendMessage(ctx, message);

        verify(ctx, times(1)).writeAndFlush(message);
    }
}