package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTopicUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    CreateTopicUserCommand createTopicUserCommand;

    @Test
    void execute_SendErrorMessage_WhenCountParamInvalid() {
        String[] params = new String[]{};

        createTopicUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.create_topic.invalid"));
        verifyNoInteractions(topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenParamInvalid() {
        String[] params = new String[]{"-invalid=Name"};

        createTopicUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.create_topic.invalid"));
        verifyNoInteractions(topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNameEmpty() {
        String[] params = new String[]{"-n=  "};

        createTopicUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
        verifyNoInteractions(topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNameAlreadyExist() {
        String topicName = "topicName";
        String[] params = new String[]{"-n="+topicName};
        when(topicRepository.containsTopicByName(topicName)).thenReturn(true);

        createTopicUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.already_exist", topicName));
        verify(topicRepository, times(1)).containsTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository);
    }

    @Test
    void execute_CreateTopicAndSendSuccessMessage_WhenValidDataAndTopicNoExist() {
        String topicName = "topicName";
        String[] params = new String[]{"-n="+topicName};
        when(topicRepository.containsTopicByName(topicName)).thenReturn(false);

        createTopicUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("command.create_topic.success", topicName));
        verify(topicRepository, times(1)).containsTopicByName(topicName);
        verify(topicRepository, times(1)).saveTopic(any(Topic.class));
        verifyNoMoreInteractions(topicRepository);
    }
}