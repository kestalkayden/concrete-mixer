package com.kestalkayden.concretemixer.client;

import java.util.List;

import com.kestalkayden.concretemixer.block.ConcreteMixerBlockEntity;
import com.kestalkayden.concretemixer.menu.ConcreteMixerMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** Recipe-card / spec-sheet layout. Three labeled sections (Inputs / Water / Status) plus the
 *  standard player inventory below. Programmatically rendered — no PNG asset needed. */
public class ConcreteMixerScreen extends AbstractContainerScreen<ConcreteMixerMenu> {

    private static final int BG_GRAY     = 0xFFC6C6C6;
    private static final int BG_BORDER   = 0xFF555555;
    // Vanilla-style inset bevel — dark top/left, light bottom/right around a flat interior.
    private static final int SLOT_BEVEL_DARK  = 0xFF373737;
    private static final int SLOT_BEVEL_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_INNER       = 0xFF8B8B8B;
    private static final int LABEL_COLOR      = 0xFF404040;
    private static final int TANK_EMPTY  = 0xFF2A2A2A;
    private static final int TANK_FILL   = 0xFF3F76E4;
    private static final int TANK_EDGE   = 0xFF1A3F8A;
    private static final int ARROW_BG    = 0xFF646464;
    private static final int ARROW_FG    = 0xFF55FFAA;
    private static final int WATER_SLOT_TINT = 0xFF6E8FBC;   // bluish-tint backplate for water input
    private static final int OUTPUT_SLOT_TINT = 0xFF6E9C5A;  // greenish backplate for output

