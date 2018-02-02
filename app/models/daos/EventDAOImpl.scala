package models.daos

import java.util.UUID

import models.Event
import models.daos.EventDAOImpl._

import scala.collection.mutable
import scala.concurrent.Future

import anorm._

/**
 * Give access to the event object.
 */
class EventDAOImpl extends EventDAO {

  /**
   * Finds an event by its user ID.
   *
   * @param eventID The ID of the event to find.
   * @return The found event or None if no event for the given ID could be found.
   */
  def find(eventID: UUID) = Future.successful(events.get(eventID))

  /**
   * Finds all the events by its user ID.
   *
   * @param userEmail The ID of the event to find.
   * @return The found event or None if no event for the given ID could be found.
   */
  def findByUser(userEmail: String) = Future.successful(eventsByUser.get(userEmail).getOrElse(List.empty))

  /**
   * Saves a event.
   *
   * @param event The event to save.
   * @return The saved event.
   */
  def save(event: Event) = {
    events += (event.eventID -> event)
    eventsByUser += ("karenya11@hotmail.com" -> List(event))
    Future.successful(event)
  }
}

/**
 * The companion object.
 */
object EventDAOImpl {

  /**
   * The list of events.
   */
  val events: mutable.HashMap[UUID, Event] = mutable.HashMap()

  /**
   * The list of events.
   */
  val eventsByUser: mutable.HashMap[String, List[Event]] = mutable.HashMap()
}
