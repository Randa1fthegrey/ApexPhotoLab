package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.val_util as global_val_util

/**
 * Job: CVPS Jobs Value Utility.
 * Responsibility: Centralizing all shared and trajectory-based constants for the Job Manuals.
 * Uniformity Protocol: Every directory gets a val_util for hierarchy and deduplication.
 */
object val_util {

    // ==========================================
    // GLOBAL TRAJECTORY (Universal definitions)
    // ==========================================
    
    val SENTINEL = global_val_util.SENTINEL // * (Shared by Jobs: 2, 2_TEST, 5)

    val OFFSETS = global_val_util.OFFSETS // * (Shared by Jobs: 2, 2_TEST)

    // ==========================================
    // DISCOVERY TRAJECTORY (Job 2 & 2_TEST)
    // ==========================================

    val VACUUM_SEARCH_ORDER = intArrayOf(-2, -1, 0, 1, 2, 3, 4, -3)
    val RECOVERY_RADIUS = 3

    // ==========================================
    // CONSOLIDATION TRAJECTORY (Job 5)
    // ==========================================

    val REACH_RADIUS_DEFAULT = 10
    val REACH_RADIUS_WHITE = 5
    val REACH_RADIUS_ALPHA = 15
    val REACH_RADIUS_BLACK = 20
    val REACH_RADIUS_GREY = 15

    // ==========================================
    // MAINTENANCE TRAJECTORY (Job 7)
    // ==========================================

    val STITCH_RADIUS = 50
    val CLOSURE_RADIUS = 100
    val MIN_PATH_SIZE = 10
    val BORDER_BUFFER = 50

}
