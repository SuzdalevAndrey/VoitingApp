package ru.andreyszdlv.service.command.user;

import io.netty.channel.Channel;
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
import ru.andreyszdlv.service.UserService;
import ru.andreyszdlv.validator.VoteValidator;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    UserService userService;

    @Mock
    VoteValidator voteValidator;

    @Mock
    MessageService messageService;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    DeleteUserCommand deleteUserCommand;

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String voteName = "VoteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        deleteUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic_vote.not_found", topicName, voteName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository, messageService);
        verifyNoInteractions(ctx, voteValidator, userService);
    }

    @Test
    void execute_SendErrorMessage_WhenVoteNotFound() {
        String topicName = "topicName";
        String voteName = "voteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Topic topic = mock(Topic.class);

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.containsVoteByName(voteName)).thenReturn(false);

        deleteUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.topic_vote.not_found", topicName, voteName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).containsVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository, topic, messageService);
        verifyNoInteractions(ctx, voteValidator, userService);
    }

    @Test
    void execute_SendErrorMessage_WhenUserNoCreateVote() {
        String topicName = "topicName";
        String voteName = "voteName";
        String userName = "userName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Channel channel = mock(Channel.class);
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);

        when(ctx.channel()).thenReturn(channel);
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));
        when(topic.containsVoteByName(voteName)).thenReturn(true);
        when(voteValidator.isUserAuthorOfVote(vote, userName)).thenReturn(false);
        when(userService.findUserNameByChannel(channel)).thenReturn(userName);

        deleteUserCommand.execute(ctx, params);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.no_delete", voteName);
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verify(userService, times(1)).findUserNameByChannel(channel);
        verifyNoMoreInteractions(topicRepository, userService, voteValidator, messageService, topic, vote);
    }

    @Test
    void execute_DeleteVoteAndSendSuccessMessage_WhenUserCreateVote() {
        String topicName = "topicName";
        String voteName = "voteName";
        String userName = "userName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Channel channel = mock(Channel.class);
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);

        when(ctx.channel()).thenReturn(channel);
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));
        when(topic.containsVoteByName(voteName)).thenReturn(true);
        when(userService.findUserNameByChannel(channel)).thenReturn(userName);
        when(voteValidator.isUserAuthorOfVote(vote, userName)).thenReturn(true);

        deleteUserCommand.execute(ctx, params);

        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verify(userService, times(1)).findUserNameByChannel(channel);
        verify(topicRepository, times(1)).removeVote(topicName, voteName);
        verify(messageService, times(1))
                .sendMessageByKey(ctx, "command.delete.success", voteName);

        verifyNoMoreInteractions(topicRepository, userService, voteValidator, messageService, topic, vote);
    }
}