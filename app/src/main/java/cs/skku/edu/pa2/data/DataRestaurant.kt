package cs.skku.edu.pa2.data

data class DataRestaurant(
    var id: Int? = null,
    var restaurant: String? = null,
    var type: String? = null,
    var location: String? = null,
    var rating: String? = null,
    var image: String? = null,
    var description: String? = null,
    var openingHours: OpenClose,
    var Menu: ArrayList<Menus>
) {
    data class OpenClose(var open: String? = null, var close: String? = null)
    data class Menus(
        var id: Int? = null,
        var name: String? = null,
        var price: Int? = null,
        var image: String? = null
    )
}