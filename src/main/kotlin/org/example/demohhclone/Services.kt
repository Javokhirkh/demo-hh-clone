package org.example.demohhclone

import jakarta.transaction.Transactional
import org.example.demohhclone.Dtos.AreaDto
import org.example.demohhclone.Dtos.EmployementTypeDto
import org.example.demohhclone.Dtos.EmployerDto
import org.example.demohhclone.Dtos.ExcperienceDto
import org.example.demohhclone.Dtos.HHResponseDto
import org.example.demohhclone.Dtos.ProfessionalRoleDto
import org.example.demohhclone.Dtos.ScheduleDto
import org.example.demohhclone.Dtos.VacancyResponseDto
import org.example.demohhclone.Dtos.WorkingHoursDto
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.LocalDateTime


interface RequestObjectService {
    fun getAllRequestObjects(): List<RequestObject>
    fun getRequestObjectById(id: Long): RequestObject?
    fun getRequestObjectByName(name: String): RequestObject?
    fun createRequestObject(dto: RequestObject)
    fun deleteRequestObject(id: Long)
}

@Service
class RequestObjectServiceImpl(
    private val requestObjectRepository: RequestObjectRepository
) : RequestObjectService {
    override fun getAllRequestObjects(): List<RequestObject> {
        return requestObjectRepository.findAll()
    }

    override fun getRequestObjectById(id: Long): RequestObject? {
        return requestObjectRepository.findById(id).orElse(null)
    }

    override fun getRequestObjectByName(name: String): RequestObject? {
        return requestObjectRepository.findByNameAndDeletedFalse(name)
    }

    override fun createRequestObject(dto: RequestObject) {
        requestObjectRepository.save(dto)
    }

    override fun deleteRequestObject(id: Long) {
        requestObjectRepository.trash(id)
    }
}



interface VacancyService {
    fun searchVacancies(name: String, pageable: Pageable): Page<VacancyResponseDto>
    fun getVacancyById(id: Long): VacancyResponseDto
}

@Service
class VacancyServiceImpl(
    private val vacancyRepository: VacancyRepository,
    private val vacancyMapper: VacancyMapper,
    private val requestObjectService: RequestObjectService
) : VacancyService {


    override fun searchVacancies(searchText: String, pageable: Pageable): Page<VacancyResponseDto> {
        return vacancyRepository.findByRequestObjectAndDeletedFalse(searchText,pageable).map { vacancyMapper.toDto(it) }
    }

    override fun getVacancyById(id: Long): VacancyResponseDto {
        return vacancyMapper.toDto(vacancyRepository.findByIdAndDeletedFalse(id) ?: throw Exception())
    }
}


@Service
class UpdateDatabase(
    private val hhApiClient: HHApiClient,
    private val entitySaveService: EntitySaveService,
    private val requestObjectService: RequestObjectService,
    private val vacancyRepository: VacancyRepository,
    private val requestObjectVacancyRepository:RequestObjectVacancyRepository
) {
    @MeasureTime
    @Scheduled(fixedRate = 18_000_000)
    fun updateDatabase() {
        val startTime = LocalDateTime.now()

        requestObjectService.createRequestObject(RequestObject("java"))

        val requestObjects = requestObjectService.getAllRequestObjects()
        requestObjects.forEach { requestObject ->
            fetchAllVacancies(requestObject)
        }



        // hammasini update qilib bolgandan keyin lastmodified bilan
        // solishtirib update boshlanishidan oldin update bogan hamma vacancyni ochiradi
        deleteAllNotUsingObjects(startTime)
    }

    private fun deleteAllNotUsingObjects(startTime: LocalDateTime) {
        val notUsingVacancies = vacancyRepository.findAllNotDeletedAndBeforeModifiedDate(startTime)
        vacancyRepository.trashList(notUsingVacancies.map { it.id }.toList() as List<Long>)
    }


    fun fetchAllVacancies(requestObject: RequestObject, perPage: Int = 20) {

        val firstPage = hhApiClient.getVacancies(requestObject.name, perPage, 0)
        val totalPages = firstPage.pages


        processVacancies(firstPage.items,requestObject)

        for (page in 1 until totalPages) {

            val response = hhApiClient.getVacancies(requestObject.name, perPage, page)
            processVacancies(response.items,requestObject)



        }


    }

    private fun processVacancies(vacancies: List<HHResponseDto>,requestObject: RequestObject) {

        vacancies.forEach { vacancy ->
            try {
                val vacancy=entitySaveService.saveVacancy(vacancy)
                connectVacancyAndSearchObject(vacancy, requestObject)
            } catch (e: Exception) {
                println("Error processing vacancy ${vacancy.id}: ${e.message}")
            }
        }
    }

    private fun connectVacancyAndSearchObject(
        vacancy: Vacancy?,
        requestObject: RequestObject
    ) {
        requestObjectVacancyRepository.save(RequestObjectVacancy(requestObject,vacancy!!))
    }
}

