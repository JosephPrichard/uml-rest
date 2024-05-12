package com.turbouml.relationship;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.schema.Tables.CLASSES;
import static org.jooq.schema.Tables.RELATIONSHIPS;
import static org.jooq.impl.DSL.val;

@Repository("RelationshipDao")
public class RelationshipDao {
    private final DSLContext create;

    @Autowired
    public RelationshipDao(DSLContext create) {
        this.create = create;
    }

    public void save(RelationshipDto newRelationship) {
        create
            .insertInto(RELATIONSHIPS,
                RELATIONSHIPS.RELATIONSHIP_ID,
                RELATIONSHIPS.CLASS_ID_FROM,
                RELATIONSHIPS.CLASS_ID_TO,
                RELATIONSHIPS.TYPE,
                RELATIONSHIPS.LABEL,
                RELATIONSHIPS.PROJECT_ID
            )
            .select(
                create.select(
                        val(newRelationship.getRelationshipId()),
                        val(newRelationship.getClassIdFrom()),
                        val(newRelationship.getClassIdTo()),
                        val(newRelationship.getType().name()),
                        val(newRelationship.getLabel()),
                        CLASSES.PROJECT_ID
                    ).from(CLASSES)
                    .where(CLASSES.CLASS_ID.eq(newRelationship.getClassIdTo()))
            )
            .execute();
    }

    public List<RelationshipDto> findByProjectId(String projectId) {
        return create.select()
            .from(RELATIONSHIPS)
            .where(RELATIONSHIPS.PROJECT_ID.eq(projectId))
            .fetchInto(RelationshipDto.class);
    }

    public void rename(String relationshipId, String label) {
        create.update(RELATIONSHIPS)
            .set(RELATIONSHIPS.LABEL, label)
            .where(RELATIONSHIPS.RELATIONSHIP_ID.eq(relationshipId))
            .execute();
    }

    public void delete(String relationshipId) {
        create.deleteFrom(RELATIONSHIPS)
            .where(RELATIONSHIPS.RELATIONSHIP_ID.eq(relationshipId))
            .execute();
    }
}
