package org.example.demohhclone
import org.example.demohhclone.Dtos.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vacancies")
class VacancyController(
    private val vacancyService: VacancyService,
    private val vacancyMapper: VacancyMapper
) {

    @GetMapping
    fun getVacancies(
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "publishedAt") sortBy: String,
        @RequestParam(defaultValue = "DESC") direction: String
    ): ResponseEntity<Page<VacancyResponseDto>> {
        val sort = Sort.by(Sort.Direction.fromString(direction), sortBy)
        val pageable = PageRequest.of(page, size, sort)

        val vacancies = vacancyService.searchVacancies(name, pageable)
        return ResponseEntity.ok(vacancies)
    }

    @GetMapping("/{id}")
    fun getVacancyById(@PathVariable id: Long): ResponseEntity<VacancyResponseDto> {
        val vacancy = vacancyService.getVacancyById(id)
        return if (vacancy != null) {
            ResponseEntity.ok(vacancy)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/search")
    fun searchVacancies(
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<VacancyResponseDto>> {
        val pageable = PageRequest.of(page, size)
        val vacancies = vacancyService.searchVacancies(q, pageable)
        return ResponseEntity.ok(vacancies)
    }
}

@RestController
@RequestMapping("/api/request-objects")
class RequestObjectController(
    private val requestObjectService: RequestObjectService
) {

    @GetMapping
    fun getAllRequestObjects(): ResponseEntity<List<RequestObject>> {
        val requestObjects = requestObjectService.getAllRequestObjects()
        return ResponseEntity.ok(requestObjects)
    }

    @GetMapping("/{id}")
    fun getRequestObjectById(@PathVariable id: Long): ResponseEntity<RequestObject> {
        val requestObject = requestObjectService.getRequestObjectById(id)
        return if (requestObject != null) {
            ResponseEntity.ok(requestObject)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping
    fun createRequestObject(@RequestBody requestObject: RequestObject): ResponseEntity<Void> {
        requestObjectService.createRequestObject(requestObject)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @DeleteMapping("/{id}")
    fun deleteRequestObject(@PathVariable id: Long): ResponseEntity<Void> {
        requestObjectService.deleteRequestObject(id)
        return ResponseEntity.noContent().build()
    }
}