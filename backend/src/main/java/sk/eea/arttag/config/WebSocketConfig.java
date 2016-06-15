package sk.eea.arttag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import sk.eea.arttag.controller.WebSocketGameController;

@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameController(), "/ws/game");
    }

    @Bean
    public WebSocketHandler gameController() {
        return new WebSocketGameController();
    }

    @Scheduled(fixedRate=1000)
    public void trigger() {
        WebSocketGameController.JOB.run();
    }

}
