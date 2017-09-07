package prototype.hibernate.search;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Component
public class FullTextSearch {

    private EntityManager entityManager;

    @Autowired
    public FullTextSearch(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PostConstruct
    public void start() {

    }

    @Transactional
    public void startFullTestSearch() {
        try {
            FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
            fullTextSession.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Component
    public class test implements CommandLineRunner {

        private FullTextSearch fullTextSearch;

        @Autowired
        public test(FullTextSearch fullTextSearch) {
            this.fullTextSearch = fullTextSearch;
        }

        @Override
        public void run(String... args) throws Exception {
            fullTextSearch.startFullTestSearch();
        }
    }
}
