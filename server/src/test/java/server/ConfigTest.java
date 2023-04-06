package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import server.database.*;
import server.services.RepositoryBasedAuthService;
import server.services.SocketRefreshService;
import server.services.TestAuthService;
import server.services.TestSocketRefresher;

@TestConfiguration
public class ConfigTest {
    @Autowired
    private ApplicationContext context;

    @Bean
    @Primary
    public SocketRefreshService socketRefreshServiceMock() {
        return new TestSocketRefresher();
    }

    @Bean
    public TestBoardsRepository testBoardsRepository() {
        return (TestBoardsRepository) context.getBean(BoardRepository.class);
    }

    @Bean
    public TestUserRepository testUserRepository() {
        return (TestUserRepository) context.getBean(UserRepository.class);
    }

    @Bean
    public TestCardRepository testCardRepository() {
        return (TestCardRepository) context.getBean(CardRepository.class);
    }

    @Bean
    public TestCardListRepository testCardListRepository() {
        return (TestCardListRepository) context.getBean(CardListRepository.class);
    }

    @Bean
    public TestTaskRepository testTaskRepository() {
        return (TestTaskRepository) context.getBean(TaskRepository.class);
    }

    @Bean
    public TestTagRepository testTagRepository() {
        return (TestTagRepository) context.getBean(TagRepository.class);
    }

    @Bean
    public TestColorPresetRepository testColorPresetRepository() {
        return (TestColorPresetRepository) context.getBean(ColorPresetRepository.class);
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UserRepository userRepositoryMock() {
        return new TestUserRepository();
    }

    @Bean
    @Primary
    public RepositoryBasedAuthService repositoryBasedAuthServiceMock() {
        return new TestAuthService();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BoardRepository boardRepositoryMock() {
        return new TestBoardsRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CardRepository cardRepositoryMock() {
        return new TestCardRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CardListRepository cardListRepositoryMock() {
        return new TestCardListRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskRepository taskRepositoryMock() {
        return new TestTaskRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TagRepository tagRepositoryMock() {
        return new TestTagRepository();
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ColorPresetRepository colorPresetRepositoryMock() {
        return new TestColorPresetRepository();
    }
}
