package kaufland.com.demo.mapper

import kaufland.com.coachbasebinderapi.PersistenceConfig
import kaufland.com.coachbasebinderapi.TypeConversion
import kaufland.com.demo.entity.ProductEntity
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.math.BigDecimal
import kotlin.reflect.KClass

class DummyMapperSourceTest {

    @Before
    fun init() {
        PersistenceConfig.configure(object : PersistenceConfig.Connector {
            override val typeConversions: Map<KClass<*>, TypeConversion>
                get() = mapOf()

            override fun getDocument(id: String, dbName: String): Map<String, Any>? {
                return null
            }

            override fun queryDoc(dbName: String, queryParams: Map<String, Any>): List<Map<String, Any>> {
                return emptyList()
            }

            override fun deleteDocument(id: String, dbName: String) {
                //nope
            }

            override fun upsertDocument(document: MutableMap<String, Any>, id: String?, dbName: String) {
                //nope
            }

        })
    }

    @Test
    fun `check toMap`() {
        val mapper = DummyMapperSourceMapper()
        val obj = DummyMapperSource("oldValue").apply {
            bigDecimalValue = BigDecimal.valueOf(1.2)
            booleanValue = true
            product = ProductEntity.create().builder().setName("Foo").exit()
            testSerializable = DummyMapperSource.TestSerializable("Bar", 1)
            liveData.exposedVal = "myLiveVal"
            nullableList.add("1")
        }
        val mapToPersist = mapper.toMap(obj)

        val newObj = DummyMapperSource("newValue")

        assertEquals("newValue", newObj.privateValExpose)
        assertEquals("newValue", newObj.testSerializable.test1)
        mapper.fromMap(newObj, mapToPersist)

        newObj.apply {
            assertEquals("oldValue", privateValExpose)
            assertEquals("Bar", testSerializable.test1)
            assertEquals(1, testSerializable.test2)
            assertEquals(BigDecimal.valueOf(1.2), bigDecimalValue)
            assertEquals(true, booleanValue)
            assertEquals("Foo", product!!.name)
            assertEquals("oldValue", newObj.innerObject.myString)
            assertEquals("oldValue", newObj.innerObjectList[0].myString)
            assertEquals("oldValue", newObj.innerObjectMap["test"]!!.myString)
            assertEquals("myLiveVal", newObj.liveData.exposedVal)
            assertEquals(2, newObj.nullableList.size)
            assertEquals(null, newObj.nullableList[0])
        }
    }
}
