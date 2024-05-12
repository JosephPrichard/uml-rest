package com.turbouml.packageuml;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.schema.Tables.PACKAGES;

@Repository("PackageDao")
public class PackageDao {
    private final DSLContext create;

    @Autowired
    public PackageDao(DSLContext create) {
        this.create = create;
    }

    public void save(PackageDto newPackage) {
        create
            .insertInto(PACKAGES,
                PACKAGES.PACKAGE_ID,
                PACKAGES.CONTENT_NAME,
                PACKAGES.X_POS,
                PACKAGES.Y_POS,
                PACKAGES.X_DIST,
                PACKAGES.Y_DIST,
                PACKAGES.PROJECT_ID
            )
            .values(
                newPackage.getPackageId(),
                newPackage.getContentName(),
                newPackage.getXPos(),
                newPackage.getYPos(),
                newPackage.getXDist(),
                newPackage.getYDist(),
                newPackage.getProjectId()
            )
            .execute();
    }

    public List<PackageDto> findByProjectId(String projectId) {
        return create.select()
            .from(PACKAGES)
            .where(PACKAGES.PROJECT_ID.eq(projectId))
            .fetchInto(PackageDto.class);
    }

    public void rename(String packageId, String name) {
        create.update(PACKAGES)
            .set(PACKAGES.CONTENT_NAME, name)
            .where(PACKAGES.PACKAGE_ID.eq(packageId))
            .execute();
    }

    public void move(String packageId, int x, int y) {
        create.update(PACKAGES)
            .set(PACKAGES.X_POS, x)
            .set(PACKAGES.Y_POS, y)
            .where(PACKAGES.PACKAGE_ID.eq(packageId))
            .execute();
    }

    public void resize(String packageId, int distX, int distY) {
        create.update(PACKAGES)
            .set(PACKAGES.X_DIST, distX)
            .set(PACKAGES.Y_DIST, distY)
            .where(PACKAGES.PACKAGE_ID.eq(packageId))
            .execute();
    }

    public void reFrame(String packageId, int x, int y, int distX, int distY) {
        create.update(PACKAGES)
            .set(PACKAGES.X_POS, x)
            .set(PACKAGES.Y_POS, y)
            .set(PACKAGES.X_DIST, distX)
            .set(PACKAGES.Y_DIST, distY)
            .where(PACKAGES.PACKAGE_ID.eq(packageId))
            .execute();
    }

    public void delete(String packageId) {
        create.deleteFrom(PACKAGES)
            .where(PACKAGES.PACKAGE_ID.eq(packageId))
            .execute();
    }

}
