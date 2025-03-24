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
import ru.andreyszdlv.service.MessageService;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    MessageService messageService;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    ViewUserCommand viewUserCommand;

    @Test
    void execute_SendAllTopics_WhenNoParamsAndTopicExists() {
        String[] param = new String[]{};
        Map<String, Topic> topics = Map.of(
                "Topic1", new Topic("Topic1"),
                "Topic2", new Topic("Topic2")
        );
        StringBuilder response = new StringBuilder();
        for (String key : topics.keySet()) {
            response.append(String.format("\"%s\" (votes in topic=0)\n", key));
        }
        response.setLength(response.length() - System.lineSeparator().length());

        when(topicRepository.findAll()).thenReturn(topics);

        viewUserCommand.execute(ctx, param);

        verify(messageService, times(1)).sendMessage(ctx, response.toString());
        verify(topicRepository, times(1)).findAll();
        verifyNoMoreInteractions(messageService, topicRepository);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenNoParamsAndTopicsNotExist() {
        String[] param = new String[]{};

        when(topicRepository.findAll()).thenReturn(Map.of());

        viewUserCommand.execute(ctx, param);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "view.topics.not_found");
        verify(topicRepository, times(1)).findAll();
        verifyNoMoreInteractions(topicRepository, messageService);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenSingleParamAndTopicNotFound() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        viewUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic.not_found", topicName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, ctx);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_ShowTopicDetails_WhenSingleParamAndTopicExists() {
        String topicName = "TopicName";
        Topic topic = new Topic(topicName);
        String[] params = new String[]{"-t=" + topicName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));

        viewUserCommand.execute(ctx, params);

        verify(messageService, times(1)).sendMessage(ctx, topic.toString());
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, messageService);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTwoParamsAndTopicNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        viewUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic.not_found", topicName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, messageService);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenTwoParamsAndVoteNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        Topic topic = mock(Topic.class);
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.empty());

        viewUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.not_found", voteName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, topic, messageService);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_SendVoteDetails_WhenTwoParamsAndTopicAndVoteExist() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));

        viewUserCommand.execute(ctx, params);

        verify(messageService, times(1)).sendMessage(ctx, vote.toString());
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, topic, vote, messageService);
        verifyNoInteractions(ctx);
    }
}