package me.patothebest.gamecore.chat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.IndexedFunction;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.*;

public class Pagination<T> {

    public static final int DEFAULT_PER_PAGE = 14;

    private int perPage = DEFAULT_PER_PAGE;
    private @Nullable
    String title;
    private IndexedFunction<? super T, String> formatter = (t, i) -> String.valueOf(t);

    public Pagination() {
        this(DEFAULT_PER_PAGE);
    }

    public Pagination(int perPage) {
        checkArgument(perPage > 0);
        this.perPage = perPage;
    }

    public Pagination<T> perPage(int perPage) {
        this.perPage = perPage;
        return this;
    }

    public Pagination<T> title(@Nullable String title) {
        this.title = title;
        return this;
    }

    public Pagination<T> entries(IndexedFunction<? super T, String> formatter) {
        this.formatter = checkNotNull(formatter);
        return this;
    }

    @SuppressWarnings("unchecked")
    public void display(CommandSender sender, Collection<? extends T> results, int page) {
        if (results.isEmpty()) {
            sender.sendMessage(CoreLang.NO_RESULTS.getMessage(sender));
            return;
        }

        final int pages = Utils.divideRoundingUp(results.size(), perPage);

        if (page < 1 || page > pages) {
            sender.sendMessage(CoreLang.INVALID_PAGRE.replace(sender, page, pages));
            return;
        }

        final int start = perPage * (page - 1);
        final int end = Math.min(perPage * page, results.size());

        sender.sendMessage(header(page, pages, sender));

        String subHeader = subHeader(page, pages);

        if (subHeader != null && !subHeader.isEmpty()) {
            sender.sendMessage(subHeader);
        }

        if (results instanceof List) {
            List<? extends T> list = (List<? extends T>) results;
            for (int index = start; index < end; index++) {
                multiEntry(list.get(index), index, sender).forEach(o -> {
                    if (o != null) {
                        sendMessage(sender, o);
                    }
                });
            }
        } else {
            final Iterator<? extends T> iterator = results.iterator();
            for (int index = Iterators.advance(iterator, start); index < end; index++) {
                multiEntry(iterator.next(), index, sender).forEach(o -> {
                    if (o != null) {
                        sendMessage(sender, o);
                    }
                });
            }
        }

        footer(page, pages, sender);
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message == null) {
            return;
        }

        sender.sendMessage(message);
    }

    public String header(int page, int pages, CommandSender sender) {
        String string = org.bukkit.ChatColor.GRAY.toString();

        String title = title();
        if (title != null) {
            string += title + ChatColor.BLUE;
        }

        string += CoreLang.PAGE.replace(sender, page, pages);
        return string;
    }

    public String subHeader(int page, int pages) {
        return "";
    }

    public void footer(int page, int pages, CommandSender sender) {
    }

    protected @Nullable
    String title() {
        return title;
    }

    protected String entry(T entry, int index, CommandSender commandSender) {
        return formatter.apply(entry, index);
    }

    protected List<? extends String> multiEntry(T entry, int index, CommandSender commandSender) {
        String entry1 = entry(entry, index, commandSender);
        return entry1 != null ? ImmutableList.of(entry1) : ImmutableList.of();
    }
}