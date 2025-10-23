package ar.com.uade.pds.final_project.menu;


import ar.com.uade.pds.final_project.domain.controller.AuthController;
import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@Component
public class AuthMenu {

    private final AuthController controller;

    public AuthMenu(AuthController controller) {
        this.controller = controller;
    }

    public void show(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== MENÚ DE AUTENTICACIÓN ===");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Validar email con token");
            System.out.println("0. Volver al menú principal");
            System.out.print("Selecciona una opción: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleRegister(scanner);
                case "2" -> handleAuthenticate(scanner);
                case "3" -> handleVerifyEmail(scanner);
                case "0" -> back = true;
                default ->System.out.println("Opción inválida.");
            }
        }
    }

    private void handleRegister(Scanner scanner) {
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
        ValidationDTOResponse data = (ValidationDTOResponse) response.getData();
        if(response.isSuccess()) {
           System.out.println("Registro exitoso. Por favor, verifica tu email para activar tu cuenta.");
           System.out.println("Token de verificación: " + data.getData().toString());
        } else {
           System.out.println("Registro fallido: " + response.getMessage());
        }
    }

    private void handleAuthenticate(Scanner scanner) {
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

    private void handleVerifyEmail(Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Token: ");
        String token = scanner.nextLine();

        EmailVerificationRequest request = new EmailVerificationRequest(email, token);
        ResponseWrapper response = controller.validateToken(request);
        System.out.println(response.getMessage());
    }
}
