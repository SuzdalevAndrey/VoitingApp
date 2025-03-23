package ru.andreyszdlv.props;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@PropertySource("classpath:application.properties")
public class ServerProperties {

    @Value("${server.port}")
    private int port;
}