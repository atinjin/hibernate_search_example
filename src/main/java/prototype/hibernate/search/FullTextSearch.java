package prototype.hibernate.search;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FullTextSearch {

    private final EntityManager entityManager;
    private final BookRepository bookRepository;

    @Autowired
    public FullTextSearch(EntityManager entityManager, BookRepository bookRepository) {
        this.entityManager = entityManager;
        this.bookRepository = bookRepository;
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

    public List search(String query) {

        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
//        entityManager.getTransaction().begin();

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

//        entityManager.getTransaction().commit();
//        entityManager.close();

        return result;
    }


    enum subtitles {
        asdfas, dfere,sdfsd,fsdfs,werw,fghhh,uy5yr,cvx,sdfa,erwe,zvc,qwerq,sdfsfd,werwq,sfasdf,erqwe,sdfasd,zvzxcv,h,rtyru,rtye;
    }

    @Component
    public class test implements CommandLineRunner {

        private FullTextSearch fullTextSearch;
        private BookRepository bookRepository;
        private AuthorRepository authorRepository;

        @Autowired
        public test(FullTextSearch fullTextSearch, BookRepository bookRepository, AuthorRepository authorRepository) {
            this.fullTextSearch = fullTextSearch;
            this.bookRepository = bookRepository;
            this.authorRepository = authorRepository;
        }

        @Override
        public void run(String... args) throws Exception {
            fullTextSearch.startFullTestSearch();

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

        }
    }
}
