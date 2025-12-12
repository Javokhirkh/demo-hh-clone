package org.example.demohhclone.Dtos

import org.example.demohhclone.Area
import org.example.demohhclone.Employer
import org.example.demohhclone.EmploymentType
import org.example.demohhclone.Experience
import org.example.demohhclone.ProfessionalRole
import org.example.demohhclone.Schedule
import org.example.demohhclone.Vacancy
import org.example.demohhclone.WorkingHours
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
    val name: String?,
    val area: AreaDto?,
    val salaryRange: SalaryRangeDto?,
    val publishedAt: LocalDateTime?,
    val url: String?,
    val employer: EmployerDto?,
    val snippet: SnippetDto?,
    val schedule: ScheduleDto?,
    val workingHours: WorkingHoursDto?,
    val professionalRoles: List<ProfessionalRoleDto>?,
    val experience: ExcperienceDto?,
    val employment: EmployementTypeDto?
) {
    companion object

    fun toEntity(
        area: Area,
        employer: Employer,
        schedule: Schedule?,
        workingHours: WorkingHours?,
        experience: Experience,
        employmentType: EmploymentType
    ): Vacancy {
        return Vacancy(
            hhId = this.id,
            name = this.name ?: "Без названия",
            publishedAt = this.publishedAt ?: LocalDateTime.now(),
            url = this.url ?: "",
            requirement = this.snippet?.requirement,
            responsibility = this.snippet?.responsibility,
            fromAmount = this.salaryRange?.from,
            toAmount = this.salaryRange?.to,
            currency = this.salaryRange?.currency,
            gross = this.salaryRange?.gross,
            area = area,
            employer = employer,
            schedule = schedule,
            workingHours = workingHours,
            experience = experience,
            employmentType = employmentType
        )
    }
}

data class EmployementTypeDto(
    val id: String,
    val name: String
) {
    fun toEntity(): EmploymentType {
        return EmploymentType(
            hhId = this.id,
            name = this.name
        )
    }
}

data class ExcperienceDto(
    val id: String,
    val name: String
) {
    fun toEntity(): Experience {
        return Experience(
            hhId = this.id,
            name = this.name
        )
    }
}

data class ProfessionalRoleDto(
    val id: Long,
    val name: String
) {
    fun toEntity(): ProfessionalRole {
        return ProfessionalRole(
            hhId = this.id,
            name = this.name
        )
    }
}

data class WorkingHoursDto(
    val id: String,
    val name: String
) {
    fun toEntity(): WorkingHours {
        return WorkingHours(
            hhId = this.id,
            name = this.name
        )
    }
}

data class ScheduleDto(
    val id: String,
    val name: String
) {
    fun toEntity(): Schedule {
        return Schedule(
            hhId = this.id,
            name = this.name
        )
    }
}

data class SnippetDto(
    val requirement: String?,
    val responsibility: String?
)

data class EmployerDto(
    val id: Long,
    val name: String?,  // Made nullable
    val url: String?,  // Made nullable
    val countryId: Long?
) {
    fun toEntity(): Employer {
        return Employer(
            hhId = this.id,
            name = this.name ?: "Неизвестный работодатель",
            url = this.url ?: "",
            countryId = this.countryId ?: 0L
        )
    }
}

data class SalaryRangeDto(
    val from: Int?,
    val to: Int?,
    val currency: String?,
    val gross: Boolean?
)

data class AreaDto(
    val id: String,
    val name: String?,  // Made nullable
    val url: String?  // Made nullable
) {
    fun toEntity(): Area {
        return Area(
            hhId = this.id,
            name = this.name ?: "Неизвестная область",
            url = this.url ?: ""
        )
    }
}