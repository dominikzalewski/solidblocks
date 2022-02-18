/*
 * This file is generated by jOOQ.
 */
package de.solidblocks.config.db.tables.records

import de.solidblocks.config.db.tables.Status
import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record5
import org.jooq.Row5
import org.jooq.impl.UpdatableRecordImpl
import java.time.LocalDateTime
import java.util.UUID

/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class StatusRecord() : UpdatableRecordImpl<StatusRecord>(Status.STATUS), Record5<UUID?, UUID?, String?, String?, LocalDateTime?> {

    var id: UUID?
        set(value) = set(0, value)
        get() = get(0) as UUID?

    var entity: UUID?
        set(value) = set(1, value)
        get() = get(1) as UUID?

    var status: String?
        set(value) = set(2, value)
        get() = get(2) as String?

    var code: String?
        set(value) = set(3, value)
        get() = get(3) as String?

    var statusTimestamp: LocalDateTime?
        set(value) = set(4, value)
        get() = get(4) as LocalDateTime?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<UUID?> = super.key() as Record1<UUID?>

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row5<UUID?, UUID?, String?, String?, LocalDateTime?> = super.fieldsRow() as Row5<UUID?, UUID?, String?, String?, LocalDateTime?>
    override fun valuesRow(): Row5<UUID?, UUID?, String?, String?, LocalDateTime?> = super.valuesRow() as Row5<UUID?, UUID?, String?, String?, LocalDateTime?>
    override fun field1(): Field<UUID?> = Status.STATUS.ID
    override fun field2(): Field<UUID?> = Status.STATUS.ENTITY
    override fun field3(): Field<String?> = Status.STATUS.STATUS_
    override fun field4(): Field<String?> = Status.STATUS.CODE
    override fun field5(): Field<LocalDateTime?> = Status.STATUS.STATUS_TIMESTAMP
    override fun component1(): UUID? = id
    override fun component2(): UUID? = entity
    override fun component3(): String? = status
    override fun component4(): String? = code
    override fun component5(): LocalDateTime? = statusTimestamp
    override fun value1(): UUID? = id
    override fun value2(): UUID? = entity
    override fun value3(): String? = status
    override fun value4(): String? = code
    override fun value5(): LocalDateTime? = statusTimestamp

    override fun value1(value: UUID?): StatusRecord {
        this.id = value
        return this
    }

    override fun value2(value: UUID?): StatusRecord {
        this.entity = value
        return this
    }

    override fun value3(value: String?): StatusRecord {
        this.status = value
        return this
    }

    override fun value4(value: String?): StatusRecord {
        this.code = value
        return this
    }

    override fun value5(value: LocalDateTime?): StatusRecord {
        this.statusTimestamp = value
        return this
    }

    override fun values(value1: UUID?, value2: UUID?, value3: String?, value4: String?, value5: LocalDateTime?): StatusRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        this.value4(value4)
        this.value5(value5)
        return this
    }

    /**
     * Create a detached, initialised StatusRecord
     */
    constructor(id: UUID? = null, entity: UUID? = null, status: String? = null, code: String? = null, statusTimestamp: LocalDateTime? = null) : this() {
        this.id = id
        this.entity = entity
        this.status = status
        this.code = code
        this.statusTimestamp = statusTimestamp
    }
}
