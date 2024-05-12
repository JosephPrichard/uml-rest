package com.turbouml.packageuml;

import com.turbouml.auth.AuthService;
import com.turbouml.exceptions.AccessDeniedException;
import com.turbouml.exceptions.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.turbouml.diagram.ProjectDiagramDto.MAX_X;
import static com.turbouml.diagram.ProjectDiagramDto.MAX_Y;

@Service("PackageService")
public class PackageService {
    private final PackageDao packageDao;
    private final AuthService authService;

    @Autowired
    public PackageService(
        @Qualifier("PackageDao") PackageDao packageDao,
        @Qualifier("AuthorizationService") AuthService authService
    ) {
        this.packageDao = packageDao;
        this.authService = authService;
    }

    public void savePackage(String userId, PackageDto newPackage)
        throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, newPackage.getProjectId());
        packageDao.save(newPackage);
    }

    public List<PackageDto> retrieveAllPackages(String userId, String projectId)
        throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, projectId);
        return packageDao.findByProjectId(projectId);
    }

    public void renamePackage(String userId, String packageId, String name)
        throws AccessDeniedException {
        authService.authorizePackageAccess(userId, packageId);
        packageDao.rename(packageId, name);
    }

    public void validatePackage(int x, int y, int distX, int distY) {
        if (x + distX > MAX_X || y + distY > MAX_Y) {
            throw new InvalidInputException("Package cannot be placed out of bounds");
        }
    }

    public void reFramePackage(String userId, String packageId, int x, int y, int distX, int distY)
        throws AccessDeniedException {
        validatePackage(x, y, distX, distY);
        authService.authorizePackageAccess(userId, packageId);
        packageDao.reFrame(packageId, x, y, distX, distY);
    }

    public void movePackage(String userId, String packageId, int x, int y)
        throws AccessDeniedException {
        authService.authorizePackageAccess(userId, packageId);
        packageDao.move(packageId, x, y);
    }

    public void resizePackage(String userId, String packageId, int distX, int distY)
        throws AccessDeniedException {
        authService.authorizePackageAccess(userId, packageId);
        packageDao.resize(packageId, distX, distY);
    }

    public void deletePackage(String userId, String packageId)
        throws AccessDeniedException {
        authService.authorizePackageAccess(userId, packageId);
        packageDao.delete(packageId);
    }
}
