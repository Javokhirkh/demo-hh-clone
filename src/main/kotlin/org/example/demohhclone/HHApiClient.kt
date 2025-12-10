package org.example.demohhclone

import org.example.demohhclone.Dtos.HHListResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "hhApiClient",
    url = "https://api.hh.ru"
)
interface HHApiClient {

    @GetMapping("/vacancies/")
    fun getVacancies(
        @RequestParam("text") text: String,
        @RequestParam("per_page") perPage: Int = 20,
        @RequestParam("page") page: Int = 0
    ): HHListResponseDto
}