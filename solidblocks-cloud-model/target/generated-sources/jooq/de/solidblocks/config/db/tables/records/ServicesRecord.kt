/*
 * This file is generated by jOOQ.
 */
package de.solidblocks.config.db.tables.records


import de.solidblocks.config.db.tables.Services

import java.util.UUID

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record5
import org.jooq.Row5
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class ServicesRecord() : UpdatableRecordImpl<ServicesRecord>(Services.SERVICES), Record5<UUID?, String?, String?, Boolean?, UUID?> {

    var id: UUID?
        set(value) = set(0, value)
        get() = get(0) as UUID?

    var name: String?
        set(value) = set(1, value)
        get() = get(1) as String?

    var type: String?
        set(value) = set(2, value)
        get() = get(2) as String?

    var deleted: Boolean?
        set(value) = set(3, value)
        get() = get(3) as Boolean?

    var tenant: UUID?
        set(value) = set(4, value)
        get() = get(4) as UUID?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<UUID?> = super.key() as Record1<UUID?>

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row5<UUID?, String?, String?, Boolean?, UUID?> = super.fieldsRow() as Row5<UUID?, String?, String?, Boolean?, UUID?>
    override fun valuesRow(): Row5<UUID?, String?, String?, Boolean?, UUID?> = super.valuesRow() as Row5<UUID?, String?, String?, Boolean?, UUID?>
    override fun field1(): Field<UUID?> = Services.SERVICES.ID
    override fun field2(): Field<String?> = Services.SERVICES.NAME
    override fun field3(): Field<String?> = Services.SERVICES.TYPE
    override fun field4(): Field<Boolean?> = Services.SERVICES.DELETED
    override fun field5(): Field<UUID?> = Services.SERVICES.TENANT
    override fun component1(): UUID? = id
    override fun component2(): String? = name
    override fun component3(): String? = type
    override fun component4(): Boolean? = deleted
    override fun component5(): UUID? = tenant
    override fun value1(): UUID? = id
    override fun value2(): String? = name
    override fun value3(): String? = type
    override fun value4(): Boolean? = deleted
    override fun value5(): UUID? = tenant

    override fun value1(value: UUID?): ServicesRecord {
        this.id = value
        return this
    }

    override fun value2(value: String?): ServicesRecord {
        this.name = value
        return this
    }

    override fun value3(value: String?): ServicesRecord {
        this.type = value
        return this
    }

    override fun value4(value: Boolean?): ServicesRecord {
        this.deleted = value
        return this
    }

    override fun value5(value: UUID?): ServicesRecord {
        this.tenant = value
        return this
    }

    override fun values(value1: UUID?, value2: String?, value3: String?, value4: Boolean?, value5: UUID?): ServicesRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        this.value4(value4)
        this.value5(value5)
        return this
    }

    /**
     * Create a detached, initialised ServicesRecord
     */
    constructor(id: UUID? = null, name: String? = null, type: String? = null, deleted: Boolean? = null, tenant: UUID? = null): this() {
        this.id = id
        this.name = name
        this.type = type
        this.deleted = deleted
        this.tenant = tenant
    }
}
