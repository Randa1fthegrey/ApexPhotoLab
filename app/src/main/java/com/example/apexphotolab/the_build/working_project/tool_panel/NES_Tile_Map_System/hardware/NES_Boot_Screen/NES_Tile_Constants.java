package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.hardware.NES_Boot_Screen;

/**
 * Job: NES Hardware Tile Index Constants.
 * Responsibility: Centralizing tile index definitions for PCB circuitry, framing rails, and logo glyphs.
 */
public class NES_Tile_Constants {

    // Circuit & Framing Tiles (0x00..0x1F)
    public static final int TILE_BLANK = 0x00;
    public static final int TILE_BORDER_VERT = 0x01;
    public static final int TILE_BORDER_HORIZ = 0x02;
    public static final int TILE_BORDER_TL = 0x03;
    public static final int TILE_BORDER_TR = 0x04;
    public static final int TILE_BORDER_BL = 0x05;
    public static final int TILE_BORDER_BR = 0x06;
    public static final int TILE_DOUBLE_BAR = 0x07;
    public static final int TILE_TRACE_VERT = 0x08;
    public static final int TILE_TRACE_CURVE_L = 0x09;
    public static final int TILE_TRACE_CURVE_R = 0x0A;
    public static final int TILE_IC_TOP_L = 0x0B;
    public static final int TILE_IC_TOP_R = 0x0C;
    public static final int TILE_IC_BOT_L = 0x0D;
    public static final int TILE_IC_BOT_R = 0x0E;
    public static final int TILE_GOLD_VIA = 0x0F;
    public static final int TILE_OCT_CAP_TL = 0x10;
    public static final int TILE_OCT_CAP_TR = 0x11;
    public static final int TILE_OCT_CAP_BL = 0x12;
    public static final int TILE_OCT_CAP_BR = 0x13;
    public static final int TILE_OCT_SIDE_VERT = 0x14;
    public static final int TILE_TRACE_SWEEP_IN_L = 0x15;
    public static final int TILE_TRACE_SWEEP_IN_R = 0x16;

    // Logo 3D Metallic Glyph Tiles (0x80..0x9F)
    public static final int TILE_LOGO_D_TOP = 0x80;
    public static final int TILE_LOGO_D_BOT = 0x81;
    public static final int TILE_LOGO_R_TOP = 0x82;
    public static final int TILE_LOGO_R_BOT = 0x83;
    public static final int TILE_LOGO_E_TOP = 0x84;
    public static final int TILE_LOGO_E_BOT = 0x85;
    public static final int TILE_LOGO_A_TOP = 0x86;
    public static final int TILE_LOGO_A_BOT = 0x87;
    public static final int TILE_LOGO_M_TOP = 0x88;
    public static final int TILE_LOGO_M_BOT = 0x89;
    public static final int TILE_LOGO_C_TOP = 0x8A;
    public static final int TILE_LOGO_C_BOT = 0x8B;
    public static final int TILE_LOGO_H_TOP = 0x8C;
    public static final int TILE_LOGO_H_BOT = 0x8D;
    public static final int TILE_LOGO_I_TOP = 0x8E;
    public static final int TILE_LOGO_I_BOT = 0x8F;
    public static final int TILE_LOGO_N_TOP = 0x90;
    public static final int TILE_LOGO_N_BOT = 0x91;
}
