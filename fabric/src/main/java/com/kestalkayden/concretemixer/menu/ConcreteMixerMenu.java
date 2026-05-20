package com.kestalkayden.concretemixer.menu;

import com.kestalkayden.concretemixer.block.ConcreteMixerBlockEntity;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ConcreteMixerMenu extends AbstractContainerMenu {

    public static final int BE_SLOTS = ConcreteMixerBlockEntity.CONTAINER_SIZE;  // 5
    public static final int BE_SLOT_END = BE_SLOTS;                              // 5
    public static final int PLAYER_INV_START = BE_SLOT_END;                      // 5

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_WATER_MB = 1;
    public static final int DATA_MAX_PROGRESS = 2;
    public static final int DATA_STATUS = 3;
    public static final int DATA_SIZE = 4;

    // Layout — recipe-card / spec-sheet style. GUI is 176x184 to make room for section labels
    // and a status line under the arrow.
    public static final int GUI_W = 176;
    public static final int GUI_H = 184;

    // Section label Y positions
    public static final int LABEL_INPUTS_Y = 18;
    public static final int LABEL_WATER_Y = 48;
    public static final int LABEL_STATUS_Y = 78;

    // Top row: inputs + arrow + output
    public static final int INPUT_ROW_Y = 28;
    public static final int INPUT_X_START = 10;          // first of three input slots, 18px apart
    public static final int OUTPUT_SLOT_X = 144;
    public static final int OUTPUT_SLOT_Y = INPUT_ROW_Y;

    // Long horizontal arrow with triangular tip
    public static final int ARROW_X = 70;
    public static final int ARROW_Y = 32;
    public static final int ARROW_W = 56;                // includes triangle tip
    public static final int ARROW_H = 8;

    // Middle row: water slot + horizontal water bar spanning the GUI width
    public static final int WATER_SLOT_X = 10;
    public static final int WATER_SLOT_Y = 58;
    public static final int WATER_BAR_X = 32;
    public static final int WATER_BAR_Y = 62;
    public static final int WATER_BAR_W = 136;
    public static final int WATER_BAR_H = 8;

    public static final int PLAYER_INV_Y = 102;

    private final Container beContainer;
    private final ContainerData data;

    public ConcreteMixerMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv,
             new SimpleContainer(BE_SLOTS),
             new SimpleContainerData(DATA_SIZE));
    }

    public ConcreteMixerMenu(int containerId, Inventory playerInv, ConcreteMixerBlockEntity be) {
        this(containerId, playerInv, be, new ContainerData() {
            @Override public int get(int i) {
                return switch (i) {
                    case DATA_PROGRESS -> be.getProgress();
                    case DATA_WATER_MB -> be.getWaterMb();
                    case DATA_MAX_PROGRESS -> be.getActiveMixTicks();
                    case DATA_STATUS -> be.getStatusCode();
                    default -> 0;
                };
            }
            @Override public void set(int i, int v) { /* server-only state, no client writes */ }
            @Override public int getCount() { return DATA_SIZE; }
        });
    }

    private ConcreteMixerMenu(int containerId, Inventory playerInv,
                              Container beContainer, ContainerData data) {
        super(ConcreteMixerMenus.CONCRETE_MIXER_MENU, containerId);
        this.beContainer = beContainer;
        this.data = data;

        // 3 input slots, top row. mayPlace delegates to BE.canPlaceItem so non-recipe items
        // (cobble, sandstone, etc.) get rejected for drag/drop AND moveItemStackTo (shift-click).
        // Vanilla Slot.mayPlace returns true unconditionally — it does NOT call canPlaceItem —
        // so without this override the slot-level filter is bypassed entirely.
        for (int i = 0; i < 3; i++) {
            final int slotId = i;
            addSlot(new Slot(beContainer, slotId, INPUT_X_START + i * 18, INPUT_ROW_Y) {
                @Override public boolean mayPlace(ItemStack stack) {
                    return beContainer.canPlaceItem(slotId, stack);
                }
            });
        }
        // Water bucket slot — same mayPlace delegation.
        addSlot(new Slot(beContainer, ConcreteMixerBlockEntity.SLOT_WATER, WATER_SLOT_X, WATER_SLOT_Y) {
            @Override public boolean mayPlace(ItemStack stack) {
                return beContainer.canPlaceItem(ConcreteMixerBlockEntity.SLOT_WATER, stack);
            }
        });
        // Output slot — player can take but can't place. Mirrors furnace ResultSlot semantics.
        addSlot(new Slot(beContainer, ConcreteMixerBlockEntity.SLOT_OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        // Player inv 3 rows × 9
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, PLAYER_INV_Y + 58));
        }

        addDataSlots(data);
    }

    public int getProgress() { return data.get(DATA_PROGRESS); }
    public int getWaterMb() { return data.get(DATA_WATER_MB); }
    public int getMaxProgress() { return data.get(DATA_MAX_PROGRESS); }
    public int getStatusCode() { return data.get(DATA_STATUS); }

    @Override
    public boolean stillValid(Player player) {
        return beContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < BE_SLOT_END) {
            // BE → player inv (any direction shift-clicks works)
            if (!moveItemStackTo(stack, PLAYER_INV_START, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // Player inv → BE. Water buckets go to water slot; everything else tries input slots.
            int waterSlot = ConcreteMixerBlockEntity.SLOT_WATER;
            if (ConcreteMixerBlockEntity.isValidWaterInput(stack)) {
                if (!moveItemStackTo(stack, waterSlot, waterSlot + 1, false)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, 0, 3, false)) return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return original;
    }
}
