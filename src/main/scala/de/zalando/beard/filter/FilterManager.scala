package de.zalando.beard.filter

import scala.collection.immutable.Map
import scalaz._, Scalaz._
import org.slf4j.LoggerFactory

/**
  * @author boopathi
  */
object FilterManager {

  val logger = LoggerFactory.getLogger(this.getClass())

  type StateRegistry[A] = State[Registry, A]

  val empty: StateRegistry[Unit] = State.state(Registry(Map.empty))

  case class Registry(data: Map[String, Filter]) {

    def get(name: String): Option[Filter] =
      data.get(name)

    def add(filter: Filter): Registry =
      Registry(data + (filter.name -> filter))

  }

  def register (filter: Filter): StateRegistry[Unit] = for {

    fDoesNotExist <- State.gets { r: Registry =>
      r.get(filter.name) match {
        case Some(f) => false
        case None => true
      }
    }

    _ <- modify[Registry] { r:Registry =>
      fDoesNotExist ? r.add(filter) | {
        // TODO: (@boopathi)
        // check if it's possible to NOT tap in the middle
        // of a functional code to throw an exception
        // Instead propagate this upward to where you call run method ?
        throw new FilterExists(filter.name)
        r
      }

    }

  } yield ()

  // incase the user wants to override
  // the default filters
  def registerOverwrite (filter: Filter): StateRegistry[Unit] = {
    logger.warn(s"Overwriting Filter ${filter.name}")
    modify[Registry] { _.add(filter) }
  }

}

object DefaultFilterRegistry {

  import FilterManager._

  val filters: StateRegistry[Unit] = for {
    _ <- register(LowercaseFilter())
    _ <- register(UppercaseFilter())
    _ <- register(DateFormatFilter())
  } yield ()

}
