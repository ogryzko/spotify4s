package spotify4s.unit

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spotify4s.domain.{Track, Tracks}
import spotify4s.utils.FakeResponses
import io.circe.parser._
import spotify4s.Decoders._

class DecodersSpec extends AnyFlatSpec with Matchers with FakeResponses  {
  "Track decoder" should "return a track when the JSON is valid" in {
    decode[Track](getTrackResponse).isRight shouldBe true
  }

  "Track decoder" should "return a list of tracks when the JSON is valid" in {
    decode[Tracks](listTracksResponse).isRight shouldBe true
  }
}
