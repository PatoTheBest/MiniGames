package me.patothebest.gamecore.gui.inventory.page;

import com.google.common.collect.Iterators;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.StainedGlassPane;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public abstract class DualListPage<T> extends GUIMultiPage {


    private final Supplier<Collection<? extends T>> leftListProvider;
    private final Supplier<Collection<? extends T>> rightListProvider;
    private int size = 0;

    protected DualListPage(Plugin plugin, Player player, String rawName, Supplier<Collection<? extends T>> leftListProvider, Supplier<Collection<? extends T>> rightListProvider) {
        super(plugin, player, rawName, 54);
        this.leftListProvider = leftListProvider;
        this.rightListProvider = rightListProvider;
        this.pageSize = 16;
    }

    protected DualListPage(Plugin plugin, Player player, ILang title, Supplier<Collection<? extends T>> leftListProvider, Supplier<Collection<? extends T>> rightListProvider) {
        super(plugin, player, title, 54);
        this.leftListProvider = leftListProvider;
        this.rightListProvider = rightListProvider;
        this.pageSize = 16;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void buildContent() {
        buildHeader();
        Collection<? extends T> results = leftListProvider.get();
        final int start = pageSize * currentPage;
        final int end = Math.min(pageSize * (currentPage + 1), results.size());

        int slot = 9;
        if (results instanceof List) {
            List<? extends T> list = (List<? extends T>) results;
            for (int index = start; index < end; index++) {
                addButton(createLeftButton(list.get(index)), (index - start)/4*5 + slot);
                slot++;
            }
        } else {
            final Iterator<? extends T> iterator = results.iterator();
            for (int index = Iterators.advance(iterator, start); index < end; index++) {
                addButton(createLeftButton(iterator.next()), (index - start)/4*5 + slot);
                slot++;
            }
        }

        Collection<? extends T> rightResults = rightListProvider.get();
        final int rightEnd = Math.min(pageSize * (currentPage + 1), rightResults.size());

        slot = 9;
        if (rightResults instanceof List) {
            List<? extends T> list = (List<? extends T>) rightResults;
            for (int index = start; index < rightEnd; index++) {
                addButton(createRightButton(list.get(index)), (index - start)/4*5 + slot + 5);
                slot++;
            }
        } else {
            final Iterator<? extends T> iterator = rightResults.iterator();
            for (int index = Iterators.advance(iterator, start); index < rightEnd; index++) {
                addButton(createRightButton(iterator.next()), (index - start)/4*5 + slot + 5);
                slot++;
            }
        }

        for (int i = 13; i < 45; i+=9) {
            addButton(new PlaceHolder(new ItemStackBuilder(StainedGlassPane.GRAY).name("")), i);
        }

        size = Math.max(results.size(), rightResults.size()) + 20;
        buildFooter();
    }

    protected abstract GUIButton createLeftButton(T item);

    protected abstract GUIButton createRightButton(T item);

    protected void buildHeader() {}

    protected void buildFooter() {}

    @Override
    protected int getListCount() {
        if (size <= 36) {
            return -1;
        }
        return size;
    }
}
