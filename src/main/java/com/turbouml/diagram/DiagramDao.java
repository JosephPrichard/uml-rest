package com.turbouml.diagram;

import com.turbouml.classuml.Access;
import com.turbouml.field.FieldDto;
import com.turbouml.method.MethodEntity;
import com.turbouml.classuml.ClassDto;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.schema.Tables.METHODS;
import static org.jooq.schema.tables.Classes.CLASSES;
import static org.jooq.schema.tables.Fields.FIELDS;
import static org.jooq.impl.DSL.array;
import static org.jooq.impl.DSL.arrayAgg;

@Repository("ClassDiagramDao")
public class DiagramDao {
    public static final String CLASS_ID_ALIAS = "class_id";
    public static final String CONTENT_NAME_ALIAS = "content_name";
    public static final String ACCESS_ALIAS = "access";
    public static final String STEREOTYPES_ALIAS = "stereotypes";
    public static final String PROJECT_ID_ALIAS = "project_id";
    public static final String X_POS_ALIAS = "x_pos";
    public static final String Y_POS_ALIAS = "y_pos";

    public static final String FIELDS_ALIAS = "fields";
    public static final String METHODS_ALIAS = "methods";

    public static final Field<String> CLASS_ID_ALIASED = CLASSES.CLASS_ID.as(CLASS_ID_ALIAS);
    public static final Field<String> CONTENT_NAME_ALIASED = CLASSES.CONTENT_NAME.as(CONTENT_NAME_ALIAS);
    public static final Field<String> ACCESS_ALIASED = CLASSES.ACCESS.as(ACCESS_ALIAS);
    public static final Field<String[]> STEREOTYPES_ALIASED = CLASSES.STEREOTYPES.as(STEREOTYPES_ALIAS);
    public static final Field<String> PROJECT_ID_ALIASED = CLASSES.PROJECT_ID.as(PROJECT_ID_ALIAS);
    public static final Field<Integer> X_POS_ALIASED = CLASSES.X_POS.as(X_POS_ALIAS);
    public static final Field<Integer> Y_POS_ALIASED = CLASSES.Y_POS.as(Y_POS_ALIAS);

    private final DSLContext create;

    @Autowired
    public DiagramDao(DSLContext create) {
        this.create = create;
    }

    private List<ClassDiagramEntity> getResult(ResultSet rs)
        throws SQLException {
        List<ClassDiagramEntity> diagrams = new ArrayList<>();

        while (rs.next()) {
            var obj = new ClassDiagramEntity();

            var umlClass = new ClassDto();
            umlClass.setClassId(rs.getString(CLASS_ID_ALIAS));
            umlClass.setContentName(rs.getString(CONTENT_NAME_ALIAS));
            umlClass.setAccess(Access.stringToEnum(rs.getString(ACCESS_ALIAS)));
            umlClass.setStereotypes((String[]) rs.getArray(STEREOTYPES_ALIAS).getArray());
            umlClass.setProjectId(rs.getString(PROJECT_ID_ALIAS));
            umlClass.setXPos(rs.getInt(X_POS_ALIAS));
            umlClass.setYPos(rs.getInt(Y_POS_ALIAS));

            obj.setUmlClass(umlClass);
            obj.setFields(sqlArrayToFieldsList(rs.getArray(FIELDS_ALIAS)));
            obj.setMethods(sqlArrayToMethodsList(rs.getArray(METHODS_ALIAS)));

            diagrams.add(obj);
        }

        return diagrams;
    }

    private List<FieldDto> sqlArrayToFieldsList(Array sqlArray) {
        try {
            List<FieldDto> classFields = new ArrayList<>();
            if (sqlArray == null) {
                return classFields;
            }
            String[][] rows = (String[][]) sqlArray.getArray();
            for (String[] columns : rows) {
                var obj = new FieldDto();
                obj.setFieldId(columns[0]);
                obj.setContentName(columns[1]);
                obj.setDataType(columns[2]);
                obj.setAccess(Access.stringToEnum(columns[3]));
                obj.setStatic(Boolean.parseBoolean(columns[4]));
                obj.setClassId(columns[5]);
                obj.setProjectId(columns[6]);
                classFields.add(obj);
            }
            return classFields;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<MethodEntity> sqlArrayToMethodsList(Array sqlArray) {
        try {
            List<MethodEntity> classMethods = new ArrayList<>();
            if (sqlArray == null) {
                return classMethods;
            }
            String[][] rows = (String[][]) sqlArray.getArray();
            for (String[] columns : rows) {
                var obj = new MethodEntity();
                obj.setMethodId(columns[0]);
                obj.setContentName(columns[1]);
                obj.setReturnType(columns[2]);
                obj.setParams(columns[3]);
                obj.setAccess(Access.stringToEnum(columns[4]));
                obj.setStatic(Boolean.parseBoolean(columns[5]));
                obj.setAbstract(Boolean.parseBoolean(columns[6]));
                obj.setClassId(columns[7]);
                obj.setProjectId(columns[8]);
                classMethods.add(obj);
            }
            return classMethods;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<ClassDiagramEntity> findByProjectId(String projectId) throws SQLException {
        var result = create.select()
            .select(
                CLASS_ID_ALIASED,
                CONTENT_NAME_ALIASED,
                ACCESS_ALIASED,
                STEREOTYPES_ALIASED,
                PROJECT_ID_ALIASED,
                X_POS_ALIASED,
                Y_POS_ALIASED,
                create.select(
                        arrayAgg(
                            array(
                                FIELDS.FIELD_ID,
                                FIELDS.CONTENT_NAME,
                                FIELDS.DATA_TYPE,
                                FIELDS.ACCESS,
                                FIELDS.STATIC.cast(String.class),
                                FIELDS.CLASS_ID,
                                FIELDS.PROJECT_ID
                            )
                        ).orderBy(FIELDS.CONTENT_ORDER)
                    ).from(FIELDS)
                    .where(FIELDS.CLASS_ID.eq(CLASSES.CLASS_ID))
                    .asField(FIELDS_ALIAS),
                create.select(
                        arrayAgg(
                            array(
                                METHODS.METHOD_ID,
                                METHODS.CONTENT_NAME,
                                METHODS.RETURN_TYPE,
                                METHODS.PARAMS,
                                METHODS.ACCESS,
                                METHODS.STATIC.cast(String.class),
                                METHODS.ABSTRACT.cast(String.class),
                                METHODS.CLASS_ID,
                                METHODS.PROJECT_ID
                            )
                        ).orderBy(METHODS.CONTENT_ORDER)
                    ).from(METHODS)
                    .where(METHODS.CLASS_ID.eq(CLASSES.CLASS_ID))
                    .asField(METHODS_ALIAS)
            )
            .from(CLASSES)
            .where(CLASSES.PROJECT_ID.eq(projectId))
            .fetch();
        return getResult(result.intoResultSet());
    }
}
