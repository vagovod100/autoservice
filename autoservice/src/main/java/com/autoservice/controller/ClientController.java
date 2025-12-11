package com.autoservice.controller;

import com.autoservice.entity.Client;
import com.autoservice.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public List<Client> getAll() {
        return service.getAllClients();
    }

    @PostMapping
    public Client create(@RequestBody Client client) {
        return service.createClient(client);
    }
}