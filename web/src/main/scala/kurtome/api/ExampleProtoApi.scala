package kurtome.api

import kurtome.example.proto.hello._
import kurtome.api.AjaxApiHelper.ProtoAction

import scala.concurrent.Future

object ExampleProtoApi {

  def requestAsJson = AjaxApiHelper.jsonRequest(_, _)

  def hello(request: HelloRequest): Future[HelloResponse] = AjaxApiHelper.protoRequest(new ProtoAction[HelloRequest, HelloResponse] {
    override val route = "hello"

    override def serializeRequest(r: HelloRequest) = HelloRequest.toByteArray(r)

    override def parseResponse(r: Array[Byte]) = HelloResponse.parseFrom(r)
  })(request)

}
