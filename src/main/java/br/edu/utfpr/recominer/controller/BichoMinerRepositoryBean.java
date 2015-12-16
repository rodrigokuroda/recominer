package br.edu.utfpr.recominer.controller;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class BichoMinerRepositoryBean implements Serializable {

    @EJB
    private GenericBichoDAO dao;

    public List<String> getAllRepositories() {
        // TODO refactor
        return new BichoDAO(dao, "", 20).listAllProjects();
    }

}
