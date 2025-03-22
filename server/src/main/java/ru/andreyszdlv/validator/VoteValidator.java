package ru.andreyszdlv.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.model.Vote;

@Component
@RequiredArgsConstructor
public class VoteValidator {

    public boolean isUserAuthorOfVote(Vote vote, String userName) {
        return userName.equals(vote.getAuthorName());
    }
}
