package org.example.demohhclone

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: LocalDateTime? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: LocalDateTime? = null,
    @CreatedBy var createdBy: String? = null,
    @LastModifiedBy var lastModifiedBy: String? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)


@Entity
@Table(name = "vacancies")
class Vacancy(
    @Column(nullable = false)
    val hhId: Long? = null,


    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val publishedAt: LocalDateTime,

    @Column(nullable = false)
    val url: String,

    // buyerda muommo vacancylarni hammasini alganda  requriment va responsibility toliq kelmayapdi bir qismi kelyapdi
    // ln shunda ham ozini yozdim chunki har bir vacanciani qayta olsam 4000 tagab yaqin request qilshim kerak
    // bu api tomonidan kop request qilganim un otkaz bermasligi un
    @Column(columnDefinition = "TEXT")
    val requirement: String? = null,

    @Column(columnDefinition = "TEXT")
    val responsibility: String? = null,

    @Column(name = "from_amount")
    val fromAmount: Int? = null,

    @Column(name = "to_amount")
    val toAmount: Int? = null,

    val currency: String? = null,

    val gross: Boolean? = null,

    @ManyToOne()
    @JoinColumn(name = "area_id")
    val area: Area,

    @ManyToOne()
    @JoinColumn(name = "employer_id")
    val employer: Employer,


    @ManyToOne()
    @JoinColumn(name = "schedule_id")
    val schedule: Schedule? = null,

    @ManyToOne()
    @JoinColumn(name = "working_hours_id")
    val workingHours: WorkingHours? = null,


    @ManyToOne()
    @JoinColumn(name = "experience_id")
    val experience: Experience,

    @ManyToOne()
    @JoinColumn(name = "employment_id")
    val employmentType: EmploymentType

): BaseEntity()

@Entity
@Table(name = "employment_types")
 class EmploymentType(
    @Column(name = "hh_id",unique = true )
    val hhId: String,

    @Column(nullable = false)
    val name: String
): BaseEntity()

@Entity
@Table(name = "experience")
 class Experience(
    @Column(name = "hh_id",unique = true )
    val hhId: String,

    @Column(nullable = false)
    val name: String
): BaseEntity()

@Entity
@Table(name = "professional_roles")
 class ProfessionalRole(
    @Column(name = "hh_id",unique = true )
    val hhId: Long,

    @Column(nullable = false)
    val name: String
): BaseEntity()

@Entity
@Table(name = "working_hours")
 class WorkingHours(
    @Column(name = "hh_id",unique = true )
    val hhId: String,

    @Column(nullable = false)
    val name: String
): BaseEntity()

@Entity
@Table(name = "schedules")
 class Schedule(
    @Column(name = "hh_id",unique = true )
    val hhId: String,

    @Column(nullable = false)
    val name: String
): BaseEntity()


@Entity
@Table(name = "employers")
 class Employer(

    @Column(name = "hh_id",unique = true )
    val hhId: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val countryId: Long
): BaseEntity()


@Entity
@Table(name = "areas")
 class Area(
    @Column(name = "hh_id",unique = true )
    val hhId: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val url: String
): BaseEntity()

@Entity
@Table(name = "vacancy_professional_roles")
data class VacancyProfessionalRole(

    @ManyToOne
    @JoinColumn(name = "vacancy_id", nullable = false)
    val vacancy: Vacancy,

    @ManyToOne()
    @JoinColumn(name = "professional_role_id", nullable = false)
    val professionalRole: ProfessionalRole
): BaseEntity()

@Entity
@Table(name ="request_objects" )
 class RequestObject(
    @Column(nullable = false, unique = true)
    val name: String
 ): BaseEntity()

@Entity
@Table(name ="request_object_vacancies" )
class RequestObjectVacancy(
    @ManyToOne
    @JoinColumn(name = "request_object_id", nullable = false)
    val requestObject: RequestObject,

    @ManyToOne()
    @JoinColumn(name = "vacancy_id", nullable = false)
    val vacancy: Vacancy
): BaseEntity()

