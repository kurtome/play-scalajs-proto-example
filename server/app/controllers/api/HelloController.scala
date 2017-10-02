package controllers.api

import javax.inject._

import kurtome.example.proto.hello._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class HelloController @Inject()(cc: ControllerComponents) extends ProtobufController[HelloRequest, HelloResponse](cc) {
  override def parseRequest(bytes: Array[Byte]) = HelloRequest.parseFrom(bytes)

  override def action(request: HelloRequest) = {
    val name = request.name
    Future(HelloResponse(s"Frankenstein is my name, $name.", System.currentTimeMillis()))
  }
}

