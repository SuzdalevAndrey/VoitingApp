package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    ViewUserCommand viewUserCommand;

    @Test
    void execute_ShowAllTopics_WhenNoParamsAndTopicExists() {
        String[] param = new String[]{};
        Map<String, Topic> topics = Map.of(
                "Topic1", new Topic("Topic1"),
                "Topic2", new Topic("Topic2")
        );
        when(topicRepository.findAll()).thenReturn(topics);

        viewUserCommand.execute(ctx, param);

        for (Map.Entry<String, Topic> entry : topics.entrySet()) {
            verify(ctx, times(1))
                    .writeAndFlush(String.format("\"%s\" (votes in topic=%s)\n",
                            entry.getKey(),
                            entry.getValue().countVotes()
                    ));
        }
        verify(topicRepository, times(1)).findAll();
        verifyNoMoreInteractions(ctx, topicRepository);
    }

    @Test
    void execute_ShowNothing_WhenNoParamsAndTopicsNotExist() {
        String[] param = new String[]{};
        when(topicRepository.findAll()).thenReturn(Map.of());

        viewUserCommand.execute(ctx, param);

        verify(topicRepository, times(1)).findAll();
        verifyNoMoreInteractions(topicRepository);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenSingleParamInvalid() {
        String[] params = new String[]{"-invalid=Name"};

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.view.single_param.invalid"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNameEmpty() {
        String[] params = new String[]{"-t=  "};

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, ctx);
    }

    @Test
    void execute_ShowTopicDetails_WhenTopicExists() {
        String topicName = "TopicName";
        Topic topic = new Topic(topicName);
        String[] params = new String[]{"-t=" + topicName};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1)).writeAndFlush(topic.toString());
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTwoParamsInvalid() {
        String[] params = new String[]{"-t=Topic", "-invalid=Vote"};

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.view.two_params.invalid"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicOrVoteNameEmpty() {
        String[] params = new String[]{"-t=Topic", "-v=  "};

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.vote.name.empty"));
        verifyNoInteractions(topicRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTwoParamsValidAndTopicNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTwoParamsValidAndVoteNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        Topic topic = mock(Topic.class);
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.empty());

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.not_found", voteName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, topic, ctx);
    }

    @Test
    void execute_ShowVoteDetails_WhenTwoParamsValidAndTopicAndVoteExist() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));

        viewUserCommand.execute(ctx, params);

        verify(ctx, times(1)).writeAndFlush(vote.toString());
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, topic, vote);
    }
}