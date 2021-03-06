package io.ghostbuster91.jsondiff

import org.junit.Assert
import org.junit.Test

class DifferTest {
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

    @Test
    fun shouldCalculateMismatchWhenBothValuesAreDifferentPrimitives() {
        val firstJson = """{
          "created_at": 1533970194318,
          "sequence": 3385
        }
        """.trimIndent()
        val secondJson = """{
          "created_at": "other",
          "sequence": 3385
        }
        """.trimIndent()
        Assert.assertEquals(listOf(DiffResult(
                key = ".created_at",
                firstValue = 1533970194318.0,
                secondValue = "other",
                firstObject = mapOf("created_at" to 1533970194318.0, "sequence" to 3385.0),
                secondObject = mapOf("created_at" to "other", "sequence" to 3385.0)
        )), compare(firstJson, secondJson))
    }

    @Test
    fun shouldDetectTypeMismatchEvenWhenValuesAreTheSame() {
        val firstJson = """{
          "created_at": 1533970194318,
          "sequence": 3385
        }
        """.trimIndent()
        val secondJson = """{
          "created_at": "1533970194318",
          "sequence": 3385
        }
        """.trimIndent()
        Assert.assertEquals(listOf(DiffResult(
                key = ".created_at",
                firstValue = 1533970194318.0,
                secondValue = "1533970194318",
                firstObject = mapOf("created_at" to 1533970194318.0, "sequence" to 3385.0),
                secondObject = mapOf("created_at" to "1533970194318", "sequence" to 3385.0)
        )), compare(firstJson, secondJson))
    }

    @Test
    fun shouldReportOnlyNotMatchedItemsWhenMatchingIsProvidedForWholeItems_Primitives() {
        val firstJson = """{
          "items": [1,2,3,4,5]
        }
        """.trimIndent()
        val secondJson = """{
          "items": [1,3,4,5]
        }
        """.trimIndent()
        Assert.assertEquals(1, compare(firstJson, secondJson, mapOf(".items[]" to createPropertyBasedListCombiner("."))).size)
    }

    @Test
    fun shouldReportOnlyNotMatchedItemsWhenMatchingIsProvidedForWholeItems_Objects() {
        val firstJson = """{
          "items": [
            {"id":1},
            {"id":2},
            {"id":3},
            {"id":4},
            {"id":5}
          ]
        }
        """.trimIndent()
        val secondJson = """{
          "items": [
            {"id":1},
            {"id":3},
            {"id":4},
            {"id":5}
          ]
        }
        """.trimIndent()
        Assert.assertEquals(1, compare(firstJson, secondJson, mapOf(".items[]" to createPropertyBasedListCombiner("."))).size)
    }

    @Test
    fun shouldSupportJsonPathAsKeyForListComparator() {
        val firstJson = """{
          "items": [
            {"nested":{"id":1}},
            {"nested":{"id":2}}
          ]
        }
        """.trimIndent()
        val secondJson = """{
          "items": [
            {"nested":{"id":2}},
            {"nested":{"id":1, "other_key":"not_important"}}
          ]
        }
        """.trimIndent()
        Assert.assertEquals(listOf(DiffResult(
                key = ".items[].nested.other_key",
                firstValue = null,
                secondValue = "not_important",
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf("id" to 1.0, "other_key" to "not_important")
        )), compare(firstJson, secondJson, mapOf(".items[]" to createPropertyBasedListCombiner(".nested.id"))))
    }
}

