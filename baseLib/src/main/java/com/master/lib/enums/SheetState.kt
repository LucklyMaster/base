package com.master.lib.enums

/**
 * [com.master.lib.view.SheetLayout]的状态
 * @author: MasterChan
 * @date: 2022-09-13 21:13
 */
enum class SheetState(val values: Int) {
    DRAGGING(0),
    FOLD(1),
    DISPLAY(2),
    EXPAND(3);

    companion object {
        fun convert2State(values: Int): SheetState {
            return when (values) {
                0 -> DRAGGING
                1 -> FOLD
                2 -> DISPLAY
                3 -> EXPAND
                else -> FOLD
            }
        }
    }
}