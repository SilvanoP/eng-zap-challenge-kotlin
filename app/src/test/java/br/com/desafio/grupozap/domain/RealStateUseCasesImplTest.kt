package br.com.desafio.grupozap.domain

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RealStateUseCasesImplTest {

    @Mock
    private lateinit var mockRepository: DataRepository
    @InjectMocks
    private lateinit var mockRealStateUseCasesImpl: RealStateUseCasesImpl

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {
    }

    @Test
    fun refreshCachedLegalStates() {
    }

    @Test
    fun clearFilter() {
    }

    @Test
    fun getByFilter() {
    }
}