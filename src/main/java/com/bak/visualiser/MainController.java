package com.bak.visualiser;

import com.bak.visualiser.data.*;
import com.bak.visualiser.parser.Parser;
import com.bak.visualiser.persistance.PsManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MainController {
    private final PsManager psManager;

    public MainController(PsManager psManager) {
        this.psManager = psManager;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void getFile(@RequestBody MultipartFile file) throws IOException {
        if (psManager.getProjects().contains(file.getOriginalFilename())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project already exist");
        }
        Parser parser = new Parser(file.getInputStream());
        psManager.save(parser, file.getOriginalFilename());
    }

    @GetMapping("/projects")
    public List<String> getProjects() {
        return psManager.getProjects();
    }

    @GetMapping("/{project}/thread")
    public Graph getThreads(@RequestParam Integer step, @PathVariable String project) {
        Parser parser = psManager.get(project);
        return parser.getGraph(step);
    }

    @GetMapping("/{project}/syn")
    public Graph getSyn(@RequestParam Integer step, @RequestParam String synId, @PathVariable String project) {
        Parser parser = psManager.get(project);
        Graph graph = parser.getGraph(step);
            return graph.getSynGraph(synId);
    }

    @GetMapping("/{project}/stack")
    public Stacks getStacks(@RequestParam Integer step, @PathVariable String project) {
        Parser parser = psManager.get(project);
        return parser.getStacks(step);
    }

    @GetMapping("/{project}/func")
    public Graph getFunc(@RequestParam Integer step, @PathVariable String project) {
        Parser parser = psManager.get(project);
        return parser.getFunc(step);
    }

    @GetMapping("/{project}/size")
    public Integer getSize(@PathVariable String project) {
        Parser parser = psManager.get(project);
        return parser.getSize();
    }

    @GetMapping("/{project}/graphs")
    public State getState(@RequestParam Integer step, @PathVariable String project) {
        return new State(getThreads(step, project),getFunc(step, project), getStacks(step, project));
    }
}
