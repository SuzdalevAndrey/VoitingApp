package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteAnswerServiceTest {

    @Mock
    UserService userService;

    @Mock
    HandlerFactory handlerFactory;

    @Mock
    MessageService messageService;

    @Mock
    HandlerService handlerService;

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    Vote vote;

    @InjectMocks
    VoteAnswerService voteAnswerService;

    @BeforeEach
    void setUp() {
        voteAnswerService.setVote(vote);
    }

    @Test
    void processVoteAnswer_SendErrorMessage_WhenAnswerNotNumber() {
        String answer = "one";

        voteAnswerService.processVoteAnswer(ctx, answer);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.option.invalid");
        verifyNoInteractions(userService, ctx, handlerFactory, handlerService);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void processVoteAnswer_SendErrorMessage_WhenOptionNegative() {
        String answer = "-1";

        voteAnswerService.processVoteAnswer(ctx, answer);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.option.negative");
        verifyNoInteractions(userService, ctx, handlerFactory, handlerService);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void processVoteAnswer_SendErrorMessage_WhenOptionZero() {
        String answer = "0";

        voteAnswerService.processVoteAnswer(ctx, answer);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.option.negative");
        verifyNoInteractions(userService, ctx, handlerFactory, handlerService);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void processVoteAnswer_SendErrorMessage_WhenOptionExceedsOptionsSize() {
        String answer = "2";
        List<AnswerOption> options = List.of(mock(AnswerOption.class));

        when(vote.getAnswerOptions()).thenReturn(options);

        voteAnswerService.processVoteAnswer(ctx, answer);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.option.invalid");
        verify(vote, times(1)).getAnswerOptions();
        verifyNoInteractions(userService, ctx, handlerFactory, handlerService);
        verifyNoMoreInteractions(messageService, vote);
    }

    @Test
    void processVote_SendErrorMessage_WhenAlreadyVoted() {
        String answer = "1";
        String userName = "user";
        Channel channel = mock(Channel.class);
        AnswerOption voteOption = mock(AnswerOption.class);
        List<AnswerOption> options = List.of(voteOption);
        Set<String> votedUsers = new HashSet<>();
        votedUsers.add(userName);

        when(ctx.channel()).thenReturn(channel);
        when(userService.findUserNameByChannel(channel)).thenReturn(userName);
        when(vote.getAnswerOptions()).thenReturn(options);
        when(voteOption.getVotedUsers()).thenReturn(votedUsers);

        voteAnswerService.processVoteAnswer(ctx, answer);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.option.already_choose");
        verify(userService, times(1)).findUserNameByChannel(channel);
        verify(vote, times(1)).getAnswerOptions();
        verify(voteOption, times(1)).getVotedUsers();
        verifyNoMoreInteractions(userService, vote, voteOption, messageService, ctx);
        verifyNoInteractions(handlerFactory, handlerService);
    }

    @Test
    void processVoteAnswer_VotedUsersAndSendSuccess_WhenValidInput() {
        String answer = "1";
        String userName = "user";
        Channel channel = mock(Channel.class);
        AnswerOption voteOption = mock(AnswerOption.class);
        List<AnswerOption> options = List.of(voteOption);
        Set<String> votedUsers = new HashSet<>();
        CommandHandler commandHandler = mock(CommandHandler.class);

        when(ctx.channel()).thenReturn(channel);
        when(vote.getAnswerOptions()).thenReturn(options);
        when(userService.findUserNameByChannel(channel)).thenReturn(userName);
        when(voteOption.getVotedUsers()).thenReturn(votedUsers);
        when(handlerFactory.createCommandHandler()).thenReturn(commandHandler);

        voteAnswerService.processVoteAnswer(ctx, answer);

        verify(voteOption, times(1)).getVotedUsers();
        verify(userService, times(1)).findUserNameByChannel(channel);
        verify(vote, times(1)).getAnswerOptions();
        verify(messageService, times(1))
                .sendMessageByKey(ctx, "vote.success", 1);
        verify(handlerService, times(1)).switchHandler(ctx, commandHandler);
        verify(handlerFactory, times(1)).createCommandHandler();
        assertTrue(votedUsers.contains(userName));
        verifyNoMoreInteractions(userService, vote, voteOption, messageService, handlerFactory, handlerService);
    }
}