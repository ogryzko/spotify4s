package spotify4s.domain

final case class Pagination(
                             offset: Option[Int],
                             limit: Option[Int]
                           )
