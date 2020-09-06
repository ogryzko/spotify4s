package spotify4s.domain

case class Artists(
                    external_urls: Map[String, String],
                    href: String,
                    id: String,
                    name: String,
                    `type`: String,
                    uri: String
                  )
