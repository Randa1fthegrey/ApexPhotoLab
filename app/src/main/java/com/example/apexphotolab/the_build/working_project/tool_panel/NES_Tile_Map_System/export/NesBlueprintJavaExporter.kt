package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.export

/**
 * Job: NES Java Blueprint Code Exporter.
 * Responsibility: Generates a complete, ready-to-use NES_BIOS_Blueprint_Data.java source file
 * containing static byte arrays for PALETTE (16B), NAMETABLE (1,024B), and CHR_TILES (4,096B).
 */
object NesBlueprintJavaExporter {

    fun generateJavaClass(
        palette16: ByteArray,
        nametable1024: ByteArray,
        exRam1024: ByteArray,
        chrBytes: ByteArray,
        packageName: String = "com.example.dream_machine.nes.NES_Clean_Boot.NES_Boot_Screen"
    ): String {
        val sb = StringBuilder()

        sb.append("package ").append(packageName).append(";\n\n")
        sb.append("/**\n")
        sb.append(" * Clean-Room NES BIOS Blueprint Data generated automatically by ApexPhotoLab.\n")
        sb.append(" * Contains exact hardware byte arrays for PALETTE, NAMETABLE, EXRAM_ATTRIBUTES, and CHR_TILES.\n")
        sb.append(" */\n")
        sb.append("public class NES_BIOS_Blueprint_Data {\n\n")

        // 1. PALETTE (16 Bytes)
        sb.append("    // 16 Bytes: 4 Sub-Palettes x 4 NES Color Codes\n")
        sb.append("    public static final byte[] PALETTE = new byte[]{\n")
        sb.append("        ").append(formatByteArray(palette16, 4)).append("\n")
        sb.append("    };\n\n")

        // 2. NAMETABLE (1,024 Bytes)
        sb.append("    // 1,024 Bytes: 960 Lower Tile IDs (32x30 grid) + 64 Padding Bytes\n")
        sb.append("    public static final byte[] NAMETABLE = new byte[]{\n")
        sb.append("        ").append(formatByteArray(nametable1024, 16)).append("\n")
        sb.append("    };\n\n")

        // 3. EXRAM ATTRIBUTES (1,024 Bytes)
        sb.append("    // 1,024 Bytes: MMC5 ExRAM (Upper bits of Tile ID + 2-bit Attribute Palette per 8x8 tile)\n")
        sb.append("    public static final byte[] EXRAM_ATTRIBUTES = new byte[]{\n")
        sb.append("        ").append(formatByteArray(exRam1024, 16)).append("\n")
        sb.append("    };\n\n")

        // 4. CHR_TILES (Dynamic Size up to 15,360 Bytes)
        val numTiles = chrBytes.size / 16
        sb.append("    // ").append(chrBytes.size).append(" Bytes: ").append(numTiles).append(" Unique 8x8 Tiles x 16 Bytes per Tile\n")
        sb.append("    public static final byte[] CHR_TILES = new byte[]{\n")
        sb.append("        ").append(formatByteArray(chrBytes, 16)).append("\n")
        sb.append("    };\n")

        sb.append("}\n")

        return sb.toString()
    }

    private fun formatByteArray(bytes: ByteArray, bytesPerLine: Int): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = String.format("(byte)0x%02X", bytes[i].toInt() and 0xFF)
            sb.append(hex)
            if (i < bytes.size - 1) {
                sb.append(", ")
            }
            if ((i + 1) % bytesPerLine == 0 && i < bytes.size - 1) {
                sb.append("\n        ")
            }
        }
        return sb.toString()
    }
}
