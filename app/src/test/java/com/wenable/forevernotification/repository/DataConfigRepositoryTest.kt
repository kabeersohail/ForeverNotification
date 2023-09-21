package com.wenable.forevernotification.repository

import android.content.Context
import android.util.Log
import com.wenable.downloadmanager.models.ConfigData
import com.wenable.forevernotification.extensions.isConfigDataAlreadyAvailable
import com.wenable.forevernotification.network.ApiService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataConfigRepositoryTest {

    @MockK(relaxed = true)
    lateinit var context: Context

    @MockK
    lateinit var apiService: ApiService

    @MockK
    lateinit var call: Call<List<ConfigData>>

    private lateinit var dataConfigRepository: DataConfigRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        dataConfigRepository = DataConfigRepository(context, apiService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when config data executes successfully, it must call handle api success`() {
        // Given
        val expectedResponse: List<ConfigData> = listOf(
            ConfigData(
                id = "randomId",
                name = "randomName.svg",
                type = "IMAGE",
                cdn_path = "www.google.com/random_path",
                sizeInBytes = 200L
            )
        )

        // Set up the behavior of apiService.getConfigData() to return callX
        every { apiService.getConfigData() } returns call

        // Set up the behavior of callX.enqueue() to simulate a successful API response
        every { call.enqueue(any()) } answers {
            val callback = firstArg<Callback<List<ConfigData>>>()
            val response = Response.success(expectedResponse)
            callback.onResponse(this@DataConfigRepositoryTest.call, response)
        }

        // Mock the behavior of isConfigDataAlreadyAvailable
        mockkStatic(ConfigData::isConfigDataAlreadyAvailable)
        every { expectedResponse.first().isConfigDataAlreadyAvailable(any()) } returns false

        // Mock the behavior of Log.e
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        // When
        dataConfigRepository.fetchConfigData()

        // Then
        // Verify that handleAPISuccess(expectedResponse) was called exactly once
        verify { dataConfigRepository.handleAPISuccess(expectedResponse) }
    }
}
