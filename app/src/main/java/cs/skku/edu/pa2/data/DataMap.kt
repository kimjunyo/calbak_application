package cs.skku.edu.pa2.data

data class DataMap (var location:LocationState) {
    data class LocationState(var lat: Double? = null, var lon: Double? = null)
}