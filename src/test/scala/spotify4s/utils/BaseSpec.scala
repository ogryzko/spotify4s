package spotify4s.utils

import cats.effect.IO
import io.circe.Decoder
import org.http4s.client.Client
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spotify4s.SPResponse
import spotify4s.domain.Pagination
import spotify4s.http.HttpClient

trait BaseSpec extends AnyFlatSpec with Matchers with TestData with MockFactory  {

  /*class HttpClientTest extends HttpClient[IO](mock[Client[IO]], implicitly)

  def httpClientMockGet[Out](
                              url: String,
                              params: Map[String, String] = Map.empty,
                              headers: Map[String, String] = Map.empty,
                              response: IO[SPResponse[Out]]
                            ): HttpClient[IO] = {
    val httpClientMock = mock[HttpClientTest]
    (httpClientMock
      .get[Out](
        _: Option[String],
        _: String,
        _: Map[String, String],
        _: Map[String, String],
        _: Option[Pagination]
      )(_: Decoder[Out]))
      .expects(sampleToken, url, headers ++ headerUserAgent, params, *, *)
      .returns(response)
    httpClientMock
  }*/

}
