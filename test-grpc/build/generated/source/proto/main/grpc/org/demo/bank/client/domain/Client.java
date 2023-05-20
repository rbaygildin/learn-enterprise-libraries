package org.demo.bank.client.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Roman Baygildin (RVBaygildin@sberbank.ru)
 */
@Data
public class Client {
    private UUID id;
    private String name;
    private String surname;
    private Gender gender;
    private int age;
    private String email;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
