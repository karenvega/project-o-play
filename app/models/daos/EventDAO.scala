package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{ Event, User }

import scala.concurrent.Future

/**
 * Give access to the event object.
 */
trait EventDAO {

  /**
   * Finds an event by its event ID.
   *
   * @param eventID The ID of the event to find.
   * @return The found event or None if no event for the given ID could be found.
   */
  def find(eventID: UUID): Future[Option[Event]]

  /**
   * Finds all the events by its user ID.
   *
   * @param userEmail The ID of the event to find.
   * @return The found event or None if no event for the given ID could be found.
   */
  def findByUser(userEmail: String): Future[List[Event]]

  /**
   * Saves an event.
   *
   * @param event The event to save.
   * @return The saved event.
   */
  def save(event: Event): Future[Event]
}
