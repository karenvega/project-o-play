package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.services.IdentityService
import models.Event
import play.api.db.Database

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait EventService {

  /**
   * Retrieves an event that matches the specified ID.
   *
   * @param id The ID to retrieve an event.
   * @return The retrieved event or None if no event could be retrieved for the given ID.
   */
  def retrieve(id: UUID): Future[Option[Event]]

  /**
   * Retrieves an event that matches the specified ID.
   *
   * @param userEmail The ID to retrieve an event.
   * @return The retrieved event or None if no event could be retrieved for the given ID.
   */
  def getAll(userEmail: String): Future[List[Event]]

  /**
   * Saves an event.
   *
   * @param event The event to save.
   * @return The saved event.
   */
  def save(event: Event): Future[Event]

  def update(event: Event): Future[Event]

  def delete(event: Event): Future[Event]

}
