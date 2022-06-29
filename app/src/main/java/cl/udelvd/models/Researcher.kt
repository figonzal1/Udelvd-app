package cl.udelvd.models

data class Researcher(
    var id: Int = 0,
    var name: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var isActivated: Boolean = false,
    var idRole: Int = 0,
    var rolName: String? = null,
    var createTime: String? = null
)