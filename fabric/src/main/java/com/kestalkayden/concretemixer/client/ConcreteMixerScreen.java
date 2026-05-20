package com.kestalkayden.concretemixer.client;

import com.kestalkayden.concretemixer.block.ConcreteMixerBlockEntity;
import com.kestalkayden.concretemixer.menu.ConcreteMixerMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** Programmatically-rendered background — no PNG asset required for v0.1. Polish pass in Phase 9. */
public class ConcreteMixerScreen extends AbstractContainerScreen<ConcreteMixerMenu> {

    private static final int GUI_W = 176;
    private static final int GUI_H = 166;

    // Color palette — vanilla container-ish gray
    private static final int BG_GRAY     = 0xFFC6C6C6;
    private static final int BG_BORDER   = 0xFF555555;
    private static final int SLOT_BORDER = 0xFF373737;
    private static final int SLOT_INNER  = 0xFF8B8B8B;
    private static final int LABEL_COLOR = 0xFF404040;
    private static final int TANK_EMPTY  = 0xFF2A2A2A;
    private static final int TANK_FILL   = 0xFF3F76E4;  // water blue
    private static final int TANK_EDGE   = 0xFF1A3F8A;
    private static final int ARROW_BG    = 0xFF646464;
    private static final int ARROW_FG    = 0xFF55FFAA;

    public ConcreteMixerScreen(ConcreteMixerMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, GUI_W, GUI_H);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = leftPos;
        int y = topPos;

        // Background panel
        g.fill(x, y, x + GUI_W, y + GUI_H, BG_GRAY);
        g.fill(x, y, x + GUI_W, y + 1, BG_BORDER);
        g.fill(x, y + GUI_H - 1, x + GUI_W, y + GUI_H, BG_BORDER);
        g.fill(x, y, x + 1, y + GUI_H, BG_BORDER);
        g.fill(x + GUI_W - 1, y, x + GUI_W, y + GUI_H, BG_BORDER);

        // Slot wells (1px border around each)
        for (Slot slot : menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            g.fill(sx, sy, sx + 18, sy + 18, SLOT_BORDER);
            g.fill(sx + 1, sy + 1, sx + 17, sy + 17, SLOT_INNER);
        }

        // Water tank well (taller vertical bar)
        int tx = x + ConcreteMixerMenu.WATER_BAR_X;
        int ty = y + ConcreteMixerMenu.WATER_BAR_Y;
        int tw = ConcreteMixerMenu.WATER_BAR_W;
        int th = ConcreteMixerMenu.WATER_BAR_H;
        g.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, TANK_EDGE);
        g.fill(tx, ty, tx + tw, ty + th, TANK_EMPTY);

        // Water fill — bottom-up
        int waterMb = menu.getWaterMb();
        int fillH = (th * waterMb) / ConcreteMixerBlockEntity.TANK_CAPACITY_MB;
        if (fillH > 0) {
            g.fill(tx, ty + (th - fillH), tx + tw, ty + th, TANK_FILL);
        }

        // Tick marks every 1000 mB (10 segments total)
        for (int i = 1; i < 10; i++) {
            int tickY = ty + (th * i) / 10;
            g.fill(tx, tickY, tx + tw, tickY + 1, TANK_EDGE);
        }

        // Progress arrow
        int ax = x + ConcreteMixerMenu.ARROW_X;
        int ay = y + ConcreteMixerMenu.ARROW_Y;
        int aw = ConcreteMixerMenu.ARROW_W;
        g.fill(ax, ay, ax + aw, ay + 4, ARROW_BG);
        int progress = menu.getProgress();
        int progressW = (aw * progress) / ConcreteMixerBlockEntity.MIX_TICKS;
        if (progressW > 0) {
            g.fill(ax, ay, ax + progressW, ay + 4, ARROW_FG);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractLabels(g, mouseX, mouseY);
        // Default labels include the GUI title at top + inventory label above player inv — fine.
    }
}
