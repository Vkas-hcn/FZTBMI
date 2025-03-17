package com.ling.ding.sighed.limt

interface AdLimitRule {
    fun checkLimit(isCanUp:Boolean): Boolean
    fun reset()
    fun recordEvent()
}
