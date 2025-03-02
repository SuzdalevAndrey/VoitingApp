package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtil;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DeleteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing delete command with params: {}", (Object) paramsCommand);

        if(paramsCommand.length != 2) {
            log.warn("Invalid number of parameters for delete command");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.delete.invalid"));
            return;
        }

        String topicName = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            log.warn("Delete command options invalid. Expected: -t=TopicName -v=VoteName. Received: {}",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.delete.invalid"));
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()) {
            log.warn("Topic name or vote name is empty");
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.topic.name.vote.name.empty"));
            return;
        }

        Optional<Topic> topic = topicRepository.findTopicByName(topicName);

        if(topic.isEmpty()) {
            log.warn("Topic \"{}\" not found", topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
            return;
        }

        Optional<Vote> vote = topic.get().getVoteByName(voteName);

        if(vote.isEmpty()) {
            log.warn("Vote \"{}\" not found in topic '{}'", voteName, topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.not_found", voteName));
            return;
        }

        String userName = userRepository.findUserByChannelId(ctx.channel().id().asLongText());

        if(!vote.get().getAuthorName().equals(userName)){
            log.warn("User \"{}\" not author vote \"{}\", deletion denied", userName, voteName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.no_delete", voteName));
            return;
        }

        topicRepository.removeVote(topicName, voteName);
        log.info("Vote \"{}\" successfully deleted from topic \"{}\"", voteName, topicName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.delete.success", voteName));
    }
}