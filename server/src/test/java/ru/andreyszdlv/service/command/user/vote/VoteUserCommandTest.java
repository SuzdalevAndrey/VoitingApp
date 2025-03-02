package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class VoteUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    ChannelPipeline pipeline;

    @InjectMocks
    VoteUserCommand voteUserCommand;

    @Test
    void execute_SendErrorMessage_WhenParamsCountInvalid() {
        String[] params = new String[]{"-t=topic"};

        voteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.vote.invalid"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenParamsFormatInvalid() {
        String[] params = new String[]{"-invalid=topic", "vote"};

        voteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.vote.invalid"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicOrVoteNameEmpty() {
        String[] params = new String[]{"-t=", "-v=vote"};

        voteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.vote.name.empty"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        voteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenVoteNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Topic topic = mock(Topic.class);
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.empty());

        voteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.not_found", voteName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, ctx);
    }

    @Test
    void execute_SendOptionsAndAddHandler_WhenValidInput() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);
        List<AnswerOption> options = List.of(new AnswerOption("Yes"), new AnswerOption("No"));
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));
        when(vote.getAnswerOptions()).thenReturn(options);
        when(ctx.pipeline()).thenReturn(pipeline);

        voteUserCommand.execute(ctx, params);

        String expectedResponse = "Варианты ответа:\n" +
                "Вариант #1: Yes\n" +
                "Вариант #2: No\n" +
                MessageProviderUtil.getMessage("command.vote.success");

        verify(ctx, times(1)).writeAndFlush(expectedResponse);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verify(pipeline, times(1)).removeLast();
        verify(pipeline, times(1)).addLast(any(VoteAnswerHandler.class));
        verifyNoMoreInteractions(topicRepository, ctx, pipeline);
    }
}