@Service
class EntitySaveService(
    private val areaRepository: AreaRepository,
    private val employerRepository: EmployerRepository,
    private val scheduleRepository: ScheduleRepository,
    private val workingHoursRepository: WorkingHoursRepository,
    private val experienceRepository: ExperienceRepository,
    private val employmentTypeRepository: EmploymentTypeRepository,
    private val professionalRoleRepository: ProfessionalRoleRepository,
    private val vacancyProfessionalRoleRepository: VacancyProfessionalRoleRepository,
    private val vacancyRepository: VacancyRepository
) {

    @Transactional
    fun saveOrGetArea(areaDto: AreaDto): Area {
        return areaRepository.findByHhIdAndDeletedIsFalse(areaDto.id)
            ?: areaRepository.save(areaDto.toEntity())
    }


    @Transactional
    fun saveOrGetEmployer(employerDto: EmployerDto): Employer {
        return employerRepository.findByHhIdAndDeletedIsFalse(employerDto.id)
            ?: employerRepository.save(employerDto.toEntity())
    }

    @Transactional
    fun saveOrGetSchedule(scheduleDto: ScheduleDto?): Schedule? {
        if (scheduleDto == null) return null
        return scheduleRepository.findByHhIdAndDeletedIsFalse(scheduleDto.id)
            ?: scheduleRepository.save(scheduleDto.toEntity())
    }

    @Transactional
    fun saveOrGetWorkingHours(workingHoursDto: WorkingHoursDto?): WorkingHours? {
        if (workingHoursDto == null) return null
        return workingHoursRepository.findByHhIdAndDeletedIsFalse(workingHoursDto.id)
            ?: workingHoursRepository.save(workingHoursDto.toEntity())
    }

    @Transactional
    fun saveOrGetExperience(experienceDto: ExcperienceDto): Experience {

        return experienceRepository.findByHhIdAndDeletedIsFalse(experienceDto.id)
            ?: experienceRepository.save(experienceDto.toEntity())
    }

    @Transactional
    fun saveOrGetEmploymentType(employmentTypeDto: EmployementTypeDto): EmploymentType {
        return employmentTypeRepository.findByHhIdAndDeletedIsFalse(employmentTypeDto.id)
            ?: employmentTypeRepository.save(employmentTypeDto.toEntity())
    }

    @Transactional
    fun saveOrGetProfessionalRole(roleDto: ProfessionalRoleDto): ProfessionalRole {
        return professionalRoleRepository.findByHhIdAndDeletedIsFalse(roleDto.id)
            ?: professionalRoleRepository.save(roleDto.toEntity())
    }

    @Transactional
    fun saveVacancy(dto: HHResponseDto): Vacancy? {
        // Skip if essential data is missing
        /*if (dto.publishedAt == null || dto.experience == null || dto.employmentType == null) {
            println("Skipping vacancy ${dto.id} due to missing required fields")
            return null
        }*/

        val area = saveOrGetArea(dto.area!!)
        val employer = saveOrGetEmployer(dto.employer!!)
        val schedule = saveOrGetSchedule(dto.schedule)
        val workingHours = saveOrGetWorkingHours(dto.workingHours)
        val experience = saveOrGetExperience(dto.experience!!)
        val employmentType = saveOrGetEmploymentType(dto.employment?: EmployementTypeDto("test","null"))

        val existingVacancy = vacancyRepository.findByHhIdAndDeletedIsFalse(dto.id)

        val updated = dto.toEntity(area, employer, schedule, workingHours, experience, employmentType)
        updated.id = existingVacancy?.id
        updated.modifiedDate = LocalDateTime.now()

        val savedVacancy = vacancyRepository.save(updated)

        if (existingVacancy != null) {
            updateVacancyProfessionalRoles(existingVacancy, dto, savedVacancy)
        } else if (!dto.professionalRoles.isNullOrEmpty()) {
            dto.professionalRoles.forEach { roleDto ->
                val role = saveOrGetProfessionalRole(roleDto)
                vacancyProfessionalRoleRepository.save(
                    VacancyProfessionalRole(vacancy = savedVacancy, professionalRole = role)
                )
            }
        }

        return savedVacancy
    }

    private fun updateVacancyProfessionalRoles(
        existingVacancy: Vacancy,
        dto: HHResponseDto,
        saved: Vacancy
    ) {
        val oldRoles = vacancyProfessionalRoleRepository.findByVacancyId(existingVacancy.id!!)
        val existingRoleIds = oldRoles.map { it.professionalRole.hhId }.toSet()
        val newRoleIds = dto.professionalRoles?.map { it.id }?.toSet() ?: emptySet()

        val rolesToDelete = oldRoles.filter { it.professionalRole.hhId !in newRoleIds }
        rolesToDelete.forEach {
            vacancyProfessionalRoleRepository.delete(it)
        }

        val rolesToAdd = dto.professionalRoles?.filter { it.id !in existingRoleIds } ?: emptyList()
        rolesToAdd.forEach { roleDto ->
            val role = saveOrGetProfessionalRole(roleDto)
            vacancyProfessionalRoleRepository.save(
                VacancyProfessionalRole(vacancy = saved, professionalRole = role)
            )
        }
    }
}