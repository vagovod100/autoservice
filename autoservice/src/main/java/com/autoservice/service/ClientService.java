package com.autoservice.service;

import com.autoservice.entity.Client;
import com.autoservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> getAllClients() {
        // Берём всех и сортируем по id сами
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Client::getId))
                .collect(Collectors.toList());
    }

    public Client createClient(Client client) {
        return repository.save(client);
    }

    public boolean deleteClientById(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Client> getClientById(Integer id) {
        return repository.findById(id);
    }

    public List<Client> searchByName(String query) {
        String q = query == null ? "" : query.toLowerCase();

        return repository.findAll().stream()
                .filter(c -> c.getFullName() != null &&
                        c.getFullName().toLowerCase().contains(q))
                .sorted(Comparator.comparing(Client::getId))
                .collect(Collectors.toList());
    }

    public List<Client> searchByPhone(String query) {
        String q = query == null ? "" : query.toLowerCase();

        return repository.findAll().stream()
                .filter(c -> c.getPhone() != null &&
                        c.getPhone().toLowerCase().contains(q))
                .sorted(Comparator.comparing(Client::getId))
                .collect(Collectors.toList());
    }
}
