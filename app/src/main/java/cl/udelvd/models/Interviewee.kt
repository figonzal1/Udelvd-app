package cl.udelvd.models

import java.util.*

data class Interviewee(
    var id: Int = 0,
    var name: String? = null,
    var lastName: String? = null,
    var gender: String? = null,
    var birthDate: Date? = null,
    var isLegalRetired: Boolean = false,
    var isFalls: Boolean = false,
    var nCaidas: Int = 0,
    var nCohabiting3Months: Int = 0,
    var idResearcher: Int = 0,
    var city: City? = null,
    var civilState: CivilState? = null,

    //opcionales
    var educationalLevel: EducationalLevel? = null,
    var coexistenteType: CohabitType? = null,
    var profession: Profession? = null,

    //Relaciones
    //Numero total de entrevistas de la persona
    var nInterviews: Int = 0,
    var researcherName: String? = null,
    var lastNameResearcher: String? = null
)