package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVoteUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    HandlerFactory handlerFactory;

    @Mock
    MessageService messageService;

    @Mock
    HandlerService handlerService;

    @InjectMocks
    CreateVoteUserCommand createVoteUserCommand;

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName};

        when(topicRepository.containsTopicByName(topicName)).thenReturn(false);

        createVoteUserCommand.execute(ctx, params);

        verify(topicRepository, times(1)).containsTopicByName(topicName);
        verify(messageService, times(1)).sendMessageByKey(ctx, "error.topic.not_found", topicName);
        verifyNoMoreInteractions(topicRepository, messageService);
        verifyNoInteractions(handlerFactory);
    }

    @Test
    void execute_AddVoteDescriptionHandlerToPipelineAndSendSuccessMessage_WhenTopicFound() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName};
        VoteDescriptionHandler handler = mock(VoteDescriptionHandler.class);

        when(topicRepository.containsTopicByName(topicName)).thenReturn(true);
        when(handlerFactory.createVoteDescriptionHandler(topicName)).thenReturn(handler);

        createVoteUserCommand.execute(ctx, params);

        verify(topicRepository, times(1)).containsTopicByName(topicName);
        verify(messageService, times(1)).sendMessageByKey(ctx, "command.create_vote.success");
        verify(handlerService, times(1)).switchHandler(ctx, handler);
        verifyNoMoreInteractions(ctx, topicRepository, handlerService, messageService, handlerFactory);
    }
}