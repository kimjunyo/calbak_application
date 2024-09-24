package cs.skku.edu.pa2.data

data class DataUser(
    var id: String? = null,
    var passwd: String? = null,
    var info: Infos,
    var reserved: ArrayList<Reservations>
) {
    data class Infos(var name: String? = null, var age: Int? = null, var gender: String? = null)
    data class Reservations(
        var reservation_id: Int? = null,
        var restaurant_id: Int? = null,
        var number_of_people: Int? = null,
        var date: String? = null,
        var time: String? = null
    )
}