package spotify4s.unit

import cats.effect.IO
import spotify4s.SPResponse
import spotify4s.domain.Track
import spotify4s.interpreters.TracksInterpreter
import spotify4s.utils.BaseSpec

class TracksSpec extends BaseSpec {
  /*"Tracks.listTracks" should "call httpClient.get with the right parameters" in {
    val response: IO[SPResponse[List[Track]]] =
      IO(SPResponse(List(track).asRight, okStatusCode, Map.empty))

    implicit val httpClientMock = httpClientMockGet[List[Track]](
      url = s"repos/$validRepoOwner/$validRepoName/issues",
      response = response
    )

    val issues = new TracksInterpreter[IO]

    issues.listIssues(validRepoOwner, validRepoName, None, headerUserAgent)

  }*/
}
