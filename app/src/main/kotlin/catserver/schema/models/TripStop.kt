package catserver.schema.models

data class TripStop(
    val arrivalTime: Long,
    val departureTime: Long,
    val stopId: String,
    val stopSequence: Int,
    val stopHeadsign: String?
)