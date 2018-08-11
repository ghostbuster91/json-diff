package io.ghostbuster91.jsondiff

import org.junit.Assert
import org.junit.Test

class DifferTest {
    private val REMOVED_STRING = "**Removed**"

    @Test
    fun shouldReturnTrueForTwoIdenticalJsons() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
         "id": 1
        }""".trimIndent()
        Assert.assertEquals(emptyList<DiffResult>(), compare(first, second))
    }

    @Test
    fun shouldDetectValueDifference() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
         "id": 2
        }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".id",
                firstValue = 1.0,
                secondValue = 2.0,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf("id" to 2.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingKey() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
        }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".id",
                firstValue = 1.0,
                secondValue = null,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf()
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingKeyInFirst() {
        val first = """{
        }""".trimIndent()

        val second = """{
         "id": 1
        }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".id",
                firstValue = null,
                secondValue = 1.0,
                firstObject = mapOf(),
                secondObject = mapOf("id" to 1.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectDifferenceRecursively() {
        val first = """{
         "id": {
            "key": "1"
         }
        }""".trimIndent()

        val second = """{
         "id": {
            "key": "2"
         }
        }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".id.key",
                firstValue = "1",
                secondValue = "2",
                firstObject = mapOf("key" to "1"),
                secondObject = mapOf("key" to "2")
        ), compare(first, second).first())
    }


    @Test
    fun shouldDetectDifferenceInListOfObjects() {
        val first = """{ "items":[
        {
         "id": 1
         }
        ]}""".trimIndent()

        val second = """{ "items":[
        {
         "id": 2
         }
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items[].id",
                firstValue = 1.0,
                secondValue = 2.0,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf("id" to 2.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectTypesMismatch() {
        val first = """{ "items":
        {
         "id": 1
         }
        }""".trimIndent()

        val second = """{ "items":[
        {
         "id": 2
         }
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items",
                firstValue = "object",
                secondValue = "list",
                firstObject = mapOf("items" to mapOf("id" to 1.0)),
                secondObject = mapOf("items" to listOf(mapOf("id" to 2.0)))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectTypesMismatch2() {
        val first = """{ "items":
        {
         "id": 1
         }
        }""".trimIndent()

        val second = """{ "items": 1 }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items",
                firstValue = "object",
                secondValue = "primitive",
                firstObject = mapOf("items" to mapOf("id" to 1.0)),
                secondObject = mapOf("items" to 1.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectDifferenceInListOfPrimitives() {
        val first = """{ "items":[
         1,2,3
        ]}""".trimIndent()

        val second = """{ "items":[
         1,2,4
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items[]",
                firstValue = 3.0,
                secondValue = 4.0,
                firstObject = mapOf("items" to listOf(1.0, 2.0, 3.0)),
                secondObject = mapOf("items" to listOf(1.0, 2.0, 4.0))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingItemInListOfPrimitives() {
        val first = """{ "items":[
         1,2,3
        ]}""".trimIndent()

        val second = """{ "items":[
         1,2
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items[]",
                firstValue = 3.0,
                secondValue = null,
                firstObject = mapOf("items" to listOf(1.0, 2.0, 3.0)),
                secondObject = mapOf("items" to listOf(1.0, 2.0))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingItemInListOfPrimitivesMirrored() {
        val first = """{ "items":[
         1,2
        ]}""".trimIndent()

        val second = """{ "items":[
         1,2,3
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items[]",
                firstValue = null,
                secondValue = 3.0,
                firstObject = mapOf("items" to listOf(1.0, 2.0)),
                secondObject = mapOf("items" to listOf(1.0, 2.0, 3.0))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectNestedListDifferences() {
        val first = """{ "items":[
         [1,2]
        ]}""".trimIndent()

        val second = """{ "items":[
         [1,2,3]
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = ".items[][]",
                firstValue = null,
                secondValue = 3.0,
                firstObject = mapOf("items" to listOf(listOf(1.0, 2.0))),
                secondObject = mapOf("items" to listOf(listOf(1.0, 2.0, 3.0)))
        ), compare(first, second).first())
    }

    @Test
    fun complexTest() {
        val first = """{ "items":
        [{
         "id": 2,
         "labels": ["2l1"]
         },
         {
         "id": 3,
         "labels": ["3l1", "3l2"]
         }]
        }""".trimIndent()

        val second = """{ "items":
        [{
         "id": 1,
         "labels": ["2l1"]
         },
         {
         "id": 3,
         "labels": ["3l1", "3l3"]
         }]
        }""".trimIndent()
        Assert.assertEquals(setOf(DiffResult(
                key = ".items[].id",
                firstValue = 2.0,
                secondValue = 1.0,
                firstObject = mapOf("id" to 2.0, "labels" to listOf("2l1")),
                secondObject = mapOf("id" to 1.0, "labels" to listOf("2l1"))
        ), DiffResult(
                key = ".items[].labels[]",
                firstValue = "3l2",
                secondValue = "3l3",
                firstObject = mapOf("id" to 3.0, "labels" to listOf("3l1", "3l2")),
                secondObject = mapOf("id" to 3.0, "labels" to listOf("3l1", "3l3"))
        )
        ), compare(first, second).toSet())
    }

    @Test
    fun shouldMatchItemsWithinListByProperty() {
        val firstJson = """{ "items":
        [{
         "id": 2
         },
         {
         "id": 3
         }]
        }""".trimIndent()

        val secondJson = """{ "items":
        [{
         "id": 3
         },
         {
         "id": 2
         }]
        }""".trimIndent()
        val propertyBasedListCombiner = createPropertyBasedListCombiner("id")
        val listCombinerMapping = mapOf(".items[]" to propertyBasedListCombiner).withDefault { orderBasedListCombiner }
        Assert.assertEquals(emptyList<DiffResult>(), compare(firstJson, secondJson, listCombinerMapping))
    }

    @Test
    fun propertyBasedListCombinerShouldBeAppliedOnlyToParticularLevel() {
        val firstJson = """{ "items":
        [{
         "id": 2,
         "sub":[{
            "id":1
            },{
            "id":2
            }]
         }]
        }""".trimIndent()

        val secondJson = """{ "items":
        [{
         "id": 2,
         "sub":[{
            "id":2
            },{
            "id":1
            }]
         }]
        }""".trimIndent()
        val propertyBasedListCombiner = createPropertyBasedListCombiner("id")
        Assert.assertEquals(listOf(
                DiffResult(".items[].sub[].id", 1.0, 2.0, mapOf("id" to 1.0), mapOf("id" to 2.0)),
                DiffResult(".items[].sub[].id", 2.0, 1.0, mapOf("id" to 2.0), mapOf("id" to 1.0))),
                compare(firstJson, secondJson, mapOf(".items[]" to propertyBasedListCombiner).withDefault { orderBasedListCombiner }))
    }

    @Test
    fun shouldMatchItemsWithinListByPropertyAndHandleMisses() {
        val firstJson = """{ "items":
        [{
         "id": 3
         }]
        }""".trimIndent()

        val secondJson = """{ "items":
        [{
         "id": 3
         },
         {
         "id": 2
         }]
        }""".trimIndent()
        val propertyBasedListCombiner = createPropertyBasedListCombiner("id")
        val listCombinerMapping = mapOf(".items[]" to propertyBasedListCombiner).withDefault { orderBasedListCombiner }
        Assert.assertEquals(listOf(
                DiffResult(
                        key = ".items[]",
                        firstValue = null,
                        secondValue = mapOf("id" to 2.0),
                        firstObject = mapOf("items" to listOf(mapOf("id" to 3.0))),
                        secondObject = mapOf("items" to listOf(mapOf("id" to 3.0), mapOf("id" to 2.0)))
                )),
                compare(firstJson, secondJson, listCombinerMapping))
    }
}

