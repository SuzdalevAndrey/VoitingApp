package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteAnswerServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    Vote vote;

    @InjectMocks
    VoteAnswerService voteAnswerService;

    @Test
    void answer_SendErrorMessage_WhenAnswerNotNumber() {
        String answer = "one";

        voteAnswerService.answer(ctx, answer);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void answer_SendErrorMessage_WhenOptionNegative() {
        String answer = "-1";

        voteAnswerService.answer(ctx, answer);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.negative"));
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void answer_SendErrorMessage_WhenOptionZero() {
        String answer = "0";

        voteAnswerService.answer(ctx, answer);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.negative"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void answer_SendErrorMessage_WhenOptionExceedsAnswerOptionsSize() {
        String answer = "2";
        List<AnswerOption> options = List.of(mock(AnswerOption.class));
        when(vote.getAnswerOptions()).thenReturn(options);

        voteAnswerService.answer(ctx, answer);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
        verify(vote, times(1)).getAnswerOptions();
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(ctx, vote);
    }

    @Test
    void answer_SendErrorMessage_WhenUserAlreadyVoted() {
        String answer = "1";
        String userName = "user";
        Channel channel = mock(Channel.class);
        ChannelId channelId = mock(ChannelId.class);
        AnswerOption voteOption = mock(AnswerOption.class);
        List<AnswerOption> options = List.of(voteOption);
        Set<String> votedUsers = new HashSet<>();
        votedUsers.add(userName);
        when(ctx.channel()).thenReturn(channel);
        when(channel.id()).thenReturn(channelId);
        when(ctx.channel().id().asLongText()).thenReturn("channelId");
        when(vote.getAnswerOptions()).thenReturn(options);
        when(userRepository.findUserByChannelId("channelId")).thenReturn(userName);
        when(voteOption.getVotedUsers()).thenReturn(votedUsers);

        voteAnswerService.answer(ctx, answer);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.already_choose"));
        verify(userRepository, times(1)).findUserByChannelId("channelId");
        verify(vote, times(1)).getAnswerOptions();
        verify(voteOption, times(1)).getVotedUsers();
        verifyNoMoreInteractions(userRepository, vote, voteOption, ctx);
    }

    @Test
    void answer_AddUserToVotedUsersAndSendSuccess_WhenValidInput() {
        String answer = "1";
        String userName = "user";
        Channel channel = mock(Channel.class);
        ChannelId channelId = mock(ChannelId.class);
        AnswerOption voteOption = mock(AnswerOption.class);
        List<AnswerOption> options = List.of(voteOption);
        Set<String> votedUsers = new HashSet<>();
        ChannelPipeline pipeline = mock(ChannelPipeline.class);
        when(ctx.channel()).thenReturn(channel);
        when(channel.id()).thenReturn(channelId);
        when(ctx.channel().id().asLongText()).thenReturn("channelId");
        when(vote.getAnswerOptions()).thenReturn(options);
        when(userRepository.findUserByChannelId("channelId")).thenReturn(userName);
        when(voteOption.getVotedUsers()).thenReturn(votedUsers);
        when(ctx.pipeline()).thenReturn(pipeline);

        voteAnswerService.answer(ctx, answer);

        verify(voteOption, times(1)).getVotedUsers();
        verify(userRepository, times(1)).findUserByChannelId("channelId");
        verify(vote, times(1)).getAnswerOptions();
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("vote.success", 1));
        verify(pipeline, times(1)).removeLast();
        verify(pipeline, times(1)).addLast(any(CommandHandler.class));
        assertTrue(votedUsers.contains(userName));
    }
}