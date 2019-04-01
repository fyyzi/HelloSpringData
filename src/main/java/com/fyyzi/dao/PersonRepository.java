package com.fyyzi.dao;

import com.fyyzi.entity.base.animal.primate.People;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * people
 *
 * @author 羽化荼
 */
@RepositoryDefinition(domainClass = People.class, idClass = Integer.class)
public interface PersonRepository {
}
