package models.contribution

import java.util.UUID
import models.{ ContentType, HasContentTypeList }
import models.annotation.AnnotationBody
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import models.HasContentTypeList

case class Item(
  
  itemType: ItemType.Value,
  
  documentId: String,
  
  filepartId: Option[Int],
  
  contentType: ContentType,
  
  annotationId: Option[UUID],
  
  annotationVersionId: Option[UUID]
  
)

object Item extends HasContentTypeList {
  
  implicit val itemFormat: Format[Item] = (
    (JsPath \ "item_type").format[ItemType.Value] and
    (JsPath \ "document_id").format[String] and
    (JsPath \ "filepart_id").formatNullable[Int] and
    (JsPath \ "content_type").format[Seq[String]].inmap[ContentType](fromCTypeList, toCTypeList) and
    (JsPath \ "annotation_id").formatNullable[UUID] and
    (JsPath \ "annotation_version_id").formatNullable[UUID]
  )(Item.apply, unlift(Item.unapply))

}
  
object ItemType extends Enumeration {
  
  val DOCUMENT = Value("DOCUMENT")
  
  val QUOTE_BODY = Value("QUOTE_BODY")
  
  val COMMENT_BODY = Value("COMMENT_BODY")
  
  val PLACE_BODY = Value("PLACE_BODY")
  
  def fromBodyType(bodyType: AnnotationBody.Value) = bodyType match {

    case AnnotationBody.QUOTE => QUOTE_BODY
      
    case AnnotationBody.COMMENT => COMMENT_BODY

    case AnnotationBody.PLACE => PLACE_BODY    
    
  }
  
  // TODO PERSON_BODY, EVENT_BODY, FORUM_POST
  
  /** JSON conversion **/
  implicit val itemTypeFormat: Format[ItemType.Value] =
    Format(
      JsPath.read[String].map(ItemType.withName(_)),
      Writes[ItemType.Value](s => JsString(s.toString))
    )
 
}