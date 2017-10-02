package controllers.api

import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.util.ByteString
import com.trueaccord.scalapb.json.JsonFormat
import com.trueaccord.scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}
import play.api.libs.streams.Accumulator
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.Try

abstract class ProtobufController[
    TRequest <: GeneratedMessage with Message[TRequest] : GeneratedMessageCompanion,
    TResponse <: GeneratedMessage with Message[TResponse] : GeneratedMessageCompanion]
  (cc: ControllerComponents)
  extends AbstractController(cc) {

  def parseRequest(bytes: Array[Byte]): TRequest

  def action(request: TRequest): Future[TResponse]

  def parseJsonRequest(json: String): TRequest = JsonFormat.fromJsonString[TRequest](json)

  def protoAction() = Action.async(new ProtoParser) { implicit request: Request[TRequest] =>
    action(request.body) map { response =>
      request.contentType map {
        case "application/x-protobuf" => Ok(response.toByteArray)
        case _ => Ok(JsonFormat.toJsonString(response))
      } get
    }
  }

  private class ProtoParser extends BodyParser[TRequest] with Results {
    val jsonSink: Sink[ByteString, Future[Either[Result, TRequest]]] = Flow[ByteString].map { bytes =>
          Try {
            val s = bytes.decodeString("UTF-8")
            parseJsonRequest(s)
          }.toEither.left.map( t => BadRequest("Unable to parse JSON."))
    }.toMat(Sink.head)(Keep.right)

    val bytesSink: Sink[ByteString, Future[Either[Result, TRequest]]] = Flow[ByteString].map { bytes =>
      Try(parseRequest(bytes.toArray)).toEither.left.map( t => BadRequest("Unable to parse bytes."))
    }.toMat(Sink.head)(Keep.right)

    override def apply(header: RequestHeader) : Accumulator[ByteString, Either[Result, TRequest]] = {
      header.contentType map {
        case "application/x-protobuf" => Accumulator(bytesSink)
        case "application/json" => Accumulator(jsonSink)
        case _ => Accumulator.done(Left(BadRequest("API only supports protobuf as JSON or bytes.")))
      } get
    }
  }
}
