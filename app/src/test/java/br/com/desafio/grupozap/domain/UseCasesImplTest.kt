package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.*
import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

@RunWith(MockitoJUnitRunner::class)
class UseCasesImplTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Mock
    private lateinit var mockRepository: DataRepository
    @InjectMocks
    private lateinit var mockUseCasesImpl: UseCasesImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun refreshCachedLegalStatesTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                val state = createRandomData()
                val data: MutableList<RealState> = ArrayList()
                data.add(state)
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(data.toList())

                val resultOk = mockUseCasesImpl.refreshCachedLegalStates()

                assertTrue("CachedLegalRealStates Error", resultOk)

                val resultList = mockUseCasesImpl.getByFilter(EnumMap(FilterType::class.java))

                assertTrue("Wrong result size: %d".format(resultList.size), resultList.size == 1)
                assertTrue("Wrong state id: %s".format(resultList[0].id), resultList[0].id == state.id)
            }
        }
    }

    @Test
    fun getByFilterNoFilterTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                val state1 = createRandomData()
                val state2 = createRandomData()
                val data: MutableList<RealState> = ArrayList()
                data.add(state1)
                data.add(state2)
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(data.toList())

                val resultOk = mockUseCasesImpl.refreshCachedLegalStates()

                assertTrue("CachedLegalRealStates Error", resultOk)

                val resultList = mockUseCasesImpl.getByFilter(EnumMap(FilterType::class.java))

                assertTrue("Wrong result size: %d".format(resultList.size), resultList.size == 1)
                assertTrue("Wrong state id: %s".format(resultList[0].id), resultList[0].id == state1.id)
            }
        }
    }

    @Test
    fun getByFilterWithOneFilterTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                val state1 = createRandomData()
                val state2 = createRandomData()
                val data: MutableList<RealState> = ArrayList()
                data.add(state1)
                data.add(state2)
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(data.toList())

                val resultOk = mockUseCasesImpl.refreshCachedLegalStates()

                assertTrue("CachedLegalRealStates Error", resultOk)

                val filterMap: MutableMap<FilterType, String> = EnumMap(FilterType::class.java)
                filterMap[FilterType.LOCATION] = "City"

                val resultList = mockUseCasesImpl.getByFilter(filterMap.toMap())

                assertTrue("Wrong result size: %d".format(resultList.size), resultList.size == 2)
            }
        }
    }

    @Test
    fun getByFilterWithMoreFiltersTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                val state1 = createRandomData()
                val state2 = createRandomData()
                val data: MutableList<RealState> = ArrayList()
                data.add(state1)
                data.add(state2)
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(data.toList())

                val resultOk = mockUseCasesImpl.refreshCachedLegalStates()

                assertTrue("CachedLegalRealStates Error", resultOk)

                val filterMap: MutableMap<FilterType, String> = EnumMap(FilterType::class.java)
                filterMap[FilterType.BEDROOMS] = state1.bedrooms.toString()

                val resultList = mockUseCasesImpl.getByFilter(filterMap)

                var resultSize = 1
                if (state1.bedrooms == state2.bedrooms) {
                    resultSize = 2
                }

                assertTrue("Wrong result size: %d".format(resultList.size), resultList.size == resultSize)
                assertTrue("Wrong state id: %s".format(resultList[0].id), resultList[0].id == state1.id)
            }
        }
    }

    private fun createRandomData(): RealState {
        val loc = Location(Random.nextDouble(0.0, 99.9), Random.nextDouble(0.0, 99.9))
        val geo = GeoLocation("TEST", loc)
        val address = Address("City", "neighborhood", geo)
        val pricingInfos = PricingInfos(Random.nextInt(50,150), Random.nextInt(100000,900000),
            Random.nextInt(1000,10000), BusinessType.SALE.toString(), "0")
        return RealState(30, "", "",  "", Random.nextInt().toString(),
            1, "", false, ArrayList(), address, 1, Random.nextInt(1,6),
            pricingInfos)
    }
}