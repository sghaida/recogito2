package models

import org.jooq.{ Table, Record }
import play.api.cache.CacheApi
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import scala.reflect.ClassTag
import storage.DB

/** Generic sort order symbol **/
sealed class SortOrder

object SortOrder {
  case object ASC extends SortOrder
  case object DESC extends SortOrder
  
  def fromString(str: String): Option[SortOrder] =
    if (str.equalsIgnoreCase("asc"))
      Some(ASC)
    else if (str.equalsIgnoreCase("desc"))
      Some(DESC)
    else
      None
}

/** Various helpers of general use to Service classes **/
trait BaseService {

  /** JOOQ happily creates domain objects where all properties are null - this method is to check **/
  protected def isNotNull(record: Record) =
    (0 to record.size - 1).map(idx => {
      record.getValue(idx) != null
    }).exists(_ == true)

  /** Converts the results of a two-table left join to a Map[DomainObj1, Seq[DomainObj2]] **/
  protected def groupLeftJoinResult[T <: Record, V <: Record](records: Seq[Record], t: Class[T], v: Class[V]) =
    records
      .map(r => (r.into(t), r.into(v)))
      .groupBy(_._1)
      .mapValues(_.map(_._2).filter(record => isNotNull(record)))

  /** Boilerplate code for conducting a cache lookup, followed by DB lookup if nothing in cache **/
  protected def cachedLookup[T: ClassTag](prefix: String, key: String, 
      dbLookup: String => Future[Option[T]])(implicit db: DB, cache: CacheApi, ctx: ExecutionContext): Future[Option[T]] = {
    
    val maybeCachedValue = cache.get[T](prefix + "_" + key)
    if (maybeCachedValue.isDefined) {
      Future.successful(maybeCachedValue)
    } else {
      dbLookup(key).map(maybeStoredValue => {
        if (maybeStoredValue.isDefined)
          cache.set(prefix + "_" + key, maybeStoredValue.get, 10.minutes)

        maybeStoredValue
      })
    }
  }
  
  protected def removeFromCache(prefix: String, key: String)(implicit cache: CacheApi) = {
    cache.remove(prefix + "_" + key)
  }
  
  protected def getField[T <: Table[_]](table: T, fieldname: String) =
    table.fields().find(_.getName.equalsIgnoreCase(fieldname))
    
  protected def getField[T <: Table[_]](tables: Seq[T], fieldname: String) =
    tables.flatMap(_.fields).find(_.getName.equalsIgnoreCase(fieldname))

}
