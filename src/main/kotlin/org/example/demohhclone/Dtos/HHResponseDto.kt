package org.example.demohhclone.Dtos

import java.time.LocalDateTime

data class HHListResponseDto(
    val items: List<HHResponseDto>,
    val found: Int,
    val pages: Int,
    val perPage: Int,
    val page: Int
)

data class HHResponseDto(
    val id: Long,
    val name: String,
    val area: AreaDto,
    val type: TypeDto,
    val salaryRange: SalaryRangeDto?,
    val publishedAt: LocalDateTime,
    val url: String,
    val employer: EmployerDto,
    val snippet: SnippetDto?,
    val schedule: ScheduleDto?,
    val workingHours: WorkingHoursDto?,
    val professionalRoles: List<ProfessionalRoleDto>?,
    val experience : ExcperienceDto,
    val employment: EmployementDto,

)

data class EmployementDto (
    val id: String,
    val name: String
)

data class ExcperienceDto (
    val id: String,
    val name: String
)

data class ProfessionalRoleDto(
    val id: Long,
    val name: String
)

data class WorkingHoursDto(
    val id: String,
    val name: String
)

data class ScheduleDto(
    val id: String,
    val name: String
)

data class SnippetDto(
    val requirement: String?,
    val responsibility: String?
)

data class EmployerDto (
    val id:Long,
    val name:String,
    val url:String,
    val countryId:Long,
)

data class TypeDto(
    val id: String,
    val name: String
)


data class SalaryRangeDto (
    val from: Int?,
    val to: Int?,
    val currency: String?,
    val gross: Boolean?
)

data class AreaDto(
    val id: String,
    val name: String,
    val url: String
)
