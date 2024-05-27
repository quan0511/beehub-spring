package vn.aptech.beehub.services.impl;

import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.services.IFilesStorageService;

import java.io.IOException;
import java.net.MalformedURLException;
@Service
public class FilesStorageService implements IFilesStorageService {
	  private final Path root = Paths.get("images");
	  @Override
	  public void init() {
	    try {
	      Files.createDirectories(root);
	    } catch (IOException e) {
	      throw new RuntimeException("Could not initialize folder for upload!");
	    }
	  }
	@Override
	public Resource load(String filename) {
		try {
		      Path file = root.resolve(filename);
		      Resource resource = new UrlResource(file.toUri());

		      if (resource.exists() || resource.isReadable()) {
		        return resource;
		      } else {
		        throw new RuntimeException("Could not read the file!");
		      }
		    } catch (MalformedURLException e) {
		      throw new RuntimeException("Error: " + e.getMessage());
		    }
	}
	@Override
	public Stream<Path> loadAll() {
		try {
	      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
	    } catch (IOException e) {
	      throw new RuntimeException("Could not load the files!");
	    }
	}

}
