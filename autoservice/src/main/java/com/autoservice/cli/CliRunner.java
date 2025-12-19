package com.autoservice.cli;

import com.autoservice.entity.Client;
import com.autoservice.entity.Order;
import com.autoservice.entity.Car;
import com.autoservice.service.ClientService;
import com.autoservice.service.OrderService;
import com.autoservice.service.CarService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class CliRunner implements CommandLineRunner {

    private final ClientService clientService;
    private final OrderService orderService;
    private final CarService carService;

    private final Scanner scanner = new Scanner(System.in);

    public CliRunner(ClientService clientService, OrderService orderService, CarService carService) {
        this.clientService = clientService;
        this.orderService = orderService;
        this.carService = carService;
    }

    @Override
    public void run(String... args) {
        clearScreen();  // Очищаем экран перед первым выводом
        System.out.println("=== Autoservice CLI ===");
        System.out.println("Ништяк, прога запущена. Работай, родной.\n");

        boolean running = true;
        while (running) {
            printMainMenu();  // Выводим главное меню
            String choice = readLine(">>> ");
            clearScreen();  // Очищаем экран после выбора
            switch (choice) {
                case "1" -> manageClients();
                case "2" -> manageOrders();
                case "0" -> {
                    System.out.println("Выход. До связи 👋");
                    running = false;
                }
                default -> System.out.println("Не понял выбор, попробуй ещё.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("Главное меню:");
        System.out.println("  1) Клиенты");
        System.out.println("  2) Заказы");
        System.out.println("  0) Выход");
    }

    private void manageClients() {
        boolean back = false;
        while (!back) {
            System.out.println("\nМеню клиентов:");
            System.out.println("  1) Добавить клиента");
            System.out.println("  2) Удалить клиента");
            System.out.println("  3) Поиск клиента");
            System.out.println("  0) Назад");
            String choice = readLine(">>> ");
            clearScreen();  // Очищаем экран после выбора
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

    private String safe(String s) {
        return s == null ? "-" : s;
    }

    private String readNonEmptyOrDash(String prompt, boolean allowDash) {
        String input;
        while (true) {
            input = readLine(prompt);
            if ("-".equals(input) && allowDash) {
                return input;
            }
            if (input != null && !input.trim().isEmpty()) {
                return input;
            }
            System.out.println("Необходимо ввести значение, попробуй снова.");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Integer readIntOrNull(String prompt) {
        String input = readLine(prompt);
        if ("-".equals(input)) {
            return null;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа.");
            return null;
        }
    }

    private void manageOrders() {
        boolean back = false;
        while (!back) {
            System.out.println("\nМеню заказов:");
            System.out.println("  1) Создать заказ");
            System.out.println("  2) Назначить сотрудника на заказ");
            System.out.println("  3) Список заказов");
            System.out.println("  4) Просмотр заказа по ID");
            System.out.println("  0) Назад");
            String choice = readLine(">>> ");
            clearScreen();  // Очищаем экран после выбора
            switch (choice) {
                case "1" -> createOrderFlow();
                case "2" -> assignEmployeeToOrder();
                case "3" -> listOrders();
                case "4" -> viewOrderById();
                case "0" -> back = true;
                default -> System.out.println("Не понял выбор.");
            }
        }
    }

    private void listOrders() {
        List<Order> orders = orderService.getAllOrders();
        System.out.println("Список заказов:");
        for (Order order : orders) {
            System.out.printf("ID: %d | Статус: %s | Клиент ID: %d | Авто ID: %d | Сотрудник: %s%n",
                    order.getId(), order.getStatus(), order.getClientId(), order.getCarId(), order.getAssignedEmployee());
        }
    }

    private void viewOrderById() {
        Integer orderId = readIntOrNull("Введите ID заказа для просмотра: ");
        if (orderId == null) {
            System.out.println("Отмена просмотра заказа.");
            return;
        }

        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            System.out.println("Детали заказа:");
            System.out.printf("ID: %d | Статус: %s | Клиент ID: %d | Авто ID: %d | Сотрудник: %s | Сумма: %s%n",
                    order.getId(), order.getStatus(), order.getClientId(), order.getCarId(), order.getAssignedEmployee(), order.getTotalCost());
        } else {
            System.out.println("Заказ с таким ID не найден.");
        }
    }

    private void createOrderFlow() {
        System.out.println("\n=== Создание заказа ===");

        // 1. Выбор клиента
        Integer clientId;
        List<Client> clients = clientService.getAllClients();
        System.out.println("Доступные клиенты:");
        for (Client client : clients) {
            System.out.printf("ID: %d | %s\n", client.getId(), client.getFullName());
        }

        while (true) {
            clientId = readIntOrNull("Выберите ID клиента (или '-' для отмены): ");
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

        // 2. Выбор автомобиля
        Integer carId;
        List<Car> cars = carService.getAllCars();  
        System.out.println("Доступные автомобили:");
        for (Car car : cars) {
            System.out.printf("ID: %d | %s %s (%d)\n", car.getId(), car.getMake(), car.getModel(), car.getYear());
        }

        while (true) {
            carId = readIntOrNull("Выберите ID автомобиля (или '-' для отмены): ");
            if (carId == null) {
                System.out.println("Отмена создания заказа.");
                return;
            }
            var carOpt = carService.getCarById(carId);
            if (carOpt.isEmpty()) {
                System.out.println("Нет автомобиля с таким ID, попробуй ещё.");
                continue;
            }
            System.out.println("Автомобиль: " + carOpt.get().getMake() + " " + carOpt.get().getModel());
            break;
        }

        // 3. Статус заказа
        String status = readLine("Статус (по умолчанию CREATED, можно '-' для CREATED): ");
        if (status.isEmpty()) {
            status = "CREATED";
        }

        // 4. Сумма заказа
        BigDecimal totalCost = readBigDecimalOrNull("Сумма заказа (по умолчанию 0): ");
        if (totalCost == null) {
            totalCost = BigDecimal.ZERO;
        }

        // 5. Назначение сотрудника (по умолчанию "Nobody")
        String assignedEmployee = readLine("Назначить сотрудника (по умолчанию 'Nobody'): ");
        if (assignedEmployee.isEmpty()) {
            assignedEmployee = "Nobody";
        }

        // 6. Создание заказа
        Order order = orderService.createSimpleOrder(clientId, status, totalCost, carId, assignedEmployee);
        System.out.println("Заказ успешно создан, ID заказа: " + order.getId());
    }

    private void assignEmployeeToOrder() {
        Integer orderId = readIntOrNull("Введите ID заказа для назначения сотрудника: ");
        if (orderId == null) {
            System.out.println("Отмена назначения сотрудника.");
            return;
        }

        String employeeName = readLine("Введите имя сотрудника: ");
        orderService.assignEmployeeToOrder(orderId, employeeName);
        System.out.println("Сотрудник " + employeeName + " назначен на заказ с ID " + orderId);
    }

    private BigDecimal readBigDecimalOrNull(String prompt) {
        String input = readLine(prompt);
        if ("-".equals(input)) {
            return null;
        }
        try {
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа.");
            return null;
        }
    }

    private void clearScreen() {
        // Очистка экрана
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Не удалось очистить экран.");
        }
    }
}

