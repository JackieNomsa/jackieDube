package com.eviro.assessment.grad001.jackieDube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/image")
public class ImageController {
    @Autowired
    private final FileParserImp fileParserImp;

    public ImageController(FileParserImp fileParserImp) {
        this.fileParserImp = fileParserImp;
    }

    @GetMapping(value = "/{name}/{surname}")
    public FileSystemResource gethttpImageLink(
            @PathVariable String name,
            @PathVariable String surname)
    {
        System.out.println("#############################");
        return new FileSystemResource(this.fileParserImp.getImageLink(name,surname));
    }
}
