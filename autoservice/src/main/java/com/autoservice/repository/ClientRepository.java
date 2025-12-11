package com.autoservice.repository;

import com.autoservice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByOrderByIdAsc();

    public boolean existsById(Integer id);

    public void deleteById(Integer id);

    public Optional<Client> findById(Integer id);
}
