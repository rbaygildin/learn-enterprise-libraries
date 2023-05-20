package org.demo.bank.client.dao;

import org.checkerframework.checker.units.qual.C;
import org.demo.bank.client.domain.Client;
import org.demo.bank.common.Singletone;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roman Baygildin (RVBaygildin@sberbank.ru)
 */
public class ClientService {

    private static ClientService instance;

    private ConcurrentHashMap<UUID, Client> db = new ConcurrentHashMap<>();

    static {
        if (instance == null) {
            synchronized (ClientService.class) {
                if (instance == null) {
                    instance = new ClientService();
                }
            }
        }
    }

    private ClientService() {

    }

    public UUID create(Client client) {
        var id = UUID.randomUUID();
        client.setId(id);
        db.put(id, client);
        return id;
    }

    public List<Client> findAll() {
        return new ArrayList<>(db.values());
    }

    public static ClientService getInstance() {
        return instance;
    }
}
