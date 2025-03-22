package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.UserService;
import ru.andreyszdlv.util.ParamUtil;
import ru.andreyszdlv.validator.TopicVoteValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final TopicVoteValidator topicVoteValidator;
    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing delete command with params: {}", (Object) paramsCommand);

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParamUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        if (!topicRepository.containsVoteByTopicNameAndVoteName(topicName, voteName)) {
            log.warn("Topic \"{}\" or vote \"{}\" not found", topicName, voteName);
            messageService.sendMessageByKey(ctx, "error.topic_vote.not_found", topicName, voteName);
            return;
        }

        String userName = userService.getUserNameByChannel(ctx.channel());

        if (!topicVoteValidator.isUserAuthorOfVote(topicName, voteName, userName)) {
            log.warn("User \"{}\" not author vote \"{}\", deletion denied", userName, voteName);
            messageService.sendMessageByKey(ctx, "error.vote.no_delete", voteName);
            return;
        }

        topicRepository.removeVote(topicName, voteName);
        log.info("Vote \"{}\" successfully deleted from topic \"{}\"", voteName, topicName);
        messageService.sendMessageByKey(ctx, "command.delete.success", voteName);
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.DELETE;
    }
}