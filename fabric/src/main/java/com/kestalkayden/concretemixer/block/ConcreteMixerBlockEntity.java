package com.kestalkayden.concretemixer.block;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ConcreteMixerBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

    public static final int CONTAINER_SIZE = 5;
    public static final int SLOT_INPUT_A = 0;
    public static final int SLOT_INPUT_B = 1;
    public static final int SLOT_INPUT_C = 2;
    public static final int SLOT_WATER = 3;
    public static final int SLOT_OUTPUT = 4;

    public static final int TANK_CAPACITY_MB = 10_000;
    public static final int WATER_PER_CRAFT_MB = 1000;
    public static final int SAND_PER_CRAFT = 4;
    public static final int GRAVEL_PER_CRAFT = 4;
    public static final int DYE_PER_CRAFT = 1;
    public static final int OUTPUT_PER_CRAFT = 4;
    public static final int MIX_TICKS = 160;

    private static final int[] TOP_SLOTS = new int[]{SLOT_INPUT_A, SLOT_INPUT_B, SLOT_INPUT_C};
    private static final int[] SIDE_SLOTS = new int[]{SLOT_INPUT_A, SLOT_INPUT_B, SLOT_INPUT_C, SLOT_WATER};
    private static final int[] BOTTOM_SLOTS = new int[]{SLOT_OUTPUT};

    private static final Map<Item, DyeColor> DYE_TO_COLOR = Map.ofEntries(
        Map.entry(Items.WHITE_DYE,      DyeColor.WHITE),
        Map.entry(Items.ORANGE_DYE,     DyeColor.ORANGE),
        Map.entry(Items.MAGENTA_DYE,    DyeColor.MAGENTA),
        Map.entry(Items.LIGHT_BLUE_DYE, DyeColor.LIGHT_BLUE),
        Map.entry(Items.YELLOW_DYE,     DyeColor.YELLOW),
        Map.entry(Items.LIME_DYE,       DyeColor.LIME),
        Map.entry(Items.PINK_DYE,       DyeColor.PINK),
        Map.entry(Items.GRAY_DYE,       DyeColor.GRAY),
        Map.entry(Items.LIGHT_GRAY_DYE, DyeColor.LIGHT_GRAY),
        Map.entry(Items.CYAN_DYE,       DyeColor.CYAN),
        Map.entry(Items.PURPLE_DYE,     DyeColor.PURPLE),
        Map.entry(Items.BLUE_DYE,       DyeColor.BLUE),
        Map.entry(Items.BROWN_DYE,      DyeColor.BROWN),
        Map.entry(Items.GREEN_DYE,      DyeColor.GREEN),
        Map.entry(Items.RED_DYE,        DyeColor.RED),
        Map.entry(Items.BLACK_DYE,      DyeColor.BLACK)
    );

    private static final Map<Item, DyeColor> POWDER_TO_COLOR = Map.ofEntries(
        Map.entry(Items.WHITE_CONCRETE_POWDER,      DyeColor.WHITE),
        Map.entry(Items.ORANGE_CONCRETE_POWDER,     DyeColor.ORANGE),
        Map.entry(Items.MAGENTA_CONCRETE_POWDER,    DyeColor.MAGENTA),
        Map.entry(Items.LIGHT_BLUE_CONCRETE_POWDER, DyeColor.LIGHT_BLUE),
        Map.entry(Items.YELLOW_CONCRETE_POWDER,     DyeColor.YELLOW),
        Map.entry(Items.LIME_CONCRETE_POWDER,       DyeColor.LIME),
        Map.entry(Items.PINK_CONCRETE_POWDER,       DyeColor.PINK),
        Map.entry(Items.GRAY_CONCRETE_POWDER,       DyeColor.GRAY),
        Map.entry(Items.LIGHT_GRAY_CONCRETE_POWDER, DyeColor.LIGHT_GRAY),
        Map.entry(Items.CYAN_CONCRETE_POWDER,       DyeColor.CYAN),
        Map.entry(Items.PURPLE_CONCRETE_POWDER,     DyeColor.PURPLE),
        Map.entry(Items.BLUE_CONCRETE_POWDER,       DyeColor.BLUE),
        Map.entry(Items.BROWN_CONCRETE_POWDER,      DyeColor.BROWN),
        Map.entry(Items.GREEN_CONCRETE_POWDER,      DyeColor.GREEN),
        Map.entry(Items.RED_CONCRETE_POWDER,        DyeColor.RED),
        Map.entry(Items.BLACK_CONCRETE_POWDER,      DyeColor.BLACK)
    );

    private static final Map<DyeColor, Item> COLOR_TO_CONCRETE = Map.ofEntries(
        Map.entry(DyeColor.WHITE,      Items.WHITE_CONCRETE),
        Map.entry(DyeColor.ORANGE,     Items.ORANGE_CONCRETE),
        Map.entry(DyeColor.MAGENTA,    Items.MAGENTA_CONCRETE),
        Map.entry(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_CONCRETE),
        Map.entry(DyeColor.YELLOW,     Items.YELLOW_CONCRETE),
        Map.entry(DyeColor.LIME,       Items.LIME_CONCRETE),
        Map.entry(DyeColor.PINK,       Items.PINK_CONCRETE),
        Map.entry(DyeColor.GRAY,       Items.GRAY_CONCRETE),
        Map.entry(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_CONCRETE),
        Map.entry(DyeColor.CYAN,       Items.CYAN_CONCRETE),
        Map.entry(DyeColor.PURPLE,     Items.PURPLE_CONCRETE),
        Map.entry(DyeColor.BLUE,       Items.BLUE_CONCRETE),
        Map.entry(DyeColor.BROWN,      Items.BROWN_CONCRETE),
        Map.entry(DyeColor.GREEN,      Items.GREEN_CONCRETE),
        Map.entry(DyeColor.RED,        Items.RED_CONCRETE),
        Map.entry(DyeColor.BLACK,      Items.BLACK_CONCRETE)
    );

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    private int waterMb = 0;
    private int progress = 0;
    private boolean powered = false;

    public ConcreteMixerBlockEntity(BlockPos pos, BlockState state) {
        super(ConcreteMixerBlockEntities.CONCRETE_MIXER_BE, pos, state);
    }

    public int getWaterMb() { return waterMb; }
    public int getProgress() { return progress; }

    public void setPowered(boolean powered) {
        if (this.powered != powered) {
            this.powered = powered;
            setChanged();
        }
    }

    // --- Tick ---

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        // Redstone signal pauses operation; progress is preserved so removing the signal resumes
        // mid-batch rather than restarting. Bucket fill is also paused — full hopper feed should
        // be stoppable with a single lever.
        if (powered) return;

        boolean changed = false;

        // Bucket → tank fill (Phase 5). External capability path (mod canisters) is Phase 6.
        if (tryDrainBucket()) changed = true;

        // Try raw recipe first; if it doesn't match, fall back to the powder shortcut.
        Optional<DyeColor> rawColor = tryMatchRawRecipe();
        Optional<DyeColor> powderColor = rawColor.isPresent() ? Optional.empty() : tryMatchPowderRecipe();
        Optional<DyeColor> craftColor = rawColor.isPresent() ? rawColor : powderColor;
        boolean canCraft = craftColor.isPresent() && hasWaterAndOutputRoom(craftColor.get());

        if (canCraft) {
            progress++;
            if (progress >= MIX_TICKS) {
                if (rawColor.isPresent()) performRawCraft(rawColor.get());
                else performPowderCraft(powderColor.get());
                progress = 0;
            }
            changed = true;
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        boolean shouldBeLit = progress > 0;
        BlockState current = getBlockState();
        if (current.getValue(ConcreteMixerBlock.LIT) != shouldBeLit) {
            level.setBlock(pos, current.setValue(ConcreteMixerBlock.LIT, shouldBeLit), Block.UPDATE_ALL);
        }

        if (changed) {
            setChanged();
        }
    }

    // --- Recipe matching ---

    /** Returns the dye color iff the 3 input slots collectively contain ≥4 sand, ≥4 gravel,
     *  and exactly one kind of dye (in any number of slots, count ≥ 1). Otherwise empty. */
    private Optional<DyeColor> tryMatchRawRecipe() {
        int sandCount = countMatching(this::isSand);
        int gravelCount = countMatching(this::isGravel);
        if (sandCount < SAND_PER_CRAFT || gravelCount < GRAVEL_PER_CRAFT) return Optional.empty();

        DyeColor color = null;
        int dyeCount = 0;
        for (int slot = SLOT_INPUT_A; slot <= SLOT_INPUT_C; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;
            DyeColor c = DYE_TO_COLOR.get(stack.getItem());
            if (c == null) continue;
            if (color == null) color = c;
            else if (color != c) return Optional.empty();
            dyeCount += stack.getCount();
        }
        if (color == null || dyeCount < DYE_PER_CRAFT) return Optional.empty();
        return Optional.of(color);
    }

    /** Powder shortcut: ONE input slot holds ≥4 concrete powder, others empty. Mixed-with-powder
     *  arrangements (powder + sand, etc.) are intentionally rejected as ambiguous. */
    private Optional<DyeColor> tryMatchPowderRecipe() {
        DyeColor color = null;
        int powderCount = 0;
        for (int slot = SLOT_INPUT_A; slot <= SLOT_INPUT_C; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;
            DyeColor c = POWDER_TO_COLOR.get(stack.getItem());
            if (c == null) return Optional.empty();  // non-powder item present → not powder mode
            if (color != null && color != c) return Optional.empty();  // multiple colors → ambiguous
            color = c;
            powderCount += stack.getCount();
        }
        if (color == null || powderCount < OUTPUT_PER_CRAFT) return Optional.empty();
        return Optional.of(color);
    }

    private boolean hasWaterAndOutputRoom(DyeColor color) {
        if (waterMb < WATER_PER_CRAFT_MB) return false;
        Item concrete = COLOR_TO_CONCRETE.get(color);
        ItemStack output = items.get(SLOT_OUTPUT);
        if (output.isEmpty()) return true;
        if (!output.is(concrete)) return false;
        return output.getCount() + OUTPUT_PER_CRAFT <= output.getMaxStackSize();
    }

    // --- Crafting ---

    private void performRawCraft(DyeColor color) {
        consumeFromInputs(this::isSand, SAND_PER_CRAFT);
        consumeFromInputs(this::isGravel, GRAVEL_PER_CRAFT);
        consumeFromInputs(s -> DYE_TO_COLOR.get(s.getItem()) == color, DYE_PER_CRAFT);
        waterMb -= WATER_PER_CRAFT_MB;
        addToOutput(COLOR_TO_CONCRETE.get(color), OUTPUT_PER_CRAFT);
    }

    private void performPowderCraft(DyeColor color) {
        Item powderItem = items.get(SLOT_INPUT_A).getItem();  // any matching slot works
        // Find the actual powder item present (since we accept it in any of the 3 slots).
        for (int slot = SLOT_INPUT_A; slot <= SLOT_INPUT_C; slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty() && POWDER_TO_COLOR.get(stack.getItem()) == color) {
                powderItem = stack.getItem();
                break;
            }
        }
        final Item match = powderItem;
        consumeFromInputs(s -> s.is(match), OUTPUT_PER_CRAFT);
        waterMb -= WATER_PER_CRAFT_MB;
        addToOutput(COLOR_TO_CONCRETE.get(color), OUTPUT_PER_CRAFT);
    }

    private void addToOutput(Item item, int count) {
        ItemStack output = items.get(SLOT_OUTPUT);
        if (output.isEmpty()) {
            items.set(SLOT_OUTPUT, new ItemStack(item, count));
        } else {
            output.grow(count);
        }
    }

    /** Walks the 3 input slots and shrinks matching stacks until `count` has been consumed. */
    private void consumeFromInputs(Predicate<ItemStack> match, int count) {
        for (int slot = SLOT_INPUT_A; slot <= SLOT_INPUT_C && count > 0; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty() || !match.test(stack)) continue;
            int take = Math.min(count, stack.getCount());
            stack.shrink(take);
            count -= take;
        }
    }

    /** Drains one water bucket from the water slot into the tank, replacing with an empty bucket.
     *  Returns true iff a fill happened. Water buckets are max-stack-1, so the count==1 branch
     *  is the normal case; the else exists for defense against modded behavior. */
    private boolean tryDrainBucket() {
        ItemStack stack = items.get(SLOT_WATER);
        if (stack.isEmpty() || !stack.is(Items.WATER_BUCKET)) return false;
        if (waterMb + 1000 > TANK_CAPACITY_MB) return false;
        if (stack.getCount() == 1) {
            items.set(SLOT_WATER, new ItemStack(Items.BUCKET));
        } else {
            stack.shrink(1);
        }
        waterMb += 1000;
        return true;
    }

    private int countMatching(Predicate<ItemStack> match) {
        int total = 0;
        for (int slot = SLOT_INPUT_A; slot <= SLOT_INPUT_C; slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty() && match.test(stack)) total += stack.getCount();
        }
        return total;
    }

    private boolean isSand(ItemStack stack) { return stack.is(ItemTags.SAND); }

    /** v0.1 only accepts vanilla gravel — modded gravel tag support deferred to v0.2. */
    private boolean isGravel(ItemStack stack) { return stack.is(Items.GRAVEL); }

    // --- Container scaffolding ---

    @Override
    public int getContainerSize() { return CONTAINER_SIZE; }

    @Override
    protected NonNullList<ItemStack> getItems() { return items; }

    @Override
    protected void setItems(NonNullList<ItemStack> items) { this.items = items; }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new com.kestalkayden.concretemixer.menu.ConcreteMixerMenu(containerId, playerInventory, this);
    }

    // --- NBT ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, items);
        }
        output.putInt("WaterMb", waterMb);
        output.putInt("Progress", progress);
        output.putBoolean("Powered", powered);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        if (!tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, items);
        }
        this.waterMb = Math.max(0, Math.min(TANK_CAPACITY_MB, input.getIntOr("WaterMb", 0)));
        this.progress = Math.max(0, input.getIntOr("Progress", 0));
        this.powered = input.getBooleanOr("Powered", false);
    }

    // --- WorldlyContainer ---

    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> TOP_SLOTS;
            case DOWN -> BOTTOM_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        if (slot == SLOT_OUTPUT) return false;
        if (slot == SLOT_WATER) return isValidWaterInput(stack);
        return isValidInput(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        // Output: pull from below (vanilla hopper convention).
        // Water slot: pull empty buckets from the sides so a hopper full of water buckets can
        // continuously refill the tank, with empties draining back out automatically.
        if (slot == SLOT_OUTPUT && direction == Direction.DOWN) return true;
        if (slot == SLOT_WATER && stack.is(Items.BUCKET)) return true;
        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_OUTPUT) return false;
        if (slot == SLOT_WATER) return isValidWaterInput(stack);
        return isValidInput(stack);
    }

    /** Accepts only items that could actually participate in a recipe: sand (any variant via
     *  the sand tag), vanilla gravel, any vanilla dye, or any concrete powder. Anything else
     *  is rejected at the slot level so hoppers and shift-clicks can't stuff junk into a mixer. */
    public static boolean isValidInput(ItemStack stack) {
        return stack.is(ItemTags.SAND)
            || stack.is(Items.GRAVEL)
            || DYE_TO_COLOR.containsKey(stack.getItem())
            || POWDER_TO_COLOR.containsKey(stack.getItem());
    }

    /** Phase 6 will extend this to accept any fluid-storing item via capability lookup. */
    public static boolean isValidWaterInput(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET);
    }
}
