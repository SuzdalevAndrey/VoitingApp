package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class VoteDescriptionHandler extends SimpleChannelInboundHandler<String> {

    private final String nameTopic;
    private String nameVote;
    private String description;
    private int numberOfOptions;
    private final List<AnswerOption> options = new ArrayList<>();
    private int step = 1;
    private final TopicRepository topicRepository = new TopicRepository();
    private final UserRepository userRepository = new UserRepository();

    public VoteDescriptionHandler(String nameTopic) {
        this.nameTopic = nameTopic;
    }
    //todo доделать

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {

        switch (step) {
            case 1:
                nameVote = message;
                step = 2;

                ctx.writeAndFlush("Введите тему голосования:\n");
                break;
            case 2:
                description = message;
                step = 3;

                ctx.writeAndFlush("Введите количество вариантов ответа:\n");
                break;

            case 3:
                try {
                    numberOfOptions = Integer.parseInt(message);
                    if (numberOfOptions <= 0) {
                        ctx.writeAndFlush("Количество вариантов должно быть положительным числом.\n");
                        return;
                    }

                    step = 4;
                    ctx.writeAndFlush("Введите вариант ответа #1:\n");

                } catch (NumberFormatException e) {
                    ctx.writeAndFlush("Пожалуйста, введите правильное число вариантов.\n");
                }
                break;

            case 4:
                options.add(new AnswerOption(message));

                if (options.size() < numberOfOptions) {
                    ctx.writeAndFlush("Введите вариант ответа #" + (options.size() + 1) + ":\n");
                } else {
                    Vote vote = new Vote(
                            nameVote,
                            description,
                            userRepository.getUsername(ctx.channel()),
                            options
                    );
                    topicRepository.addVote(nameTopic, vote);

                    ctx.writeAndFlush("Голосование \"" + vote.getName() + "\" успешно создано в топике \"" + nameTopic + "\".\n");

                    ctx.pipeline().remove(this);

                    ctx.pipeline().addLast(new CommandHandler());
                }
                break;

            default:
                ctx.writeAndFlush("Ошибка: неизвестный шаг.\n");
                break;
        }
    }
}