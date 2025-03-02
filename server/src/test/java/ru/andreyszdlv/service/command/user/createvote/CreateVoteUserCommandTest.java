package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVoteUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    CreateVoteUserCommand createVoteUserCommand;

    @Test
    void execute_SendErrorMessage_WhenParamsCountInvalid() {
        String[] params = new String[]{};

        createVoteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.create_vote.invalid"));
        verifyNoMoreInteractions(ctx);
        verifyNoInteractions(topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenParamInvalid() {
        String[] params = new String[]{"-invalid=TopicName"};

        createVoteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.create_vote.invalid"));
        verifyNoMoreInteractions(ctx);
        verifyNoInteractions(topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNameEmpty() {
        String[] params = new String[]{"-t=  "};

        createVoteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
        verifyNoMoreInteractions(ctx);
        verifyNoInteractions(topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName};
        when(topicRepository.containsTopicByName(topicName)).thenReturn(false);

        createVoteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
        verify(topicRepository, times(1)).containsTopicByName(topicName);
        verifyNoMoreInteractions(ctx, topicRepository);
    }

    @Test
    void execute_AddVoteDescriptionHandlerToPipelineAndSendSuccessMessage_WhenValidInput() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName};
        ChannelPipeline pipeline = mock(ChannelPipeline.class);
        when(ctx.pipeline()).thenReturn(pipeline);
        when(topicRepository.containsTopicByName(topicName)).thenReturn(true);

        createVoteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("command.create_vote.success"));
        verify(topicRepository, times(1)).containsTopicByName(topicName);
        verify(pipeline, times(1)).removeLast();
        verify(pipeline, times(1)).addLast(any(VoteDescriptionHandler.class));
        verifyNoMoreInteractions(ctx, topicRepository, pipeline);
    }
}