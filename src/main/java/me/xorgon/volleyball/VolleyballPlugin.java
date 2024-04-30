package me.xorgon.volleyball;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import de.slikey.effectlib.EffectManager;
import me.xorgon.volleyball.commands.VBFischietto;
import me.xorgon.volleyball.commands.VolleyballCommand;
import me.xorgon.volleyball.objects.Court;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public class VolleyballPlugin extends JavaPlugin {

    private static VolleyballPlugin instance;
    private VManager manager;
    private static final String COMMAND_PREFIX = "vb";
    private EffectManager effectManager;
    private CommandsManager<CommandSender> commands;


    @Override
    public void onEnable() {
        super.onEnable();
        this.getLogger().info("§8§m-------------------------------------------");
        this.getLogger().info("§eVolley§fball §6FORK §7Version: 1.0");
        this.getLogger().info("§fMade By §exorgon");
        this.getLogger().info("§fForked By §eMilanstarkk");
        this.getLogger().info("§e§ndsc.gg/milanstarkk");
        this.getLogger().info("§8§m-------------------------------------------");
        instance = this;
        manager = new VManager();
        getServer().getPluginManager().registerEvents(new VListener(), this);
        setupCommands();
        effectManager = new EffectManager(this);
        effectManager.enableDebug(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        manager.getCourts().values().forEach(Court::resetCourt);
        manager.getCourtsConfig().save();
        manager.getMessagesConfig().save();
        effectManager.dispose();
    }

    public static VolleyballPlugin getInstance() {
        return instance;
    }

    public VManager getManager() {
        return manager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    private boolean commandsRegistered = false;

    public void setupCommands() {
        if (!commandsRegistered) {
            commands = new CommandsManager<CommandSender>() {
                @Override
                public boolean hasPermission(CommandSender commandSender, String s) {
                    return commandSender.hasPermission(s);
                }
            };

            new CommandsManagerRegistration(this, commands).register(VolleyballCommand.VolleyballRootCommand.class);
            new CommandsManagerRegistration(this, commands).register(VBFischietto.FischiettoRootCommand.class);

            commandsRegistered = true; // Imposta lo stato dei comandi come registrati
        }
    }
}
