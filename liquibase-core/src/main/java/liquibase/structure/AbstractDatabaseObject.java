package liquibase.structure;

import liquibase.AbstractExtensibleObject;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.ParsedNodeException;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.LiquibaseSerializable;
import liquibase.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractDatabaseObject  extends AbstractExtensibleObject implements DatabaseObject {

    private String snapshotId;

    @Override
    public String getObjectTypeName() {
        return StringUtils.lowerCaseFirst(getClass().getSimpleName());
    }

    public AbstractDatabaseObject() {
    }

    public AbstractDatabaseObject(String name) {
        setName(name);
    }

    public AbstractDatabaseObject(ObjectName name) {
        setName(name);
    }

    public String getSimpleName() {
        ObjectName name = getName();
        if (name == null) {
            return null;
        } else {
            return name.getName();
        }
    }

    public ObjectName getName() {
        return get(Attr.name, ObjectName.class);
    }

    @Override
    public <T> T setName(String name) {
        set(Attr.name, new ObjectName(name));
        return (T) this;
    }

    @Override
    public <T> T setName(ObjectName name) {
        set(ObjectName.Attr.name, name);

        return (T) this;
    }

    @Override
    public String getSnapshotId() {
        return snapshotId;
    }

    @Override
    public void setSnapshotId(String snapshotId) {
        if (this.snapshotId != null) {
            throw new UnexpectedLiquibaseException("snapshotId already set");
        }
        this.snapshotId = snapshotId;
    }

    @Override
    public boolean snapshotByDefault() {
        return true;
    }

    @Override
    public int compareTo(Object o) {
        return this.getName().compareTo(((AbstractDatabaseObject) o).getName());
    }

    @Override
    public String getSerializedObjectName() {
        return getObjectTypeName();
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_SNAPSHOT_NAMESPACE;
    }

    @Override
    public String getSerializableFieldNamespace(String field) {
        return getSerializedObjectNamespace();
    }

    @Override
    public Set<String> getSerializableFields() {
        TreeSet<String> fields = new TreeSet<String>(getAttributeNames());
        fields.add("snapshotId");
        return fields;
    }

    @Override
    public Object getSerializableFieldValue(String field) {
        if (field.equals("snapshotId")) {
            return snapshotId;
        }
        if (!getAttributeNames().contains(field)) {
            throw new UnexpectedLiquibaseException("Unknown field "+field);
        }
        Object value = get(field, Object.class);
        if (value instanceof DatabaseObject) {
            try {
                DatabaseObject clone = (DatabaseObject) value.getClass().newInstance();
                clone.setName(((DatabaseObject) value).getName());
                clone.setSnapshotId(((DatabaseObject) value).getSnapshotId());
                return clone;
            } catch (Exception e) {
                throw new UnexpectedLiquibaseException(e);
            }
        }
        return value;
    }

    @Override
    public LiquibaseSerializable.SerializationType getSerializableFieldType(String field) {
        if (getSerializableFieldValue(field) instanceof DatabaseObject) {
            return LiquibaseSerializable.SerializationType.NAMED_FIELD;
        } else {
            return LiquibaseSerializable.SerializationType.NAMED_FIELD;
        }
    }

    @Override
    public void load(ParsedNode parsedNode, ResourceAccessor resourceAccessor) throws ParsedNodeException {
        throw new RuntimeException("TODO");
    }

    @Override
    public ParsedNode serialize() {
        throw new RuntimeException("TODO");
    }

    @Override
    public String toString() {
        return getName().toShortString();
    }

    @Override
    public int hashCode() {
        return StringUtils.trimToEmpty(toString()).hashCode();
    }

}
