package me.MiniDigger.KatzenBot;

import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Funcionality for Pokemon Commands
 * Allows Users to request a pokemon to be named after them (or anything else)
 * Created by Martin on 01.10.2016.
 */
public class PokemonHandler {

    private Set<String> pokemon = new HashSet<>();
    private List<PokemonEntry> list = new CopyOnWriteArrayList<>();
    private boolean useSpellChecker = true;
    private SpellChecker spellChecker;

    public void init(CommandHandler handler) {
        try {
            Directory directory = FSDirectory.open(new File("./spellchecker/"));
            spellChecker = new SpellChecker(directory);
            spellChecker.indexDictionary(new PlainTextDictionary(new File("pokemonlist.txt")));
            System.out.println("Enabled spellchecker!");
        } catch (Exception ex) {
            ex.printStackTrace();
            useSpellChecker = false;
        }

        try (Scanner scan = new Scanner(new FileReader(new File("pokemonlist.txt")))) {
            while (scan.hasNext()) {
                pokemon.add(scan.nextLine().toLowerCase());
            }
            System.out.println("loaded " + pokemon.size() + " Pokemon");
        } catch (Exception ex) {
            System.err.println("error while loading pokemon");
            ex.printStackTrace();
        }

        loadPkm();

        handler.add(new CommandHandler.Command("!pokemon", "all", (label, args, sender, channel, event) -> {
            if (args.length < 2) {
                event.respond("usage: !pokemon <pokemon> <wunschnick>");
                return;
            }
            if (args[2].length() > 10) {
                event.respond("Nick ist zu lang! (max is 10) cmonBruh");
                return;
            }

            if (!pokemon.contains(args[1].toLowerCase())) {
                event.respond("Unbekanntes Pokemon!");
                if (useSpellChecker) {
                    try {
                        String[] suggestions = spellChecker.suggestSimilar(args[1].toLowerCase(), 1);
                        if (suggestions.length > 0) {
                            event.respond("Meintest du " + suggestions[0] + "?");
                        } else {
                            System.out.println("no suggestions for " + args[1].toLowerCase());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }

            Optional<PokemonEntry> old = get(sender);
            if (old.isPresent()) {
                PokemonEntry e = old.get();
                e.nickName = args[2];
                e.pokemonName = args[1];
                event.respond("Aktualisiert DxCat");
            } else {
                list.add(new PokemonEntry(sender, args[1], args[2]));
                event.respond("Hinzugefügt DxCat");
            }

            savePkm();
            list.forEach(System.out::println);
        }));
    }

    public void loadPkm() {
        try (Scanner scan = new Scanner(new FileInputStream(new File("pkm.txt")))) {
            while (scan.hasNext()) {
                String[] l = scan.next().split("%%%");
                if (l.length == 3) {
                    list.add(new PokemonEntry(l[0], l[1], l[2]));
                } else {
                    System.err.println("invalid pkm " + Arrays.toString(l));
                }
            }
        } catch (Exception ex) {
            System.err.println("error while loading ops");
            ex.printStackTrace();
        }
    }

    public void savePkm() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File("pkm.txt")))) {
            for (PokemonEntry entry : list) {
                writer.println(entry.twitchName + "%%%" + entry.pokemonName + "%%%" + entry.nickName);
            }
        } catch (Exception ex) {
            System.err.println("error while saving pkm");
            ex.printStackTrace();
        }
    }

    public Optional<PokemonEntry> get(String twitchUser) {
        for (PokemonEntry entry : list) {
            if (entry.twitchName.equalsIgnoreCase(twitchUser)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }


    class PokemonEntry {
        String twitchName;
        String pokemonName;
        String nickName;

        public PokemonEntry(String twitchName, String pokemonName, String nickName) {
            this.twitchName = twitchName;
            this.pokemonName = pokemonName;
            this.nickName = nickName;
        }

        @Override
        public String toString() {
            return "PokemonEntry[twitchName=" + twitchName + ",pokemonName=" + pokemonName + ",nickName=" + nickName + "]";
        }
    }
}
