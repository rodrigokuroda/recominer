package br.edu.utfpr.recominer.core.repository;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
public interface ReadOnlyRepository<T extends Persistable<ID>, ID extends Serializable> extends Repository<T, ID> {

    long count();

    boolean exists(ID id);

    List<T> findAll();

    Iterable<T> findAll(Iterable<ID> iterable);
    
    List<T> findAll(Sort sort);

    Page<T> findAll(Pageable page);

    T findOne(ID id);
    
}
