package org.example.demohhclone

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.function.ServerResponse.async

@FeignClient(name = "hhClient", url = "https://api.hh.ru")
interface HhClient {
    @GetMapping("/vacancies")
    fun searchVacancies(
        @RequestParam("text") text: String,
        @RequestParam("page") page: Int = 0,
        @RequestParam("per_page") perPage: Int = 20
    )
}

@Service
class HhService(
    private val hhClient: HhClient,
    private val vacancyRepository: VacancyRepository
) {

    private val BATCH_SIZE = 20
    private val MAX_PARALLEL_REQUESTS = 10 // limit parallelism to 10

    suspend fun loadTenPercentStreaming(text: String) = coroutineScope {

        // 1️⃣ Load first page to get total pages
        val firstPage = withContext(Dispatchers.IO) { hhClient.searchVacancies(text, 0) }
        val totalPages = firstPage.pages
        val tenPercentPages = maxOf(1, totalPages / 10)

        println("Fetching $tenPercentPages pages in parallel (10% of $totalPages)")

        val channel = Channel<HhVacancy>(capacity = BATCH_SIZE * 2)

        // 2️⃣ Producer: fetch pages in parallel and send vacancies to channel
        launch {
            val semaphore = Semaphore(MAX_PARALLEL_REQUESTS)
            (0 until tenPercentPages).map { page ->
                async(Dispatchers.IO) {
                    semaphore.acquire()
                    try {
                        val items = hhClient.searchVacancies(text, page).items
                        items.forEach { channel.send(it) }
                    } finally {
                        semaphore.release()
                    }
                }
            }.awaitAll()
            channel.close() // signal that no more vacancies are coming
        }

        val buffer = mutableListOf<VacancyEntity>()
        for (vacancy in channel) {
            buffer.add(vacancy.toEntity())
            if (buffer.size >= BATCH_SIZE) {
                vacancyRepository.saveAll(buffer)
                println("Saved batch of ${buffer.size} vacancies")
                buffer.clear()
            }
        }
        if (buffer.isNotEmpty()) {
            vacancyRepository.saveAll(buffer)
            println("Saved last batch of ${buffer.size} vacancies")
        }

    }


}
