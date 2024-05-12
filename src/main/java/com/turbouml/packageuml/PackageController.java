package com.turbouml.packageuml;

import com.turbouml.utils.Serializer;
import com.turbouml.exceptions.AccessDeniedException;
import com.turbouml.exceptions.InvalidInputException;
import com.turbouml.utils.Session;
import com.turbouml.utils.ID;
import com.turbouml.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;

@Validated
@RestController
public class PackageController {
    private final PackageService packageService;

    @Autowired
    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @ResponseBody
    @PostMapping(value = "/packages/create")
    public ResponseEntity<String> createPackage(
        @Valid @Min(0) @RequestParam int xPos,
        @Valid @Min(0) @RequestParam int yPos,
        @Valid @Min(0) @RequestParam int xDist,
        @Valid @Min(0) @RequestParam int yDist,
        @RequestParam String name,
        @RequestParam String projectId
    ) {
        String userId = Session.userIdContext();
        var newPackage = new PackageDto();
        newPackage.setPackageId(ID.generate());
        newPackage.setContentName(name);
        newPackage.setXPos(xPos);
        newPackage.setYPos(yPos);
        newPackage.setXDist(xDist);
        newPackage.setYDist(yDist);
        newPackage.setProjectId(projectId);
        packageService.validatePackage(xPos, yPos, xDist, yDist);
        try {
            packageService.savePackage(userId, newPackage);
            return new ResponseEntity<>(
                Serializer.serialize(newPackage),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ResponseBody
    @GetMapping(value = "/packages/forProject")
    public ResponseEntity<String> findPackagesForProject(@RequestParam String projectId) {
        String userId = Session.userIdContext();
        try {
            return new ResponseEntity<>(
                Serializer.serialize(
                    packageService.retrieveAllPackages(userId, projectId)
                ),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/packages/move")
    public ResponseEntity<String> movePackage(
        @RequestParam(value = "package_id") String packageId,
        @Valid @Min(0) @RequestParam(value = "x_pos") int x,
        @Valid @Min(0) @RequestParam(value = "y_pos") int y,
        @Valid @Min(0) @RequestParam(value = "x_dist") int xDist,
        @Valid @Min(0) @RequestParam(value = "y_dist") int yDist
    ) {
        String userId = Session.userIdContext();
        try {
            packageService.reFramePackage(userId, packageId, x, y, xDist, yDist);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/packages/rename")
    public ResponseEntity<String> renamePackage(
        @RequestParam(value = "package_id") String packageId,
        @RequestParam(value = "name") String packageName
    ) {
        String userId = Session.userIdContext();
        try {
            packageService.renamePackage(userId, packageId, packageName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/packages/delete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> deletePackage(
        @RequestParam(value = "package_id") String packageId
    ) {
        String userId = Session.userIdContext();
        try {
            packageService.deletePackage(userId, packageId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler({ConstraintViolationException.class, InvalidInputException.class})
    public ResponseEntity<String> handleInputException(Exception ex) {
        return new ResponseEntity<>(
            ResponseUtils.getResponse(ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(
            ResponseUtils.getResponse(ex.getMessage()),
            HttpStatus.UNAUTHORIZED
        );
    }

}
