package org.example.demohhclone.Dtos

import java.time.LocalDateTime

data class VacancyResponseDto(
    val id: Long?,
    val hhId: Long?,
    val name: String,
    val publishedAt: LocalDateTime,
    val url: String,
    val requirement: String?,
    val responsibility: String?,
    val fromAmount: Int?,
    val toAmount: Int?,
    val currency: String?,
    val gross: Boolean?,
    val createdDate: LocalDateTime?,
    val modifiedDate: LocalDateTime?,

    val area: AreaResponseDto,
    val employer: EmployerResponseDto,
    val schedule: ScheduleResponseDto?,
    val workingHours: WorkingHoursResponseDto?,
    val experience: ExperienceResponseDto,
    val employmentType: EmploymentTypeResponseDto
)

data class AreaResponseDto(
    val id: Long?,
    val hhId: String,
    val name: String,
    val url: String,

)

data class EmployerResponseDto(
    val id: Long?,
    val hhId: Long,
    val name: String,
    val url: String,
    val countryId: Long,

)

data class ScheduleResponseDto(
    val id: Long?,
    val hhId: String,
    val name: String,

)

data class WorkingHoursResponseDto(
    val id: Long?,
    val hhId: String,
    val name: String,

)

data class ExperienceResponseDto(
    val id: Long?,
    val hhId: String,
    val name: String,
)

data class EmploymentTypeResponseDto(
    val id: Long?,
    val hhId: String,
    val name: String,
)

data class ProfessionalRoleResponseDto(
    val id: Long?,
    val hhId: Long,
    val name: String,
)

data class VacancyProfessionalRoleResponseDto(
    val id: Long?,
    val vacancyId: Long?,
    val professionalRole: ProfessionalRoleResponseDto,
    val createdDate: LocalDateTime?,
    val modifiedDate: LocalDateTime?
)

data class RequestObjectResponseDto(
    val id: Long?,
    val name: String,
    val createdDate: LocalDateTime?,
    val modifiedDate: LocalDateTime?
)
