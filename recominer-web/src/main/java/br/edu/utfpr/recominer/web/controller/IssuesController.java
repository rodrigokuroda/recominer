package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Issue;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuesController {

//    @Inject
//    private IssuesRepository repository;

    public IssuesController() {
    }

    @RequestMapping(value = "/issues", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Issue> listIssues(@RequestBody Integer id) {
//        return repository.findAll();
        return Arrays.asList(new Issue(1, "Bug", "AVRO-1", new Date(), new Date(), new Date()));
    }
}
