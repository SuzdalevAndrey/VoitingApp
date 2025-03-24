package ru.andreyszdlv.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerServiceTest {

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    ChannelPipeline pipeline;

    @Mock
    SimpleChannelInboundHandler<String> handler;

    HandlerService handlerService;

    @BeforeEach
    void setUp() {
        handlerService = new HandlerService();
    }

    @Test
    void switchHandler_Success() {
        when(ctx.pipeline()).thenReturn(pipeline);

        handlerService.switchHandler(ctx, handler);

        verify(ctx, times(2)).pipeline();
        verify(pipeline, times(1)).removeLast();
        verify(pipeline, times(1)).addLast(handler);
        verifyNoMoreInteractions(ctx, pipeline);
    }
}