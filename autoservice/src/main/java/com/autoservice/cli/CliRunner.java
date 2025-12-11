package com.autoservice.cli;

import com.autoservice.entity.Client;
import com.autoservice.entity.Order;
import com.autoservice.service.ClientService;
import com.autoservice.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class CliRunner implements CommandLineRunner {

    private final ClientService clientService;
    private final OrderService orderService;

    private final Scanner scanner = new Scanner(System.in);

    private enum WatchedTable {
        CLIENTS,
        ORDERS
    }

    private final EnumSet<WatchedTable> watchedTables = EnumSet.of(WatchedTable.CLIENTS);

    public CliRunner(ClientService clientService, OrderService orderService) {
        this.clientService = clientService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Autoservice CLI (Spring Boot) ===");
        System.out.println("Ништяк, прога запущена. Работай, родной.\n");

        boolean running = true;
        while (running) {
            printWatchedTables();
            printMainMenu();
            String choice = readLine(">>> ");

            switch (choice) {
                case "1" -> manageClients();
                case "2" -> manageOrders();
                case "3" -> configureWatchedTables();
                case "0" -> {
                    System.out.println("Выход. До связи 👋");
                    running = false;
                }
                default -> System.out.println("Не понял выбор, попробуй ещё.");
            }
        }
    }

    // ===== Основное меню =====

    private void printMainMenu() {
        System.out.println();
        System.out.println("Главное меню:");
        System.out.println("  1) Клиенты");
        System.out.println("  2) Заказы (простая форма)");
        System.out.println("  3) Настроить отображаемые таблицы");
        System.out.println("  0) Выход");
    }

    // ===== Отображение таблиц =====

    private void printWatchedTables() {
        System.out.println();
        System.out.println("=== Текущие таблицы ===");
        if (watchedTables.contains(WatchedTable.CLIENTS)) {
            printClientsTable();
        }
        if (watchedTables.contains(WatchedTable.ORDERS)) {
            printOrdersTable();
        }
        System.out.println("=======================");
    }

    private void printClientsTable() {
        List<Client> clients = clientService.getAllClients();
        System.out.println("Таблица: clients");
        if (clients.isEmpty()) {
            System.out.println("  (пока пусто)");
            return;
        }
        System.out.printf("  %-4s | %-25s | %-15s | %-25s | %s%n",
                "ID", "ФИО", "Телефон", "Email", "Заметки");
        System.out.println("  " + "-".repeat(80));
        for (Client c : clients) {
            System.out.printf("  %-4d | %-25s | %-15s | %-25s | %s%n",
                    c.getId(),
                    safe(c.getFullName()),
                    safe(c.getPhone()),
                    safe(c.getEmail()),
                    safe(c.getNotes()));
        }
    }

    private void printOrdersTable() {
        List<Order> orders = orderService.getAllOrders();
        System.out.println("Таблица: orders");
        if (orders.isEmpty()) {
            System.out.println("  (пока пусто)");
            return;
        }
        System.out.printf("  %-4s | %-8s | %-10s | %-19s | %-10s%n",
                "ID", "clientId", "status", "createdAt", "totalCost");
        System.out.println("  " + "-".repeat(70));
        for (Order o : orders) {
            System.out.printf("  %-4d | %-8d | %-10s | %-19s | %-10s%n",
                    o.getId(),
                    o.getClientId(),
                    safe(o.getStatus()),
                    o.getCreatedAt(),
                    o.getTotalCost() == null ? "-" : o.getTotalCost().toPlainString());
        }
    }

    private String safe(String s) {
        return s == null ? "-" : s;
    }

    // ===== Настройка "наблюдаемых" таблиц =====

    private void configureWatchedTables() {
        System.out.println("\nОтображаемые таблицы сейчас: " + watchedTables);
        System.out.println("Выбери, что показать / спрятать:");
        System.out.println("  1) clients");
        System.out.println("  2) orders");
        System.out.println("  0) Назад");

        String choice = readLine(">>> ");
        switch (choice) {
            case "1" -> toggleTable(WatchedTable.CLIENTS);
            case "2" -> toggleTable(WatchedTable.ORDERS);
            case "0" -> {
                // ничего
            }
            default -> System.out.println("Не понял выбор.");
        }
    }

    private void toggleTable(WatchedTable table) {
        if (watchedTables.contains(table)) {
            watchedTables.remove(table);
            System.out.println("Теперь таблица " + table + " НЕ будет отображаться.");
        } else {
            watchedTables.add(table);
            System.out.println("Теперь таблица " + table + " будет отображаться.");
        }
    }

    // ===== Работа с клиентами =====

    private void manageClients() {
        boolean back = false;
        while (!back) {
            System.out.println("\nМеню клиентов:");
            System.out.println("  1) Добавить клиента");
            System.out.println("  2) Удалить клиента");
            System.out.println("  3) Поиск клиента");
            System.out.println("  0) Назад");

            String choice = readLine(">>> ");
            switch (choice) {
                case "1" -> addClientFlow();
                case "2" -> deleteClientFlow();
                case "3" -> searchClientFlow();
                case "0" -> back = true;
                default -> System.out.println("Не понял выбор.");
            }
        }
    }

    private void addClientFlow() {
        System.out.println("\n=== Добавление клиента ===");
        String fullName = readNonEmptyOrDash("ФИО (обязательно, '-' не принимается): ", false);
        String phone = readNonEmptyOrDash("Телефон (можно '-', если не знаешь): ", true);
        String email = readNonEmptyOrDash("Email (можно '-'): ", true);
        String notes = readNonEmptyOrDash("Заметки (можно '-'): ", true);

        Client c = new Client();
        c.setFullName(fullName);
        c.setPhone("-".equals(phone) ? null : phone);
        c.setEmail("-".equals(email) ? null : email);
        c.setNotes("-".equals(notes) ? null : notes);

        Client saved = clientService.createClient(c);
        System.out.println("Клиент создан, id=" + saved.getId());
    }

    private void deleteClientFlow() {
        System.out.println("\n=== Удаление клиента ===");
        Integer id = readIntOrNull("ID клиента (или '-' для отмены): ");
        if (id == null) {
            System.out.println("Отмена удаления.");
            return;
        }
        boolean ok = clientService.deleteClientById(id);
        if (ok) {
            System.out.println("Клиент с id=" + id + " удалён.");
        } else {
            System.out.println("Клиент с таким id не найден.");
        }
    }

    private void searchClientFlow() {
        System.out.println("\n=== Поиск клиента ===");
        System.out.println("  1) По ФИО (подстрока)");
        System.out.println("  2) По телефону (подстрока)");
        System.out.println("  0) Назад");
        String choice = readLine(">>> ");

        List<Client> result = List.of();
        switch (choice) {
            case "1" -> {
                String q = readLine("Введите часть имени: ");
                result = clientService.searchByName(q);
            }
            case "2" -> {
                String q = readLine("Введите часть телефона: ");
                result = clientService.searchByPhone(q);
            }
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Не понял выбор.");
                return;
            }
        }

        System.out.println("Найдено: " + result.size());
        for (Client c : result) {
            System.out.printf("  id=%d | %s | %s | %s%n",
                    c.getId(),
                    safe(c.getFullName()),
                    safe(c.getPhone()),
                    safe(c.getEmail()));
        }
    }

    // ===== Работа с заказами (простая форма) =====

    private void manageOrders() {
        boolean back = false;
        while (!back) {
            System.out.println("\nМеню заказов:");
            System.out.println("  1) Создать новый заказ (простая форма)");
            System.out.println("  0) Назад");
            String choice = readLine(">>> ");
            switch (choice) {
                case "1" -> createOrderFlow();
                case "0" -> back = true;
                default -> System.out.println("Не понял выбор.");
            }
        }
    }

    private void createOrderFlow() {
        System.out.println("\n=== Создание заказа ===");

        // 1. Выбор клиента
        Integer clientId;
        while (true) {
            System.out.println("Выбери клиента по ID. Подсказка: сначала можешь глянуть таблицу clients сверху.");
            clientId = readIntOrNull("ID клиента (или '-' для отмены): ");
            if (clientId == null) {
                System.out.println("Отмена создания заказа.");
                return;
            }
            var clientOpt = clientService.getClientById(clientId);
            if (clientOpt.isEmpty()) {
                System.out.println("Нет клиента с таким ID, попробуй ещё.");
                continue;
            }
            System.out.println("Клиент: " + clientOpt.get().getFullName());
            break;
        }

        // 2. Статус заказа
        System.out.println("""
                Статус заказа (подсказка):
                  CREATED      — создан
                  IN_PROGRESS  — в работе
                  DONE         — выполнен
                  CANCELED     — отменён
                """);
        String status;
        while (true) {
            status = readLine("Статус (по умолчанию CREATED, можно '-' для CREATED): ").trim();
            if (status.isEmpty() || "-".equals(status)) {
                status = "CREATED";
            }
            if (List.of("CREATED", "IN_PROGRESS", "DONE", "CANCELED").contains(status)) {
                break;
            }
            System.out.println("Неверный статус. Допустимые: CREATED, IN_PROGRESS, DONE, CANCELED.");
        }

        // 3. Пример простой "стоимости"
        System.out.println("""
                Сумма заказа (подсказка):
                  - Можно ввести число типа 2500.50
                  - Можно '-' если пока неизвестно (тогда 0)
                """);
        BigDecimal totalCost = null;
        while (totalCost == null) {
            String input = readLine("Сумма: ");
            if (input.isBlank() || "-".equals(input.trim())) {
                totalCost = BigDecimal.ZERO;
                break;
            }
            try {
                totalCost = new BigDecimal(input.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Не похоже на число, попробуй ещё. Пример: 1999.99 или '-'.");
            }
        }

        Order order = orderService.createSimpleOrder(clientId, status, totalCost);
        System.out.println("Заказ создан, id=" + order.getId());
    }

    // ===== Хелперы ввода =====

    private String readLine(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        return line == null ? "" : line.trim();
    }

    /**
     * Если allowDash = true:
     *   - пустая строка -> "-"
     *   - "-" -> "-"
     * Если allowDash = false:
     *   - не даём выйти, пока не введёт хоть что-то кроме "-"
     */
    private String readNonEmptyOrDash(String prompt, boolean allowDash) {
        while (true) {
            String input = readLine(prompt);
            if (!allowDash) {
                if (input.isBlank() || "-".equals(input)) {
                    System.out.println("Поле обязательно, '-' не допускается. Попробуй ещё.");
                    continue;
                }
                return input;
            } else {
                if (input.isBlank()) {
                    return "-";
                }
                return input;
            }
        }
    }

    private Integer readIntOrNull(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input.isBlank() || "-".equals(input)) {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Нужно целое число или '-' для отмены.");
            }
        }
    }
}
