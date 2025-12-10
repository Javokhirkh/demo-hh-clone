package org.example.demohhclone

import feign.Param
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>
    fun findByHhIdAndDeletedIsFalse(id: Long): T?
    fun findByHhIdAndDeletedIsFalse(id: String): T?
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    private val isNotDeletedSpecification = Specification<T> { root, _, cb ->
        cb.equal(root.get<Boolean>("deleted"), false)
    }

    override fun findByIdAndDeletedFalse(id: Long): T? =
        findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? =
        findByIdOrNull(id)?.run {
            deleted = true
            save(this)
        }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)

    override fun findAllNotDeleted(pageable: Pageable): Page<T> =
        findAll(isNotDeletedSpecification, pageable)

    override fun findByHhIdAndDeletedIsFalse(id: Long): T? =
        findOne(
            Specification<T> { root, _, cb ->
                cb.and(
                    cb.equal(root.get<Long>("hhId"), id),
                    cb.equal(root.get<Boolean>("deleted"), false)
                )
            }
        ).orElse(null)

    override fun findByHhIdAndDeletedIsFalse(id: String): T?=
        findOne(
            Specification<T> { root, _, cb ->
                cb.and(
                    cb.equal(root.get<String>("hhId"), id),
                    cb.equal(root.get<Boolean>("deleted"), false)
                )
            }
        ).orElse(null)

    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }
}

@Repository
interface VacancyRepository: BaseRepository<Vacancy>{
    @Query("""
        SELECT v FROM Vacancy v WHERE v.deleted = false AND v.modifiedDate < :startTime
    """)
    fun findAllNotDeletedAndBeforeModifiedDate(@Param("startTime") startTime: LocalDateTime): List<Vacancy>

}
@Repository
interface EmployerRepository: BaseRepository<Employer>{

}
@Repository
interface AreaRepository: BaseRepository<Area>{
    fun findByHhId(id: String): Area

}


@Repository
interface EmploymentTypeRepository: BaseRepository<EmploymentType>{

}

@Repository
interface ExperienceRepository: BaseRepository<Experience>{

}

@Repository
interface ProfessionalRoleRepository: BaseRepository<ProfessionalRole>{

}

@Repository
interface WorkingHoursRepository: BaseRepository<WorkingHours>{

}

@Repository
interface ScheduleRepository: BaseRepository<Schedule>{

}

@Repository
interface VacancyProfessionalRoleRepository: BaseRepository<VacancyProfessionalRole>{
    fun findByVacancyId(id: Long):List<VacancyProfessionalRole>
}

@Repository
interface RequestObjectRepository : BaseRepository<RequestObject>{

}

@Repository
interface RequestObjectVacancyRepository : BaseRepository<RequestObjectVacancy>{

}
