/**
 * This class is generated by jOOQ
 */
package models.generated.tables.records


import java.lang.Object

import javax.annotation.Generated

import models.generated.tables.SqliteSequence

import org.jooq.Field
import org.jooq.Record2
import org.jooq.Row2
import org.jooq.impl.TableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = Array(
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	),
	comments = "This class is generated by jOOQ"
)
class SqliteSequenceRecord extends TableRecordImpl[SqliteSequenceRecord](SqliteSequence.SQLITE_SEQUENCE) with Record2[Object, Object] {

	/**
	 * Setter for <code>sqlite_sequence.name</code>.
	 */
	def setName(value : Object) : Unit = {
		setValue(0, value)
	}

	/**
	 * Getter for <code>sqlite_sequence.name</code>.
	 */
	def getName : Object = {
		val r = getValue(0)
		if (r == null) null else r.asInstanceOf[Object]
	}

	/**
	 * Setter for <code>sqlite_sequence.seq</code>.
	 */
	def setSeq(value : Object) : Unit = {
		setValue(1, value)
	}

	/**
	 * Getter for <code>sqlite_sequence.seq</code>.
	 */
	def getSeq : Object = {
		val r = getValue(1)
		if (r == null) null else r.asInstanceOf[Object]
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	override def fieldsRow : Row2[Object, Object] = {
		super.fieldsRow.asInstanceOf[ Row2[Object, Object] ]
	}

	override def valuesRow : Row2[Object, Object] = {
		super.valuesRow.asInstanceOf[ Row2[Object, Object] ]
	}
	override def field1 : Field[Object] = SqliteSequence.SQLITE_SEQUENCE.NAME
	override def field2 : Field[Object] = SqliteSequence.SQLITE_SEQUENCE.SEQ
	override def value1 : Object = getName
	override def value2 : Object = getSeq

	override def value1(value : Object) : SqliteSequenceRecord = {
		setName(value)
		this
	}

	override def value2(value : Object) : SqliteSequenceRecord = {
		setSeq(value)
		this
	}

	override def values(value1 : Object, value2 : Object) : SqliteSequenceRecord = {
		this.value1(value1)
		this.value2(value2)
		this
	}

	/**
	 * Create a detached, initialised SqliteSequenceRecord
	 */
	def this(name : Object, seq : Object) = {
		this()

		setValue(0, name)
		setValue(1, seq)
	}
}