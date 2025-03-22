package ru.andreyszdlv.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.repo.TopicRepository;

@Component
@RequiredArgsConstructor
public class TopicVoteValidator {

    private final TopicRepository topicRepository;

    public boolean isUserAuthorOfVote(String topicName, String voteName, String userName) {
        return userName.equals(
                topicRepository.findTopicByName(topicName).get()
                        .getVoteByName(voteName).get()
                        .getAuthorName()
        );
    }
}
