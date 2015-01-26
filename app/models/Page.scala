package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import utils.AnormType._
import java.util.UUID
import org.joda.time.DateTime

/** A page to monitor */
case class Page(id: UUID, created_at: DateTime, url: String, hits: Long, last_fetch: DateTime) {
	def this(id: UUID, url: String) = this(id, new DateTime, url, 0, new DateTime)
	def title: Option[String] = Content.findLastWithTitleByPage(id).flatMap(_.title)
}

object Page {
	/** Simple row parser */
	val simple = {
		get[UUID]("page_id") ~
		get[DateTime]("created_at") ~
		get[String]("url") ~
		get[Long]("hits") ~
		get[DateTime]("last_fetch") map {
			case id~created_at~url~hits~last_fetch =>
				Page(id, created_at, url, hits, last_fetch)
		}
	}

	/** Find item by its ID */
	def findById(id: UUID): Option[Page] = DB.withConnection { implicit conn =>
		SQL"""
			SELECT page_id, created_at, url, hits, last_fetch
			FROM page
			WHERE page_id = ${id}
		""".as(simple.singleOpt)
	}

	/** Find item by its url */
	def findByUrl(url: String): Option[Page] = DB.withConnection { implicit conn =>
		SQL"""
			SELECT page_id, created_at, url, hits, last_fetch
			FROM page
			WHERE url = $url
		""".as(simple.singleOpt)
	}

	/** Create an item */
	def create(i: Page): Int = DB.withConnection { implicit conn =>
		SQL"""
			INSERT INTO page (page_id, created_at, url, hits, last_fetch)
			VALUES (${i.id}, ${i.created_at}, ${i.url}, ${i.hits}, ${i.last_fetch})
		""".executeUpdate()
	}

	/** Update an item */
	def update(i: Page): Int = DB.withConnection { implicit conn =>
		SQL"""
			UPDATE page
			SET hits = ${i.hits}, last_fetch = ${i.last_fetch}
			WHERE page_id = ${i.id}
		""".executeUpdate()
	}
}
