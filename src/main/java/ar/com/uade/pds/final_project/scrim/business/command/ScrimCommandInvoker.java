package ar.com.uade.pds.final_project.scrim.business.command;

import java.util.Stack;

public class ScrimCommandInvoker {

    private final Stack<ScrimCommand> history = new Stack<>();

    public void executeCommand(ScrimCommand command) {
        command.execute();
        history.push(command);
    }

    public void undoLastCommand() {
        if (!history.isEmpty()) {
            ScrimCommand command = history.pop();
            command.undo();
        }
    }
}

