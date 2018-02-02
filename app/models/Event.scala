package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity

/**
 * The event object.
 *
 * @param eventID The unique ID of the event.
 * @param userEmail The unique ID of the user.
 * @param name Maybe the first name of the authenticated user.
 * @param category Maybe the last name of the authenticated user.
 * @param location Maybe the full name of the authenticated user.
 * @param address Maybe the email of the authenticated provider.
 * @param startDate Maybe the avatar URL of the authenticated provider.
 * @param endDate Indicates that the user has activated its registration.
 * @param isVirtual Indicates that the user has activated its registration.
 */
case class Event(
  eventID: UUID,
  userEmail: String,
  name: Option[String],
  category: Option[String],
  location: Option[String],
  address: Option[String],
  startDate: Option[String],
  endDate: Option[String],
  isVirtual: Boolean) extends Identity {
}
