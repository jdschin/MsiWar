import de.htwg.se.msiwar.aview.tui.MsiWarTui
import de.htwg.se.msiwar.controller.{MsiWarControllerImpl}
import de.htwg.se.msiwar.model.MsiWarModelImpl

var tui = new MsiWarTui(new MsiWarControllerImpl(new MsiWarModelImpl))

// Print welcome message
tui.printWelcomeMessage

// Print available commands
tui.printAvailableActions

// Print current board to console
tui.printBoard

// Execute move command
tui.executeCommand("M")