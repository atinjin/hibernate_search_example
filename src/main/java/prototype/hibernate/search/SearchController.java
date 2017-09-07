package prototype.hibernate.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private FullTextSearch fullTextSearch;

    @Autowired
    public SearchController(FullTextSearch fullTextSearch) {
        this.fullTextSearch = fullTextSearch;
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public List<Book> getBooks(String query) {



        return null;
    }
}
