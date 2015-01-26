package models

import java.util.UUID
import org.joda.time.DateTime

case class Entry(id: UUID, title: String, updated: DateTime, content: String)
case class Feed(id: UUID, title: String, created: DateTime, updated: DateTime, link: String, entries: List[Entry])
