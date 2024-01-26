package com.rickclephas.kmp.nsexceptionkt.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class ThrowableReasonTests {

    private fun testReasonNoCause(message: String?, appendCausedBy: Boolean) {
        val exception = Exception(message)
        val reason = exception.getReason(appendCausedBy)
        assertEquals(message, reason)
    }

    @Test
    fun testReasonNoCauseDontAppend() = testReasonNoCause("Test message", false)

    @Test
    fun testReasonNoCauseAppend() = testReasonNoCause("Test message", true)

    @Test
    fun testReasonNoMessageNoCauseDontAppend() = testReasonNoCause(null, false)

    @Test
    fun testReasonNoMessageNoCauseAppend() = testReasonNoCause(null, true)

    @Test
    fun testReasonWithCauseDontAppend() {
        val cause = Exception("Cause message")
        val message = "Test message"
        val exception = Exception(message, cause)
        val reason = exception.getReason(false)
        assertEquals(message, reason)
    }

    @Test
    fun testReasonWithCause() {
        val cause = Exception("Cause message")
        val exception = Exception("Test message", cause)
        val reason = exception.getReason(true) ?: fail("No reason")
        assertTrue(reason.startsWith("""
            Test message
            Caused by: kotlin.Exception: Cause message
        """.trimIndent()))
    }

    @Test
    fun testReasonNoMessageWithCause() {
        val cause = Exception("Cause message")
        val exception = Exception(null, cause)
        val reason = exception.getReason(true) ?: fail("No reason")
        assertTrue(reason.startsWith("""
            Caused by: kotlin.Exception: Cause message
        """.trimIndent()))
    }

    @Test
    fun testReasonNoCauseMessage() {
        val exception = Exception("Test message", Exception())
        val reason = exception.getReason(true) ?: fail("No reason")
        assertTrue(reason.startsWith("""
            Test message
            Caused by: kotlin.Exception
        """.trimIndent()))
    }

    @Test
    fun testReasonWithDoubleCause() {
        val cause1 = Exception("Cause1 message")
        val cause2 = Exception("Cause2 message", cause1)
        val exception = Exception("Test message", cause2)
        val reason = exception.getReason(true) ?: fail("No reason")
        assertTrue(reason.contains("Test message"))
        assertTrue(reason.contains("Caused by: kotlin.Exception: Cause2 message"))
        assertTrue(reason.contains("Caused by: kotlin.Exception: Cause1 message"))
    }
}
