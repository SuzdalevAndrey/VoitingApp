package ru.andreyszdlv.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.model.Vote;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteValidatorTest {

    @Mock
    Vote vote;

    VoteValidator voteValidator;

    @BeforeEach
    void setUp() {
        voteValidator = new VoteValidator();
    }

    @Test
    void isUserAuthorOfVote_ReturnTrue_WhenUserAuthor() {
        String userName = "userName";

        when(vote.getAuthorName()).thenReturn(userName);

        boolean response = voteValidator.isUserAuthorOfVote(vote, userName);

        assertTrue(response);
        verify(vote, times(1)).getAuthorName();
        verifyNoMoreInteractions(vote);
    }

    @Test
    void isUserAuthorOfVote_ReturnFalse_WhenUserNotAuthor() {
        String userName = "userName";
        String authorName = "authorName";

        when(vote.getAuthorName()).thenReturn(authorName);

        boolean response = voteValidator.isUserAuthorOfVote(vote, userName);

        assertFalse(response);
        verify(vote, times(1)).getAuthorName();
        verifyNoMoreInteractions(vote);
    }
}