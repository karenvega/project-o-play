package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the sign up process.
 */
object CreateEventForm {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "place" -> nonEmptyText,
      "address" -> nonEmptyText,
      "startDate" -> nonEmptyText,
      "endDate" -> nonEmptyText,
      "type" -> nonEmptyText,
      "category" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param name The name of the event.
   * @param place The location where the event is going to take place.
   * @param address The address of the location.
   * @param startDate The start date of the event.
   * @param endDate The end date of the event.
   * @param type The type of the event.
   * @param category The category of the event.
   */
  case class Data(
    name: String,
    place: String,
    address: String,
    startDate: String,
    endDate: String,
    `type`: String,
    category: String)
}
