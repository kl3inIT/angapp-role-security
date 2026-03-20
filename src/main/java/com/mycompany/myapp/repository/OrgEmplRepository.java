package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OrgEmpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgEmplRepository extends JpaRepository<OrgEmpl, Long> {}
