package com.worldstack;

import java.util.Arrays;

public class ClientDimensionData {

    public static final int SIZE = 8;

    private static int baseX;
    private static int baseZ;

    private static int[] netherBlocks = new int[0];
    private static int[] endBlocks = new int[0];

    public static void update(
        int x,
        int z,
        int[] nether,
        int[] end
    ) {
        baseX = x;
        baseZ = z;

        netherBlocks = Arrays.copyOf(nether, nether.length);
        endBlocks = Arrays.copyOf(end, end.length);
    }

    public static int getBaseX() {
        return baseX;
    }

    public static int getBaseZ() {
        return baseZ;
    }

    public static int[] getNetherBlocks() {
        return netherBlocks;
    }

    public static int[] getEndBlocks() {
        return endBlocks;
    }

    public static void clear() {
        netherBlocks = new int[0];
        endBlocks = new int[0];
    }
}
