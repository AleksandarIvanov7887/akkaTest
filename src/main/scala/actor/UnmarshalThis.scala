package actor

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, Multipart, StatusCode, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.google.protobuf.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContextExecutor, Future}

object UnmarshalThis extends App {
  implicit val system: ActorSystem = ActorSystem("thissAS")
  implicit val execContext: ExecutionContextExecutor = system.getDispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val client = new FilterDaemonClientImpl()

  client
    .singlePostRequest(5, "tsGuid-5", 0, ByteString.copyFromUtf8("tsGuid-5-contents"))
    .map[String] {
      case (StatusCodes.OK, _) =>
        "OK"
      case (StatusCodes.Accepted, _)     =>
        "ACCEPTED"
      case (StatusCodes.Unauthorized, responseEntity) =>
        println(responseEntity.prio)
        println(responseEntity.ts)
        println(ByteString.copyFrom(responseEntity.status).toStringUtf8)
        "UNAUTH"
      case _ =>
        "DEFAULT"
    }
    .foreach(println)
}

object FilterDaemonResponse {
  val EMPTY: FilterDaemonResponse = FilterDaemonResponse(0L, 0, Array())
}

case class FilterDaemonResponse(ts: Long, prio: Int, status: Array[Byte]) {
  def withTs(ts: Long): FilterDaemonResponse = copy(ts, this.prio, this.status)
  def withPrio(prio: Int): FilterDaemonResponse = copy(this.ts, prio, this.status)
  def withStatus(status: Array[Byte]): FilterDaemonResponse = copy(this.ts, this.prio, status)
}

trait FilterDaemonClient {
  def singlePostRequest(akid: Int, guid: String, tries: Int, payload: ByteString): Future[(StatusCode, FilterDaemonResponse)]
}

class FilterDaemonClientImpl(implicit val actorSystem: ActorSystem, val mat: ActorMaterializer) extends FilterDaemonClient {
  implicit val execContext: ExecutionContextExecutor = actorSystem.getDispatcher

  override def singlePostRequest(akid: Int, guid: String, tries: Int, payload: ByteString): Future[(StatusCode, FilterDaemonResponse)] = {
    val contentsPart = FormData.BodyPart.Strict("status", HttpEntity(ContentTypes.`application/octet-stream`, payload.toByteArray))
    val emailPart = FormData.BodyPart.Strict("email", HttpEntity(ContentTypes.`application/octet-stream`, ByteString.copyFromUtf8("SASHETO OT PLEVEN").toByteArray))
    val entity = FormData(contentsPart, emailPart).toEntity()
    Http()
      .singleRequest(HttpRequest(HttpMethods.POST, s"http://localhost:8081/SEND?akid=$akid&guid=$guid&tries=$tries", entity = entity))
      .flatMap[(StatusCode, FilterDaemonResponse)] { httpResponse =>
      httpResponse.status match {
        case StatusCodes.Unauthorized =>
          Future.successful(httpResponse.status).zip(unmarshalResponse(httpResponse))
        case _ =>
          Future.successful((httpResponse.status, FilterDaemonResponse.EMPTY))
      }
    }
  }

  def unmarshalResponse(resp: HttpResponse): Future[FilterDaemonResponse] = {
    Unmarshal(resp.entity).to[Multipart.FormData]
      .map[Source[Multipart.FormData.BodyPart, Any]](formData => formData.parts)
      .flatMap(parts => parts.runWith(sink))
  }

  val sink: Sink[FormData.BodyPart, Future[FilterDaemonResponse]] =
    Sink.foldAsync[FilterDaemonResponse, Multipart.FormData.BodyPart](FilterDaemonResponse.EMPTY) {
      (acc, bodyPart) =>
        def discard(p: Multipart.FormData.BodyPart): Future[FilterDaemonResponse] = {
          p.entity.discardBytes()
          Future.successful(acc)
        }

        bodyPart.entity match {
          case HttpEntity.Strict(ct, data) if ct.isInstanceOf[ContentType.NonBinary] ⇒
            val charsetName = ct.asInstanceOf[ContentType.NonBinary].charset.nioCharset.name
            val partContent = data.decodeString(charsetName)
              bodyPart.name match {
              case "ts" => Future.successful(acc.withTs(partContent.toLong))
              case "prio" => Future.successful(acc.withPrio(partContent.toInt))
              case _ => discard(bodyPart)
            }
          case HttpEntity.Strict(ct, data) if ct.isInstanceOf[ContentType.Binary] ⇒
            if (bodyPart.name != "status") {
              discard(bodyPart)
            }

            Future.successful(acc.withStatus(data.toArray))
          case _ ⇒
            discard(bodyPart)
        }
    }
}
