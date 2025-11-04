package ar.com.uade.pds.final_project.menu;


import ar.com.uade.pds.final_project.domain.controller.NotificationController;
import ar.com.uade.pds.final_project.domain.controller.ScrimController;
import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SearchRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@AllArgsConstructor
public class NotificationsMenu {

    private final NotificationController controller;

    public void show(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== MENÚ DE SCRIMS ===");
            System.out.println("1. Desubscribirse de notificaciones");
            System.out.println("0. Volver al menú principal");
            System.out.print("Selecciona una opción: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleUnsuscribe(scanner);
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void handleUnsuscribe(Scanner scanner) {
        System.out.println("\n--- Desubscribirse de notificaciones ---");

        ResponseWrapper response = controller.unsubscribeNotifications();
        if ((boolean) response.getData()) {
            System.out.println("Te has desubscripto de las notificaciones exitosamente.");
        } else {
            System.out.println("No estabas subscripto a las notificaciones.");
        }
    }
}
