package utils

import anorm._
import java.util.UUID

/** Helps Anorm deal with some more data types */
object AnormType {

	// Store UUID as object instead of string
	implicit val uuidToStatement = new ToStatement[UUID] {
		def set(s: java.sql.PreparedStatement, index: Int, aValue: UUID): Unit = s.setObject(index, aValue)
	}
}
