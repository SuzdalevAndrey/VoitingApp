package ru.andreyszdlv.service.command.user;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserCommandTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ChannelHandlerContext ctx;

    @InjectMocks
    DeleteUserCommand deleteUserCommand;

    @Test
    void execute_SendErrorMessage_WhenCountParamInvalid() {
        String[] params = new String[]{"-t=TopicName -v=VoteName -thirdParam=invalid"};

        deleteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.delete.invalid"));
        verifyNoInteractions(topicRepository, userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenParamInvalid() {
        String[] params = new String[]{"-invalid=TopicName", "-v=VoteName"};

        deleteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.command.delete.invalid"));
        verifyNoInteractions(topicRepository, userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicOrVoteNameEmpty() {
        String[] params = new String[]{"-t=  ", "-v= "};

        deleteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.vote.name.empty"));
        verifyNoInteractions(topicRepository, userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenTopicNotFound() {
        String topicName = "TopicName";
        String[] params = new String[]{"-t=" + topicName, "-v=VoteName"};
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.empty());

        deleteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verifyNoMoreInteractions(topicRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenVoteNotFound() {
        String topicName = "topicName";
        String voteName = "voteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Topic topic = mock(Topic.class);
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.empty());

        deleteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.not_found", voteName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verifyNoMoreInteractions(topicRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenUserNoCreateVote() {
        String topicName = "topicName";
        String voteName = "voteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Channel channel = mock(Channel.class);
        ChannelId channelId = mock(ChannelId.class);
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);
        when(ctx.channel()).thenReturn(channel);
        when(channel.id()).thenReturn(channelId);
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));
        when(vote.getAuthorName()).thenReturn("AuthorName");
        when(channelId.asLongText()).thenReturn("channelId");
        when(userRepository.findUserByChannelId("channelId")).thenReturn("CurrentUser");

        deleteUserCommand.execute(ctx, params);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.no_delete", voteName));
        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verify(vote, times(1)).getAuthorName();
        verify(userRepository, times(1)).findUserByChannelId("channelId");
        verifyNoMoreInteractions(topicRepository, userRepository);
    }

    @Test
    void execute_DeleteVoteAndSendSuccessMessage_WhenUserCreateVote() {
        String topicName = "topicName";
        String voteName = "voteName";
        String[] params = new String[]{"-t=" + topicName, "-v=" + voteName};
        Channel channel = mock(Channel.class);
        ChannelId channelId = mock(ChannelId.class);
        Topic topic = mock(Topic.class);
        Vote vote = mock(Vote.class);
        when(ctx.channel()).thenReturn(channel);
        when(channel.id()).thenReturn(channelId);
        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.of(topic));
        when(topic.getVoteByName(voteName)).thenReturn(Optional.of(vote));
        when(vote.getAuthorName()).thenReturn("CurrentUser");
        when(channelId.asLongText()).thenReturn("channelId");
        when(userRepository.findUserByChannelId("channelId")).thenReturn("CurrentUser");

        deleteUserCommand.execute(ctx, params);

        verify(topicRepository, times(1)).findTopicByName(topicName);
        verify(topic, times(1)).getVoteByName(voteName);
        verify(vote, times(1)).getAuthorName();
        verify(userRepository, times(1)).findUserByChannelId("channelId");
        verify(topicRepository, times(1)).removeVote(topicName, voteName);
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("command.delete.success", voteName));

        verifyNoMoreInteractions(topicRepository, userRepository);
    }
}