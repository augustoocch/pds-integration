package ar.com.uade.pds.final_project.menu;


import ar.com.uade.pds.final_project.domain.controller.MatchmakingController;
import ar.com.uade.pds.final_project.domain.controller.ScrimController;
import ar.com.uade.pds.final_project.domain.controller.TeamManagementController;
import ar.com.uade.pds.final_project.domain.dto.request.*;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
@Component
@AllArgsConstructor
public class ScrimMenu {

    private final ScrimController scrimController;
    private final TeamManagementController teamManagementController;
    private final MatchmakingController matchmakingController;

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
            System.out.println("7. Matchmaking");
            System.out.println("8. Asignar rol a jugador");
            System.out.println("9. Intercambiar jugadores");
            System.out.println("10. Deshacer última acción");
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
                case "7" -> handleMatchmaking(scanner);
                case "8" -> handleAssignRole(scanner);
                case "9" -> handleSwapPlayers(scanner);
                case "10" -> handleUndoLastAction();
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
        System.out.print("Modo de juego (ranked, casual): ");
        String mode = scanner.nextLine();

        ScrimCreationRequest request = new ScrimCreationRequest(game, format, mode);
        ResponseWrapper response = scrimController.createScrim(request);
        System.out.println(response.getMessage());
    }

    private void handleEndScrim(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long id = Long.parseLong(scanner.nextLine());
        ResponseWrapper response = scrimController.endScrim(id);
        System.out.println(response.getMessage());
    }

    private void handleCancelScrim(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long id = Long.parseLong(scanner.nextLine());
        ResponseWrapper response = scrimController.cancelScrim(id);
        System.out.println(response.getMessage());
    }

    private void handleConfirmScrim(Scanner scanner) {
        System.out.print("ID del scrim: ");
        Long id = Long.parseLong(scanner.nextLine());
        ResponseWrapper response = scrimController.confirmScrim(id);
        System.out.println(response.getMessage());
    }

    private void handleSearchScrim(Scanner scanner) {
        System.out.println("\n--- Buscar Scrims ---");
        System.out.print("Juego (desert, urban, space): ");
        String game = scanner.nextLine();
        System.out.print("Región (LATAM, US, EU, ASIA): ");
        String region = scanner.nextLine();
        System.out.print("Formato (1V1, 2V2, 5V5): ");
        String format = scanner.nextLine();

        SearchRequest request = new SearchRequest(game, region, format);
        ResponseWrapper response = scrimController.searchScrim(request);
        if (!response.isSuccess()) {
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
        ResponseWrapper response = scrimController.joinQueue(request);
        System.out.println(response.getMessage());
    }

    private void handleMatchmaking(Scanner scanner) {
        System.out.print("Tipo de busqueda (range, latency, compatibility): ");
        String type = scanner.nextLine();
        MatchmakingRequest request = new MatchmakingRequest(type);
        ResponseWrapper response = matchmakingController.joinMatchmakingScrim(request);
        System.out.println(response.getMessage());
    }

    // === FUNCIONALIDADES DE EQUIPOS ===

    private void handleAssignRole(Scanner scanner) {
        System.out.println("\n--- Asignar rol a jugador ---");
        System.out.print("ID del scrim: ");
        Long scrimId = Long.parseLong(scanner.nextLine());
        System.out.print("ID del jugador: ");
        Long userId = Long.parseLong(scanner.nextLine());
        System.out.print("Nuevo rol (sniper, support, tank, warrior, assassin, mage, marksman): ");
        String roleInput = scanner.nextLine();

        try {
            RoleAssignmentRequest request = new RoleAssignmentRequest(scrimId, userId, roleInput);
            ResponseWrapper response = teamManagementController.assignRole(request);
            System.out.println(response.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Rol inválido. Intenta nuevamente.");
        }
    }

    private void handleSwapPlayers(Scanner scanner) {
        System.out.println("\n--- Intercambiar jugadores ---");
        System.out.print("ID del scrim: ");
        Long scrimId = Long.parseLong(scanner.nextLine());
        System.out.print("ID del Jugador A: ");
        Long userAId = Long.parseLong(scanner.nextLine());
        System.out.print("ID del Jugador B: ");
        Long userBId = Long.parseLong(scanner.nextLine());

        SwapRequest request = new SwapRequest(scrimId, userAId, userBId);
        ResponseWrapper response = teamManagementController.swapPlayers(request);
        System.out.println(response.getMessage());
    }

    private void handleUndoLastAction() {
        ResponseWrapper response = teamManagementController.undoLastAction();
        System.out.println(response.getMessage());
    }

    // === UTILITARIO ===

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
