package org.example.demohhclone

import org.example.demohhclone.Dtos.AreaResponseDto
import org.example.demohhclone.Dtos.EmployerResponseDto
import org.example.demohhclone.Dtos.EmploymentTypeResponseDto
import org.example.demohhclone.Dtos.ExperienceResponseDto
import org.example.demohhclone.Dtos.ScheduleResponseDto
import org.example.demohhclone.Dtos.VacancyResponseDto
import org.example.demohhclone.Dtos.WorkingHoursResponseDto
import org.springframework.stereotype.Component

@Component
class VacancyMapper {

    fun toDto(v: Vacancy): VacancyResponseDto =
        VacancyResponseDto(
            id = v.id,
            hhId = v.hhId,
            name = v.name,
            publishedAt = v.publishedAt,
            url = v.url,
            requirement = v.requirement,
            responsibility = v.responsibility,
            fromAmount = v.fromAmount,
            toAmount = v.toAmount,
            currency = v.currency,
            gross = v.gross,
            createdDate = v.createdDate,
            modifiedDate = v.modifiedDate,

            area = toAreaDto(v.area),
            employer = toEmployerDto(v.employer),
            schedule = v.schedule?.let { toScheduleDto(it) },
            workingHours = v.workingHours?.let { toWorkingHoursDto(it) },
            experience = toExperienceDto(v.experience),
            employmentType = toEmploymentTypeDto(v.employmentType)
        )


    private fun toAreaDto(a: Area) =
        AreaResponseDto(
            id = a.id,
            hhId = a.hhId,
            name = a.name,
            url = a.url,

        )

    private fun toEmployerDto(e: Employer) =
        EmployerResponseDto(
            id = e.id,
            hhId = e.hhId,
            name = e.name,
            url = e.url,
            countryId = e.countryId,
        )

    private fun toScheduleDto(s: Schedule) =
        ScheduleResponseDto(
            id = s.id,
            hhId = s.hhId,
            name = s.name,
        )

    private fun toWorkingHoursDto(w: WorkingHours) =
        WorkingHoursResponseDto(
            id = w.id,
            hhId = w.hhId,
            name = w.name,

        )

    private fun toExperienceDto(e: Experience) =
        ExperienceResponseDto(
            id = e.id,
            hhId = e.hhId,
            name = e.name,

        )

    private fun toEmploymentTypeDto(e: EmploymentType) =
        EmploymentTypeResponseDto(
            id = e.id,
            hhId = e.hhId,
            name = e.name
        )
}