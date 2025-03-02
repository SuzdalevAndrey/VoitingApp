package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtil;

@Slf4j
@RequiredArgsConstructor
public class ViewUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing view command with parameters: {}", (Object) paramsCommand);

        switch(paramsCommand.length){
            case 0 -> handleNoParams(ctx);
            case 1 -> handleSingleParam(ctx, paramsCommand);
            case 2 -> handleTwoParams(ctx, paramsCommand);
            default -> {
                log.warn("Invalid number of parameters for view command");
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.invalid_command"));
            }
        }
    }

    private void handleNoParams(ChannelHandlerContext ctx){
        log.info("Fetching all topics");
        topicRepository.findAll().forEach(
                (name, topic) -> ctx.writeAndFlush(String.format(
                        "\"%s\" (votes in topic=%s)\n",
                        name,
                        topic.countVotes())
                )
        );
    }

    private void handleSingleParam(ChannelHandlerContext ctx, String[] paramsCommand){

        String topicName = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-t=");

        if(topicName == null){
            log.warn("View command options invalid. Expected: -t=TopicName. Received {}.",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.command.view.single_param.invalid"));
            return;
        }

        if(topicName.isBlank()){
            log.warn("Topic name is empty");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
            return;
        }

        log.info("Fetching topic: {}", topicName);
        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> {
                            log.info("Topic found: \"{}\"", topicName);
                            ctx.writeAndFlush(topic.toString());
                        },
                        () -> {
                            log.warn("Topic not found: \"{}\"", topicName);
                            ctx.writeAndFlush(MessageProviderUtil
                                    .getMessage("error.topic.not_found", topicName));
                        }
                );
    }

    private void handleTwoParams(ChannelHandlerContext ctx, String[] paramsCommand){

        String topicName = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null){
            log.warn("View command options invalid. Expected: -t=TopicName -v=VoteName. Received {}.",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.view.two_params.invalid"));
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()){
            log.warn("Topic name or vote name is empty");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.vote.name.empty"));
            return;
        }

        log.info("Fetching topic \"{}\" find vote \"{}\"", topicName, voteName);
        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> topic.getVoteByName(voteName)
                                .ifPresentOrElse(
                                        vote -> {
                                            log.info("Vote found: \"{}\"", voteName);
                                            ctx.writeAndFlush(vote.toString());
                                        },
                                        () -> {
                                            log.warn("Vote not found: \"{}\"", voteName);
                                            ctx.writeAndFlush(MessageProviderUtil
                                                    .getMessage("error.vote.not_found", voteName));
                                        }
                                ),
                        () -> {
                            log.warn("Topic not found: \"{}\"", topicName);
                            ctx.writeAndFlush(MessageProviderUtil
                                    .getMessage("error.topic.not_found", topicName));
                        }
                );
    }
}