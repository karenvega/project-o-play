package models.services

import java.util.UUID
import javax.inject.Inject

import models.Event
import models.daos.EventDAO
import play.api.db.Database

import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Handles actions to users.
 *
 * @param eventDAO The event DAO implementation.
 * @param ex      The execution context.
 */
class EventServiceImpl @Inject() (eventDAO: EventDAO, db: Database)(implicit ex: ExecutionContext) extends EventService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param id The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(id: UUID) = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * from events where eventID = '$id' ")
      if (rs.next()) {
        val id = rs.getString("eventID")
        val userEmail = rs.getString("userEmail")
        val name = rs.getString("name")
        val category = rs.getString("category")
        val location = rs.getString("location")
        val address = rs.getString("address")
        val startDate = rs.getString("startDate")
        val endDate = rs.getString("endDate")
        val isVirtual = rs.getString("isVirtual")
        Future.successful(Some(Event(UUID.fromString(id.toString), userEmail, Some(name), Some(category), Some(location), Some(address), Some(startDate), Some(endDate), isVirtual.toBoolean)))
      } else {
        Future.successful(Option.empty)
      }
    } finally {
      conn.close()
    }
  }

  /**
   * Retrieves an event that matches the specified ID.
   *
   * @param userEmail The ID to retrieve an event.
   * @return The retrieved event or None if no event could be retrieved for the given ID.
   */
  def getAll(userEmail: String) = {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement
      val allEvents: mutable.MutableList[Event] = mutable.MutableList.empty
      val rs = stmt.executeQuery(s"SELECT * from events where userEmail = '$userEmail' ")
      while (rs.next()) {
        val id = rs.getString("eventID")
        val userEmail = rs.getString("userEmail")
        val name = rs.getString("name")
        val category = rs.getString("category")
        val location = rs.getString("location")
        val address = rs.getString("address")
        val startDate = rs.getString("startDate")
        val endDate = rs.getString("endDate")
        val isVirtual = rs.getString("isVirtual")
        allEvents += Event(UUID.fromString(id.toString), userEmail, Some(name), Some(category), Some(location), Some(address), Some(startDate), Some(endDate), isVirtual.toBoolean)
      }
      Future.successful(allEvents.toList)
    } finally {
      conn.close()
    }
  }

  /**
   * Saves an event.
   *
   * @param event The event to save.
   * @return The saved event.
   */
  def save(event: Event) = {
    val conn = db.getConnection()
    try {
      conn.prepareStatement(s"INSERT INTO events(eventID, userEmail, name, category, location, address, startDate, endDate, isVirtual) " +
        s"values ( '${event.eventID}', '${event.userEmail}', '${event.name.get}', '${event.category.get}', '${event.location.get}', '${event.address.get}', '${event.startDate.get}', '${event.endDate.get}', '${event.isVirtual}' )").execute()
      Future.successful(event)
    } finally {
      conn.close()
    }
  }

  /**
   * Saves an event.
   *
   * @param event The event to save.
   * @return The saved event.
   */
  def update(event: Event) = {
    val conn = db.getConnection()
    try {
      conn.prepareStatement(s"UPDATE events SET name = '${event.name.get}', category = '${event.category.get}', " +
        s"location = '${event.location.get}', address = '${event.address.get}', startDate = '${event.startDate.get}', " +
        s"endDate = '${event.endDate.get}', isVirtual = '${event.isVirtual}' WHERE eventID = '${event.eventID}' ").execute()
      Future.successful(event)
    } finally {
      conn.close()
    }
  }

  /**
   * Saves an event.
   *
   * @param event The event to save.
   * @return The saved event.
   */
  def delete(event: Event) = {
    val conn = db.getConnection()
    try {
      conn.prepareStatement(s"DELETE from events WHERE eventID = '${event.eventID}' ").execute()
      Future.successful(event)
    } finally {
      conn.close()
    }
  }

}
