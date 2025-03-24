package ru.andreyszdlv.props;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource("classpath:application.properties")
public class ClientProperties {

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int port;
}