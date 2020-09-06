package spotify4s.http

import cats.effect.IO
import fs2.Stream
import io.circe.Json
import org.http4s.{EntityEncoder, Response}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.generic.auto._
import org.http4s.circe._
import io.circe.syntax._
import spotify4s.utils.FakeResponses

class HttpClientSpec extends AnyFlatSpec with Matchers {
  case class Foo(a: Int)

  def createBody[F[_]](js: Json): Stream[F, Byte] =
    implicitly[EntityEncoder[F, Json]].toEntity(js).body

  "buildResponse" should "build a right if the response is successful and can be decoded" in {
    val response = Response[IO](body = createBody(Foo(3).asJson))
    val res      = HttpClient.buildResponse[IO, Foo](response)
    res.unsafeRunSync() shouldBe Right(Foo(3))
  }
}
