package spotify4s

final case class SPResponse[A](
                                result: Either[SPError, A],
                                statusCode: Int,
                                headers: Map[String, String]
                              )
