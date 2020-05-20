package com.procurement.orchestrator.domain.util.extension

import com.procurement.orchestrator.domain.functional.Result
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CollectionExtensionTest {

    @Test
    fun isUnique() {
        val uniqueCollection = listOf(1, 2, 3)
        val notUniqueCollection = listOf(1, 1, 3)

        assertTrue(uniqueCollection.isUnique { it })
        assertFalse(notUniqueCollection.isUnique { it })
    }

    @Test
    fun toSetBy() {
        val collection = listOf(1, 2, 3)

        val actualCollection: Set<String> = collection.toSetBy { it.toString() }
        val expectedCollection = setOf("1", "2", "3")

        assertEquals(expectedCollection, actualCollection)
    }

    @Test
    fun mapToResult() {
        val collection = listOf(1, 2, 3)

        val successResult = collection.mapToResult {
            Result.success(it.toString())
        }

        val failureResult = collection.mapToResult {
            Result.failure("Error")
        }

        val expectedSuccessResult = listOf("1", "2", "3")

        assertTrue(successResult.isSuccess)
        assertEquals(expectedSuccessResult, successResult.get)

        assertTrue(failureResult.isFail)
        assertEquals("Error", failureResult.error)
    }

    @Test
    fun getUnknownElements() {
        val unknownElement = 3
        val knownElements = setOf(1, 2)
        val receivedElements = knownElements + unknownElement

        val unknownElements = getUnknownElements(received = receivedElements, known = knownElements)

        assertEquals(1, unknownElements.size)
        assertTrue(unknownElements.contains(unknownElement))
    }

    @Test
    fun getNewElements() {
        val newElement = 3
        val knownElements = setOf(1, 2)
        val receivedElements = knownElements + newElement

        val newElements = getNewElements(received = receivedElements, known = knownElements)

        assertEquals(1, newElements.size)
        assertTrue(newElements.contains(newElement))
    }

    @Test
    fun getElementsForUpdate() {
        val firstElementForUpdate = 1
        val secondElementForUpdate = 2
        val newElement = 3
        val knownElements = setOf(firstElementForUpdate, secondElementForUpdate)
        val receivedElements = knownElements + newElement

        val elementsForUpdate = getElementsForUpdate(received = receivedElements, known = knownElements)

        assertEquals(2, elementsForUpdate.size)
        assertTrue(elementsForUpdate.contains(firstElementForUpdate))
        assertTrue(elementsForUpdate.contains(secondElementForUpdate))
    }
}
