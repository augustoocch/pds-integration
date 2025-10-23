package ar.com.uade.pds.final_project;

import ar.com.uade.pds.final_project.domain.controller.AuthController;
import ar.com.uade.pds.final_project.domain.controller.ScrimController;
import ar.com.uade.pds.final_project.domain.dto.request.*;
import ar.com.uade.pds.final_project.menu.AuthMenu;
import ar.com.uade.pds.final_project.menu.ScrimMenu;
import ar.com.uade.pds.final_project.users.Business.SessionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


import java.util.Scanner;

@SpringBootApplication
public class FinalProjectApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(FinalProjectApplication.class, args);

        AuthController authController = context.getBean(AuthController.class);
        ScrimController scrimController = context.getBean(ScrimController.class);

        AuthMenu authMenu = new AuthMenu(authController);
        ScrimMenu scrimMenu = new ScrimMenu(scrimController);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Autenticación");
            System.out.println("2. Scrims");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> authMenu.show(scanner);
                case "2" -> scrimMenu.show(scanner);
                case "0" -> {
                    running = false;
                    System.out.println("Saliendo de la aplicación...");
                }
                default -> System.out.println("Opción inválida. Intenta nuevamente.");
            }
        }
    }
}
