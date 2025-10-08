package ar.com.uade.pds.final_project;

import ar.com.uade.pds.final_project.domain.controller.AuthController;
import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class FinalProjectApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(FinalProjectApplication.class, args);
        AuthController authController = context.getBean(AuthController.class);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Register");
            System.out.println("2. Authenticate");
            System.out.println("3. Verify Email Token");
            System.out.println("0. Exit");
            System.out.print("Selecciona una opción: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> handleRegister(authController, scanner);
                case "2" -> handleAuthenticate(authController, scanner);
                case "3" -> handleVerifyToken(authController, scanner);
                case "0" -> {
                    running = false;
                    System.out.println("Saliendo de la aplicación...");
                }
                default -> System.out.println("Opción inválida. Intenta nuevamente.");
            }
        }
    }

    private static void handleRegister(AuthController controller, Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        RegisterRequest request = RegisterRequest.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
        ResponseWrapper response = controller.register(request);
        System.out.println(response.getMessage());
    }

    private static void handleAuthenticate(AuthController controller, Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        AuthenticationRequest request = new AuthenticationRequest(email, password);
        ResponseWrapper response = controller.authenticate(request);
        System.out.println(response.getMessage());
        if (response.isSuccess() && response.getData() != null) {
            System.out.println("Token: " + response.getData());
        }
    }

    private static void handleVerifyToken(AuthController controller, Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Token: ");
        String token = scanner.nextLine();

        EmailVerificationRequest request = new EmailVerificationRequest(email, token);
        ResponseWrapper response = controller.validateToken(request);
        System.out.println(response.getMessage());
    }
}
