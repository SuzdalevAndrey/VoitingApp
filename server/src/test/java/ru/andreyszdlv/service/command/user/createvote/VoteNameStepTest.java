package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.MessageService;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteNameStepTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    MessageService messageService;

    @Mock
    VoteCreationService voteCreationService;

    @Mock
    ChannelHandlerContext ctx;

    final String topicName = "TopicName";

    @InjectMocks
    VoteNameStep voteNameStep;

    @BeforeEach
    void setUp() {
        voteNameStep.setTopicName(topicName);
    }

    @Test
    void execute_SendErrorMessageAndDoNotProceed_WhenVoteNameIsBlank() {
        String voteName = "    ";

        voteNameStep.execute(ctx, voteName, voteCreationService);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.name.empty");
        verifyNoMoreInteractions(messageService);
        verifyNoInteractions(voteCreationService, ctx, topicRepository);
    }

    @Test
    void execute_SendErrorMessage_WhenVoteAlreadyExists() {
        String voteName = "VoteName";
        Topic topic = mock(Topic.class);

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.ofNullable(topic));
        when(topic.containsVoteByName(voteName)).thenReturn(true);

        voteNameStep.execute(ctx, voteName, voteCreationService);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.already_exist", voteName);
        verifyNoInteractions(voteCreationService, ctx);
        verifyNoMoreInteractions(messageService, topicRepository);
    }

    @Test
    void execute_SetVoteNameAndProceedToNextStep_WhenVoteNameValid() {
        String voteName = "newVote";
        Topic topic = mock(Topic.class);

        when(topicRepository.findTopicByName(topicName)).thenReturn(Optional.ofNullable(topic));
        when(topic.containsVoteByName(voteName)).thenReturn(false);

        voteNameStep.execute(ctx, voteName, voteCreationService);

        verify(voteCreationService, times(1)).setVoteName(voteName);
        verify(voteCreationService, times(1)).nextStep();
        verify(messageService, times(1))
                .sendMessageByKey(ctx, "vote.description");
        verifyNoMoreInteractions(voteCreationService, messageService, topicRepository);
        verifyNoInteractions(ctx);
    }
}