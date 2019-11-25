package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.*
import br.com.desafio.grupozap.utils.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

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
    fun refreshCachedLegalStatesSuccessTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(listRealStates())

                val result = mockUseCasesImpl.refreshCachedLegalStates()

                Assert.assertTrue("List should not be empty!", result)
            }
        }
    }

    @Test
    fun refreshCachedLegalStatesFailedTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(listIlegalRealStates())

                val result = mockUseCasesImpl.refreshCachedLegalStates()

                Assert.assertTrue("List should be empty!", !result)
            }
        }
    }

    @Test
    fun saveFiltersTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                val location = "Sao Paulo"
                val forSale = false
                val portal = "ZAP"
                val priceRate = 4
                val price = "4000"
                Mockito.`when`(mockRepository.saveFilter(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true)
                Mockito.`when`(mockRepository.getFilter(FilterType.LOCATION.toString()))
                    .thenReturn(location)
                Mockito.`when`(mockRepository.getFilter(FilterType.PORTAL.toString()))
                    .thenReturn(portal)
                Mockito.`when`(mockRepository.getFilter(FilterType.PRICE.toString()))
                    .thenReturn(price)
                Mockito.`when`(mockRepository.getFilter(FilterType.TYPE.toString()))
                    .thenReturn("RENTAL")

                mockUseCasesImpl.saveFilters(location, forSale, !forSale, portal, priceRate)
                val filterView = mockUseCasesImpl.getFilters()

                Assert.assertTrue("Location incorrect", filterView.location == location)
                Assert.assertTrue("Type incorrect", filterView.forSale == forSale)
                Assert.assertTrue("Portal incorrect %s".format(filterView.portal),
                    filterView.portal == portal)
                Assert.assertTrue("Price Rate incorrect %d".format(priceRate),
                    filterView.priceRate == priceRate)
                Assert.assertTrue("Price incorrect", filterView.priceLabel == price)
            }
        }
    }

    @Test
    fun getNextPageTest() {
        runBlocking {
            launch(Dispatchers.Main) {
                Mockito.`when`(mockRepository.getAllRealStates()).thenReturn(listRealStates())
                Mockito.`when`(mockRepository.saveFilter(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true)

                mockUseCasesImpl.refreshCachedLegalStates()
                mockUseCasesImpl.saveFilters("City", false, true, "ALL", 6)
                val result = mockUseCasesImpl.getNextPage(0)

                Assert.assertTrue("Empty list", result.size == 1)
                Assert.assertTrue("Correct Result", result[0].usableAreas == 35 && result[0].city == "City")
            }
        }
    }

    private fun listRealStates(): List<RealState> {
        val images1: MutableList<String> = ArrayList()
        images1.add("https://resizedimgs.vivareal.com/crop/400x300/vr.images.sp/some-id1.jpg")

        val validLocation = Location(-46.716542, -23.502555)
        val validGeoLocation = GeoLocation("ROOFTOP", validLocation)
        val validAddress = Address("City", "nwighboohood", validGeoLocation)
        val pricingInfos = PricingInfos(0, 5000, 5500, "RENTAL", 500)
        val legalRealState = RealState(35, "USED", "2016-11-16T04:14:02Z",
            "ACTIVE", "VALID_1", 1, "2016-11-16T04:14:02Z", false,
            images1, validAddress, 1, 1, pricingInfos, null)

        val legalList: MutableList<RealState> = ArrayList()
        legalList.add(legalRealState)
        return legalList
    }

    private fun listIlegalRealStates(): List<RealState>{
        val images1: MutableList<String> = ArrayList()
        images1.add("https://resizedimgs.vivareal.com/crop/400x300/vr.images.sp/some-id1.jpg")

        val invalidLocation = Location(0.0,0.0)
        val invalidGeoLocation = GeoLocation("", invalidLocation)
        val invalidAddress = Address("City", "", invalidGeoLocation)
        val pricingInfos = PricingInfos(0, 5000, 5500, "RENTAL", 500)
        val ilegalRealState = RealState(35, "USED", "2016-11-16T04:14:02Z",
            "ACTIVE", "VALID_1", 1, "2016-11-16T04:14:02Z", false,
            images1, invalidAddress, 1, 1, pricingInfos, null)

        val ilegalList: MutableList<RealState> = ArrayList()
        ilegalList.add(ilegalRealState)
        return ilegalList
    }
}