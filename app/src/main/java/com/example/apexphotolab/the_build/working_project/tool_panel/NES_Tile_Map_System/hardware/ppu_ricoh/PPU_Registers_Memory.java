package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.hardware.ppu_ricoh;

// Holds Ricoh 2C02 PPU registers, latches, frame buffers, and internal memory arrays.
public class PPU_Registers_Memory {

    private static final String TAG = "PPU_Registers_Memory";

    // 256x240 ARGB Frame Buffer
    public final int[] frameBuffer = new int[256 * 240];

    // Master 64-color NTSC Palette Table (32-bit ARGB) - Authentic NTSC Colors
    public static final int[] PALETTE_64 = new int[]{
            0xFF666666, 0xFF002A88, 0xFF1412A7, 0xFF3B00A4, 0xFF5C007E, 0xFF6E0040, 0xFF6C0600, 0xFF561D00,
            0xFF333500, 0xFF0B4800, 0xFF005200, 0xFF004F08, 0xFF00404D, 0xFF000000, 0xFF000000, 0xFF000000,
            0xFFB4B4B4, 0xFF0063C5, 0xFF0038EC, 0xFF7B00EF, 0xFFA500B5, 0xFFB70056, 0xFFB21400, 0xFF8B3200,
            0xFF575C00, 0xFF197500, 0xFF008100, 0xFF007E16, 0xFF007077, 0xFF000000, 0xFF000000, 0xFF000000,
            0xFFECECEC, 0xFF3CB6FE, 0xFF6C83FE, 0xFFD35BFE, 0xFFFE5BFE, 0xFFFE5B99, 0xFFFE6E3B, 0xFFE08C00,
            0xFF9B9B00, 0xFF52BF00, 0xFF24C700, 0xFF00C752, 0xFF00B6C5, 0xFF000000, 0xFF000000, 0xFF000000,
            0xFFFFFFFF, 0xFFA6E2FE, 0xFFB9C8FE, 0xFFE8C8FE, 0xFFFEC8FE, 0xFFFEC8DB, 0xFFFEC8B5, 0xFFF5D7A1,
            0xFFD8E4A1, 0xFFB2F0A1, 0xFF9EE8B5, 0xFF9EE8E8, 0xFF9EE8FE, 0xFF000000, 0xFF000000, 0xFF000000
    };

    // Internal PPU Memory
    public final byte[][] nameTable = new byte[2][1024]; // 2KB internal VRAM
    public final byte[] paletteTable = new byte[32];     // Palette RAM ($3F00-$3F1F)
    public final byte[] oam = new byte[256];              // Primary OAM (64 sprites x 4 bytes)
    public byte[] chrRom = new byte[8192];               // 8KB CHR-ROM / Pattern Tables

    // PPU Registers ($2000-$2007)
    public int ppuControl = 0;  // $2000
    public int ppuMask = 0;     // $2001
    public int ppuStatus = 0;   // $2002
    public int oamAddr = 0;     // $2003

    // PPU Scrolling & Address Latches ($2005 / $2006)
    public boolean addressLatch = false;
    public int vramAddr = 0;
    public int tramAddr = 0;
    public int fineX = 0;
    public int ppuDataBuffer = 0;

    // Timing & Interconnect
    public int scanline = -1;
    public int cycle = 0;
    public boolean nmiRequested = false;
    public boolean mirrorHorizontal = false;
    public volatile boolean frameComplete = false;

    public void clearInternalMemory() {
        java.util.Arrays.fill(frameBuffer, 0);
        java.util.Arrays.fill(paletteTable, (byte) 0);
        java.util.Arrays.fill(oam, (byte) 0);
        java.util.Arrays.fill(chrRom, (byte) 0);
        for (int t = 0; t < 2; t++) {
            java.util.Arrays.fill(nameTable[t], (byte) 0);
        }
        ppuControl = 0;
        ppuMask = 0;
        ppuStatus = 0;
        oamAddr = 0;
        vramAddr = 0;
        tramAddr = 0;
        fineX = 0;
        ppuDataBuffer = 0;
        addressLatch = false;
    }

    public int countNonZeroTiles() {
        int count = 0;
        for (int t = 0; t < 2; t++) {
            for (int i = 0; i < 1024; i++) {
                if ((nameTable[t][i] & 0xFF) != 0) count++;
            }
        }
        return count;
    }
}
