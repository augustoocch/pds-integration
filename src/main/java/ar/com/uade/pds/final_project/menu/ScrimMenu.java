package ar.com.uade.pds.final_project.menu;


import ar.com.uade.pds.final_project.domain.controller.ScrimController;
import ar.com.uade.pds.final_project.domain.dto.request.GameFormatValue;
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
public class ScrimMenu {

    private final ScrimController controller;

    public void show(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== MENÚ DE SCRIMS ===");
            System.out.println("1. Crear scrim");
            System.out.println("2. Finalizar scrim");
            System.out.println("3. Cancelar scrim");
            System.out.println("4. Confirmar scrim");
            System.out.println("5. Buscar scrims");
            System.out.println("6. Unirse a una cola");
            System.out.println("0. Volver al menú principal");
            System.out.print("Selecciona una opción: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleCreateScrim(scanner);
                case "2" -> handleEndScrim(scanner);
                case "3" -> handleCancelScrim(scanner);
                case "4" -> handleConfirmScrim(scanner);
                case "5" -> handleSearchScrim(scanner);
                case "6" -> handleJoinQueue(scanner);
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void handleCreateScrim(Scanner scanner) {
        System.out.println("\n--- Crear Scrim ---");
        System.out.print("Juego (desert, urban, space): ");
        String game = scanner.nextLine();
        System.out.print("Formato (1V1, 2V2, 5V5): ");
        String format = scanner.nextLine();
        System.out.print("Modo de juego (ranked, unranked): ");
        String mode = scanner.nextLine();

        ScrimCreationRequest request = new ScrimCreationRequest(game,
                format, mode);
        ResponseWrapper response = controller.createScrim(request);
        System.out.println(response.getMessage());
    }

    private void handleEndScrim(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long id = Long.parseLong(scanner.nextLine());
        ResponseWrapper response = controller.endScrim(id);
        System.out.println(response.getMessage());
    }

    private void handleCancelScrim(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long id = Long.parseLong(scanner.nextLine());
        ResponseWrapper response = controller.cancelScrim(id);
        System.out.println(response.getMessage());
    }

    private void handleConfirmScrim(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long id = Long.parseLong(scanner.nextLine());
        ResponseWrapper response = controller.confirmScrim(id);
        System.out.println(response.getMessage());
    }

    private void handleSearchScrim(Scanner scanner) {
        System.out.println("\n--- Buscar Scrims ---");
        System.out.print("Juego (desert, urban, space): ");
        String game = scanner.nextLine();
        System.out.print("Region (LATAM, US, EU, ASIA): ");
        String region = scanner.nextLine();
        System.out.print("Formato (1V1, 2V2, 5V5): ");
        String format = scanner.nextLine();

        SearchRequest request = new SearchRequest(game, region, format);
        ResponseWrapper response = controller.searchScrim(request);
        if(!response.isSuccess()) {
            System.out.println("Error al buscar scrims: " + response.getMessage());
            return;
        }
        List<ScrimDTO> dtos = (List<ScrimDTO>) response.getData();
        printAllScrimsFound(dtos);
    }

    private void handleJoinQueue(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long idScrim = Long.parseLong(scanner.nextLine());

        JoinScrimRequest request = new JoinScrimRequest(idScrim);
        ResponseWrapper response = controller.joinQueue(request);
        System.out.println(response.getMessage());
    }



    public void printAllScrimsFound(List<ScrimDTO> scrims) {
        if (scrims.isEmpty()) {
            System.out.println("No se encontraron scrims.");
        } else {
            System.out.println("Scrims encontrados:");
            for (ScrimDTO scrim : scrims) {
                System.out.println("- Id: " + scrim.getId());
                System.out.println("  Juego: " + scrim.getGame());
                System.out.println("  Formato: " + scrim.getFormat());
                System.out.println("  Latencia: " + scrim.getLatency());
                System.out.println("  Región: " + scrim.getRegion());
                System.out.println("  Modo: " + scrim.getMode());
                System.out.println();
            }
        }
    }
}
