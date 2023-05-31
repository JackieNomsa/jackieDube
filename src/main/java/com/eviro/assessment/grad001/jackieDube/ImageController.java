package com.eviro.assessment.grad001.jackieDube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/image")
public class ImageController {
    @Autowired
    private final FileParserImp fileParserImp;

    @Autowired
    public ImageController(FileParserImp fileParserImp) {
        this.fileParserImp = fileParserImp;
    }

    @GetMapping(value = "/")
    public Map<String,String> test(){
        HashMap<String, String> map = new HashMap<>();
        map.put("message","this works");
        return map;
    }

    @GetMapping(value = "/{name}/{surname}")
    public FileSystemResource gethttpImageLink(
            @PathVariable String name,
            @PathVariable String surname)
    {
        return new FileSystemResource(this.fileParserImp.getImageLink(name,surname));
    }
}
