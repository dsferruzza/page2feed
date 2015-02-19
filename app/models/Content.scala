package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import utils.AnormType._
import java.util.UUID
import org.joda.time.DateTime

/** A fetched content */
case class Content(id: UUID, page_id: UUID, fetched_at: DateTime, first: Boolean, title: Option[String], body: String, content: String) {
	def this(id: UUID, page_id: UUID, first: Boolean, title: Option[String], body: String, content: String) = this(id, page_id, new DateTime, first, title, body, content)

	/** Check if a content has changed since last fetch */
	def isNew: Boolean = {
		Content.findLastByPage(page_id) match {
			case Some(c) => c.body != body || c.title != title
			case None => true
		}
	}
}

object Content {
	/** Simple row parser */
	val simple = {
		get[UUID]("content_id") ~
		get[UUID]("page_id") ~
		get[DateTime]("fetched_at") ~
		get[Boolean]("first") ~
		get[Option[String]]("title") ~
		get[String]("body") ~
		get[String]("content") map {
			case id~page_id~fetched_at~first~title~body~content =>
				Content(id, page_id, fetched_at, first, title, body, content)
		}
	}

	/** Get last items by page (except the first item) */
	def getLastsByPage(page_id: UUID, limit: Int): List[Content] = DB.withConnection { implicit connection =>
		SQL"""
			SELECT content_id, page_id, fetched_at, first, title, body, content
			FROM content
			WHERE page_id = ${page_id}
			ORDER BY fetched_at DESC
			LIMIT ${limit}
		""".as(simple.*)
	}

	/** Find item by its ID */
	def findById(id: UUID): Option[Content] = DB.withConnection { implicit conn =>
		SQL"""
			SELECT content_id, page_id, fetched_at, first, title, body, content
			FROM content
			WHERE content_id = ${id}
		""".as(simple.singleOpt)
	}

	/** Find last item for a give page */
	def findLastByPage(page_id: UUID): Option[Content] = DB.withConnection { implicit conn =>
		SQL"""
			SELECT content_id, page_id, fetched_at, first, title, body, content
			FROM content
			WHERE page_id = ${page_id}
			ORDER BY fetched_at DESC
			LIMIT 1
		""".as(simple.singleOpt)
	}

	/** Find last item with a title for a give page */
	def findLastWithTitleByPage(page_id: UUID): Option[Content] = DB.withConnection { implicit conn =>
		SQL"""
			SELECT content_id, page_id, fetched_at, first, title, body, content
			FROM content
			WHERE page_id = ${page_id} AND title IS NOT NULL
			ORDER BY fetched_at DESC
			LIMIT 1
		""".as(simple.singleOpt)
	}

	/** Create an item */
	def create(i: Content): Int = DB.withConnection { implicit conn =>
		SQL"""
			INSERT INTO content (content_id, page_id, fetched_at, first, title, body, content)
			VALUES (${i.id}, ${i.page_id}, ${i.fetched_at}, ${i.first}, ${i.title}, ${i.body}, ${i.content})
		""".executeUpdate()
	}

	/** Put every content side by side with the previous content */
	def prepareForDiff(contents: List[Content]): List[(Content, Content)] = {
		val initialValue: List[(Option[Content], Option[Content])] = List((None, None))
		contents
			.foldRight(initialValue) { (cur, acc) =>
				val newItem = (Some(cur), None)
				val updatedItem = (acc.head._1, Some(cur))
				newItem :: updatedItem :: acc.tail
			}
			.tail
			.init
			.map {
				case (o1, o2) => (o1.get, o2.get)
			}
	}
}