    public ConcreteMixerScreen(ConcreteMixerMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, ConcreteMixerMenu.GUI_W, ConcreteMixerMenu.GUI_H);
        // Pin the inventory label to vanilla's standard "above the player inventory" spot.
        this.inventoryLabelY = ConcreteMixerMenu.PLAYER_INV_Y - 10;
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractTooltip(g, mouseX, mouseY);
        // Water bar hover tooltip — only show when not hovering a slot and not dragging an item.
        if (hoveredSlot == null && menu.getCarried().isEmpty() && isHoveringWaterBar(mouseX, mouseY)) {
            List<Component> lines = List.of(
                Component.translatable("gui.concretemixer.tooltip.water_title").withStyle(ChatFormatting.AQUA),
                Component.literal(menu.getWaterMb() + " / " + ConcreteMixerBlockEntity.TANK_CAPACITY_MB + " mB")
                    .withStyle(ChatFormatting.GRAY)
            );
            g.setTooltipForNextFrame(font, lines, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isHoveringWaterBar(int mouseX, int mouseY) {
        int tx = leftPos + ConcreteMixerMenu.WATER_BAR_X;
        int ty = topPos + ConcreteMixerMenu.WATER_BAR_Y;
        return mouseX >= tx && mouseX < tx + ConcreteMixerMenu.WATER_BAR_W
            && mouseY >= ty && mouseY < ty + ConcreteMixerMenu.WATER_BAR_H;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = leftPos;
        int y = topPos;
        int w = ConcreteMixerMenu.GUI_W;
        int h = ConcreteMixerMenu.GUI_H;

        // Background panel + thin border
        g.fill(x, y, x + w, y + h, BG_GRAY);
        g.fill(x, y, x + w, y + 1, BG_BORDER);
        g.fill(x, y + h - 1, x + w, y + h, BG_BORDER);
        g.fill(x, y, x + 1, y + h, BG_BORDER);
        g.fill(x + w - 1, y, x + w, y + h, BG_BORDER);

        // Slot wells — vanilla-style chiseled inset bevel. Water slot tinted blue, output
        // tinted green. (Tried an oversized output well but vanilla's per-slot sprite in 26.1
        // masks any custom background larger than the standard 18×18, so we stick with standard.)
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            int wellX = x + slot.x - 1;
            int wellY = y + slot.y - 1;
            int innerColor = SLOT_INNER;
            if (slot.container instanceof ConcreteMixerBlockEntity) {
                if (slot.getContainerSlot() == ConcreteMixerBlockEntity.SLOT_WATER) {
                    innerColor = WATER_SLOT_TINT;
                } else if (slot.getContainerSlot() == ConcreteMixerBlockEntity.SLOT_OUTPUT) {
                    innerColor = OUTPUT_SLOT_TINT;
                }
            }
            drawSlotWell(g, wellX, wellY, 18, 18, innerColor);
        }

        // Horizontal water bar (replaces vertical one)
        int tx = x + ConcreteMixerMenu.WATER_BAR_X;
        int ty = y + ConcreteMixerMenu.WATER_BAR_Y;
        int tw = ConcreteMixerMenu.WATER_BAR_W;
        int th = ConcreteMixerMenu.WATER_BAR_H;
        g.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, TANK_EDGE);
        g.fill(tx, ty, tx + tw, ty + th, TANK_EMPTY);
        int waterMb = menu.getWaterMb();
        int fillW = (tw * waterMb) / ConcreteMixerBlockEntity.TANK_CAPACITY_MB;
        if (fillW > 0) {
            g.fill(tx, ty, tx + fillW, ty + th, TANK_FILL);
        }
        // Tick marks every 1000 mB (10 segments) — vertical hairlines on top of fill
        for (int i = 1; i < 10; i++) {
            int tickX = tx + (tw * i) / 10;
            g.fill(tickX, ty, tickX + 1, ty + th, TANK_EDGE);
        }

        // Real arrow shape — shaft + triangular tip pointing at the output slot.
        // tipLen=4 caps the loop at i=3; iterations 4-5 with the old tipLen=6 produced a
        // degenerate fill that MC's coord-normalizing fill turned into a stray 2x1 pixel.
        int ax = x + ConcreteMixerMenu.ARROW_X;
        int ay = y + ConcreteMixerMenu.ARROW_Y;
        int aw = ConcreteMixerMenu.ARROW_W;
        int ah = ConcreteMixerMenu.ARROW_H;
        int tipLen = 4;
        int shaftEnd = ax + aw - tipLen;

        // BG shaft
        g.fill(ax, ay + 2, shaftEnd, ay + ah - 2, ARROW_BG);
        // BG tip triangle (receding 1-px columns from shaft end to point)
        for (int i = 0; i < tipLen; i++) {
            g.fill(shaftEnd + i, ay + i, shaftEnd + i + 1, ay + ah - i, ARROW_BG);
        }

        // FG progress overlay across the same shape
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        int filledW = maxProgress > 0 ? (aw * progress) / maxProgress : 0;
        if (filledW > 0) {
            int fgShaftEnd = Math.min(shaftEnd, ax + filledW);
            g.fill(ax, ay + 2, fgShaftEnd, ay + ah - 2, ARROW_FG);
            int tipFilled = Math.max(0, (ax + filledW) - shaftEnd);
            for (int i = 0; i < Math.min(tipFilled, tipLen); i++) {
                g.fill(shaftEnd + i, ay + i, shaftEnd + i + 1, ay + ah - i, ARROW_FG);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractLabels(g, mouseX, mouseY);
        g.text(font, Component.translatable("gui.concretemixer.label.inputs").getString(),
            8, ConcreteMixerMenu.LABEL_INPUTS_Y, LABEL_COLOR, false);
        g.text(font, Component.translatable("gui.concretemixer.label.water").getString(),
            8, ConcreteMixerMenu.LABEL_WATER_Y, LABEL_COLOR, false);

        // Status: "Status:" label in neutral gray, then the colored state word next to it
        String statusPrefix = Component.translatable("gui.concretemixer.label.status").getString();
        g.text(font, statusPrefix, 8, ConcreteMixerMenu.LABEL_STATUS_Y, LABEL_COLOR, false);
        int code = menu.getStatusCode();
        String statusWord = Component.translatable(statusKey(code)).getString();
        int statusX = 8 + font.width(statusPrefix) + 4;
        g.text(font, statusWord, statusX, ConcreteMixerMenu.LABEL_STATUS_Y, statusColor(code), false);
    }

    /** Chiseled inset slot — top+left edges dark (panel shadow falling into the recess),
     *  bottom+right edges white (recess catching light), flat interior. Matches vanilla container
     *  slot styling. Top/bottom rows span full width to keep TR corner dark + BL corner light. */
    private static void drawSlotWell(GuiGraphicsExtractor g, int wx, int wy, int ww, int wh, int innerColor) {
        g.fill(wx, wy, wx + ww, wy + 1, SLOT_BEVEL_DARK);                  // top edge (incl. corners)
        g.fill(wx, wy + wh - 1, wx + ww, wy + wh, SLOT_BEVEL_LIGHT);       // bottom edge (incl. corners)
        g.fill(wx, wy + 1, wx + 1, wy + wh - 1, SLOT_BEVEL_DARK);          // left edge (between corners)
        g.fill(wx + ww - 1, wy + 1, wx + ww, wy + wh - 1, SLOT_BEVEL_LIGHT); // right edge
        g.fill(wx + 1, wy + 1, wx + ww - 1, wy + wh - 1, innerColor);       // interior
    }

    private static String statusKey(int code) {
        return switch (code) {
            case ConcreteMixerBlockEntity.STATUS_MIXING_RAW    -> "gui.concretemixer.status.mixing_raw";
            case ConcreteMixerBlockEntity.STATUS_MIXING_POWDER -> "gui.concretemixer.status.mixing_powder";
            case ConcreteMixerBlockEntity.STATUS_NEED_WATER    -> "gui.concretemixer.status.need_water";
            case ConcreteMixerBlockEntity.STATUS_OUTPUT_FULL   -> "gui.concretemixer.status.output_full";
            case ConcreteMixerBlockEntity.STATUS_COLOR_MISMATCH-> "gui.concretemixer.status.color_mismatch";
            case ConcreteMixerBlockEntity.STATUS_POWERED       -> "gui.concretemixer.status.powered";
            case ConcreteMixerBlockEntity.STATUS_INVALID       -> "gui.concretemixer.status.invalid";
            default                                            -> "gui.concretemixer.status.idle";
        };
    }

    private static int statusColor(int code) {
        return switch (code) {
            case ConcreteMixerBlockEntity.STATUS_MIXING_RAW,
                 ConcreteMixerBlockEntity.STATUS_MIXING_POWDER -> 0xFF3FA13C;  // green
            case ConcreteMixerBlockEntity.STATUS_NEED_WATER,
                 ConcreteMixerBlockEntity.STATUS_OUTPUT_FULL,
                 ConcreteMixerBlockEntity.STATUS_INVALID       -> 0xFFB83C3C;  // red
            case ConcreteMixerBlockEntity.STATUS_COLOR_MISMATCH-> 0xFFD08A2C;  // orange/gold
            case ConcreteMixerBlockEntity.STATUS_POWERED       -> 0xFF4060B8;  // blue
            default                                            -> 0xFF606060;  // gray (idle)
        };
    }
}
