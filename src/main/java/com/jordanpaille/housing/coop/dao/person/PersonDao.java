package com.jordanpaille.housing.coop.dao.person;

import com.jordanpaille.housing.coop.entity.PersonName;

import java.util.Optional;

public interface PersonDao {

    Optional<PersonName> findMemberNameByUsername(String username);
}
