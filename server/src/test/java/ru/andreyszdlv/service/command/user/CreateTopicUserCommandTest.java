package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.TopicService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTopicUserCommandTest {

    @Mock
    TopicService topicService;

    @Mock
    MessageService messageService;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    CreateTopicUserCommand createTopicUserCommand;

    @Test
    void execute_SendErrorMessage_WhenTopicAlreadyExist() {
        String topicName = "topicName";
        String[] params = new String[]{"-n=" + topicName};
        when(topicService.createTopicIfNotExists(topicName)).thenReturn(false);

        createTopicUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic.already_exist", topicName);
        verify(topicService, times(1)).createTopicIfNotExists(topicName);
        verifyNoMoreInteractions(topicService, messageService);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_CreateTopicAndSendSuccessMessage_WhenValidDataAndTopicNoExist() {
        String topicName = "topicName";
        String[] params = new String[]{"-n=" + topicName};
        when(topicService.createTopicIfNotExists(topicName)).thenReturn(true);

        createTopicUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "command.create_topic.success", topicName);
        verify(topicService, times(1)).createTopicIfNotExists(topicName);
        verifyNoMoreInteractions(topicService, messageService);
        verifyNoInteractions(ctx);
    }
}