package me.MiniDigger.KatzenBot;

import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Martin on 01.10.2016.
 */
public class CommandHandler {
    
    private List<Command> commands = new CopyOnWriteArrayList<>();
    private List<String> ops = new CopyOnWriteArrayList<>();
    
    private List<String> giveaway = new CopyOnWriteArrayList<>();
    private boolean giveawayEnabled = false;
    
    private PokemonHandler pokemonHandler;
    
    public void initCommands() {
        loadOps();
        loadCommands();
        
        pokemonHandler = new PokemonHandler();
        pokemonHandler.init(this);
        
        commands.add(new Command("!addcmd", "op", (label, args, channel, sender, event) -> {
            if (getCommand(args[1]).isPresent()) {
                event.respond("Command already exists!");
                return;
            }
            commands.add(new MemoryCommand("!" + args[1], "all", argsToString(args, 2)));
            event.respond("Command " + args[1] + " added");
            saveCommands();
        }));
        
        commands.add(new Command("!remcmd", "op", (label, args, channel, sender, event) -> {
            Optional<Command> cmd = getCommand("!" + args[1]);
            if (cmd.isPresent()) {
                commands.remove(cmd.get());
                event.respond("Command " + args[1] + " removed");
                saveCommands();
            }
        }));
        
        commands.add(new Command("!editcmd", "op", (label, args, channel, sender, event) -> {
            Optional<Command> cmd = getCommand("!" + args[1]);
            if (cmd.isPresent()) {
                commands.remove(cmd.get());
                event.respond("Command " + args[1] + " updated");
            } else {
                event.respond("Command " + args[1] + " added");
            }
            commands.add(new MemoryCommand("!" + args[1], "all", argsToString(args, 2)));
            saveCommands();
        }));
        
        commands.add(new Command("!op", "op", (label, args, channel, sender, event) -> {
            ops.add(args[1].toLowerCase());
            event.respond("Oped " + args[1].toLowerCase() + "!");
            saveOps();
        }));
        
        commands.add(new Command("!deop", "op", (label, args, channel, sender, event) -> {
            ops.remove(args[1].toLowerCase());
            event.respond("De-Oped " + args[1].toLowerCase() + "!");
            saveOps();
        }));
        
        commands.add(new Command("!giveaway", "all", ((label, args, sender, channel, event) -> {
            if (!giveawayEnabled) {
                event.respond("Zur Zeit läuft kein Giveaway OMGScoots");
                return;
            }
            if (!giveaway.contains(sender)) {
                giveaway.add(sender);
                event.respond("Du bist dabei FutureMan");
            }
        })));
        
        commands.add(new Command("!giveawaystart", "op", ((label, args, sender, channel, event) -> {
            giveawayEnabled = true;
            giveaway.clear();
            event.getBot().send().message(channel, "Giveway gestartet! Gebe !giveway ein um mitzumachen!");
        })));
        
        commands.add(new Command("!giveawayend", "op", ((label, args, sender, channel, event) -> {
            if (!giveawayEnabled) {
                event.respond("Es läuft kein Giveaway du Idiot NotLikeThis");
                return;
            }
            giveawayEnabled = false;
            event.getBot().send().message(channel, "Giveaway zuende, ziehe Gewinner ResidentSleeper");
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException ignored) {
            }
            
            Collections.shuffle(giveaway);
            String winner = giveaway.get(0);
            
            event.getBot().send().message(channel, "Winner gezogen!");
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException ignored) {
            }
            
            event.getBot().send().message(channel, "Es ist");
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException ignored) {
            }
            
            event.getBot().send().message(channel, " @" + winner + "!!! BloodTrail");
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException ignored) {
            }
            
            giveaway.clear();
        })));
    }
    
    public void saveOps() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File("ops.txt")))) {
            ops.forEach(writer::println);
        } catch (Exception ex) {
            System.err.println("error while saving ops");
            ex.printStackTrace();
        }
    }
    
    public void loadOps() {
        try (Scanner scan = new Scanner(new FileInputStream(new File("ops.txt")))) {
            while (scan.hasNext()) {
                ops.add(scan.nextLine());
            }
        } catch (Exception ex) {
            System.err.println("error while loading ops");
            ex.printStackTrace();
        }
    }
    
    public void saveCommands() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File("commands.txt")))) {
            commands.stream().filter(cmd -> cmd instanceof MemoryCommand).forEach(cmd -> {
                MemoryCommand m = (MemoryCommand) cmd;
                writer.println(m.command + "%%%" + m.role + "%%%" + m.response);
            });
        } catch (Exception ex) {
            System.err.println("error while saving commands");
            ex.printStackTrace();
        }
    }
    
    public void loadCommands() {
        try (Scanner scan = new Scanner(new FileInputStream(new File("commands.txt")))) {
            while (scan.hasNext()) {
                String[] line = scan.nextLine().split("%%%");
                if (line.length == 3) {
                    add(new MemoryCommand(line[0], line[1], line[2]));
                } else {
                    System.err.println("bac command: " + Arrays.toString(line));
                }
            }
        } catch (Exception ex) {
            System.err.println("error while loading commands");
            ex.printStackTrace();
        }
    }
    
    public void executeCommand(String label, String[] args, String sender, String channel, MessageEvent event) {
        commands.stream().filter(command -> command.command.equalsIgnoreCase(label)).forEach(command -> {
            if (command.role.equalsIgnoreCase("all") || (command.role.equalsIgnoreCase("op") && ops.contains(sender.toLowerCase()))) {
                command.executor.execute(label, args, sender, channel, event);
            }
        });
    }
    
    private Optional<Command> getCommand(String label) {
        return commands.stream().filter(command -> command.command.equalsIgnoreCase(label)).findAny();
    }
    
    private String argsToString(String[] args, int beginIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = beginIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString();
    }
    
    public void add(Command all) {
        this.commands.add(all);
    }
    
    static class Command {
        protected String command;
        protected String role;
        protected CommandExecutor executor;
        
        public Command(String command, String role, CommandExecutor executor) {
            this.command = command;
            this.role = role;
            this.executor = executor;
        }
    }
    
    class MemoryCommand extends Command {
        
        private String response;
        
        public MemoryCommand(String command, String role, String response) {
            super(command, role, null);
            this.executor = (label, args, sender, channel, event) -> {
                event.getBot().send().message(channel, constructResponse(label, args, sender, channel, event));
            };
            this.response = response;
        }
        
        public String constructResponse(String label, String[] args, String sender, String channel, MessageEvent event) {
            //TODO replace placeholders and shit
            return response;
        }
    }
    
    interface CommandExecutor {
        public void execute(String label, String[] args, String sender, String channel, MessageEvent event);
    }
}
