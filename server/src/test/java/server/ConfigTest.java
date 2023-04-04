package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import server.database.*;
import server.services.SocketRefreshService;
import server.services.TestSocketRefresher;

import static org.springframework.util.ObjectUtils.isEmpty;

@TestConfiguration
public class ConfigTest {
    @Bean
    @Primary
    public SocketRefreshService socketRefreshService() {
        return new TestSocketRefresher();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserRepository userRepository() {
        return new TestUserRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BoardRepository boardRepository() {
        return new TestBoardsRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CardRepository cardRepository() {
        return new TestCardRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CardListRepository cardListRepository() {
        return new TestCardListRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskRepository taskRepository() {
        return new TestTaskRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TagRepository tagRepository() {
        return new TestTagRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ColorPresetRepository colorPresetRepository() {
        return new TestColorPresetRepository();
    }
}
