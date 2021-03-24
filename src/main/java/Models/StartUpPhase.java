package Models;

import Constants.ApplicationConstants;
import Controllers.GameEngine;
import Exceptions.InvalidCommand;
import Exceptions.InvalidMap;
import Utils.Command;
import Utils.CommonUtil;
import Utils.ExceptionLogHandler;
import Views.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Start Up Phase implementation for GamePlay using State Pattern.
 */
public class StartUpPhase extends Phase{

    /**
     * It's a constructor that init the GameEngine context in Phase class.
     *
     * @param p_gameEngine GameEngine Context
     */
    public StartUpPhase(GameEngine p_gameEngine){
        super(p_gameEngine);
    }

    /**
     * {@inheritDoc}
     */
    public void performMapEdit(Command p_command) throws IOException, InvalidCommand, InvalidMap {
        List<java.util.Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));

        if (l_operations_list == null || l_operations_list.isEmpty()) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITMAP);
        } else {
            for (Map<String, String> l_map : l_operations_list) {
                if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)) {
                    d_mapService.editMap(d_gameState, l_map.get(ApplicationConstants.ARGUMENTS));
                } else {
                    throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITMAP);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performEditContinent(Command p_command) throws IOException, InvalidCommand, InvalidMap {
        if (!l_isMapLoaded) {
            d_gameEngine.setD_gameEngineLog("Can not Edit Continent, please perform `editmap` first", "effect");
            return;
        }

        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (l_operations_list == null || l_operations_list.isEmpty()) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITCONTINENT);
        } else {
            for (Map<String, String> l_map : l_operations_list) {
                if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)
                        && p_command.checkRequiredKeysPresent(ApplicationConstants.OPERATION, l_map)) {
                    d_mapService.editFunctions(d_gameState, l_map.get(ApplicationConstants.ARGUMENTS),
                            l_map.get(ApplicationConstants.OPERATION), 1);
                } else {
                    throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITCONTINENT);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performSaveMap(Command p_command) throws InvalidCommand, InvalidMap {
        if (!l_isMapLoaded) {
            d_gameEngine.setD_gameEngineLog("No map found to save, Please `editmap` first", "effect");
            return;
        }

        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (null == l_operations_list || l_operations_list.isEmpty()) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_SAVEMAP);
        } else {
            for (Map<String, String> l_map : l_operations_list) {
                if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)) {
                    boolean l_fileUpdateStatus = d_mapService.saveMap(d_gameState,
                            l_map.get(ApplicationConstants.ARGUMENTS));
                    if (l_fileUpdateStatus) {
                        d_gameEngine.setD_gameEngineLog("Required changes have been made in map file", "effect");
                    } else
                        System.out.println(d_gameState.getError());
                } else {
                    throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_SAVEMAP);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLoadMap(Command p_command) throws InvalidCommand, InvalidMap {
        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();
        boolean l_flagValidate = false;

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (null == l_operations_list || l_operations_list.isEmpty()) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_LOADMAP);
        } else {
            for (Map<String, String> l_map : l_operations_list) {
                if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)) {
                    // Loads the map if it is valid or resets the game state
                    Models.Map l_mapToLoad = d_mapService.loadMap(d_gameState,
                            l_map.get(ApplicationConstants.ARGUMENTS));
                    if (l_mapToLoad.Validate()) {
                        l_flagValidate = true;
                        d_gameState.setD_loadCommand();
                        d_gameEngine.setD_gameEngineLog(l_map.get(ApplicationConstants.ARGUMENTS)+ " has been loaded to start the game", "effect" );
                    } else {
                        d_mapService.resetMap(d_gameState, l_map.get(ApplicationConstants.ARGUMENTS));
                    }
                    if(!l_flagValidate){
                        d_mapService.resetMap(d_gameState, l_map.get(ApplicationConstants.ARGUMENTS));
                    }
                } else {
                    throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_LOADMAP);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performValidateMap(Command p_command) throws InvalidMap, InvalidCommand {
        if (!l_isMapLoaded) {
            d_gameEngine.setD_gameEngineLog("No map found to validate, Please `loadmap` & `editmap` first", "effect");
            return;
        }

        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (null == l_operations_list || l_operations_list.isEmpty()) {
            Models.Map l_currentMap = d_gameState.getD_map();
            if (l_currentMap == null) {
                throw new InvalidMap(ApplicationConstants.INVALID_MAP_ERROR_EMPTY);
            } else {
                if (l_currentMap.Validate()) {
                    d_gameEngine.setD_gameEngineLog(ApplicationConstants.VALID_MAP, "effect");
                } else {
                    throw new InvalidMap("Failed to Validate map!");
                }
            }
        } else {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_VALIDATEMAP);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performEditCountry(Command p_command) throws InvalidCommand, InvalidMap, IOException {
        if (!l_isMapLoaded) {
            d_gameEngine.setD_gameEngineLog("Can not Edit Country, please perform `editmap` first", "effect");
            return;
        }

        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (null == l_operations_list || l_operations_list.isEmpty()) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITCOUNTRY);
        } else {
            for (Map<String, String> l_map : l_operations_list) {
                if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)
                        && p_command.checkRequiredKeysPresent(ApplicationConstants.OPERATION, l_map)) {
                    d_mapService.editFunctions(d_gameState, l_map.get(ApplicationConstants.OPERATION),
                            l_map.get(ApplicationConstants.ARGUMENTS), 2);
                } else {
                    throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITCOUNTRY);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performEditNeighbour(Command p_command) throws InvalidCommand, InvalidMap, IOException {
        if (!l_isMapLoaded) {
            d_gameEngine.setD_gameEngineLog("Can not Edit Neighbors, please perform `editmap` first", "effect");
            return;
        }

        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (null == l_operations_list || l_operations_list.isEmpty()) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITCOUNTRY);
        } else {
            for (Map<String, String> l_map : l_operations_list) {
                if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)
                        && p_command.checkRequiredKeysPresent(ApplicationConstants.OPERATION, l_map)) {
                    d_mapService.editFunctions(d_gameState, l_map.get(ApplicationConstants.OPERATION),
                            l_map.get(ApplicationConstants.ARGUMENTS), 3);
                } else {
                    throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_EDITCOUNTRY);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createPlayers(Command p_command) throws InvalidCommand {
        if (!l_isMapLoaded) {
            d_gameEngine.setD_gameEngineLog("No map found, Please `loadmap` before adding game players", "effect");
            return;
        }

        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (CommonUtil.isCollectionEmpty(l_operations_list)) {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_GAMEPLAYER);
        } else {
            if (d_gameState.getD_loadCommand()) {
                for (Map<String, String> l_map : l_operations_list) {
                    if (p_command.checkRequiredKeysPresent(ApplicationConstants.ARGUMENTS, l_map)
                            && p_command.checkRequiredKeysPresent(ApplicationConstants.OPERATION, l_map)) {
                        d_playerService.updatePlayers(d_gameState, l_map.get(ApplicationConstants.OPERATION),
                                l_map.get(ApplicationConstants.ARGUMENTS));
                    } else {
                        throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_GAMEPLAYER);
                    }
                }
            } else {
                d_gameEngine.setD_gameEngineLog("Please load a valid map first via loadmap command!", "effect");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performAssignCountries(Command p_command) throws InvalidCommand, IOException{
        List<Map<String, String>> l_operations_list = p_command.getOperationsAndArguments();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionLogHandler(d_gameState));
        if (CommonUtil.isCollectionEmpty(l_operations_list)) {
            d_playerService.assignCountries(d_gameState);
            d_playerService.assignColors(d_gameState);

            while (!CommonUtil.isCollectionEmpty(d_gameState.getD_players())) {
                d_gameEngine.setD_gameEngineLog("\n********Starting Main Game Loop***********\n", "start");

                // Assigning armies to players
                d_playerService.assignArmies(d_gameState);
                issueOrders();
                executeOrders();

                MapView l_map_view = new MapView(d_gameState);
                l_map_view.showMap();

                System.out.println("Press Y/y if you want to continue for next turn or else press N/n");
                BufferedReader l_reader = new BufferedReader(new InputStreamReader(System.in));
                String l_continue = l_reader.readLine();
                if (l_continue.equalsIgnoreCase("N"))
                    break;
            }
        } else {
            throw new InvalidCommand(ApplicationConstants.INVALID_COMMAND_ERROR_ASSIGNCOUNTRIES);
        }
    }


}