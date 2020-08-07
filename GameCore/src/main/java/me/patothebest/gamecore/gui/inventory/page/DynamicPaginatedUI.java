package me.patothebest.gamecore.gui.inventory.page;

import com.google.common.collect.Iterators;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public abstract class DynamicPaginatedUI<T> extends GUIMultiPage {

    private final Supplier<Collection<? extends T>> listProvider;

    protected DynamicPaginatedUI(Plugin plugin, Player player, String rawName, Supplier<Collection<? extends T>> listProvider) {
        super(plugin, player, rawName, Math.min(54, Utils.transformToInventorySize(listProvider.get().size())));
        this.listProvider = listProvider;
    }

    protected DynamicPaginatedUI(Plugin plugin, Player player, ILang title, Supplier<Collection<? extends T>> listProvider) {
        super(plugin, player, title, Math.min(54, Utils.transformToInventorySize(listProvider.get().size())));
        this.listProvider = listProvider;
    }

    protected DynamicPaginatedUI(Plugin plugin, Player player, String rawName, Supplier<Collection<? extends T>> listProvider, int size) {
        super(plugin, player, rawName, size);
        this.listProvider = listProvider;
    }

    protected DynamicPaginatedUI(Plugin plugin, Player player, ILang title, Supplier<Collection<? extends T>> listProvider, int size) {
        super(plugin, player, title, size);
        this.listProvider = listProvider;
    }


    @SuppressWarnings("unchecked")
    @Override
    protected final void buildContent() {
        int slot = 0;
        Collection<? extends T> results = listProvider.get();
        final int start = pageSize * currentPage;
        final int end = Math.min(pageSize * (currentPage + 1), results.size());

        if (results instanceof List) {
            List<? extends T> list = (List<? extends T>) results;
            for (int index = start; index < end; index++) {
                addButton(createButton(list.get(index)), slot++);
            }
        } else {
            final Iterator<? extends T> iterator = results.iterator();
            for (int index = Iterators.advance(iterator, start); index < end; index++) {
                addButton(createButton(iterator.next()), slot++);
            }
        }
    }

    protected abstract GUIButton createButton(T item);

    @Override
    protected int getListCount() {
        if(listProvider.get().size() <= 45) {
            return -1;
        }

        return listProvider.get().size();
    }
}
