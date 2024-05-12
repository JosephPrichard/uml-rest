package com.turbouml.project;

import com.turbouml.utils.Serializer;
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
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Validated
@RestController
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ResponseBody
    @PostMapping(value = "/projects/create")
    public ResponseEntity<String> createProject(
        @Size(min = 4, max = 30) @RequestParam String name,
        @Valid @RequestParam Lang lang
    ) {
        String userId = Session.userIdContext();
        var newProject = new ProjectDto();
        newProject.setProjectId(ID.generate());
        newProject.setUserId(Session.userIdContext());
        newProject.setContentName(name);
        newProject.setLang(lang);
        newProject.setTimestamp(
            DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss")
                .format(LocalDateTime.now())
        );
        try {
            projectService.saveProject(userId, newProject);
            return new ResponseEntity<>(
                Serializer.serialize(newProject),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ResponseBody
    @GetMapping(value = "/projects/getById")
    public ResponseEntity<String> findProject(
        @RequestParam String projectId
    ) {
        String userId = Session.userIdContext();
        try {
            return new ResponseEntity<>(
                Serializer.serialize(
                    projectService.retrieveProject(userId, projectId)
                ),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @GetMapping(value = "/projects/getAll")
    public ResponseEntity<String> findProjectForUser() {
        String userId = Session.userIdContext();
        try {
            return new ResponseEntity<>(
                Serializer.serialize(
                    projectService.retrieveAllProjects(userId)
                ),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/projects/rename")
    public ResponseEntity<String> renameProject(
        @RequestParam String projectId,
        @Size(min = 4, max = 30) @RequestParam String name
    ) {
        String userId = Session.userIdContext();
        try {
            projectService.renameProject(userId, projectId, name);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/projects/delete")
    public ResponseEntity<String> deleteProject(
        @RequestParam String projectId
    ) {
        String userId = Session.userIdContext();
        try {
            projectService.deleteProject(userId, projectId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(
            ResponseUtils.getErrorResponse(ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }
}
