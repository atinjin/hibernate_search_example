package prototype.hibernate.search;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class FullTextSearch {

    private final EntityManager entityManager;

    @Autowired
    public FullTextSearch(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PostConstruct
    public void start() {
        reindexing();
    }

    public List searchLog(String query) {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Message.class).get();
        org.apache.lucene.search.Query luceneQuery = qb
                .keyword().wildcard()
                .onFields("sourceName", "sourceIpAddress", "sourcePName"
                        , "sourceGroupName", "userId", "userKeyword", "userDescription")
                .matching(query)
                .createQuery();
        javax.persistence.Query jpaQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, Message.class);

        // execute search
        return jpaQuery.getResultList();
    }

    public List search(String query) {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        // create native Lucene query unsing the query DSL
        // alternatively you can write the Lucene query using the Lucene query parser
        // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Book.class).get();
        org.apache.lucene.search.Query luceneQuery = qb
                .keyword().wildcard()
                .onFields("title", "subtitle", "authors.name")
                .matching(query)
                .createQuery();

        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query jpaQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, Book.class);

        // execute search
        List result = jpaQuery.getResultList();

        return result;
    }

    public void reindexing() {
        long now = System.currentTimeMillis();
        System.out.println("Start indexing at "+ now);
        try {
            FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
            fullTextSession.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("End indexing during "+ (System.currentTimeMillis() - now) +"ms");
    }


    enum subtitles {
        asdfas, dfere,sdfsd,fsdfs,werw,fghhh,uy5yr,cvx,sdfa,erwe,zvc,qwerq,sdfsfd,werwq,sfasdf,erqwe,sdfasd,zvzxcv,h,rtyru,rtye;
    }

    @Component
    public class test implements CommandLineRunner {

        private final BookRepository bookRepository;
        private final AuthorRepository authorRepository;
        private final LogRepository logRepository;

        @Autowired
        public test(BookRepository bookRepository, AuthorRepository authorRepository, LogRepository logRepository) {
            this.bookRepository = bookRepository;
            this.authorRepository = authorRepository;
            this.logRepository = logRepository;
        }

        @Override
        public void run(String... args) throws Exception {
            for (int i = 0; i < 200; i++) {
                Set authors = new HashSet<Author>(){};
                Author a = new Author();
                a.setName("Ryan");
                a = authorRepository.save(a);

                authors.add(a);
                Book book = new Book();
                book.setTitle("Book for you " + i);
                book.setSubtitle(subtitles.values()[i % subtitles.values().length].toString());
                book.setPublicationDate(new Date());
                book.setAuthors(authors);

                bookRepository.save(book);
            }

            IntStream.range(0, 100).forEach(i -> {
                Message log = getRandomEvent();
                logRepository.save(log);
            });
        }

        private Message getRandomEvent() {
            Message log = new Message();

            log.setGuid(UUID.randomUUID().toString());

            //Log type
            log.setLogType(4001);
            log.setLogSubType(0);
            log.setLogCategory(4000);
            log.setLogExplanation("Motion detection");
            log.setEventContext(Message.EventContext.occur);
            log.setStatus(1);

            //Time
            log.setOccurrenceTime(System.currentTimeMillis());
            log.setDetectionTime(System.currentTimeMillis());
            log.setDetectionDateTime(new Date(log.getDetectionTime()));
            log.setConfirmTime(0);
            log.setConfirmDateTime(new Date(log.getConfirmTime()));

            //Channel
            log.setSourceUid(UUID.randomUUID().toString());
            log.setSourceName(UUID.randomUUID().toString());
            log.setSourcePid(UUID.randomUUID().toString());
            log.setSourcePName(UUID.randomUUID().toString());
            log.setSourceParentSystemUuid(UUID.randomUUID().toString());
            log.setSourceSystemUuid(UUID.randomUUID().toString());
            log.setSourceTypeName(UUID.randomUUID().toString());
            log.setSourceIpAddress(UUID.randomUUID().toString());

            //User
            log.setUserUid(UUID.randomUUID().toString());
            log.setUserId(UUID.randomUUID().toString());
            log.setUserGroupUid(UUID.randomUUID().toString());
            log.setUserKeyword(UUID.randomUUID().toString());
            log.setUserDescription(UUID.randomUUID().toString());

            log.getRelatedEvent().add(UUID.randomUUID().toString());

            return log;
        }
    }
}
