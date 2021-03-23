package Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Exceptions.InvalidCommand;
import Exceptions.InvalidMap;
import Models.*;

/**
 * This is the entry point of the Game and keeps the track of current Game
 * State.
 */
public class GameEngine {

	/**
	 *	It is the current game play phase as per state pattern.
	 */
	Phase d_currentPhase = new StartUpPhase(this);

	/**
	 * It's used to update context.
	 *
	 * @param p_phase new Phase to set in Game context
	 */
	public void setD_CurrentPhase(Phase p_phase){
		d_currentPhase = p_phase;
	}

	/**
	 * This method is getter for current Phase of Game Context.
	 *
	 * @return current Phase of Game Context
	 */
	public Phase getD_CurrentPhase(){
		return d_currentPhase;
	}

	/**
	 * Shows and Writes GameEngine Logs.
	 *
	 * @param p_gameEngineLog String of Log message.
	 * @param p_logType Type of Log.
	 */
	public void setD_gameEngineLog(String p_gameEngineLog, String p_logType) {
		d_currentPhase.getD_gameState().updateLog(p_gameEngineLog, p_logType);
		System.out.println(p_gameEngineLog);
	}

	/**
	 * The main method responsible for accepting command from users and redirecting
	 * those to corresponding logical flows.
	 *
	 * @param p_args the program doesn't use default command line arguments
	 */
	public static void main(String[] p_args) {
		GameEngine l_game = new GameEngine();

		l_game.getD_CurrentPhase().getD_gameState().updateLog("Initializing the Game ......"+System.lineSeparator(), "start");
		l_game.initGamePlay();
	}

	/**
	 * This method initiates the CLI to accept commands from user and maps them to
	 * corresponding action handler.
	 */
	private void initGamePlay() {
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				System.out.println("Enter Game Commands or type 'exit' for quitting");
				String l_commandEntered = l_reader.readLine();

				d_currentPhase.handleCommand(l_commandEntered);
			} catch (InvalidCommand | InvalidMap | IOException l_exception) {
				this.setD_gameEngineLog(l_exception.getMessage(), "effect");
			}
		}
	}
}
