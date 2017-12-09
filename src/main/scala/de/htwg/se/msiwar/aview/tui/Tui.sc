import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.{ControllerImpl}
import de.htwg.se.msiwar.model.GameModelImpl

var tui = new Tui(new ControllerImpl(new GameModelImpl))

// Print welcome message
tui.printWelcomeMessage

// Print available commands
tui.printUserActions

// Print current board to console
tui.printBoard

// Execute move command
tui.executeCommand("M")