package me.xorgon.volleyball.util;

import me.xorgon.volleyball.objects.Court;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class VMessages {
    private Map<String, String> messages;

    private boolean needsRepair;

    private Map<String, Integer> globalPlaceholders = new HashMap<>();

    private String helpDefault = "§dCome giocare a Pallavolo:\n"
            + "§eCorrere e saltare aumentano entrambi la potenza del tuo colpo.\n"
            + "§eUsa uno, o entrambi, per colpire la palla quanto vuoi.\n"
            + "§eSi consiglia di correre solo per fare un servizio.\n\n"
            + "§eLe squadre si alternano nel servire.\n"
            + "§eLa prima squadra a segnare "
            + "§d" + "{court.maxscore}" + "§e punti vince!\n";

    private String fullGameDefault = "§eLa partita è già iniziata, ma puoi guardarla!";

    private String winMessageDefault = "La squadra {teamname} ha vinto! Congratulazioni!";

    private String drawMessageDefault = "§eÈ pareggio!";

    private String gameLeaveBeforeStartDefault = "§eHai lasciato il campo prima dell'inizio della partita.";

    private String notEnoughPlayersDefault = "§eNon ci sono abbastanza giocatori.";

    private String gameStartDefault = "§ePartita iniziata, sei nella squadra {teamname}.";

    private String scoredDefault = "{teamname} §eha segnato!";

    private String matchPointDefault = "{teamname} §ematch point!";

    private String leaveGameThreatDefault = "§cLascerai il gioco se non torni in campo!";

    private String returnToCourtDefault = "§eBentornato!";

    private String leftGameDefault = "§cHai lasciato il campo da pallavolo!";

    private String teamForfeitDefault = "§eLa squadra {teamname} §eha troppi pochi giocatori e hanno dato forfait.";

    private String doubleForfeitDefault = "§eBoth teams forfeit.";

    private String wrongSideDefault = "§eNon puoi colpire la palla mentre è dalla parte degli avversari!";

    private String tooManyHitsDefault = "§eYour team has already hit it {court.maxhits} times!";

    private String clickForHelpDefault = "§dClicca qui per imparare a giocare a Pallavolo!";

    private String noPermissionsDefault = "§cNon hai il permesso per giocare a pallavolo.";

    private String matchStartedDefault = "§cPartita già iniziata!";

    private String joinedTeamDefault = "§eTi sei unito nella squadra {teamname}§e!";

    private String fullTeamDefault = "§cSquadra {teamname} §cè piena.";

    private String matchStartingWithNameDefault = "§eVolleyball game starting at the {court.name} court in {court.startdelay} seconds!";

    private String matchStartingWithoutNameDefault = "§eVolleyball game starting in {court.startdelay} seconds!";

    private String clickToJoinDefault = "§dClick here to join the game!";

    private String notInCourtDefault = "§eNon sei in un campo da gioco.";

    private String courtDoesNotExistDefault = "§cQuesto campo non esiste.";

    private String worldNotLoadedDefault = "§cThe world for that court is not loaded.";

    private String courtNotReadyDefault = "§cQuesto campo di gioco non è ancora pronto";

    public VMessages() {
        globalPlaceholders.put("court.maxscore", Court.MAX_SCORE);
        globalPlaceholders.put("court.maxhits", Court.MAX_HITS);
        globalPlaceholders.put("court.startdelay", Court.START_DELAY_SECS);

        needsRepair = false;
        createMapWithDefaults();
        replaceAllPlaceholders();
    }

    public void createMapWithDefaults() {
        messages = new HashMap<>();
        messages.put("help", helpDefault);
        messages.put("full-game", fullGameDefault);
        messages.put("win-message", winMessageDefault);
        messages.put("draw-message", drawMessageDefault);
        messages.put("game-leave-before-start", gameLeaveBeforeStartDefault);
        messages.put("not-enough-players", notEnoughPlayersDefault);
        messages.put("game-start", gameStartDefault);
        messages.put("scored", scoredDefault);
        messages.put("match-point", matchPointDefault);

        // MinPlayersChecker
        messages.put("leave-game-threat", leaveGameThreatDefault);
        messages.put("return-to-court", returnToCourtDefault);
        messages.put("left-game", leftGameDefault);
        messages.put("team-forfeit", teamForfeitDefault);
        messages.put("double-forfeit", doubleForfeitDefault);

        // VListener
        messages.put("wrong-side", wrongSideDefault);
        messages.put("too-many-hits", tooManyHitsDefault);
        messages.put("click-for-help", clickForHelpDefault);
        messages.put("no-permissions", noPermissionsDefault);
        messages.put("match-started", matchStartedDefault);
        messages.put("joined-team", joinedTeamDefault);
        messages.put("full-team", fullTeamDefault);
        messages.put("match-starting-with-name", matchStartingWithNameDefault);
        messages.put("match-starting-without-name", matchStartingWithoutNameDefault);
        messages.put("click-to-join", clickToJoinDefault);

        // VolleyballCommand
        messages.put("not-in-court", notInCourtDefault);
        messages.put("court-does-not-exist", courtDoesNotExistDefault);
        messages.put("world-not-loaded", worldNotLoadedDefault);
        messages.put("court-not-ready", courtNotReadyDefault);
    }

    public boolean hasMessageKey(String key) {
        return messages.containsKey(key);
    }

    public void setMessage(String key, String message) {
        messages.replace(key, replacePlaceholders(message));
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    private void replaceAllPlaceholders() {
        for (String messageKey : messages.keySet()) {
            messages.replace(messageKey, replacePlaceholders(messages.get(messageKey)));
        }
    }

    private String replacePlaceholders(String message) {
        for (String replacement : globalPlaceholders.keySet()) {
            message = message.replaceAll("\\{" + replacement + "}", globalPlaceholders.get(replacement).toString());
        }
        return message;
    }

    private String getTeamName(Court.Team team) {
        String teamName = "";
        if (team == Court.Team.RED) {
            teamName = ChatColor.RED + "Red";
        } else if (team == Court.Team.BLUE) {
            teamName = ChatColor.BLUE + "Blue";
        }
        return teamName;
    }

    public boolean needsRepair() {
        return needsRepair;
    }

    public void setNeedsRepair(boolean needsRepair) {
        this.needsRepair = needsRepair;
    }

    // Message getters.

    public String getHelpMessage() {
        return messages.get("help");
    }

    public String getFullGameMessage() {
        return messages.get("full-game");
    }

    public String getWinMessage(Court.Team team) {
        String teamName = getTeamName(team);
        if (team == Court.Team.NONE) {
            return messages.get("draw-message");
        } else {
            return messages.get("win-message").replaceAll("\\{teamname}", teamName);
        }
    }

    public String getGameLeaveBeforeStartMessage() {
        return messages.get("game-leave-before-start");
    }

    public String getNotEnoughPlayersMessage() {
        return messages.get("not-enough-players");
    }

    public String getGameStartMessage(Court.Team team) {
        return messages.get("game-start").replaceAll("\\{teamname}", getTeamName(team));
    }

    public String getScoredMessage(Court.Team team) {
        return messages.get("scored").replaceAll("\\{teamname}", getTeamName(team));
    }

    public String getMatchPointMessage(Court.Team team) {
        String teamName = getTeamName(team);
        if (teamName.equals("")) {
            teamName = "§eDouble";
        }
        return messages.get("match-point").replaceAll("\\{teamname}", teamName);
    }

    public String getLeaveGameThreatMessage() {
        return messages.get("leave-game-threat");
    }

    public String getReturnToCourtMessage() {
        return messages.get("return-to-court");
    }

    public String getLeftGameMessage() {
        return messages.get("left-game");
    }

    public String getForfeitMessage(Court.Team team) {
        if (team == Court.Team.NONE) {
            return messages.get("double-forfeit");
        } else {
            return messages.get("team-forfeit").replaceAll("\\{teamname}", getTeamName(team));
        }
    }

    public String getWrongSideMessage() {
        return messages.get("wrong-side");
    }

    public String getTooManyHitsMessage() {
        return messages.get("too-many-hits");
    }

    public String getClickForHelpMessage() {
        return messages.get("click-for-help");
    }

    public String getNoPermissionsMessage() {
        return messages.get("no-permissions");
    }

    public String getMatchStartedMessage() {
        return messages.get("match-started");
    }

    public String getJoinedTeamMessage(Court.Team team) {
        return messages.get("joined-team").replaceAll("\\{teamname}", getTeamName(team));
    }

    public String getFullTeamMessage(Court.Team team) {
        return messages.get("full-team").replaceAll("\\{teamname}", getTeamName(team));
    }

    public String getMatchStartingWithNameMessage(Court court) {
        return messages.get("match-starting-with-name").replaceAll("\\{court.name}", court.getDisplayName());
    }

    public String getMatchStartingWithoutNameMessage() {
        return messages.get("match-starting-without-name");
    }

    public String getClickToJoinMessage() {
        return messages.get("click-to-join");
    }

    public String getNotInCourtMessage() {
        return messages.get("not-in-court");
    }

    public String getCourtDoesNotExistMessage() {
        return messages.get("court-does-not-exist");
    }

    public String getWorldNotLoadedMessage() {
        return messages.get("world-not-loaded");
    }

    public String getCourtNotReadyMessage() {
        return messages.get("court-not-ready");
    }
}
