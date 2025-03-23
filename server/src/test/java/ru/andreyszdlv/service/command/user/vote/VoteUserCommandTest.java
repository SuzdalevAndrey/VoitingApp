package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    HandlerFactory handlerFactory;

    @Mock
    HandlerService handlerService;

    @Mock
    MessageService messageService;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    VoteUserCommand voteUserCommand;

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        voteUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic_vote.not_found", topicName, voteName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, messageService);
        verifyNoInteractions(handlerFactory, handlerService);
    }

    @Test
    void execute_SendErrorMessage_WhenVoteNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Topic topic = mock(Topic.class);

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.containsVoteByName(voteName)).thenReturn(false);

        voteUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic_vote.not_found", topicName, voteName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).containsVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, messageService, topic);
        verifyNoInteractions(handlerFactory, handlerService);
    }

    @Test
    void execute_SendOptionsAndAddHandler_WhenValidInput() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);
        List<AnswerOption> options = List.of(new AnswerOption("Yes"), new AnswerOption("No"));
        VoteAnswerHandler voteAnswerHandler = mock(VoteAnswerHandler.class);

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.containsVoteByName(voteName)).thenReturn(true);
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));
        when(vote.getAnswerOptions()).thenReturn(options);
        when(handlerFactory.createVoteAnswerHandler(vote)).thenReturn(voteAnswerHandler);

        voteUserCommand.execute(ctx, params);

        String expectedResponse = "Варианты ответа:\n" +
                "Вариант #1: Yes\n" +
                "Вариант #2: No\n" +
                MessageProviderUtil.getMessage("command.vote.success");

        verify(messageService, times(1)).sendMessage(ctx, expectedResponse);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verify(handlerService, times(1)).switchHandler(ctx, voteAnswerHandler);
        verifyNoMoreInteractions(topicRepository, messageService, handlerService, handlerFactory);
        verifyNoInteractions(ctx);
    }
}