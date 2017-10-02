package kurtome

import kurtome.example.proto.hello._
import kurtome.api.ExampleProtoApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.{Dynamic, JSON}
import scala.util.{Failure, Success}

object HelloDummy {

  def runApiRequests: Unit = {
    println("Preparing to run API requests...")

    val request = HelloRequest(name = "Mary")

    // Test bytes request
    println("Running proto bytes request...")
    ExampleProtoApi.hello(request) onComplete {
      case Success(response) => {
        println(s"Got proto response with message '${response.message}' at epoch ${response.serverEpochMs}")
      }
      case Failure(ex) => println("Proto failure: " + ex.getMessage)
    }

    // Test JSON request
    println("Running proto JSON request...")
    ExampleProtoApi.requestAsJson("hello", Dynamic.literal("name" -> "Jason")) onComplete  {
      case Success(response) => {
        println("Got JSON object response, stringified: " + JSON.stringify(response))
      }
      case Failure(ex) => println("JSON failure: " + ex.getMessage)
    }

  }
}
